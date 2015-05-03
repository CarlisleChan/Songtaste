package com.carlisle.songtaste.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chengxin on 2/14/15.
 */
public class Utils {

    public static int getSystemVersion() {
		/* 获取当前系统的android版本号 */
        int version = android.os.Build.VERSION.SDK_INT;
        return version;
    }

    /**
     * 用来判断activity是否运行.
     * @param context
     * @param className 判断的activity名字：包名+类名
     * @return true 在运行, false 不在运行
     */
    public static boolean isActivityRunning(Context context, String className){

        boolean isRunning = false;
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> activityList = mActivityManager.getRunningTasks(Integer.MAX_VALUE);

        if (!(activityList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < activityList.size(); i++) {
            if (activityList.get(i).baseActivity.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }

    /**
     * 用来判断服务是否运行.
     * @param context
     * @param className 判断的服务名字：包名+类名
     * @return true 在运行, false 不在运行
     */
    public static boolean isServiceRunning(Context context, String className) {

        boolean isRunning = false;
        ActivityManager activityManager =(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 判别手机是否为正确手机号码；
     * 号码段分配如下：
     * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186
     * 电信：133、153、180、189、（1349卫通）
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断邮箱是否合法
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 从短信字符窜提取验证码
     *
     * @param body   短信内容
     * @param length 验证码的长度 一般6位或者4位
     * @return 接取出来的验证码
     */
    public static String getVerfyCode(String body, int length) {
        // 首先([a-zA-Z0-9]{length})是得到一个连续的六位数字字母组合
        // (?<![a-zA-Z0-9])负向断言([0-9]{YZMLENGTH})前面不能有数字
        // (?![a-zA-Z0-9])断言([0-9]{YZMLENGTH})后面不能有数字出现
        // Pattern p = Pattern.compile("(?<![a-zA-Z0-9])([a-zA-Z0-9]{" + length + "})(?![a-zA-Z0-9])");

        String number = "[0-9\\\\.]{" + length + "}";

        boolean isVerifycode = body.contains("验")
                && body.contains("证")
                && body.contains("码");

        if (isVerifycode) {
            Pattern p = Pattern.compile(number);
            Matcher m = p.matcher(body);
            if (m.find()) {
                System.out.println(m.group());
                return m.group(0);
            }
        }

        return null;
    }

    /**
     * 获取 GSF ID KEY
     * @param context
     * @return
     */
    public static String getGsfAndroidId(Context context) {
        Uri uri = Uri.parse("content://com.google.android.gsf.gservices");
        String ID_KEY = "android_id";
        String params[] = {ID_KEY};
        Cursor c = context.getContentResolver().query(uri, null, null, params, null);
        if (!c.moveToFirst() || c.getColumnCount() < 2)
            return null;
        try {
            return Long.toHexString(Long.parseLong(c.getString(1)));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取 Device ID
     * @param context
     * @return
     */
    public static String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
