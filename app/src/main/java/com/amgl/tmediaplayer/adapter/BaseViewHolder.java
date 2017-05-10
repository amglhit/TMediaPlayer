package com.amgl.tmediaplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by 阿木 on 2017/5/9.
 */

public abstract class BaseViewHolder<D> extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    protected D mData;
    protected int mPosition;

    public void bindData(D data, int position) {
        mData = data;
        mPosition = position;
        onBind();
    }

    protected abstract void onBind();
}
