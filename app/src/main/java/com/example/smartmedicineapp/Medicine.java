package com.example.smartmedicineapp;

public class Medicine {
    private int id;
    private String name;
    private String dosage;
    private int quantity;
    private String reminderTime;
    private String repeatFrequency;

    public Medicine(int id, String name, String dosage, int quantity, String reminderTime, String repeatFrequency) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.quantity = quantity;
        this.reminderTime = reminderTime;
        this.repeatFrequency = repeatFrequency;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDosage() { return dosage; }
    public int getQuantity() { return quantity; }
    public String getReminderTime() { return reminderTime; }
    public String getRepeatFrequency() { return repeatFrequency; }
}