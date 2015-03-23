package com.carlisle.songtaste.utils;

import com.carlisle.songtaste.modle.SongDetailInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlisle on 3/21/15.
 */
public class QueueHelper {
    private static QueueHelper queueHelper;
    private static QueueType queueType = QueueType.LOCAL_QUEUE;

    public enum QueueType {
        NEW_QUEUE,
        HOT_QUEUE,
        ALBUM_DETAIL_QUEUE,
        TAG_DEAIL_QUEUE,
        FAVORITE_QUEUE,
        OFFLINE_QUEUE,
        LOCAL_QUEUE
    }

    private List<SongDetailInfo> newQueue = new ArrayList<>();
    private List<SongDetailInfo> hotQueue = new ArrayList<>();
    private List<SongDetailInfo> albumDetailQueue = new ArrayList<>();
    private List<SongDetailInfo> tagDetailQueue = new ArrayList<>();
    private List<SongDetailInfo> favoriteQueue = new ArrayList<>();
    private List<SongDetailInfo> offlineQueue = new ArrayList<>();
    private List<SongDetailInfo> localQueue = new ArrayList<>();

    public static QueueHelper getInstance() {
        if (queueHelper == null) {
            queueHelper = new QueueHelper();
        }
        return queueHelper;
    }

    public void setCurrentQueue(QueueType queueType) {
        this.queueType = queueType;
    }

    public List<SongDetailInfo> getCurrentQueue() {
        switch (queueType) {
            case NEW_QUEUE:
                return getNewQueue();
            case HOT_QUEUE:
                return getHotQueue();
            case ALBUM_DETAIL_QUEUE:
                return getAlbumDetailQueue();
            case TAG_DEAIL_QUEUE:
                return getTagDetailQueue();
            case FAVORITE_QUEUE:
                return getFavoriteQueue();
            case OFFLINE_QUEUE:
                return getOfflineQueue();
            case LOCAL_QUEUE:
                return getLocalQueue();
        }
        return null;
    }

    public boolean setLocalSongQueue(List<SongDetailInfo> queue) {
        this.localQueue = queue;
        return true;
    }

    public List<SongDetailInfo> getLocalQueue() {
        return localQueue;
    }

    public List<SongDetailInfo> getNewQueue() {
        return newQueue;
    }

    public List<SongDetailInfo> getHotQueue() {
        return hotQueue;
    }

    public List<SongDetailInfo> getAlbumDetailQueue() {
        return albumDetailQueue;
    }

    public List<SongDetailInfo> getTagDetailQueue() {
        return tagDetailQueue;
    }

    public List<SongDetailInfo> getFavoriteQueue() {
        return favoriteQueue;
    }

    public List<SongDetailInfo> getOfflineQueue() {
        return offlineQueue;
    }

    public boolean isIndexPlayable(int index) {
        return (getCurrentQueue() != null && index >= 0 && index < getCurrentQueue().size());
    }

}
