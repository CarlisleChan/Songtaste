package com.carlisle.songtaste.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.github.stephanenicolas.loglifecycle.LogLifeCycle;

/**
 * Created by chengxin on 2/13/15.
 */
@LogLifeCycle
public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
