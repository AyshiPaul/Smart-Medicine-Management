package com.example.smartmedicineapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeeklyReportActivity extends AppCompatActivity {
    private TextView tvWeeklyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_report);

        tvWeeklyData = findViewById(R.id.tvWeeklyData);
        fetchWeeklyReport();
    }

    private void fetchWeeklyReport() {
        String url = "http://192.168.0.104/smart_medicine_api/get_weekly_report.php?user_id=1";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            StringBuilder builder = new StringBuilder();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                builder.append("📅 ").append(obj.getString("day")).append("\n")
                                        .append("   Taken: ").append(obj.getInt("taken")).append("\n")
                                        .append("   Missed: ").append(obj.getInt("missed")).append("\n\n");
                            }
                            tvWeeklyData.setText(builder.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Failed to load report", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(stringRequest);
    }
}