package bike.rapido.rapido_test.view;

import com.google.gson.JsonObject;

import rx.Observable;

/**
 * Created by Dell 3450 on 7/12/2017.
 */

public interface PlacesViewInterface {
    void onCompleted();
    void onError(String message);
    void onResults(JsonObject results, int inputFrom);
    Observable<JsonObject> getPlaces(String url);
}
