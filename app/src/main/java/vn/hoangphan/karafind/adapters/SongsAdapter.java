package vn.hoangphan.karafind.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.models.Song;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.OnSongDetailClick;

/**
 * Created by eastagile-tc on 1/12/16.
 */
public class SongsAdapter extends Adapter<SongsAdapter.SongHolder> {
    private List<Song> mSongs = new ArrayList<>();
    private static OnSongDetailClick mOnSongDetailClick = null;
    private Activity mActivity;

    public SongsAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongHolder(view);
    }

    @Override
    public void onBindViewHolder(SongHolder holder, final int position) {
        if (position < mSongs.size()) {
            final Song song = mSongs.get(position);
            holder.populate(song);
            if (!TextUtils.isEmpty(song.getSinger())) {
                holder.mTvAuthorSinger.setText(mActivity.getString(R.string.singer_ph) + song.getSinger());
            } else if (!TextUtils.isEmpty(song.getAuthor())) {
                holder.mTvAuthorSinger.setText(mActivity.getString(R.string.author_ph) + song.getAuthor());
            } else {
                holder.mTvAuthorSinger.setText(R.string.unknown_author);
            }
            holder.mIvFavorited.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    song.setFavorited(!song.isFavorited());
                    DatabaseHelper.getInstance().updateFavorite(song);
                    mSongs.remove(position);
                    mSongs.add(position, song);
                    notifyDataSetChanged();
                    mActivity.sendBroadcast(new Intent(Constants.INTENT_FAVORITE));
                    mActivity.sendBroadcast(new Intent(Constants.INTENT_UPDATED_COMPLETED));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public void setSongs(List<Song> songs) {
        mSongs = songs;
    }

    public void setOnSongDetailClick(OnSongDetailClick callback) {
        mOnSongDetailClick = callback;
    }

    public static class SongHolder extends ViewHolder {
        private TextView mTvId, mTvName, mTvLyric, mTvAuthorSinger;
        private ImageView mIvFavorited;
        private LinearLayout mLySongDetail;

        public SongHolder(View view) {
            super(view);
            mTvId = (TextView)view.findViewById(R.id.tv_song_id);
            mTvName = (TextView)view.findViewById(R.id.tv_song_name);
            mTvLyric = (TextView)view.findViewById(R.id.tv_song_lyric);
            mTvAuthorSinger = (TextView)view.findViewById(R.id.tv_song_author);
            mIvFavorited = (ImageView)view.findViewById(R.id.iv_song_favorite);
            mLySongDetail = (LinearLayout)view.findViewById(R.id.ly_song_detail);
        }

        public void populate(final Song song) {
            mTvId.setText(song.getId());
            mTvName.setText(song.getName());;
            mTvLyric.setText(song.getLyric());
            mIvFavorited.setImageResource(song.isFavorited() ? R.drawable.ic_star : R.drawable.ic_star_off);

            mLySongDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnSongDetailClick.view(song);
                }
            });
        }
    }
}
