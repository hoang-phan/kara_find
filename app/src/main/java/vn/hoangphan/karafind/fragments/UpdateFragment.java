package vn.hoangphan.karafind.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.adapters.DataLinksAdapter;
import vn.hoangphan.karafind.db.DatabaseHelper;

/**
 * Created by Hoang Phan on 1/12/2016.
 */
public class UpdateFragment extends Fragment {
    RecyclerView mRvDataLinks;
    DataLinksAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        mAdapter = new DataLinksAdapter();
        mRvDataLinks = (RecyclerView)view.findViewById(R.id.rv_data_links);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvDataLinks.setAdapter(mAdapter);
        mRvDataLinks.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setDataLinks(DatabaseHelper.getInstance().allDataLinks());
        mAdapter.notifyDataSetChanged();
    }
}
