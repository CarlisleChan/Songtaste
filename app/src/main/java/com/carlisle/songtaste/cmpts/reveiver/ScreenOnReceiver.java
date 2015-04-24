package com.carlisle.songtaste.cmpts.reveiver;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.util.Log;

import com.carlisle.songtaste.cmpts.events.PlayerSendingEvent;
import com.carlisle.songtaste.cmpts.events.ScreenOnEvent;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.services.DataAccessor;
import com.carlisle.songtaste.utils.Common;

import de.greenrobot.event.EventBus;

public class ScreenOnReceiver extends BroadcastReceiver {
    private Service musicService;
    private ComponentName componentName;
    private AudioManager audioManager;
    private RemoteControlClient remoteControlClient;
    private SongDetailInfo songDetailInfo;

    private static String singerName = "";
    private static String songName = "";
    private boolean isFirstReceive = true;

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
            isFirstReceive = true;
        }
    }

    public void register(Service musicService) {
        this.audioManager = (AudioManager) musicService.getSystemService(Context.AUDIO_SERVICE);
        this.musicService = musicService;
        this.componentName = new ComponentName(musicService.getPackageName(), ScreenOnReceiver.class.getName());
        EventBus.getDefault().register(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Common.Screen.SCREEN_ON_ACTION);
        musicService.registerReceiver(this, intentFilter);
    }

    public void unRegister() {
        musicService.unregisterReceiver(this);
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(PlayerSendingEvent playerSendingEvent) {
        if (!isFirstReceive) return;
        isFirstReceive = false;
        Log.d("PlayerSendingEvent====>","receive");

        songDetailInfo = DataAccessor.SINGLE_INSTANCE.getPlayingSong();
        singerName = songDetailInfo.getSinger_name();
        songName = songDetailInfo.getSong_name();

        // build the PendingIntent for the remote control client
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(componentName);
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(musicService, 0,
                mediaButtonIntent, 0);

        // create and register the remote control client
        remoteControlClient = new RemoteControlClient(mediaPendingIntent);
        audioManager.registerRemoteControlClient(remoteControlClient);

        switch (playerSendingEvent.serviceCanSend) {
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PLAYING:
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PAUSED:
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_STOPPED:
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                break;
        }

        remoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
                | RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
                | RemoteControlClient.FLAG_KEY_MEDIA_NEXT);

        // update remote controls
        remoteControlClient
                .editMetadata(true)
                .putString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, singerName)
                .putString(MediaMetadataRetriever.METADATA_KEY_TITLE, songName)
//                .putBitmap(RemoteControlReceiver.METADATA_KEY_ARTWORK, song_bm_locked)
                .apply();
    }

}
