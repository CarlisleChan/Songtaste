package com.carlisle.songtaste.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

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
import com.carlisle.songtaste.ui.about.AboutActivity;
import com.carlisle.songtaste.ui.develop.DeveloperOptionsActivity;
import com.carlisle.songtaste.ui.discover.DiscoverFragment;
import com.carlisle.songtaste.ui.discover.discoverFragments.AlbumDetailFragment;
import com.carlisle.songtaste.ui.discover.discoverFragments.TagDetailFragment;
import com.carlisle.songtaste.ui.favorite.FavoriteFragment;
import com.carlisle.songtaste.ui.local.LocalFragment;
import com.carlisle.songtaste.ui.login.LoginActicity;
import com.carlisle.songtaste.ui.offline.OfflineFragment;
import com.carlisle.songtaste.ui.setting.SettingActivity;
import com.carlisle.songtaste.utils.FragmentSwitcher;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
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

    private AccountHeader.Result headerResult = null;
    private Drawer.Result result = null;

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
        initMenu();
        initNavigationDrawer(savedInstanceState);
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
                Log.d("这个设备的 id: ", AVInstallation.getCurrentInstallation().getInstallationId());
                AVInstallation.getCurrentInstallation().saveInBackground();
            }
        });
    }

    private void initNavigationDrawer(Bundle savedInstanceState) {
        final IProfile profile = new ProfileDrawerItem().withName("登录").withIcon(getResources().getDrawable(R.drawable.default_artist));
        headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.bg_album)
                .addProfiles(profile)
                .withHeightDp(120)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        startActivity(new Intent(MainActivity.this, LoginActicity.class));
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        result = new Drawer()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_discover).withIcon(FontAwesome.Icon.faw_compass).withIdentifier(0),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_favorite).withIcon(FontAwesome.Icon.faw_heart).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_offline).withIcon(FontAwesome.Icon.faw_cloud_download).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_local).withIcon(FontAwesome.Icon.faw_folder).withIdentifier(3),
                        new SectionDrawerItem().withName(R.string.drawer_section_name),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(4),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_about).withIcon(FontAwesome.Icon.faw_info_circle).withIdentifier(5)
                )
                .withFullscreen(true)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 4) {
                                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                            } else if (drawerItem.getIdentifier() == 5) {
                                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                            } else {
                                switcher.switchToFragment(drawerItem.getIdentifier());
                                setToolBarTitle(drawerItem.getIdentifier());
                                setMenuType(drawerItem.getIdentifier());
                            }
                        }
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .build();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick","one");
                if (doucleClick.requestDoubleClick()) {
                    Log.d("onClick","two");
                    EventBus.getDefault().post(new RefreshDataEvent());
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = getCurrentFragment().getClass().getSimpleName();
                if (!className.equals(AlbumDetailFragment.class.getSimpleName())
                        && !className.equals(TagDetailFragment.class.getSimpleName())) {
                    result.openDrawer();
                } else {
                    handleBack();
                    resetToolbarTitleAndIcon("Songtaste", R.drawable.ic_menu);
                }
            }
        });
    }

    private void initMenu() {
        MENU_TYPE = R.menu.menu_local;
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
        switcher.addFragment(new OfflineFragment());
        switcher.addFragment(new LocalFragment());
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
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else if (slidingUpPanelLayout != null
                && (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED
                || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            handleBack();
            resetToolbarTitleAndIcon("Songtaste", R.drawable.ic_menu);
        }

    }

    private void setToolBarTitle(int position) {
        switch (position) {
            case 0:
                getSupportActionBar().setTitle("发现音乐");
                break;
            case 1:
                getSupportActionBar().setTitle("我的收藏");
                break;
            case 2:
                getSupportActionBar().setTitle("我的离线");
                break;
            case 3:
                getSupportActionBar().setTitle("本地音乐");
                break;
        }
    }

    public void resetToolbarTitleAndIcon(String title, int icon) {
        getSupportActionBar().setTitle(title);
        toolbar.setNavigationIcon(getResources().getDrawable(icon));
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        menu.clear();
        creatMenu(menu, MENU_TYPE);
        return super.onPrepareOptionsPanel(view, menu);
    }

    private void creatMenu(Menu menu, int menuType) {
        getMenuInflater().inflate(menuType, menu);
    }

    public void setMenuType(int menuType) {
        switch (menuType) {
            case 0:
                MENU_TYPE = R.menu.menu_discover;
                break;
            case 1:
                MENU_TYPE = R.menu.menu_favorite;
                break;
            case 2:
                MENU_TYPE = R.menu.menu_offline;
                break;
            case 3:
                MENU_TYPE = R.menu.menu_local;
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                quit();
                return true;
            case R.id.action_scan_again:
                MENU_TYPE = R.menu.menu_offline;
                return true;
            case R.id.action_manager:
                return true;
            case R.id.action_start_download:
                return true;
            case R.id.action_stop_download:
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
