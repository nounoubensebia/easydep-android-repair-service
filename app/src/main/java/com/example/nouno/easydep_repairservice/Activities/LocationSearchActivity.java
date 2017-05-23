package com.example.nouno.easydep_repairservice.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nouno.easydep_repairservice.Data.AssistanceRequest;
import com.example.nouno.easydep_repairservice.Data.Position;
import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.Data.SearchSuggestion;
import com.example.nouno.easydep_repairservice.ListAdapters.SearchSuggestionAdapter;
import com.example.nouno.easydep_repairservice.QueryUtils;
import com.example.nouno.easydep_repairservice.R;

import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class LocationSearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private ListView listView;
    private AssistanceRequest detailedAssistanceRequest;
    private RepairService repairService;
    private View userPositionLayout;
    private boolean departure;
    private Position userPosition;
    private TextView userLocationText;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private boolean isUserPosition;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);
        userPositionLayout = findViewById(R.id.user_position_layout);
        userLocationText = (TextView) findViewById(R.id.user_position_text);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.list);
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        retreiveData();
        if (repairService != null) {
            userPositionLayout.setVisibility(View.VISIBLE);
        }
    }

    private void retreiveData() {
        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        if (bundle.containsKey("assistanceRequest")) {
            detailedAssistanceRequest = gson.fromJson(bundle.getString("assistanceRequest"), AssistanceRequest.class);
            departure = bundle.getBoolean("departure");
        } else {
            if (bundle.containsKey("repairService")) {
                repairService = gson.fromJson(bundle.getString("repairService"), RepairService.class);
            }
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(
                    this, // Activity
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
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
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        userPosition = new Position(latitude, longitude, null);
        GetUserPositionTask getUserPositionTask = new GetUserPositionTask();
        getUserPositionTask.execute(userPosition);
        googleApiClient.disconnect();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    googleApiClient.connect();


                } else {


                }
                return;
            }
        }
    }

    public class GetUserPositionTask extends AsyncTask<Position, Void, Position> {

        @Override
        protected Position doInBackground(Position... params) {
            LinkedHashMap map = new LinkedHashMap();
            Position position = null;
            map.put("latlng", params[0].getLatitude() + "," + params[0].getLongitude());
            String response = QueryUtils.makeHttpGetRequest(QueryUtils.GET_USER_LOCATION_NAME_URL, map);
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray resultes = jsonObject.getJSONArray("results");
                JSONObject result = resultes.getJSONObject(0);
                String loc = result.getString("formatted_address");
                String[] tab = loc.split(", Algérie");
                String location = tab[0];
                position = params[0];
                position.setLocationName(location);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return position;

        }

        @Override
        protected void onPostExecute(final Position position) {
            userPosition = position;
            userLocationText.setText(position.getLocationName());
            userPositionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isUserPosition = true;
                    startAskingActivity(position);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location_search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_searche);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setFocusable(true);

        if (repairService != null)
            searchView.setQueryHint("Lieu de recherche");
        else
            searchView.setQueryHint("Indiquez votre position actuelle");
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 1) {
                    SuggestionsTask suggestionsTask = new SuggestionsTask();
                    suggestionsTask.execute(newText);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public class SuggestionsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap = QueryUtils.buildSearchSuggestionsParamsMap(params[0]);
            String response = QueryUtils.makeHttpGetRequest(QueryUtils.GET_PLACE_PREDICTIONS_URL, linkedHashMap);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            final ArrayList<SearchSuggestion> arrayList = SearchSuggestion.parseJson(s);
            SearchSuggestionAdapter searchSuggestionAdapter = new SearchSuggestionAdapter(getApplicationContext(), arrayList);
            listView.setDividerHeight(0);
            listView.setAdapter(searchSuggestionAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GetPositionTask getPositionTask = new GetPositionTask();
                    getPositionTask.execute(arrayList.get(position));
                }
            });
        }
    }

    public class GetPositionTask extends AsyncTask<SearchSuggestion, Void, Position> {

        @Override
        protected Position doInBackground(SearchSuggestion... params) {
            return (params[0].getPosition());
        }

        @Override
        protected void onPostExecute(Position position) {
            isUserPosition = false;
            startAskingActivity(position);
        }
    }



    private void startAskingActivity(Position position) {
        if (detailedAssistanceRequest != null) {
            if (departure)
                detailedAssistanceRequest.setUserPositon(position);
            else
                detailedAssistanceRequest.setDestination(position);
            Intent i = new Intent(this, CreateAssistanceRequestActivity.class);
            i.putExtra("assistanceRequest", detailedAssistanceRequest.toJson());
            startActivity(i);
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras.containsKey("repairService")) {
                repairService.setPosition(position);
                if (isUserPosition == true) {

                    repairService.setAutomaticLocationDetection(true);
                } else {
                    repairService.setAutomaticLocationDetection(false);
                }
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("repairService",repairService.toJson());
                editor.apply();
                if (extras.containsKey("password")) {
                    Intent i = new Intent(this, Signup4Activity.class);
                    i.putExtra("repairService", repairService.toJson());
                    i.putExtra("password", extras.getString("password"));
                    startActivity(i);
                }
                updatePosition();
            }


        }
    }

    private void updatePosition() {

        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("action","update_location");
        map.put("repair_service_id",repairService.getId()+"");
        map.put("longitude",repairService.getPosition().getLongitude()+"");
        map.put("latitude",repairService.getPosition().getLatitude()+"");

        UpdatePositionTask updateTask = new UpdatePositionTask();
        updateTask.execute(map);
    }

    private class UpdatePositionTask extends AsyncTask<Map<String,String>,Void,Void>
    {


        @Override
        protected Void doInBackground(Map<String, String>... params) {
            String response=null;
            try {
                response= QueryUtils.makeHttpPostRequest(QueryUtils.ACCOUNT_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Bundle extras = getIntent().getExtras();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("repairService",repairService.toJson());
            editor.apply();
            if (!extras.containsKey("password"))
            {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.putExtra("myAccount","");
                startActivity(i);
                finish();
            }


        }
    }
}
