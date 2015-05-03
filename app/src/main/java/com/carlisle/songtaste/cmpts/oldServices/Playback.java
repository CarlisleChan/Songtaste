/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.carlisle.songtaste.cmpts.oldServices;

import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.services.MusicService;

/**
 * Interface representing either Local or Remote Playback. The {@link MusicService} works
 * directly with an instance of the Playback object to make the various calls such as
 * play, pause etc.
 */
public interface Playback {

    public final static int STATE_NONE = 0;
    public final static int STATE_STOPPED = 1;
    public final static int STATE_PAUSED = 2;
    public final static int STATE_PLAYING = 3;
    public final static int STATE_FAST_FORWARDING = 4;
    public final static int STATE_REWINDING = 5;
    public final static int STATE_BUFFERING = 6;
    public final static int STATE_ERROR = 7;
    public final static int STATE_CONNECTING = 8;
    public final static int STATE_SKIPPING_TO_PREVIOUS = 9;
    public final static int STATE_SKIPPING_TO_NEXT = 10;
    public final static int STATE_SKIPPING_TO_QUEUE_ITEM = 11;
    public final static long PLAYBACK_POSITION_UNKNOWN = -1;


    /**
     * Start/setup the playback.
     * Resources/listeners would be allocated by implementations.
     */
    void start();

    /**
     * Stop the playback. All resources can be de-allocated by implementations here.
     * @param notifyListeners if true and a callback has been set by setCallback,
     *                        callback.onPlaybackStatusChanged will be called after changing
     *                        the state.
     */
    void stop(boolean notifyListeners);

    /**
     * Set the latest playback state as determined by the caller.
     */
    void setState(int state);

    /**
     * Get the current {@link android.media.session.PlaybackState#getState()}
     */
    int getState();

    /**
     * @return boolean that indicates that this is ready to be used.
     */
    boolean isConnected();

    /**
     * @return boolean indicating whether the player is playing or is supposed to be
     * playing when we gain audio focus.
     */
    boolean isPlaying();

    /**
     * @return pos if currently playing an item
     */
    int getCurrentStreamPosition();

    /**
     * Set the current position. Typically used when switching players that are in
     * paused state.
     *
     * @param pos position in the stream
     */
    void setCurrentStreamPosition(int pos);

    /**
     * @param item to play
     */
    void play(SongDetailInfo item);

    /**
     * Pause the current playing item
     */
    void pause();

    /**
     * Seek to the given position
     */
    void seekTo(int position);

    /**
     * Set the current mediaId. This is only used when switching from one
     * playback to another.
     *
     * @param mediaId to be set as the current.
     */
    void setCurrentMediaUrl(String mediaId);

    /**
     *
     * @return the current media Id being processed in any state or null.
     */
    String getCurrentMediaUrl();

    interface Callback {
        /**
         * On current music completed.
         */
        void onCompletion();
        /**
         * on Playback status changed
         * Implementations can use this callback to update
         * playback state on the media sessions.
         */
        void onPlaybackStatusChanged(int state);

        /**
         * @param error to be added to the PlaybackState
         */
        void onError(String error);

        /**
         * @param mediaId being currently played
         */
        void onMetadataChanged(String mediaId);
    }

    /**
     * @param callback to be called
     */
    void setCallback(Callback callback);
}