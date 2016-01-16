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
                    DatabaseHelper.getInstance().insertDataLinks(response.body().getDataLinks());
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
