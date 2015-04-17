package com.carlisle.songtaste.fm;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.carlisle.songtaste.cmpts.events.DownloadCompleteEvent;
import com.carlisle.songtaste.cmpts.events.PlayerReceivingEvent;
import com.carlisle.songtaste.cmpts.events.PlayerSendingEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import de.greenrobot.event.EventBus;
import javazoom.jl.decoder.JavaLayerException;

/**
 * Created by xudong on 13-5-19.
 */
public class YueduService extends IntentService {

    private void sendPreparedBroadcast() {
        EventBus.getDefault().post(new PlayerSendingEvent(PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PREPARED));
    }

    private void sendWillPrepareBroadcast() {
        EventBus.getDefault().post(new PlayerSendingEvent(PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_PREPARE));
    }

    private void sendWillPlayBroadcast() {
        EventBus.getDefault().post(new PlayerSendingEvent(PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_PLAY));
    }

    private void sendPlayingBroadcast() {
        EventBus.getDefault().post(new PlayerSendingEvent(PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PLAYING));
    }

    private void sendWillPauseBroadcast() {
        EventBus.getDefault().post(new PlayerSendingEvent(PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_PAUSE));
    }

    private void sendPausedBroadcast() {
        EventBus.getDefault().post(new PlayerSendingEvent(PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PAUSED));
    }

    private void sendWillStopBroadcast() {
        EventBus.getDefault().post(new PlayerSendingEvent(PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_STOP));
    }

    private void sendStoppedBroadcast() {
        EventBus.getDefault().post(new PlayerSendingEvent(PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_STOPPED));
    }

    private void sendErrorOccurredBroadcast(String error) {
        EventBus.getDefault().post(new PlayerSendingEvent(PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_ERROR_OCCURRED));
    }

    private void sendCompletionBroadcast() {
        EventBus.getDefault().post(new PlayerSendingEvent(PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_COMPLETE));
    }

    private void sendPlayStateBroadcast() {
        EventBus.getDefault().post(new PlayerSendingEvent(PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_STATE_REPORT, getmPlayer().isPlaying()));
    }

    protected static final String PLAYER_SENDING_BROADCAST_ACTION = "player_sending_broadcast";
    protected static final String PLAYER_RECEIVING_BROADCAST_ACTION = "player_receiving_broadcast";


    private AudioManager mAudioManager;
    private TelephonyManager mTelephonyManager;
    private StreamingDownloadMediaPlayer mPlayer;
    private AudioManager.OnAudioFocusChangeListener mFocusListener;
    private PhoneStateListener mPhoneStateListener;
    static private final ComponentName REMOTE_CONTROL_RECEIVER_NAME = new ComponentName("com.yuedu.fm", "RemoteControlReceiver");
    private NoisyAudioStreamReceiver mNoisyAudioStreamReceiver;

    public void onEvent(DownloadCompleteEvent downloadCompleteEvent) {
        YueduNotificationManager.SINGLE_INSTANCE.showForegroundNotification(YueduService.this);
    }

    public void onEvent(PlayerReceivingEvent playerReceivingEvent) {
        String path = null;
        switch (playerReceivingEvent.serviceCanHandle) {
            case PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_PLAY:
                path = DataAccessor.SINGLE_INSTANCE.getPlayingTune().getUrl();
                tryToPrepareForPath(path);
                break;
            case PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_PLAY_NEXT:
                path = DataAccessor.SINGLE_INSTANCE.playNextTune().getUrl();
                tryToPrepareForPath(path);
                break;
            case PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_PAUSE:
                pause();
                break;
            case PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_SWITCH_PLAYSTATE:
                if (getmPlayer().isPlaying()) {
                    pause();
                } else {
                    path = DataAccessor.SINGLE_INSTANCE.getPlayingTune().getUrl();
                    tryToPrepareForPath(path);
                }
                break;
            case PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_REQUEST_PLAYSTATE:
                Log.d("yuedu", "some one sent request for playing state!!!!");
                sendPlayStateBroadcast();
                break;
        }
    }

