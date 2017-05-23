package com.example.nouno.easydep_repairservice.Fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nouno.easydep_repairservice.Activities.LocationSearchActivity;
import com.example.nouno.easydep_repairservice.Activities.LoginActivity;
import com.example.nouno.easydep_repairservice.Data.Position;
import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.Data.Tokens;
import com.example.nouno.easydep_repairservice.DialogUtils;
import com.example.nouno.easydep_repairservice.QueryUtils;
import com.example.nouno.easydep_repairservice.R;
import com.example.nouno.easydep_repairservice.Utils;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    private RepairService repairService;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private View infoLayout;
    private View passwordLayout;
    public static boolean loadedInfo = false;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    private void getRepairService() {
        repairService = Utils.getLoggedRepairService(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        getRepairService();
        //displayData(view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        infoLayout = view.findViewById(R.id.info_layout);
        passwordLayout = view.findViewById(R.id.password_layout);
        passwordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = buildPasswordDialog();
                dialog.show();
            }
        });
        if (loadedInfo != true)
            getInfo();
        else
            displayData(view);

        return view;
    }

    private void displayData(View view) {
        //getRepairService();
        TextView nameText = (TextView) view.findViewById(R.id.name_text);
        TextView phoneText = (TextView) view.findViewById(R.id.phone_text);
        TextView priceText = (TextView) view.findViewById(R.id.price_text);
        TextView statusText = (TextView) view.findViewById(R.id.status_text);
        TextView positionText = (TextView) view.findViewById(R.id.position_text);
        phoneText.setText(repairService.getPhoneNumber());
        priceText.setText(repairService.getPriceString());
        statusText.setText(repairService.getAvailableString());
        String s = "";
        if (repairService.isAutomaticLocationDetection())
            s = "(Détection automatique)";
        else
            s = "";
        positionText.setText(repairService.getPosition().getLocationName() + " " + s);
        View positionsTexts = view.findViewById(R.id.positions_text);
        positionsTexts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LocationSearchActivity.class);
                i.putExtra("repairService", repairService.toJson());
                startActivity(i);
                getActivity().finish();
            }
        });
        nameText.setText(repairService.getFullName());
        View logoutLayout = view.findViewById(R.id.logout_layout);
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void logoutUser() {
        String token = FirebaseInstanceId.getInstance().getToken();
        String email = repairService.getEmail();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("token", token);
        map.put("email", email);
        map.put("action", "logout");
        LogoutTask logoutTask = new LogoutTask();
        logoutTask.execute(map);
    }

    private class LogoutTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog) DialogUtils.buildProgressDialog("Veuillez patientez", getActivity());
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.ACCOUNT_URL, params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            startLoginActivity();

        }
    }

    private void startLoginActivity() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("repairService");
        editor.commit();
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
    }

    public void getInfo() {
        loadedInfo = true;
        getRepairService();
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("action", "get_account_info");
        linkedHashMap.put("repair_service_id", repairService.getId() + "");
        GetInfoTask getInfoTask = new GetInfoTask();
        getInfoTask.execute(linkedHashMap);
    }

    private class GetInfoTask extends AsyncTask<Map<String, String>, Void, String> {
        @Override
        protected void onPreExecute() {
            getRepairService();
            progressBar.setVisibility(View.VISIBLE);
            infoLayout.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.ACCOUNT_URL, params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            Tokens tokens = repairService.getTokens();
            boolean bool = repairService.isAutomaticLocationDetection();
            repairService = RepairService.parseJson(s);
            repairService.setTokens(tokens);
            repairService.setAutomaticLocationDetection(bool);
            displayData(getView());
            progressBar.setVisibility(View.GONE);
            infoLayout.setVisibility(View.VISIBLE);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("repairService", repairService.toJson());
            editor.commit();
        }
    }

    private Dialog buildPasswordDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.change_password_dialog, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String oldPassword = ((EditText) view.findViewById(R.id.old_password)).getText().toString();
                        String newPassword = ((EditText) view.findViewById(R.id.new_password)).getText().toString();
                        changePassword(oldPassword,newPassword);
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

    private void changePassword (String oldPassword,String newPassword)
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("old_password",oldPassword);
        map.put("new_password",newPassword);
        map.put("repair_service_id",repairService.getId()+"");
        map.put("action","change_password");
        ChangePasswordTask changePasswordTask = new ChangePasswordTask();
        changePasswordTask.execute(map);
    }

    private class ChangePasswordTask extends AsyncTask<Map<String, String>, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog) DialogUtils.buildProgressDialog("Veuillez patienter", getActivity());
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.ACCOUNT_URL, params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s.equals("failed")) {
                Dialog dialog = DialogUtils.buildInfoDialog("Erreur", "Mot de passe actuel incorrect", getActivity());
                dialog.show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String refreshToken = jsonObject.getString("refresh_token");
                    String accessToken = jsonObject.getString("access_token");
                    Tokens tokens = new Tokens(accessToken, refreshToken);
                    repairService.setTokens(tokens);
                    Utils.saveRepairService(getActivity(), repairService);
                    Dialog dialog = DialogUtils.buildInfoDialog("Mot de passe changé", "Votre mot de passe a été mis a jour", getActivity());
                    dialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Dialog buildPriceDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_change_price, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO remplir

                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

}
