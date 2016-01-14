package vn.hoangphan.karafind.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import vn.hoangphan.karafind.models.DataLink;
import vn.hoangphan.karafind.models.Song;

/**
 * Created by eastagile-tc on 1/12/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "kara.db";
    public static final String TABLE_SONGS = "songs";
    public static final String TABLE_DATA_LINKS = "data_links";
    public static final String COLUMN_FAVORITED = "favorited";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SONG_ID = "song_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LYRIC = "lyric";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_VOL = "vol";
    public static final String COLUMN_LINK = "link";

    public static final int VALUE_TRUE = 1;
    public static final int VALUE_FALSE = 0;

    public static final String CREATE_TABLE_SONGS_SQL = "CREATE TABLE %s (%s integer primary key, %s text, %s text, %s text, %s text, %s integer, %s integer)";
    public static final String CREATE_TABLE_DATA_LINKS_SQL = "CREATE TABLE %s (%s integer primary key, %s integer, %s text, %s integer)";

    public static final String ADD_INDEX_SQL = "CREATE INDEX `index_%s` ON `%s` (`%s` ASC)";
    public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS %s";
    public static final String DROP_INDEX_SQL = "DROP INDEX IF EXISTS `index_%s`";

    private static DatabaseHelper instance = null;

    public static void newInstance(Context context) {
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
        db.execSQL(String.format(CREATE_TABLE_SONGS_SQL, TABLE_SONGS, COLUMN_ID, COLUMN_SONG_ID, COLUMN_NAME, COLUMN_LYRIC, COLUMN_AUTHOR, COLUMN_VOL, COLUMN_FAVORITED));
        db.execSQL(String.format(CREATE_TABLE_DATA_LINKS_SQL, TABLE_DATA_LINKS, COLUMN_ID, COLUMN_VOL, COLUMN_LINK, COLUMN_UPDATED_AT));
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

    public boolean insertSong(String id, String name, String lyric, String author, int volumn, boolean favorited) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SONG_ID, id);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_LYRIC, lyric);
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_VOL, volumn);
        values.put(COLUMN_FAVORITED, favorited ? VALUE_TRUE : VALUE_FALSE);
        db.insert(TABLE_SONGS, null, values);
        return true;
    }

    public boolean insertDataLink(DataLink dataLink) {
        getWritableDatabase().insert(TABLE_DATA_LINKS, null, valuesFor(dataLink));
        return true;
    }

    public boolean insertDataLinks(List<DataLink> dataLinks) {
        String insertSql = String.format("INSERT OR REPLACE INTO %1$s(%2$s, %3$s, %4$s) VALUES ((SELECT %2$s FROM %1$s WHERE %3$s = ?), ?, ?);", TABLE_DATA_LINKS, COLUMN_ID, COLUMN_VOL, COLUMN_LINK);
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(insertSql);
        db.beginTransaction();
        for (DataLink dataLink : dataLinks) {
            statement.clearBindings();
            statement.bindLong(1, dataLink.getVol());
            statement.bindLong(2, dataLink.getVol());
            statement.bindString(3, dataLink.getLink());
            statement.execute();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    public int update(String id, boolean favorited) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FAVORITED, favorited ? VALUE_TRUE : VALUE_FALSE);
        return db.update(TABLE_SONGS, values, String.format("%s = ?", COLUMN_SONG_ID), new String[] { id });
    }

    public int delete(Song song) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_SONGS, "song_id = ?", new String[]{song.getId()});
    }

    public List<Song> allSongs() {
        ArrayList<Song> songs = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery(String.format("SELECT * FROM %s", TABLE_SONGS), null);
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
        Cursor res = db.rawQuery(String.format("SELECT * FROM %s", TABLE_DATA_LINKS), null);
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
        return dataLink;
    }

    private ContentValues valuesFor(DataLink dataLink) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_VOL, dataLink.getVol());
        values.put(COLUMN_LINK, dataLink.getLink());
        return values;
    }
}
