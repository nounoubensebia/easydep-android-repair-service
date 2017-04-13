package com.example.nouno.easydep_repairservice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //private TextView mTextMessage;
    //ViewPager viewPager;
    MyAccountFragment myAccountFragment;
    MyRequestsFragment myRequestsFragment;
    StatisticsFragment statisticsFragment;
    View content;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_my_account:
                    content.setVisibility(View.VISIBLE);
                    myAccountFragment = new MyAccountFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content,myAccountFragment).commit();
                    getSupportActionBar().setElevation(12);
                    if (myRequestsFragment!=null)
                    getSupportFragmentManager().beginTransaction().remove(myRequestsFragment).commitAllowingStateLoss();
                    if (statisticsFragment!=null)
                    getSupportFragmentManager().beginTransaction().remove(statisticsFragment).commitAllowingStateLoss();
                    return true;
                case R.id.navigation_lists:
                    getSupportActionBar().setElevation(0);
                    myRequestsFragment = new MyRequestsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, myRequestsFragment).commit();
                    if (myAccountFragment!=null)
                    getSupportFragmentManager().beginTransaction().remove(myAccountFragment).commitAllowingStateLoss();
                    if (statisticsFragment!=null)
                    getSupportFragmentManager().beginTransaction().remove(statisticsFragment).commitAllowingStateLoss();
                    return true;
                case R.id.navigation_statistics:
                    getSupportActionBar().setElevation(12);
                    content.setVisibility(View.VISIBLE);
                    statisticsFragment = new StatisticsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content,statisticsFragment).commit();
                    if (myAccountFragment!=null)
                    getSupportFragmentManager().beginTransaction().remove(myAccountFragment).commitAllowingStateLoss();
                    if (myRequestsFragment!=null)
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
    }

}
