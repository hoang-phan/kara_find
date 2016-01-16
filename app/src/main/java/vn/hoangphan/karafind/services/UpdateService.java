package vn.hoangphan.karafind.services;

import android.app.IntentService;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.LanguageUtils;

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

                CSVReader reader = new CSVReader(new InputStreamReader(url.openStream()));
                List<String> headers = Arrays.asList(reader.readNext());

                int column_id = headers.indexOf("id"),
                        column_name = headers.indexOf("name"),
                        column_author = headers.indexOf("author"),
                        column_lyric = headers.indexOf("lyric");

                String[] parts;

                while ((parts = reader.readNext()) != null) {
                    String lyric = parts[column_lyric];
                    DatabaseHelper.getInstance().insertSong(parts[column_id], parts[column_name], lyric, parts[column_author], vol, false, LanguageUtils.translateToUtf(lyric));
                }
                DatabaseHelper.getInstance().prepareFTSTable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
