package com.amgl.viewscrollhelper.position;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by 阿木 on 2017/5/9.
 */

public class RecyclerViewItemPositionHelper implements IItemPositionHelper {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    public RecyclerViewItemPositionHelper(RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        mRecyclerView = recyclerView;
        mLayoutManager = layoutManager;
    }

    @Override
    public View getChildAt(int position) {
        int firstIndex = getFirstVisiblePosition();
        int index = position - firstIndex;
        return mRecyclerView.getChildAt(index);
    }

    @Override
    public int indexOfChild(View view) {
        return mRecyclerView.indexOfChild(view);
    }

    @Override
    public int getChildCount() {
        return mRecyclerView.getChildCount();
    }

    @Override
    public int getLastVisiblePosition() {
        return mLayoutManager.findLastVisibleItemPosition();
    }

    @Override
    public int getFirstVisiblePosition() {
        return mLayoutManager.findFirstVisibleItemPosition();
    }
}
