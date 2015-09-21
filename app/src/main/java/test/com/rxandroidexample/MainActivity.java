package test.com.rxandroidexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();
    private Subscription mSubscription = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.d(TAG + " %s ", "inside onCreate()");
        ButterKnife.inject(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @OnClick(R.id.btn_custom_observable)
    public void createCustomObservable(View view){
        createCustomObservable();
    }

    @OnClick(R.id.btn_buffering)
    public void inputBuffering(View view){

    }


    private void createCustomObservable(){

        Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
        Timber.d(TAG + " %s ", "doing some heavy task here. ");

        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> observer) {
                try {
                    if (!observer.isUnsubscribed()) {

                        for (int i = 1; i < 5; i++) {
                            Timber.d(TAG +" %s ", "doing some heavy task here. "+(i+1));

                            // notifying progress event
                            observer.onNext(i);
                        }

                        //notify execution finished.
                        observer.onCompleted();
                    }
                } catch (Exception e) {
                    //throw error.
                    observer.onError(e);
                }
            }
        });


        observable.subscribeOn(Schedulers.io());
        observable.observeOn(AndroidSchedulers.mainThread());

        mSubscription = observable.subscribe(new Subscriber<Integer>() {
            @Override
            public void onNext(Integer item) {

                //todo update progress  view here
                Timber.d(TAG + " Next: %s ", "" + item);
            }

            @Override
            public void onError(Throwable error) {
                //todo handle error
                Timber.e(TAG + " Error: %s ", "" + error.getMessage());
            }

            @Override
            public void onCompleted() {
                Timber.i(TAG + " %s ", "Operation complete.");
            }
        });

       // observable.startWith(0);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);

        if(mSubscription != null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }

}