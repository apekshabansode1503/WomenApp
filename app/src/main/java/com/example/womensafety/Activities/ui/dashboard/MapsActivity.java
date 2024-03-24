package com.example.womensafety.Activities.ui.dashboard;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.womensafety.Auth.Login;
import com.example.womensafety.Model.LocationModel;
import com.example.womensafety.R;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.womensafety.databinding.ActivityMapsBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


import java.util.UUID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;

    private FirebaseDatabase database ;
    private FirebaseAuth auth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            if (ContextCompat.checkSelfPermission(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        100);
            }
        }
        if (mMap != null) {
            mMap.setOnMapClickListener(this);
        }
        binding.btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
        addMarkerAtCurrentLocation();
    }

    private void addMarkerAtCurrentLocation() {
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                            }
                        }
                    });
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.clear(); // Clear existing marker
                                mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location")); // Add new marker
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                                
                                binding.add.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AddToDatabase(location);
                                    }
                                });
                            } else {
                                Toast.makeText(MapsActivity.this, "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
        }
    }

    private void AddToDatabase(Location location) {

        double lat = location.getLatitude();
        double lon = location.getLongitude();
        String Uid = UUID.randomUUID().toString();

        LocationModel model = new LocationModel(Uid,lon,lat);
        database.getReference().child("SafePlaces").child(auth.getCurrentUser().getUid()).child(Uid)
                        .setValue( model)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(MapsActivity.this, "Location has Successfully Added", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(MapsActivity.this, "Failed to add the Location"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker").snippet("Lat: " + latLng.latitude + ", Lng: " + latLng.longitude));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15)); // Move camera
    }
}
