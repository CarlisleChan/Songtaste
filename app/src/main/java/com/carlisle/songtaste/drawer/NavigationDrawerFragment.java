package com.carlisle.songtaste.drawer;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.makeramen.RoundedImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 2/13/15.
 */
public class NavigationDrawerFragment extends BaseFragment {

    @InjectView(R.id.user_avatar)
    RoundedImageView userAvatar;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.btn_found)
    Button foundButton;
    @InjectView(R.id.btn_down_load)
    Button gownLoadButton;
    @InjectView(R.id.btn_favorite)
    Button favoriteButton;
    @InjectView(R.id.btn_local)
    Button localButton;

    private NavigationDrawerCallbacks callbacks;
    private View fragmentContainerView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        fragmentContainerView = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;
        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;

                getActivity().invalidateOptionsMenu();
            }
        };

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    public void openDrawer() {
        drawerLayout.openDrawer(fragmentContainerView);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(fragmentContainerView);
    }

    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }


}
