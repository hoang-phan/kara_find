package vn.hoangphan.karafind.net;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

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
            Retrofit restAdapter = new Retrofit.Builder().baseUrl(Constants.API_ENDPOINT).addConverterFactory(JacksonConverterFactory.create()).build();
            instance = restAdapter.create(KaraAPI.class);
        } else {
        }
        return instance;
    }
}
