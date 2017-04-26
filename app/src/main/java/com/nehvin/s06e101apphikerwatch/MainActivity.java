package com.nehvin.s06e101apphikerwatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locMgr;
    LocationListener locationListener;


    private void updateLocationInfo(Location location)
    {
        Log.i("Location", location.toString());
        TextView lat = (TextView) findViewById(R.id.latitude);
        TextView longi = (TextView) findViewById(R.id.longitude);
        TextView accuracy = (TextView) findViewById(R.id.accuracy);
        TextView alti = (TextView) findViewById(R.id.altitude);
        TextView addr = (TextView) findViewById(R.id.address);

        lat.setText("Latitude: "+location.getLatitude());
        longi.setText("Longitude: "+location.getLongitude());
        accuracy.setText("Accuracy: "+location.getAccuracy());
        alti.setText("Altitude: "+location.getAltitude());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> address = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            if(address != null && address.size()>0)
            {
                Log.i("Place",address.get(0).toString());
                StringBuilder addressBuild = new StringBuilder();
                addressBuild.append("\n");
//                addressBuild.append(address.get(0).getAddressLine(0)).append("\n");
//                addressBuild.append(address.get(0).getAddressLine(1)).append("\n");
//                addressBuild.append(address.get(0).getAddressLine(2)).append("\n");
//                addressBuild.append(address.get(0).getAddressLine(3)).append("\n");
                addressBuild.append(address.get(0).getSubThoroughfare()).append("\n");
                addressBuild.append(address.get(0).getThoroughfare()).append("\n");
                addressBuild.append(address.get(0).getSubLocality()).append("\n");
                addressBuild.append(address.get(0).getLocality()).append("\n");
                addressBuild.append(address.get(0).getPostalCode()).append("\n");
                addressBuild.append(address.get(0).getCountryName()).append("\n");
                addr.setText("Address: "+addressBuild.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && permissions.length > 0 && grantResults.length > 0
                && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location location = fetchBestLocation();
//                Location location = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location != null) {
                    updateLocationInfo(location);
                }


            } else {
                locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location location = fetchBestLocation();
//                Location location = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location != null) {
                    updateLocationInfo(location);
                }

            }
        }
        else
        {
            Toast.makeText(this, "Please Grant Location Permission and Restart App", Toast.LENGTH_SHORT).show();
        }
    }

    private Location fetchBestLocation() {
        Location locationGPS = null;
        Location locationNetwork = null;

        // get both but return more accurate of GPS & network location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationGPS = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationNetwork = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (locationGPS == null && locationNetwork == null) return null;
        else if (locationGPS == null) return locationNetwork;
        else if (locationNetwork == null) return locationGPS;
        else return (locationGPS.getAccuracy() < locationNetwork.getAccuracy() ? locationGPS : locationNetwork);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
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
        };

        if(Build.VERSION.SDK_INT < 23 )
        {
            locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener );
        }
        else
        {
            if(ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else
            {
                locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener );
                Location location = fetchBestLocation();
//                Location location = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location != null) {
                    updateLocationInfo(location);
                }
            }
        }
    }
}