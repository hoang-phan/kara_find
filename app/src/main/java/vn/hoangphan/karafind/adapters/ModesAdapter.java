package vn.hoangphan.karafind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import vn.hoangphan.karafind.R;

/**
 * Created by Hoang Phan on 1/19/2016.
 */
public class ModesAdapter extends ArrayAdapter<String> {
    LayoutInflater mInflater;
    public static final int MODE_FREE = 0;
    public static final int MODE_FIRST_LETTER = 1;
    public static final int MODE_NAME = 2;

    public ModesAdapter(Context context) {
        super(context, 0, new String[] { "free", "fl", "name" } );
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_mode_dropdown, parent, false);
        TextView tvMode = (TextView)view.findViewById(R.id.tv_mode);
        ImageView ivMode = (ImageView)view.findViewById(R.id.iv_mode);
        ivMode.setImageResource(getModeImageRes(position));
        switch (position) {
            case MODE_FREE:
                tvMode.setText(R.string.mode_free);
                break;
            case MODE_FIRST_LETTER:
                tvMode.setText(R.string.mode_first_letter);
                break;
            case MODE_NAME:
                tvMode.setText(R.string.mode_name);
                break;
            default:
                break;
        }
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_mode, parent, false);
        ImageView ivMode = (ImageView)view.findViewById(R.id.iv_mode_selected);
        ivMode.setImageResource(getModeImageRes(position));
        return view;
    }

    private int getModeImageRes(int position) {
        switch (position) {
            case MODE_FREE:
                return R.drawable.word_vi;
            case MODE_FIRST_LETTER:
                return R.drawable.abbr_vi;
            case MODE_NAME:
                return R.drawable.name_vi;
        }
        return 0;
    }
}
