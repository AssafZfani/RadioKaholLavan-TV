package zfani.assaf.radio_kahol_lavan_tv.model;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIInterface {

    @GET("json")
    Call<JsonObject> getRecentFeed();
}
