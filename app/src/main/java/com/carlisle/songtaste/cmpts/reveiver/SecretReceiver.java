package com.carlisle.songtaste.cmpts.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.carlisle.songtaste.utils.PreferencesHelper;

/**
 * Created by chengxin on 4/15/15.
 */
public class SecretReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals("android.provider.Telephony.SECRET_CODE")) {
                PreferencesHelper.getInstance(context).putBoolean(PreferencesHelper.DEVELOPER_OPTIONS, true);
            }
        } catch (Exception e) {

        }
    }
}
