package com.example.nouno.easydep_repairservice.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.QueryUtils;
import com.example.nouno.easydep_repairservice.R;

import com.example.nouno.easydep_repairservice.SnackBarUtils;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.LinkedHashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout emailWrapper;
    private TextInputLayout passwordWrapper;
    private LoginActivity loginActivity;
    private Button signInButton;
    private Button signUpButton;
    private ProgressBar progressBar;
    private RepairService repairService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        checkIfConnected();
        setContentView(R.layout.activity_login);
        emailWrapper = (TextInputLayout)findViewById(R.id.eamilWrapper);
        passwordWrapper = (TextInputLayout)findViewById(R.id.passwordWrapper);
        signInButton = (Button) findViewById(R.id.signin_button);
        signUpButton = (Button)findViewById(R.id.signup_button);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailWrapper = (TextInputLayout)findViewById(R.id.eamilWrapper);
                passwordWrapper = (TextInputLayout)findViewById(R.id.passwordWrapper);

                String email = emailWrapper.getEditText().getText().toString();
                String password = passwordWrapper.getEditText().getText().toString();
                emailWrapper.setErrorEnabled(false);
                passwordWrapper.setErrorEnabled(false);
                if (!QueryUtils.validateEmail(email)) {
                    emailWrapper.setErrorEnabled(true);
                    emailWrapper.setError("L'adresse e-mail n'est pas valide");
                }
                if (!QueryUtils.validatePassword(password)) {
                    passwordWrapper.setErrorEnabled(true);
                    passwordWrapper.setError("Le mot de passe n'est pas valide");
                }
                if (QueryUtils.validateEmail(email)&&QueryUtils.validatePassword(password))
                loginUser(email,password);
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Signup1Activity.class);
                startActivity(i);
            }
        });

    }

    private void checkIfConnected ()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.contains("repairService"))
        {
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
            finish();
        }
    }


    private void loginUser (String email,String password)
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("email",email);
        map.put("password",password);
        map.put("token", FirebaseInstanceId.getInstance().getToken());
        map.put("action","login");
        LoginTask loginTask = new LoginTask();
        loginTask.execute(map);
    }


    private class LoginTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            signInButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
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
            if (s.equals("failed"))
            {
                Snackbar snackbar = SnackBarUtils.makeSnackBar(loginActivity,signInButton);
                signInButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
            else
            {
                repairService = RepairService.parseJson(s);
                startApp();
            }
        }
    }

    private void startApp ()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("repairService",repairService.toJson());
        editor.commit();
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }
}
