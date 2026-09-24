package com.example.smartmedicineapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvAccountSettings, tvNotificationSettings, tvAboutApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvAccountSettings = findViewById(R.id.tvAccountSettings);
        tvNotificationSettings = findViewById(R.id.tvNotificationSettings);
        tvAboutApp = findViewById(R.id.tvAboutApp);

        tvAccountSettings.setOnClickListener(v ->
                Toast.makeText(this, "Account Settings Clicked", Toast.LENGTH_SHORT).show());

        tvNotificationSettings.setOnClickListener(v ->
                Toast.makeText(this, "Notification Settings Clicked", Toast.LENGTH_SHORT).show());

        tvAboutApp.setOnClickListener(v ->
                Toast.makeText(this, "Smart Medicine App v1.0", Toast.LENGTH_SHORT).show());
    }
}