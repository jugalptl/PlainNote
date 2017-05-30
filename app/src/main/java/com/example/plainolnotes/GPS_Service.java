package com.example.plainolnotes;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Created by iGroup on 3/25/2017.
 */
public class GPS_Service extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    String location_name;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                /*Geocoder geocoder = new Geocoder(GPS_Service.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Address obj = addresses.get(0);
                    location_name = obj.getAdminArea()+" , "+obj.getPostalCode() ;

                }catch (Exception e)
                {
                    e.printStackTrace();
                }*/

                Intent i = new Intent("Location_Updates");
                i.putExtra("lat",location.getLatitude());
                i.putExtra("long",location.getLongitude());
               // i.putExtra("location",location_name);
                sendBroadcast(i);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null)
        {
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}


