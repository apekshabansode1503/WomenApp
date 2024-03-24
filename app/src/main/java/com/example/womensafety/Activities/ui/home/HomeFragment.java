package com.example.womensafety.Activities.ui.home;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.womensafety.R;
import com.example.womensafety.databinding.FragmentHomeBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SharedPreferences sharedPreferences;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        binding.add.setOnClickListener(v -> showAddContactDialog());

        sharedPreferences = requireActivity().getSharedPreferences("contact", Context.MODE_PRIVATE);

        binding.panic.setOnClickListener(v -> {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    requestLocation();
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                }
            } else {
                showEnableLocationServicesDialog();
            }
        });

        return root;
    }

    private void showEnableLocationServicesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Location Services Disabled")
                .setMessage("Location services are disabled. Please enable them to use this feature.")
                .setPositiveButton("Enable", (dialog, which) -> {
                    // Open location settings
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(getContext(), "Location is necessary", Toast.LENGTH_SHORT).show();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private void requestLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            // Permissions already granted
           LocationGetting();
        }
    }

    private void LocationGetting() {
        // Get the last known location
        // Note: This may return null if location is disabled or not available
        GPSTracker gpsTracker = new GPSTracker(getContext());
        Location location = gpsTracker.getlocation();

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.d("GURU",""+latitude+longitude);
                binding.longitude.setText(String.valueOf(longitude));
                binding.latitude.setText(String.valueOf(latitude));
                getAddressFromLocation(getContext(), latitude, longitude);
        } else {
            Toast.makeText(getContext(), "Location not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location
                getLocation();
            } else {
                // Permission denied
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void getAddressFromLocation(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressLine = address.getAddressLine(0);
                String city = address.getLocality();
                String state = address.getAdminArea();
                String country = address.getCountryName();
                String postalCode = address.getPostalCode();
                String knownName = address.getFeatureName();

                sharedPreferences = requireActivity().getSharedPreferences("contact", Context.MODE_PRIVATE);
                String name = sharedPreferences.getString("Name", "");
                String fullAddress = "SOS! I am in danger "+ addressLine + ", " + city + ", " + state + ", " + country;
                SendMessage(fullAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void SendMessage(String message) {
        sharedPreferences = requireActivity().getSharedPreferences("contact", Context.MODE_PRIVATE);
        String contact = sharedPreferences.getString("Contact", "");
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contact, null, message, null, null);
            Toast.makeText(getContext(), "SOS Sent Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to send SOS", Toast.LENGTH_SHORT).show();
        }
    }


    private void showAddContactDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_contact);
        dialog.setTitle("Add Contact");

        EditText nameEditText = dialog.findViewById(R.id.nameEditText);
        EditText numberEditText = dialog.findViewById(R.id.numberEditText);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String number = numberEditText.getText().toString().trim();
            SaveData(name, number);
            Toast.makeText(getContext(), "Contact Added Successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void SaveData(String name, String number) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", name);
        editor.putString("Contact", number);
        editor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
        binding = null;
    }
}
