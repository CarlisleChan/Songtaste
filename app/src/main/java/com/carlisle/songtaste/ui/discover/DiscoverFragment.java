package com.carlisle.songtaste.ui.discover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.carlisle.songtaste.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 3/5/15.
 */
public class DiscoverFragment extends Fragment {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    ViewPager pager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        ButterKnife.inject(this, view);

        pager.setAdapter(new DiscoverAdapter(getChildFragmentManager()));
        tabs.setViewPager(pager);
        tabs.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int i) {
                Toast.makeText(getActivity(), "" + i, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public class DiscoverAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"最新", "热门", "专辑", "标签"};

        public DiscoverAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Fragment getItem(int position) {
            return SuperAwesomeCardFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }
    }
}
