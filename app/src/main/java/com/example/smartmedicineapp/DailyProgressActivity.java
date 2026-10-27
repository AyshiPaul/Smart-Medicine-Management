package com.example.smartmedicineapp;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyProgressActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvProgressPercentage, tvProgressDetails;
    private TextView tvTakenCount, tvRemainingCount, tvMissedCount;
    private RecyclerView recyclerViewProgressMedicines;

    private static final String BASE_URL = "http://10.235.181.39/smart_medicine_api/";
    private int userId = 1;
    private List<JSONObject> medicineList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_progress);

        progressBar = findViewById(R.id.progressBar);
        tvProgressPercentage = findViewById(R.id.tvProgressPercentage);
        tvProgressDetails = findViewById(R.id.tvProgressDetails);
        tvTakenCount = findViewById(R.id.tvTakenCount);
        tvRemainingCount = findViewById(R.id.tvRemainingCount);
        tvMissedCount = findViewById(R.id.tvMissedCount);
        recyclerViewProgressMedicines = findViewById(R.id.recyclerViewProgressMedicines);

        recyclerViewProgressMedicines.setLayoutManager(new LinearLayoutManager(this));

        fetchDailyProgress();
        fetchMedicinesList();
    }

    public void fetchDailyProgress() {
        String url = BASE_URL + "get_daily_progress.php?user_id=" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            int total = jsonObject.getInt("total");
                            int taken = jsonObject.getInt("taken");
                            int missed = jsonObject.getInt("missed");
                            int remaining = jsonObject.getInt("remaining");

                            int percentage = (total > 0) ? (taken * 100 / total) : 0;
                            progressBar.setProgress(percentage);

                            tvProgressPercentage.setText(percentage + "% Completed");
                            tvProgressDetails.setText(taken + " of " + total + " Doses Taken");

                            tvTakenCount.setText(String.valueOf(taken));
                            tvRemainingCount.setText(String.valueOf(remaining));
                            tvMissedCount.setText(String.valueOf(missed));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void fetchMedicinesList() {
        String url = BASE_URL + "get_medicines.php?user_id=" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            medicineList.clear();
                            for (int i = 0; i < dataArray.length(); i++) {
                                medicineList.add(dataArray.getJSONObject(i));
                            }
                            DailyProgressAdapter adapter = new DailyProgressAdapter(DailyProgressActivity.this, medicineList);
                            recyclerViewProgressMedicines.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Failed to load medicines", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    public void logMedicineStatus(int medicineId, String status) {
        String url = BASE_URL + "log_medicine.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Marked as " + status, Toast.LENGTH_SHORT).show();
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