package vn.hoangphan.karafind.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vn.hoangphan.karafind.R;
import vn.hoangphan.karafind.adapters.ModesAdapter;
import vn.hoangphan.karafind.adapters.TypesAdapter;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.models.Song;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.LanguageUtils;
import vn.hoangphan.karafind.utils.PagerUtils;
import vn.hoangphan.karafind.utils.PreferenceUtils;

/**
 * Created by Hoang Phan on 1/12/2016.
 */
public class SearchFragment extends BaseSongsFragment {
    private static final int REQUEST_CODE = 1234;

    private EditText mEtSearch;
    private ImageView mIcVoice;
    private ImageView mIcAdvance;
    private ImageView mIcClear;
    private ModesAdapter mModesAdapter;
    private TypesAdapter mTypesAdapter;
    private PopupWindow mPopupSearch;
    private ListView mLvModes;
    private ListView mLvTypes;
    private Button mBtnToUpdate;
    private LinearLayout mLyContentNone;
    private Thread mDatabaseThread;
    private List<Runnable> mNotifyChangeRunners;
    private PackageManager mPackageManager;
    private boolean isRunning = false;

    @Override
    protected void populateData() {
        if (mDatabaseThread != null && mDatabaseThread.isAlive()) {
            mDatabaseThread.interrupt();
        }

        final String filter = mEtSearch.getText().toString();
        mDatabaseThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Song> dataList = getSongs(filter);
                mSongAdapter.setSongs(dataList);
                mNotifyChangeRunners.add(new Runnable() {
                    @Override
                    public void run() {
                        isRunning = true;
                        if (dataList.isEmpty()) {
                            mRvSongs.setVisibility(View.GONE);
                            mLyContentNone.setVisibility(View.VISIBLE);
                        } else {
                            mRvSongs.setVisibility(View.VISIBLE);
                            mLyContentNone.setVisibility(View.GONE);
                        }
                        mSongAdapter.notifyDataSetChanged();
                    }
                });
                triggerRun();
            }
        });
        mDatabaseThread.start();

        if (TextUtils.isEmpty(filter)){
            mBtnToUpdate.setVisibility(View.VISIBLE);
        } else {
            mBtnToUpdate.setVisibility(View.GONE);
        }
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
        mNotifyChangeRunners = new ArrayList<>();
        mEtSearch = (EditText) view.findViewById(R.id.et_search);
        mIcVoice = (ImageView) view.findViewById(R.id.ic_voice);
        mIcAdvance = (ImageView) view.findViewById(R.id.ic_advance);
        mIcClear = (ImageView) view.findViewById(R.id.ic_clear);
        mLyContentNone = (LinearLayout) view.findViewById(R.id.ly_content_none);
        mBtnToUpdate = (Button) view.findViewById(R.id.btn_to_update);

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
        mPackageManager = getActivity().getPackageManager();
    }

    @Override
    protected void extraPopulate(View view) {
        getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                isRunning = false;
            }
        });

        mIcVoice.setOnClickListener(new View.OnClickListener() {
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
        mIcClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtSearch.setText("");
            }
        });
        mBtnToUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagerUtils.getInstance().changePage(1);
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
        List<ResolveInfo> infos = mPackageManager.queryIntentServices(new Intent(RecognitionService.SERVICE_INTERFACE), 0);
        if (infos.isEmpty()) {
            Toast.makeText(getActivity(), R.string.no_service_available, Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi");
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.say_words));
            startActivityForResult(intent, REQUEST_CODE);
        }
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

    private List<Song> getSongs(String filter) {
        if (TextUtils.isEmpty(filter)) {
            return DatabaseHelper.getInstance().allSongs();
        }
        String transformed = LanguageUtils.translateToUtf(filter);
        switch ((int)PreferenceUtils.getInstance().getConfigLong(Constants.MODE)) {
            case Constants.MODE_FREE:
                return DatabaseHelper.getInstance().songsMatch(transformed);
            case Constants.MODE_ABBR:
                return DatabaseHelper.getInstance().getSongsWithFirstLetters(transformed);
        }
        return new ArrayList<>();
    }

    private void triggerRun() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        if (!mNotifyChangeRunners.isEmpty()) {
            getActivity().runOnUiThread(mNotifyChangeRunners.get(0));
            mNotifyChangeRunners.clear();
        }
    }
}
