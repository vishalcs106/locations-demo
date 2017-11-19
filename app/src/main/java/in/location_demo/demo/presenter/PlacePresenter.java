package in.location_demo.demo.presenter;

import com.google.gson.JsonObject;

import in.location_demo.demo.App;
import in.location_demo.demo.model.ApiService;
import in.location_demo.demo.view.PlacesViewInterface;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        Subscription subscription = App.getInstance().mRetrofit.create(ApiService.class).request(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.computation())
                .subscribe(PlacePresenter.this);
        configureSubscription().add(subscription);

    }
}
