package com.carlisle.songtaste.cmpts.events;

import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;

/**
 * Created by carlisle on 3/22/15.
 */
public class UpdateUIEvent {
    private static final String TAG = UpdateUIEvent.class.getSimpleName();

    public SongDetailInfo songDetailInfo;
    public int state;

    public UpdateUIEvent() {

    }

    public UpdateUIEvent(int state) {
        this.state = state;
    }

    public UpdateUIEvent(SongDetailInfo songDetailInfo) {
        this.songDetailInfo = songDetailInfo;
    }
}
