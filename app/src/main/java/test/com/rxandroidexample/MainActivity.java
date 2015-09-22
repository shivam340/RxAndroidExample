package test.com.rxandroidexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewClickEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();
    private Subscription mSubscription = null;
    private Subscription mBufferingSubscription = null;


    @InjectView(R.id.btn_buffering)
    protected Button inputBuffering;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.d(TAG + " %s ", "inside onCreate()");
        ButterKnife.inject(this);


        mBufferingSubscription = inputBuffering();


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


    public Subscription inputBuffering(){

        return RxView.clickEvents(inputBuffering)
                .map(new Func1<ViewClickEvent, Integer>() {
                    @Override
                    public Integer call(ViewClickEvent onClickEvent) {
                        Timber.d("--------- GOT A TAP");
                        Timber.d("GOT A TAP");
                        return 1;
                    }
                })
                .buffer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Integer>>() {

                    @Override
                    public void onCompleted() {
                        // fyi: you'll never reach here
                        Timber.d("----- onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "--------- Woops on error!");

                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        Timber.d("--------- onNext");
                        if (integers.size() > 0) {
                            Timber.d(String.format("%d taps", integers.size()));
                        } else {
                            Timber.d("--------- No taps received ");
                        }
                    }
                });


    }


    private void createCustomObservable(){

        Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
        Timber.d(TAG + " %s ", "doing some heavy task here. ");


        //OnSubscribe is interface and has method call , when we call subscribe on observable
        // this method (call) gets invoked.

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

        if(mBufferingSubscription != null && !mBufferingSubscription.isUnsubscribed()){
            mBufferingSubscription.unsubscribe();
        }

    }

}