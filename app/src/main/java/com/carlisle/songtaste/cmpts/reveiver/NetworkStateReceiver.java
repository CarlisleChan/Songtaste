package com.carlisle.songtaste.cmpts.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by chengxin on 4/17/15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        Log.w("yuedu", "Network Type Changed " + info);
        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI && info.getState() == NetworkInfo.State.CONNECTED) {
            Log.d("yuedu", "wifi is connected");
        } else {
            Log.w("yuedu", "wifi is disconnected");
            Log.d("yuedu", "pause playing");
//            pausePlay();
        }
    }
}
