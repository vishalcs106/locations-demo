package in.location_demo.demo.presenter;

import com.google.gson.JsonObject;

import in.location_demo.demo.view.RoutesViewInterface;
import rx.Observer;

/**
 * Created by Dell 3450 on 7/13/2017.
 */

public class RoutesPresenter extends BasePresenter implements Observer<JsonObject> {
    RoutesViewInterface mViewInterface;
    public RoutesPresenter(RoutesViewInterface viewInterface){
        mViewInterface = viewInterface;
    }
    @Override
    public void onCompleted() {
        mViewInterface.onRoutesCompleted();
    }

    @Override
    public void onError(Throwable e) {
        mViewInterface.onRoutesError(e.getMessage());
    }

    @Override
    public void onNext(JsonObject jsonObject) {
        mViewInterface.onRoutesResults(jsonObject);
    }
    public void getRoutes(String url){
        unSubscribeAll();
        subscribe(mViewInterface.getRoutes(url), RoutesPresenter.this);
    }
}