    private void tryToPrepareForPath(String path) {
        if (!TextUtils.isEmpty(path) && (getCurrentDataSource() == null || !path.equals(getCurrentDataSource()))) {
            prepareForPath(path);
        } else {
            try {
                play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareForPath(String path) {
        if (mScheduler != null) {
            getmScheduler().purge();
            getmScheduler().pause();
        }
        try {
            if (getmPlayer().isPlaying() || getmPlayer().isPaused() || getmPlayer().isCompleted() || getmPlayer().isPreparing()) {
                sendWillStopBroadcast();
                getmPlayer().stop();
                sendStoppedBroadcast();
                YueduNotificationManager.SINGLE_INSTANCE.setPlayButtonPlaying(this, false);
            }
            setTunePath(path);
            prepareToPlay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCurrentDataSource() {
        return getmPlayer().getDataSource() == null ? null : getmPlayer().getDataSource().toString();
    }

    private PausableThreadPoolExecutor mScheduler;

    public PausableThreadPoolExecutor getmScheduler() {
        if (mScheduler == null) {
            mScheduler = new PausableThreadPoolExecutor(1);
            mScheduler.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
            mScheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(true);
            mScheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    long currentPosition = getmPlayer().getCurrentPosition();
                    long duration = getmPlayer().getDuration();
                    EventBus.getDefault().post(new PlayerSendingEvent(PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_CURRENT_POSITION, currentPosition));
                }
            }, 0, 500, TimeUnit.MILLISECONDS);
        }
        return mScheduler;
    }

    public StreamingDownloadMediaPlayer getmPlayer() {
        if (mPlayer == null) {
            mPlayer = new StreamingDownloadMediaPlayer();
            File diskFileCacheDir = new File(getExternalCacheDir(), "audio");
            if (diskFileCacheDir.exists() || diskFileCacheDir.mkdirs()) {
                mPlayer.setCacheDir(diskFileCacheDir);
            }
            //TODO listener API
            mPlayer.setOnPreparedListener(new StreamingDownloadMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(StreamingDownloadMediaPlayer mp) {
                    sendPreparedBroadcast();
                    prepareToStart();
                    play();
                    getmScheduler().purge();
                    getmScheduler().resume();
                }
            });
            mPlayer.setOnCompletionListener(new StreamingDownloadMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(StreamingDownloadMediaPlayer mediaPlayer) {
                    if (mScheduler != null) {
                        getmScheduler().purge();
                        getmScheduler().pause();
                    }
                    DataAccessor.SINGLE_INSTANCE.playNextTune();
                    prepareForPath(DataAccessor.SINGLE_INSTANCE.getPlayingTune().getUrl());
                    sendCompletionBroadcast();
                }
            });
            mPlayer.setOnErrorListener(new StreamingDownloadMediaPlayer.OnErrorListener() {
                @Override
                public void onError(StreamingDownloadMediaPlayer mediaPlayer, Throwable e) {
                    if (mScheduler != null) {
                        getmScheduler().purge();
                        getmScheduler().pause();
                    }
                    String error;
                    if (e instanceof FileNotFoundException) {
                        error = "未发现网络音频文件";
                    } else {
                        error = e.getLocalizedMessage();
                    }
                    sendErrorOccurredBroadcast(error);
                    YueduNotificationManager.SINGLE_INSTANCE.setPlayButtonPlaying(YueduService.this, false);
                }
            });
        }
        return mPlayer;
    }

    private void sendLocalBroadcast(Intent intent) {
        if (intent != null) {
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    public AudioManager getmAudioManager() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        }
        return mAudioManager;
    }

    public TelephonyManager getmTelephonyManager() {
        if (mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        }
        return mTelephonyManager;
    }

