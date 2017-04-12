package com.example.nouno.easydep_repairservice;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**565
 * Created by nouno on 07/04/2017.
 */

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
 public FragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new RequestsListFragment();
        } else {
            return new QueueFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position==0)
        {
            return "Mes demandes";
        }
        else
        {
            return "Ma file d'attente";
        }
    }
}

