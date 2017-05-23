package com.example.nouno.easydep_repairservice.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.nouno.easydep_repairservice.Data.AssistanceRequest;
import com.example.nouno.easydep_repairservice.DialogUtils;
import com.example.nouno.easydep_repairservice.QueryUtils;
import com.example.nouno.easydep_repairservice.R;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

//TODO add map and listeners

public class AssistanceRequestInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private AssistanceRequest assistanceRequest;
    private ProgressDialog progressDialog;
    private GoogleMap map;
    int flag;
    private AssistanceRequestInfoActivity assistanceRequestInfoActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        assistanceRequestInfoActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistance_request_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        retreiveData();
        hideTitleText();
        //displayData();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void retreiveData ()
    {

        Bundle extras = getIntent().getExtras();

        long id = extras.getLong("assistanceRequestId");
        flag = extras.getInt("flag");
        LinkedHashMap<String,String>map = new LinkedHashMap<>();
        map.put("assistance_request_id",id+"");
        map.put("action","get_repair_service_request");
        GetRequestData getRequestData = new GetRequestData();
        getRequestData.execute(map);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel((int)id);
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
        departureText.setText(assistanceRequest.getUserPositon().getLocationName());
        TextView durationText = (TextView)findViewById(R.id.duration_text);
        durationText.setText(assistanceRequest.getDepartureDurationString());
        TextView typeText = (TextView)findViewById(R.id.type_text);
        Drawable drawable;
        if (assistanceRequest.isHeavy())
        {
            typeText.setText("Poids lourd");
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
        departureText2.setText(assistanceRequest.getUserPositon().getLocationName());
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
        if (flag==AssistanceRequest.FLAG_IN_QUEUE)
        {
            writeEstimateText.setVisibility(View.GONE);
        }
        if (flag==AssistanceRequest.FLAG_INTERVENTION)
        {
            //writeEstimateText.setVisibility(View.GONE);
            writeEstimateText.setText("Terminer intervention");
            writeEstimateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    completeIntervention();
                }
            });
            drawable = getDrawable(R.drawable.ic_intervention_finished_40dp);
            writeEstimateText.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
            cancelRequestText.setText("Annuler intervention");
        }
        if (flag==AssistanceRequest.FLAG_ESTIMATE_REQUEST)
        {
            writeEstimateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSendEstimateActivity();
                }
            });
        }

        cancelRequestText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequest();
            }
        });

    }
    private void cancelRequest ()
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("action", QueryUtils.CANCEL_REQUEST_ACTION);
        map.put("assistance_request_id",assistanceRequest.getId()+"");
        CancelRequestTask cancelRequestTask = new CancelRequestTask();
        cancelRequestTask.execute(map);
    }

    private void startSendEstimateActivity()
    {
        String json = assistanceRequest.toJson();
        Intent intent = new Intent (this,EstimateActivity.class);
        intent.putExtra("assistanceRequest",json);
        startActivity(intent);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);


    }

    private void markUser()
    {
        double latitudecenter = assistanceRequest.getUserPositon().getLatitude();
        double longitudecenter = assistanceRequest.getUserPositon().getLongitude();
        LatLng centerauto=new LatLng(latitudecenter,longitudecenter);
        Marker marker1 = map.addMarker(new MarkerOptions().position(centerauto).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        map.moveCamera((CameraUpdateFactory.newLatLngZoom(centerauto,12)));

    }

    private void completeIntervention()
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("action","complete_intervention");
        map.put("assistance_request_id",assistanceRequest.getId()+"");
        CompleteIntervention completeIntervention = new CompleteIntervention();
        completeIntervention.execute(map);
    }

    private class CancelRequestTask extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog) DialogUtils.buildProgressDialog("Veuillez patientez",assistanceRequestInfoActivity);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.REQUESTS_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            startMainActivity();
            finish();
        }
    }



    private class GetRequestData extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog)DialogUtils.buildProgressDialog("Récupération des données",assistanceRequestInfoActivity);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
           String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.REQUESTS_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                assistanceRequest = AssistanceRequest.parseJson(jsonObject);
                displayData();
                markUser();
                progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class CompleteIntervention extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {

            progressDialog = (ProgressDialog)DialogUtils.buildProgressDialog("Veuillez patienter",assistanceRequestInfoActivity);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.REQUESTS_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
                Dialog dialog = DialogUtils.buildClickableInfoDialog("Intervention terminée", "", assistanceRequestInfoActivity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startMainActivity();
                    }
                });
            dialog.show();
        }


    }

    private void startMainActivity()
    {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }
}
