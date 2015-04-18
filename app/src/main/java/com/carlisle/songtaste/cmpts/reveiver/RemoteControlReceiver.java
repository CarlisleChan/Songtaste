package com.carlisle.songtaste.cmpts.reveiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.view.KeyEvent;

import com.carlisle.songtaste.cmpts.events.PlayerReceivingEvent;
import com.carlisle.songtaste.cmpts.events.PlayerSendingEvent;
import com.carlisle.songtaste.cmpts.services.MusicService;
import com.carlisle.songtaste.cmpts.services.DataAccessor;
import com.carlisle.songtaste.utils.Common;

import de.greenrobot.event.EventBus;

public class RemoteControlReceiver extends BroadcastReceiver {
    private MusicService musicService;
    private AudioManager audioManager;
    private ComponentName componentName;

    private boolean isPlaying;

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
                    DataAccessor.SINGLE_INSTANCE.playNextSong();
                EventBus.getDefault().post(new PlayerReceivingEvent(PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_PLAY));
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (keyAction == KeyEvent.ACTION_UP) {
                    EventBus.getDefault().post(new PlayerReceivingEvent(PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_SWITCH_PLAYSTATE));
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                if (keyAction == KeyEvent.ACTION_UP)

                break;
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if (keyAction == KeyEvent.ACTION_UP) {
                    EventBus.getDefault().post(new PlayerReceivingEvent(PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_SWITCH_PLAYSTATE));
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

    public void onEvent(PlayerSendingEvent playerSendingEvent) {
        switch (playerSendingEvent.serviceCanSend) {
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_STATE_REPORT:
                this.isPlaying = playerSendingEvent.playStateKey;
                break;
        }
    }

}