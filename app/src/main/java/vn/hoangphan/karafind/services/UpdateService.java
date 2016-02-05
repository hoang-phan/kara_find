package vn.hoangphan.karafind.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

            int vol = dataLink.getVol();
            if (vol >= 0) {
                try {
                    String link = dataLink.getLink();
                    String stype = dataLink.getStype();
                    Log.d("Updating", "Vol " + vol + ". Type: " + stype);
                    URL url = new URL(link);

                    ZipFile zipFile = new ZipFile(createTempFile(url));
                    zipFile.setPassword(dataLink.getDecryptedPassword());
                    List<Object> fileHeaderList = zipFile.getFileHeaders();
                    InputStream is = zipFile.getInputStream((FileHeader) fileHeaderList.get(0));

                    CSVReader reader = new CSVReader(new InputStreamReader(is));
                    List<String> headers = Arrays.asList(reader.readNext());

                    int column_id = headers.indexOf("id"),
                            column_name = headers.indexOf("name"),
                            column_author = headers.indexOf("author"),
                            column_singer = headers.indexOf("singer"),
                            column_lyric = headers.indexOf("lyric");

                    String[] parts;
                    List<Song> songs = new ArrayList<>();

                    while ((parts = reader.readNext()) != null) {
                        Song song = new Song();
                        song.setId(parts[column_id]);
                        song.setName(parts[column_name]);
                        song.setAuthor(parts[column_author]);
                        song.setSinger(parts[column_singer]);
                        song.setLyric(parts[column_lyric]);
                        song.setVol(vol);
                        songs.add(song);
                    }

                    reader.close();

                    Log.d("Reading CSV", "completed");

                    DatabaseHelper.getInstance().insertSongs(songs, stype);
                    DatabaseHelper.getInstance().updateLinkVersion(dataLink);

                    Intent fragmentIntent = new Intent(Constants.INTENT_GET_DATA_LINKS_COMPLETED);
                    fragmentIntent.putExtra(Constants.TYPE, dataLink.getStype());
                    fragmentIntent.putExtra(Constants.VOL_LABEL, dataLink.getVol());
                    sendBroadcast(fragmentIntent);
                    Thread.sleep(200);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ZipException e) {
                    e.printStackTrace();
                }
            }
            sendBroadcast(new Intent(Constants.INTENT_UPDATED_COMPLETED));

            mIsRunning = false;
        }
    }

    private File createTempFile(URL url) throws IOException {
        File tempFile = File.createTempFile("temp", ".zip");
        tempFile.deleteOnExit();

        FileOutputStream fout = new FileOutputStream(tempFile);
        InputStream is = url.openStream();

        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) != -1) {
            fout.write(buf, 0, len);
        }

        fout.close();
        return tempFile;
    }
}
