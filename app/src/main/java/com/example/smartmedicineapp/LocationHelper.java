package com.example.smartmedicineapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationHelper {
    private FusedLocationProviderClient fusedLocationClient;

    public interface LocationCallback {
        void onLocationRetrieved(double latitude, double longitude);
        void onError(String errorMsg);
    }

    public LocationHelper(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(LocationCallback callback) {
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    callback.onLocationRetrieved(location.getLatitude(), location.getLongitude());
                } else {
                    callback.onError("Location not found. Please enable GPS.");
                }
            }
        });
    }
}