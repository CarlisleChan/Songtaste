package com.carlisle.songtaste.cmpts.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.carlisle.songtaste.cmpts.events.NetworkTypeChangedEvent;
import com.carlisle.songtaste.cmpts.services.MusicService;

import de.greenrobot.event.EventBus;

/**
 * Created by chengxin on 4/17/15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    private MusicService musicService;
    public static final int DISCONNECT = -1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        Log.w("songtaste", "Network Type Changed " + info);
        if (info != null && info.getState() == NetworkInfo.State.CONNECTED) {
            EventBus.getDefault().post(new NetworkTypeChangedEvent(info.getType()));
        } else {
            Log.w("songtaste", "wifi is disconnected");
            Log.d("songtaste", "pause playing");
            EventBus.getDefault().post(new NetworkTypeChangedEvent(DISCONNECT));
        }
    }

    public void register(MusicService musicService) {
        this.musicService = musicService;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.musicService.registerReceiver(this, intentFilter);
    }

    public void unRegister() {
        musicService.unregisterReceiver(this);
    }
}
