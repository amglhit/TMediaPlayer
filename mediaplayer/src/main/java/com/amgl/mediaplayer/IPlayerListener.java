package com.amgl.mediaplayer;

/**
 * Created by 阿木 on 2017/5/4.
 */

public interface IPlayerListener {
    void onBufferingStart();

    void onBufferingEnd();

    void onError();

    void onComplete();

    void onStart();

    void onStop();

    void onPaused();

    void onSeekComplete(boolean start);

    void onFirstFrameAppeared();

    void onReset();

    void onProgress(int progress);

    void onBuffering(int percent);
}
