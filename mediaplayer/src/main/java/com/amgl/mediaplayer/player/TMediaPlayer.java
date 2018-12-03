package com.amgl.mediaplayer.player;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.amgl.mediaplayer.listener.IOnPreparedListener;
import com.amgl.mediaplayer.listener.IPlayerListener;

import java.io.IOException;

import timber.log.Timber;

/**
 * Created by 阿木 on 2017/5/3.
 * <p>
 * <p>
 * enum media_player_states {
 * MEDIA_PLAYER_STATE_ERROR        = 0,           // 0状态
 * MEDIA_PLAYER_IDLE                = 1 << 0,     // 1状态
 * MEDIA_PLAYER_INITIALIZED        = 1 << 1,      // 2 状态
 * MEDIA_PLAYER_PREPARING            = 1 << 2,    // 4 状态
 * MEDIA_PLAYER_PREPARED            = 1 << 3,     // 8状态
 * MEDIA_PLAYER_STARTED            = 1 << 4,      // 16状态
 * MEDIA_PLAYER_PAUSED                = 1 << 5,   // 32状态
 * MEDIA_PLAYER_STOPPED            = 1 << 6,      // 64 状态
 * MEDIA_PLAYER_PLAYBACK_COMPLETE  = 1 << 7,      // 128 状态
 * }
 * <p>
 * release 状态不能setLooping
 * release 状态不能prepare
 * <p>
 * stop 状态下不能start
 */

public class TMediaPlayer implements IPlayer {
    private MediaPlayer mMediaPlayer;

    private Handler mMainHandler;

    private PlayerState mPlayerState = PlayerState.IDLE;

    private int mRestorePosition = 0;
    private int mBufferingPercent = 0;

    private boolean mIsBuffering = false;

    private String mUrl = "";
    private int mLastPosition = 0;
    private int mStartPosition = 0;
    private boolean mStartWhenSeekComplete = false;

    private PlayerNotifier mPlayerNotifier;

