package com.example.komaki.a7segosr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by komaki on 17/06/21.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HowtoFragment0();
            default:
                return new HowtoFragment1();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
