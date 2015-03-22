package com.carlisle.songtaste.utils;

import android.util.Log;

import com.carlisle.songtaste.modle.SongDetailInfo;
import com.carlisle.songtaste.modle.SongInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlisle on 3/21/15.
 */
public class QueueHelper {
    private static QueueHelper queueHelper;
    private static int currentQueue = 1;

    private List<SongDetailInfo> localSongQueue = new ArrayList<>();
    private List<SongDetailInfo> songtasteQueue = new ArrayList<>();

    public static QueueHelper getInstance() {
        if (queueHelper == null) {
            queueHelper = new QueueHelper();
        }
        return queueHelper;
    }

    public void setCurrentQueue(int currentQueue) {
        this.currentQueue = currentQueue;
    }

    public List<SongDetailInfo> getCurrentQueue() {
        if (currentQueue == 0) {
            return getLocalSongQueue();
        } else {
            return getSongtasteQueue();
        }
    }

    public boolean setLocalSongQueue(List<SongDetailInfo> queue) {
        this.localSongQueue = queue;
        return true;
    }

    public List<SongDetailInfo> getLocalSongQueue() {
        return localSongQueue;
    }

    public boolean setSongtasteQueue(List<SongInfo> queue) {
        return true;
    }

    public List<SongDetailInfo> getSongtasteQueue() {
        Log.d("songtasteQueue--->size",""+songtasteQueue.size());
        return songtasteQueue;
    }

    public boolean isIndexPlayable(int index) {
        return (getCurrentQueue() != null && index >= 0 && index < getCurrentQueue().size());
    }

}
