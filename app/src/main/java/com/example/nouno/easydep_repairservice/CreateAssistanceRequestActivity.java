package com.example.nouno.easydep_repairservice;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nouno.easydep_repairservice.Data.AssistanceRequest;
import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateAssistanceRequestActivity extends AppCompatActivity {
    private AssistanceRequest assistanceRequest;
    private CreateAssistanceRequestActivity createAssistanceRequestActivity;
    private RepairService repairService;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAssistanceRequestActivity = this;
        setContentView(R.layout.activity_manual_assistance_request);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        retrieveData();
        displayData();
    }

    private void retrieveData()
    {
        Gson gson = new Gson();
        if (getIntent().getExtras()!=null&&getIntent().getExtras().containsKey("assistanceRequest"))
        {
        String json = getIntent().getExtras().getString("assistanceRequest");
        assistanceRequest = gson.fromJson(json,AssistanceRequest.class);
        }
        else assistanceRequest = new AssistanceRequest();
        repairService = new RepairService(7,"Bensebia","Noureddine");

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
                    assistanceRequest.setLength(AssistanceRequest.DONT_KNOW);
                    assistanceRequest.setWeight(AssistanceRequest.DONT_KNOW);
                    startHeavyRequestActivity();
                }
                else {
                    assistanceRequest.setHeavy(false);
                    assistanceRequest.setLength(AssistanceRequest.NOT_HEAVY);
                    assistanceRequest.setWeight(AssistanceRequest.NOT_HEAVY);
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
                addNewQueueElement();
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

    private void addNewQueueElement ()
    {
        try {
            JSONObject jsonObject = new JSONObject(assistanceRequest.toJson());
            //jsonObject.put("repairService",repairService.toJson());
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id",repairService.getId());
            jsonObject.put("repairService",jsonObject1);
            AddNewElementTask addNewElementTask = new AddNewElementTask();
            addNewElementTask.execute(jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddNewElementTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            View fab = findViewById(R.id.go_button);
            fab.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostJsonRequest(QueryUtils.SEND_REQUEST_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(),"Automobiliste ajouté",Toast.LENGTH_LONG).show();
            startMainActivity();
        }
    }

    private void startMainActivity ()
    {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
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
