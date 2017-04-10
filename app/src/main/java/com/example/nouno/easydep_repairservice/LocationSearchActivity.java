package com.example.nouno.easydep_repairservice;

import android.content.Intent;
import android.os.AsyncTask;
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

import com.example.nouno.easydep_repairservice.Data.AssistanceRequest;
import com.example.nouno.easydep_repairservice.Data.DetailedAssistanceRequest;
import com.example.nouno.easydep_repairservice.Data.Position;
import com.example.nouno.easydep_repairservice.Data.SearchSuggestion;
import com.example.nouno.easydep_repairservice.ListAdapters.SearchSuggestionAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class LocationSearchActivity extends AppCompatActivity {
    private ListView listView;
    private DetailedAssistanceRequest detailedAssistanceRequest;
    private boolean departure;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView)findViewById(R.id.list);
        retreiveData();
    }

    private void retreiveData ()
    {
        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        detailedAssistanceRequest = gson.fromJson(bundle.getString("assistanceRequest"),DetailedAssistanceRequest.class);
        departure = bundle.getBoolean("departure");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location_search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_searche);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setFocusable(true);
        searchView.setQueryHint("Lieu de recherche");
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length()>1)
                {
                    SuggestionsTask suggestionsTask = new SuggestionsTask();
                    suggestionsTask.execute(newText);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public class SuggestionsTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap = QueryUtils.buildSearchSuggestionsParamsMap(params[0]);
            String response = QueryUtils.makeHttpGetRequest(QueryUtils.GET_PLACE_PREDICTIONS_URL,linkedHashMap);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            final ArrayList<SearchSuggestion> arrayList = SearchSuggestion.parseJson(s);
            SearchSuggestionAdapter searchSuggestionAdapter = new SearchSuggestionAdapter(getApplicationContext(),arrayList);
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

    public class GetPositionTask extends AsyncTask<SearchSuggestion,Void,Position> {

        @Override
        protected Position doInBackground(SearchSuggestion... params) {
            return (params[0].getPosition());
        }

        @Override
        protected void onPostExecute(Position position) {
            startAskingActivity(position);
        }
    }
    private void startAskingActivity(Position position)
    {
        if (departure)
            detailedAssistanceRequest.setDeparture(position);
        else
            detailedAssistanceRequest.setDestination(position);
        Intent i = new Intent(this, CreateAssistanceRequestActivity.class);
        i.putExtra("assistanceRequest",detailedAssistanceRequest.toJson());
        startActivity(i);
    }
}
