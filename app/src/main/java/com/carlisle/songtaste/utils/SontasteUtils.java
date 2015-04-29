package com.carlisle.songtaste.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by chengxin on 4/29/15.
 */
public class SontasteUtils {

    public static  void getUserIdFromSontaste(Context context) {
        String fromFile = " data/data/duomi.app.activity/shared_prefs";
        String toFile = " data/data/com.carlisle.songtaste/";
        String userId = null;

        if (ShellUtils.checkRootPermission()) {
            String[] cpFile = new String[]{"mount -o rw,remount /system", "cp -r" + fromFile + toFile};
            ShellUtils.CommandResult cpResult = ShellUtils.execCommand(cpFile, true);

            String[] changeMod = new String[]{"mount -o rw,remount /system", "chmod 664 data/data/com.carlisle.songtaste/shared_prefs/USER_DATA.xml"};
            ShellUtils.CommandResult chResult = ShellUtils.execCommand(changeMod, true);

            SharedPreferences sharedPreferences = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
            String userID = sharedPreferences.getString("userID", "");
            Log.d("==>", "userId:{" + userID + "}");

            if (TextUtils.isEmpty(userId)) {
                Toast.makeText(context, "获取失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "获取成功", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "手机未 root ", Toast.LENGTH_SHORT).show();
        }
    }
}
