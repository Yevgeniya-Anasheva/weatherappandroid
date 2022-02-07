//Student ID: 119338192
//Name: Yevgeniya Anasheva
package com.example.yevgeniya_weather.network;

import com.example.yevgeniya_weather.models.Weather;
import com.example.yevgeniya_weather.models.WeatherContainer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {
    String BASE_URL = "https://api.weatherapi.com/v1/";

    //https://api.weatherapi.com/v1/current.json?key=5a5a4544b9e74df186412338211807&q=43.64599,-79.3776167&aqi=no
    @GET("./current.json")
    Call<WeatherContainer> retrieveCurrentWeather(@Query("key") String key, @Query("q") String coordinates, @Query("aqi") String airQuality);
}
