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
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.ui.discover.discoverFragments.AlbumFragment;
import com.carlisle.songtaste.ui.discover.discoverFragments.HotFragment;
import com.carlisle.songtaste.ui.discover.discoverFragments.NewFragment;
import com.carlisle.songtaste.ui.discover.discoverFragments.TagFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 3/5/15.
 */
public class DiscoverFragment extends BaseFragment {

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
        tabs.setBackgroundColor(getResources().getColor(R.color.primary));
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
            switch (position) {
                case 0:
                    return getNewFragment();
                case 1:
                    return getHotFragment();
                case 2:
                    return getAlbumFragment();
                case 3:
                    return getTagFragment();
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }
    }

    private NewFragment getNewFragment() {
        NewFragment fragment = (NewFragment) getChildFragmentManager().findFragmentByTag(NewFragment.class.getName());
        if (fragment == null) {
            fragment = new NewFragment();
        }
        return fragment;
    }

    private HotFragment getHotFragment() {
        HotFragment fragment = (HotFragment) getChildFragmentManager().findFragmentByTag(HotFragment.class.getName());
        if (fragment == null) {
            fragment = new HotFragment();
        }
        return fragment;
    }

    private AlbumFragment getAlbumFragment() {
        AlbumFragment fragment = (AlbumFragment) getChildFragmentManager().findFragmentByTag(AlbumFragment.class.getName());
        if (fragment == null) {
            fragment = new AlbumFragment();
        }
        return fragment;
    }

    private TagFragment getTagFragment() {
        TagFragment fragment = (TagFragment) getChildFragmentManager().findFragmentByTag(TagFragment.class.getName());
        if (fragment == null) {
            fragment = new TagFragment();
        }
        return fragment;
    }
}
