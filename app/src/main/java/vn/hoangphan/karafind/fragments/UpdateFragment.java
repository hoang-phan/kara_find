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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.adapters.DataLinksAdapter;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.PreferenceUtils;

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
    private LinearLayout mLyHeader;
    private LinearLayout mLyHeaderNone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        mAdapter = new DataLinksAdapter(getActivity());
        mRvDataLinks = (RecyclerView)view.findViewById(R.id.rv_data_links);
        mCbSelectAll = (CheckBox)view.findViewById(R.id.cb_select_all);
        mTvSelectAll = (TextView) view.findViewById(R.id.tv_select_all);
        mBtnUpdate = (Button) view.findViewById(R.id.btn_update);
        mLyHeader = (LinearLayout) view.findViewById(R.id.ly_update_header);
        mLyHeaderNone = (LinearLayout) view.findViewById(R.id.ly_update_header_none);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvDataLinks.setAdapter(mAdapter);
        mRvDataLinks.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setDataLinks(DatabaseHelper.getInstance().nonUpdatedDataLinks());
        mAdapter.notifyDataSetChanged();
        checkLinksCount();
        mCbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAdapter.selectAll();
                    mTvSelectAll.setText(R.string.unselect_all);
                } else {
                    mAdapter.unselectAll();
                    mTvSelectAll.setText(R.string.select_all);
                }
            }
        });
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdating();
            }
        });
        mCbSelectAll.setChecked(false);
        mCbSelectAll.setChecked(true);
        if (PreferenceUtils.getInstance().getConfigLong(Constants.AUTO_UPDATE) == 1) {
            startUpdating();
        }
    }

    private void startUpdating() {
        mCbSelectAll.setVisibility(View.GONE);
        mTvSelectAll.setText(R.string.updating);
        mBtnUpdate.setVisibility(View.GONE);
        mAdapter.updateSelected();
    }

    private void stopUpdating() {
        mCbSelectAll.setVisibility(View.VISIBLE);
        mBtnUpdate.setVisibility(View.VISIBLE);
        mTvSelectAll.setText(mCbSelectAll.isChecked() ? R.string.unselect_all : R.string.select_all);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.INTENT_GET_DATA_LINKS_COMPLETED);
        intentFilter.addAction(Constants.INTENT_AUTO_UPDATE_ON);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Constants.INTENT_GET_DATA_LINKS_COMPLETED:
                        int label = intent.getIntExtra(Constants.VOL_LABEL, -1);
                        String type = intent.getStringExtra(Constants.TYPE);
                        mAdapter.setDataLinks(DatabaseHelper.getInstance().nonUpdatedDataLinks());
                        if (label != -1) {
                            Toast.makeText(getActivity(), String.format(getString(R.string.updated), type + " - VOL " + label), Toast.LENGTH_SHORT).show();
                            if (mAdapter.notifyLinkUpdated(type, label)) {
                                stopUpdating();
                            }
                        } else {
                            mCbSelectAll.setChecked(false);
                            mCbSelectAll.setChecked(true);
                            if (PreferenceUtils.getInstance().getConfigLong(Constants.AUTO_UPDATE) == 1) {
                                startUpdating();
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        checkLinksCount();
                        break;
                    case Constants.INTENT_AUTO_UPDATE_ON:
                        mCbSelectAll.setChecked(false);
                        mCbSelectAll.setChecked(true);
                        startUpdating();
                        break;
                }
            }
        };

        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }

    private void checkLinksCount() {
        if (mAdapter.getItemCount() == 0) {
            mLyHeader.setVisibility(View.GONE);
            mLyHeaderNone.setVisibility(View.VISIBLE);
        } else {
            mLyHeaderNone.setVisibility(View.GONE);
            mLyHeader.setVisibility(View.VISIBLE);
        }
    }
}
