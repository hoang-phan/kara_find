package vn.hoangphan.karafind.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import vn.hoangphan.karafind.utils.LanguageUtils;
import vn.hoangphan.karafind.utils.OnSongDetailClick;

/**
 * Created by Hoang Phan on 1/12/2016.
 */
public class SearchFragment extends Fragment {
    private static final int REQUEST_CODE = 1234;

    private EditText mEtSearch;
    private RecyclerView mRvSongs;
    private ImageView mIcSearch;
    private ImageView mIcAdvance;
    private SongsAdapter mSongAdapter;
    private ModesAdapter mModesAdapter;
    private TypesAdapter mTypesAdapter;
    private PopupWindow mPopupSong;
    private PopupWindow mPopupSearch;
    private ListView mLvModes;
    private ListView mLvTypes;
    private LayoutInflater mInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mRvSongs = (RecyclerView)view.findViewById(R.id.rv_songs);
        mEtSearch = (EditText) view.findViewById(R.id.et_search);
        mIcSearch = (ImageView)view.findViewById(R.id.ic_search);
        mIcAdvance = (ImageView)view.findViewById(R.id.ic_advance);
        mSongAdapter = new SongsAdapter();
        mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View popupView = mInflater.inflate(R.layout.popup_advance, null);
        mLvModes = (ListView)popupView.findViewById(R.id.lv_modes);
        mLvTypes = (ListView)popupView.findViewById(R.id.lv_types);
        mModesAdapter = new ModesAdapter();
        mTypesAdapter = new TypesAdapter();
        mLvModes.setAdapter(mModesAdapter);
        mLvTypes.setAdapter(mTypesAdapter);
        mPopupSearch = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvSongs.setAdapter(mSongAdapter);
        mRvSongs.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                filterSongs();
            }
        });

        mSongAdapter.setOnSongDetailClick(new OnSongDetailClick() {
            @Override
            public void view(Song song) {

                View popupView = mInflater.inflate(R.layout.popup_song_detail, null);
                TextView tvSongId = (TextView) popupView.findViewById(R.id.tv_song_id_detail);
                TextView tvSongName = (TextView) popupView.findViewById(R.id.tv_song_name_detail);
                TextView tvSongAuthor = (TextView) popupView.findViewById(R.id.tv_song_author_detail);
                TextView tvSongLyric = (TextView) popupView.findViewById(R.id.tv_song_lyric_detail);
                Button btnDismiss = (Button) popupView.findViewById(R.id.btn_dismiss);
                tvSongId.setText(song.getId());
                tvSongName.setText(song.getName());
                tvSongAuthor.setText(song.getAuthor());
                tvSongLyric.setText(song.getLyric());
                mPopupSong = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                btnDismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopupSong.dismiss();
                    }
                });
                mPopupSong.showAsDropDown(mEtSearch, 0, 0);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> infos = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        mIcSearch.setEnabled(infos.size() > 0);
        filterSongs();
    }

    private void filterSongs() {
        String filter = LanguageUtils.translateToUtf(mEtSearch.getText().toString());
        if (TextUtils.isEmpty(filter)){
            mSongAdapter.setSongs(DatabaseHelper.getInstance().allSongs());
        } else {
            mSongAdapter.setSongs(DatabaseHelper.getInstance().songsMatch(filter));
        }
        mSongAdapter.notifyDataSetChanged();
    }

    private void togglePopupSearch() {
        if (mPopupSearch.isShowing()) {
            mPopupSearch.dismiss();
        } else {
            mPopupSearch.showAsDropDown(mIcAdvance, 0, 0);
        }
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói tên bài hát hoặc một phần lời bài hát...");
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
        if (mPopupSong != null && mPopupSong.isShowing()) {
            mPopupSong.dismiss();
        }
        if (mPopupSearch != null && mPopupSearch.isShowing()) {
            mPopupSearch.dismiss();
        }
        super.onDestroy();
    }
}
