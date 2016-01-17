package vn.hoangphan.karafind.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import vn.hoangphan.karafind.fragments.SearchFragment;
import vn.hoangphan.karafind.fragments.SettingsFragment;
import vn.hoangphan.karafind.fragments.UpdateFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    public static final int TAB_SEARCH = 0;
    public static final int TAB_UPDATE = 1;
    public static final int TAB_SETTINGS = 2;

    FragmentManager mManager;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        mManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_SEARCH:
                return new SearchFragment();
            case TAB_UPDATE:
                return new UpdateFragment();
            case TAB_SETTINGS:
                return new SettingsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}