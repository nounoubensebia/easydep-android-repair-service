package com.example.nouno.easydep_repairservice.Data;

/**
 * Created by nouno on 09/04/2017.
 */

public class Path {
    private int distance;
    private int duration;

    public Path(int distance, int duration) {
        this.distance = distance;
        this.duration = duration;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }
}
