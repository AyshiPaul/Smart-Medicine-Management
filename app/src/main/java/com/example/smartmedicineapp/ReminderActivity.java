package com.example.smartmedicineapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private Button btnSetReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        timePicker = findViewById(R.id.timePicker);
        btnSetReminder = findViewById(R.id.btnSetReminder);

        // Android 13 (API 33) বা তার ওপরের ভার্সনে Notification Permission চাওয়ার জন্য
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        btnSetReminder.setOnClickListener(v -> setAlarm());
    }

    private void setAlarm() {
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // সময় যদি বর্তমান সময় থেকে পিছিয়ে থাকে (অতীতে হয়), তবে পরের দিনের জন্য সেট হবে
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("medicine_name", "Napa Extra");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Reminder set successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}