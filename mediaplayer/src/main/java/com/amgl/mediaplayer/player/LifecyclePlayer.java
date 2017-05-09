package com.amgl.mediaplayer.player;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.amgl.mediaplayer.IOnPreparedListener;
import com.amgl.mediaplayer.IPlayerListener;

import timber.log.Timber;

/**
 * 生命周期调用的Player
 * Created by 阿木 on 2017/5/5.
 */

public class LifecyclePlayer {
    private IPlayer mPlayer;
    private boolean isVisible = false;

    private SurfaceHolder mSurfaceHolder;

    private PlayerData mPlayerData;

    public void setSurfaceView(SurfaceView surfaceView) {
        Timber.d("set surface view: %s", surfaceView.getId());
        if (mSurfaceHolder != null) {
            mSurfaceHolder.removeCallback(mCallback);
        }

        mSurfaceHolder = surfaceView.getHolder();

        if (mPlayer != null) {
            mPlayer.setDisplay(surfaceView.getHolder());
        }

        mSurfaceHolder.addCallback(mCallback);
    }


    public void onCreate() {
        Timber.d("on create");
        initPlayer();
    }

    public void onDestroy() {
        Timber.d("on destroy");
        releasePlayer();
    }

    public void onStart() {
        Timber.d("on start");
        isVisible = true;

        if (mPlayer != null && mPlayerData != null && (mPlayerData.playerState == PlayerState.RELEASED || mPlayerData.playerState == PlayerState.IDLE)) {
            Timber.d("reset player: %s", mPlayerData.playerState);
            mPlayer.reset();
        }

        if (mPlayer != null && mPlayerData != null && mPlayerData.needRestore) {
            PlayerHelper.restorePlayerState(mPlayerData, mPlayer);
        }
    }

    public void onStop() {
        Timber.d("on stop");
        isVisible = false;
        if (mPlayer != null) {
            mPlayerData = PlayerHelper.savePlayerState(mPlayer, mPlayerData);
            mPlayer.release();
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer.removePlayerListener(mPlayerListener);
            mPlayer.removeOnPreparedListener(mOnPreparedListener);
            mPlayer = null;
        }
    }

    private void initPlayer() {
        if (mPlayer != null) {
            releasePlayer();
        }

        mPlayer = new TMediaPlayer();
        mPlayer.addOnPreparedListener(mOnPreparedListener);
        mPlayer.addPlayerListener(mPlayerListener);
    }

    public IPlayer getPlayer() {
        return mPlayer;
    }

    private IOnPreparedListener mOnPreparedListener = new IOnPreparedListener() {
        @Override
        public void onPrepared(int startPosition) {
            if (mPlayer != null && isVisible) {
                PlayerHelper.onPrepared(mPlayer, startPosition, mPlayerData != null ? mPlayerData.playerState : PlayerState.IDLE);
            }
        }

        @Override
        public void onPrepareStart() {

        }
    };

    private IPlayerListener mPlayerListener = new IPlayerListener() {

        @Override
        public void onSeekComplete(boolean start) {
            if (isVisible && mPlayer != null) {
                PlayerHelper.onSeekComplete(mPlayer, start);
            }
        }

        @Override
        public void onBuffering(int percent) {
            Timber.v("on buffering: %s", percent);
        }

        @Override
        public void onProgress(int progress) {
            Timber.v("on progress: %s", progress);
        }

        @Override
        public void onBufferingStart() {
            Timber.d("on player buffering start");
        }

        @Override
        public void onBufferingEnd() {
            Timber.d("on player buffering end");
        }

        @Override
        public void onError() {
            Timber.d("on player error");
        }

        @Override
        public void onComplete() {
            Timber.d("on player complete");
        }

        @Override
        public void onStart() {
            Timber.d("on player start");
        }

        @Override
        public void onStop() {
            Timber.d("on player stop");
        }

        @Override
        public void onPaused() {
            Timber.d("on player pause");
        }

        @Override
        public void onFirstFrameAppeared() {
            Timber.d("on first frame");
        }

        @Override
        public void onReset() {
            Timber.d("player reset");
        }
    };

    private SurfaceHolder.Callback2 mCallback = new SurfaceHolder.Callback2() {
        @Override
        public void surfaceRedrawNeeded(SurfaceHolder holder) {
            Timber.d("surfaceRedrawNeeded");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Timber.d("surface created");
            mSurfaceHolder = holder;
            if (mPlayer != null) {
                mPlayer.setDisplay(mSurfaceHolder);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Timber.d("surface changed, f:%s; w:%s; h:%s;", format, width, height);
            mSurfaceHolder = holder;
            if (mPlayer != null) {
                mPlayer.setDisplay(holder);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Timber.d("surface destroyed");
            mSurfaceHolder = null;
            if (mPlayer != null) {
                mPlayer.setDisplay(null);
            }
        }
    };
}
