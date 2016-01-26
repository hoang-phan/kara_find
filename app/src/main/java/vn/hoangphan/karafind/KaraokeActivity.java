package vn.hoangphan.karafind;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateUtils;

import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import vn.hoangphan.karafind.adapters.PagerAdapter;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.fragments.SongDetailsFragment;
import vn.hoangphan.karafind.services.GetLinkService;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.LanguageUtils;
import vn.hoangphan.karafind.utils.PreferenceUtils;
import vn.hoangphan.karafind.views.NonSwipeableViewPager;

public class KaraokeActivity extends ActionBarActivity {
    TabLayout mTabLayout;
    NonSwipeableViewPager mPager;
    PagerAdapter mAdapter;
    Locale mLocale = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/windsor.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        initConfigurations();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LanguageUtils.getInstance().changeLanguage(mLocale, newConfig);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initConfigurations() {
        DatabaseHelper.init(this);
        PreferenceUtils.init(this);
        LanguageUtils.init(this);

        String language = PreferenceUtils.getInstance().getConfigString(Constants.PREFERRED_LANGUAGE);

        if (!TextUtils.isEmpty(language)) {
            mLocale = new Locale(language);
            LanguageUtils.getInstance().changeLanguage(mLocale);
            proceed();
            return;
        }

        if ("English".equals(Locale.getDefault().getDisplayLanguage())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.language_title));
            builder.setMessage(getString(R.string.language_choose_message));
            builder.setPositiveButton(getString(R.string.language_vi), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mLocale = new Locale("vi");
                    PreferenceUtils.getInstance().saveConfig(Constants.PREFERRED_LANGUAGE, "vi");
                    LanguageUtils.getInstance().changeLanguage(mLocale);
                    proceed();
                }
            });
            builder.setNegativeButton(getString(R.string.language_other), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferenceUtils.getInstance().saveConfig(Constants.PREFERRED_LANGUAGE, "en");
                    proceed();
                }
            });
            builder.setCancelable(false);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.create().show();
        } else {
            proceed();
        }
    }

    private void proceed() {
        setContentView(R.layout.activity_karaoke);
        initComponents();
        bindComponents();
    }

    private void initComponents() {
        mAdapter = new PagerAdapter(getSupportFragmentManager());
        mTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mPager = (NonSwipeableViewPager)findViewById(R.id.viewPager);
    }

    private void bindComponents() {
        mPager.setAdapter(mAdapter);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.search).setIcon(R.drawable.ic_search));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.update).setIcon(R.drawable.ic_update));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.settings).setIcon(R.drawable.ic_settings));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.favorite).setIcon(R.drawable.ic_star));
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
        mPager.setOffscreenPageLimit(4);

        if (!DateUtils.isToday(PreferenceUtils.getInstance().getConfigLong(Constants.LAST_FETCHED_AT))) {
            startService(new Intent(this, GetLinkService.class));
        }
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof SongDetailsFragment) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                return;
            }
        }
        super.onBackPressed();
    }
}
