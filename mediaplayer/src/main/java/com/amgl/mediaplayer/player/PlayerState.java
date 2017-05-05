package com.amgl.mediaplayer.player;

/**
 * Created by 阿木 on 2017/5/3.
 */

public enum PlayerState {
    IDLE,
    INITIALIZED,
    PREPARING,
    PREPARED,
    STARTED,
    PAUSED,
    STOPPED,
    COMPLETE,
    RELEASED,
    ERROR
}
