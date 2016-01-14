package vn.hoangphan.karafind.utils;

import java.text.DateFormat;

/**
 * Created by Hoang Phan on 1/13/2016.
 */
public class CalendarUtils {
    public static String secondToDateTime(long seconds) {
        return DateFormat.getDateInstance().format(seconds * 1000);
    }
}
