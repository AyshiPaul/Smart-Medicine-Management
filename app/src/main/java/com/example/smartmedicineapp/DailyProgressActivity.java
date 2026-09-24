package com.example.smartmedicineapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DailyProgressActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvProgressText;
    private Button btnLogTaken, btnLogMissed;

    private static final String BASE_URL = "http://192.168.0.104/smart_medicine_api/";
    private int userId = 1;
    private int medicineId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_progress);

        progressBar = findViewById(R.id.progressBar);
        tvProgressText = findViewById(R.id.tvProgressText);
        btnLogTaken = findViewById(R.id.btnLogTaken);
        btnLogMissed = findViewById(R.id.btnLogMissed);

        fetchDailyProgress();

        btnLogTaken.setOnClickListener(v -> logMedicineStatus("Taken"));
        btnLogMissed.setOnClickListener(v -> logMedicineStatus("Missed"));
    }

    private void fetchDailyProgress() {
        String url = BASE_URL + "get_daily_progress.php?user_id=" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            int total = jsonObject.getInt("total");
                            int taken = jsonObject.getInt("taken");

                            int percentage = (total > 0) ? (taken * 100 / total) : 0;
                            progressBar.setProgress(percentage);
                            tvProgressText.setText(percentage + "% Completed (" + taken + "/" + total + ")");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void logMedicineStatus(String status) {
        String url = BASE_URL + "log_medicine.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Status updated to: " + status, Toast.LENGTH_SHORT).show();
                    fetchDailyProgress();
                },
                error -> Toast.makeText(this, "Error logging status", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("medicine_id", String.valueOf(medicineId));
                params.put("status", status);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}