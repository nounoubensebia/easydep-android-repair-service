package com.example.nouno.easydep_repairservice.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.QueryUtils;
import com.example.nouno.easydep_repairservice.R;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.LinkedHashMap;
import java.util.Map;

public class Signup3Activity extends AppCompatActivity {
    TextInputLayout passwordWrapper;
    private RepairService repairService;
    View fab;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);
        passwordWrapper = (TextInputLayout)findViewById(R.id.passwordWrapper);
        fab = findViewById(R.id.fab);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        getRepairService();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordWrapper.getEditText().getText().toString();
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
    }


}
