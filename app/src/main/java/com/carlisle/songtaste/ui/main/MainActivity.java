package com.carlisle.songtaste.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.carlisle.songtaste.BuildConfig;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.cmpts.events.RefreshDataEvent;
import com.carlisle.songtaste.cmpts.services.MusicService;
import com.carlisle.songtaste.ui.develop.DeveloperOptionsActivity;
import com.carlisle.songtaste.ui.discover.DiscoverFragment;
import com.carlisle.songtaste.ui.favorite.FavoriteFragment;
import com.carlisle.songtaste.ui.setting.SettingActivity;
import com.carlisle.songtaste.utils.FragmentSwitcher;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PROFILE_SETTING = 1;
    private static int MENU_TYPE = R.menu.menu_discover;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;

    private FragmentSwitcher switcher;
    private NowPlayingFragment nowPlayingFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        startService(new Intent(this, MusicService.class));
        if (savedInstanceState == null) {
            createAddFragment();
        } else {
            this.nowPlayingFragment = (NowPlayingFragment) getSupportFragmentManager()
                    .findFragmentByTag(NowPlayingFragment.class.getSimpleName());
        }

        initToolbar();
        initFragmentSwitcher();
        initSlidingUpPanel();
        initLeanCloudPush();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        AVAnalytics.trackAppOpened(intent);
    }

    private void initLeanCloudPush() {
        PushService.setDefaultPushCallback(this, MainActivity.class);
        PushService.subscribe(this, "public", MainActivity.class);
        PushService.subscribe(this, "protected", MainActivity.class);
        if (BuildConfig.DEBUG) {
            PushService.subscribe(this, "private", DeveloperOptionsActivity.class);
        }

        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                AVInstallation.getCurrentInstallation().saveInBackground();
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doucleClick.requestDoubleClick()) {
                    EventBus.getDefault().post(new RefreshDataEvent());
                }
            }
        });
    }

    private void createAddFragment() {
        if (nowPlayingFragment != null) {
            return;
        }
        nowPlayingFragment = new NowPlayingFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_playback_control, nowPlayingFragment, NowPlayingFragment.class.getSimpleName())
                .commit();
    }

    private void initFragmentSwitcher() {
        switcher = new FragmentSwitcher(getSupportFragmentManager(), R.id.fragment_content);
        switcher.addFragment(new DiscoverFragment());
        switcher.addFragment(new FavoriteFragment());
        switcher.switchToFragment(0);
    }

    private void initSlidingUpPanel() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                nowPlayingFragment.hideBottomControl(v);
            }

            @Override
            public void onPanelCollapsed(View view) {

            }

            @Override
            public void onPanelExpanded(View view) {

            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (slidingUpPanelLayout != null
                && (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED
                || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_discover, menu);
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                quit();
                return true;
            case R.id.action_sound_recognition:
                startActivity(new Intent(this, SoundRecognitionActivity.class));
                return true;
            case R.id.action_setting:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void quit() {
        this.stopService(new Intent(this.getApplicationContext(), MusicService.class));
        this.finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
