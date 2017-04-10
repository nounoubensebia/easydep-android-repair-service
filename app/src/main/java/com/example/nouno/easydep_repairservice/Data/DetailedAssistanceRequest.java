package com.example.nouno.easydep_repairservice.Data;

import com.google.gson.Gson;

/**
 * Created by nouno on 10/04/2017.
 */

public class DetailedAssistanceRequest extends AssistanceRequest {

    private boolean heavy;
    private boolean vehiculeCanMove;
    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 15;
    public static final int MIN_WEIGHT = 6;
    public static final int MAX_WEIGHT = 12;
    public static final int DONT_KNOW = -1;
    public static final int NOT_HEAVY = -2;
    private float length;
    private float weight;

    public DetailedAssistanceRequest(long id, CarOwner carOwner, Position departure, Position destination, long time, boolean heavy, boolean vehiculeCanMove, float length, float weight) {
        super(id, carOwner, departure, destination, time);
        this.heavy = heavy;
        this.vehiculeCanMove = vehiculeCanMove;
        this.length = length;
        this.weight = weight;
    }

    public DetailedAssistanceRequest() {
        heavy = false;
        vehiculeCanMove = false;
        length = NOT_HEAVY;
        weight = NOT_HEAVY;
        CarOwner carOwner = new CarOwner(-1,CarOwner.ANONYMOUS,CarOwner.ANONYMOUS);
        setCarOwner(carOwner);
    }

    public boolean isHeavy() {
        return heavy;
    }

    public boolean isVehiculeCanMove() {
        return vehiculeCanMove;
    }

    public float getLength() {
        return length;
    }

    public float getWeight() {
        return weight;
    }

    public void setHeavy(boolean heavy) {
        this.heavy = heavy;
    }

    public void setVehiculeCanMove(boolean vehiculeCanMove) {
        this.vehiculeCanMove = vehiculeCanMove;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getLengthString ()
    {
        if (length==DONT_KNOW)
        {
            return "Non spécifiée";
        }
        else
        {
            return length+" Metres";
        }
    }

    public String getWeightString ()
    {
        if (weight==DONT_KNOW)
        {
            return "Non spécifié";
        }
        else
        {
            return weight+" Tonnes";
        }
    }

    public String getDimensionsString () {
        if (weight==DONT_KNOW&&length==DONT_KNOW)
        {
            return "Non spécifiés";
        }

        if (weight==NOT_HEAVY&&length==NOT_HEAVY)
            return "Non défini";
        return "Longeur : "+getLengthString()+", "+"Poids : "+getWeightString();
    }
    public String getDepartureString()
    {
        if (getDeparture()==null)
        {
            return "Non spécifiée";
        }
        else
        {
            return getDeparture().getLocationName();
        }
    }
    public String getDestinationString()
    {
        if (getDestination()==null)
        {
            return "Non spécifiée";
        }
        else
        {
            return getDestination().getLocationName();
        }
    }
    public String toJson ()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
