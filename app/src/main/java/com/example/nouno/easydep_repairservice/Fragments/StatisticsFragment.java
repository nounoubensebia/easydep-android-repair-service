package com.example.nouno.easydep_repairservice.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nouno.easydep_repairservice.Activities.MainActivity;
import com.example.nouno.easydep_repairservice.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {
    MainActivity mainActivity;

    public StatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();
        //mainActivity.getSupportActionBar().show();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onStop() {

        //mainActivity.getSupportActionBar().hide();
        super.onStop();
    }
}
