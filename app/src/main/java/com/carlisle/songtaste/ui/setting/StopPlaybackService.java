package com.carlisle.songtaste.ui.setting;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.carlisle.songtaste.cmpts.events.ExitEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by carlisle on 4/2/15.
 */
public class StopPlaybackService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().post(new ExitEvent());
        stopSelf();
    }
}
