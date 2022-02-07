//Student ID: 119338192
//Name: Yevgeniya Anasheva
package com.example.yevgeniya_weather.models;

import com.google.gson.annotations.SerializedName;

public class Condition {
    private @SerializedName("text") String condition;

    public String getConditionText() {
        return condition;
    }
}
