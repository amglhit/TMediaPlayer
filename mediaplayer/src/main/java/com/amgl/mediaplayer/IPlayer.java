package com.amgl.mediaplayer;


/**
 * Created by 阿木 on 2017/5/3.
 */

public interface IPlayer {
    void prepare(String url);

    void start();

    void stop();

    void pause();

    void resume();

    void release();

    void reset();

    void seekTo(int position);

    int getCurrentPosition();

    int getDuration();

    boolean isCanPlayback();

    boolean isPlaying();
}
