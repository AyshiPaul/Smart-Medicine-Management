package com.example.smartmedicineapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // ৩ সেকেন্ড পর LoginActivity-তে নিয়ে যাবে
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // ব্যাক বাটন চাপলে যেন স্প্ল্যাশ স্ক্রিনে আর ফেরত না আসে
        }, 3000);
    }
}