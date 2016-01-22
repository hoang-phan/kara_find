package vn.hoangphan.karafind.utils;

/**
 * Created by eastagile-tc on 1/12/16.
 */
public interface Constants {
    String API_ENDPOINT = "http://api-karaoke-db.herokuapp.com";
    String INTENT_GET_DATA_LINKS_COMPLETED = "android.intent.action.GET_DATA_LINKS_COMPLETED";
    String LAST_FETCHED_AT = "last_fetched_at";
    String PREFERRED_LANGUAGE = "preferred_lnguage";
    String MODE = "mode";
    String TYPE = "type";

    int MODE_FREE = 0;
    int MODE_ABBR = 1;

    int TYPE_ARIRANG_5 = 0;
    int TYPE_MUSIC_CORE = 1;
    int TYPE_VIETKTV = 2;
    int TYPE_CALI = 3;
}
