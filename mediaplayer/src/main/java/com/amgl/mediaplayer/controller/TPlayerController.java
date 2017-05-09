package com.amgl.mediaplayer.controller;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amgl.mediaplayer.IOnPreparedListener;
import com.amgl.mediaplayer.IPlayerListener;
import com.amgl.mediaplayer.R;
import com.amgl.mediaplayer.player.IPlayer;
import com.amgl.mediaplayer.player.PlayerState;
import com.amgl.mediaplayer.player.TMediaPlayer;

import timber.log.Timber;

/**
 * Created by 阿木 on 2017/5/5.
 */

public class TPlayerController extends FrameLayout implements IPlayerController {

    public TPlayerController(@NonNull Context context) {
        this(context, null);
    }

    public TPlayerController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TPlayerController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_t_player_controller, this, true);

        mViewBottomControl = findViewById(R.id.bottom_control);
        mImagePlay = (ImageView) findViewById(R.id.image_play);
        mTextProgress = (TextView) findViewById(R.id.text_progress);

        mViewTopControl = findViewById(R.id.top_control);
        mSeekBarProgress = (AppCompatSeekBar) findViewById(R.id.seek_bar);

        mViewBuffering = findViewById(R.id.view_buffering);
        mTextBuffering = (TextView) findViewById(R.id.text_buffering);

        mViewLoading = findViewById(R.id.view_loading);
        mTextLoading = (TextView) findViewById(R.id.text_loading);

        initView();

        mImagePlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playOrPause();
            }
        });
    }

    private void playOrPause() {
        if (mPlayer != null) {
            final boolean isPlaying = mPlayer.isPlaying();
            final boolean isPaused = mPlayer.isPaused() || mPlayer.getPlayerState() == PlayerState.PREPARED;
            final boolean isCanPlayback = mPlayer.isCanPlayback();
            if (isPlaying) {
                mPlayer.pause();
            } else if (isPaused) {
                mPlayer.start();
            } else if (isCanPlayback) {
                Timber.w("restart, %s", mPlayer.getPlayerState());
                mPlayer.restart(false);
            } else {
                Timber.w("do nothing");
            }
        }
    }

    public void setPlayer(IPlayer player) {
        if (mPlayer != null) {
            mPlayer.removePlayerListener(mPlayerListener);
            mPlayer.removeOnPreparedListener(mOnPreparedListener);
        }
        mPlayer = player;
        mPlayer.addOnPreparedListener(mOnPreparedListener);
        mPlayer.addPlayerListener(mPlayerListener);
    }

    private void initView() {
        mViewBuffering.setVisibility(GONE);
        mViewLoading.setVisibility(GONE);
        mViewBottomControl.setVisibility(GONE);
        mViewTopControl.setVisibility(GONE);
    }

    private View mViewBottomControl;
    private ImageView mImagePlay;
    private TextView mTextProgress;

    private View mViewTopControl;
    private AppCompatSeekBar mSeekBarProgress;

    private View mViewLoading;
    private TextView mTextLoading;

    private View mViewBuffering;
    private TextView mTextBuffering;

    private IPlayer mPlayer;

    private IOnPreparedListener mOnPreparedListener = new IOnPreparedListener() {
        @Override
        public void onPrepared(int startPosition) {
            mViewLoading.setVisibility(GONE);
            mViewBottomControl.setVisibility(VISIBLE);
            mViewTopControl.setVisibility(VISIBLE);
        }

        @Override
        public void onPrepareStart() {
            initView();
            mViewLoading.setVisibility(VISIBLE);
        }
    };

    private IPlayerListener mPlayerListener = new IPlayerListener() {
        @Override
        public void onBuffering(int percent) {
            if (mViewBuffering.getVisibility() == VISIBLE) {
                mTextBuffering.setText(percent + "%");
            }
        }

        @Override
        public void onBufferingStart() {
            mViewBuffering.setVisibility(VISIBLE);
        }

        @Override
        public void onBufferingEnd() {
            mViewBuffering.setVisibility(GONE);
        }

        @Override
        public void onError() {
            updatePlayBtn(false);
            mViewLoading.setVisibility(GONE);
            mViewBuffering.setVisibility(GONE);
        }

        @Override
        public void onComplete() {
            updatePlayBtn(false);
        }

        @Override
        public void onStart() {
            updatePlayBtn(true);
        }

        @Override
        public void onStop() {
            updatePlayBtn(false);
        }

        @Override
        public void onPaused() {
            updatePlayBtn(false);
        }

        @Override
        public void onSeekComplete(boolean start) {
            updateProgress();
        }

        @Override
        public void onFirstFrameAppeared() {
            updateProgress();
        }

        @Override
        public void onReset() {
            updateProgress();
            updatePlayBtn(false);
        }

        @Override
        public void onProgress(int progress) {
            updateProgress();
        }
    };

    private void updateProgress() {
        int progress = mPlayer.getCurrentPosition();
        int total = mPlayer.getDuration();
        if (total >= 0) {
            mSeekBarProgress.setMax(total);
        }
        if (progress >= 0) {
            mSeekBarProgress.setProgress(progress);
        }

        if (total >= 0 && progress >= 0 && total >= progress) {
            mTextProgress.setText(progress + "/" + total);
        } else {
            mTextProgress.setText("");
        }
    }

    private void updatePlayBtn(boolean isPlaying) {
        Timber.d("update play btn: %s", isPlaying);
        if (isPlaying) {
            mImagePlay.setImageResource(R.drawable.ic_player_pause);
        } else {
            mImagePlay.setImageResource(R.drawable.ic_player_play);
        }
    }
}
