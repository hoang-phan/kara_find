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
        return pattern.matcher(temp).replaceAll("").toUpperCase().replaceAll("Đ", "D");
    }
}
