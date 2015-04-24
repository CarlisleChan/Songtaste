package com.carlisle.songtaste.cmpts.oldServices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.carlisle.songtaste.cmpts.events.ExitEvent;
import com.carlisle.songtaste.cmpts.events.ScreenOnEvent;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.oldEvents.PauseEvent;
import com.carlisle.songtaste.cmpts.oldEvents.PlayEvent;
import com.carlisle.songtaste.cmpts.oldEvents.SkipToNextEvent;
import com.carlisle.songtaste.cmpts.oldEvents.SkipToPrevEvent;
import com.carlisle.songtaste.cmpts.oldEvents.UpdatePlaybackEvent;
import com.carlisle.songtaste.cmpts.reveiver.HeadsetPlugReceiver;
import com.carlisle.songtaste.cmpts.reveiver.NotificationReceiver;
import com.carlisle.songtaste.cmpts.reveiver.RemoteControlReceiver;
import com.carlisle.songtaste.cmpts.reveiver.ScreenOnReceiver;
import com.carlisle.songtaste.utils.QueueHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by chengxin on 2/28/15.
 */
public class OldMusicService extends Service implements Playback.Callback {
    private static final String TAG = OldMusicService.class.getSimpleName();

    public static final String LAUNCH_NOW_PLAYING_ACTION = "com.carlisle.songtaste.LAUNCH_NOW_PLAYING_ACTION";
    public static final String CURRENT_ID = "current id";

    private static final int STOP_DELAY = 30000;

    private Playback playback;

    private boolean serviceStarted;
    private int currentIndexOnQueue;

    private HeadsetPlugReceiver headsetPlugReceiver = new HeadsetPlugReceiver();
    private NotificationReceiver notificationReceiver = new NotificationReceiver();
    private ScreenOnReceiver screenOnReceiver = new ScreenOnReceiver();
    private RemoteControlReceiver remoteControlReceiver = new RemoteControlReceiver();

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
        EventBus.getDefault().register(this);

        headsetPlugReceiver.register(this);
        screenOnReceiver.register(this);
        notificationReceiver.register(this);
        remoteControlReceiver.register(this);

        playback = new AudioPlayback(this);
        playback.setState(Playback.STATE_NONE);
        playback.setCallback(this);
        playback.start();

    }

    public Playback getPlayback() {
        return playback;
    }

    public void setPlayback(Playback playback) {
        this.playback = playback;
    }

    public void onEvent(ExitEvent exitEvent) {
        stopSelf();
    }

    public void onEvent(ScreenOnEvent screenOnEvent) {
        Log.d("showRemoteControl===","service");
        SongDetailInfo songDetailInfo = QueueHelper.getInstance().getCurrentQueue().get(currentIndexOnQueue);
        EventBus.getDefault().post(new UpdatePlaybackEvent(songDetailInfo));
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
        if (!serviceStarted) {
            startService(new Intent(getApplicationContext(), OldMusicService.class));
            serviceStarted = true;
        }

        if (QueueHelper.getInstance().isIndexPlayable(currentIndexOnQueue)) {
            SongDetailInfo songDetailInfo = QueueHelper.getInstance().getCurrentQueue().get(currentIndexOnQueue);
            playback.play(songDetailInfo);
            EventBus.getDefault().post(new UpdatePlaybackEvent(songDetailInfo));
        }
    }

    private void handlePauseRequest() {
        playback.pause();
        EventBus.getDefault().post(new UpdatePlaybackEvent(playback.getState()));
    }

    private void handleStopRequest() {
        playback.stop(true);
        EventBus.getDefault().post(new UpdatePlaybackEvent(playback.getState()));
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
        notificationReceiver.unRegister();
    }
}
