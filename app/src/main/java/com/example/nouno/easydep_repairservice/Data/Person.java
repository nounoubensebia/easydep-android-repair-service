package com.example.nouno.easydep_repairservice.Data;

/**
 * Created by nouno on 09/04/2017.
 */

public class Person {
    private long id;
    private String firstname;
    private String lastname;

    public Person(long id, String firstname, String lastname) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
    public String getFullName ()
    {
        return firstname+" "+lastname;
    }
}
