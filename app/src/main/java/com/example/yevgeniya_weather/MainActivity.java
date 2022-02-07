//Student ID: 119338192
//Name: Yevgeniya Anasheva
package com.example.yevgeniya_weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.yevgeniya_weather.databinding.ActivityMainBinding;
import com.example.yevgeniya_weather.helpers.LocationHelper;
import com.example.yevgeniya_weather.models.Condition;
import com.example.yevgeniya_weather.models.Weather;
import com.example.yevgeniya_weather.models.WeatherContainer;
import com.example.yevgeniya_weather.network.RetrofitClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getCanonicalName();
    ActivityMainBinding binding;
    private LocationHelper locationHelper;
    private Location lastLocation;
    private LocationCallback locationCallback;
    private static String coordinates = "";  //for saving the location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //bind the view
        this.binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());
        //loading the object and checking permissions
        this.locationHelper = LocationHelper.getInstance();
        this.locationHelper.checkPermissions(this);

        if(this.locationHelper.locationPermissionGranted) {
            Log.d(TAG, "onCreate: Location Permission Granted");

            //get the last location
            Log.d(TAG, "onCreate: Trying to get last location");

            this.locationHelper.getLastLocation(this).observe(this, new Observer<Location>() {
                @Override
                public void onChanged(Location location) {
                    if(location != null) {
                        lastLocation = location;
                        Address receivedAddress = locationHelper.performForwardGeocoding(getApplicationContext(), lastLocation);
                        //save the location
                        coordinates = location.getLatitude() + "," + location.getLongitude();
                        getCurrentWeather();

                        if(receivedAddress != null) {
                            binding.tvCity.setText(receivedAddress.getLocality());
                            binding.tvCountry.setText(receivedAddress.getCountryName());
                        }
                        else {
                            Log.e(TAG, "onChanged: last location not obtained");
                        }
                    }
                    else {
                        Log.e(TAG, "onCreate: Last Location not obtained");
                    }
                }
            });

            //keep listening for updated location
            this.initiateLocationListener();
        }
        else {
            Log.d(TAG, "onCreate: Location Permission Denied");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.locationHelper.stopLocationUpdates(this, this.locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.locationHelper.requestLocationUpdates(this, this.locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == this.locationHelper.REQUEST_CODE_LOCATION) {
            this.locationHelper.locationPermissionGranted = (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED);

            if (this.locationHelper.locationPermissionGranted){
                Log.d(TAG, "onRequestPermissionsResult: Result - Location Permission Granted "
                        + this.locationHelper.locationPermissionGranted);
            }
        }
    }

    private void initiateLocationListener() {
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location loc : locationResult.getLocations()) {
                    lastLocation = loc;
                    Address receivedAddress = locationHelper.performForwardGeocoding(getApplicationContext(), lastLocation);
                    //save the location
                    coordinates = loc.getLatitude() + "," + loc.getLongitude();
                    //fetch the weather
                    getCurrentWeather();

                    if(receivedAddress != null) {
                        binding.tvCity.setText(receivedAddress.getLocality());
                        binding.tvCountry.setText(receivedAddress.getCountryName());
                    }
                    else {
                        Log.e(TAG, "initiateLocationListener: last location not obtained");
                    }
                }
            }
        };
        this.locationHelper.requestLocationUpdates(this, locationCallback);
    }

    private void getCurrentWeather() {
        Call<WeatherContainer> weatherContainerCall = RetrofitClient.getInstance().getApi()
                .retrieveCurrentWeather("5a5a4544b9e74df186412338211807", coordinates, "no");
        try {
            weatherContainerCall.enqueue(new Callback<WeatherContainer>() {
                @Override
                public void onResponse(Call<WeatherContainer> call, Response<WeatherContainer> response) {
                    if (response.code() == 200) {
                        WeatherContainer weatherContainer = response.body();
                        if(weatherContainer.getCurrent() == null) {
                            Log.e(TAG, "onResponse: the current weather is empty");
                        }
                        else {
                            Log.d(TAG, "onResponse: weather received " + weatherContainer.toString());
                            Weather currentWeather = weatherContainer.getCurrent();
                            Condition condition = currentWeather.getCondition();
                            binding.tvCondition.setText(condition.getConditionText());
                            binding.tvTemperature.setText(Float.toString(currentWeather.getTemp()));
                            binding.tvFeels.setText(Float.toString(currentWeather.getFeelsLike()));
                            binding.tvWind.setText(Float.toString(currentWeather.getWind()));
                            binding.tvWindDirection.setText(currentWeather.getWindDirection());
                            binding.tvHumidity.setText(Integer.toString(currentWeather.getHumidity()));
                            binding.tvUvIndex.setText(Float.toString(currentWeather.getUvIndex()));
                            binding.tvVisibility.setText(Float.toString(currentWeather.getVisibility()));
                            if(condition.getConditionText().equals("Clear")) {
                                binding.imgResult.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sunny));
                            }
                            else if (condition.getConditionText().equals("Partly cloudy")) {
                                binding.imgResult.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.partly_cloudy));
                            }
                            else if (condition.getConditionText().equals("Light rain") || condition.getConditionText().equals("Heavy rain")) {
                                binding.imgResult.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rainy));
                            }
                            else {
                                binding.imgResult.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.clouds));
                            }
                        }
                    }
                    else {
                        Log.e(TAG, "onResponse: unsuccessful response");
                    }
                    weatherContainerCall.cancel();
                }

                @Override
                public void onFailure(Call<WeatherContainer> call, Throwable t) {
                    Log.e(TAG, "onFailure: Call Failed " + t.getLocalizedMessage() );
                }
            });
        }
        catch(Exception ex) {
            Log.e(TAG, "getCurrentWeather: exception occurred while fetching weather " + ex.getLocalizedMessage());
        }
    }
}