package com.example.smartmedicineapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;
import java.util.List;

public class DailyProgressAdapter extends RecyclerView.Adapter<DailyProgressAdapter.ViewHolder> {

    private Context context;
    private List<JSONObject> medicineList;

    public DailyProgressAdapter(Context context, List<JSONObject> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_daily_progress_action, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject obj = medicineList.get(position);
            int id = obj.getInt("id");
            String medName = obj.getString("name");
            String dosage = obj.getString("dosage");
            String reminderTime = obj.getString("reminder_time");

            holder.tvMedName.setText(medName + " (" + dosage + ")");
            holder.tvMedTime.setText("Times: " + reminderTime);

            // Calculate total allowed slots from reminder times
            int totalSlots = 1;
            if (reminderTime != null && !reminderTime.isEmpty()) {
                totalSlots = reminderTime.split(",").length;
            }

            final int maxLimit = totalSlots;

            holder.btnMarkTaken.setOnClickListener(v -> {
                if (context instanceof DailyProgressActivity) {
                    ((DailyProgressActivity) context).logMedicineStatus(id, "Taken");
                }
            });

            holder.btnMarkMissed.setOnClickListener(v -> {
                if (context instanceof DailyProgressActivity) {
                    ((DailyProgressActivity) context).logMedicineStatus(id, "Missed");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedName, tvMedTime;
        Button btnMarkTaken, btnMarkMissed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedName = itemView.findViewById(R.id.tvMedName);
            tvMedTime = itemView.findViewById(R.id.tvMedTime);
            btnMarkTaken = itemView.findViewById(R.id.btnMarkTaken);
            btnMarkMissed = itemView.findViewById(R.id.btnMarkMissed);
        }
    }
}