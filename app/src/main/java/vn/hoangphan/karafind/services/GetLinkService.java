package vn.hoangphan.karafind.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import vn.hoangphan.karafind.db.DatabaseHelper;
import vn.hoangphan.karafind.models.net.DataLinksResponse;
import vn.hoangphan.karafind.net.APIService;
import vn.hoangphan.karafind.utils.Constants;
import vn.hoangphan.karafind.utils.PreferenceUtils;

/**
 * Created by Hoang Phan on 1/12/2016.
 */
public class GetLinkService extends IntentService {
    public GetLinkService() {
        super("GetLinkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Call<DataLinksResponse> call = APIService.getInstance().getDataLinks();
        call.enqueue(new Callback<DataLinksResponse>() {
            @Override
            public void onResponse(Response<DataLinksResponse> response, Retrofit retrofit) {
                if (response != null && response.body() != null) {
                    Log.d("Retrofit: ", response.body().getDataLinks().toString());
                    long time = System.currentTimeMillis();
                    DatabaseHelper.getInstance().insertDataLinks(response.body().getDataLinks());
                    Log.d("Insert Data Links time", (System.currentTimeMillis() - time) + " milliseconds");
                    PreferenceUtils.getInstance().saveConfig(Constants.LAST_FETCHED_AT, System.currentTimeMillis());
                    Intent intent = new Intent(Constants.INTENT_GET_DATA_LINKS_COMPLETED);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }
}
