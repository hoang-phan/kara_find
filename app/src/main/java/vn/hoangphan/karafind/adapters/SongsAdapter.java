package vn.hoangphan.karafind.adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.models.Song;

/**
 * Created by eastagile-tc on 1/12/16.
 */
public class SongsAdapter extends Adapter<SongsAdapter.SongHolder> {
    private List<Song> mSongs = new ArrayList<>();

    @Override
    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongHolder(view);
    }

    @Override
    public void onBindViewHolder(SongHolder holder, int position) {
        holder.populate(mSongs.get(position));
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public void setSongs(List<Song> songs) {
        mSongs = songs;
    }

    public static class SongHolder extends ViewHolder {
        private TextView mTvId, mTvName, mTvLyric, mTvAuthor;
        private ImageView mIvFavorited;

        public SongHolder(View view) {
            super(view);
            mTvId = (TextView)view.findViewById(R.id.tv_song_id);
            mTvName = (TextView)view.findViewById(R.id.tv_song_name);
            mTvLyric = (TextView)view.findViewById(R.id.tv_song_lyric);
            mTvAuthor = (TextView)view.findViewById(R.id.tv_song_author);
            mIvFavorited = (ImageView)view.findViewById(R.id.iv_song_favorite);
        }

        public void populate(Song song) {
            mTvId.setText(song.getId());
            mTvName.setText(song.getName());
            mTvAuthor.setText(song.getAuthor());
            mTvLyric.setText(song.getLyric());
            mIvFavorited.setImageResource(song.isFavorited() ? android.R.drawable.star_big_on : android.R.drawable.star_big_off);
        }
    }
}
