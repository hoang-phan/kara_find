package vn.hoangphan.karafind.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.adapters.SongsAdapter;
import vn.hoangphan.karafind.models.Song;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.OnSongDetailClick;

/**
 * Created by eastagile-tc on 1/25/16.
 */
public abstract class BaseSongsFragment extends Fragment {
    protected SongsAdapter mSongAdapter;
    protected RecyclerView mRvSongs;
    protected BroadcastReceiver mReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutRes(), container, false);
        mRvSongs = (RecyclerView)view.findViewById(R.id.rv_songs);
        mSongAdapter = new SongsAdapter(getActivity());
        extraInit(view, inflater);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRvSongs.setAdapter(mSongAdapter);
        mRvSongs.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSongAdapter.setOnSongDetailClick(new OnSongDetailClick() {
            @Override
            public void view(Song song) {
                SongDetailsFragment songDetailsFragment = new SongDetailsFragment();
                Bundle args = new Bundle();
                args.putString(Constants.SONG_ID, song.getId());
                args.putString(Constants.SONG_NAME, song.getName());
                args.putString(Constants.SONG_LYRIC, song.getLyric());
                args.putString(Constants.SONG_AUTHOR, song.getAuthor());
                args.putInt(Constants.SONG_FAVORITE, song.isFavorited() ? 1 : 0);
                songDetailsFragment.setArguments(args);

                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, songDetailsFragment).commit();
            }
        });
        extraPopulate(view);
        populateData();
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(intentIdentifier());
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                populateData();
            }
        };

        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    protected void extraInit(View view, LayoutInflater inflater) {

    }

    protected void extraPopulate(View view) {

    }

    protected abstract void populateData();
    protected abstract String intentIdentifier();
    protected abstract int layoutRes();
}
