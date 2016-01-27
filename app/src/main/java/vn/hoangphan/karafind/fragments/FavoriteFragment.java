package vn.hoangphan.karafind.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.models.Song;
import vn.hoangphan.karafind.utils.Constants;

/**
 * Created by Hoang Phan on 1/24/2016.
 */
public class FavoriteFragment extends BaseSongsFragment {
    private TextView mTvNoSong;

    @Override
    protected void extraInit(View view, LayoutInflater inflater) {
        mTvNoSong = (TextView)view.findViewById(R.id.tv_no_song);
    }

    @Override
    protected void populateData() {
        List<Song> songsList = DatabaseHelper.getInstance().favoritedSongs();
        mSongAdapter.setSongs(songsList);
        mSongAdapter.notifyDataSetChanged();
        if (songsList.isEmpty()) {
            mRvSongs.setVisibility(View.GONE);
            mTvNoSong.setVisibility(View.VISIBLE);
        } else {
            mTvNoSong.setVisibility(View.GONE);
            mRvSongs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String intentIdentifier() {
        return Constants.INTENT_FAVORITE;
    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_favorite;
    }
}
