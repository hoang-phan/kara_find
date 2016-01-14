package vn.hoangphan.karafind.services;

import android.app.IntentService;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.utils.Constants;

/**
 * Created by Hoang Phan on 1/12/2016.
 */
public class UpdateService extends IntentService {
    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String link = intent.getStringExtra(Constants.LINK);
        int vol = intent.getIntExtra(Constants.VOL, -1);

        if (vol >= 0) {
            try {
                URL url = new URL(link);

                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str = in.readLine();

                List<String> headers = Arrays.asList(str.split(","));
                int column_id = headers.indexOf("id"),
                        column_name = headers.indexOf("name"),
                        column_author = headers.indexOf("author"),
                        column_lyric = headers.indexOf("lyric");

                while ((str = in.readLine()) != null) {
                    String[] parts = str.split(",", 4);
                    if (parts.length == 4) {
                        DatabaseHelper.getInstance().insertSong(parts[column_id], parts[column_name], parts[column_lyric], parts[column_author], vol, false);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
