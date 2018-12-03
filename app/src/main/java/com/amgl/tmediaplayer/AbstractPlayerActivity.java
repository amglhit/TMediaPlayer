package com.amgl.tmediaplayer;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.amgl.mediaplayer.player.IPlayer;
import com.amgl.mediaplayer.VideoPlayerManager;
import com.amgl.mediaplayer.wrapper.PlayerHelper;

/**
 * Created by 阿木 on 2017/5/9.
 */

public abstract class AbstractPlayerActivity extends AppCompatActivity {
    private VideoPlayerManager mLifecycleWrapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLifecycleWrapper = new VideoPlayerManager();
        mLifecycleWrapper.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLifecycleWrapper.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLifecycleWrapper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLifecycleWrapper.onStop();
    }

    protected IPlayer getPlayer() {
        if (mLifecycleWrapper != null) {
            return mLifecycleWrapper.getPlayer();
        }
        return null;
    }

    protected void startPlay(String url, SurfaceTexture surfaceTexture) {
        if (getPlayer() != null) {
            getPlayer().reset();
            getPlayer().setSurface(surfaceTexture);
        }
        PlayerHelper.startPlayer(getPlayer(), url, 0);
    }

    protected void startPlay(String url) {
        if (getPlayer() != null) {
            getPlayer().reset();
        }
        PlayerHelper.startPlayer(getPlayer(), url, 0);
    }

    protected void stopPlay() {
        if (getPlayer() != null) {
            getPlayer().stop();
        }
    }

}
