package com.example.nouno.easydep_repairservice.Data;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by nouno on 09/04/2017.
 */

public class AssistanceRequest {
    private long id;
    private CarOwner carOwner;
    private Position userPositon;
    private Path toDeparture;
    private Position destination;
    private Path toDestination;
    private long time;
    private boolean heavy;
    private boolean vehiculeCanMove;
    private int flag;
    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 15;
    public static final int MIN_WEIGHT = 6;
    public static final int MAX_WEIGHT = 12;
    public static final int DONT_KNOW = -1;
    public static final int NOT_HEAVY = -2;
    private float length;
    private float weight;
    public static final int FLAG_ESTIMATE_REQUEST = 0;
    public static final int FLAG_IN_QUEUE = 1;
    public static final int FLAG_INTERVENTION = 2;

    public AssistanceRequest(long id, CarOwner carOwner, Position userPositon, Path toDeparture, long time) {
        this.id = id;
        this.carOwner = carOwner;
        this.userPositon = userPositon;
        this.toDeparture = toDeparture;
        this.time = time;
        destination = null;
        toDestination = null;
    }

    public AssistanceRequest(long id, CarOwner carOwner, Position userPositon, Path toDeparture, Position destination, Path toDestination, long time) {
        flag = FLAG_ESTIMATE_REQUEST;
        this.id = id;
        this.carOwner = carOwner;
        this.userPositon = userPositon;
        this.toDeparture = toDeparture;
        this.destination = destination;
        this.toDestination = toDestination;
        this.time = time;
    }

    public AssistanceRequest(long id, CarOwner carOwner, Position userPositon, Position destination, long time) {
        flag = FLAG_ESTIMATE_REQUEST;
        this.id = id;
        this.carOwner = carOwner;
        this.userPositon = userPositon;
        this.destination = destination;
        this.time = time;
    }

    public AssistanceRequest(long id, CarOwner carOwner, Position userPositon, Path toDeparture, Position destination, Path toDestination, long time, boolean heavy, boolean vehiculeCanMove, float length, float weight) {
        this.id = id;
        this.carOwner = carOwner;
        this.userPositon = userPositon;
        this.toDeparture = toDeparture;
        this.destination = destination;
        this.toDestination = toDestination;
        this.time = time;
        this.heavy = heavy;
        this.vehiculeCanMove = vehiculeCanMove;
        this.length = length;
        this.weight = weight;
        flag = FLAG_ESTIMATE_REQUEST;
    }

    public AssistanceRequest() {
        flag = FLAG_ESTIMATE_REQUEST;
        heavy =false;
        vehiculeCanMove = false;
        length = NOT_HEAVY;
        weight = NOT_HEAVY;
        carOwner = new CarOwner(-1,CarOwner.ANONYMOUS,CarOwner.ANONYMOUS);
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public CarOwner getCarOwner() {
        return carOwner;
    }

    public Position getUserPositon() {
        return userPositon;
    }

    public Path getToDeparture() {
        return toDeparture;
    }

    public Position getDestination() {
        return destination;
    }

    public Path getToDestination() {
        return toDestination;
    }

    public void setDestination(Position destination) {
        this.destination = destination;
    }

    public void setToDestination(Path toDestination) {
        this.toDestination = toDestination;
    }

    public void setUserPositon(Position userPositon) {
        this.userPositon = userPositon;
    }

    public void setCarOwner(CarOwner carOwner) {
        this.carOwner = carOwner;
    }

    public String getDepartureDurationString ()
    {
        double durationInMinutes = toDeparture.getDuration()/60;
        NumberFormat nf = new DecimalFormat("0.#");
        String s = nf.format(durationInMinutes);
        return ("a "+s+" Minutes de route");
    }
    public String getDepartureToDestinationString()
    {
        double durationInMinutes = toDestination.getDuration()/60;
        NumberFormat nf = new DecimalFormat("0.#");
        String s = nf.format(durationInMinutes)+" min";
        double distanceInKm = toDestination.getDistance()/1000;

        if (distanceInKm <10)
        {nf = new DecimalFormat("0.#");
            s +=" "+"("+nf.format(distanceInKm)+"km)";}
        else
        {
            int dist = (int)distanceInKm;
            s+=" "+"("+dist+"km)";
        }
        return s;
    }

    public String getDepartureDistanceString()
    {
        double distanceInKm = toDeparture.getDistance()/1000;
        String s = "";
        NumberFormat nf = new DecimalFormat("0.#");
        if (distanceInKm <10)
        {
            s= nf.format(distanceInKm)+"KM";
        }
        else
        {
            int dist = (int)distanceInKm;
            s=dist+"KM";
        }
        return s;
    }

    public static ArrayList<AssistanceRequest> parseJson (String json)
    {
        ArrayList<AssistanceRequest> assistanceRequests = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                long assistanceRequestId = jsonObject.getLong("id");
                JSONObject carOnwerJsonObject = jsonObject.getJSONObject("car_owner");
                long carOwnerid = carOnwerJsonObject.getLong("id");
                String firstname = carOnwerJsonObject.getString("first_name");
                String lastname = carOnwerJsonObject.getString("last_name");
                CarOwner carOwner = new CarOwner(carOwnerid,firstname,lastname);
                JSONObject departureJsonObject = jsonObject.getJSONObject("departure");
                double latitude = departureJsonObject.getDouble("latitude");
                double longitude = departureJsonObject.getDouble("longitude");
                String locationName = departureJsonObject.getString("location_name");
                Position departure = new Position(latitude,longitude,locationName);
                JSONObject toDepartureJsonObject = jsonObject.getJSONObject("to_departure");
                int distance = toDepartureJsonObject.getInt("distance");
                int duration = toDepartureJsonObject.getInt("duration");
                Path toDeparture = new Path(distance,duration);
                long time = jsonObject.getLong("time");
                AssistanceRequest assistanceRequest = new AssistanceRequest(assistanceRequestId,carOwner,departure,toDeparture,time);
                if (jsonObject.has("destination"))
                {
                    JSONObject destinationJsonObject = jsonObject.getJSONObject("destination");
                    latitude = destinationJsonObject.getDouble("latitude");
                    longitude =destinationJsonObject.getDouble("longitude");
                    locationName = destinationJsonObject.getString("location_name");
                    Position destination = new Position(latitude,longitude,locationName);
                    JSONObject toDestinationJsonObject = jsonObject.getJSONObject("to_destination");
                    distance = toDestinationJsonObject.getInt("distance");
                    duration = toDestinationJsonObject.getInt("duration");
                    Path toDestinaion = new Path(distance,duration);
                    assistanceRequest.setDestination(destination);
                    assistanceRequest.setToDestination(toDestinaion);
                }
                boolean heavy = jsonObject.getBoolean("heavy");
                assistanceRequest.setHeavy(heavy);
                if (heavy)
                {
                    assistanceRequest.setLength((float)jsonObject.getDouble("length"));
                    assistanceRequest.setWeight((float)jsonObject.getDouble("weight"));
                }
                assistanceRequests.add(assistanceRequest);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return assistanceRequests;
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
            return length+" Métres";
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
        if (getUserPositon()==null)
        {
            return "Non spécifiée";
        }
        else
        {
            return getUserPositon().getLocationName();
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