    public TMediaPlayer() {
        mMainHandler = new Handler(Looper.getMainLooper());
        mPlayerNotifier = new PlayerNotifier();
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Timber.d("onPrepared");
                setState(PlayerState.PREPARED);
                mPlayerNotifier.notifyPrepareEnd(mStartPosition);
            }
        });

        mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Timber.d("onInfo w: " + what + ";  e: " + extra);
                onMediaPlayerInfo(what, extra);
                return false;
            }
        });

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Timber.v("onBufferingUpdate: " + percent);
                mBufferingPercent = percent;
                mPlayerNotifier.notifyBuffering(percent);
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Timber.d("onCompletion");
                if (getPlayerState() != PlayerState.ERROR) {
                    setState(PlayerState.COMPLETE);
                }
            }
        });

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Timber.w("onError, w: " + what + ";  e: " + extra);
                setState(PlayerState.ERROR);
                return false;
            }
        });

        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Timber.d("onSeekComplete, %s", mStartWhenSeekComplete);
                mPlayerNotifier.notifySeekComplete(mStartWhenSeekComplete);
            }
        });

        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                Timber.d("onVideoSizeChanged, w:" + width + "; h:" + height);
            }
        });
    }

    @Override
    public void addOnPreparedListener(IOnPreparedListener listener) {
        mPlayerNotifier.addOnPreparedListener(listener);
    }

    @Override
    public void removeOnPreparedListener(IOnPreparedListener listener) {
        mPlayerNotifier.removeOnPreparedListener(listener);
    }

    @Override
    public void addPlayerListener(IPlayerListener playerListener) {
        mPlayerNotifier.addPlayerListener(playerListener);
    }

    @Override
    public void removePlayerListener(IPlayerListener playerListener) {
        mPlayerNotifier.removePlayerListener(playerListener);
    }

    private synchronized void setState(PlayerState playerState) {
        Timber.i("state:  " + mPlayerState.name() + " => " + playerState.name());
        mPlayerState = playerState;
        switch (playerState) {
            case PREPARING:
                mPlayerNotifier.notifyPrepareStart();
                break;
            case STARTED:
                mPlayerNotifier.notifyPlayStart();
                break;
            case STOPPED:
                mPlayerNotifier.notifyPlayStop();
                break;
            case ERROR:
                mPlayerNotifier.notifyError();
                break;
            case COMPLETE:
                mPlayerNotifier.notifyPlayComplete();
                break;
        }
    }

    @Override
    public PlayerState getPlayerState() {
        return mPlayerState;
    }

    private void saveCurrentPosition() {
        int position = getCurrentPosition();
        Timber.d("saveCurrentPosition: " + position);
        if (position >= 0) {
            mRestorePosition = position;
        }
    }

    public int getRestorePosition() {
        return mRestorePosition;
    }

    @Override
    public int getLastPosition() {
        return mLastPosition;
    }

    @Override
    public boolean isBuffering() {
        return mIsBuffering;
    }

    private void startPositionUpdate() {
        int position = getCurrentPosition();
        if (position >= 0) {
            mLastPosition = position;
            mPlayerNotifier.notifyProgress(position);
        }
        Timber.v("update position: " + position);
        mMainHandler.postDelayed(mRunnablePositionUpdate, 1000);
    }

    private void stopPositionUpdate() {
        Timber.d("stopPositionUpdate");
        mMainHandler.removeCallbacks(mRunnablePositionUpdate);
    }

    @Override
    public void setDataSource(String url) {
        final PlayerState state = getPlayerState();
        Timber.d("setDataSource, current: " + state);
        switch (state) {
            case IDLE:
                try {
                    mUrl = url;
                    mMediaPlayer.setDataSource(url);
                    setState(PlayerState.INITIALIZED);
                } catch (IOException e) {
                    e.printStackTrace();
                    Timber.w("setDataSource error: " + e.getMessage());
                    setState(PlayerState.ERROR);
                }
                break;
            default:
                Timber.w("setDataSource, illegal state: " + state);
        }
    }

    @Override
    public void prepare() {
        prepare(0);
    }

    @Override
    public void prepare(int startPosition) {
        final PlayerState state = getPlayerState();
        mLastPosition = 0;
        Timber.d("prepare, current: " + state);
        switch (state) {
            case STOPPED:
            case INITIALIZED:
                mStartPosition = startPosition;
                mMediaPlayer.setScreenOnWhilePlaying(true);
                mMediaPlayer.setLooping(false);
                mMediaPlayer.prepareAsync();
                setState(PlayerState.PREPARING);
                break;
            default:
                Timber.w("prepare, illegal state: " + state);
        }
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    //TODO: 异常状态时重新开始
    @Override
    public void restart(boolean fromStart) {
        Timber.d("restart: %s", fromStart);
        final PlayerState state = getPlayerState();
        switch (state) {
            case INITIALIZED:
                if (!fromStart && getLastPosition() > 0) {
                    prepare(0);
                } else {
                    prepare(getLastPosition());
                }
                Timber.d("prepare");
                break;
            case PAUSED:
            case STOPPED:
                resume(true);
                Timber.d("resume");
                break;
            case COMPLETE:
                reset();
                restart(true);
                break;
            case RELEASED:
                Timber.d("do nothing");
                reset();
                restart(true);
                break;
            case IDLE:
                if (!TextUtils.isEmpty(mUrl)) {
                    setDataSource(mUrl);
                    if (!fromStart && mStartPosition > 0) {
                        prepare(mStartPosition);
                    } else {
                        prepare(0);
                    }
                    Timber.d("reload url: %s", mUrl);
                } else {
                    Timber.w("do nothing");
                }
                Timber.w("do nothing");
                break;
            case ERROR:
                Timber.d("reset");
                reset();
                restart(false);
                break;
        }
    }

    @Override
    public void start() {
        final PlayerState state = getPlayerState();
        Timber.d("start, current: %s", state);
        switch (state) {
            case PREPARED:
            case PAUSED:
            case COMPLETE:
                mMediaPlayer.setScreenOnWhilePlaying(true);
                mMediaPlayer.start();
                startPositionUpdate();
                setState(PlayerState.STARTED);
                break;
            default:
                Timber.w("start, illegal state: %s", state);
//                throw new IllegalStateException("start called from illegal state: " + state);
        }
    }

    @Override
    public void stop() {
        final PlayerState state = getPlayerState();
        Timber.d("stop, current: " + state);
        switch (state) {
            case STARTED:
            case COMPLETE:
            case PREPARED:
            case PAUSED:
                saveCurrentPosition();
                stopPositionUpdate();
                mMediaPlayer.setScreenOnWhilePlaying(false);
                mMediaPlayer.stop();
                setState(PlayerState.STOPPED);
                break;
            default:
                Timber.w("stop, illegal state: " + state);
        }
    }

    @Override
    public void pause() {
        final PlayerState state = getPlayerState();
        Timber.d("pause, current: " + state);
        if (state == PlayerState.STARTED) {
            saveCurrentPosition();
            stopPositionUpdate();
            mMediaPlayer.setScreenOnWhilePlaying(false);
            mMediaPlayer.pause();
            setState(PlayerState.PAUSED);
            mPlayerNotifier.notifyPlayPaused();
        } else {
            Timber.w("pause, illegal state: " + state);
        }
    }

    @Override
    public void resume(boolean autoStart) {
        final PlayerState state = getPlayerState();
        Timber.d("resume, current: " + state);
        switch (state) {
            case PAUSED:
                int position = getRestorePosition();
                if (position > 0) {
                    seekTo(position, autoStart);
                } else {
                    if (autoStart) {
                        start();
                    }
                }
                break;
            default:
                Timber.w("resume, illegal state: " + state);
        }
    }

    @Override
    public void release() {
        if (mMediaPlayer == null) {
            return;
        }
        stopPositionUpdate();
        final PlayerState state = getPlayerState();
        Timber.d("release, current: " + state);
        mUrl = null;
        mMediaPlayer.setScreenOnWhilePlaying(false);
        mMediaPlayer.release();
        setState(PlayerState.RELEASED);

        mMediaPlayer.setOnErrorListener(null);
        mMediaPlayer.setOnInfoListener(null);
        mMediaPlayer.setOnCompletionListener(null);
        mMediaPlayer.setOnPreparedListener(null);
        mMediaPlayer.setOnBufferingUpdateListener(null);
        mMediaPlayer.setOnVideoSizeChangedListener(null);
        mMediaPlayer.setOnSeekCompleteListener(null);

        mMediaPlayer = null;
    }

    @Override
    public void reset() {
        final PlayerState state = getPlayerState();
        Timber.d("reset, current: " + state);
        if (state == PlayerState.RELEASED || mMediaPlayer == null) {
            Timber.w("reset, illegal state: " + state);
            initMediaPlayer();
        } else {
            mMediaPlayer.setScreenOnWhilePlaying(false);
            mMediaPlayer.reset();
        }

        stopPositionUpdate();
        setState(PlayerState.IDLE);
        mPlayerNotifier.notifyPlayerReset();
    }

    @Override
    public void seekTo(int position, boolean autoStart) {
        mStartWhenSeekComplete = autoStart;
        final PlayerState state = getPlayerState();
        Timber.d("seekTo, current: %s, autoStart: %s, position: %s", state, autoStart, position);
        switch (state) {
            case PREPARED:
            case STARTED:
            case PAUSED:
            case COMPLETE:
                mMediaPlayer.seekTo(position);
                break;
            default:
                Timber.w("seekTo, illegal state: %s", state);
        }
    }

    @Override
    public int getCurrentPosition() {
        final PlayerState state = getPlayerState();
        int position = -1;
        switch (state) {
            case PREPARED:
            case STARTED:
            case PAUSED:
            case COMPLETE:
                position = mMediaPlayer.getCurrentPosition();
                Timber.v("getCurrentPosition, state: %s;  position: %s", state, position);
                break;
            default:
                Timber.d("getCurrentPosition, illegal state: %s", state);
        }
        return position;
    }

    @Override
    public int getDuration() {
        final PlayerState state = getPlayerState();
        int duration = 0;
        switch (state) {
            case PREPARED:
            case STARTED:
            case PAUSED:
            case COMPLETE:
            case STOPPED:
                duration = mMediaPlayer.getDuration();
                Timber.v("getDuration, state: %s; duration: %s", state, duration);
                break;
            default:
                duration = 0;
                Timber.d("getDuration, illegal state: %s", state);
        }

        return duration;
    }

    @Override
    public boolean isCanPlayback() {
        final PlayerState state = getPlayerState();
        switch (state) {
            case PREPARED:
            case STARTED:
            case PAUSED:
            case COMPLETE:
                return true;
        }
        return false;
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public boolean isPaused() {
        return getPlayerState() == PlayerState.PAUSED;
    }

    @Override
    public int getBufferingPercent() {
        return mBufferingPercent;
    }

    /**
     * 1. RELEASE 之后 setDisplay报错
     * 2.
     *
     * @param surfaceHolder
     */
    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        if (surfaceHolder != null) {
            final PlayerState state = getPlayerState();
            if (state == PlayerState.RELEASED) {
                Timber.w("set display, illegal state: %s", state);
                return;
            }
            mMediaPlayer.setDisplay(surfaceHolder);
        } else {
            mMediaPlayer.setDisplay(null);
        }
    }

    @Override
    public void setSurface(SurfaceTexture surfaceTexture) {
        if (surfaceTexture != null) {

            final PlayerState state = getPlayerState();
            if (state == PlayerState.RELEASED) {
                Timber.w("set display, illegal state: %s", state);
                return;
            }
            mMediaPlayer.setSurface(new Surface(surfaceTexture));
        } else {
            mMediaPlayer.setSurface(null);
        }
    }


    private void onMediaPlayerInfo(int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_UNKNOWN:
                Timber.v("onInfo, MEDIA_INFO_UNKNOWN");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                Timber.v("onInfo, MEDIA_INFO_VIDEO_TRACK_LAGGING");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                Timber.d("onInfo, MEDIA_INFO_VIDEO_RENDERING_START");
                mPlayerNotifier.notifyRenderingStart();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                Timber.d("onInfo, MEDIA_INFO_BUFFERING_START");
                mIsBuffering = true;
                mPlayerNotifier.notifyBufferingStart();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                Timber.d("onInfo, MEDIA_INFO_BUFFERING_END");
                mIsBuffering = false;
                mPlayerNotifier.notifyBufferingEnd();
                break;
//            case MediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
//                Timber.d("onInfo, MEDIA_INFO_NETWORK_BANDWIDTH: %s", extra);
//                break;

            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                Timber.v("onInfo, MEDIA_INFO_BAD_INTERLEAVING");
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                Timber.w("onInfo, MEDIA_INFO_NOT_SEEKABLE");
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                Timber.v("onInfo, MEDIA_INFO_METADATA_UPDATE");
                break;
            case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                Timber.v("onInfo, MEDIA_INFO_UNSUPPORTED_SUBTITLE");
                break;
            case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                Timber.v("onInfo, MEDIA_INFO_SUBTITLE_TIMED_OUT");
                break;
        }
    }

    private Runnable mRunnablePositionUpdate = new Runnable() {
        @Override
        public void run() {
            startPositionUpdate();
        }
    };
}
