//Student ID: 119338192
//Name: Yevgeniya Anasheva
package com.example.yevgeniya_weather.helpers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;

public class LocationHelper {
    private final String TAG = this.getClass().getCanonicalName();
    public boolean locationPermissionGranted = false;
    public final int REQUEST_CODE_LOCATION = 101;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient = null;
    //Location mLocation;
    MutableLiveData<Location> mLocation = new MutableLiveData<>();

    //Singleton Pattern
    private static final LocationHelper singletonInstance = new LocationHelper();
    public static LocationHelper getInstance() {
        return singletonInstance;
    }

    private LocationHelper() {
        this.locationRequest = new LocationRequest();
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        this.locationRequest.setInterval(10000);  //update every 10 seconds
    }

    //check if the user granted permissions
    public void checkPermissions(Context context) {
        //check if the current application has permissions
        this.locationPermissionGranted = (PackageManager.PERMISSION_GRANTED ==
                (ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)));

        Log.d(TAG, "checkPermissions: locationPermissionGranted : " + this.locationPermissionGranted);

        if(!this.locationPermissionGranted) {
            requestLocationPermission(context);
        }
    }

    public void requestLocationPermission(Context context){
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.REQUEST_CODE_LOCATION );
    }

    public FusedLocationProviderClient getFusedLocationProviderClient(Context context) {
        if(this.fusedLocationProviderClient == null) {
            this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        }
        return this.fusedLocationProviderClient;
    }

    @SuppressLint("MissingPermission")
    public MutableLiveData<Location> getLastLocation(Context context) {
        Log.d(TAG, "getLastLocation: location helper initiated");
        if(this.locationPermissionGranted) {
            try {
                this.getFusedLocationProviderClient(context)
                        .getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location != null) {
                                    mLocation.setValue(location);
                                    Log.d(TAG, "onSuccess: Last Location -- Latitude : " + mLocation.getValue().getLatitude() +
                                            " Longitude : " + mLocation.getValue().getLongitude());
                                }
                                Log.d(TAG, "onSuccess: completed");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.e(TAG, "getLastLocation: Exception while accessing last location " + e.getLocalizedMessage());

                                Log.d(TAG, "onFailure: completed");
                            }
                        });
            }
            catch(Exception ex) {
                Log.e(TAG, "getLastLocation: Exception while accessing last location " + ex.getLocalizedMessage());
                return null;
            }
            return this.mLocation;
        }
        else {
            Log.e(TAG, "getLastLocation: Warning - The app doesn't have access to location");
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdates(Context context, LocationCallback locationCallback) {
        if (this.locationPermissionGranted) {
            try{
                this.getFusedLocationProviderClient(context)
                        .requestLocationUpdates(this.locationRequest, locationCallback, Looper.getMainLooper());
            }
            catch(Exception ex) {
                Log.e(TAG, "requestLocationUpdates: Exception while getting location updates " + ex.getLocalizedMessage() );
            }
        }
    }

    public void stopLocationUpdates(Context context, LocationCallback locationCallback){
        try{
            this.getFusedLocationProviderClient(context).removeLocationUpdates(locationCallback);
        }
        catch(Exception ex) {
            Log.e(TAG, "stopLocationUpdates: Exception while stopping location updates " + ex.getLocalizedMessage() );
        }
    }
    //forward geocoding
    public Address performForwardGeocoding(Context context, Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(addressList.size() > 0) {
                Address addressObj = addressList.get(0);
                Log.d(TAG, "performForwardGeocoding: Address obtained from forward geocoding " + addressObj.getAddressLine(0));
                return addressObj;
            }
        }
        catch(Exception ex) {
            Log.e(TAG, "performForwardGeocoding: Couldn't get address for the given location" + ex.getLocalizedMessage());
        }
        return null;
    }
}
