package com.example.nouno.easydep_repairservice.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.nouno.easydep_repairservice.Activities.LocationSearchActivity;
import com.example.nouno.easydep_repairservice.Data.Position;
import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.QueryUtils;
import com.example.nouno.easydep_repairservice.Utils;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.LinkedHashMap;
import java.util.Map;

public class LocationUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private RepairService repairService;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Position locatedPosition;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.i("Service","Launched");
        repairService = Utils.getLoggedRepairService(this);
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setInterval(3000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            googleApiClient.disconnect();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Double latitude =location.getLatitude();
        Double longitude = location.getLongitude();
        Log.i("LocInfo","changed "+location.getLongitude()+"  "+location.getLatitude());
        if (repairService.isAutomaticLocationDetection())
        {
            locatedPosition= new Position(latitude,longitude,null);
            updateRepairServiceLocation();
        }
        else
        {
            stopSelf();
        }
        //googleApiClient.disconnect();
    }

    private void updateRepairServiceLocation ()
    {
        repairService = Utils.getLoggedRepairService(getApplicationContext());
        if (repairService.isAutomaticLocationDetection())
        {
            LinkedHashMap<String,String> map = new LinkedHashMap<>();
            map.put("action","update_location");
            map.put("repair_service_id",repairService.getId()+"");
            map.put("longitude",locatedPosition.getLongitude()+"");
            map.put("latitude",locatedPosition.getLatitude()+"");
            UpdateTask updateTask = new UpdateTask();
            updateTask.execute(map);
            //repairService.setPosition(new Position(locatedPosition.getLatitude(),locatedPosition.getLongitude(),null));
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("repairService",repairService.toJson());
            editor.commit();
        }
            else stopSelf();

    }

    private class UpdateTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected String doInBackground(Map<String,String>... params) {
            try {
                String response = QueryUtils.makeHttpPostRequest(QueryUtils.ACCOUNT_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
