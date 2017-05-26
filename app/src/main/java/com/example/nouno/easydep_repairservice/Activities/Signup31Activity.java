package com.example.nouno.easydep_repairservice.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.R;

public class Signup31Activity extends AppCompatActivity {
    private View fab;
    private RepairService repairService;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup31);
        getRepairService();
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),LocationSearchActivity.class);
                    i.putExtra("password",password);
                    i.putExtra("repairService",repairService.toJson());
                    startActivity(i);
                    finish();
            }
        });
    }

    private void getRepairService ()
    {
        repairService = RepairService.fromJson(getIntent().getExtras().getString("repairService"));
        password = getIntent().getExtras().getString("password");
    }
}
