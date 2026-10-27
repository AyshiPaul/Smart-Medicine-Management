package com.example.smartmedicineapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AddMedicineActivity extends AppCompatActivity {

    private EditText etName, etDosage, etQuantity;
    private Spinner spFrequency;
    private LinearLayout timeContainer;
    private Button btnSaveMedicine;

    private List<EditText> timeEditTextList = new ArrayList<>();
    private static final String ADD_URL = "http://10.235.181.39/smart_medicine_api/add_medicine.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        etName = findViewById(R.id.etName);
        etDosage = findViewById(R.id.etDosage);
        etQuantity = findViewById(R.id.etQuantity);
        spFrequency = findViewById(R.id.spFrequency);
        timeContainer = findViewById(R.id.timeContainer);
        btnSaveMedicine = findViewById(R.id.btnSaveMedicine);

        // Check for Android 12+ Exact Alarm Permission
        checkExactAlarmPermission();

        String[] frequencies = {"1 Time Daily", "2 Times Daily", "3 Times Daily", "4 Times Daily"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, frequencies) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.BLACK);
                view.setTextSize(16);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setBackgroundColor(Color.WHITE);
                view.setTextColor(Color.BLACK);
                view.setPadding(24, 24, 24, 24);
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrequency.setAdapter(adapter);

        spFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int count = position + 1;
                generateDigitalTimePickers(count);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSaveMedicine.setOnClickListener(v -> addMedicine());
    }

    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }

    private void generateDigitalTimePickers(int count) {
        timeContainer.removeAllViews();
        timeEditTextList.clear();

        for (int i = 0; i < count; i++) {
            final EditText etTime = new EditText(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 130);
            params.setMargins(0, 12, 0, 12);
            etTime.setLayoutParams(params);
            etTime.setBackgroundResource(android.R.color.white);
            etTime.setPadding(35, 0, 35, 0);
            etTime.setHint("Tap to select Time " + (i + 1) + " (Digital Clock)");

            etTime.setFocusable(false);
            etTime.setClickable(true);
            etTime.setInputType(InputType.TYPE_NULL);
            etTime.setTextColor(Color.BLACK);
            etTime.setHintTextColor(Color.GRAY);

            etTime.setOnClickListener(v -> showDigitalTimePicker(etTime));

            timeContainer.addView(etTime);
            timeEditTextList.add(etTime);
        }
    }

    private void showDigitalTimePicker(EditText targetEditText) {
        Calendar mcurrentTime = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka"));
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker = new TimePickerDialog(AddMedicineActivity.this, (timePicker, selectedHour, selectedMinute) -> {
            String AM_PM = (selectedHour < 12) ? "AM" : "PM";
            int hour12 = (selectedHour > 12) ? (selectedHour - 12) : selectedHour;
            if (hour12 == 0) hour12 = 12;

            String formattedTime = String.format(Locale.US, "%02d:%02d %s", hour12, selectedMinute, AM_PM);
            targetEditText.setText(formattedTime);
        }, hour, minute, false);

        mTimePicker.setTitle("Select Digital Time");
        mTimePicker.show();
    }

    private void addMedicine() {
        final String name = etName.getText().toString().trim();
        final String dosage = etDosage.getText().toString().trim();
        final String quantity = etQuantity.getText().toString().trim();

        StringBuilder timesBuilder = new StringBuilder();
        for (EditText et : timeEditTextList) {
            String val = et.getText().toString().trim();
            if (val.isEmpty()) {
                Toast.makeText(this, "Please select time for all slots", Toast.LENGTH_SHORT).show();
                return;
            }
            if (timesBuilder.length() > 0) timesBuilder.append(", ");
            timesBuilder.append(val);
        }

        final String timeInput = timesBuilder.toString();

        if (name.isEmpty() || dosage.isEmpty() || timeInput.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));
        final String currentDate = dateFormat.format(new Date());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("SERVER_RESPONSE", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                scheduleAlarms(name, timeInput);
                                Toast.makeText(AddMedicineActivity.this, "Medicine Added & Alarms Set!", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(AddMedicineActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddMedicineActivity.this, "Parsing Error: " + response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Network Error!";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            errorMsg = new String(error.networkResponse.data);
                        }
                        Log.e("VOLLEY_ERROR", errorMsg);
                        Toast.makeText(AddMedicineActivity.this, "Server Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", "1");
                params.put("name", name);
                params.put("dosage", dosage);
                params.put("quantity", quantity.isEmpty() ? "1" : quantity);
                params.put("reminder_time", timeInput);
                params.put("start_date", currentDate);
                params.put("repeat_frequency", spFrequency.getSelectedItem().toString());
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

                    // If time already passed today, set for tomorrow
                    if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }

                    Intent intent = new Intent(this, AlarmReceiver.class);
                    intent.putExtra("medicine_name", medicineName);

                    int requestCode = (int) System.currentTimeMillis() + i;
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
}