    public AudioManager.OnAudioFocusChangeListener getmFocusListener() {
        if (mFocusListener == null) {
            mFocusListener = new AudioManager.OnAudioFocusChangeListener() {
                @TargetApi(Build.VERSION_CODES.FROYO)
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            pause();
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                            prepareToPlay();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            pause();
                        default:
                            break;
                    }
                }
            };
        }
        return mFocusListener;
    }

    public PhoneStateListener getmPhoneStateListener() {
        if (mPhoneStateListener == null) {
            mPhoneStateListener = new PhoneStateListener() {

                boolean isPausedByCall;

                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        //Incoming call: Pause music
                        if (getmPlayer().isPlaying()) {
                            pause();
                            isPausedByCall = true;
                        }
                        Log.d("yuedu", "incoming call!!!! number is " + incomingNumber);
                    } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                        //Not in call: Play music
                        if (getmPlayer().isPaused() && isPausedByCall) {
                            play();
                            isPausedByCall = false;
                        }
                        Log.d("yuedu", "not in call!!!!");
                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        //A call is dialing, active or on hold
                        if (getmPlayer().isPlaying()) {
                            pause();
                            isPausedByCall = true;
                        }
                    }
                    super.onCallStateChanged(state, incomingNumber);
                }
            };
        }
        return mPhoneStateListener;
    }

    public NoisyAudioStreamReceiver getmNoisyAudioStreamReceiver() {
        if (mNoisyAudioStreamReceiver == null) {
            mNoisyAudioStreamReceiver = new NoisyAudioStreamReceiver();
        }
        return mNoisyAudioStreamReceiver;
    }

    public YueduService() {
        super("YueduService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        EventBus.getDefault().unregister(this);

        TelephonyManager telMgr = getmTelephonyManager();
        if (telMgr != null) {
            telMgr.listen(getmPhoneStateListener(), PhoneStateListener.LISTEN_NONE);
        }
        if (mScheduler != null) {
            mScheduler.purge();
            mScheduler.shutdownNow();
        }
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("yuedu", "on start command ");
        sendPlayStateBroadcast();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        TelephonyManager telMgr = getmTelephonyManager();
        if (telMgr != null) {
            Log.d("yuedu", "start listen phone state");
            telMgr.listen(getmPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void setTunePath(final String tunePath) throws IOException, JavaLayerException {
        StreamingDownloadMediaPlayer player = getmPlayer();
        player.reset();
        player.setDataSource(new URL(tunePath));
    }

    private void play() {
        sendWillPlayBroadcast();
        getmPlayer().start();
        sendPlayingBroadcast();
        YueduNotificationManager.SINGLE_INSTANCE.setPlayButtonPlaying(this, true);
    }

    private boolean prepareToPlay() {
        AudioManager audioMgr = getmAudioManager();
        if (audioMgr != null) {
            int focus = audioMgr.requestAudioFocus(getmFocusListener(), AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                StreamingDownloadMediaPlayer player = getmPlayer();
                try {
                    sendWillPrepareBroadcast();
                    YueduNotificationManager.SINGLE_INSTANCE.updateTitle(YueduService.this);
                    player.prepareAsync();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //request audio focus failed
            }
        }
        return false;
    }

    private boolean stop() {
        if (getmPlayer().isPlaying()) {
            prepareToStop();
            sendWillStopBroadcast();
            getmPlayer().stop();
            sendStoppedBroadcast();
            return true;
        }
        return false;
    }

    private boolean pause() {
        if (getmPlayer().isPlaying()) {
            prepareToPause();
            sendWillPauseBroadcast();
            getmPlayer().pause();
            sendPausedBroadcast();
            YueduNotificationManager.SINGLE_INSTANCE.setPlayButtonPlaying(this, false);
            return true;
        }
        return false;
    }

    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private void prepareToStart() {
        registerReceiver(getmNoisyAudioStreamReceiver(), intentFilter);
    }

    private void prepareToStop() {
        unregisterReceiver(getmNoisyAudioStreamReceiver());
        AudioManager audioMgr = getmAudioManager();
        if (audioMgr != null) {
            audioMgr.unregisterMediaButtonEventReceiver(REMOTE_CONTROL_RECEIVER_NAME);
            audioMgr.abandonAudioFocus(getmFocusListener());
        }
    }

    private void prepareToPause() {
        AudioManager audioMgr = getmAudioManager();
        if (audioMgr != null) {
            audioMgr.abandonAudioFocus(getmFocusListener());
        }
    }

    private class NoisyAudioStreamReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                pause();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
        if (mPlayer != null) {
            mPlayer.release();
        }
        YueduNotificationManager.SINGLE_INSTANCE.stopForeground(this);
    }

    static class PausableThreadPoolExecutor extends ScheduledThreadPoolExecutor {
        private boolean isPaused;
        private ReentrantLock pauseLock = new ReentrantLock();
        private Condition unpaused = pauseLock.newCondition();

        public PausableThreadPoolExecutor(int corePoolSize) {
            super(corePoolSize);
        }

        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            pauseLock.lock();
            try {
                while (isPaused) unpaused.await();
            } catch (InterruptedException ie) {
                t.interrupt();
            } finally {
                pauseLock.unlock();
            }
        }

        public void pause() {
            pauseLock.lock();
            try {
                isPaused = true;
            } finally {
                pauseLock.unlock();
            }
        }

        public void resume() {
            pauseLock.lock();
            try {
                isPaused = false;
                unpaused.signalAll();
            } finally {
                pauseLock.unlock();
            }
        }
    }
}
