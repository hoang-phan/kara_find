package vn.hoangphan.karafind.net;

import retrofit.Call;
import retrofit.http.GET;
import vn.hoangphan.karafind.models.net.DataLinksResponse;

/**
 * Created by eastagile-tc on 1/12/16.
 */
public interface KaraAPI {

    @GET("/data_links")
    Call<DataLinksResponse> getDataLinks();
}
