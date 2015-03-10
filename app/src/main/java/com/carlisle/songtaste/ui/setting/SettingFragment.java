package com.carlisle.songtaste.ui.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by carlisle on 3/10/15.
 */
public class SettingFragment extends BaseFragment {
    @InjectView(R.id.tv_power_off)
    TextView powerOffButton;
    @InjectView(R.id.cb_use_gprs)
    CheckBox useGprsCheckBox;
    @InjectView(R.id.rl_use_gprs)
    RelativeLayout useGprsButton;
    @InjectView(R.id.tv_cache_values)
    TextView cacheValues;
    @InjectView(R.id.rl_clear_cache)
    RelativeLayout clearCacheButton;
    @InjectView(R.id.tv_about)
    TextView aboutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.inject(this, view);
        return view;
    }
}
