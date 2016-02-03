package vn.hoangphan.karafind.utils;

import android.app.Activity;
import android.support.design.widget.TabLayout;

import vn.hoangphan.karafind.KaraokeActivity;
import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.adapters.PagerAdapter;
import vn.hoangphan.karafind.views.NonSwipeableViewPager;

/**
 * Created by Hoang Phan on 1/28/2016.
 */
public class PagerUtils {
    private static PagerUtils instance;

    public static void init(KaraokeActivity activity) {
        instance = new PagerUtils(activity);
    }

    public static PagerUtils getInstance() {
        return instance;
    }

    private PagerAdapter mAdapter;
    private TabLayout mTabLayout;
    private NonSwipeableViewPager mPager;

    private PagerUtils(KaraokeActivity activity) {
        mAdapter = new PagerAdapter(activity.getSupportFragmentManager());
        mTabLayout = (TabLayout)activity.findViewById(R.id.tab_layout);
        mPager = (NonSwipeableViewPager)activity.findViewById(R.id.viewPager);

        mPager.setAdapter(mAdapter);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.search).setIcon(R.drawable.ic_search));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.update).setIcon(R.drawable.ic_update));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.settings).setIcon(R.drawable.ic_settings));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.favorite).setIcon(R.drawable.ic_star));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changePage(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mPager.setOffscreenPageLimit(4);
    }

    public void changePage(int position) {
        mPager.setCurrentItem(position);
        mTabLayout.setScrollPosition(position, 0, true);
    }
}
