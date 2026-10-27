package com.example.smartmedicineapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReminderActivity extends AppCompatActivity {

    private RecyclerView recyclerViewReminders;
    private List<JSONObject> medicineList = new ArrayList<>();
    private static final String GET_MEDICINES_URL = "http://10.235.181.39/smart_medicine_api/get_medicines.php?user_id=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        recyclerViewReminders = findViewById(R.id.recyclerViewReminders);
        recyclerViewReminders.setLayoutManager(new LinearLayoutManager(this));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        fetchReminders();
    }

    private void fetchReminders() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, GET_MEDICINES_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray dataArray = response.getJSONArray("data");
                                medicineList.clear();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    medicineList.add(dataArray.getJSONObject(i));
                                }

                                ReminderAdapter adapter = new ReminderAdapter(ReminderActivity.this, medicineList);
                                recyclerViewReminders.setAdapter(adapter);
                            } else {
                                Toast.makeText(ReminderActivity.this, "No active reminders found!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ReminderActivity.this, "Failed to load reminders!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }
}