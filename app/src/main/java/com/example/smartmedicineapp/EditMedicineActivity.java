package com.example.smartmedicineapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class EditMedicineActivity extends AppCompatActivity {

    private EditText etEditName, etEditDosage, etEditTime;
    private SwitchCompat switchAlarmStatus;
    private Button btnUpdate, btnDelete;
    private int medicineId;

    private static final String UPDATE_URL = "http://10.235.181.39/smart_medicine_api/update_medicine.php";
    private static final String DELETE_URL = "http://10.235.181.39/smart_medicine_api/delete_medicine.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medicine);

        etEditName = findViewById(R.id.etEditName);
        etEditDosage = findViewById(R.id.etEditDosage);
        etEditTime = findViewById(R.id.etEditTime);
        switchAlarmStatus = findViewById(R.id.switchAlarmStatus);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        medicineId = getIntent().getIntExtra("id", -1);
        String name = getIntent().getStringExtra("name");
        String dosage = getIntent().getStringExtra("dosage");
        String time = getIntent().getStringExtra("reminder_time");

        if (name != null) etEditName.setText(name);
        if (dosage != null) etEditDosage.setText(dosage);
        if (time != null) etEditTime.setText(time);

        etEditTime.setInputType(InputType.TYPE_NULL);
        etEditTime.setOnClickListener(v -> showTimePicker());

        btnUpdate.setOnClickListener(v -> updateMedicine());
        btnDelete.setOnClickListener(v -> deleteMedicine());
    }

    private void showTimePicker() {
        Calendar mcurrentTime = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka"));
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker = new TimePickerDialog(EditMedicineActivity.this, (timePicker, selectedHour, selectedMinute) -> {
            String AM_PM = (selectedHour < 12) ? "AM" : "PM";
            int hour12 = (selectedHour > 12) ? (selectedHour - 12) : selectedHour;
            if (hour12 == 0) hour12 = 12;

            String formattedTime = String.format(Locale.US, "%02d:%02d %s", hour12, selectedMinute, AM_PM);
            etEditTime.setText(formattedTime);
        }, hour, minute, false);

        mTimePicker.setTitle("Select New Time");
        mTimePicker.show();
    }

    private void updateMedicine() {
        final String name = etEditName.getText().toString().trim();
        final String dosage = etEditDosage.getText().toString().trim();
        final String time = etEditTime.getText().toString().trim();
        final boolean isAlarmOn = switchAlarmStatus != null && switchAlarmStatus.isChecked();

        if (name.isEmpty() || dosage.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optBoolean("success", true)) {
                            cancelExistingAlarm();
                            if (isAlarmOn) {
                                scheduleAlarms(name, time);
                                Toast.makeText(EditMedicineActivity.this, "Updated & Alarm Scheduled!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditMedicineActivity.this, "Updated (Alarm Disabled)", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        } else {
                            Toast.makeText(EditMedicineActivity.this, jsonObject.optString("message", "Update Failed"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        cancelExistingAlarm();
                        if (isAlarmOn) {
                            scheduleAlarms(name, time);
                        }
                        Toast.makeText(EditMedicineActivity.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                error -> Toast.makeText(EditMedicineActivity.this, "Update Error", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(medicineId));
                params.put("name", name);
                params.put("dosage", dosage);
                params.put("reminder_time", time);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void deleteMedicine() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_URL,
                response -> {
                    cancelExistingAlarm();
                    Toast.makeText(EditMedicineActivity.this, "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(EditMedicineActivity.this, "Delete Error", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(medicineId));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void scheduleAlarms(String medicineName, String timeInput) {
        String[] timeList = timeInput.split(",");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);

        for (int i = 0; i < timeList.length; i++) {
            String t = timeList[i].trim();
            try {
                Date date = sdf.parse(t);
                if (date != null) {
                    Calendar timeCal = Calendar.getInstance();
                    timeCal.setTime(date);

                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka"));
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }

                    Intent intent = new Intent(this, AlarmReceiver.class);
                    intent.putExtra("medicine_name", medicineName);

                    int requestCode = (medicineId > 0 ? medicineId : 100) * 100 + i;
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            this,
                            requestCode,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );

                    if (alarmManager != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    calendar.getTimeInMillis(),
                                    pendingIntent
                            );
                        } else {
                            alarmManager.setExact(
                                    AlarmManager.RTC_WAKEUP,
                                    calendar.getTimeInMillis(),
                                    pendingIntent
                            );
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cancelExistingAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        for (int i = 0; i < 4; i++) {
            int requestCode = (medicineId > 0 ? medicineId : 100) * 100 + i;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
            );

            if (pendingIntent != null && alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
    }
}