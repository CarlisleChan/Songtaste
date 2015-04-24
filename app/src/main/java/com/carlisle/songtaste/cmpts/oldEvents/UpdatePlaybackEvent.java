package com.carlisle.songtaste.cmpts.oldEvents;

import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;

/**
 * Created by carlisle on 3/22/15.
 */
public class UpdatePlaybackEvent {
    private static final String TAG = UpdatePlaybackEvent.class.getSimpleName();

    public SongDetailInfo songDetailInfo;
    public int state;

    public UpdatePlaybackEvent() {

    }

    public UpdatePlaybackEvent(int state) {
        this.state = state;
    }

    public UpdatePlaybackEvent(SongDetailInfo songDetailInfo) {
        this.songDetailInfo = songDetailInfo;
    }
}
