package com.carlisle.songtaste.base;

import android.app.Application;

/**
 * Created by chengxin on 2/13/15.
 */
public class BaseApplication extends Application {
    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
