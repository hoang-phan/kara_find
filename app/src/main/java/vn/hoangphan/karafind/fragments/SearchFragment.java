package vn.hoangphan.karafind.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.adapters.ModesAdapter;
import vn.hoangphan.karafind.adapters.SongsAdapter;
import vn.hoangphan.karafind.adapters.TypesAdapter;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.models.Song;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.LanguageUtils;
import vn.hoangphan.karafind.utils.OnSongDetailClick;
import vn.hoangphan.karafind.utils.PreferenceUtils;

/**
 * Created by Hoang Phan on 1/12/2016.
 */
public class SearchFragment extends BaseSongsFragment {
    private static final int REQUEST_CODE = 1234;

    private EditText mEtSearch;
    private ImageView mIcSearch;
    private ImageView mIcAdvance;
    private ModesAdapter mModesAdapter;
    private TypesAdapter mTypesAdapter;
    private PopupWindow mPopupSearch;
    private ListView mLvModes;
    private ListView mLvTypes;

    @Override
    protected void populateData() {
        String filter = mEtSearch.getText().toString();
        if (TextUtils.isEmpty(filter)){
            mSongAdapter.setSongs(DatabaseHelper.getInstance().allSongs());
        } else {
            String transformed = LanguageUtils.translateToUtf(filter);
            switch ((int)PreferenceUtils.getInstance().getConfigLong(Constants.MODE)) {
                case Constants.MODE_FREE:
                    mSongAdapter.setSongs(DatabaseHelper.getInstance().songsMatch(transformed));
                    break;
                case Constants.MODE_ABBR:
                    mSongAdapter.setSongs(DatabaseHelper.getInstance().getSongsWithFirstLetters(transformed));
                    break;
            }
        }
        mSongAdapter.notifyDataSetChanged();
    }

    @Override
    protected String intentIdentifier() {
        return Constants.INTENT_UPDATED_COMPLETED;
    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_search;
    }

    @Override
    protected void extraInit(View view, LayoutInflater inflater) {
        mEtSearch = (EditText) view.findViewById(R.id.et_search);
        mIcSearch = (ImageView) view.findViewById(R.id.ic_search);
        mIcAdvance = (ImageView) view.findViewById(R.id.ic_advance);

        View advancePopupView = inflater.inflate(R.layout.popup_advance, null);
        mLvModes = (ListView)advancePopupView.findViewById(R.id.lv_modes);
        mLvTypes = (ListView)advancePopupView.findViewById(R.id.lv_types);
        mModesAdapter = new ModesAdapter();
        mTypesAdapter = new TypesAdapter();
        mLvModes.setAdapter(mModesAdapter);
        mLvTypes.setAdapter(mTypesAdapter);
        mLvModes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PreferenceUtils.getInstance().saveConfig(Constants.MODE, position);
                mModesAdapter.notifyDataSetChanged();
                if (position == 0) {
                    mEtSearch.setHint(R.string.name_author_or_lyric);
                } else {
                    mEtSearch.setHint(R.string.abbreviate);
                }
                populateData();
            }
        });
        mLvTypes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PreferenceUtils.getInstance().saveConfig(Constants.TYPE, position);
                mTypesAdapter.notifyDataSetChanged();
                populateData();
            }
        });
        mPopupSearch = new PopupWindow(advancePopupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void extraPopulate(View view) {
        mIcSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });
        mIcAdvance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePopupSearch();
            }
        });

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                populateData();
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> infos = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        mIcSearch.setEnabled(infos.size() > 0);
    }

    private void togglePopupSearch() {
        if (mPopupSearch.isShowing()) {
            mPopupSearch.dismiss();
        } else {
            mPopupSearch.setFocusable(true);
            mPopupSearch.setOutsideTouchable(true);
            mPopupSearch.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mPopupSearch.showAsDropDown(mIcAdvance, 0, 0);
        }
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.say_words));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                mEtSearch.setText(matches.get(0));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (mPopupSearch != null && mPopupSearch.isShowing()) {
            mPopupSearch.dismiss();
        }
        super.onDestroy();
    }
}
