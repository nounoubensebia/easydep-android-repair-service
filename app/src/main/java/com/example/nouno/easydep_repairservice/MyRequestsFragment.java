package com.example.nouno.easydep_repairservice;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyRequestsFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    FragmentPagerAdapter fragmentPagerAdapter;
    public MyRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_requests, container, false);
        tabLayout = (TabLayout)rootView.findViewById(R.id.sliding_tabs);

        viewPager = (ViewPager)rootView.findViewById(R.id.viewpager);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return rootView;
    }



    @Override
    public void onStop() {
        //getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        super.onStop();

    }
}
