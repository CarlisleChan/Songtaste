package com.carlisle.songtaste.cmpts.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.carlisle.songtaste.cmpts.events.PlayerReceivingEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by chengxin on 4/17/15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        Log.w("songtaste", "Network Type Changed " + info);
        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI && info.getState() == NetworkInfo.State.CONNECTED) {
            Log.d("songtaste", "wifi is connected");
        } else {
            Log.w("songtaste", "wifi is disconnected");
            Log.d("songtaste", "pause playing");
            EventBus.getDefault().post(new PlayerReceivingEvent(PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_PAUSE));
        }
    }
}
