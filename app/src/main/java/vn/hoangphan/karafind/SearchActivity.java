package vn.hoangphan.karafind;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.hoangphan.karafind.adapters.SongsAdapter;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.models.Song;

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

        mDatabase.insert("54433", "Dau mua", "Mot con mua di qua...", "Trung Quan Idol", 53, true);
        mDatabase.insert("53232", "Vet mua", "Con mua, da xoa het nhung ngay yeu qua...", "Vu Cat Tuong", 54, false);

        mAdapter.setSongs(mDatabase.findAll());
        mAdapter.notifyDataSetChanged();
    }
}
