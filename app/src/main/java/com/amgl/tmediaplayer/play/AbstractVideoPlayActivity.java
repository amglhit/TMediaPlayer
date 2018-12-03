package com.amgl.tmediaplayer.play;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.amgl.tmediaplayer.service.VideoPlayService;

import timber.log.Timber;

/**
 * Created by amglhit on 2017/7/1.
 */

public abstract class AbstractVideoPlayActivity extends AppCompatActivity {
    protected void startPlay(String url, SurfaceTexture surfaceTexture) {
        if (mPlayerBinder != null) {
            mPlayerBinder.startPlay(url, surfaceTexture);
        } else {
            Timber.d("play binder is null");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        startService(VideoPlayService.newIntent(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(VideoPlayService.newIntent(this), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(VideoPlayService.newIntent(this));
    }

    protected VideoPlayService.PlayerBinder mPlayerBinder = null;

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Timber.d("onServiceConnected");
            mPlayerBinder = (VideoPlayService.PlayerBinder) iBinder;
            onServiceCon();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Timber.d("onServiceDisconnected");
            mPlayerBinder = null;
            onServiceDisCon();
        }
    };

    public abstract void onServiceCon();

    public abstract void onServiceDisCon();
}
