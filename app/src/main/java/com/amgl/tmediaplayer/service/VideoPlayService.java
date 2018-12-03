package com.amgl.tmediaplayer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Binder;
import android.os.IBinder;

import com.amgl.mediaplayer.VideoPlayerManager;
import com.amgl.mediaplayer.player.IPlayer;
import com.amgl.mediaplayer.wrapper.PlayerHelper;

import timber.log.Timber;

public class VideoPlayService extends Service {
    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, VideoPlayService.class);
        return intent;
    }

    public static void intentToStart(Context context) {
        context.startService(newIntent(context));
    }

    public static void intentToStop(Context context) {
        context.stopService(newIntent(context));
    }

    public VideoPlayService() {
    }

    private VideoPlayerManager mVideoPlayerManager;

    @Override
    public IBinder onBind(Intent intent) {
        Timber.d("on bind");
        return new PlayerBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Timber.d("on unbind");
        return super.onUnbind(intent);
    }

    //    @IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("on start command");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("on create");
        mVideoPlayerManager = new VideoPlayerManager();
        mVideoPlayerManager.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("on destroy");
        if (mVideoPlayerManager != null) {
            mVideoPlayerManager.onDestroy();
        }
    }

    protected IPlayer getPlayer() {
        if (mVideoPlayerManager != null) {
            return mVideoPlayerManager.getPlayer();
        }
        return null;
    }

    private void startPlay(String url, SurfaceTexture surfaceTexture) {
        Timber.d("start play");
        if (getPlayer() != null) {
            getPlayer().reset();
            getPlayer().setSurface(surfaceTexture);
        }
        PlayerHelper.startPlayer(getPlayer(), url, 0);
    }

    private void startPlay(String url) {
        if (getPlayer() != null) {
            getPlayer().reset();
        }
        PlayerHelper.startPlayer(getPlayer(), url, 0);
    }

    private void stopPlay() {
        Timber.d("stop play");
        if (getPlayer() != null) {
            getPlayer().stop();
        }
    }

    public class PlayerBinder extends Binder {
        public VideoPlayerManager getVideoPlayManager() {
            return VideoPlayService.this.mVideoPlayerManager;
        }

        public IPlayer getPlayer() {
            return VideoPlayService.this.getPlayer();
        }

        public void startPlay(String url, SurfaceTexture surfaceTexture) {
            VideoPlayService.this.startPlay(url, surfaceTexture);
        }

        public void startPlay(String url) {
            VideoPlayService.this.startPlay(url);
        }

        public void stopPlay() {
            VideoPlayService.this.stopPlay();
        }

        public void pause() {
            if (getPlayer() != null) {
                getPlayer().pause();
            }
        }

        public void resume() {
            if (getPlayer() != null) {
                getPlayer().resume(true);
            }
        }
    }
}
