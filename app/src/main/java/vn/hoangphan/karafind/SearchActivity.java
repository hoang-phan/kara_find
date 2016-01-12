package vn.hoangphan.karafind;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import vn.hoangphan.karafind.adapters.SongsAdapter;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.models.Song;
import vn.hoangphan.karafind.models.net.DataLinksResponse;
import vn.hoangphan.karafind.net.APIService;

public class SearchActivity extends Activity {
    private RecyclerView mRvSongs;
    private SongsAdapter mAdapter;
    private DatabaseHelper mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initComponents();
        bindComponents();
    }

    private void initComponents() {
        mRvSongs = (RecyclerView)findViewById(R.id.rv_songs);
        mAdapter = new SongsAdapter();
        mDatabase = new DatabaseHelper(this);
    }

    private void bindComponents() {
        mRvSongs.setAdapter(mAdapter);
        mRvSongs.setLayoutManager(new LinearLayoutManager(this));

        mDatabase.resetDb(mDatabase.getWritableDatabase());

        Call<DataLinksResponse> call = APIService.getInstance().getDataLinks();
        call.enqueue(new Callback<DataLinksResponse>() {
            @Override
            public void onResponse(Response<DataLinksResponse> response, Retrofit retrofit) {
                final DataLinksResponse.DataLink dataLink = response.body().getDataLinks().get(0);
                if (dataLink != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            populateSongs(dataLink);
                        }
                    }).start();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }

    private void populateSongs(DataLinksResponse.DataLink dataLink) {
        try {
            URL url = new URL(dataLink.getLink());

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str = in.readLine();

            List<String> headers = Arrays.asList(str.split(","));
            int column_id = headers.indexOf("id"),
                    column_name = headers.indexOf("name"),
                    column_author = headers.indexOf("author"),
                    column_lyric = headers.indexOf("lyric");

            while ((str = in.readLine()) != null) {
                String[] parts = str.split(",", 4);
                mDatabase.insert(parts[column_id], parts[column_name], parts[column_lyric], parts[column_author], dataLink.getVol(), false);
            }
            mAdapter.setSongs(mDatabase.findAll());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
