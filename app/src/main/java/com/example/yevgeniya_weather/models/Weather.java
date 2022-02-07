//Student ID: 119338192
//Name: Yevgeniya Anasheva
package com.example.yevgeniya_weather.models;

import com.google.gson.annotations.SerializedName;

public class Weather {
    private Condition condition;
    private @SerializedName("temp_c") float temp;
    private @SerializedName("feelslike_c") float feelsLike;
    private @SerializedName("wind_kph") float wind;
    private @SerializedName("wind_dir") String windDirection;
    private int humidity;
    private @SerializedName("uv") float uvIndex;
    private @SerializedName("vis_km") float visibility;

    public Condition getCondition() {
        return condition;
    }

    public float getTemp() {
        return temp;
    }

    public float getFeelsLike() {
        return feelsLike;
    }

    public float getWind() {
        return wind;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public int getHumidity() {
        return humidity;
    }

    public float getUvIndex() {
        return uvIndex;
    }

    public float getVisibility() {
        return visibility;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public void setFeelsLike(float feelsLike) {
        this.feelsLike = feelsLike;
    }

    public void setWind(float wind) {
        this.wind = wind;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setUvIndex(float uvIndex) {
        this.uvIndex = uvIndex;
    }

    public void setVisibility(float visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "condition=" + condition.getConditionText() +
                ", temp=" + temp +
                ", feelsLike=" + feelsLike +
                ", wind=" + wind +
                ", windDirection='" + windDirection + '\'' +
                ", humidity=" + humidity +
                ", uvIndex=" + uvIndex +
                ", visibility=" + visibility +
                '}';
    }
}
