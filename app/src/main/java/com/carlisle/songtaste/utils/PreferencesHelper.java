package com.carlisle.songtaste.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.carlisle.songtaste.cmpts.modle.User;

/**
 * Created by carlisle on 3/22/15.
 */
public class PreferencesHelper {
    private static PreferencesHelper instance;

    public static final String KEY_USER = User.class.getSimpleName();
    public static final String PLAY_ONLY_WIFI = "play_only_wifi";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public static PreferencesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesHelper();
            instance.sharedPreferences = context.getSharedPreferences(PreferencesHelper.class.getName(), Context.MODE_PRIVATE);
            instance.editor = instance.sharedPreferences.edit();
        }
        instance.context = context;

        return instance;
    }

    public String getUID() {
        return "6973651";
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void doTransaction(Runnable runnable) {
        runnable.run();
        editor.commit();
    }

    public void clearData() {
        editor.clear();
        editor.commit();
    }

    public void logout() {
        doTransaction(new Runnable() {
            @Override
            public void run() {
                editor.remove(KEY_USER);
            }
        });
    }

    public void setPlayOnlyWifi(boolean playOnlyWifi) {
        putBoolean(PLAY_ONLY_WIFI, playOnlyWifi);
    }

    public boolean isPlayOnlyWifi() {
        return getBoolean(PLAY_ONLY_WIFI, false);
    }
}
