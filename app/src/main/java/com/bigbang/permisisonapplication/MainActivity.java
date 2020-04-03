package com.bigbang.permisisonapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private final int REQUEST_CODE = 707;

    private TextView permissionRequiredTextView;
    private Button openSettingsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionRequiredTextView = findViewById(R.id.textView);
        openSettingsButton = findViewById(R.id.open_setting_button);
        openSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "Permissions");
//                "System.openSettings(package://com.bigbang.permisisonapplication)"

                openSettings.setData(uri);
                startActivity(openSettings);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //check if permission was granted
        //Step 1
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
        ) {
            setUpLocation();
            setVisibilityGone();
        } else {
            //Step 2 if is not granted
            requestPermissions();
        }
    }

    private void setVisibilityGone() {
        permissionRequiredTextView.setVisibility(View.GONE);
        openSettingsButton.setVisibility(View.GONE);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    setUpLocation();
                else { //Permission was denied

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                        requestPermissions();
                    else
                        showRequirements();
                }
            } if (permissions[1].equals(Manifest.permission.READ_SMS)) {
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    setUpLocation();
                else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS))
                        requestPermissions();
                    else
                        showRequirements();
                }
            }
        }

    }

    private void showRequirements() {

        permissionRequiredTextView.setVisibility(View.VISIBLE);
        openSettingsButton.setVisibility(View.VISIBLE);

    }

    @SuppressLint("MissingPermission")
    private void setUpLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                10,
                this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationManager != null)
            locationManager.removeUpdates(this); //This will also stop memory leaks....
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("TAG_X", "LOCATION : " + location.getLatitude() + "," + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
