package vn.hoangphan.karafind.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Created by Hoang Phan on 1/16/2016.
 */
public class LanguageUtils {
    public static String translateToUtf(String unicode) {
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
