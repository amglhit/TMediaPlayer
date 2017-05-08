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
    private int mLastPosition = 0;

    private SurfaceHolder mSurfaceHolder;

    private PlayerState mLastState = PlayerState.IDLE;

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
                restorePlayerState();
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
                holder.setKeepScreenOn(false);
                savePlayerState();
            }
        }
    };

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
    }

    public void onStop() {
        Timber.d("on stop");
        isVisible = false;
    }

    public void onHide() {
        Timber.d("on hide");
        isVisible = false;
        releasePlayer();
//        if (mPlayer != null) {
//            savePlayerState();
//        }
    }

    public void onShow() {
        Timber.d("on show");
        isVisible = true;
//        if (mPlayer != null) {
//            restorePlayerState();
//        }
    }

    private IOnPreparedListener mOnPreparedListener = new IOnPreparedListener() {
        @Override
        public void onPrepared(int startPosition) {
            LifecyclePlayer.this.onPrepared(startPosition);
        }

        @Override
        public void onPrepareStart() {

        }
    };

    private IPlayerListener mPlayerListener = new IPlayerListener() {
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
        public void onSeekComplete(boolean start) {
            if (isVisible && start && mPlayer != null) {
                Timber.d("start player on seek complete");
                mPlayer.start();
            }
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

    /**
     * 保存播放器状态
     */
    private void savePlayerState() {
        if (mPlayer == null) {
            mLastState = PlayerState.IDLE;
            return;
        }

        final PlayerState state = mPlayer.getPlayerState();

        mLastState = state;

        if (state == PlayerState.STARTED) {
            mPlayer.pause();
            mLastPosition = mPlayer.getCurrentPosition();
        } else if (state == PlayerState.PAUSED) {
            mLastPosition = mPlayer.getCurrentPosition();
        } else if (mPlayer.isCanPlayback()) {
            mLastPosition = mPlayer.getCurrentPosition();
        } else {
            mLastPosition = 0;
        }
        Timber.d("store player state on hide, state: %s, position: %s", mLastState, mLastPosition);
    }

    /**
     * 恢复播放器状态
     */
    private void restorePlayerState() {
        if (mPlayer == null || mLastState == PlayerState.IDLE)
            return;

        final int lastPosition = mLastPosition;

        final PlayerState currentState = mPlayer.getPlayerState();

        Timber.d("restore player state: preStat: %s; currentStat:%s", mLastState, currentState);

        if (mLastState == PlayerState.PREPARING) {
            mPlayer.prepare(lastPosition);
            Timber.d("prepare, when preparing");
        } else if (mLastState == PlayerState.STARTED) {
            if (currentState == PlayerState.INITIALIZED) {
                prepare(lastPosition);
                Timber.d("prepare, when started");
            } else if (mPlayer.isCanPlayback()) {
                Timber.d("resume and auto start");
                mPlayer.resume(true);
            }
        } else if (mLastState == PlayerState.PAUSED) {
            if (currentState == PlayerState.INITIALIZED) {
                prepare(lastPosition);
                Timber.d("prepare, when init");
            }
        } else if (mLastState == PlayerState.ERROR) {
            if (mPlayer != null) {
                mPlayer.reset();
            }
        }

    }

    private void prepare(int startPosition) {
        mPlayer.prepare(startPosition);
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

    private void onPrepared(int startPosition) {
        if (mPlayer == null)
            return;

        if (!isVisible)
            return;

        Timber.d("on prepared, preStat:%s, startPos:%s", mLastState, startPosition);

        mLastPosition = 0;

        if (mLastState == PlayerState.PAUSED) {
            if (startPosition > 0) {
                mPlayer.seekTo(startPosition, false);
            } else {
                mPlayer.start();
            }
        } else if (mLastState == PlayerState.STARTED) {
            if (startPosition > 0) {
                mPlayer.seekTo(startPosition, true);
            } else {
                mPlayer.start();
            }
        } else if (mLastState == PlayerState.STOPPED || mLastState == PlayerState.COMPLETE) {
            Timber.d("do nothing");
        } else if (mLastState == PlayerState.IDLE || mLastState == PlayerState.PREPARED) {
            //第一次播放时（IDLE）或者退出时状态为prepared时。
            Timber.d("start");
            mPlayer.start();
        } else {
            Timber.d("start on prepared");
//            mPlayer.restart(false);
        }
    }

    public IPlayer getPlayer() {
        return mPlayer;
    }
}
