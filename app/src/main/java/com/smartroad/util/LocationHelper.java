package com.smartroad.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

/** Thin wrapper over the Fused Location Provider. */
public class LocationHelper {

    public interface LocationResult {
        void onLocation(Location location);
        void onError(String message);
    }

    private final FusedLocationProviderClient client;
    private final Context context;

    public LocationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.client = LocationServices.getFusedLocationProviderClient(this.context);
    }

    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(final LocationResult callback) {
        if (!hasLocationPermission()) {
            callback.onError("Location permission not granted");
            return;
        }
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,
                        new CancellationTokenSource().getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        callback.onLocation(location);
                    } else {
                        // Fall back to last known location
                        client.getLastLocation()
                                .addOnSuccessListener(last -> {
                                    if (last != null) callback.onLocation(last);
                                    else callback.onError("Unable to get location");
                                })
                                .addOnFailureListener(e -> callback.onError(e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
