package com.amgl.tmediaplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 阿木 on 2017/5/9.
 */

public abstract class BaseRVAdapter<D> extends RecyclerView.Adapter<BaseViewHolder<D>> {
    private final List<D> mDataList = new ArrayList<>();

    @Override
    public void onBindViewHolder(BaseViewHolder<D> holder, int position) {
        D data = getItem(position);
        if (data != null) {
            holder.bindData(data, position);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }

    public D getItem(int position) {
        if (getItemCount() > position) {
            return mDataList.get(position);
        }
        return null;
    }

    public void setDataList(List<D> dataList) {
        mDataList.clear();
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
    }

    protected View createItemView(int layoutId, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }
}
