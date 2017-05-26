package com.example.nouno.easydep_repairservice.Data;

/**
 * Created by nouno on 09/04/2017.
 */

public class CarOwner extends Person {
    private String noThing;
    public static String ANONYMOUS = "inconnu";
    public CarOwner(long id, String firstname, String lastname) {
        super(id, firstname, lastname);
    }

    @Override
    public String getFirstname() {
        if (super.getFirstname().equals(ANONYMOUS))
            return "Inconnu";
        else
        return super.getFirstname();
    }

    @Override
    public String getLastname() {
        if (super.getLastname().equals(ANONYMOUS))
            return "Inconnu";
        else
            return super.getLastname();
    }
}
