package com.carlisle.songtaste.cmpts.services;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.carlisle.songtaste.cmpts.events.ProgressEvent;
import com.carlisle.songtaste.cmpts.events.UpdatePlaybackEvent;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;

import de.greenrobot.event.EventBus;

import static android.media.MediaPlayer.OnBufferingUpdateListener;
import static android.media.MediaPlayer.OnCompletionListener;
import static android.media.MediaPlayer.OnErrorListener;
import static android.media.MediaPlayer.OnPreparedListener;
import static android.media.MediaPlayer.OnSeekCompleteListener;

/**
 * Created by carlisle on 3/21/15.
 */
public class AudioPlayback implements Playback, AudioManager.OnAudioFocusChangeListener,
        OnBufferingUpdateListener, OnCompletionListener,
        OnErrorListener, OnPreparedListener, OnSeekCompleteListener {
    private static final String TAG = AudioPlayback.class.getSimpleName();

    public static final float VOLUME_DUCK = 0.2f;
    public static final float VOLUME_NORMAL = 1.0f;

    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_FOCUSED = 2;

    private final MusicService musicService;
    private int state;
    private Callback callback;
    private boolean playOnFocusGain;

    private int audioFocus = AUDIO_NO_FOCUS_NO_DUCK;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    private int currentPosition;
    private String currentMediaUrl;
    private int currentStreamPosition;

    private Handler handler = new Handler();

    public AudioPlayback(MusicService musicService) {
        this.musicService = musicService;
        this.audioManager = (AudioManager) musicService.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void start() {

    }

    @Override
    public boolean isPlaying() {
        return playOnFocusGain || (mediaPlayer != null && mediaPlayer.isPlaying());
    }

    @Override
    public void play(SongDetailInfo songDetailInfo) {
        Log.d(TAG, "play");
        playOnFocusGain = true;
        tryToGetAudioFocus();

        String url = songDetailInfo.getUrl();
        boolean hasMediaChanged = !TextUtils.equals(url, currentMediaUrl);
        if (hasMediaChanged) {
            currentPosition = 0;
            currentMediaUrl = url;
        }

        if (state == Playback.STATE_PAUSED && mediaPlayer != null) {
            configMediaPlayerState();
        } else {
            state = Playback.STATE_STOPPED;
            relaxResources(false);
            String source = songDetailInfo.getUrl();
            try {
                createMediaPlayerIfNeeded();
                state = Playback.STATE_BUFFERING;
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(source);
                mediaPlayer.prepareAsync();

                if (callback != null) {
                    callback.onPlaybackStatusChanged(state);
                }

            } catch (Exception e) {
                Log.e(TAG, e + "Exception playing song");
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        }

        onSeekBarUpdate();
//        EventBus.getDefault().post(new UpdateUIEvent(state));
    }

    @Override
    public void pause() {
        Log.d(TAG, "pause");
        if (state == Playback.STATE_PLAYING) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                currentPosition = mediaPlayer.getCurrentPosition();
                relaxResources(false);
                giveUpAudioFocus();
            }
        }

        state = Playback.STATE_PAUSED;

        if (callback != null) {
            callback.onPlaybackStatusChanged(state);
        }

        handler.removeCallbacks(null);
        EventBus.getDefault().post(new UpdatePlaybackEvent(state));
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion from MediaPlayer");
        if (callback != null) {
            callback.onCompletion();
        }
    }

    @Override
    public void stop(boolean notifyListeners) {
        Log.d(TAG, "stop");
        state = Playback.STATE_STOPPED;

        if (notifyListeners && callback != null) {
            callback.onPlaybackStatusChanged(state);
        }

        currentPosition = getCurrentStreamPosition();
        giveUpAudioFocus();
        relaxResources(true);

//        EventBus.getDefault().post(new UpdateUIEvent(state));
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public boolean isConnected() {
        return true;
    }


    @Override
    public int getCurrentStreamPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : currentStreamPosition;
    }

    @Override
    public void setCurrentStreamPosition(int pos) {
        this.currentStreamPosition = pos;
    }


    private void tryToGetAudioFocus() {
        Log.d(TAG, "tryToGetAudioFocus");
        if (audioFocus != AUDIO_FOCUSED) {
            int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                audioFocus = AUDIO_FOCUSED;
            }
        }
    }

    private void giveUpAudioFocus() {
        Log.d(TAG, "giveUpAudioFocus");
        if (audioFocus == AUDIO_FOCUSED) {
            if (audioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                audioFocus = AUDIO_NO_FOCUS_NO_DUCK;
            }
        }
    }

    @Override
    public void seekTo(int position) {
        Log.d(TAG, "seekTo called with " + position);
        if (mediaPlayer == null) {
            currentPosition = position;
        } else {
            if (mediaPlayer.isPlaying()) {
                state = Playback.STATE_BUFFERING;
            }
            mediaPlayer.seekTo(position);
            if (callback != null) {
                callback.onPlaybackStatusChanged(state);
            }
        }
    }

    public void onEvent(ProgressEvent progressEvent) {
        if (progressEvent.trackTouch) {
            seekTo(progressEvent.currentPosition);
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "onSeekComplete from MediaPlayer:" + mp.getCurrentPosition());
        currentPosition = mp.getCurrentPosition();
        if (state == Playback.STATE_BUFFERING) {
            mediaPlayer.start();
            state = Playback.STATE_PLAYING;
        }
        if (callback != null) {
            callback.onPlaybackStatusChanged(state);
        }
    }

    @Override
    public void setCurrentMediaUrl(String mediaId) {
        this.currentMediaUrl = mediaId;
    }

    @Override
    public String getCurrentMediaUrl() {
        return currentMediaUrl;
    }

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d(TAG, "onAudioFocusChange. focusChange=" + focusChange);
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            audioFocus = AUDIO_FOCUSED;

        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            boolean canDuck = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
            audioFocus = canDuck ? AUDIO_NO_FOCUS_CAN_DUCK : AUDIO_NO_FOCUS_NO_DUCK;

            if (state == Playback.STATE_PLAYING && !canDuck) {
                playOnFocusGain = true;
            }
        } else {
            Log.e(TAG, "onAudioFocusChange: Ignoring unsupported focusChange: " + focusChange);
        }
        configMediaPlayerState();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "Media player error: what=" + what + ", extra=" + extra);
        if (callback != null) {
            callback.onError("MediaPlayer error " + what + " (" + extra + ")");
        }
        return true;
    }

    private void createMediaPlayerIfNeeded() {
        Log.d(TAG, "createMediaPlayerIfNeeded. needed? " + (mediaPlayer == null));
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setWakeMode(musicService.getApplicationContext(),
                    PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
        } else {
            mediaPlayer.reset();
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared from MediaPlayer");
        configMediaPlayerState();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        int currentPosition;
        int maxPosition;

        currentPosition = mediaPlayer.getCurrentPosition();
        maxPosition = mediaPlayer.getDuration();
        EventBus.getDefault().post(new ProgressEvent(currentPosition, maxPosition, false));
    }

    // 本地音乐播放时更新进度条
    public void onSeekBarUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentPosition;
                int maxPosition;

                currentPosition = mediaPlayer.getCurrentPosition();
                maxPosition = mediaPlayer.getDuration();
                EventBus.getDefault().post(new ProgressEvent(currentPosition, maxPosition, false));
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void configMediaPlayerState() {
        Log.d(TAG, "configMediaPlayerState. mAudioFocus=" + audioFocus);
        if (audioFocus == AUDIO_NO_FOCUS_NO_DUCK) {
            if (state == Playback.STATE_PLAYING) {
                pause();
            }
        } else {
            if (audioFocus == AUDIO_NO_FOCUS_CAN_DUCK) {
                mediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK);
            } else {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL);
                }
            }

            if (playOnFocusGain) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    if (currentPosition == mediaPlayer.getCurrentPosition()) {
                        mediaPlayer.start();
                        state = Playback.STATE_PLAYING;
                    } else {
                        mediaPlayer.seekTo(currentPosition);
                        state = Playback.STATE_BUFFERING;
                    }
                }
                playOnFocusGain = false;
            }
        }
        if (callback != null) {
            callback.onPlaybackStatusChanged(state);
        }
    }

    private void relaxResources(boolean releaseMediaPlayer) {
        Log.d(TAG, "relaxResources. releaseMediaPlayer=" + releaseMediaPlayer);

        musicService.stopForeground(true);

        if (releaseMediaPlayer && mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        EventBus.getDefault().unregister(this);
    }

}
