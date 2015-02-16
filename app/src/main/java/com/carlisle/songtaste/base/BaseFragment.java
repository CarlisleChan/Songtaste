package com.carlisle.songtaste.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.stephanenicolas.loglifecycle.LogLifeCycle;
import com.hannesdorfmann.fragmentargs.FragmentArgs;


/**
 * Created by chengxin on 2/13/15.
 */
@LogLifeCycle
public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
    }

}