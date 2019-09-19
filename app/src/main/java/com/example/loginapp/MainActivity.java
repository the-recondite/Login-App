package com.example.loginapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
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


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mFirebaseAuth;
    EditText emailText;
    EditText passwordText;
    ProgressBar progressBar;
    TextView loginTextView;
    TextView registerTextView;
    LinearLayout loginlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        progressBar = findViewById(R.id.progressBar);
        loginTextView = findViewById(R.id.loginTextView);
        registerTextView = findViewById(R.id.registerCaptionTextView);
        loginTextView.setOnClickListener(this);
        registerTextView.setOnClickListener(this);
        loginlayout = findViewById(R.id.loginLayout);
        loginlayout.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mFirebaseAuth.getCurrentUser()!= null) {
            mFirebaseAuth.signOut();
        }
    }

    public void login() {
        if (!validateForm()){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                            emailText.setText("");
                            passwordText.setText("");
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Wrong Username or Password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(emailText.getText().toString())) {
            emailText.setError("Required");
            result = false;
        }
        else {
            emailText.setError(null);
        }
        if (TextUtils.isEmpty(passwordText.getText().toString())) {
            emailText.setError("Required");
            result = false;
        }
        else {
            emailText.setError(null);
        }
        return result;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.loginTextView) {
            login();
        }
        if (i == R.id.registerCaptionTextView) {
            signUpIntent();
        }
        if (i == R.id.loginLayout) {
            closeKeyboard();
        }
    }

    public void signUpIntent() {
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
    }
    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
