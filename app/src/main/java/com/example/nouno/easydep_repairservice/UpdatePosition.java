package com.example.nouno.easydep_repairservice;

import android.os.AsyncTask;

import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.Services.LocationUpdateService;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nouno on 05/05/2017.
 */

public class UpdatePosition {
    private Double latitude;
    private Double longitude;
    private RepairService repairService;

    public UpdatePosition(RepairService repairService,Double latitude,Double longitude) {
        this.repairService = repairService;
        this.latitude=latitude;
        this.longitude=longitude;
    }


    public void updatePosition()
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("action","update_location");
        map.put("repair_service_id",repairService.getId()+"");
        map.put("longitude",repairService.getPosition().getLongitude()+"");
        map.put("latitude",repairService.getPosition().getLatitude()+"");
        UpdateTask updateTask = new UpdateTask();
        updateTask.execute(map);
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
