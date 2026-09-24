package com.example.smartmedicineapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private TextView tvProgressText;
    private Button btnOpenReminder, btnOpenProgress, btnOpenPharmacy, btnOpenReport, btnOpenSettings;
    private static final String PROGRESS_URL = "http://192.168.0.104/smart_medicine_api/get_daily_progress.php?user_id=1";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvProgressText = view.findViewById(R.id.tvProgressText);
        btnOpenReminder = view.findViewById(R.id.btnOpenReminder);
        btnOpenProgress = view.findViewById(R.id.btnOpenProgress);
        btnOpenPharmacy = view.findViewById(R.id.btnOpenPharmacy);
        btnOpenReport = view.findViewById(R.id.btnOpenReport);
        btnOpenSettings = view.findViewById(R.id.btnOpenSettings);

        fetchDailyProgress();

        // ⏰ Set Alarm বাটনে ক্লিক করলে ReminderActivity খুলবে
        btnOpenReminder.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReminderActivity.class);
            startActivity(intent);
        });

        // 📊 Daily Progress বাটনে ক্লিক করলে DailyProgressActivity খুলবে
        btnOpenProgress.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DailyProgressActivity.class);
            startActivity(intent);
        });

        // 🏥 Nearby Pharmacy বাটনে ক্লিক করলে PharmacyActivity খুলবে
        btnOpenPharmacy.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PharmacyActivity.class);
            startActivity(intent);
        });

        // 📈 Weekly Report বাটনে ক্লিক করলে WeeklyReportActivity খুলবে
        btnOpenReport.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WeeklyReportActivity.class);
            startActivity(intent);
        });

        // ⚙️ Settings বাটনে ক্লিক করলে SettingsActivity খুলবে
        btnOpenSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void fetchDailyProgress() {
        Context context = getContext();
        if (context == null) return;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, PROGRESS_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("success") && response.getBoolean("success")) {
                                int total = response.optInt("total", 0);
                                int taken = response.optInt("taken", 0);
                                int missed = response.optInt("missed", 0);

                                tvProgressText.setText("Taken: " + taken + " | Missed: " + missed + " | Total: " + total);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Error fetching dashboard data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }
}