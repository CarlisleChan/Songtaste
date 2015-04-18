package com.carlisle.songtaste.cmpts.reveiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.RemoteControlClient;

import com.carlisle.songtaste.cmpts.events.ScreenOnEvent;
import com.carlisle.songtaste.cmpts.events.UpdatePlaybackEvent;
import com.carlisle.songtaste.cmpts.services.MusicService;
import com.carlisle.songtaste.utils.Common;

import de.greenrobot.event.EventBus;

public class ScreenOnReceiver extends BroadcastReceiver {
    private MusicService musicService;
    private ComponentName componentName;
    private AudioManager audioManager;
    private RemoteControlClient remoteControlClient;

    private static String singerName = "";
    private static String songName = "";

    public ScreenOnReceiver(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public ScreenOnReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(Common.Screen.SCREEN_ON_ACTION)) {
            EventBus.getDefault().post(new ScreenOnEvent());
        }
    }

    public void register(MusicService musicService) {
        this.audioManager = (AudioManager) musicService.getSystemService(Context.AUDIO_SERVICE);
        this.musicService = musicService;
        this.componentName = new ComponentName(musicService.getPackageName(), RemoteControlReceiver.class.getName());
        EventBus.getDefault().register(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Common.Screen.SCREEN_ON_ACTION);
        musicService.registerReceiver(this, intentFilter);
    }

    public void unRegister() {
        musicService.unregisterReceiver(this);
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(UpdatePlaybackEvent event) {
        if (event.songDetailInfo != null) {
//            state = Playback.STATE_PLAYING;
            singerName = event.songDetailInfo.getSinger_name();
            songName = event.songDetailInfo.getSong_name();
        } else {
//            state = event.state;
        }
        showRemoteControl();
    }

    public void showRemoteControl() {
        // build the PendingIntent for the remote control client
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(componentName);
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(musicService, 0,
                mediaButtonIntent, 0);

        // create and register the remote control client
        remoteControlClient = new RemoteControlClient(mediaPendingIntent);
        audioManager.registerRemoteControlClient(remoteControlClient);

//        switch (state) {
//            case Playback.STATE_PAUSED:
//                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
//                break;
//            case Playback.STATE_STOPPED:
//                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
//                break;
//            case Playback.STATE_NONE:
//                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
//                break;
//            default:
//                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
//                break;
        }

//        remoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
//                | RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
//                | RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
//
//        update remote controls
//        remoteControlClient
//                .editMetadata(true)
//                .putString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, singerName)
//                .putString(MediaMetadataRetriever.METADATA_KEY_TITLE, songName)
//                .putBitmap(RemoteControlReceiver.METADATA_KEY_ARTWORK,song_bm_locked)
//                .apply();
//    }

}
