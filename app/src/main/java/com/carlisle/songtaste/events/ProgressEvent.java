package com.carlisle.songtaste.events;

/**
 * Created by carlisle on 3/24/15.
 */
public class ProgressEvent {
    public int currentPosition;
    public int maxPosition;
    public boolean trackTouch = false;

    public ProgressEvent(int currentPosition, int maxPosition, boolean trackTouch) {
        this.currentPosition = currentPosition;
        this.maxPosition = maxPosition;
        this.trackTouch = trackTouch;
    }
}
