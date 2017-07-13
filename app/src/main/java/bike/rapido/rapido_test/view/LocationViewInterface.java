package bike.rapido.rapido_test.view;

import com.google.gson.JsonObject;

import rx.Observable;

/**
 * Created by Dell 3450 on 7/12/2017.
 */

public interface LocationViewInterface {
    void onLocationCompleted();
    void onLocationError(String message);
    void onLocationResults(JsonObject result, int inputFrom);
    Observable<JsonObject> getLocation(String url);
}
