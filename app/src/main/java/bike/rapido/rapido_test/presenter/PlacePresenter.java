package bike.rapido.rapido_test.presenter;

import com.google.gson.JsonObject;

import bike.rapido.rapido_test.view.PlacesViewInterface;
import rx.Observer;

/**
 * Created by Dell 3450 on 7/12/2017.
 */

public class PlacePresenter extends BasePresenter implements Observer<JsonObject> {
    PlacesViewInterface mViewInterface;
    int mInputFrom = 0;
    public PlacePresenter(PlacesViewInterface viewInterface){
        mViewInterface = viewInterface;
    }
    @Override
    public void onCompleted() {
        mViewInterface.onCompleted();
    }

    @Override
    public void onError(Throwable e) {
        mViewInterface.onError(e.getMessage());
    }

    @Override
    public void onNext(JsonObject jsonObject) {
        mViewInterface.onResults(jsonObject, mInputFrom);
    }

    public void getPlaces(String url, int inputFrom){
        mInputFrom = inputFrom;
        unSubscribeAll();
        subscribe(mViewInterface.getPlaces(url), PlacePresenter.this);

    }
}
