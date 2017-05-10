package com.amgl.tmediaplayer;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.amgl.viewscrollhelper.RecyclerScrollListener;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ListActivity extends AbstractPlayerActivity {
    private static final String[] URLS = new String[]{MainActivity.URL, MainActivity.URL, MainActivity.URL};

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private SampleVideoRVAdapter mRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRVAdapter = new SampleVideoRVAdapter();
        mRecyclerView.setAdapter(mRVAdapter);

        mRVAdapter.setDataList(Arrays.asList(URLS));
        mRVAdapter.notifyDataSetChanged();

        mRVAdapter.setVideoListListener(mVideoListListener);

        mRecyclerView.addOnScrollListener(new RecyclerScrollListener(this, mRecyclerView, mLayoutManager));
    }

    private SampleVideoRVAdapter.IVideoListListener mVideoListListener = new SampleVideoRVAdapter.IVideoListListener() {
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
            Timber.d("on click: %s", position);
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
