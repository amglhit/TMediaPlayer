package com.amgl.viewscrollhelper;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.amgl.viewscrollhelper.position.IItemPositionHelper;
import com.amgl.viewscrollhelper.position.RecyclerViewItemPositionHelper;

import timber.log.Timber;

/**
 * Created by 阿木 on 2017/5/9.
 */

public class RecyclerScrollListener extends RecyclerView.OnScrollListener {

    public RecyclerScrollListener(Context context, RecyclerView recyclerView, LinearLayoutManager linearLayoutManager) {
        mItemPositionHelper = new RecyclerViewItemPositionHelper(recyclerView, linearLayoutManager);
        mScrollDirectionDetector = new ScrollDirectionDetector(context, mScrollDirectionChangeListener, mItemPositionHelper);
    }

    private ScrollDirectionDetector mScrollDirectionDetector;
    private IItemPositionHelper mItemPositionHelper;

    private ScrollDirectionDetector.ScrollDirectionChangeListener mScrollDirectionChangeListener = new ScrollDirectionDetector.ScrollDirectionChangeListener() {
        @Override
        public void onScrollDirectionChanged(ScrollDirectionDetector.ScrollDirection scrollDirection) {
            Timber.d("on scroll direction changed: %s", scrollDirection);
        }
    };

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        mScrollDirectionDetector.onScroll();
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            Timber.d("scroll state idle");
        }
    }
}
