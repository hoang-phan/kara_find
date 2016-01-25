package vn.hoangphan.karafind.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.models.Song;
import vn.hoangphan.karafind.utils.Constants;

/**
 * Created by Hoang Phan on 1/24/2016.
 */
public class SongDetailsFragment extends Fragment {
    private TextView mTvSongId;
    private TextView mTvSongName;
    private TextView mTvSongAuthor;
    private TextView mTvSongLyric;
    private ImageView mIvFavorite;
    private Button mBtnDismiss;
    private FragmentManager mFragmentManager;
    private boolean mIsFavorited = false;
    private String mSongId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_detail, container, false);
        mTvSongId = (TextView) view.findViewById(R.id.tv_song_id_detail);
        mTvSongName = (TextView) view.findViewById(R.id.tv_song_name_detail);
        mTvSongAuthor = (TextView) view.findViewById(R.id.tv_song_author_detail);
        mTvSongLyric = (TextView) view.findViewById(R.id.tv_song_lyric_detail);
        mIvFavorite = (ImageView) view.findViewById(R.id.iv_favorite_detail);
        mBtnDismiss = (Button) view.findViewById(R.id.btn_dismiss);
        mFragmentManager = getActivity().getSupportFragmentManager();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        mIsFavorited = args.getInt(Constants.SONG_FAVORITE) == 1;
        mSongId = args.getString(Constants.SONG_ID);
        mTvSongId.setText(mSongId);
        mTvSongName.setText(args.getString(Constants.SONG_NAME));
        mTvSongAuthor.setText(args.getString(Constants.SONG_AUTHOR));
        mTvSongLyric.setText(args.getString(Constants.SONG_LYRIC));
        mIvFavorite.setImageResource(mIsFavorited ? R.drawable.ic_star : R.drawable.ic_star_off);
        mIvFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsFavorited = !mIsFavorited;
                DatabaseHelper.getInstance().updateFavorite(mSongId, mIsFavorited);
                mIvFavorite.setImageResource(mIsFavorited ? R.drawable.ic_star : R.drawable.ic_star_off);
                getActivity().sendBroadcast(new Intent(Constants.INTENT_FAVORITE));
                getActivity().sendBroadcast(new Intent(Constants.INTENT_UPDATED_COMPLETED));
            }
        });
        mBtnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }
        });
    }

    private void removeSelf() {
        mFragmentManager.beginTransaction().remove(SongDetailsFragment.this).commit();
    }
}
