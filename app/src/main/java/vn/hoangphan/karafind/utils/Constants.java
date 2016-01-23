package vn.hoangphan.karafind.utils;

/**
 * Created by eastagile-tc on 1/12/16.
 */
public interface Constants {
    String API_ENDPOINT = "http://api-karaoke-db.herokuapp.com";
    String INTENT_GET_DATA_LINKS_COMPLETED = "android.intent.action.GET_DATA_LINKS_COMPLETED";
    String INTENT_UPDATED_COMPLETED = "android.intent.action.UPDATED_COMPLETED";
    String LAST_FETCHED_AT = "last_fetched_at";
    String PREFERRED_LANGUAGE = "preferred_lnguage";
    String MODE = "mode";
    String TYPE = "type";

    String[] ALL_TYPES = { "Arirang 5", "Music Core", "Viet KTV", "California", "Arirang English" };

    int MODE_FREE = 0;
    int MODE_ABBR = 1;
}
