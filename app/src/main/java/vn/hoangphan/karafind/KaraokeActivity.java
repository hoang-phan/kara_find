package vn.hoangphan.karafind;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import vn.hoangphan.karafind.adapters.PagerAdapter;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.services.GetLinkService;
import vn.hoangphan.karafind.views.CustomTabLayout;

public class KaraokeActivity extends ActionBarActivity {
    CustomTabLayout mTabLayout;
    ViewPager mPager;
    PagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karaoke);

        initComponents();
        bindComponents();
    }

    private void initComponents() {
        DatabaseHelper.newInstance(this);
        mAdapter = new PagerAdapter(getSupportFragmentManager());

        mTabLayout = (CustomTabLayout)findViewById(R.id.tab_layout);
        mPager = (ViewPager)findViewById(R.id.viewPager);
    }

    private void bindComponents() {
        mPager.setAdapter(mAdapter);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.search));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.update));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        Intent intent = new Intent(this, GetLinkService.class);
        startService(intent);
    }
}
