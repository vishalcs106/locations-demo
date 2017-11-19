package in.location_demo.demo.view;

import com.google.gson.JsonObject;

import rx.Observable;

/**
 * Created by Dell 3450 on 7/13/2017.
 */

public interface RoutesViewInterface {
    void onRoutesCompleted();
    void onRoutesError(String message);
    void onRoutesResults(JsonObject results);
    Observable<JsonObject> getRoutes(String url);
}
