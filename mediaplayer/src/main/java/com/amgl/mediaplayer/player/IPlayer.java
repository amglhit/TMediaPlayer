package com.amgl.mediaplayer.player;


import android.view.SurfaceHolder;

import com.amgl.mediaplayer.IOnPreparedListener;
import com.amgl.mediaplayer.IPlayerListener;
import com.amgl.mediaplayer.player.PlayerState;

/**
 * Created by 阿木 on 2017/5/3.
 */

public interface IPlayer {
    String getUrl();

    void restart(boolean fromStart);

    void addPlayerListener(IPlayerListener listener);

    void removePlayerListener(IPlayerListener listener);

    void addOnPreparedListener(IOnPreparedListener listener);

    void removeOnPreparedListener(IOnPreparedListener listener);

    void setDataSource(String url);

    void prepare();

    void prepare(int startPosition);

    void start();

    void stop();

    void pause();

    void resume(boolean autoStart);

    void release();

    void reset();

    void seekTo(int position, boolean autoStart);

    int getCurrentPosition();

    int getDuration();

    boolean isCanPlayback();

    boolean isPlaying();

    boolean isPaused();

    int getBufferingPercent();

    PlayerState getPlayerState();

    void setDisplay(SurfaceHolder surfaceHolder);

    int getLastPosition();

    boolean isBuffering();
}
