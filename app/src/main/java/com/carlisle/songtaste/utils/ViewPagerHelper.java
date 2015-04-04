package com.carlisle.songtaste.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.cmpts.events.RefreshEvent;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by carlisle on 4/4/15.
 */
public class ViewPagerHelper {

    public List<BaseFragment> fragments = new ArrayList<BaseFragment>();

    private ViewPager viewPager;
    private PagerSlidingTabStrip tabStrip;

    private Context context;
    private FragmentManager fragmentManager;

    private HashMap<Integer, Object> mObjs = new LinkedHashMap<Integer, Object>();
    private View mLeft;
    private View mRight;

    public ViewPagerHelper(ViewPager viewPager, PagerSlidingTabStrip tabStrip, final Context context, FragmentManager fragmentManager) {
        this.viewPager = viewPager;
        this.tabStrip = tabStrip;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void addFragment(BaseFragment fragment) {
        fragments.add(fragment);
    }

    public void init() {
        viewPager.setAdapter(new DiscoverAdapter(fragmentManager));
        tabStrip.setViewPager(viewPager);
        tabStrip.setBackgroundColor(context.getResources().getColor(R.color.primary));
        tabStrip.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int i) {
                EventBus.getDefault().post(new RefreshEvent(i));
            }
        });
        tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                float effectOffset = isSmall(positionOffset) ? 0 : positionOffset;

                mLeft = findViewFromObject(position);
                mRight = findViewFromObject(position + 1);

                animateFade(mLeft, mRight, effectOffset);

//                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    protected void animateFade(View left, View right, float positionOffset) {
        if (left != null) {
            ViewHelper.setAlpha(left, 1 - positionOffset);
        }
        if (right != null) {
            ViewHelper.setAlpha(right, positionOffset);
        }
    }

    private boolean isSmall(float positionOffset) {
        return Math.abs(positionOffset) < 0.0001;
    }

    public View findViewFromObject(int position) {
        Object o = mObjs.get(Integer.valueOf(position));
        if (o == null) {
            return null;
        }
        PagerAdapter a = viewPager.getAdapter();
        View v;
        for (int i = 0; i < viewPager.getChildCount(); i++) {
            v = viewPager.getChildAt(i);
            if (a.isViewFromObject(v, o))
                return v;
        }
        return null;
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
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }
    }
}
