package com.carlisle.songtaste.cmpts.provider;

import android.util.Log;

import retrofit.converter.Converter;

/**
 * Created by chengxin on 2/26/15.
 */
public class ApiFactory {
    private SongtasteApi songtasteApi;

    public SongtasteApi getSongtasteApi(Converter converter) {
        if (songtasteApi == null) {
            Log.d("ApiFactory", "-----------getSongtasteApi");
            songtasteApi = RetrofitFactory.getRestAdapter(converter).create(SongtasteApi.class);
        }
        return songtasteApi;
    }
}
