package com.carlisle.songtaste.base;

import android.app.Application;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.carlisle.songtaste.cmpts.crash.CrashHandler;
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

//        if (BuildConfig.DEBUG) {
//            AVAnalytics.setAnalyticsEnabled(false);
//        }

        AVOSCloud.initialize(this, "jvv9g0dd7mo59jkwtqmtxp9s4777bd9m4la2fkzzgc8mhb6p", "w4e9u2h85n0q73j9i9r7m7f0fr6rdhvr4nrhf05liazbqbgp");
        AVAnalytics.enableCrashReport(this, true);

        // collect crash
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

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
