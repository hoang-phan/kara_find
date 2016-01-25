package vn.hoangphan.karafind.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.Locale;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.LanguageUtils;
import vn.hoangphan.karafind.utils.PreferenceUtils;

/**
 * Created by Hoang Phan on 1/17/2016.
 */
public class SettingsFragment extends Fragment {
    private Switch mSwAutoUpdate;
    private Spinner mSpnLanguage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mSwAutoUpdate = (Switch) view.findViewById(R.id.sw_auto_update);
        mSpnLanguage = (Spinner) view.findViewById(R.id.spn_language);
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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnLanguage.setAdapter(adapter);
        mSpnLanguage.setSelection(PreferenceUtils.getInstance().getConfigString(Constants.PREFERRED_LANGUAGE) == "en" ? 1 : 0);

        mSpnLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String language = languageFromSelect(position);
                String prevLanguage = PreferenceUtils.getInstance().getConfigString(Constants.PREFERRED_LANGUAGE);
                if (!language.equals(prevLanguage)) {
                    PreferenceUtils.getInstance().saveConfig(Constants.PREFERRED_LANGUAGE, language);
                    LanguageUtils.getInstance().changeLanguage(new Locale(language));
                    getActivity().recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String languageFromSelect(int position) {
        switch (position) {
            case 0:
                return "vi";
            case 1:
                return "en";
        }
        return "";
    }
}
