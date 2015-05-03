package com.carlisle.songtaste.ui.setting;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.ui.develop.DeveloperOptionsActivity;
import com.carlisle.songtaste.ui.view.timePicker.PickerView;
import com.carlisle.songtaste.utils.FileUtils;
import com.carlisle.songtaste.utils.LeancloudEventIDS;
import com.carlisle.songtaste.utils.PreferencesHelper;

import org.joda.time.LocalTime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * Created by carlisle on 3/7/15.
 */
public class SettingActivity extends BaseActivity implements SwipeBackActivityBase {

    @InjectView(R.id.cb_use_gprs)
    CheckBox useGprsCheckBox;
    @InjectView(R.id.tv_cache_values)
    TextView cacheValues;
    @InjectView(R.id.rl_developer_options)
    RelativeLayout developerOptions;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private ProgressDialog progressDialog;
    private Double time;
    private SwipeBackActivityHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("设置");

        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        try {
            cacheValues.setText(String.valueOf(FileUtils.getFolderSize(new File(getExternalCacheDir(), "audio"))) + "MB");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PreferencesHelper.getInstance(this).getBoolean(PreferencesHelper.DEVELOPER_OPTIONS, false)) {
            developerOptions.setVisibility(View.VISIBLE);
        }
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
        AVAnalytics.onEvent(this, LeancloudEventIDS.GPRS_CLICK);

        if (useGprsCheckBox.isChecked()) {
            useGprsCheckBox.setChecked(false);
        } else {
            useGprsCheckBox.setChecked(true);
        }
        PreferencesHelper.getInstance(this).setPlayOnlyWifi(!useGprsCheckBox.isChecked());
    }

    @OnClick(R.id.tv_timer)
    protected void onTimerClick() {
        View view = View.inflate(this, R.layout.dialog_timer, null);
        PickerView pickerView = (PickerView) view.findViewById(R.id.picker);
        final List<String> data = new ArrayList<String>();
        data.add("15 分钟");
        data.add("30 分钟");
        data.add("45 分钟");
        data.add("1 小时");
        data.add("1.5 小时");
        data.add("2 小时");

        pickerView.setData(data);
        pickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String string) {
                time = Double.parseDouble(string.substring(0, string.length() - 3));
                if (time > 2) {
                    time = time * 60 * 1000;
                } else {
                    time = time * 60 * 60 * 1000;
                }
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AVAnalytics.onEvent(SettingActivity.this, LeancloudEventIDS.TIMER_CLICK, data.get(3).toString());
                        setAlarm();
                        Toast.makeText(SettingActivity.this, data.get(3).toString() + "后将停止播放", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setView(view)
                .create();

//        Window window = dialog.getWindow();
//        // 设置显示动画
//        window.setWindowAnimations(R.style.main_menu_animstyle);
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        window.setGravity(Gravity.CENTER);
//        // 设置点击外围解散
//        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StopPlaybackService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        LocalTime localTime = new LocalTime();
        localTime = localTime.plusSeconds(time.intValue());
        alarmManager.set(AlarmManager.RTC_WAKEUP, localTime.toDateTimeToday().getMillis(), pendingIntent);
    }

    @OnClick(R.id.rl_clear_cache)
    protected void onClearCacheClick() {
        AVAnalytics.onEvent(this, LeancloudEventIDS.CLEAR_CACHE_CLICK);

        View view = View.inflate(this, R.layout.dialog_clear_cache, null);
        TextView cancleButton = (TextView) view.findViewById(R.id.cancle);
        TextView confirmButton = (TextView) view.findViewById(R.id.confirm);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.show();

        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCache();
                dialog.dismiss();
            }
        });

    }

    @OnClick(R.id.rl_developer_options)
    public void onDevelopClick() {
        startActivity(new Intent(this, DeveloperOptionsActivity.class));
    }

    private void clearCache() {
        progressDialog = ProgressDialog.show(this, "", "正在清除中, 请稍后..");
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    FileUtils.deleteFolderFile(getExternalCacheDir().getPath(), true);
                    cacheValues.setText(String.valueOf(FileUtils.getFolderSize(getExternalCacheDir()) + "MB"));
                    SongDetailInfo.deleteAll();
                    progressDialog.dismiss();
                    Toast.makeText(SettingActivity.this, "已清除", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v != null)
            return v;
        return mHelper.findViewById(id);
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        getSwipeBackLayout().scrollToFinishActivity();
    }

}
