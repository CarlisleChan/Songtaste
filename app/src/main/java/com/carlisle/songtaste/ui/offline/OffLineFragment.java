package com.carlisle.songtaste.ui.offline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carlisle.songtaste.base.BaseFragment;

/**
 * Created by chengxin on 2/25/15.
 */
public class OffLineFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView text = new TextView(this.getActivity());
        text.setText("offline");
        text.setGravity(Gravity.CENTER);
        return text;
    }
}
