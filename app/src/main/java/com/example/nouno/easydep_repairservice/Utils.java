package com.example.nouno.easydep_repairservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.nouno.easydep_repairservice.Data.Person;
import com.example.nouno.easydep_repairservice.Data.Position;
import com.example.nouno.easydep_repairservice.Data.RepairService;

/**
 * Created by nouno on 17/04/2017.
 */

public class Utils {

    public static RepairService getLoggedRepairService(Context context)
    {
        RepairService repairService = new RepairService(2,"haha","dob");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        repairService = RepairService.fromJson(sharedPref.getString("repairService",repairService.toJson()));
        //repairService.setPosition(new Position(36.56,3.456,"Cité Zerhouni Mokhtar"));
        return repairService;
    }


}
