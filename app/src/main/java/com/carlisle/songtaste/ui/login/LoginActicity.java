package com.carlisle.songtaste.ui.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by carlisle on 3/27/15.
 */
public class LoginActicity extends BaseActivity {
    @InjectView(R.id.et_username)
    EditText username;
    @InjectView(R.id.et_password)
    EditText password;
    @InjectView(R.id.btn_login)
    Button loginbutton;
    @InjectView(R.id.tv_pass)
    TextView passButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }
}
