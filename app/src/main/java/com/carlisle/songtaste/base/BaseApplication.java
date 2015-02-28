package com.carlisle.songtaste.base;

import android.app.Application;

import com.carlisle.songtaste.services.AudioPlaybackService;

/**
 * Created by chengxin on 2/13/15.
 */
public class BaseApplication extends Application {
    private static BaseApplication instance;

    //Service reference and flags.
    private AudioPlaybackService mService;
    private boolean mIsServiceRunning = false;

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public boolean isServiceRunning() {
        return mIsServiceRunning;
    }

    public void setIsServiceRunning(boolean mIsServiceRunning) {
        this.mIsServiceRunning = mIsServiceRunning;
    }

    public AudioPlaybackService getService() {
        return mService;
    }

    public void setService(AudioPlaybackService mService) {
        this.mService = mService;
    }
}
