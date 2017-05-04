package com.example.nouno.easydep_repairservice.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.R;

public class PriceActivity extends AppCompatActivity {

    private RepairService repairService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
    }
}
