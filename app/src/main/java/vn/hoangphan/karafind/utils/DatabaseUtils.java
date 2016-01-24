package vn.hoangphan.karafind.utils;

import vn.hoangphan.karafind.db.DatabaseHelper;

/**
 * Created by Hoang Phan on 1/24/2016.
 */
public class DatabaseUtils {
    public static String getTableName(String type) {
        switch (type) {
            case Constants.TYPE_AR5:
                return DatabaseHelper.TABLE_AR5;
            case Constants.TYPE_MSC:
                return DatabaseHelper.TABLE_MSC;
            case Constants.TYPE_KTV:
                return DatabaseHelper.TABLE_KTV;
            case Constants.TYPE_CAL:
                return DatabaseHelper.TABLE_CAL;
            case Constants.TYPE_ARE:
                return DatabaseHelper.TABLE_ARE;
        }
        return null;
    }

    public static String getTableFTSLyricName(String type) {
        switch (type) {
            case Constants.TYPE_AR5:
                return DatabaseHelper.TABLE_FTS_AR5_LYRICS;
            case Constants.TYPE_MSC:
                return DatabaseHelper.TABLE_FTS_MSC_LYRICS;
            case Constants.TYPE_KTV:
                return DatabaseHelper.TABLE_FTS_KTV_LYRICS;
            case Constants.TYPE_CAL:
                return DatabaseHelper.TABLE_FTS_CAL_LYRICS;
            case Constants.TYPE_ARE:
                return DatabaseHelper.TABLE_FTS_ARE_LYRICS;
        }
        return null;
    }

    public static String getTableFTSInfoName(String type) {
        switch (type) {
            case Constants.TYPE_AR5:
                return DatabaseHelper.TABLE_FTS_AR5_INFO;
            case Constants.TYPE_MSC:
                return DatabaseHelper.TABLE_FTS_MSC_INFO;
            case Constants.TYPE_KTV:
                return DatabaseHelper.TABLE_FTS_KTV_INFO;
            case Constants.TYPE_CAL:
                return DatabaseHelper.TABLE_FTS_CAL_INFO;
            case Constants.TYPE_ARE:
                return DatabaseHelper.TABLE_FTS_ARE_INFO;
        }
        return null;
    }

    public static String getTableName(int type) {
        return getTableName(Constants.ALL_TYPES[type]);
    }

    public static String getTableFTSLyricName(int type) {
        return getTableFTSLyricName(Constants.ALL_TYPES[type]);
    }

    public static String getTableFTSInfoName(int type) {
        return getTableFTSInfoName(Constants.ALL_TYPES[type]);
    }
}
