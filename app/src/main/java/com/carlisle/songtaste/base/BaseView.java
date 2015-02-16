package com.carlisle.songtaste.base;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import icepick.Icepick;

/**
 * Created by chengxin on 2/13/15.
 */
public class BaseView extends View {
    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

}