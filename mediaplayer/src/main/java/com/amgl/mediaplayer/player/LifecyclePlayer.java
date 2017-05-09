package com.amgl.mediaplayer.player;

import android.text.TextUtils;
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

    private PlayerData mPlayerData;

    private SurfaceHolder mSurfaceHolder;

//    private int mLastPosition = 0;
//    private String mLastUrl = "";
//    private PlayerState mLastState = PlayerState.IDLE;

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
//                restorePlayerState();
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
//                savePlayerState();
            }
        }
    };

    public void onCreate() {
        Timber.d("on create");
        initPlayer();
    }

    private boolean mIsNeedRestoreState = false;

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
            restorePlayerState(mPlayerData, mPlayer);
        }
    }

    public void onStop() {
        Timber.d("on stop");
        isVisible = false;
        if (mPlayer != null) {
            mPlayerData = savePlayerState(mPlayer);
            mPlayer.release();
        }
    }

    private IOnPreparedListener mOnPreparedListener = new IOnPreparedListener() {
        @Override
        public void onPrepared(int startPosition) {
            if (mPlayer != null && isVisible) {
                LifecyclePlayer.this.onPrepared(mPlayer, startPosition, mPlayerData != null ? mPlayerData.playerState : PlayerState.IDLE);
            }
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
            if (isVisible && mPlayer != null) {
                if (start) {
                    Timber.d("start player on seek complete");
                    mPlayer.start();
                } else {
                    Timber.d("pause player on seek complete");
                    mPlayer.pause();
                }
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
     * 保存播放状态
     *
     * @return
     */
    private static PlayerData savePlayerState(IPlayer player) {
        if (player == null) {
            return null;
        }
        PlayerData playerData = new PlayerData();

        final PlayerState state = player.getPlayerState();
        playerData.playerState = state;

        if (state == PlayerState.RELEASED || state == PlayerState.IDLE) {
            return playerData;
        }

        playerData.url = player.getUrl();

        if (state == PlayerState.STARTED) {
            player.pause();
            playerData.position = player.getCurrentPosition();
            playerData.needRestore = true;
        } else if (state == PlayerState.PAUSED) {
            playerData.position = player.getCurrentPosition();
            playerData.needRestore = true;
        } else if (player.isCanPlayback()) {
            playerData.position = player.getCurrentPosition();
            playerData.needRestore = true;
        } else {

        }
        Timber.d("store player state on hide, state: %s, position: %s; saved: %s", playerData.playerState, playerData.position, playerData.needRestore);
        return playerData;
    }

    /**
     * 恢复播放器状态
     *
     * @param playerData
     */
    private static void restorePlayerState(PlayerData playerData, IPlayer player) {
        if (player == null || playerData == null)
            return;

        int lastPosition = playerData.position;
        final PlayerState lastState = playerData.playerState;
        String lastUrl = playerData.url;

        final PlayerState currentState = player.getPlayerState();

        Timber.d("restore player state: preStat: %s; currentStat:%s", lastState, currentState);

        if (lastState == PlayerState.PREPARING || lastState == PlayerState.STARTED || lastState == PlayerState.PAUSED) {
            if (currentState == PlayerState.INITIALIZED) {
                player.prepare(lastPosition);
                Timber.d("prepare");
            } else if (player.isCanPlayback()) {
                Timber.d("resume and start");
                player.resume(true);
            } else if (currentState == PlayerState.STOPPED) {
                player.prepare(lastPosition);
            } else if (currentState == PlayerState.RELEASED) {
                player.reset();
                if (!TextUtils.isEmpty(lastUrl)) {
                    startPlayer(player, lastUrl, lastPosition);
                }
            } else if (currentState == PlayerState.IDLE) {
                if (!TextUtils.isEmpty(lastUrl)) {
                    startPlayer(player, lastUrl, lastPosition);
                }
            }
        } else if (lastState == PlayerState.ERROR) {
            restartPlayer(player, lastUrl);
        }
    }

    private static void startPlayer(IPlayer player, String lastUrl, int startPosition) {
        Timber.d("start, url: %s; position: %s;", lastUrl, startPosition);
        player.setDataSource(lastUrl);
        player.prepare(startPosition);
    }

    private static void restartPlayer(IPlayer player, String lastUrl) {
        int startPosition = player.getLastPosition();
        player.reset();
        startPlayer(player, lastUrl, startPosition);
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

    /**
     * 调用时需要判断是否在前台
     *
     * @param player
     * @param startPosition
     * @param lastState
     */
    private static void onPrepared(IPlayer player, int startPosition, PlayerState lastState) {
        if (player == null)
            return;

        if (lastState == null) {
            lastState = PlayerState.IDLE;
        }

//        if (!isVisible)
//            return;

        Timber.d("on prepared, preStat:%s, startPos:%s", lastState, startPosition);

        if (lastState == PlayerState.PAUSED) {
            if (startPosition > 0) {
                player.seekTo(startPosition, false);
            } else {
                player.start();
            }
        } else if (lastState == PlayerState.STARTED) {
            if (startPosition > 0) {
                player.seekTo(startPosition, true);
            } else {
                player.start();
            }
        } else if (lastState == PlayerState.IDLE || lastState == PlayerState.PREPARED) {
            //第一次播放时（IDLE）或者退出时状态为prepared时。
            Timber.d("start");
            player.start();
        } else if (lastState == PlayerState.ERROR) {
            if (startPosition > 0) {
                player.seekTo(startPosition, true);
            } else {
                player.start();
            }
        } else {
            Timber.d("do nothing: %s", lastState);
        }
    }

    public IPlayer getPlayer() {
        return mPlayer;
    }
}
