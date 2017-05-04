package com.amgl.mediaplayer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

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
 */

public class TMediaPlayer implements IPlayer {
    private static final String TAG = TMediaPlayer.class.getSimpleName();

    private static void printLogD(String log) {
        Log.d(TAG, log);
    }

    private static void printLogV(String log) {
        Log.v(TAG, log);
    }

    private static void printLogI(String log) {
        Log.i(TAG, log);
    }

    private static void printLogW(String log) {
        Log.w(TAG, log);
    }

    private MediaPlayer mMediaPlayer;
    private Handler mHandler;

    private PlayerState mPlayerState = PlayerState.IDLE;

    private SurfaceHolder mSurfaceHolder;
    private int mRestorePosition = 0;

    public TMediaPlayer() {
        mHandler = new Handler(Looper.getMainLooper());

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                printLogD("onPrepared");
                setState(PlayerState.PREPARED);
                start();
            }
        });

        mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                printLogD("onInfo w: " + what + ";  e: " + extra);
                printInfo(what);
                return false;
            }
        });

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                printLogV("onBufferingUpdate: " + percent);
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                printLogD("onCompletion");
                if (getPlayerState() != PlayerState.ERROR) {
                    setState(PlayerState.COMPLETE);
                }
            }
        });

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                printLogW("onError, w: " + what + ";  e: " + extra);
                setState(PlayerState.ERROR);
                return false;
            }
        });

        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                printLogD("onSeekComplete");
                start();
            }
        });

        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                printLogD("onVideoSizeChanged, w:" + width + "; h:" + height);
            }
        });
    }

    private synchronized void setState(PlayerState playerState) {
        printLogI("state:  " + mPlayerState.name() + " => " + playerState.name());
        mPlayerState = playerState;
    }

    public synchronized PlayerState getPlayerState() {
        return mPlayerState;
    }

    private void saveCurrentPosition() {
        int position = getCurrentPosition();
        printLogD("saveCurrentPosition: " + position);
        if (position >= 0) {
            mRestorePosition = position;
        }
    }

    public int getRestorePosition() {
        return mRestorePosition;
    }

    private void startPositionUpdate() {
        int position = getCurrentPosition();
        printLogD("update position: " + position);
        mHandler.postDelayed(mRunnablePositionUpdate, 1000);
    }

    private void stopPositionUpdate() {
        printLogD("stopPositionUpdate");
        mHandler.removeCallbacks(mRunnablePositionUpdate);
    }

    @Override
    public void prepare(String url) {
        final PlayerState state = getPlayerState();
        printLogD("prepare, current: " + state);
        switch (state) {
            case STOPPED:
            case IDLE:
            case ERROR:
                try {
                    mMediaPlayer.setDataSource(url);
                    mMediaPlayer.prepareAsync();
                    setState(PlayerState.PREPARING);
                } catch (IOException e) {
                    e.printStackTrace();
                    printLogW("prepare error " + e.getMessage());
                    setState(PlayerState.ERROR);
                }
                break;
            default:
                printLogD("prepare, illegal state: " + state);
        }
    }

    @Override
    public void start() {
        final PlayerState state = getPlayerState();
        printLogD("start, current: " + state);
        switch (state) {
            case PREPARED:
            case PAUSED:
            case COMPLETE:
            case STOPPED:
                mMediaPlayer.start();
                setState(PlayerState.PLAYING);
                break;
            default:
                printLogD("start, illegal state: " + state);
//                throw new IllegalStateException("start called from illegal state: " + state);
        }
    }

    @Override
    public void stop() {
        final PlayerState state = getPlayerState();
        printLogD("stop, current: " + state);
        switch (state) {
            case STARTED:
            case PLAYING:
            case COMPLETE:
            case PREPARED:
            case PAUSED:
                saveCurrentPosition();
                stopPositionUpdate();
                mMediaPlayer.stop();
                setState(PlayerState.STOPPED);
                break;
            default:
                printLogD("stop, illegal state: " + state);
        }
    }

    @Override
    public void pause() {
        final PlayerState state = getPlayerState();
        printLogD("pause, current: " + state);
        if (state == PlayerState.PLAYING) {
            saveCurrentPosition();
            stopPositionUpdate();
            mMediaPlayer.pause();
            setState(PlayerState.PAUSED);
        } else {
            printLogD("pause, illegal state: " + state);
        }
    }

    @Override
    public void resume() {
        final PlayerState state = getPlayerState();
        printLogD("resume, current: " + state);
        switch (state) {
            case PAUSED:
            case STOPPED:
                int position = getRestorePosition();
                if (position == 0) {
                    start();
                } else if (position > 0) {
                    seekTo(position);
                }
                break;
            default:
                printLogD("resume, illegal state: " + state);
        }
    }

    @Override
    public void release() {
        final PlayerState state = getPlayerState();
        printLogD("release, current: " + state);
        mMediaPlayer.release();
        setState(PlayerState.RELEASED);
    }

    @Override
    public void reset() {
        final PlayerState state = getPlayerState();
        printLogD("reset, current: " + state);
        if (state == PlayerState.RELEASED) {
            printLogW("reset, illegal state: " + state);
            return;
        }
        mMediaPlayer.reset();
        setState(PlayerState.IDLE);
    }

    @Override
    public void seekTo(int position) {
        final PlayerState state = getPlayerState();
        printLogD("seekTo, current: " + state);
        switch (state) {
            case PREPARED:
            case PLAYING:
            case PAUSED:
            case COMPLETE:
                mMediaPlayer.seekTo(position);
                break;
            default:
                printLogD("seekTo, illegal state: " + state);
        }
    }

    @Override
    public int getCurrentPosition() {
        final PlayerState state = getPlayerState();
        int position = -1;
        switch (state) {
            case PREPARED:
            case PLAYING:
            case PAUSED:
            case COMPLETE:
                position = mMediaPlayer.getCurrentPosition();
                printLogD("getCurrentPosition, state: " + state + ";  position: " + position);
                break;
            default:
                printLogD("getCurrentPosition, illegal state: " + state);
        }
        return position;
    }

    @Override
    public int getDuration() {
        final PlayerState state = getPlayerState();
        int duration = 0;
        switch (state) {
            case PREPARED:
            case PLAYING:
            case PAUSED:
            case COMPLETE:
            case STOPPED:
                duration = mMediaPlayer.getDuration();
                printLogD("getDuration, state: " + state + "; duration: " + duration);
                break;
            default:
                duration = 0;
                printLogD("getDuration, illegal state: " + state);
        }

        return duration;
    }

    @Override
    public boolean isCanPlayback() {
        final PlayerState state = getPlayerState();
        switch (state) {
            case PREPARED:
            case PLAYING:
            case PAUSED:
            case COMPLETE:
                return true;
        }
        return false;
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    private SurfaceHolder.Callback2 mSurfaceCallback = new SurfaceHolder.Callback2() {
        @Override
        public void surfaceRedrawNeeded(SurfaceHolder holder) {
            printLogD("surfaceRedrawNeeded");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            printLogD("surface created");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            printLogD("surface changed");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            printLogD("surface destroyed");
        }
    };

    /**
     * 1. RELEASE 之后 setDisplay报错
     * 2.
     *
     * @param surfaceHolder
     */
    public void setDisplay(SurfaceHolder surfaceHolder) {
        if (surfaceHolder != null) {
            final PlayerState state = getPlayerState();
            if (state == PlayerState.RELEASED) {
                printLogW("set display, illegal state: " + state);
                return;
            }

            if (mSurfaceHolder != null) {
                mSurfaceHolder.removeCallback(mSurfaceCallback);
            }

            mSurfaceHolder = surfaceHolder;
            mSurfaceHolder.addCallback(mSurfaceCallback);
            mMediaPlayer.setDisplay(mSurfaceHolder);
        }
    }

    private void printInfo(int what) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_UNKNOWN:
                printLogV("onInfo, MEDIA_INFO_UNKNOWN");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                printLogV("onInfo, MEDIA_INFO_VIDEO_TRACK_LAGGING");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                printLogV("onInfo, MEDIA_INFO_VIDEO_RENDERING_START");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                printLogV("onInfo, MEDIA_INFO_BUFFERING_START");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                printLogV("onInfo, MEDIA_INFO_BUFFERING_END");
                break;
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                printLogV("onInfo, MEDIA_INFO_BAD_INTERLEAVING");
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                printLogV("onInfo, MEDIA_INFO_NOT_SEEKABLE");
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                printLogV("onInfo, MEDIA_INFO_METADATA_UPDATE");
                break;
            case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                printLogV("onInfo, MEDIA_INFO_UNSUPPORTED_SUBTITLE");
                break;
            case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                printLogV("onInfo, MEDIA_INFO_SUBTITLE_TIMED_OUT");
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
