package com.quotes.kehgye;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    android.app.ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    TextView  signuptxt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Bind views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signuptxt = findViewById(R.id.signupkey);
        //progressBar = findViewById(R.id.progressBar);


        // Set footer text
        //footerTextView.setText("All rights reserved to Kehgye");

        //click on signup area
        signuptxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,Registration.class);
                startActivity(intent);
//                finish();
            }
        });

        // Set login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    progressDialog.dismiss();
                    emailEditText.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    progressDialog.dismiss();
                    passwordEditText.setError("Password is required");
                    return;
                }
                if (password.length()<6) {
                    progressDialog.dismiss();
                    passwordEditText.setError("password should be more than 6 character");
                }

                //progressBar.setVisibility(View.VISIBLE);

                // Sign in with email and password
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, task -> {
                            //progressBar.setVisibility(View.GONE);
                            progressDialog.show();
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(Login.this, "Authentication Successful.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Login.this, "Authentication Failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        });
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Go to main activity or any other activity after successful login
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
