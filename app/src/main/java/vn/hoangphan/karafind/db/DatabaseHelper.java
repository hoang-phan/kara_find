package vn.hoangphan.karafind.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import vn.hoangphan.karafind.models.DataLink;
import vn.hoangphan.karafind.models.Song;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.DatabaseUtils;
import vn.hoangphan.karafind.utils.LanguageUtils;
import vn.hoangphan.karafind.utils.PreferenceUtils;

/**
 * Created by eastagile-tc on 1/12/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "kara.db";
    public static final String TABLE_DATA_LINKS = "data_links";

    public static final String TABLE_AR5 = "ar5";
    public static final String TABLE_FTS_AR5_LYRICS = "fts_ar5_lyrics";
    public static final String TABLE_FTS_AR5_INFO = "fts_ar5_info";

    public static final String TABLE_CAL = "cal";
    public static final String TABLE_FTS_CAL_LYRICS = "fts_cal_lyrics";
    public static final String TABLE_FTS_CAL_INFO = "fts_cal_info";

    public static final String TABLE_KTV = "ktv";
    public static final String TABLE_FTS_KTV_LYRICS = "fts_ktv_lyrics";
    public static final String TABLE_FTS_KTV_INFO = "fts_ktv_info";

    public static final String TABLE_MSC = "msc";
    public static final String TABLE_FTS_MSC_LYRICS = "fts_msc_lyrics";
    public static final String TABLE_FTS_MSC_INFO = "fts_msc_info";

    public static final String TABLE_ARE = "are";
    public static final String TABLE_FTS_ARE_LYRICS = "fts_are_lyrics";
    public static final String TABLE_FTS_ARE_INFO = "fts_are_info";

    public static final String COLUMN_FAVORITED = "favorited";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SONG_ID = "song_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ABBR = "abbr";
    public static final String COLUMN_LYRIC = "lyric";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_VOL = "vol";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_UTF = "utf";
    public static final String COLUMN_VERSION = "version";
    public static final String COLUMN_INFO_UTF = "info_utf";
    public static final String COLUMN_STYPE = "stype";

    public static final int VALUE_TRUE = 1;

    public static final String CREATE_TABLE_DATA_LINKS_SQL = "CREATE TABLE %s (%s integer primary key, %s integer, %s text, %s integer default 0, %s integer default 0, %s text)";

    public static final String CREATE_TABLE_SONGS_SQL = "CREATE TABLE %s (%s integer primary key, %s text, %s text, %s text, %s text, %s integer, %s integer default 0, %s text, %s text, %s text)";
    public static final String CREATE_TABLE_FTS_SEARCH_SQL = "CREATE VIRTUAL TABLE %s USING fts4 (%s)";

    public static final String ADD_UNIQUE_INDEX_SQL = "CREATE UNIQUE INDEX `index_%2$s_%1$s` ON `%1$s` (`%2$s` ASC)";
    public static final String ADD_UNIQUE_INDEXES_SQL = "CREATE UNIQUE INDEX `index_%2$s_%3$s_%1$s` ON `%1$s` (`%2$s`, `%3$s`)";
    public static final String ADD_INDEX_SQL = "CREATE INDEX `index_%2$s_%1$s` ON `%1$s` (`%2$s` ASC)";
    public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS %s";
    public static final String DROP_INDEX_SQL = "DROP INDEX IF EXISTS `index_%2$s_%1$s`";
    public static final String DROP_INDEXES_SQL = "DROP INDEX IF EXISTS `index_%2$s_%3$s_%1$s`";

    public static final String SELECT_FTS = "SELECT docid, '%3$s' || HEX(MATCHINFO(%1$s, 's')) AS rank, LENGTH(%2$s) AS len FROM %1$s WHERE %1$s MATCH ?";

    private static DatabaseHelper instance = null;

    public static void init(Context context) {
        instance = new DatabaseHelper(context);
    }

    public static DatabaseHelper getInstance() {
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String type : Constants.ALL_TYPES) {
            String tableLyrics = DatabaseUtils.getTableFTSLyricName(type);
            String tableInfo = DatabaseUtils.getTableFTSInfoName(type);
            String tableName = DatabaseUtils.getTableName(type);

            db.execSQL(String.format(CREATE_TABLE_SONGS_SQL, tableName, COLUMN_ID, COLUMN_SONG_ID, COLUMN_NAME, COLUMN_LYRIC, COLUMN_AUTHOR, COLUMN_VOL, COLUMN_FAVORITED, COLUMN_UTF, COLUMN_INFO_UTF, COLUMN_ABBR));
            db.execSQL(String.format(CREATE_TABLE_FTS_SEARCH_SQL, tableLyrics, COLUMN_UTF));
            db.execSQL(String.format(CREATE_TABLE_FTS_SEARCH_SQL, tableInfo, COLUMN_INFO_UTF));
            db.execSQL(String.format(ADD_UNIQUE_INDEX_SQL, tableName, COLUMN_SONG_ID));
            db.execSQL(String.format(ADD_INDEX_SQL, tableName, COLUMN_VOL));
        }

        db.execSQL(String.format(CREATE_TABLE_DATA_LINKS_SQL, TABLE_DATA_LINKS, COLUMN_ID, COLUMN_VOL, COLUMN_LINK, COLUMN_UPDATED_AT, COLUMN_VERSION, COLUMN_STYPE));
        db.execSQL(String.format(ADD_UNIQUE_INDEXES_SQL, TABLE_DATA_LINKS, COLUMN_VOL, COLUMN_STYPE));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        reset(db);
    }

    public void reset(SQLiteDatabase db) {
        dropDb(db);
        onCreate(db);
    }

    public void reset() {
        reset(getWritableDatabase());
    }

    public boolean insertSongs(List<Song> songs, String type) {
        String insertSql = String.format("INSERT OR REPLACE INTO %1$s(%2$s, %3$s, %4$s, %5$s, %6$s, %7$s, %8$s, %9$s, %10$s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, (SELECT %10$s FROM %1$s WHERE %2$s = ? LIMIT 1));", DatabaseUtils.getTableName(type), COLUMN_SONG_ID, COLUMN_NAME, COLUMN_LYRIC, COLUMN_AUTHOR, COLUMN_VOL, COLUMN_UTF, COLUMN_INFO_UTF, COLUMN_ABBR, COLUMN_FAVORITED);
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(insertSql);
        db.beginTransaction();
        for (Song song : songs) {
            statement.clearBindings();
            statement.bindString(1, song.getId());
            statement.bindString(2, song.getName());
            statement.bindString(3, song.getLyric());
            statement.bindString(4, song.getAuthor());
            statement.bindLong(5, song.getVol());
            statement.bindString(6, LanguageUtils.translateToUtf(song.getLyric()));
            statement.bindString(7, LanguageUtils.translateToUtf(song.getName() + " " + song.getAuthor() + " " + song.getId()));
            statement.bindString(8, LanguageUtils.translateToUtf(LanguageUtils.getFirstLetters(song.getName())));
            statement.bindString(9, song.getId());
            statement.execute();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    public boolean insertDataLinks(List<DataLink> dataLinks) {
        String insertSql = String.format("INSERT OR REPLACE INTO %1$s(%2$s, %3$s, %4$s, %5$s, %6$s) VALUES (?, ?, ?, ?, (SELECT %6$s FROM %1$s WHERE %2$s = ? LIMIT 1));", TABLE_DATA_LINKS, COLUMN_VOL, COLUMN_LINK, COLUMN_UPDATED_AT, COLUMN_STYPE, COLUMN_VERSION);
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(insertSql);
        db.beginTransaction();
        for (DataLink dataLink : dataLinks) {
            statement.clearBindings();
            statement.bindLong(1, dataLink.getVol());
            statement.bindString(2, dataLink.getLink());
            statement.bindLong(3, dataLink.getUpdatedAt());
            statement.bindString(4, dataLink.getStype());
            statement.bindLong(5, dataLink.getVol());
            statement.execute();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    public boolean updateLinkVersion(DataLink dataLink) {
        dataLink.setVersion(dataLink.getUpdatedAt());
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(String.format("UPDATE %s SET %s = ? WHERE %s = ?", TABLE_DATA_LINKS, COLUMN_VERSION, COLUMN_VOL), new String[]{String.valueOf(dataLink.getVersion()), String.valueOf(dataLink.getVol())});
        return true;
    }

    public boolean updateFavorite(Song song) {
        SQLiteDatabase db = getWritableDatabase();
        int type = (int)PreferenceUtils.getInstance().getConfigLong(Constants.TYPE);
        db.execSQL(String.format("UPDATE %s SET %s = ? WHERE %s = ?", DatabaseUtils.getTableName(type), COLUMN_FAVORITED, COLUMN_SONG_ID), new String[] { song.isFavorited() ? "1" : "0", song.getId() });
        return true;
    }

    public boolean prepareFTSTables() {
        SQLiteDatabase db = getWritableDatabase();
        for (String type : Constants.ALL_TYPES) {
            String tableLyrics = DatabaseUtils.getTableFTSLyricName(type);
            String tableInfo = DatabaseUtils.getTableFTSInfoName(type);
            String tableName = DatabaseUtils.getTableName(type);
            db.delete(tableLyrics, null, null);
            db.delete(tableInfo, null, null);
            db.execSQL(String.format("INSERT INTO %1$s(docid, %2$s) SELECT %3$s, %2$s FROM %4$s", tableLyrics, COLUMN_UTF, COLUMN_ID, tableName));
            db.execSQL(String.format("INSERT INTO %1$s(docid, %2$s) SELECT %3$s, %2$s FROM %4$s", tableInfo, COLUMN_INFO_UTF, COLUMN_ID, tableName));
        }

        return true;
    }

    public List<Song> songsMatch(String filter) {
        int type = getCurrentType();
        String matchLyricSql = String.format(SELECT_FTS, DatabaseUtils.getTableFTSLyricName(type), COLUMN_UTF, "0");
        String matchSongSql = String.format(SELECT_FTS, DatabaseUtils.getTableFTSInfoName(type), COLUMN_INFO_UTF, "1");

        Cursor res = getReadableDatabase().rawQuery(String.format("SELECT %1$s.* FROM %1$s JOIN (SELECT docid, GROUP_CONCAT(rank) AS sum_rank, MIN(len) AS min_len FROM (%3$s UNION ALL %2$s) GROUP BY docid ORDER BY sum_rank DESC, min_len ASC LIMIT 20) fts ON %1$s.%4$s = fts.docid ORDER BY fts.sum_rank DESC, fts.min_len ASC limit 20", DatabaseUtils.getTableName(type), matchLyricSql, matchSongSql, COLUMN_ID), new String[]{ filter, filter });
        return getSongs(res);
    }

    public List<Song> getSongsWithFirstLetters(String filter) {
        Cursor res = getReadableDatabase().rawQuery(String.format("SELECT * FROM %1$s WHERE %2$s LIKE ? ORDER BY LENGTH(%2$s) LIMIT 20", DatabaseUtils.getTableName(getCurrentType()), COLUMN_ABBR), new String[] { filter + "%" });
        return getSongs(res);
    }

    public List<Song> allSongs() {
        Cursor res = getReadableDatabase().rawQuery(String.format("SELECT %2$s, %3$s, %4$s, %5$s, %6$s, %7$s FROM %1$s ORDER BY %2$s LIMIT 20", DatabaseUtils.getTableName(getCurrentType()), COLUMN_NAME, COLUMN_SONG_ID, COLUMN_AUTHOR, COLUMN_LYRIC, COLUMN_VOL, COLUMN_FAVORITED), null);
        return getSongs(res);
    }

    public List<Song> favoritedSongs() {
        Cursor res = getReadableDatabase().rawQuery(String.format("SELECT %2$s, %3$s, %4$s, %5$s, %6$s, %7$s FROM %1$s WHERE %7$s = ? ORDER BY %2$s LIMIT 20", DatabaseUtils.getTableName(getCurrentType()), COLUMN_NAME, COLUMN_SONG_ID, COLUMN_AUTHOR, COLUMN_LYRIC, COLUMN_VOL, COLUMN_FAVORITED), new String[] { "1" });
        return getSongs(res);
    }

    public List<Song> getSongs(Cursor res) {
        long current = System.currentTimeMillis();
        List<Song> songs = new ArrayList<>();
        res.moveToFirst();

        while (!res.isAfterLast()) {
            songs.add(getSong(res));
            res.moveToNext();
        }
        Log.e("Database query time: ", (System.currentTimeMillis() - current) + " milliseconds");
        return songs;
    }

    public List<DataLink> nonUpdatedDataLinks() {
        List<DataLink> dataLinks = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery(String.format("SELECT * FROM %s ORDER BY %s DESC", TABLE_DATA_LINKS, COLUMN_VOL), null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            DataLink dataLink = getDataLink(res);
            if (dataLink.getUpdatedAt() > dataLink.getVersion()) {
                dataLinks.add(dataLink);
            }
            res.moveToNext();
        }

        return dataLinks;
    }

    private void dropDb(SQLiteDatabase db) {
        long current = System.currentTimeMillis();

        for (String type : Constants.ALL_TYPES) {
            String tableLyrics = DatabaseUtils.getTableFTSLyricName(type);
            String tableInfo = DatabaseUtils.getTableFTSInfoName(type);
            String tableName = DatabaseUtils.getTableName(type);

            db.execSQL(String.format(DROP_INDEX_SQL, tableName, COLUMN_SONG_ID));
            db.execSQL(String.format(DROP_INDEXES_SQL, tableName, COLUMN_VOL, COLUMN_STYPE));
            db.execSQL(String.format(DROP_TABLE_SQL, tableLyrics));
            db.execSQL(String.format(DROP_TABLE_SQL, tableInfo));
            db.execSQL(String.format(DROP_TABLE_SQL, tableName));
        }

        db.execSQL(String.format(DROP_INDEXES_SQL, TABLE_DATA_LINKS, COLUMN_VOL, COLUMN_STYPE));
        db.execSQL(String.format(DROP_TABLE_SQL, TABLE_DATA_LINKS));
        Log.d("Drop DB time", (System.currentTimeMillis() - current) + " milliseconds");
    }

    private Song getSong(Cursor res) {
        Song song = new Song();
        song.setId(res.getString(res.getColumnIndex(COLUMN_SONG_ID)));
        song.setName(res.getString(res.getColumnIndex(COLUMN_NAME)));
        song.setLyric(res.getString(res.getColumnIndex(COLUMN_LYRIC)));
        song.setAuthor(res.getString(res.getColumnIndex(COLUMN_AUTHOR)));
        song.setVol(res.getInt(res.getColumnIndex(COLUMN_VOL)));
        song.setFavorited(res.getInt(res.getColumnIndex(COLUMN_FAVORITED)) == VALUE_TRUE);
        return song;
    }

    private DataLink getDataLink(Cursor res) {
        DataLink dataLink = new DataLink();
        dataLink.setVol(res.getInt(res.getColumnIndex(COLUMN_VOL)));
        dataLink.setLink(res.getString(res.getColumnIndex(COLUMN_LINK)));
        dataLink.setUpdatedAt(res.getInt(res.getColumnIndex(COLUMN_UPDATED_AT)));
        dataLink.setStype(res.getString(res.getColumnIndex(COLUMN_STYPE)));
        dataLink.setVersion(res.getInt(res.getColumnIndex(COLUMN_VERSION)));
        return dataLink;
    }

    private int getCurrentType() {
        return (int)PreferenceUtils.getInstance().getConfigLong(Constants.TYPE);
    }
}
