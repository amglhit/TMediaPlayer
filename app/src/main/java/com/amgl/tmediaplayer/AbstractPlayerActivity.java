package com.amgl.tmediaplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;

import com.amgl.mediaplayer.player.IPlayer;
import com.amgl.mediaplayer.wrapper.LifecyclePlayer;
import com.amgl.mediaplayer.wrapper.PlayerHelper;

/**
 * Created by 阿木 on 2017/5/9.
 */

public abstract class AbstractPlayerActivity extends AppCompatActivity {
    private LifecyclePlayer mLifecycleWrapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLifecycleWrapper = new LifecyclePlayer();
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

    protected void startPlay(String url, SurfaceHolder surfaceHolder) {
        PlayerHelper.restartPlayer(mLifecycleWrapper.getPlayer(), url);
        mLifecycleWrapper.getPlayer().setDisplay(surfaceHolder);
    }

    protected void stopPlay() {
        mLifecycleWrapper.stopPlayer();
    }

}
