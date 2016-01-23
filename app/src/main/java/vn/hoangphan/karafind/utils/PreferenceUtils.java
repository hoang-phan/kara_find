package vn.hoangphan.karafind.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Hoang Phan on 1/17/2016.
 */
public class PreferenceUtils {
    private static final String SHARED_PREFERENCES_KEY = "980axnvm222";

    public static PreferenceUtils instance;

    private Context mContext;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEdit;

    public static void init(Context context) {
        instance = new PreferenceUtils(context);
    }

    public static PreferenceUtils getInstance() {
        return instance;
    }

    private PreferenceUtils(Context context) {
        mContext = context;
        mPreferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        mEdit = mPreferences.edit();
    }

    public void saveConfig(String key, long value) {
        mEdit.putLong(key, value).commit();
    }

    public void saveConfig(String key, String value) {
        mEdit.putString(key, value).commit();
    }

    public long getConfigLong(String key) {
        return mPreferences.getLong(key, 0);
    }

    public String getConfigString(String key) {
        return mPreferences.getString(key, "");
    }

    public void reset() {
        mEdit.clear().commit();
    }
}
