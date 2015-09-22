package test.com.rxandroidexample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func2;
import timber.log.Timber;

/**
 * Created by shivam on 9/22/15.
 */
public class LoginActivity extends Activity {


    @InjectView(R.id.btn_submit)
    Button submit;

    @InjectView(R.id.editText_email)
    EditText mEditTextEmail;

    @InjectView(R.id.editText_password)
    EditText mEditTextPassword;

    private Observable<CharSequence> emailChangeObservable;
    private Observable<CharSequence> passwordChangeObservable;
    private Subscription mSubscription = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);


        emailChangeObservable = RxTextView.textChanges(mEditTextEmail).skip(1);
        passwordChangeObservable = RxTextView.textChanges(mEditTextPassword).skip(1);

        validateInput();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);


        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }

    }


    //reference - https://github.com/kaushikgopal/RxJava-Android-Samples/blob/master/app/src/main
    // /java/com/morihacky/android/rxjava/fragments/FormValidationCombineLatestFragment.java
    private void validateInput() {

        mSubscription = Observable.combineLatest(emailChangeObservable,
                passwordChangeObservable,
                new Func2<CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence userName,
                                        CharSequence userPassword) {

                        boolean isNameValid = false;
                        boolean isPasswordValid = false;

                        if (userName.toString().trim().length() > 0) {
                            isNameValid = true;
                        }

                        if (!isNameValid) {
                            mEditTextEmail.setError("Invalid Email!");
                        }


                        if (userPassword.toString().trim().length() > 10) {
                            isPasswordValid = true;
                        }

                        if (!isPasswordValid) {
                            mEditTextPassword.setError("Invalid Password!");
                        }

                        return isNameValid && isPasswordValid;

                    }
                })
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "there was an error");
                    }

                    @Override
                    public void onNext(Boolean formValid) {
                        if (formValid) {
                            submit.setBackgroundColor(getResources().getColor(android.R.color
                                    .holo_green_light));
                        } else {
                            submit.setBackgroundColor(getResources().getColor(android.R.color
                                    .holo_red_light));
                        }
                    }
                });
    }
}
