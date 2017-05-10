package com.amgl.viewscrollhelper;

import android.graphics.Rect;
import android.view.View;

/**
 * Created by 阿木 on 2017/5/10.
 */

public class ItemViewHelper {
    /**
     * view可见部分百分比
     *
     * @param itemView
     * @return
     */
    public static int getVisiblePercentage(View itemView) {
        int percentage = 0;
        if (itemView == null)
            return percentage;
        final int height = itemView.getHeight();

        final Rect rect = new Rect();
        itemView.getGlobalVisibleRect(rect);

        final int top = rect.top;
        final int bottom = rect.bottom;

        if (top > 0) {
            percentage = (height - top) * 100 / height;
        } else if (bottom > 0 && bottom < height) {
            percentage = bottom * 100 / height;
        } else {
            percentage = 100;
        }
        return percentage;
    }
}
