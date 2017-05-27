package com.example.nouno.easydep_repairservice.Data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nouno on 05/05/2017.
 */

public class Statistic {
    private int numberOfInterventionsThisWeek;
    private double averageInterventionsInWeek;
    private int totalInterventions;

    public Statistic(int numberOfInterventionsThisWeek, double averageInterventionsInWeek, int totalInterventions) {
        this.numberOfInterventionsThisWeek = numberOfInterventionsThisWeek;
        this.averageInterventionsInWeek = averageInterventionsInWeek;
        this.totalInterventions = totalInterventions;
    }



    public int getNumberOfInterventionsThisWeek() {
        return numberOfInterventionsThisWeek;
    }

    public double getAverageInterventionsInWeek() {
        return averageInterventionsInWeek;
    }

    public int getTotalInterventions() {
        return totalInterventions;
    }

    public static Statistic parseJson (String json)
    {
        Statistic statistic = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            statistic = new Statistic(jsonObject.getInt("number_of_interventions_this_week"),
                    jsonObject.getDouble("average_interventions_in_week"),jsonObject.getInt("total_interventions"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return statistic;
    }

}
