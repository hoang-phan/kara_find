package vn.hoangphan.karafind;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.hoangphan.karafind.adapters.SongsAdapter;
import vn.hoangphan.karafind.models.Song;

public class SearchActivity extends Activity {
    private RecyclerView mRvSongs;
    private SongsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRvSongs = (RecyclerView)findViewById(R.id.rv_songs);

        mAdapter = new SongsAdapter();
        mRvSongs.setAdapter(mAdapter);
        mRvSongs.setLayoutManager(new LinearLayoutManager(this));

        List<Song> songs = new ArrayList<>();
        songs.add(new Song("Dau mua", "Mot con mua di qua...", "Trung Quan Idol", "54433", 53, true));
        songs.add(new Song("Vet mua", "Con mua, da xoa het nhung ngay yeu qua...", "Vu Cat Tuong", "53232", 54, false));
        mAdapter.setSongs(songs);
        mAdapter.notifyDataSetChanged();
    }
}
