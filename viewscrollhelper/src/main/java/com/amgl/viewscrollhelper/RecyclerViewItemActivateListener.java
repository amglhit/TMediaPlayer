package com.amgl.viewscrollhelper;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.amgl.viewscrollhelper.position.IItemPositionHelper;
import com.amgl.viewscrollhelper.position.RecyclerViewItemPositionHelper;

import timber.log.Timber;

/**
 * Created by 阿木 on 2017/5/9.
 */

public class RecyclerViewItemActivateListener extends RecyclerView.OnScrollListener {

    public RecyclerViewItemActivateListener(Context context, RecyclerView recyclerView, LinearLayoutManager linearLayoutManager, ItemActivateListener activateListener) {
        mItemPositionHelper = new RecyclerViewItemPositionHelper(recyclerView, linearLayoutManager);
        mScrollDirectionDetector = new ScrollDirectionDetector(context, mScrollDirectionChangeListener, mItemPositionHelper);
        mItemActivateListener = activateListener;
    }

    private ScrollDirectionDetector mScrollDirectionDetector;
    private IItemPositionHelper mItemPositionHelper;

    private ItemActivateListener mItemActivateListener;

    private ScrollDirectionDetector.ScrollDirectionChangeListener mScrollDirectionChangeListener = new ScrollDirectionDetector.ScrollDirectionChangeListener() {
        @Override
        public void onScrollDirectionChanged(ScrollDirectionDetector.ScrollDirection scrollDirection) {
            Timber.d("on scroll direction changed: %s", scrollDirection);
        }
    };

    private int mLastFirstVisibleItem = -1;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        mScrollDirectionDetector.onScroll();
        int firstVisibleItem = mItemPositionHelper.getFirstVisiblePosition();
        if (firstVisibleItem != mLastFirstVisibleItem) {
            Timber.d("first visible item changed: %s", firstVisibleItem);
            mLastFirstVisibleItem = firstVisibleItem;
        }
    }

    private int mActiveIndex = -1;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            findActiveItem();
        }
    }

    /**
     * 获取当前激活状态的item，并回调用{@link ItemActivateListener}
     */
    public void findActiveItem() {
        final ScrollDirectionDetector.ScrollDirection scrollDirection = mScrollDirectionDetector.getScrollDirection();

        final int itemIndex = findActiveItemIndex(scrollDirection);

        final int lastActiveIndex = mActiveIndex;

        if (lastActiveIndex != itemIndex) {
            if (lastActiveIndex > -1) {
                Timber.d("Deactivate: %s", lastActiveIndex);
                mItemActivateListener.onDeactivate(lastActiveIndex);
            }
            if (itemIndex > -1) {
                Timber.d("Activate: %s", itemIndex);
                mActiveIndex = itemIndex;
                mItemActivateListener.onActive(itemIndex);
            }
        }
    }

    /**
     * 得到需要激活的Item的index。
     * 根据业务需要覆盖此方法。
     *
     * @param scrollDirection
     * @return
     */
    protected int findActiveItemIndex(ScrollDirectionDetector.ScrollDirection scrollDirection) {
        return findFirstActiveItemIndex(scrollDirection, mItemPositionHelper);
    }

    public interface ItemActivateListener {
        void onActive(int position);

        void onDeactivate(int position);
    }

    /**
     * 根据滚动方向寻找第一个完整显示的Item。
     * <p>
     * 向上滚动时从上选择第一个。
     * 向下滚动时从下选择第一个。
     *
     * @param scrollDirection
     * @param itemPositionHelper
     * @return
     */
    private static int findFirstActiveItemIndex(ScrollDirectionDetector.ScrollDirection scrollDirection, IItemPositionHelper itemPositionHelper) {
        final int firstVisibleItem = itemPositionHelper.getFirstVisiblePosition();
        final int lastVisibleItem = itemPositionHelper.getLastVisiblePosition();
        int activeIndex = -1;
        View activeView;
        int percent = 0;
        if (scrollDirection == ScrollDirectionDetector.ScrollDirection.UP) {
            activeIndex = firstVisibleItem;
            activeView = itemPositionHelper.getChildAt(activeIndex);
            percent = ItemVisibleHelper.getVisiblePercentage(activeView);

            if (percent < 80) {
                int index = activeIndex + 1;
                if (index <= lastVisibleItem) {
                    View itemView = itemPositionHelper.getChildAt(index);
                    int currentPercent = ItemVisibleHelper.getVisiblePercentage(itemView);
                    if (currentPercent > percent) {
                        activeView = itemView;
                        activeIndex = index;
                        percent = currentPercent;
                    }
                }
            }
        } else if (scrollDirection == ScrollDirectionDetector.ScrollDirection.DOWN) {
            activeIndex = lastVisibleItem;
            activeView = itemPositionHelper.getChildAt(activeIndex);
            percent = ItemVisibleHelper.getVisiblePercentage(activeView);

            if (percent < 80) {
                int index = activeIndex - 1;
                if (index >= firstVisibleItem) {
                    View itemView = itemPositionHelper.getChildAt(index);
                    int currentPercent = ItemVisibleHelper.getVisiblePercentage(itemView);
                    if (currentPercent > percent) {
                        activeView = itemView;
                        activeIndex = index;
                        percent = currentPercent;
                    }
                }
            }
        } else {
            activeIndex = -1;
        }
        return activeIndex;
    }
}
