package com.example.tukitan.functiontestproject;

import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by tukitan on 17/06/17.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {
    public FragmentAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Fragment1();
            default:
                return new Fragment2();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
