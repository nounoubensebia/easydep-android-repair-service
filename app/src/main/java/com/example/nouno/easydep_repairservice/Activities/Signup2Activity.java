package com.example.nouno.easydep_repairservice.Activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.R;
import com.google.gson.Gson;

public class Signup2Activity extends AppCompatActivity {
    TextInputLayout emailWrapper;
    TextInputLayout phoneNumberWrapper;
    View fab;
    private RepairService repairService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        emailWrapper = (TextInputLayout)findViewById(R.id.eamilWrapper);
        phoneNumberWrapper = (TextInputLayout)findViewById(R.id.phoneNumberWrapper);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailWrapper.getEditText().getText().toString();
                String phoneNumber = phoneNumberWrapper.getEditText().getText().toString();
                repairService = RepairService.fromJson(getIntent().getExtras().getString("repairService"));
                repairService.setPhoneNumber(phoneNumber);
                repairService.setEmail(email);
                String json = repairService.toJson();
                Intent i = new Intent(getApplicationContext(),Signup3Activity.class);
                i.putExtra("repairService",json);
                startActivity(i);
                //TODO verify email and phonenumber existance
            }
        });
    }
}
