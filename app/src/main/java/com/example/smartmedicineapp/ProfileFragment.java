package com.example.smartmedicineapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    private TextView tvPersonalInformation, tvChangePassword, tvHealthDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnLogout = view.findViewById(R.id.btnLogout);
        tvPersonalInformation = view.findViewById(R.id.tvPersonalInformation);
        tvChangePassword = view.findViewById(R.id.tvChangePassword);
        tvHealthDetails = view.findViewById(R.id.tvHealthDetails);

        if (tvPersonalInformation != null) {
            tvPersonalInformation.setOnClickListener(v -> showPersonalDetailsDialog());
        }

        if (tvChangePassword != null) {
            tvChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        }

        if (tvHealthDetails != null) {
            tvHealthDetails.setOnClickListener(v -> showHealthDetailsDialog());
        }

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void showPersonalDetailsDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Personal Information")
                .setMessage("Name: Ayshi Paul\nEmail: ayshipaul123@gmail.com\nPhone: +8801234567890")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Password");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText inputOld = new EditText(getContext());
        inputOld.setHint("Current Password");
        inputOld.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputOld);

        final EditText inputNew = new EditText(getContext());
        inputNew.setHint("New Password");
        inputNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputNew);

        builder.setView(layout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String oldPass = inputOld.getText().toString().trim();
            String newPass = inputNew.getText().toString().trim();

            SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String savedPassword = prefs.getString("password", "");

            if (oldPass.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!savedPassword.isEmpty() && !oldPass.equals(savedPassword)) {
                Toast.makeText(getContext(), "Current password is incorrect!", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("password", newPass);
                editor.apply();
                Toast.makeText(getContext(), "Password Updated Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showHealthDetailsDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Health Details")
                .setMessage("Blood Group: O+\nAllergies: None Known\nChronic Conditions: None\nRegular Checkup: Active")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}