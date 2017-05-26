package com.example.nouno.easydep_repairservice.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.ListAdapters.WilayaSpinnerAdapter;
import com.example.nouno.easydep_repairservice.QueryUtils;
import com.example.nouno.easydep_repairservice.R;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;
import com.google.gson.Gson;

import java.util.LinkedHashMap;

public class Signup2Activity extends AppCompatActivity {
    TextInputLayout emailWrapper;
    TextInputLayout phoneNumberWrapper;
    View progressBar;
    View fab;
    Spinner wilayaSpinner;
    private RepairService repairService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        emailWrapper = (TextInputLayout)findViewById(R.id.eamilWrapper);
        phoneNumberWrapper = (TextInputLayout)findViewById(R.id.phoneNumberWrapper);
        progressBar = findViewById(R.id.progressBar);
        wilayaSpinner = (Spinner)findViewById(R.id.spinner);
        String [] arr = getResources().getStringArray(R.array.wilaya);
        WilayaSpinnerAdapter wilayaSpinnerAdapter = new WilayaSpinnerAdapter(this,arr);
        wilayaSpinner.setAdapter(wilayaSpinnerAdapter);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailWrapper.getEditText().getText().toString();
                String phoneNumber = phoneNumberWrapper.getEditText().getText().toString();
                if (QueryUtils.validateEmail(email)&&QueryUtils.validatePhoneNumber(phoneNumber))
                {
                    CheckExistanceTask checkExistanceTask = new CheckExistanceTask();
                    LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();


                    map.put("email",email);
                    map.put("phonenumber",phoneNumber);
                    map.put("action","verify_existance");
                    checkExistanceTask.execute(map);
                }
                else
                {
                    if (!QueryUtils.validatePhoneNumber(phoneNumber))
                    {
                        phoneNumberWrapper.getEditText().setError("Erreur veuillez entrer un numéro valide");
                    }
                    if (!QueryUtils.validateEmail(email))
                    {
                        emailWrapper.getEditText().setError("Erreur veuillez entrer un email valide");
                    }
                }
                //TODO verify email and phonenumber existance
            }
        });
    }

    private class CheckExistanceTask extends AsyncTask<LinkedHashMap<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {
           progressBar.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.ACCOUNT_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
                return QueryUtils.CONNECTION_PROBLEM;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            if (!s.equals(QueryUtils.CONNECTION_PROBLEM)) {
                if (s.equals("1")) {
                    emailWrapper.getEditText().setError(getString(R.string.email_already_exists));
                    phoneNumberWrapper.getEditText().setError(getString(R.string.phone_number_already_exists));
                }
                if (s.equals("2")) {
                    phoneNumberWrapper.getEditText().setError(getString(R.string.phone_number_already_exists));
                }
                if (s.equals("3")) {
                    emailWrapper.getEditText().setError(getString(R.string.email_already_exists));
                }
                if (s.equals("0")) {
                    String email = emailWrapper.getEditText().getText().toString();
                    String phoneNumber = phoneNumberWrapper.getEditText().getText().toString();
                    repairService = RepairService.fromJson(getIntent().getExtras().getString("repairService"));
                    repairService.setPhoneNumber(phoneNumber);
                    repairService.setEmail(email);
                    String wilaya = wilayaSpinner.getSelectedItem().toString();
                    repairService.setWilaya(wilaya);
                    String json = repairService.toJson();
                    Intent i = new Intent(getApplicationContext(), Signup3Activity.class);
                    i.putExtra("repairService", json);
                    startActivity(i);
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), R.string.connection_failed,Toast.LENGTH_LONG).show();
            }
        }
    }
}
