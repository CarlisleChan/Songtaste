package com.carlisle.songtaste.ui.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.ui.view.PickerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by carlisle on 3/10/15.
 */
public class SettingFragment extends BaseFragment {

    @InjectView(R.id.cb_use_gprs)
    CheckBox useGprsCheckBox;
    @InjectView(R.id.tv_cache_values)
    TextView cacheValues;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @OnClick(R.id.rl_use_gprs)
    protected void onUseGPRSClick() {
        if (useGprsCheckBox.isChecked()) {

        } else {

        }
    }

    @OnClick(R.id.tv_timer)
    protected void onTimerClick() {
        View view = View.inflate(getActivity(), R.layout.dialog_timer, null);
        PickerView pickerView = (PickerView) view.findViewById(R.id.picker);
        List<String> data = new ArrayList<String>();
        data.add("15 分钟");
        data.add("30 分钟");
        data.add("45 分钟");
        data.add("1 小时");
        data.add("1.5 小时");
        data.add("2 小时");

        pickerView.setData(data);
        pickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(int index) {
                Toast.makeText(getActivity(), "you selected" + index, Toast.LENGTH_SHORT).show();
                // TODO get timer
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO set alarm
                    }
                })
                .setView(view)
                .create();

        dialog.show();
    }

    @OnClick(R.id.rl_clear_cache)
    protected void onClearCacheClick() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("确定清除缓存")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO clear cache
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        dialog.show();

    }

    private void clearCache() {

    }
}
