package com.example.nouno.easydep_repairservice.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nouno.easydep_repairservice.Activities.LoginActivity;
import com.example.nouno.easydep_repairservice.Data.Position;
import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.DialogUtils;
import com.example.nouno.easydep_repairservice.QueryUtils;
import com.example.nouno.easydep_repairservice.R;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    private RepairService repairService;
    private ProgressDialog progressDialog;

    public MyAccountFragment() {
        // Required empty public constructor
    }
    private void getRepairService ()
    {
        repairService = new RepairService(7,"Zerhouni","Mokhtar","05489789",70,repairService.AVAILABLE,new Position(36.56,3.456,"Cité Zerhouni Mokhtar"));
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        repairService = RepairService.fromJson(sharedPref.getString("repairService",repairService.toJson()));
        repairService.setPosition(new Position(36.56,3.456,"Cité Zerhouni Mokhtar"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        getRepairService();
        displayData(view);
        return view;
    }

    private void displayData (View view)
    {
        TextView nameText = (TextView)view.findViewById(R.id.name_text);
        TextView phoneText = (TextView)view.findViewById(R.id.phone_text);
        TextView priceText = (TextView)view.findViewById(R.id.price_text);
        TextView statusText = (TextView)view.findViewById(R.id.status_text);
        TextView positionText = (TextView)view.findViewById(R.id.position_text);
        phoneText.setText(repairService.getPhoneNumber());
        priceText.setText(repairService.getPriceString());
        statusText.setText(repairService.getAvailableString());
        positionText.setText(repairService.getPosition().getLocationName());
        nameText.setText(repairService.getFullName());
        View logoutLayout = view.findViewById(R.id.logout_layout);
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void logoutUser ()
    {
        String token = FirebaseInstanceId.getInstance().getToken();
        String email = repairService.getEmail();
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("token",token);
        map.put("email",email);
        map.put("action","logout");
        LogoutTask logoutTask = new LogoutTask();
        logoutTask.execute(map);
    }

    private class LogoutTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog) DialogUtils.buildProgressDialog("Veuillez patientez",getActivity());
            progressDialog.show();
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
            progressDialog.dismiss();
            startLoginActivity();

        }
    }
    private void startLoginActivity ()
    {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("repairService");
        editor.commit();
        Intent i = new Intent(getActivity(),LoginActivity.class);
        startActivity(i);
    }

}
