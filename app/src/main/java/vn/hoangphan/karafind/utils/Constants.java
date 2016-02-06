package vn.hoangphan.karafind.utils;

/**
 * Created by eastagile-tc on 1/12/16.
 */
public interface Constants {
    String API_ENDPOINT = "http://api-karaoke-db.herokuapp.com";
    String INTENT_GET_DATA_LINKS_COMPLETED = "android.intent.action.GET_DATA_LINKS_COMPLETED";
    String INTENT_UPDATED_COMPLETED = "android.intent.action.UPDATED_COMPLETED";
    String INTENT_FAVORITE = "android.intent.action.INTENT_FAVORITE";
    String INTENT_AUTO_UPDATE_ON = "android.intent.action.INTENT_AUTO_UPDATE_ON";
    String LAST_FETCHED_AT = "last_fetched_at";
    String PREFERRED_LANGUAGE = "preferred_lnguage";
    String MODE = "mode";
    String TYPE = "type";
    String VOL_LABEL = "vol_label";

    String TYPE_AR5 = "Arirang 5";
    String TYPE_MSC = "Music Core";
    String TYPE_KTV = "Viet KTV";
    String TYPE_CAL = "California";
    String TYPE_ARE = "Arirang English";

    String[] ALL_TYPES = { TYPE_AR5, TYPE_MSC, TYPE_KTV, TYPE_CAL, TYPE_ARE };

    int MODE_FREE = 0;
    int MODE_ABBR = 1;

    String SONG_ID = "song_id";
    String SONG_NAME = "song_name";
    String SONG_AUTHOR = "song_author";
    String SONG_LYRIC = "song_lyric";
    String SONG_FAVORITE = "song_favorite";
    String AUTO_UPDATE = "auto_update";

    String LOCALE_VI = "vi";
    String LOCALE_EN = "en";
}
