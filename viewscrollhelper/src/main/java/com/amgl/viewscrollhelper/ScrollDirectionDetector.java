package com.amgl.viewscrollhelper;

import android.content.Context;
import android.view.View;
import android.view.ViewConfiguration;

import com.amgl.viewscrollhelper.position.IItemPositionHelper;

/**
 * Created by 阿木 on 2017/5/9.
 */

public class ScrollDirectionDetector {
    public interface ScrollDirectionChangeListener {
        void onScrollDirectionChanged(ScrollDirection scrollDirection);
    }

    public enum ScrollDirection {
        UP, DOWN
    }

    private final ScrollDirectionChangeListener mScrollDirectionChangeListener;
    private final int mTouchSlop;
    private IItemPositionHelper mItemPositionHelper;

    public ScrollDirectionDetector(Context context, ScrollDirectionChangeListener scrollDirectionChangeListener, IItemPositionHelper itemPositionHelper) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScrollDirectionChangeListener = scrollDirectionChangeListener;
        mItemPositionHelper = itemPositionHelper;
    }

    private int mOldFirstVisibleItem = 0;
    private int mOldTop = 0;
    private ScrollDirection mOldScrollDirection = null;

    public void onScroll() {
        final int firstVisibleItem = mItemPositionHelper.getFirstVisiblePosition();
        final View firstChild = mItemPositionHelper.getChildAt(0);
        final int top = (firstChild == null) ? 0 : firstChild.getTop();

        int val = top - mOldTop;
        if (Math.abs(val) > mTouchSlop * 2) {
            mOldTop = top;
        } else {
            val = 0;
        }

        if (firstVisibleItem == mOldFirstVisibleItem) {
            if (val > 0) {
                onScrollDown();
            } else if (val < 0) {
                onScrollUp();
            }
        } else {
            if (firstVisibleItem < mOldFirstVisibleItem) {
                onScrollDown();
            } else {
                onScrollUp();
            }
        }
        mOldFirstVisibleItem = firstVisibleItem;
    }

    private void onScrollUp() {
        if (ScrollDirection.UP != mOldScrollDirection) {
            mOldScrollDirection = ScrollDirection.UP;
            mScrollDirectionChangeListener.onScrollDirectionChanged(ScrollDirection.UP);
        }
    }

    private void onScrollDown() {
        if (ScrollDirection.DOWN != mOldScrollDirection) {
            mOldScrollDirection = ScrollDirection.DOWN;
            mScrollDirectionChangeListener.onScrollDirectionChanged(ScrollDirection.DOWN);
        }
    }

    public ScrollDirection getScrollDirection() {
        return mOldScrollDirection;
    }
}
