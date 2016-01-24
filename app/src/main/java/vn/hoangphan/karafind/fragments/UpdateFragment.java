package vn.hoangphan.karafind.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.adapters.DataLinksAdapter;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.utils.Constants;

/**
 * Created by Hoang Phan on 1/12/2016.
 */
public class UpdateFragment extends Fragment {
    private RecyclerView mRvDataLinks;
    private DataLinksAdapter mAdapter;
    private BroadcastReceiver mReceiver;
    private CheckBox mCbSelectAll;
    private Button mBtnUpdate;
    private TextView mTvSelectAll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        mAdapter = new DataLinksAdapter(getActivity());
        mRvDataLinks = (RecyclerView)view.findViewById(R.id.rv_data_links);
        mCbSelectAll = (CheckBox)view.findViewById(R.id.cb_select_all);
        mTvSelectAll = (TextView) view.findViewById(R.id.tv_select_all);
        mBtnUpdate = (Button) view.findViewById(R.id.btn_update);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvDataLinks.setAdapter(mAdapter);
        mRvDataLinks.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setDataLinks(DatabaseHelper.getInstance().nonUpdatedDataLinks());
        mAdapter.notifyDataSetChanged();
        mCbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                long start = System.currentTimeMillis();
                if (isChecked) {
                    mAdapter.selectAll();
                    mTvSelectAll.setText(R.string.unselect_all);
                } else {
                    mAdapter.unselectAll();
                    mTvSelectAll.setText(R.string.select_all);
                }
                Log.d("Checked changed time: ", (System.currentTimeMillis() - start) + " millis");
            }
        });
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateSelected();
            }
        });
        mCbSelectAll.setChecked(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(Constants.INTENT_GET_DATA_LINKS_COMPLETED);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long start = System.currentTimeMillis();
                String label = intent.getStringExtra(Constants.VOL_LABEL);
                if (label != null) {
                    Toast.makeText(getActivity(), String.format(getString(R.string.updated), label), Toast.LENGTH_SHORT).show();
                }
                mAdapter.setDataLinks(DatabaseHelper.getInstance().nonUpdatedDataLinks());
                mAdapter.notifyDataSetChanged();
                mCbSelectAll.setChecked(false);
                mCbSelectAll.setChecked(true);
                Log.d("Receive time: ", (System.currentTimeMillis() - start) + " millis");
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
