package vn.hoangphan.karafind.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import vn.hoangphan.karafind.models.Song;

/**
 * Created by eastagile-tc on 1/12/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "kara.db";
    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SONG_ID = "song_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LYRIC = "lyric";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_VOL = "vol";
    public static final String COLUMN_FAVORITED = "favorited";

    public static final int VALUE_TRUE = 1;
    public static final int VALUE_FALSE = 0;

    public static final String CREATE_TABLE_SQL = "CREATE TABLE %s (%s integer primary key, %s text, %s text, %s text, %s text, %s integer, %s integer)";
    public static final String ADD_INDEX_SQL = "CREATE INDEX `index_%s` ON `%s` (`%s` ASC)";
    public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS %s";
    public static final String DROP_INDEX_SQL = "DROP INDEX IF EXISTS `index_%s`";

    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(CREATE_TABLE_SQL, TABLE_SONGS, COLUMN_ID, COLUMN_SONG_ID, COLUMN_NAME, COLUMN_LYRIC, COLUMN_AUTHOR, COLUMN_VOL, COLUMN_FAVORITED));
        db.execSQL(String.format(ADD_INDEX_SQL, COLUMN_SONG_ID, TABLE_SONGS, COLUMN_SONG_ID));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetDb(db);
    }

    public void resetDb(SQLiteDatabase db) {
        dropDb(db);
        onCreate(db);
    }

    public boolean insert(String id, String name, String lyric, String author, int volumn, boolean favorited) {
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

    public List<Song> findAll() {
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

    private void dropDb(SQLiteDatabase db) {
        db.execSQL(String.format(DROP_INDEX_SQL, COLUMN_SONG_ID));
        db.execSQL(String.format(DROP_TABLE_SQL, TABLE_SONGS));
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
}
