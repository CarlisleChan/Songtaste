package com.carlisle.songtaste.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.services.MusicService;
import com.carlisle.songtaste.ui.about.AboutActivity;
import com.carlisle.songtaste.ui.discover.DiscoverFragment;
import com.carlisle.songtaste.ui.favorite.FavoriteFragment;
import com.carlisle.songtaste.ui.local.LocalFragment;
import com.carlisle.songtaste.ui.offline.OfflineFragment;
import com.carlisle.songtaste.ui.setting.SettingActivity;
import com.carlisle.songtaste.utils.FragmentSwitcher;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PROFILE_SETTING = 1;
    private static int MENU_TYPE = R.menu.menu_discover;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private FragmentSwitcher switcher;

    private AccountHeader.Result headerResult = null;
    private Drawer.Result result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initFragment();

        startService(new Intent(this, MusicService.class));

        MENU_TYPE = R.menu.menu_local;

        // Handle Toolbar
        setSupportActionBar(toolbar);

        // Create a few sample profile
        final IProfile profile = new ProfileDrawerItem().withName("Mike Penz").withIcon(getResources().getDrawable(R.drawable.photo));

        // Create the AccountHeader
        headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.material)
                .addProfiles(
                        profile,
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                        new ProfileSettingDrawerItem().withName("Add Account").withDescription("Add new GitHub Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add).actionBarSize().paddingDp(5)).withIdentifier(PROFILE_SETTING),
                        new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public void onProfileChanged(View view, IProfile profile) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (profile instanceof IDrawerItem && ((IDrawerItem) profile).getIdentifier() == PROFILE_SETTING) {
                            IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman").withIcon(getResources().getDrawable(R.drawable.photo));
                            if (headerResult.getProfiles() != null) {
                                //we know that there are 2 setting elements. set the new profile above them ;)
                                headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
                            } else {
                                headerResult.addProfiles(newProfile);
                            }
                        }
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //first create the main drawer (this one will be used to add the second drawer on the other side)
        result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_discover).withIcon(FontAwesome.Icon.faw_compass).withIdentifier(0),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_favorite).withIcon(FontAwesome.Icon.faw_heart).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_offline).withIcon(FontAwesome.Icon.faw_cloud_download).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_local).withIcon(FontAwesome.Icon.faw_folder).withIdentifier(3),
                        new SectionDrawerItem().withName(R.string.drawer_section_name),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(4),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_about).withIcon(FontAwesome.Icon.faw_info_circle).withIdentifier(5)
                )
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

    private void initFragment() {
        switcher = new FragmentSwitcher(getSupportFragmentManager(), R.id.fragment_content);
        switcher.addFragment(new DiscoverFragment());
        switcher.addFragment(new FavoriteFragment());
        switcher.addFragment(new OfflineFragment());
        switcher.addFragment(new LocalFragment());
        switcher.switchToFragment(0);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
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
                finish();
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

}
