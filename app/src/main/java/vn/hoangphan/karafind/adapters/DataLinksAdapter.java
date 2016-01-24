package vn.hoangphan.karafind.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.models.DataLink;
import vn.hoangphan.karafind.services.UpdateService;
import vn.hoangphan.karafind.utils.CalendarUtils;

/**
 * Created by Hoang Phan on 1/12/2016.
 */
public class DataLinksAdapter extends Adapter<DataLinksAdapter.DataLinkHolder> {
    private List<DataLink> mDataLinks = new ArrayList<>();
    private Set<Integer> mSelectedPositions = new HashSet<>();
    private Set<DataLink> mUpdatingLinks = new HashSet<>();
    private Activity mActivity;

    public DataLinksAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public DataLinkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_link, parent, false);
        return new DataLinkHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataLinkHolder holder, final int position) {
        final DataLink dataLink = mDataLinks.get(position);
        holder.mTvVol.setText("VOL " + dataLink.getVol());
        holder.mTvType.setText(dataLink.getStype());
        if (mUpdatingLinks.contains(dataLink)) {
            holder.mCbSelect.setVisibility(View.INVISIBLE);
            holder.mTvUpdatedAt.setText(R.string.updating);
        } else {
            holder.mCbSelect.setVisibility(View.VISIBLE);
            holder.mTvUpdatedAt.setText(CalendarUtils.secondToDateTime(dataLink.getUpdatedAt()));
            holder.mCbSelect.setChecked(mSelectedPositions.contains(position));
            holder.mCbSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedPositions.contains(position)) {
                        mSelectedPositions.remove(position);
                    } else {
                        mSelectedPositions.add(position);
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void selectAll() {
        for (int i = 0, size = mDataLinks.size(); i < size; i++) {
            mSelectedPositions.add(i);
        }
        notifyDataSetChanged();
    }

    public void unselectAll() {
        mSelectedPositions.clear();
        notifyDataSetChanged();
    }

    public void updateSelected() {
        for (int position : mSelectedPositions) {
            DataLink link = mDataLinks.get(position);
            UpdateService.pushLink(link);
            mUpdatingLinks.add(link);
        }
        mSelectedPositions.clear();
        notifyDataSetChanged();
        mActivity.startService(new Intent(mActivity, UpdateService.class));
    }

    @Override
    public int getItemCount() {
        return mDataLinks.size();
    }

    public void setDataLinks(List<DataLink> dataLinks) {
        mDataLinks = dataLinks;
    }

    public static class DataLinkHolder extends ViewHolder {
        TextView mTvVol, mTvUpdatedAt, mTvType;
        CheckBox mCbSelect;

        public DataLinkHolder(View view) {
            super(view);
            mTvVol = (TextView) view.findViewById(R.id.tv_vol);
            mTvUpdatedAt = (TextView) view.findViewById(R.id.tv_updated_at);
            mTvType = (TextView) view.findViewById(R.id.tv_type);
            mCbSelect = (CheckBox) view.findViewById(R.id.cb_select);
        }
    }
}
