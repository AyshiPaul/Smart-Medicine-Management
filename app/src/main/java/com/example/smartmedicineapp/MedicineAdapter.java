package com.example.smartmedicineapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    private Context context;
    private List<Medicine> medicineList;

    public MedicineAdapter(Context context, List<Medicine> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);

        holder.tvMedicineName.setText(medicine.getName());
        holder.tvDosage.setText("Dosage: " + medicine.getDosage() + " (" + medicine.getQuantity() + " pcs)");
        holder.tvTime.setText("Time: " + medicine.getReminderTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditMedicineActivity.class);
                intent.putExtra("id", medicine.getId());
                intent.putExtra("name", medicine.getName());
                intent.putExtra("dosage", medicine.getDosage());
                intent.putExtra("reminder_time", medicine.getReminderTime());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvDosage, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvDosage = itemView.findViewById(R.id.tvDosage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}