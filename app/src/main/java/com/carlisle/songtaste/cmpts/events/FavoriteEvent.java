package com.carlisle.songtaste.cmpts.events;

/**
 * Created by carlisle on 4/6/15.
 */
public class FavoriteEvent {
    public boolean isCollection;

    public FavoriteEvent(boolean isCollection) {
        this.isCollection = isCollection;
    }
}
