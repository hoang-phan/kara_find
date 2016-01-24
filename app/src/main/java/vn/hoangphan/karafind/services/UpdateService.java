package vn.hoangphan.karafind.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import au.com.bytecode.opencsv.CSVReader;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.models.DataLink;
import vn.hoangphan.karafind.models.Song;
import vn.hoangphan.karafind.utils.Constants;

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
            long start = System.currentTimeMillis();
            dataLink = mLinks.remove();

            String link = dataLink.getLink();
            int vol = dataLink.getVol();
            String stype = dataLink.getStype();

            if (vol >= 0) {
                try {
                    long time = System.currentTimeMillis();
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
                    long time2 = System.currentTimeMillis();
                    DatabaseHelper.getInstance().insertSongs(songs, stype);
                    DatabaseHelper.getInstance().updateLinkVersion(dataLink);

                    Log.d("CSV read time:", (time2 - time) + " milliseconds");
                    Log.d("Database time:", (System.currentTimeMillis() - time2) + " milliseconds");

                    Intent fragmentIntent = new Intent(Constants.INTENT_GET_DATA_LINKS_COMPLETED);
                    fragmentIntent.putExtra(Constants.VOL_LABEL, dataLink.getStype() + " - VOL " + dataLink.getVol());
                    sendBroadcast(fragmentIntent);
                    Thread.sleep(200);
                    Log.d("Overall time:", (System.currentTimeMillis() - start) + " milliseconds");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            DatabaseHelper.getInstance().prepareFTSTables();
            sendBroadcast(new Intent(Constants.INTENT_UPDATED_COMPLETED));

            mIsRunning = false;
        }
    }
}
