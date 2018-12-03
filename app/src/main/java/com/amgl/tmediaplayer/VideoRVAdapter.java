package com.amgl.tmediaplayer;

import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amgl.tmediaplayer.adapter.BaseRVAdapter;
import com.amgl.tmediaplayer.adapter.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by 阿木 on 2017/5/9.
 */

public class VideoRVAdapter extends BaseRVAdapter<String> {
    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = createItemView(R.layout.item_video, parent);
        VideoViewHolder viewHolder = new VideoViewHolder(itemView);
        return viewHolder;
    }

    private IVideoListListener mVideoListListener;

    public void setVideoListListener(IVideoListListener videoListListener) {
        mVideoListListener = videoListListener;
    }

    public class VideoViewHolder extends BaseViewHolder<String> implements View.OnClickListener {
        @BindView(R.id.text_title)
        TextView mTextView;
        @BindView(R.id.texture_view)
        TextureView mTextureView;

        SurfaceTexture mSurfaceTexture;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mSurfaceTexture = null;
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

            itemView.setOnClickListener(this);
            mTextureView.setOnClickListener(this);

//            mSurfaceView.getHolder().addCallback(mCallback2);
        }

        @Override
        protected void onBind() {
            Timber.d("onBind: %s, %s", mPosition, mData);
            mTextView.setText("po " + mPosition);
//            if (mVideoListListener != null) {
//                mVideoListListener.onBind(mPosition, mSurfaceView, mData);
//            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.texture_view) {
                Timber.d("texture view click: %s", mPosition);
                if (mVideoListListener != null && mSurfaceTexture != null) {
                    mVideoListListener.onClick(mPosition, mTextureView);
                }
            } else {
                Timber.d("item view click");
                if (mVideoListListener != null) {
                    mVideoListListener.onClick(mPosition);
                }
            }
        }

        public void onActivate() {
            Timber.d("on activate: %s", mPosition);
        }

        public void onDeactivate() {
            Timber.d("on deactivate: %s", mPosition);
        }

        private SurfaceHolder.Callback2 mCallback2 = new SurfaceHolder.Callback2() {
            @Override
            public void surfaceRedrawNeeded(SurfaceHolder holder) {

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Timber.d("surface created: %s", mPosition);
                if (mVideoListListener != null) {
                    mVideoListListener.onVisible(holder, mPosition, mData);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Timber.d("surface destroyed: %s", mPosition);
                if (mVideoListListener != null) {
                    mVideoListListener.onHide(holder, mPosition, mData);
                }
            }
        };

        private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                Timber.d("on surface texture available %s: %s, %s", mPosition, i, i1);
                mSurfaceTexture = surfaceTexture;
//                if (mVideoListListener != null) {
//                    mVideoListListener.onVisible(holder, mPosition, mData);
//                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
                mSurfaceTexture = surfaceTexture;
                Timber.d("on surface texture size changed %s: %s, %s", mPosition, i, i1);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                mSurfaceTexture = null;
                Timber.d("on surface texture destroyed: %s", mPosition);
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        };
    }

    public interface IVideoListListener {
        void onBind(int position, SurfaceView surfaceView, String url);

        void onUnBind(int position, SurfaceView surfaceView);

        void onClick(int position, TextureView textureView);

        void onClick(int position);

        void onVisible(SurfaceHolder surfaceHolder, int position, String url);

        void onHide(SurfaceHolder surfaceHolder, int position, String url);
    }
}
