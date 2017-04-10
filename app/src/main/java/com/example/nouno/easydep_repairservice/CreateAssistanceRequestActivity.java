package com.example.nouno.easydep_repairservice;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nouno.easydep_repairservice.Data.DetailedAssistanceRequest;
import com.google.gson.Gson;

public class CreateAssistanceRequestActivity extends AppCompatActivity {
    private DetailedAssistanceRequest assistanceRequest;
    private CreateAssistanceRequestActivity createAssistanceRequestActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAssistanceRequestActivity = this;
        setContentView(R.layout.activity_manual_assistance_request);
        retrieveData();
        displayData();
    }

    private void retrieveData()
    {
        Gson gson = new Gson();
        if (getIntent().getExtras()!=null&&getIntent().getExtras().containsKey("assistanceRequest"))
        {
        String json = getIntent().getExtras().getString("assistanceRequest");
        assistanceRequest = gson.fromJson(json,DetailedAssistanceRequest.class);
        }
        else assistanceRequest = new DetailedAssistanceRequest();

    }

    private void displayData ()
    {
        View upImage = findViewById(R.id.go_button);
        View positionLayout = findViewById(R.id.positionLayout);
        View destinationLayout = findViewById(R.id.destinationLayout);
        View dimensionsLayout = findViewById(R.id.dimensionsLayout);
        View firstnameLayout = findViewById(R.id.firstnameLayout);
        View lastnameLayout = findViewById(R.id.lastnameLayout);
        final TextView dimensionsTitle = (TextView)findViewById(R.id.dimensionsTitleText);
        final TextView dimensionsText = (TextView)findViewById(R.id.dimensionsText);
        final TextView firstnameText = (TextView)findViewById(R.id.firstnameText);
        final TextView lastnameText = (TextView)findViewById(R.id.lastnameText);
        firstnameText.setText(assistanceRequest.getCarOwner().getFirstname());
        lastnameText.setText(assistanceRequest.getCarOwner().getLastname());
        TextView positionText = (TextView)findViewById(R.id.positionText);
        positionText.setText(assistanceRequest.getDepartureString());
        final TextView destinationText = (TextView)findViewById(R.id.destinationText);
        destinationText.setText(assistanceRequest.getDestinationString());
        Switch heavyWeightSwitch = (Switch)findViewById(R.id.heavy_vehicule_switcher);
        Switch vehiculeCanMove = (Switch)findViewById(R.id.vehicule_can_move_switcher);
        if (assistanceRequest.isHeavy())
        {
            heavyWeightSwitch.setChecked(true);
            dimensionsTitle.setTextColor(Color.parseColor("#000000"));
            dimensionsText.setText(assistanceRequest.getDimensionsString());
        }
        positionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDepartureSearch();
            }
        });
        destinationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDestinationSearch();
            }
        });
        heavyWeightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    assistanceRequest.setHeavy(true);
                    assistanceRequest.setLength(DetailedAssistanceRequest.DONT_KNOW);
                    assistanceRequest.setWeight(DetailedAssistanceRequest.DONT_KNOW);
                    startHeavyRequestActivity();
                }
                else {
                    assistanceRequest.setHeavy(false);
                    assistanceRequest.setLength(DetailedAssistanceRequest.NOT_HEAVY);
                    assistanceRequest.setWeight(DetailedAssistanceRequest.NOT_HEAVY);
                    dimensionsText.setText(assistanceRequest.getDimensionsString());
                }
            }
        });
        vehiculeCanMove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                assistanceRequest.setVehiculeCanMove(isChecked);
            }
        });
        dimensionsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHeavyRequestActivity();
            }
        });
        upImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG",assistanceRequest.toJson());
            }
        });
        lastnameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = DialogUtils.createDialog(createAssistanceRequestActivity,"Prénom", new OnButtonClickListener<String>() {
                    @Override
                    public void OnClick(String s) {
                        assistanceRequest.getCarOwner().setLastname(s);
                        lastnameText.setText(s);
                    }
                });dialog.show();
            }

        });
        firstnameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = DialogUtils.createDialog(createAssistanceRequestActivity,"Nom", new OnButtonClickListener<String>() {
                    @Override
                    public void OnClick(String s) {
                        assistanceRequest.getCarOwner().setFirstname(s);
                        firstnameText.setText(s);
                    }
                });dialog.show();
            }
        });
    }

    private void startHeavyRequestActivity ()
    {
        Intent i = new Intent(this,HeavyAssistanceRequestActivity.class);
        i.putExtra("assistanceRequest",assistanceRequest.toJson());
        startActivity(i);
    }
    private void startDepartureSearch ()
    {
        Intent i = new Intent(this,LocationSearchActivity.class);
        i.putExtra("assistanceRequest",assistanceRequest.toJson());
        i.putExtra("departure",true);
        startActivity(i);
    }

    private void startDestinationSearch ()
    {
        Intent i = new Intent(this,LocationSearchActivity.class);

        i.putExtra("assistanceRequest",assistanceRequest.toJson());
        i.putExtra("departure",false);
        startActivity(i);
    }



}
