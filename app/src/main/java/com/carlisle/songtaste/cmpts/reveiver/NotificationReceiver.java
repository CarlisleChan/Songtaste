package com.carlisle.songtaste.cmpts.reveiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.cmpts.events.FavoriteEvent;
import com.carlisle.songtaste.cmpts.events.PlayerReceivingEvent;
import com.carlisle.songtaste.cmpts.events.PlayerSendingEvent;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.services.DataAccessor;
import com.carlisle.songtaste.ui.main.MainActivity;
import com.carlisle.songtaste.utils.Common;
import com.carlisle.songtaste.utils.LocalSongHelper;

import de.greenrobot.event.EventBus;

public class NotificationReceiver extends BroadcastReceiver {
    private Service musicService;
    private RemoteViews remoteViews;
    private Notification notification;
    private NotificationCompat.Builder builder;

    private SongDetailInfo songDetailInfo;
    private String playStatus = "";

    public NotificationReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(Common.Notification.NOTIFICATION_ACTION_BUTTON)) {
            int buttonId = intent.getIntExtra(
                    Common.Notification.INTENT_BUTTONID_TAG, 0);
            switch (buttonId) {
                case Common.Notification.NOTIFICATION_NEXT:
                    DataAccessor.SINGLE_INSTANCE.playNextSong();
                    EventBus.getDefault().post(new PlayerReceivingEvent(PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_PLAY));
                    break;
                case Common.Notification.NOTIFICATION_PAUSE:
                    EventBus.getDefault().post(new PlayerReceivingEvent(PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_SWITCH_PLAYSTATE));
                    break;
                case Common.Notification.NOTIFICATION_FAVORITE:
                    boolean isCollection = songDetailInfo.getIscollection().equals("1") ? false : true;
                    EventBus.getDefault().post(new FavoriteEvent(isCollection));
                    break;
                case Common.Notification.NOTIFICATION_CLOSE:
                    EventBus.getDefault().post(new PlayerReceivingEvent(PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_PAUSE));
                    clearNotification();
                    break;
            }
        }
    }

    public void register(Service musicService) {
        this.musicService = musicService;
        this.builder = new NotificationCompat.Builder(musicService);
        this.remoteViews = new RemoteViews(musicService.getPackageName(), R.layout.notification);
        EventBus.getDefault().register(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Common.Notification.NOTIFICATION_ACTION_BUTTON);
        musicService.registerReceiver(this, intentFilter);
    }

    public void unRegister() {
        musicService.unregisterReceiver(this);
        EventBus.getDefault().unregister(this);
    }

    public void setAlbumArt(SongDetailInfo songDetailInfo) {
        if (songDetailInfo.songType == SongDetailInfo.SongType.LOCAL_SONG) {
            remoteViews.setImageViewBitmap(R.id.ib_album_art, LocalSongHelper.getArtwork(musicService.getApplicationContext(), Long.parseLong(songDetailInfo.mediaId),
                    Long.parseLong(songDetailInfo.albumid), true));
        } else {
//            Picasso.with(musicService.getApplicationContext())
//                    .load(songDetailInfo.getAlbumArt())
//                    .placeholder(R.drawable.ic_media_status)
//                    .into(remoteViews, R.id.song_icon, 1000, notification);
        }
    }

    public void onEvent(FavoriteEvent event) {
        if (event.isCollection) {
            remoteViews.setImageViewResource(R.id.ib_favorite, R.drawable.ic_btn_loved);
        } else {
            remoteViews.setImageViewResource(R.id.ib_favorite, R.drawable.ic_btn_love_white);
        }
        songDetailInfo.setIscollection(event.isCollection ? "1" : "0");
        showNotification();
    }

    public void onEvent(PlayerSendingEvent playerSendingEvent) {
        if (playStatus.equals(playerSendingEvent.serviceCanSend)) {
            return;
        } else {
            playStatus = playerSendingEvent.serviceCanSend;
        }
        switch (playerSendingEvent.serviceCanSend) {
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PLAYING:
                remoteViews.setImageViewResource(R.id.ib_pause_btn, android.R.color.transparent);
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PAUSED:
                remoteViews.setImageViewResource(R.id.ib_pause_btn, R.drawable.bottom_btn_play);
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_ERROR_OCCURRED:
                clearNotification();
                break;
        }

        songDetailInfo = DataAccessor.SINGLE_INSTANCE.getPlayingSong();
        setAlbumArt(songDetailInfo);
        remoteViews.setTextViewText(R.id.ib_song_name, songDetailInfo.getSong_name());
        remoteViews.setTextViewText(R.id.ib_singer_name, songDetailInfo.getSinger_name());

        if (songDetailInfo.getSongType() == SongDetailInfo.SongType.SONGTASTE_SONG && songDetailInfo.getIscollection() != null) {
            if (songDetailInfo.getIscollection().equals("1")) {
                remoteViews.setImageViewResource(R.id.ib_favorite, R.drawable.ic_btn_loved);
            } else {
                remoteViews.setImageViewResource(R.id.ib_favorite, R.drawable.ic_btn_love_white);
            }
        }

        showNotification();
    }

    public void showNotification() {
        Intent buttonIntent = new Intent(Common.Notification.NOTIFICATION_ACTION_BUTTON);
        buttonIntent.putExtra(Common.Notification.INTENT_BUTTONID_TAG,
                Common.Notification.NOTIFICATION_NEXT);
        PendingIntent intent_next = PendingIntent.getBroadcast(musicService, 1,
                buttonIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ib_next_btn, intent_next);

        buttonIntent.putExtra(Common.Notification.INTENT_BUTTONID_TAG,
                Common.Notification.NOTIFICATION_PAUSE);
        PendingIntent intent_pause = PendingIntent.getBroadcast(musicService, 2,
                buttonIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ib_pause_btn,
                intent_pause);

        buttonIntent.putExtra(Common.Notification.INTENT_BUTTONID_TAG,
                Common.Notification.NOTIFICATION_FAVORITE);
        PendingIntent intent_favorite = PendingIntent.getBroadcast(musicService, 3,
                buttonIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ib_favorite, intent_favorite);

        buttonIntent.putExtra(Common.Notification.INTENT_BUTTONID_TAG,
                Common.Notification.NOTIFICATION_CLOSE);
        PendingIntent intent_close = PendingIntent.getBroadcast(musicService, 4,
                buttonIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ib_close_btn,
                intent_close);

        // 点击回到播放器主界面
        Intent notificationIntent = new Intent(musicService, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(musicService, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContent(remoteViews)
                .setContentIntent(contentIntent)
                        // 单击事件
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT).setOngoing(true)
                .setSmallIcon(R.drawable.ic_media_status);

        notification = builder.build();

        // notify.flags = Notification.FLAG_NO_CLEAR;
        // mNotificationManager.notify(200, notify);

        musicService.startForeground(Notification.FLAG_ONGOING_EVENT, notification);
    }

    // clear notify
    public void clearNotification() {
        musicService.stopForeground(true);
    }
}