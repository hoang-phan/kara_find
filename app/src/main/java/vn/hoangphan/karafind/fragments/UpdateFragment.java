package vn.hoangphan.karafind.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.adapters.DataLinksAdapter;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.models.DataLink;
import vn.hoangphan.karafind.services.UpdateService;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.OnDataLinkSelected;

/**
 * Created by Hoang Phan on 1/12/2016.
 */
public class UpdateFragment extends Fragment {
    private RecyclerView mRvDataLinks;
    private DataLinksAdapter mAdapter;
    private BroadcastReceiver mReceiver;
    private Button mBtnUpdateAll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        mAdapter = new DataLinksAdapter();
        mRvDataLinks = (RecyclerView)view.findViewById(R.id.rv_data_links);
        mBtnUpdateAll = (Button)view.findViewById(R.id.btn_update_all);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvDataLinks.setAdapter(mAdapter);
        mRvDataLinks.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setDataLinks(DatabaseHelper.getInstance().allDataLinks());
        mAdapter.setOnDataLinkSelected(new OnDataLinkSelected() {
            @Override
            public void update(DataLink dataLink) {
                UpdateService.pushLink(dataLink);
                getActivity().startService(new Intent(getActivity(), UpdateService.class));
            }
        });
        mAdapter.notifyDataSetChanged();
        mBtnUpdateAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DataLink dataLink : DatabaseHelper.getInstance().allDataLinks()) {
                    Intent intent = new Intent(getActivity(), UpdateService.class);
                    getActivity().startService(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(Constants.INTENT_GET_DATA_LINKS_COMPLETED);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mAdapter.setDataLinks(DatabaseHelper.getInstance().allDataLinks());
                mAdapter.notifyDataSetChanged();
            }
        };

        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }
}
