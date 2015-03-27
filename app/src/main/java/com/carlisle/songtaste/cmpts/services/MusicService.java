package com.carlisle.songtaste.cmpts.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.cmpts.events.PauseEvent;
import com.carlisle.songtaste.cmpts.events.PlayEvent;
import com.carlisle.songtaste.cmpts.events.SkipToNextEvent;
import com.carlisle.songtaste.cmpts.events.SkipToPrevEvent;
import com.carlisle.songtaste.cmpts.events.UpdateUIEvent;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.utils.QueueHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by chengxin on 2/28/15.
 */
public class MusicService extends Service implements Playback.Callback {
    private static final String TAG = MusicService.class.getSimpleName();

    public static final String LAUNCH_NOW_PLAYING_ACTION = "com.carlisle.songtaste.LAUNCH_NOW_PLAYING_ACTION";
    public static final String CURRENT_ID = "current id";

    private static final int STOP_DELAY = 30000;

    private Playback playback;

    private boolean serviceStarted;
    private int currentIndexOnQueue;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher,
                "Foreground Service Started.", System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MusicService.class), 0);
        notification.setLatestEventInfo(this, "Foreground Service",
                "Foreground Service Started.", contentIntent);
        startForeground(1, notification);

        EventBus.getDefault().register(this);
        playback = new AudioPlayback(this);
        playback.setState(Playback.STATE_NONE);
        playback.setCallback(this);
        playback.start();

    }

    public void onEvent(PlayEvent event) {
        if (event.position != -1) {
            currentIndexOnQueue = event.position;
        }
        onPlay();
    }

    public void onEvent(PauseEvent event) {
        onPause();
    }

    public void onEvent(SkipToNextEvent event) {
        onSkipToNext();
    }

    public void onEvent(SkipToPrevEvent event) {
        onSkipToPrev();
    }

    public void onPlay() {
        if (!QueueHelper.getInstance().getCurrentQueue().isEmpty()) {
            handlePlayRequest();
        } else {
            currentIndexOnQueue = 0;
        }
    }

    public void onPause() {
        handlePauseRequest();
    }

    public void onSkipToNext() {
        playback.stop(true);
        ++currentIndexOnQueue;

        if (QueueHelper.getInstance().isIndexPlayable(currentIndexOnQueue)) {
            handlePlayRequest();
        } else {
            --currentIndexOnQueue;
            Toast.makeText(this, "已是最后一首", Toast.LENGTH_SHORT).show();
            handleStopRequest();
        }
    }

    public void onSkipToPrev() {
        playback.stop(true);
        --currentIndexOnQueue;

        if (QueueHelper.getInstance().isIndexPlayable(currentIndexOnQueue)) {
            handlePlayRequest();
        } else {
            ++currentIndexOnQueue;
            Toast.makeText(this, "已是第一首", Toast.LENGTH_SHORT).show();
            handleStopRequest();
        }
    }

    private void handlePlayRequest() {
        Log.d("handlePlay===>","you are here");
        if (!serviceStarted) {
            startService(new Intent(getApplicationContext(), MusicService.class));
            serviceStarted = true;
        }

        if (QueueHelper.getInstance().isIndexPlayable(currentIndexOnQueue)) {
            SongDetailInfo songDetailInfo = QueueHelper.getInstance().getCurrentQueue().get(currentIndexOnQueue);
            playback.play(songDetailInfo);
            EventBus.getDefault().post(new UpdateUIEvent(songDetailInfo));
        }
    }

    private void handlePauseRequest() {
        Log.d("handlePause===>","you are here");
        playback.pause();

    }

    private void handleStopRequest() {
        playback.stop(true);
    }

    @Override
    public void onCompletion() {
        onSkipToNext();
    }

    @Override
    public void onPlaybackStatusChanged(int state) {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onMetadataChanged(String mediaId) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
