package vn.hoangphan.karafind.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Hoang Phan on 1/16/2016.
 */
public class LanguageUtils {
    private static LanguageUtils instance;
    private Activity mActivity;

    public static void init(Activity activity) {
        instance = new LanguageUtils(activity);
    }

    public static LanguageUtils getInstance() {
        return instance;
    }

    private LanguageUtils(Activity activity) {
        mActivity = activity;
    }

    public void changeLanguage(Locale locale, Configuration newConfig) {
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            mActivity.getBaseContext().getResources().updateConfiguration(newConfig, mActivity.getBaseContext().getResources().getDisplayMetrics());
        }
    }

    public void changeLanguage(Locale locale) {
        changeLanguage(locale, mActivity.getBaseContext().getResources().getConfiguration());
    }

    public static String translateToUtf(String unicode) {
        if (TextUtils.isEmpty(unicode)) {
            return "";
        }
        String temp = Normalizer.normalize(unicode, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").toUpperCase().replaceAll("[ĐÐ]", "D");
    }

    public static String getFirstLetters(String src) {
        String[] words = src.split("[ ,.?!:\\-+()*]+");
        String result = "";
        for (String word : words) {
            result += word.charAt(0);
        }
        return result;
    }
}
