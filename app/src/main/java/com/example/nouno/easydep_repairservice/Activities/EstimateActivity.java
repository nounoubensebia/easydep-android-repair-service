package com.example.nouno.easydep_repairservice.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nouno.easydep_repairservice.Data.AssistanceRequest;
import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.QueryUtils;
import com.example.nouno.easydep_repairservice.R;
import com.example.nouno.easydep_repairservice.Utils;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

public class EstimateActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private AssistanceRequest assistanceRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        retrieveData();
        handleUi();
    }

    private void retrieveData()
    {
        Gson gson = new Gson();
        String json = getIntent().getExtras().getString("assistanceRequest");
        assistanceRequest = gson.fromJson(json,AssistanceRequest.class);
    }

    private void handleUi ()
    {
        final TextInputLayout priceLayout = (TextInputLayout)findViewById(R.id.estimated_cost_wrapper);
        final TextInputLayout extraInfoLayout = (TextInputLayout)findViewById(R.id.extra_info_wrapper);
        final CheckBox dontKnowCost = (CheckBox)findViewById(R.id.dont_know_cost);
        dontKnowCost.setChecked(true);
        dontKnowCost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    priceLayout.getEditText().clearComposingText();
                }
            }
        });
        View fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price;
                if (dontKnowCost.isChecked())
                {
                    price = "-1";
                }
                else
                {
                    price = priceLayout.getEditText().getText().toString();
                }
                String comment = extraInfoLayout.getEditText().getText().toString();
                sendEstimate(price,comment);
            }
        });
        EditText priceText = priceLayout.getEditText();
        priceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty())
                {
                    dontKnowCost.setChecked(true);
                }
                else
                {
                    dontKnowCost.setChecked(false);
                }
            }
        });
    }

    private void sendEstimate (String price,String comment)
    {
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("price",price);
        linkedHashMap.put("comment",comment);
        linkedHashMap.put("assistance_request_id",assistanceRequest.getId()+"");
        linkedHashMap.put("action", QueryUtils.SEND_ESTIMATE_ACTION);
        SendEstimateTask sendEstimateTask = new SendEstimateTask();
        sendEstimateTask.execute(linkedHashMap);
    }

    private class SendEstimateTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            View fab = findViewById(R.id.fab);
            fab.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.REQUESTS_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
                return QueryUtils.CONNECTION_PROBLEM;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!s.equals(QueryUtils.CONNECTION_PROBLEM))
            {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Devis envoyé",Toast.LENGTH_LONG).show();
            RepairService repairService = Utils.getLoggedRepairService(getApplicationContext());
            repairService.setStatus(RepairService.AVAILABLE);
            Utils.saveRepairService(getApplicationContext(),repairService);
            startMainActivity();
            View fab = findViewById(R.id.fab);
            fab.setVisibility(View.GONE);}
            else
            {
                View fab = findViewById(R.id.fab);
                fab.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.connection_failed,Toast.LENGTH_LONG).show();

            }
        }
    }

    private void startMainActivity ()
    {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

}
