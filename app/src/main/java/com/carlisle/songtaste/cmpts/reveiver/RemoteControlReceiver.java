package com.carlisle.songtaste.cmpts.reveiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.view.KeyEvent;

import com.carlisle.songtaste.cmpts.events.PauseEvent;
import com.carlisle.songtaste.cmpts.events.PlayEvent;
import com.carlisle.songtaste.cmpts.events.SkipToNextEvent;
import com.carlisle.songtaste.cmpts.events.SkipToPrevEvent;
import com.carlisle.songtaste.cmpts.events.StopEvent;
import com.carlisle.songtaste.cmpts.events.UpdateUIEvent;
import com.carlisle.songtaste.cmpts.services.MusicService;
import com.carlisle.songtaste.cmpts.services.Playback;
import com.carlisle.songtaste.utils.Common;

import de.greenrobot.event.EventBus;

public class RemoteControlReceiver extends BroadcastReceiver {
    private MusicService musicService;
    private AudioManager audioManager;
    private ComponentName componentName;
    private static int state = Playback.STATE_NONE;

    public RemoteControlReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String intentAction = intent.getAction();
        KeyEvent keyEvent = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) return;

        int keyCode = keyEvent.getKeyCode();
        int keyAction = keyEvent.getAction();
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                if (keyAction == KeyEvent.ACTION_UP)
                    EventBus.getDefault().post(new SkipToNextEvent());
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (keyAction == KeyEvent.ACTION_UP) {
                    if (state == Playback.STATE_PAUSED) {
                        EventBus.getDefault().post(new PlayEvent());
                    } else if (state == Playback.STATE_NONE) {

                    } else {
                        EventBus.getDefault().post(new PauseEvent());
                    }
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                if (keyAction == KeyEvent.ACTION_UP)
                    EventBus.getDefault().post(new SkipToPrevEvent());
                break;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                if (keyAction == KeyEvent.ACTION_UP)
                    EventBus.getDefault().post(new StopEvent());
                break;
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if (keyAction == KeyEvent.ACTION_UP) {
                    if (state == Playback.STATE_PAUSED) {
                        EventBus.getDefault().post(new PlayEvent());
                    } else if (state == Playback.STATE_NONE) {

                    } else {
                        EventBus.getDefault().post(new PauseEvent());
                    }
                }
                break;
        }
    }

    public void register(MusicService musicService) {
        this.musicService = musicService;
        this.audioManager = (AudioManager) musicService.getSystemService(Context.AUDIO_SERVICE);
        this.componentName = new ComponentName(musicService.getPackageName(), RemoteControlReceiver.class.getName());
        this.audioManager.registerMediaButtonEventReceiver(componentName);
        EventBus.getDefault().register(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Common.Notification.NOTIFICATION_ACTION_BUTTON);
        musicService.registerReceiver(this, intentFilter);
    }

    public void unRegister() {
        musicService.unregisterReceiver(this);
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(UpdateUIEvent event) {
        if (event.songDetailInfo != null) {
            this.state = Playback.STATE_BUFFERING;
        } else {
            this.state = event.state;
        }
    }
}