package com.carlisle.songtaste.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.carlisle.songtaste.modle.SongDetailInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengxin on 2/28/15.
 */
public class MusicService extends Service implements Playback.Callback {
    private static final String TAG = MusicService.class.getSimpleName();

    private Service service;
    private Context context;

    //Custom actions for media player controls via the notification bar.
    public static final String LAUNCH_NOW_PLAYING_ACTION = "com.carlisle.songtaste.LAUNCH_NOW_PLAYING_ACTION";
    public static final String PREVIOUS_ACTION = "com.carlisle.songtaste.PREVIOUS_ACTION";
    public static final String PLAY_PAUSE_ACTION = "com.carlisle.songtaste.PLAY_PAUSE_ACTION";
    public static final String NEXT_ACTION = "com.carlisle.songtaste.NEXT_ACTION";
    public static final String STOP_SERVICE = "com.carlisle.songtaste.STOP_SERVICE";

    private static final int STOP_DELAY = 30000;

    private Playback playback;
    private DelayedStopHandler delayedStopHandler = new DelayedStopHandler(this);

    // Indicates whether the service was started.
    private boolean serviceStarted;

    private List<SongDetailInfo> playingQueue;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        if (startIntent != null) {

            SongDetailInfo song = (SongDetailInfo) startIntent.getParcelableExtra(LAUNCH_NOW_PLAYING_ACTION);

            Log.d("song====>", JSON.toJSONString(song));

            if (playback != null) {
                playback.play(song);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        playingQueue = new ArrayList<>();
        playback = new AudioPlayback(this);
        playback.setState(Playback.STATE_NONE);
        playback.setCallback(this);
        playback.start();


    }

    /**
     * Handle a request to play music
     */
    private void handlePlayRequest() {
        Log.d(TAG, "handlePlayRequest: mState=" + playback.getState());

        delayedStopHandler.removeCallbacksAndMessages(null);

        if (!serviceStarted) {
            Log.v(TAG, "Starting service");
            // The MusicService needs to keep running even after the calling MediaBrowser
            // is disconnected. Call startService(Intent) and then stopSelf(..) when we no longer
            // need to play media.
            startService(new Intent(getApplicationContext(), MusicService.class));
            serviceStarted = true;
        }

//        if (QueueHelper.isIndexPlayable(mCurrentIndexOnQueue, mPlayingQueue)) {
//            updateMetadata();
//            playback.play(mPlayingQueue.get(mCurrentIndexOnQueue));
//        }
    }

    private void handlePauseRequest() {
        Log.d(TAG, "handlePauseRequest: mState=" + playback.getState());
        playback.pause();
        // reset the delayed stop handler.
        delayedStopHandler.removeCallbacksAndMessages(null);
        delayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
    }

    @Override
    public void onCompletion() {

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

    /**
     * A simple handler that stops the service if playback is not active (playing)
     */
    private static class DelayedStopHandler extends Handler {
        private final WeakReference<MusicService> mWeakReference;

        private DelayedStopHandler(MusicService service) {
            mWeakReference = new WeakReference(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MusicService service = mWeakReference.get();
            if (service != null && service.playback != null) {
                if (service.playback.isPlaying()) {
                    Log.d(TAG, "Ignoring delayed stop since the media player is in use.");
                    return;
                }
                Log.d(TAG, "Stopping service with delay handler.");
                service.stopSelf();
                service.serviceStarted = false;
            }
        }
    }


}
