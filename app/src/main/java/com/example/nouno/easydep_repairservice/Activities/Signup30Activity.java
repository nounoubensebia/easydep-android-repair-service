package com.example.nouno.easydep_repairservice.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.nouno.easydep_repairservice.R;

public class Signup30Activity extends AppCompatActivity {
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup30);
        //spinner = (Spinner)findViewById(R.id.spinner);
        String[] wilayas = getResources().getStringArray(R.array.wilaya);
        //spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,wilayas));
    }
}
