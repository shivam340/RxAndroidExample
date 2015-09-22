package test.com.rxandroidexample;

import android.app.Activity;
import android.content.Intent;
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
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();



    /*
    private Subscription mSubscription = null;
    private Subscription mBufferingSubscription = null;
    private Subscription mBufferingMapSubscription = null;
    private Subscription mBufferingFlatMapSubscription = null;
    */

    private CompositeSubscription compositeSubscription = null;


    @InjectView(R.id.btn_buffering)
    protected Button inputBuffering;

    @InjectView(R.id.btn_buffering_map)
    protected Button inputBufferingMap;

    @InjectView(R.id.btn_buffering_flat_map)
    protected Button inputBufferingFlatMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.d(TAG + " %s ", "inside onCreate()");
        ButterKnife.inject(this);

        compositeSubscription = new CompositeSubscription();
        compositeSubscription.add(inputBuffering());
        compositeSubscription.add(inputBufferingWithMap());
        compositeSubscription.add(inputBufferingWithFlatMap());


        /*mBufferingSubscription = inputBuffering();
        mBufferingMapSubscription = inputBufferingWithMap();
        mBufferingFlatMapSubscription = inputBufferingWithFlatMap();
        */
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
    public void createCustomObservable(View view) {
        createCustomObservable();
    }


    public Subscription inputBuffering() {


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


    private void createCustomObservable() {

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
                            Timber.d(TAG + " %s ", "doing some heavy task here. " + (i + 1));

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

        compositeSubscription.add(observable.subscribe(new Subscriber<Integer>() {
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
        }));

        // observable.startWith(0);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);

        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }

        /*if (mBufferingSubscription != null && !mBufferingSubscription.isUnsubscribed()) {
            mBufferingSubscription.unsubscribe();
        }


        if (mBufferingMapSubscription != null && !mBufferingMapSubscription.isUnsubscribed()) {
            mBufferingMapSubscription.unsubscribe();
        }

        if (mBufferingFlatMapSubscription != null && !mBufferingFlatMapSubscription
        .isUnsubscribed()) {
            mBufferingFlatMapSubscription.unsubscribe();
        }*/

    }


    //Map returns an object of type T.
    //Map does not have to emit items of the same type as the source Observable.

    public Subscription inputBufferingWithMap() {


        return RxView.clickEvents(inputBufferingMap)
                .map(new Func1<ViewClickEvent, Integer>() {
                    @Override
                    public Integer call(ViewClickEvent onClickEvent) {
                        Timber.d("--------- GOT A TAP");
                        Timber.d("GOT A TAP");
                        return 1;
                    }
                })
                .buffer(2, TimeUnit.SECONDS)
                .map(new Func1<List<Integer>, Integer>() {
                    @Override
                    public Integer call(List<Integer> integers) {
                        return integers.size();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {

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
                    public void onNext(Integer count) {
                        Timber.d("--------- onNext");
                        Timber.d(String.format("%d taps", count));
                    }
                });

    }


    //FlatMap returns an Observable<T>.
    //that is why FlatMap is recommended if you plan to make an asynchronous call inside the method
    public Subscription inputBufferingWithFlatMap() {

        return RxView.clickEvents(inputBufferingFlatMap)
                .map(new Func1<ViewClickEvent, Integer>() {
                    @Override
                    public Integer call(ViewClickEvent onClickEvent) {
                        Timber.d("--------- GOT A TAP");
                        Timber.d("GOT A TAP");
                        return 1;
                    }
                })
                .buffer(2, TimeUnit.SECONDS)
                .flatMap(new Func1<List<Integer>, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(List<Integer> integers) {
                        return Observable.just(integers.size());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {

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
                    public void onNext(Integer count) {
                        Timber.d("--------- onNext");
                        Timber.d(String.format("%d taps", count));
                    }
                });

    }



    @OnClick(R.id.btn_combine_latest)
    protected void combineLatest(){
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }


}