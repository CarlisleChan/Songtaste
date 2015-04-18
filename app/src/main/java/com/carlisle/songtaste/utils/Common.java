package com.carlisle.songtaste.utils;

/**
 * Created by carlisle on 3/24/15.
 */
public class Common {
    public static int CURRENT_POSITION = -1;

    public class Notification{
        /** 通知栏按钮点击事件对应的ACTION */
        public final static String NOTIFICATION_ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";
        public final static String INTENT_BUTTONID_TAG = "ButtonId";
        public final static int NOTIFICATION_PREV = 1001;
        public final static int NOTIFICATION_PAUSE = 1002;
        public final static int NOTIFICATION_NEXT = 1003;
        public final static int NOTIFICATION_CLOSE = 1004;
        public final static int NOTIFICATION_FAVORITE = 1005;
    }

    public class RemoteControl{
        public final static int MEDIA_BUTTON_PAUSE = 1030;
        public final static int MEDIA_BUTTON_NEXT = 1031;
        public final static int MEDIA_BUTTON_PREV = 1032;
        public final static int MEDIA_BUTTON_STOP = 1033;
        public final static String MEDIA_BUTTON_ACTION = "android.intent.action.SCREEN_ON";
    }

    public class Screen{
        public final static int SCREEN_OFF = 1010;
        public final static int SCREEN_ON = 1011;
        public final static String SCREEN_ON_ACTION = "android.intent.action.SCREEN_ON";
    }

    public class HeadPlug{
        public final static int HEAD_PLUG_PAUSE = 1020;
        public final static String HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    }

    public class Receiver{
        public final static String SERVICE_RECEIVER = "com.intent.action.ToService";
        public final static String ACTIVITY_RECEIVER = "com.intent.action.ToActivity";
    }

}
