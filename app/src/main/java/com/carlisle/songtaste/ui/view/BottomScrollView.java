package com.carlisle.songtaste.ui.view;

/**
 * Created by carlisle on 3/17/15.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

public class BottomScrollView extends ScrollView {

    private OnScrollToBottomListener onScrollToBottom;

    public BottomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
                                  boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        Log.d("=++=>", "" + scrollX + "," + scrollY);
        if (scrollY > 0 && null != onScrollToBottom) {
            onScrollToBottom.onScrollBottomListener(clampedY);
        }
    }

    public void setOnScrollToBottomLintener(OnScrollToBottomListener listener) {
        onScrollToBottom = listener;
    }

    public interface OnScrollToBottomListener {
        public void onScrollBottomListener(boolean isBottom);
    }
}