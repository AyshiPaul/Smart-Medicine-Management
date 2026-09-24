package com.example.smartmedicineapp;

import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class EditMedicineActivity extends AppCompatActivity {

    private EditText etEditName, etEditDosage, etEditTime;
    private Button btnUpdate, btnDelete;
    private int medicineId;

    private static final String UPDATE_URL = "http://192.168.0.104/smart_medicine_api/update_medicine.php";
    private static final String DELETE_URL = "http://192.168.0.104/smart_medicine_api/delete_medicine.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medicine);

        etEditName = findViewById(R.id.etEditName);
        etEditDosage = findViewById(R.id.etEditDosage);
        etEditTime = findViewById(R.id.etEditTime);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        medicineId = getIntent().getIntExtra("id", 0);
        etEditName.setText(getIntent().getStringExtra("name"));
        etEditDosage.setText(getIntent().getStringExtra("dosage"));
        etEditTime.setText(getIntent().getStringExtra("reminder_time"));

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMedicine();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMedicine();
            }
        });
    }

    private void updateMedicine() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(EditMedicineActivity.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditMedicineActivity.this, "Update Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(medicineId));
                params.put("name", etEditName.getText().toString().trim());
                params.put("dosage", etEditDosage.getText().toString().trim());
                params.put("reminder_time", etEditTime.getText().toString().trim());
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void deleteMedicine() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(EditMedicineActivity.this, "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditMedicineActivity.this, "Delete Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
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
}