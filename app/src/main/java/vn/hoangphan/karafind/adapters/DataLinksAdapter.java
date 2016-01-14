package vn.hoangphan.karafind.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.models.DataLink;
import vn.hoangphan.karafind.utils.CalendarUtils;
import vn.hoangphan.karafind.utils.OnDataLinkSelected;

/**
 * Created by Hoang Phan on 1/12/2016.
 */
public class DataLinksAdapter extends Adapter<DataLinksAdapter.DataLinkHolder> {
    private List<DataLink> mDataLinks = new ArrayList<>();
    private static OnDataLinkSelected mOnDataLinkSelected = null;

    @Override
    public DataLinkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_link, parent, false);
        return new DataLinkHolder(view);
    }

    @Override
    public void onBindViewHolder(DataLinkHolder holder, int position) {
        holder.populateData(mDataLinks.get(position));
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
        TextView mTvVol, mTvUpdatedAt;
        Button mBtnUpdate;

        public DataLinkHolder(View view) {
            super(view);
            mTvVol = (TextView) view.findViewById(R.id.tv_vol);
            mTvUpdatedAt = (TextView) view.findViewById(R.id.tv_updated_at);
            mBtnUpdate = (Button) view.findViewById(R.id.btn_update);
        }

        public void populateData(final DataLink dataLink) {
            mTvVol.setText(String.valueOf(dataLink.getVol()));
            mTvUpdatedAt.setText(CalendarUtils.secondToDateTime(dataLink.getUpdatedAt()));
            mBtnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnDataLinkSelected != null) {
                        mOnDataLinkSelected.update(dataLink);
                    }
                }
            });
        }
    }
}
