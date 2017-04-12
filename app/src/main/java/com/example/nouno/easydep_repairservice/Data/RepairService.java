package com.example.nouno.easydep_repairservice.Data;

import com.google.gson.Gson;

/**
 * Created by nouno on 09/04/2017.
 */

public class RepairService extends Person {
    public RepairService(long id, String firstname, String lastname) {
        super(id, firstname, lastname);
    }
    public String toJson ()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
