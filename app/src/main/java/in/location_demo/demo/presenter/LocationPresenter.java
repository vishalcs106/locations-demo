package in.location_demo.demo.presenter;

import com.google.gson.JsonObject;

import in.location_demo.demo.App;
import in.location_demo.demo.model.ApiService;
import in.location_demo.demo.view.LocationViewInterface;
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
        subscribe(App.getInstance().mRetrofit.create(ApiService.class).request(url), LocationPresenter.this);

    }
}
