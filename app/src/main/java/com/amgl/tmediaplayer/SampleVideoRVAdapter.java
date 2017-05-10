package com.amgl.tmediaplayer;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
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

public class SampleVideoRVAdapter extends BaseRVAdapter<String> {
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
        @BindView(R.id.surface_view)
        SurfaceView mSurfaceView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mSurfaceView.getHolder().addCallback(mCallback2);
        }

        @Override
        protected void onBind() {
            Timber.d("onBind: %s, %s", mPosition, mData);
            mTextView.setText("po " + mPosition);
            if (mVideoListListener != null) {
                mVideoListListener.onBind(mPosition, mSurfaceView, mData);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.surface_view) {
                Timber.d("surface view click: %s", mPosition);
                if (mVideoListListener != null) {
                    mVideoListListener.onClick(mPosition);
                }
            }
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
    }

    public interface IVideoListListener {
        void onBind(int position, SurfaceView surfaceView, String url);

        void onUnBind(int position, SurfaceView surfaceView);

        void onClick(int position);

        void onVisible(SurfaceHolder surfaceHolder, int position, String url);

        void onHide(SurfaceHolder surfaceHolder, int position, String url);
    }
}
