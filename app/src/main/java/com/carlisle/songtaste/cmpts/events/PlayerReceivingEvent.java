package com.carlisle.songtaste.cmpts.events;

/**
 * Created by chengxin on 4/17/15.
 */
public class PlayerReceivingEvent {
    /**
     * Service can handle
     */
    public static final String PLAYER_RECEIVING_BROADCAST_CATEGORY_PLAY = "player_receiving_broadcast_category_play";
    public static final String PLAYER_RECEIVING_BROADCAST_CATEGORY_PLAY_NEXT = "player_receiving_broadcast_category_play_next";
    public static final String PLAYER_RECEIVING_BROADCAST_CATEGORY_PAUSE = "player_receiving_broadcast_category_pause";
    public static final String PLAYER_RECEIVING_BROADCAST_CATEGORY_SWITCH_PLAYSTATE = "player_receiving_broadcast_category_switch_playstate";
    public static final String PLAYER_RECEIVING_BROADCAST_CATEGORY_REQUEST_PLAYSTATE = "player_receiving_broadcast_category_request_paystate";

    public String serviceCanHandle;
    public PlayerReceivingEvent(String serviceCanHandle) {
        this.serviceCanHandle = serviceCanHandle;
    }
}
