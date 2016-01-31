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
    private Set<DataLink> mSelectedLinks = new HashSet<>();
    private Set<DataLink> mUpdatingLinks = new HashSet<>();
    private Activity mActivity;
    private boolean mShowUpdated = false;

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
        final DataLink dataLink = mShowUpdated ? mDataLinks.get(position) : nonUpdatedDataLink().get(position);
        holder.mTvVol.setText("VOL " + dataLink.getVol());
        holder.mTvType.setText(dataLink.getStype());
        if (dataLink.getVersion() == dataLink.getUpdatedAt()) {
            holder.mCbSelect.setVisibility(View.INVISIBLE);
            holder.mTvUpdatedAt.setText(R.string.updated);
        } else if (mUpdatingLinks.contains(dataLink)) {
            holder.mCbSelect.setVisibility(View.INVISIBLE);
            holder.mTvUpdatedAt.setText(R.string.updating);
        }else {
            holder.mCbSelect.setVisibility(View.VISIBLE);
            holder.mTvUpdatedAt.setText(CalendarUtils.secondToDateTime(dataLink.getUpdatedAt()));
            holder.mCbSelect.setChecked(mSelectedLinks.contains(dataLink));
            holder.mCbSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedLinks.contains(dataLink)) {
                        mSelectedLinks.remove(dataLink);
                    } else {
                        mSelectedLinks.add(dataLink);
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void selectAll() {
        mSelectedLinks.clear();
        mSelectedLinks.addAll(mDataLinks);
        notifyDataSetChanged();
    }

    public void unselectAll() {
        mSelectedLinks.clear();
        notifyDataSetChanged();
    }

    public void updateSelected() {
        for (DataLink dataLink : mSelectedLinks) {
            UpdateService.pushLink(dataLink);
            mUpdatingLinks.add(dataLink);
        }
        mSelectedLinks.clear();
        notifyDataSetChanged();
        mActivity.startService(new Intent(mActivity, UpdateService.class));
    }

    public boolean notifyLinkUpdated(String type, int vol) {
        for (DataLink dataLink : mUpdatingLinks) {
            if (type.equals(dataLink.getStype()) && vol == dataLink.getVol()) {
                mUpdatingLinks.remove(dataLink);
                break;
            }
        }
        return mUpdatingLinks.isEmpty();
    }

    public boolean isAllLinksUpdated() {
        for (DataLink dataLink : mDataLinks) {
            if (dataLink.getVersion() < dataLink.getUpdatedAt()) {
                return false;
            }
        }
        return true;
    }

    public boolean isShowUpdated() {
        return mShowUpdated;
    }

    public void setShowUpdated(boolean showUpdated) {
        this.mShowUpdated = showUpdated;
    }

    @Override
    public int getItemCount() {
        return mShowUpdated ? mDataLinks.size() : nonUpdatedDataLink().size();
    }

    public void setDataLinks(List<DataLink> dataLinks) {
        mDataLinks = dataLinks;
    }

    private List<DataLink> nonUpdatedDataLink() {
        List<DataLink> result = new ArrayList<>();
        for (DataLink dataLink : mDataLinks) {
            if (dataLink.getUpdatedAt() > dataLink.getVersion()) {
                result.add(dataLink);
            }
        }
        return result;
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
