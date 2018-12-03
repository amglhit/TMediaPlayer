package com.amgl.tmediaplayer.play;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.amgl.tmediaplayer.MainActivity;
import com.amgl.tmediaplayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailActivity extends AbstractVideoPlayActivity {
    private static final String ARG_POSITION = "ARG_POSITION";

    public static Intent newIntent(Activity parent, int startPosition) {
        Intent intent = new Intent(parent, DetailActivity.class);
        intent.putExtra(ARG_POSITION, startPosition);
        return intent;
    }

    @BindView(R.id.texture_view)
    TextureView mTextureView;
    @BindView(R.id.btn_play)
    Button mButton;

    SurfaceTexture mSurfaceTexture;

    private int mStartPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            mStartPosition = intent.getIntExtra(ARG_POSITION, 0);
        }

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                mSurfaceTexture = surfaceTexture;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
                mSurfaceTexture = surfaceTexture;
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                mSurfaceTexture = null;
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.d("click play, %s", mStartPosition);
                if (mSurfaceTexture != null) {
                    startPlay();
                } else {
                    Timber.d("surface texture is null");
                }
            }
        });
    }

    @Override
    public void onServiceCon() {
        if (mStartPosition > 0) {
            resumePlay(mStartPosition);
        } else {
            Timber.d("start on con");
            startPlay();
        }
    }

    @Override
    public void onServiceDisCon() {

    }

    @Override
    protected void onStop() {
        if (mPlayerBinder != null && mPlayerBinder.getPlayer() != null) {
            mPlayerBinder.getPlayer().pause();
        }
        super.onStop();
        mHandler.removeCallbacks(mStartRunnable);
    }

    private void resumePlay(final int startPosition) {
        Timber.d("resume play: %s", mStartPosition);
        if (mSurfaceTexture != null && mPlayerBinder != null) {
            mPlayerBinder.getPlayer().setSurface(mSurfaceTexture);
            mPlayerBinder.getPlayer().resume(true);
        } else {
            Timber.d("delay resume");
            mTextureView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    resumePlay(startPosition);
                }
            }, 1000);
        }
    }

    private void startPlay() {
        if (mSurfaceTexture != null) {
            Timber.d("start play");
            startPlay(MainActivity.URL, mSurfaceTexture);
        } else {
            Timber.d("delay start");
            mHandler.postDelayed(mStartRunnable, 1000);
        }
    }

    private Handler mHandler = new Handler();

    private DelayStartRunnable mStartRunnable = new DelayStartRunnable();

    private class DelayStartRunnable implements Runnable {
        @Override
        public void run() {
            startPlay();
        }
    }
}
