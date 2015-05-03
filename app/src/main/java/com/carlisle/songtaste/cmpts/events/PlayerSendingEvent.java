package com.carlisle.songtaste.cmpts.events;

/**
 * Created by chengxin on 4/17/15.
 */
public class PlayerSendingEvent {
    /**
     * Service can send
     */
    public static final String PLAYER_SENDING_BROADCAST_CATEGORY_CURRENT_POSITION = "player_sending_category_current_position";
    public static final String PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_PREPARE = "player_sending_category_player_will_prepare";
    public static final String PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PREPARED = "player_sending_category_player_prepared";
    public static final String PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_PLAY = "player_sending_category_player_will_play";
    public static final String PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PLAYING = "player_sending_category_player_playing";
    public static final String PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_PAUSE = "player_sending_category_player_will_pause";
    public static final String PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PAUSED = "player_sending_category_player_paused";
    public static final String PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_STOP = "player_sending_category_player_will_stop";
    public static final String PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_STOPPED = "player_sending_category_player_stopped";
    public static final String PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_ERROR_OCCURRED = "player_sending_category_player_error_occurred";
    public static final String PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_COMPLETE = "player_sending_category_player_complete";
    public static final String PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_STATE_REPORT = "player_sending_category_player_state_report";

    public static final String PLAYER_SERVICE_BROADCAST_EXTRA_CURRENT_POSITION_KEY = "player_service_category_extra_current_position_key";
    public static final String PLAYER_SERVICE_BROADCAST_EXTRA_DURATION_KEY = "player_service_category_extra_current_duration_key";
    public static final String PLAYER_SERVICE_BROADCAST_EXTRA_ERROR_KEY = "player_service_category_extra_tune_path_key";
    public static final String PLAYER_SERVICE_BROADCAST_EXTRA_PLAYSTATE_KEY = "player_service_category_extra_playstate_key";

    public String serviceCanSend;
    public String errorKey;
    public long currentPosition = 0;
    public long length = 0;
    public boolean playStateKey = false;

    public PlayerSendingEvent(String serviceCanSend) {
        this.serviceCanSend = serviceCanSend;
    }

    public PlayerSendingEvent(String serviceCanSend, long currentPosition) {
        this.serviceCanSend = serviceCanSend;
        this.currentPosition = currentPosition;
    }

    public PlayerSendingEvent(String serviceCanSend, long currentPosition, long length) {
        this.serviceCanSend = serviceCanSend;
        this.currentPosition = currentPosition;
        this.length = length;
    }

    public PlayerSendingEvent(String serviceCanSend, String errorKey) {
        this.serviceCanSend = serviceCanSend;
        this.errorKey = errorKey;
    }

    public PlayerSendingEvent(String serviceCanSend, boolean playStateKey) {
        this.serviceCanSend = serviceCanSend;
        this.playStateKey = playStateKey;
    }

}
