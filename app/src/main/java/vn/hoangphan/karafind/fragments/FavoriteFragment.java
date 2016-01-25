package vn.hoangphan.karafind.fragments;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.utils.Constants;

/**
 * Created by Hoang Phan on 1/24/2016.
 */
public class FavoriteFragment extends BaseSongsFragment {
    @Override
    protected void populateData() {
        mSongAdapter.setSongs(DatabaseHelper.getInstance().favoritedSongs());
        mSongAdapter.notifyDataSetChanged();
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
