package in.location_demo.demo.model;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Dell 3450 on 7/12/2017.
 */

public interface ApiService {
    @GET
    Observable<JsonObject> request(@Url String url);
}
