package vn.hoangphan.karafind.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.PreferenceUtils;

/**
 * Created by Hoang Phan on 1/20/2016.
 */
public class ModesAdapter extends BaseAdapter {
    public static final int[] ALL_MODES = { R.string.find_all, R.string.find_by_abbr };

    @Override
    public int getCount() {
        return ALL_MODES.length;
    }

    @Override
    public Integer getItem(int position) {
        return ALL_MODES[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutRes(position), parent, false);
        TextView tvMode = (TextView)view.findViewById(R.id.tv_item_name);
        tvMode.setText(getItem(position));
        return view;
    }

    private int getLayoutRes(int position) {
        if (PreferenceUtils.getInstance().getConfigLong(Constants.MODE) == position) {
            return R.layout.item_text_selected;
        } else {
            return R.layout.item_text;
        }
    }
}
