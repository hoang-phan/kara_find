package vn.hoangphan.karafind.adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.models.DataLink;
import vn.hoangphan.karafind.utils.CalendarUtils;
import vn.hoangphan.karafind.utils.OnDataLinkSelected;

/**
 * Created by Hoang Phan on 1/12/2016.
 */
public class DataLinksAdapter extends Adapter<DataLinksAdapter.DataLinkHolder> {
    private List<DataLink> mDataLinks = new ArrayList<>();
    private Set<Integer> updatingPositions = new HashSet<>();
    private static OnDataLinkSelected mOnDataLinkSelected = null;

    @Override
    public DataLinkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_link, parent, false);
        return new DataLinkHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataLinkHolder holder, final int position) {
        final DataLink dataLink = mDataLinks.get(position);
        holder.mTvVol.setText("VOL " + dataLink.getVol());
        holder.mTvUpdatedAt.setText(CalendarUtils.secondToDateTime(dataLink.getUpdatedAt()));
        if (dataLink.getVersion() != 0 && dataLink.getUpdatedAt() == dataLink.getVersion()) {
            holder.mIvUpdate.setVisibility(View.GONE);
            holder.mTvUpdating.setVisibility(View.GONE);
            holder.mTvUpdated.setVisibility(View.VISIBLE);
        } else if (updatingPositions.contains(position)) {
            holder.mIvUpdate.setVisibility(View.GONE);
            holder.mTvUpdating.setVisibility(View.VISIBLE);
            holder.mTvUpdated.setVisibility(View.GONE);
        } else {
            holder.mIvUpdate.setVisibility(View.VISIBLE);
            holder.mTvUpdating.setVisibility(View.GONE);
            holder.mTvUpdated.setVisibility(View.GONE);
        }
        holder.mIvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDataLinkSelected != null) {
                    mOnDataLinkSelected.update(dataLink);
                }
                updatingPositions.add(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataLinks.size();
    }

    public void setDataLinks(List<DataLink> dataLinks) {
        mDataLinks = dataLinks;
    }

    public void setOnDataLinkSelected(OnDataLinkSelected onDataLinkSelected) {
        mOnDataLinkSelected = onDataLinkSelected;
    }

    public static class DataLinkHolder extends ViewHolder {
        TextView mTvVol, mTvUpdatedAt, mTvUpdating, mTvUpdated;
        ImageView mIvUpdate;

        public DataLinkHolder(View view) {
            super(view);
            mTvVol = (TextView) view.findViewById(R.id.tv_vol);
            mTvUpdatedAt = (TextView) view.findViewById(R.id.tv_updated_at);
            mTvUpdating = (TextView) view.findViewById(R.id.tv_updating);
            mTvUpdated = (TextView) view.findViewById(R.id.tv_updated);
            mIvUpdate = (ImageView) view.findViewById(R.id.iv_update);
        }
    }
}
