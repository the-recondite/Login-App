package com.example.loginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText emailText;
    EditText passwordText;
    TextView signupTextView;
    FirebaseAuth mFireBaseAuth;
    TextView loginCaption;
    EditText firstNameText;
    EditText lastNameText;
    ProgressBar progressBar;
    DatabaseReference mDatabase;
    FirebaseDatabase firebaseDatabase;
    LinearLayout signUpLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mFireBaseAuth = FirebaseAuth.getInstance();
        firstNameText = findViewById(R.id.firstNameText);
        lastNameText = findViewById(R.id.lastNameText);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        signupTextView = findViewById(R.id.signUpTextView);
        loginCaption = findViewById(R.id.loginCaptionTextView);
        progressBar = findViewById(R.id.signInProgress);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference();
        signupTextView.setOnClickListener(this);
        loginCaption.setOnClickListener(this);
        signUpLayout = findViewById(R.id.signUpLayout);
        signUpLayout.setOnClickListener(this);

    }

   @Override
    protected void onStart() {
        super.onStart();
        if (mFireBaseAuth.getCurrentUser()!=null) {
            mFireBaseAuth.signOut();
        }
    }

    //method for the sign in textview
    public void signIn() {
        if (!validateForm()) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        Log.i("Email", email);
        Log.i("paassword", password);
        mFireBaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "Sign Up Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void onAuthSuccess(FirebaseUser user) {
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        writeNewUser(user.getUid(), firstName, lastName, user.getEmail());
        Log.i("yeah", "success");
        Toast.makeText(SignUpActivity.this, "Sign Up Successful, please Login", Toast.LENGTH_LONG).show();
        firstNameText.setText("");
        lastNameText.setText("");
        emailText.setText("");
        passwordText.setText("");
    }

    private void writeNewUser(String userId, String firstName, String lastName, String email) {
        User user = new User(firstName, lastName, email);
        mDatabase.push().child(userId).setValue(user);
    }
    //method to validate form
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(firstNameText.getText().toString())){
            firstNameText.setError("Required");
            result = false;
        }
        else {
            firstNameText.setError(null);
        }
        if (TextUtils.isEmpty(lastNameText.getText().toString())){
            lastNameText.setError("Required");
            result = false;
        }
        else {
            firstNameText.setError(null);
        }
        if (TextUtils.isEmpty(emailText.getText().toString())){
            emailText.setError("Required");
            result = false;
        }
        else if (!(emailText.getText().toString().contains("@")) | !(emailText.getText().toString().contains(".com"))) {
            emailText.setError("Enter valid email");
            result = false;
        }
        else {
            emailText.setError(null);
        }
        if (TextUtils.isEmpty(passwordText.getText().toString())){
            passwordText.setError("Required");
            result = false;
        }
        else if (passwordText.getText().toString().length() < 7) {
            passwordText.setError("Password should be more than 6 characters");
        }
        else {
            passwordText.setError(null);
        }
        return result;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.signUpTextView) {
            signIn();
        }
        if (i == R.id.loginCaptionTextView) {
            loginIntent();
        }
        if (i == R.id.signUpLayout) {
            closeKeyboard();
        }
    }

    public void loginIntent() {
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        finish();
    }
    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}