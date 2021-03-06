package com.example.nouno.easydep_repairservice.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.Data.Tokens;
import com.example.nouno.easydep_repairservice.QueryUtils;
import com.example.nouno.easydep_repairservice.R;
import com.example.nouno.easydep_repairservice.Services.LocationUpdateService;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Signup4Activity extends AppCompatActivity {

    private RepairService repairService;

    private View signupUnderwayLayout;
    private View signupCompletedLayout;
    private View fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup4);
        signupUnderwayLayout = findViewById(R.id.signup_underway_layout);
        signupCompletedLayout = findViewById(R.id.signup_completed_layout);
        Animation fade = AnimationUtils.loadAnimation(this,R.anim.infinite_fade);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
        fade.setRepeatCount(Animation.INFINITE);
        signupUnderwayLayout.startAnimation(fade);
        getRepairService();
        signUpUser(getIntent().getExtras().getString("password"));

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
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("email",email);
        map.put("firstname",firstname);
        map.put("lastname",lastname);
        map.put("phonenumber",phoneNumber);
        map.put("password",password);
        map.put("token",token);
        map.put("action", QueryUtils.SIGNUP_ACTION);
        map.put("latitude",repairService.getPosition().getLatitude()+"");
        map.put("longitude",repairService.getPosition().getLongitude()+"");
        map.put("wilaya",repairService.getWilaya()+"");
        SignUpTask signUpTask = new SignUpTask();
        signUpTask.execute(map);
    }

    private class SignUpTask extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected String doInBackground(Map<String, String>... params) {
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
            if (!s.equals(QueryUtils.CONNECTION_PROBLEM))
            {
                exitEnterFadeAnimation(signupUnderwayLayout,signupCompletedLayout);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    long id = jsonObject.getLong("id");
                    String accessToken = jsonObject.getString("access_token");
                    String refreshToken = jsonObject.getString("refresh_token");
                    Tokens tokens = new Tokens(accessToken,refreshToken);
                    repairService.setTokens(tokens);
                    repairService.setId(id);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                TextView patientez = (TextView)findViewById(R.id.patientez);
                patientez.setText("Erreur connexion au serveur impossible");
                signupUnderwayLayout.clearAnimation();
            }



    }
    }

    private void startMainActivity()
    {
        Intent i = new Intent(this,MainActivity.class);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("repairService",repairService.toJson());
        editor.commit();
        startActivity(i);
        finish();
    }


    public void exitEnterFadeAnimation(final View exitView, final View enterView)
    {
        final Animation fabExit = AnimationUtils.loadAnimation(exitView.getContext(),R.anim.fade_out);
        final Animation fabEnter = AnimationUtils.loadAnimation(enterView.getContext(),R.anim.fade_in);
        //fabEnter.setStartTime(2000);
        fabExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                enterView.startAnimation(fabEnter);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fabEnter.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                enterView.setVisibility(View.VISIBLE);
                exitView.setVisibility(View.INVISIBLE);
                //enterView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                enterView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        exitView.startAnimation(fabExit);
    }

}
