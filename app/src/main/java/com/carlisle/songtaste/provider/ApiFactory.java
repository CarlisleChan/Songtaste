package com.carlisle.songtaste.provider;

import android.util.Log;

import retrofit.converter.Converter;

/**
 * Created by chengxin on 2/26/15.
 */
public class ApiFactory {
    private static SongtasteApi songtasteApi;

    public synchronized static SongtasteApi getSongtasteApi(Converter converter) {
        if (songtasteApi == null) {
            Log.d("ApiFactory", "-----------getSongtasteApi");
            songtasteApi = RetrofitFactory.getRestAdapter(converter).create(SongtasteApi.class);
        }
        return songtasteApi;
    }
}
