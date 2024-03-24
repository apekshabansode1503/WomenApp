package com.example.womensafety.Activities.ui.home;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSTracker implements LocationListener {

    private final Context mContext;
    private Location mLocation;

    public GPSTracker(Context context) {
        mContext = context;
        getLocation();
    }

    private void getLocation() {
        try {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public Location getlocation() {
        return mLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    public boolean isLocationAvailable() {
        return mLocation != null;
    }
}
