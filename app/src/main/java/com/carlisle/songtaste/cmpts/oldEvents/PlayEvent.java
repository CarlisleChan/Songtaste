package com.carlisle.songtaste.cmpts.oldEvents;

/**
 * Created by carlisle on 3/22/15.
 */
public class PlayEvent {
    private static final String TAG = PlayEvent.class.getSimpleName();
    public int position = -1;

    public PlayEvent() {

    }

    public PlayEvent(int position) {
        this.position = position;
    }
}
