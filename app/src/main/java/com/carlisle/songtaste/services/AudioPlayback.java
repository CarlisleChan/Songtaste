package com.carlisle.songtaste.services;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.carlisle.songtaste.modle.SongDetailInfo;

import static android.media.MediaPlayer.OnCompletionListener;
import static android.media.MediaPlayer.OnErrorListener;
import static android.media.MediaPlayer.OnPreparedListener;
import static android.media.MediaPlayer.OnSeekCompleteListener;

/**
 * Created by carlisle on 3/21/15.
 */
public class AudioPlayback implements Playback, AudioManager.OnAudioFocusChangeListener,
        OnCompletionListener, OnErrorListener, OnPreparedListener, OnSeekCompleteListener {
    private static final String TAG = AudioPlayback.class.getSimpleName();

    public static final float VOLUME_DUCK = 0.2f;
    public static final float VOLUME_NORMAL = 1.0f;

    // we don't have audio focus, and can't duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    // we don't have focus, but can duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    // we have full audio focus
    private static final int AUDIO_FOCUSED = 2;

    private final MusicService musicService;
    private int state;
    private Callback callback;
    private boolean playOnFocusGain;

    private int audioFocus = AUDIO_NO_FOCUS_NO_DUCK;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    private int currentPosition;
    private String currentMediaId;
    private int currentStreamPosition;

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
    public void play(SongDetailInfo item) {
        playOnFocusGain = true;
        tryToGetAudioFocus();

        String mediaId = item.getMediaId();
        boolean hasMediaChanged = !TextUtils.equals(mediaId, currentMediaId);
        if (hasMediaChanged) {
            currentPosition = 0;
            currentMediaId = mediaId;
        }

        if (state == Playback.STATE_PAUSED && mediaPlayer != null) {
            configMediaPlayerState();
        } else {
            state = Playback.STATE_STOPPED;
            relaxResources(false); // release everything except MediaPlayer


            String source = item.getUrl();

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

    }

    @Override
    public void pause() {
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
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared from MediaPlayer");
        // The media player is done preparing. That means we can start playing if we
        // have audio focus.
        configMediaPlayerState();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion from MediaPlayer");
        // The media player finished playing the current song, so we go ahead
        // and start the next.
        if (callback != null) {
            callback.onCompletion();
        }
    }

    @Override
    public void stop(boolean notifyListeners) {
        state = Playback.STATE_STOPPED;
        if (notifyListeners && callback != null) {
            callback.onPlaybackStatusChanged(state);
        }
        currentPosition = getCurrentStreamPosition();
        // Give up Audio focus
        giveUpAudioFocus();
        // Relax all resources
        relaxResources(true);
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
    public void setCurrentMediaId(String mediaId) {
        this.currentMediaId = mediaId;
    }

    @Override
    public String getCurrentMediaId() {
        return currentMediaId;
    }

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d(TAG, "onAudioFocusChange. focusChange=" + focusChange);
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // We have gained focus:
            audioFocus = AUDIO_FOCUSED;

        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            // We have lost focus. If we can duck (low playback volume), we can keep playing.
            // Otherwise, we need to pause the playback.
            boolean canDuck = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
            audioFocus = canDuck ? AUDIO_NO_FOCUS_CAN_DUCK : AUDIO_NO_FOCUS_NO_DUCK;

            // If we are playing, we need to reset media player by calling configMediaPlayerState
            // with mAudioFocus properly set.
            if (state == Playback.STATE_PLAYING && !canDuck) {
                // If we don't have audio focus and can't duck, we save the information that
                // we were playing, so that we can resume playback once we get the focus back.
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
        return true; // true indicates we handled the error
    }

    private void createMediaPlayerIfNeeded() {
        Log.d(TAG, "createMediaPlayerIfNeeded. needed? " + (mediaPlayer == null));
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();

            // Make sure the media player will acquire a wake-lock while
            // playing. If we don't do that, the CPU might go to sleep while the
            // song is playing, causing playback to stop.
            mediaPlayer.setWakeMode(musicService.getApplicationContext(),
                    PowerManager.PARTIAL_WAKE_LOCK);

            // we want the media player to notify us when it's ready preparing,
            // and when it's done playing:
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
        } else {
            mediaPlayer.reset();
        }
    }


    /**
     * Reconfigures MediaPlayer according to audio focus settings and
     * starts/restarts it. This method starts/restarts the MediaPlayer
     * respecting the current audio focus state. So if we have focus, it will
     * play normally; if we don't have focus, it will either leave the
     * MediaPlayer paused or set it to a low volume, depending on what is
     * allowed by the current focus settings. This method assumes mPlayer !=
     * null, so if you are calling it, you have to do so from a context where
     * you are sure this is the case.
     */
    private void configMediaPlayerState() {
        Log.d(TAG, "configMediaPlayerState. mAudioFocus=" + audioFocus);
        if (audioFocus == AUDIO_NO_FOCUS_NO_DUCK) {
            // If we don't have audio focus and can't duck, we have to pause,
            if (state == Playback.STATE_PLAYING) {
                pause();
            }
        } else {  // we have audio focus:
            if (audioFocus == AUDIO_NO_FOCUS_CAN_DUCK) {
                mediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK); // we'll be relatively quiet
            } else {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL); // we can be loud again
                } // else do something for remote client.
            }
            // If we were playing when we lost focus, we need to resume playing.
            if (playOnFocusGain) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    Log.d(TAG, "configMediaPlayerState startMediaPlayer. seeking to " + currentPosition);
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

        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
