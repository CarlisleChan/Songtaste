package com.carlisle.songtaste.ui.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.ui.view.PickerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by carlisle on 3/7/15.
 */
public class SettingActivity extends BaseActivity {

    @InjectView(R.id.cb_use_gprs)
    CheckBox useGprsCheckBox;
    @InjectView(R.id.tv_cache_values)
    TextView cacheValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("设置");

    }

    @Override
    public MenuInflater getMenuInflater() {
        return super.getMenuInflater();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.rl_use_gprs)
    protected void onUseGPRSClick() {
        if (useGprsCheckBox.isChecked()) {

        } else {

        }
    }

    @OnClick(R.id.tv_timer)
    protected void onTimerClick() {
        View view = View.inflate(this, R.layout.dialog_timer, null);
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
                Toast.makeText(SettingActivity.this, "you selected" + index, Toast.LENGTH_SHORT).show();
                // TODO get timer
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
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
        AlertDialog dialog = new AlertDialog.Builder(this)
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
