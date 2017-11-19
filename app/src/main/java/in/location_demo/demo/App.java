package in.location_demo.demo;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vishal on 19-11-2017.
 */

public class App extends Application {
    static App mInstance;
    public Retrofit mRetrofit;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();
        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl("https://maps.googleapis.com").build();
    }
    public static App getInstance(){
        return mInstance;
    }
}
