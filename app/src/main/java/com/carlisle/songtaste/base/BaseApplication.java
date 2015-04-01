package com.carlisle.songtaste.base;

import android.app.Application;

import com.carlisle.songtaste.cmpts.services.MusicService;
import com.facebook.stetho.Stetho;

/**
 * Created by chengxin on 2/13/15.
 */
public class BaseApplication extends Application {
    private static BaseApplication instance;

    //Service reference and flags.
    private MusicService mService;
    private boolean mIsServiceRunning = false;

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    public boolean isServiceRunning() {
        return mIsServiceRunning;
    }

    public void setIsServiceRunning(boolean mIsServiceRunning) {
        this.mIsServiceRunning = mIsServiceRunning;
    }

    public MusicService getService() {
        return mService;
    }

    public void setService(MusicService mService) {
        this.mService = mService;
    }
}
