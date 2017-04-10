package com.example.nouno.easydep_repairservice.Data;

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
    private Position departure;
    private Path toDeparture;
    private Position destination;
    private Path toDestination;
    private long time;
    public AssistanceRequest(long id,CarOwner carOwner, Position departure, Path toDeparture,long time) {
        this.id = id;
        this.carOwner = carOwner;
        this.departure = departure;
        this.toDeparture = toDeparture;
        this.time = time;
        destination = null;
        toDestination = null;
    }

    public AssistanceRequest(long id, CarOwner carOwner, Position departure, Path toDeparture, Position destination, Path toDestination, long time) {
        this.id = id;
        this.carOwner = carOwner;
        this.departure = departure;
        this.toDeparture = toDeparture;
        this.destination = destination;
        this.toDestination = toDestination;
        this.time = time;
    }

    public AssistanceRequest(long id, CarOwner carOwner, Position departure, Position destination, long time) {
        this.id = id;
        this.carOwner = carOwner;
        this.departure = departure;
        this.destination = destination;
        this.time = time;
    }

    public AssistanceRequest() {
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

    public Position getDeparture() {
        return departure;
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

    public void setDeparture(Position departure) {
        this.departure = departure;
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
                double latitude = departureJsonObject.getLong("latitude");
                double longitude = departureJsonObject.getLong("longitude");
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
                assistanceRequests.add(assistanceRequest);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return assistanceRequests;
    }
}
