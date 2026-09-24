package com.example.smartmedicineapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddMedicineActivity extends AppCompatActivity {

    private EditText etName, etDosage, etQuantity, etReminderTime;
    private Button btnSaveMedicine;

    private static final String ADD_URL = "http://192.168.0.104/smart_medicine_api/add_medicine.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        etName = findViewById(R.id.etName);
        etDosage = findViewById(R.id.etDosage);
        etQuantity = findViewById(R.id.etQuantity);
        etReminderTime = findViewById(R.id.etReminderTime);
        btnSaveMedicine = findViewById(R.id.btnSaveMedicine);

        btnSaveMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMedicine();
            }
        });
    }

    private void addMedicine() {
        final String name = etName.getText().toString().trim();
        final String dosage = etDosage.getText().toString().trim();
        final String quantity = etQuantity.getText().toString().trim();
        String time = etReminderTime.getText().toString().trim();

        if (name.isEmpty() || dosage.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (time.length() == 5) {
            time = time + ":00";
        }

        final String formattedTime = time;
        final String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("SERVER_RESPONSE", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                Toast.makeText(AddMedicineActivity.this, "Medicine Added Successfully!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddMedicineActivity.this, "Server Response Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", "1");
                params.put("name", name);
                params.put("dosage", dosage);
                params.put("quantity", quantity.isEmpty() ? "1" : quantity);
                params.put("reminder_time", formattedTime);
                params.put("start_date", currentDate);
                params.put("repeat_frequency", "Everyday");
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}