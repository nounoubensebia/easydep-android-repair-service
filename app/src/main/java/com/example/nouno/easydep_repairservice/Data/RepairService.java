package com.example.nouno.easydep_repairservice.Data;

import android.util.Log;

import com.example.nouno.easydep_repairservice.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nouno on 09/04/2017.
 */

public class RepairService extends Person {
    private String phoneNumber;
    private int price;
    private int status;
    private String email;
    private Position position;
    private boolean automaticLocationDetection=true;
    public static final int NO_PRICE = 99999;
    public static final String NO_PRICE_STRING = "Tarifs non communiqués";
    public static final int NO_DURATION = -1;
    public static final int NOT_AVAILABLE = 0;
    public static final int AVAILABLE=1;
    public static final int INTERVENTION_UNDERWAY = -1;

    public RepairService(long id, String firstname, String lastname) {
        super(id, firstname, lastname);
    }

    public RepairService(long id, String firstname, String lastname, String phoneNumber, int price, int status, Position position) {
        super(id, firstname, lastname);
        this.phoneNumber = phoneNumber;
        this.price = price;
        this.status = status;
        this.position = position;
    }

    public RepairService(long id, String firstname, String lastname, String phoneNumber, int price, int status, String email, Position position) {
        super(id, firstname, lastname);
        this.phoneNumber = phoneNumber;
        this.price = price;
        this.status = status;
        this.email = email;
        this.position = position;
    }

    public boolean isAutomaticLocationDetection() {
        return automaticLocationDetection;
    }

    public void setAutomaticLocationDetection(boolean automaticLocationDetection) {
        this.automaticLocationDetection = automaticLocationDetection;
        Log.i("AUTOLOCATION",automaticLocationDetection+"");
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String toJson ()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getPriceString () {
        if (price!=NO_PRICE)
        {
            return price+"Da/KM";
        }
        else
        {
            return NO_PRICE_STRING;
        }
    }

    public String getAvailableString ()
    {
        if (status==AVAILABLE)
        {
            return "Disponible";

        }
        else
        {
            if (status == NOT_AVAILABLE)
            {
                return "Occupé";
            }
            else
            {


                  return "Intervention en cours";
            }
        }
    }
    public static RepairService fromJson (String json)
    {
        Gson gson = new Gson();
        RepairService repairService = gson.fromJson(json,RepairService.class);
        return repairService;
    }

    public static RepairService parseJson (String json)
    {

        RepairService repairService = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            String firstname = jsonObject.getString("first_name");
            String lastname = jsonObject.getString("last_name");
            String email = jsonObject.getString("email");
            long id = jsonObject.getLong("id");
            int price=RepairService.NO_PRICE;
            if (!jsonObject.isNull("price"))
                price = jsonObject.getInt("price");

            int status = jsonObject.getInt("available");
            String phoneNumber = jsonObject.getString("phone_number");
            Double latitude = jsonObject.getDouble("latitude");
            Double longitude = jsonObject.getDouble("longitude");
            String placeName = jsonObject.getString("place_name");
            Position position = new Position(latitude,longitude,placeName);
            repairService = new RepairService(id,firstname,lastname,phoneNumber,price,status,email,position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return repairService;
    }
}
