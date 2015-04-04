package com.carlisle.songtaste.ui.discover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.ui.discover.discoverFragments.AlbumFragment;
import com.carlisle.songtaste.ui.discover.discoverFragments.HotFragment;
import com.carlisle.songtaste.ui.discover.discoverFragments.NewFragment;
import com.carlisle.songtaste.ui.discover.discoverFragments.TagFragment;
import com.carlisle.songtaste.utils.ViewPagerHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 3/5/15.
 */
public class DiscoverFragment extends BaseFragment {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    ViewPager viewPager;

    private ViewPagerHelper viewPagerHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        ButterKnife.inject(this, view);

        viewPagerHelper = new ViewPagerHelper(viewPager, tabs, getActivity(), getChildFragmentManager());
        viewPagerHelper.addFragment(new NewFragment());
        viewPagerHelper.addFragment(new HotFragment());
        viewPagerHelper.addFragment(new AlbumFragment());
        viewPagerHelper.addFragment(new TagFragment());
        viewPagerHelper.init();

        return view;
    }

}
