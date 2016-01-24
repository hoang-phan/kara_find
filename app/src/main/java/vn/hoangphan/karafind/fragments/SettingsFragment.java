package vn.hoangphan.karafind.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.PreferenceUtils;

/**
 * Created by Hoang Phan on 1/17/2016.
 */
public class SettingsFragment extends Fragment {
    private Switch mSwAutoUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mSwAutoUpdate = (Switch) view.findViewById(R.id.sw_auto_update);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwAutoUpdate.setChecked(PreferenceUtils.getInstance().getConfigLong(Constants.AUTO_UPDATE) == 1);
        mSwAutoUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtils.getInstance().saveConfig(Constants.AUTO_UPDATE, isChecked ? 1 : 0);
            }
        });
    }
}
