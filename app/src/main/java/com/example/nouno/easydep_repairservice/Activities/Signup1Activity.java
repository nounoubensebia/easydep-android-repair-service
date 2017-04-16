package com.example.nouno.easydep_repairservice.Activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.R;

public class Signup1Activity extends AppCompatActivity {
    TextInputLayout firstnameWrapper;
    TextInputLayout lastnameWrapper;
    RepairService repairService;
    View fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firstnameWrapper = (TextInputLayout)findViewById(R.id.firstNameWrapper);
        lastnameWrapper = (TextInputLayout)findViewById(R.id.lastNameWrapper);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = firstnameWrapper.getEditText().getText().toString();
                String lastname = lastnameWrapper.getEditText().getText().toString();
                repairService = new RepairService(-1,firstname,lastname);
                String json = repairService.toJson();
                Intent i = new Intent(getApplicationContext(),Signup2Activity.class);
                i.putExtra("repairService",json);
                startActivity(i);
            }
        });
    }
}
