package com.amgl.viewscrollhelper.position;

import android.view.View;

/**
 * Created by 阿木 on 2017/5/9.
 */

public interface IItemPositionHelper {
    View getChildAt(int position);

    int indexOfChild(View view);

    int getChildCount();

    int getLastVisiblePosition();

    int getFirstVisiblePosition();
}
