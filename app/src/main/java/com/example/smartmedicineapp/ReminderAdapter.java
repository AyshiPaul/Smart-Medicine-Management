package com.example.smartmedicineapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private Context context;
    private List<JSONObject> medicineList;

    public ReminderAdapter(Context context, List<JSONObject> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject obj = medicineList.get(position);
            holder.tvMedicineName.setText(obj.getString("name"));
            holder.tvDosage.setText("Dosage: " + obj.getString("dosage"));
            holder.tvReminderTime.setText("⏰ Alarm Times: " + obj.getString("reminder_time"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvDosage, tvReminderTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvDosage = itemView.findViewById(R.id.tvDosage);
            tvReminderTime = itemView.findViewById(R.id.tvReminderTime);
        }
    }
}