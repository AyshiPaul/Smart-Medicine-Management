package com.example.smartmedicineapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PharmacyActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissionAndGetLocation();
    }

    private void checkPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // পারমিশন না থাকলে ইউজারের কাছে পারমিশন চাইবে
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // পারমিশন থাকলে লোকেশন নিয়ে গুগল ম্যাপ খুলবে
            getLocationAndOpenMaps();
        }
    }

    private void getLocationAndOpenMaps() {
        locationHelper = new LocationHelper(this);
        locationHelper.getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationRetrieved(double latitude, double longitude) {
                openGoogleMapsPharmacy(latitude, longitude);
            }

            @Override
            public void onError(String errorMsg) {
                // যদি লোকেশন না পায় তবে ডিফল্ট সার্চ করবে
                openGoogleMapsPharmacyWithoutLocation();
            }
        });
    }

    private void openGoogleMapsPharmacy(double lat, double lng) {
        Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng + "?q=pharmacy");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            // অ্যাপ ম্যাপ খুলতে ব্যর্থ হলে ব্রাউজারে খুলবে
            openGoogleMapsPharmacyWithoutLocation();
        }
        finish();
    }

    private void openGoogleMapsPharmacyWithoutLocation() {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=pharmacy");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndOpenMaps();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            openGoogleMapsPharmacyWithoutLocation();
        }
    }
}