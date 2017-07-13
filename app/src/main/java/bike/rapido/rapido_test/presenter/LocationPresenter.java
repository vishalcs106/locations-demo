package bike.rapido.rapido_test.presenter;

import com.google.gson.JsonObject;

import bike.rapido.rapido_test.view.LocationViewInterface;
import rx.Observer;

/**
 * Created by Dell 3450 on 7/12/2017.
 */

public class LocationPresenter extends BasePresenter implements Observer<JsonObject> {
    LocationViewInterface mViewInterface;
    int mInputFrom = 0;

    public LocationPresenter(LocationViewInterface locationViewInterface) {
        mViewInterface = locationViewInterface;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(JsonObject jsonObject) {
        mViewInterface.onLocationResults(jsonObject, mInputFrom);
    }

    public void getLocation(String url, int inputFrom){
        mInputFrom = inputFrom;
        unSubscribeAll();
        subscribe(mViewInterface.getLocation(url), LocationPresenter.this);

    }
}
