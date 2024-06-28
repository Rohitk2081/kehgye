package com.quotes.kehgye;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.quotes.kehgye.MainActivity;
import com.quotes.kehgye.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000; // Duration of wait in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Handler to start the next activity after the specified delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent to start the main activity
                Intent mainIntent = new Intent(SplashActivity.this, Login.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
