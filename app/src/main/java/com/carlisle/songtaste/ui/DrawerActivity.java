package com.carlisle.songtaste.ui;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.modle.FMNewResult;
import com.carlisle.songtaste.provider.ApiFactory;
import com.carlisle.songtaste.provider.converter.GsonConverter;
import com.carlisle.songtaste.ui.drawer.NavigationDrawerFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;

/**
 * Created by chengxin on 2/13/15.
 */
public class DrawerActivity extends BaseActivity {

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
        setContentView(R.layout.activity_drawer);
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


            ApiFactory.getSongtasteApi(new GsonConverter(GsonConverter.ConverterType.FM_NEW_RESULT)).recList("1", "1", "0", "dm.st.fmNew")
                    .subscribe(new Observer<FMNewResult>() {
                        @Override
                        public void onCompleted() {
                            Log.d("====>", "onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Log.d("====>", "onError");
                        }

                        @Override
                        public void onNext(FMNewResult songListResult) {
                            Log.d("recList====>", "" + songListResult.code);
                            Log.d("recList====>", songListResult.data.get(0).Name);
                        }
                    });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
