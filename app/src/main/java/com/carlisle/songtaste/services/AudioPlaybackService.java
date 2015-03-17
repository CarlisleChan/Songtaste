package com.carlisle.songtaste.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.carlisle.songtaste.helpers.SongtasteSongHelper;
import com.carlisle.songtaste.remoteControlClient.RemoteControlClientCompat;
import com.carlisle.songtaste.base.BaseApplication;
import com.carlisle.songtaste.broadcastReceivers.HeadsetPlugBroadcastReceiver;
import com.carlisle.songtaste.helpers.AudioManagerHelper;

import java.util.ArrayList;

/**
 * Created by chengxin on 2/28/15.
 */
public class AudioPlaybackService extends Service {
    private Service mService;

    //Custom actions for media player controls via the notification bar.
    public static final String LAUNCH_NOW_PLAYING_ACTION = "com.carlisle.songtaste.LAUNCH_NOW_PLAYING_ACTION";
    public static final String PREVIOUS_ACTION = "com.carlisle.songtaste.PREVIOUS_ACTION";
    public static final String PLAY_PAUSE_ACTION = "com.carlisle.songtaste.PLAY_PAUSE_ACTION";
    public static final String NEXT_ACTION = "com.carlisle.songtaste.NEXT_ACTION";
    public static final String STOP_SERVICE = "com.carlisle.songtaste.STOP_SERVICE";

    //MediaPlayer objects.
    private MediaPlayer mMediaPlayer;
    private boolean mFirstRun = true;

    //AudioManager.
    private AudioManager mAudioManager;
    private AudioManagerHelper mAudioManagerHelper;

    //Flags that indicate whether the mediaPlayers have been initialized.
    private boolean mMediaPlayerPrepared = false;

    //Cursor object(s) that will guide the rest of this queue.
    private Cursor mCursor;
    private MergeCursor mMergeCursor;

    //Holds the indeces of the current cursor, in the order that they'll be played.
    private ArrayList<Integer> mPlaybackIndecesList = new ArrayList<Integer>();

    //Holds the indeces of songs that were unplayable.
    private ArrayList<Integer> mFailedIndecesList = new ArrayList<Integer>();

    //Song data helpers for each MediaPlayer object.
    private SongtasteSongHelper mMediaPlayerSongtasteSongHelper;

    //Pointer variable.
    private int mCurrentSongIndex;

    //Notification elements.
    private NotificationCompat.Builder mNotificationBuilder;
    public static final int mNotificationId = 1080; //NOTE: Using 0 as a notification ID causes Android to ignore the notification call.

    //Indicates if an enqueue/queue reordering operation was performed on the original queue.
    private boolean mEnqueuePerformed = false;

    //Handler object.
    private Handler mHandler;

    //Volume variables that handle the crossfade effect.
    private float mFadeOutVolume = 1.0f;
    private float mFadeInVolume = 0.0f;

    //Headset plug receiver.
    private HeadsetPlugBroadcastReceiver mHeadsetPlugReceiver;

    //Crossfade.
    private int mCrossfadeDuration;

    //A-B Repeat variables.
    private int mRepeatSongRangePointA = 0;
    private int mRepeatSongRangePointB = 0;

    //Indicates if the user changed the track manually.
    private boolean mTrackChangedByUser = false;

    //RemoteControlClient for use with remote controls and ICS+ lockscreen controls.
    private RemoteControlClientCompat mRemoteControlClientCompat;
    private ComponentName mMediaButtonReceiverComponent;

    //Enqueue reorder scalar.
    private int mEnqueueReorderScalar = 0;

    //Temp placeholder for GMusic Uri.
    public static final Uri URI_BEING_LOADED = Uri.parse("uri_being_loaded");

    private long mServiceStartTime;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        mService = this;
        BaseApplication.getInstance().setService((AudioPlaybackService) this);
        return super.onStartCommand(intent, flags, startId);


    }

    public boolean pausePlayback() {
        return true;
    }

    public boolean startPlayback() {
        return true;
    }

    public void skipToNextTrack() {
    }

    public void skipToTrack(int index) {
    }

    public void togglePlaybackState() {
    }

    public void skipToPreviousTrack() {
    }

}
