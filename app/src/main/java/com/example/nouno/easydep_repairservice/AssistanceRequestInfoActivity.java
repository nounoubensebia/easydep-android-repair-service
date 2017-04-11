package com.example.nouno.easydep_repairservice;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.nouno.easydep_repairservice.Data.AssistanceRequest;
import com.google.gson.Gson;

//TODO add map and listeners

public class AssistanceRequestInfoActivity extends AppCompatActivity {

    private AssistanceRequest assistanceRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistance_request_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        retreiveData();
        hideTitleText();
        displayData();
    }

    private void retreiveData ()
    {
        Bundle extras = getIntent().getExtras();
        Gson gson = new Gson();
        assistanceRequest = gson.fromJson(extras.getString("assistanceRequest"),AssistanceRequest.class);
    }

    @SuppressLint("NewApi")
    private void displayData ()
    {
        TextView nameText = (TextView)findViewById(R.id.nameText);
        TextView toolbarDurationText = (TextView)findViewById(R.id.toolbar_duration_text);
        TextView toolbarDistanceText = (TextView)findViewById(R.id.toolbar_distanceText);
        nameText.setText(assistanceRequest.getCarOwner().getFullName());
        toolbarDurationText.setText(assistanceRequest.getDepartureDurationString());
        toolbarDistanceText.setText(assistanceRequest.getDepartureDistanceString());
        TextView departureText = (TextView)findViewById(R.id.departure_text1);
        departureText.setText(assistanceRequest.getDeparture().getLocationName());
        TextView durationText = (TextView)findViewById(R.id.duration_text);
        durationText.setText(assistanceRequest.getDepartureDurationString());
        TextView typeText = (TextView)findViewById(R.id.type_text);
        Drawable drawable;
        if (assistanceRequest.isHeavy())
        {
            typeText.setText("Véhicule lourd");
            drawable= getDrawable(R.drawable.ic_airport_heavy_24dp);
            typeText.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
            View heavyLayout = findViewById(R.id.heavy_layout);
            heavyLayout.setVisibility(View.VISIBLE);
            TextView lengthText = (TextView)findViewById(R.id.length_text);
            lengthText.setText("Longeur "+assistanceRequest.getLengthString());
            TextView weightText = (TextView)findViewById(R.id.weight_text);
            weightText.setText("Poids "+assistanceRequest.getWeightString());
        }
        else
        {
            typeText.setText("Véhicule léger");
        }
        TextView vehiculeCanMoveText = (TextView)findViewById(R.id.vehicule_can_move_text);
        if (!assistanceRequest.isVehiculeCanMove())
        {
            drawable=getDrawable(R.drawable.ic_build_red_24dp);
            vehiculeCanMoveText.setText("Ne démarre pas");
            vehiculeCanMoveText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            vehiculeCanMoveText.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
        }
        if (assistanceRequest.getDestination()!=null)
        {
        TextView departureText2 = (TextView)findViewById(R.id.departure_text2);
        departureText2.setText(assistanceRequest.getDeparture().getLocationName());
        TextView pathText = (TextView)findViewById(R.id.path_text);
        pathText.setText(assistanceRequest.getDepartureToDestinationString());
        TextView destinationText = (TextView)findViewById(R.id.destination_text);
        destinationText.setText(assistanceRequest.getDestination().getLocationName());
        }
        else
        {
            View pathLayout = findViewById(R.id.path_layout);
            pathLayout.setVisibility(View.GONE);
        }
        TextView writeEstimateText = (TextView)findViewById(R.id.write_estimate_text);
        TextView cancelRequestText = (TextView)findViewById(R.id.cancel_request_text);
        if (assistanceRequest.getFlag()==AssistanceRequest.FLAG_IN_QUEUE)
        {
            writeEstimateText.setVisibility(View.GONE);
        }
        if (assistanceRequest.getFlag()==AssistanceRequest.FLAG_INTERVENTION)
        {
            //writeEstimateText.setVisibility(View.GONE);
            writeEstimateText.setText("Terminer intervention");
            drawable = getDrawable(R.drawable.ic_intervention_finished_40dp);
            writeEstimateText.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
            cancelRequestText.setText("Annuler intervention");
        }

    }

    public void hideTitleText()
    {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        collapsingToolbarLayout.setTitle(" ");
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(assistanceRequest.getCarOwner().getFullName());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
}
