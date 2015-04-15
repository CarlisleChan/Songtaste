package com.carlisle.songtaste.cmpts.reveiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.avos.avoscloud.AVOSCloud;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.ui.main.MainActivity;

import org.json.JSONObject;

public class LeanCloudReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals("com.songtaste.push.action")) {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
                final String message = json.getString("alert");
                Intent resultIntent = new Intent(AVOSCloud.applicationContext, MainActivity.class);
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(AVOSCloud.applicationContext, 0, resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(AVOSCloud.applicationContext)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(
                                        AVOSCloud.applicationContext.getResources().getString(R.string.app_name))
                                .setContentText(message)
                                .setTicker(message);
                mBuilder.setContentIntent(pendingIntent);
                mBuilder.setAutoCancel(true);

                int mNotificationId = 10086;
                NotificationManager mNotifyMgr =
                        (NotificationManager) AVOSCloud.applicationContext
                                .getSystemService(
                                        Context.NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }
        } catch (Exception e) {

        }
    }
}
