package com.example.nouno.easydep_repairservice.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.QueryUtils;
import com.example.nouno.easydep_repairservice.R;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

public class Signup3Activity extends AppCompatActivity {
    TextInputLayout passwordWrapper;
    private RepairService repairService;
    View fab;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);
        passwordWrapper = (TextInputLayout)findViewById(R.id.passwordWrapper);
        fab = findViewById(R.id.fab);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        getRepairService();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordWrapper.getEditText().getText().toString();
                signUpUser(password);
            }
        });
    }

    private void getRepairService ()
    {
        repairService = RepairService.fromJson(getIntent().getExtras().getString("repairService"));
    }

    private void signUpUser (String password)
    {
        String email = repairService.getEmail();
        String firstname = repairService.getFirstname();
        String lastname = repairService.getLastname();
        String phoneNumber = repairService.getPhoneNumber();
        String token = FirebaseInstanceId.getInstance().getToken();
        LinkedHashMap<String,String>map = new LinkedHashMap<>();
        map.put("email",email);
        map.put("firstname",firstname);
        map.put("lastname",lastname);
        map.put("phonenumber",phoneNumber);
        map.put("password",password);
        map.put("token",token);
        map.put("action",QueryUtils.SIGNUP_ACTION);
        SignUpTask signUpTask = new SignUpTask();
        signUpTask.execute(map);
    }

    private class SignUpTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
           progressBar.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.ACCOUNT_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            startSignup4Activity();
        }
    }

    private void startSignup4Activity()
    {
        Intent i = new Intent(this,Signup4Activity.class);
        startActivity(i);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("repairService",repairService.toJson());
        editor.commit();
    }
}
