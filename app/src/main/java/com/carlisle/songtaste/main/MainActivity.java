package com.carlisle.songtaste.main;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.drawer.NavigationDrawerCallbacks;
import com.carlisle.songtaste.drawer.NavigationDrawerFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 2/13/15.
 */
public class MainActivity extends BaseActivity implements NavigationDrawerCallbacks{

    @InjectView(R.id.container)
    FrameLayout container;
    @InjectView(R.id.drawer)
    DrawerLayout drawer;
    @InjectView(R.id.action_bar)
    Toolbar toolbar;

    private NavigationDrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = getmNavigationDrawerFragment();
        drawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar);
    }

    private NavigationDrawerFragment getmNavigationDrawerFragment() {
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        if (navigationDrawerFragment == null) {
            return new NavigationDrawerFragment();
        }
        return navigationDrawerFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
    }
}
