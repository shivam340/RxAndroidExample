package test.com.rxandroidexample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }
}
