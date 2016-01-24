package vn.hoangphan.karafind.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.utils.Constants;

/**
 * Created by Hoang Phan on 1/24/2016.
 */
public class SongDetailsFragment extends Fragment {
    private TextView mTvSongId;
    private TextView mTvSongName;
    private TextView mTvSongAuthor;
    private TextView mTvSongLyric;
    private Button mBtnDismiss;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_detail, container, false);
        mTvSongId = (TextView) view.findViewById(R.id.tv_song_id_detail);
        mTvSongName = (TextView) view.findViewById(R.id.tv_song_name_detail);
        mTvSongAuthor = (TextView) view.findViewById(R.id.tv_song_author_detail);
        mTvSongLyric = (TextView) view.findViewById(R.id.tv_song_lyric_detail);
        mBtnDismiss = (Button) view.findViewById(R.id.btn_dismiss);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        mTvSongId.setText(args.getString(Constants.SONG_ID));
        mTvSongName.setText(args.getString(Constants.SONG_NAME));
        mTvSongAuthor.setText(args.getString(Constants.SONG_AUTHOR));
        mTvSongLyric.setText(args.getString(Constants.SONG_LYRIC));

        mBtnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }
        });
    }

    private void removeSelf() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(SongDetailsFragment.this).commit();
    }
}
