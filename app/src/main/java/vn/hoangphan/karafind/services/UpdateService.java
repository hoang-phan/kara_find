package vn.hoangphan.karafind.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import au.com.bytecode.opencsv.CSVReader;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.models.DataLink;
import vn.hoangphan.karafind.models.Song;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.LanguageUtils;

/**
 * Created by Hoang Phan on 1/12/2016.
 */
public class UpdateService extends IntentService {
    private static Queue<DataLink> mLinks = new ConcurrentLinkedQueue<>();
    private static boolean mIsRunning = false;

    public UpdateService() {
        super("UpdateService");
    }

    public static void pushLink(DataLink dataLink) {
        mLinks.add(dataLink);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (mIsRunning) {
            return;
        }
        mIsRunning = true;
        DataLink dataLink;
        while (mLinks.size() > 0) {
            dataLink = mLinks.remove();

            String link = dataLink.getLink();
            int vol = dataLink.getVol();

            if (vol >= 0) {
                try {
                    URL url = new URL(link);

                    CSVReader reader = new CSVReader(new InputStreamReader(url.openStream()));
                    List<String> headers = Arrays.asList(reader.readNext());

                    int column_id = headers.indexOf("id"),
                            column_name = headers.indexOf("name"),
                            column_author = headers.indexOf("author"),
                            column_lyric = headers.indexOf("lyric");

                    String[] parts;
                    List<Song> songs = new ArrayList<>();

                    while ((parts = reader.readNext()) != null) {
                        Song song = new Song();
                        song.setId(parts[column_id]);
                        song.setName(parts[column_name]);
                        song.setAuthor(parts[column_author]);
                        song.setLyric(parts[column_lyric]);
                        song.setVol(vol);
                        songs.add(song);
                    }
                    DatabaseHelper.getInstance().insertSongs(songs);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            DatabaseHelper.getInstance().prepareFTSTable();

            mIsRunning = false;
        }
    }
}
