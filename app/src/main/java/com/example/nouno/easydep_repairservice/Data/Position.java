package com.example.nouno.easydep_repairservice.Data;

/**
 * Created by nouno on 09/04/2017.
 */

public class Position {
    private double latitude;
    private double longitude;
    private String locationName;

    public Position(double latitude, double longitude, String locationName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
