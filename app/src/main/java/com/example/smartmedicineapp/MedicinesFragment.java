package com.example.smartmedicineapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MedicinesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MedicineAdapter adapter;
    private List<Medicine> medicineList;
    private FloatingActionButton fabAddMedicine;

    private static final String GET_MEDICINES_URL = "http://192.168.0.104/smart_medicine_api/get_medicines.php?user_id=1";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicines, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMedicines);
        fabAddMedicine = view.findViewById(R.id.fabAddMedicine);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        medicineList = new ArrayList<>();

        adapter = new MedicineAdapter(getContext(), medicineList);
        recyclerView.setAdapter(adapter);

        fabAddMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    startActivity(new Intent(getActivity(), AddMedicineActivity.class));
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchMedicines();
    }

    private void fetchMedicines() {
        Context context = getContext();
        if (context == null) return;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, GET_MEDICINES_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("success") && response.getBoolean("success")) {
                                medicineList.clear();
                                JSONArray dataArray = response.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject obj = dataArray.getJSONObject(i);

                                    int id = obj.optInt("id", 0);
                                    String name = obj.optString("name", "");
                                    String dosage = obj.optString("dosage", "");
                                    int quantity = obj.optInt("quantity", 1);
                                    String reminderTime = obj.optString("reminder_time", "");
                                    String repeatFrequency = obj.optString("repeat_frequency", "Everyday");

                                    Medicine medicine = new Medicine(id, name, dosage, quantity, reminderTime, repeatFrequency);
                                    medicineList.add(medicine);
                                }

                                adapter.notifyDataSetChanged();
                            } else {
                                String msg = response.optString("message", "No data found");
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Parsing Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API_ERROR", "Error: " + error.toString());
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Connection Error! Check XAMPP/URL", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }
}