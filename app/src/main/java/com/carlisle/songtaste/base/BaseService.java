package com.carlisle.songtaste.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by carlisle on 3/7/15.
 */
public class BaseService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
