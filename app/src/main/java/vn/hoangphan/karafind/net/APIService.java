package vn.hoangphan.karafind.net;

import android.util.Log;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;
import vn.hoangphan.karafind.utils.Constants;

/**
 * Created by eastagile-tc on 1/12/16.
 */
public class APIService {
    private static KaraAPI instance;

    public static KaraAPI getInstance() {
        if (instance == null) {
            Log.e("instance", "new instance");
            Retrofit restAdapter = new Retrofit.Builder().baseUrl(Constants.API_ENDPOINT).addConverterFactory(JacksonConverterFactory.create()).build();
            instance = restAdapter.create(KaraAPI.class);
        } else {
            Log.e("instance", "old instance");
        }
        return instance;
    }
}
