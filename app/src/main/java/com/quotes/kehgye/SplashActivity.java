package com.quotes.kehgye;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.quotes.kehgye.MainActivity;
import com.quotes.kehgye.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000; // Duration of wait in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Handler to start the next activity after the specified delay
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Intent to start the main activity
//                Intent mainIntent = new Intent(SplashActivity.this, Login.class);
//                startActivity(mainIntent);
//                finish();
//            }
//        }, SPLASH_DISPLAY_LENGTH);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check authentication state
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();

                // Decide where to navigate based on authentication state
                if (currentUser != null) {
                    // User is already authenticated, navigate to main activity
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // User is not authenticated, navigate to login activity
                    Intent intent = new Intent(SplashActivity.this, Login.class);
                    startActivity(intent);
                }

                finish(); // Finish the splash activity so it's not on the back stack
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
