package com.amgl.tmediaplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import com.amgl.tmediaplayer.play.AbstractVideoPlayActivity;
import com.amgl.tmediaplayer.play.DetailActivity;
import com.amgl.viewscrollhelper.RecyclerViewItemActivateListener;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ListActivity extends AbstractVideoPlayActivity {
    private static final String[] URLS = new String[]{MainActivity.URL, MainActivity.URL, MainActivity.URL, MainActivity.URL, MainActivity.URL, MainActivity.URL, MainActivity.URL, MainActivity.URL, MainActivity.URL, MainActivity.URL, MainActivity.URL, MainActivity.URL, MainActivity.URL};

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private VideoRVAdapter mRVAdapter;

    @Override
    public void onServiceCon() {
        if (mPlayerBinder != null) {
            mPlayerBinder.getVideoPlayManager().onStart();
        }
    }

    @Override
    public void onServiceDisCon() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRVAdapter = new VideoRVAdapter();
        mRecyclerView.setAdapter(mRVAdapter);

        mRVAdapter.setDataList(Arrays.asList(URLS));
        mRVAdapter.notifyDataSetChanged();

        mRVAdapter.setVideoListListener(mVideoListListener);

        mRecyclerView.addOnScrollListener(new RecyclerViewItemActivateListener(this, mRecyclerView, mLayoutManager, mItemActivateListener));
    }

    private RecyclerViewItemActivateListener.ItemActivateListener mItemActivateListener = new RecyclerViewItemActivateListener.ItemActivateListener() {
        @Override
        public void onActive(int position) {
            Timber.d("activate :%s", position);
        }

        @Override
        public void onDeactivate(int position) {
            Timber.d("deactivate :%s", position);
        }
    };

    private int mPositionPlay = -1;

    private VideoRVAdapter.IVideoListListener mVideoListListener = new VideoRVAdapter.IVideoListListener() {
        @Override
        public void onBind(int position, SurfaceView surfaceView, String url) {
            Timber.d("on bind: %s", position);
        }

        @Override
        public void onUnBind(int position, SurfaceView surfaceView) {
            Timber.d("on unBind: %s", position);
        }

        @Override
        public void onClick(int position) {
            Timber.d("on click: %s");
            int playProgress = 0;
            if (mPlayerBinder != null && mPlayerBinder.getPlayer() != null) {
                mPlayerBinder.getPlayer().setSurface(null);
                if (position == mPositionPlay) {
                    playProgress = mPlayerBinder.getPlayer().getCurrentPosition();
                    mPlayerBinder.pause();
                    Timber.d("play position: %s", playProgress);
                } else {
                    Timber.d("stop and play other");
                    playProgress = 0;
                    mPlayerBinder.stopPlay();
                }
            }

            Intent intent = DetailActivity.newIntent(ListActivity.this, playProgress);
            startActivity(intent);
        }

        @Override
        public void onClick(int position, TextureView textureView) {
            final String url = mRVAdapter.getItem(position);
            Timber.d("on texture click: %s, %s", position, url);
            if (textureView != null) {
                mPositionPlay = position;
                startPlay(url, textureView.getSurfaceTexture());
            }
        }

        @Override
        public void onVisible(SurfaceHolder surfaceHolder, int position, String url) {
//            Timber.d("on visible %s, %s", position, url);
//            startPlay(url, surfaceHolder);
//            mPlayingPosition = position;
//            mStartedVideos.put(position, true);
        }

        @Override
        public void onHide(SurfaceHolder surfaceHolder, int position, String url) {
//            Timber.d("on hide %s, %s, currentPlaying: %s", position, url, mPlayingPosition);
//            if (mPlayingPosition == position) {
//                Timber.d("stop");
//                stopPlay();
//            } else {
//                Timber.d("do nothing");
//            }
//            if (mStartedVideos.get(position)) {
//                stopPlay();
//                mStartedVideos.put(position, false);
//            }
        }
    };

//    private int mPlayingPosition = -1;
//
//    SparseArray<Boolean> mStartedVideos = new SparseArray<>();
}
