//Student ID: 119338192
//Name: Yevgeniya Anasheva
package com.example.yevgeniya_weather.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WeatherContainer {
    private @SerializedName("current") Weather current;

    public Weather getCurrent() {
        return current;
    }

    public void setCurrent(Weather current) {
        this.current = current;
    }

    @Override
    public String toString() {
        return "WeatherContainer{" +
                "current=" + current +
                '}';
    }
}
