package vn.hoangphan.karafind.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import vn.hoangphan.karafind.models.DataLink;
import vn.hoangphan.karafind.models.Song;
import vn.hoangphan.karafind.utils.LanguageUtils;

/**
 * Created by eastagile-tc on 1/12/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "kara.db";
    public static final String TABLE_SONGS = "songs";
    public static final String TABLE_DATA_LINKS = "data_links";
    public static final String TABLE_FTS_LYRICS = "fts_lyrics";
    public static final String TABLE_FTS_INFO = "fts_info";
    public static final String COLUMN_FAVORITED = "favorited";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SONG_ID = "song_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LYRIC = "lyric";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_VOL = "vol";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_UTF = "utf";
    public static final String COLUMN_VERSION = "version";
    public static final String COLUMN_INFO_UTF = "info_utf";

    public static final int VALUE_TRUE = 1;

    public static final String CREATE_TABLE_SONGS_SQL = "CREATE TABLE %s (%s integer primary key, %s text, %s text, %s text, %s text, %s integer, %s integer default 0, %s text, %s text)";
    public static final String CREATE_TABLE_FTS_SEARCH_SQL = "CREATE VIRTUAL TABLE %s USING fts4 (%s)";
    public static final String CREATE_TABLE_DATA_LINKS_SQL = "CREATE TABLE %s (%s integer primary key, %s integer, %s text, %s integer default 0, %s integer default 0)";

    public static final String ADD_INDEX_SQL = "CREATE UNIQUE INDEX `index_%s` ON `%s` (`%s` ASC)";
    public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS %s";
    public static final String DROP_INDEX_SQL = "DROP INDEX IF EXISTS `index_%s`";

    public static final String SELECT_FTS = "SELECT docid, HEX(MATCHINFO(%1$s, 's')) AS rank, LENGTH(%2$s) AS len FROM %1$s WHERE %1$s MATCH ?";

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
        db.execSQL(String.format(CREATE_TABLE_SONGS_SQL, TABLE_SONGS, COLUMN_ID, COLUMN_SONG_ID, COLUMN_NAME, COLUMN_LYRIC, COLUMN_AUTHOR, COLUMN_VOL, COLUMN_FAVORITED, COLUMN_UTF, COLUMN_INFO_UTF));
        db.execSQL(String.format(CREATE_TABLE_DATA_LINKS_SQL, TABLE_DATA_LINKS, COLUMN_ID, COLUMN_VOL, COLUMN_LINK, COLUMN_UPDATED_AT, COLUMN_VERSION));
        db.execSQL(String.format(CREATE_TABLE_FTS_SEARCH_SQL, TABLE_FTS_LYRICS, COLUMN_UTF));
        db.execSQL(String.format(CREATE_TABLE_FTS_SEARCH_SQL, TABLE_FTS_INFO, COLUMN_INFO_UTF));
        db.execSQL(String.format(ADD_INDEX_SQL, COLUMN_SONG_ID, TABLE_SONGS, COLUMN_SONG_ID));
        db.execSQL(String.format(ADD_INDEX_SQL, COLUMN_VOL, TABLE_DATA_LINKS, COLUMN_VOL));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetDb(db);
    }

    public void resetDb(SQLiteDatabase db) {
        dropDb(db);
        onCreate(db);
    }

    public void resetDb() {
        resetDb(getWritableDatabase());
    }

    public boolean insertSongs(List<Song> songs) {
        String insertSql = String.format("INSERT OR REPLACE INTO %1$s(%2$s, %3$s, %4$s, %5$s, %6$s, %7$s, %8$s, %9$s) VALUES (?, ?, ?, ?, ?, ?, ?, (SELECT %8$s FROM %1$s WHERE %2$s = ? LIMIT 1));", TABLE_SONGS, COLUMN_SONG_ID, COLUMN_NAME, COLUMN_LYRIC, COLUMN_AUTHOR, COLUMN_VOL, COLUMN_UTF, COLUMN_INFO_UTF, COLUMN_FAVORITED);
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
            statement.bindString(8, song.getId());
            statement.execute();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    public boolean insertDataLinks(List<DataLink> dataLinks) {
        String insertSql = String.format("INSERT OR REPLACE INTO %1$s(%2$s, %3$s, %4$s, %5$s) VALUES (?, ?, ?, (SELECT %5$s FROM %1$s WHERE %2$s = ? LIMIT 1));", TABLE_DATA_LINKS, COLUMN_VOL, COLUMN_LINK, COLUMN_UPDATED_AT, COLUMN_VERSION);
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(insertSql);
        db.beginTransaction();
        for (DataLink dataLink : dataLinks) {
            statement.clearBindings();
            statement.bindLong(1, dataLink.getVol());
            statement.bindString(2, dataLink.getLink());
            statement.bindLong(3, dataLink.getUpdatedAt());
            statement.bindLong(4, dataLink.getVol());
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
        db.execSQL(String.format("UPDATE %s SET %s = ? WHERE %s = ?", TABLE_SONGS, COLUMN_FAVORITED, COLUMN_SONG_ID), new String[] { song.isFavorited() ? "1" : "0", song.getId() });
        return true;
    }

    public boolean prepareFTSTables() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_FTS_LYRICS, null, null);
        db.delete(TABLE_FTS_INFO, null, null);
        db.execSQL(String.format("INSERT INTO %1$s(docid, %2$s) SELECT %3$s, %2$s FROM %4$s", TABLE_FTS_LYRICS, COLUMN_UTF, COLUMN_ID, TABLE_SONGS));
        db.execSQL(String.format("INSERT INTO %1$s(docid, %2$s) SELECT %3$s, %2$s FROM %4$s", TABLE_FTS_INFO, COLUMN_INFO_UTF, COLUMN_ID, TABLE_SONGS));
        return true;
    }

    public List<Song> songsMatch(String filter) {
        ArrayList<Song> songs = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String matchLyricSql = String.format(SELECT_FTS, TABLE_FTS_LYRICS, COLUMN_UTF);
        String matchSongSql = String.format(SELECT_FTS, TABLE_FTS_INFO, COLUMN_INFO_UTF);

        Cursor res = db.rawQuery(String.format("SELECT %1$s.*, fts.sum_rank FROM %1$s JOIN (SELECT docid, GROUP_CONCAT(rank) AS sum_rank, MIN(len) AS min_len FROM (%2$s UNION ALL %3$s) GROUP BY docid ORDER BY sum_rank DESC, min_len ASC LIMIT 20) fts ON %1$s.%4$s = fts.docid ORDER BY fts.sum_rank DESC, fts.min_len ASC", TABLE_SONGS, matchLyricSql, matchSongSql, COLUMN_ID), new String[]{filter, filter });
        res.moveToFirst();

        while (!res.isAfterLast()) {
            songs.add(getSong(res));
            res.moveToNext();
        }

        return songs;
    }

    public List<Song> allSongs() {
        ArrayList<Song> songs = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor res = db.rawQuery(String.format("SELECT * FROM %s ORDER BY %s LIMIT 40", TABLE_SONGS, COLUMN_NAME), null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            songs.add(getSong(res));
            res.moveToNext();
        }

        return songs;
    }

    public List<DataLink> allDataLinks() {
        ArrayList<DataLink> dataLinks = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery(String.format("SELECT * FROM %s ORDER BY %s DESC", TABLE_DATA_LINKS, COLUMN_VOL), null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            dataLinks.add(getDataLink(res));
            res.moveToNext();
        }

        return dataLinks;
    }

    private void dropDb(SQLiteDatabase db) {
        db.execSQL(String.format(DROP_INDEX_SQL, COLUMN_SONG_ID));
        db.execSQL(String.format(DROP_INDEX_SQL, COLUMN_VOL));
        db.execSQL(String.format(DROP_TABLE_SQL, TABLE_FTS_LYRICS));
        db.execSQL(String.format(DROP_TABLE_SQL, TABLE_FTS_INFO));
        db.execSQL(String.format(DROP_TABLE_SQL, TABLE_SONGS));
        db.execSQL(String.format(DROP_TABLE_SQL, TABLE_DATA_LINKS));
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
        dataLink.setVersion(res.getInt(res.getColumnIndex(COLUMN_VERSION)));
        return dataLink;
    }
}
