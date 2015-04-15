package com.carlisle.songtaste.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;

/**
 * Created by chengxin on 4/15/15.
 */
public class SplashActivity extends BaseActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, 2000);
    }

}
