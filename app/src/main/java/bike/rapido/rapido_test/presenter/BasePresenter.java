package bike.rapido.rapido_test.presenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Dell 3450 on 7/12/2017.
 */

public class BasePresenter implements PresenterInterface {
    private CompositeSubscription mCompositeSubscription;

    @Override
    public void onCreate() {
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {
        configureSubscription();
    }

    private CompositeSubscription configureSubscription() {
        if (mCompositeSubscription == null || mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription = new CompositeSubscription();
        }
        return mCompositeSubscription;
    }

    @Override
    public void onDestroy() {
        unSubscribeAll();
    }

    protected void unSubscribeAll() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription.clear();
//            mCompositeSubscription = null;
        }
    }

    protected <F> void subscribe(Observable<F> observable, Observer<F> observer) {
        Subscription subscription = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.computation())
                .subscribe(observer);
        configureSubscription().add(subscription);
    }
}
