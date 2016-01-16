package vn.hoangphan.karafind.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.adapters.SongsAdapter;
import vn.hoangphan.karafind.db.DatabaseHelper;

/**
 * Created by Hoang Phan on 1/12/2016.
 */
public class SearchFragment extends Fragment {
    private EditText mEtSearch;
    private RecyclerView mRvSongs;
    private ImageView mIcSearch;
    private SongsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mRvSongs = (RecyclerView)view.findViewById(R.id.rv_songs);
        mEtSearch = (EditText) view.findViewById(R.id.et_search);
        mIcSearch = (ImageView)view.findViewById(R.id.ic_search);
        mAdapter = new SongsAdapter();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvSongs.setAdapter(mAdapter);
        mRvSongs.setLayoutManager(new LinearLayoutManager(getActivity()));
        mIcSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filter = mEtSearch.getText().toString();
                if (!TextUtils.isEmpty(filter)){
                    mAdapter.setSongs(DatabaseHelper.getInstance().songsMatch(filter));
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
