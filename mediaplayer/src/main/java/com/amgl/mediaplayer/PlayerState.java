package com.amgl.mediaplayer;

/**
 * Created by 阿木 on 2017/5/3.
 */

public enum PlayerState {
    IDLE,
    PREPARING,
    PREPARED,
    STARTED,
    PLAYING,
    PAUSED,
    STOPPED,
    COMPLETE,
    RELEASED,
    ERROR,
    BUFFERING
}
