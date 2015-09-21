package test.com.rxandroidexample;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by shivam on 9/21/15.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
