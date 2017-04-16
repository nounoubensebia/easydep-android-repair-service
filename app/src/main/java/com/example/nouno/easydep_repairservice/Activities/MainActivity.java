package com.example.nouno.easydep_repairservice.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.nouno.easydep_repairservice.Fragments.MyAccountFragment;
import com.example.nouno.easydep_repairservice.Fragments.MyRequestsFragment;
import com.example.nouno.easydep_repairservice.R;
import com.example.nouno.easydep_repairservice.Fragments.StatisticsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {


    MyAccountFragment myAccountFragment;
    MyRequestsFragment myRequestsFragment;
    StatisticsFragment statisticsFragment;
    View content;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //getSupportActionBar().setShowHideAnimationEnabled(false);
            switch (item.getItemId()) {
                case R.id.navigation_my_account:
                    content.setVisibility(View.VISIBLE);
                    getSupportActionBar().setTitle("Mon compte");
                    //getSupportActionBar().hide();
                    myAccountFragment = new MyAccountFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, myAccountFragment).commit();
                    getSupportActionBar().setElevation(12);
                    if (myRequestsFragment != null)
                        getSupportFragmentManager().beginTransaction().remove(myRequestsFragment).commitAllowingStateLoss();
                    if (statisticsFragment != null)
                        getSupportFragmentManager().beginTransaction().remove(statisticsFragment).commitAllowingStateLoss();
                    return true;
                case R.id.navigation_lists:
                    getSupportActionBar().setElevation(0);
                    //getSupportActionBar().show();
                    getSupportActionBar().setTitle("Mes demandes");
                    myRequestsFragment = new MyRequestsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, myRequestsFragment).commit();
                    if (myAccountFragment != null)
                        getSupportFragmentManager().beginTransaction().remove(myAccountFragment).commitAllowingStateLoss();
                    if (statisticsFragment != null)
                        getSupportFragmentManager().beginTransaction().remove(statisticsFragment).commitAllowingStateLoss();
                    return true;
                case R.id.navigation_statistics:
                    getSupportActionBar().setTitle("Mes statistiques");
                    getSupportActionBar().setElevation(12);
                    getSupportActionBar().show();
                    content.setVisibility(View.VISIBLE);
                    statisticsFragment = new StatisticsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, statisticsFragment).commit();
                    if (myAccountFragment != null)
                        getSupportFragmentManager().beginTransaction().remove(myAccountFragment).commitAllowingStateLoss();
                    if (myRequestsFragment != null)
                        getSupportFragmentManager().beginTransaction().remove(myRequestsFragment).commitAllowingStateLoss();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        content = findViewById(R.id.content);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(1).setChecked(true);
        Bundle extras = getIntent().getExtras();

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setShowHideAnimationEnabled(true);
        getSupportActionBar().setTitle("Mes demandes");

        myRequestsFragment = new MyRequestsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, myRequestsFragment).commit();
        if (myAccountFragment != null)
            getSupportFragmentManager().beginTransaction().remove(myAccountFragment).commitAllowingStateLoss();
        if (statisticsFragment != null)
            getSupportFragmentManager().beginTransaction().remove(statisticsFragment).commitAllowingStateLoss();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i("TOKEN",token);

    }

}
