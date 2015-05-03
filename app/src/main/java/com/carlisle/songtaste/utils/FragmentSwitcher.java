package com.carlisle.songtaste.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlisle on 3/8/15.
 */
public class FragmentSwitcher {
    private List<Fragment> fragments = new ArrayList();
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int containerResId;

    public FragmentSwitcher(FragmentManager fragmentManager, int containerResId) {
        this.fragmentManager = fragmentManager;
        this.containerResId = containerResId;
    }

    public void addFragment(Fragment fragment) {
        if (!fragment.isAdded()) {
            fragments.add(fragment);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(containerResId, fragment, fragment.toString());
            fragmentTransaction.hide(fragment);
            fragmentTransaction.commit();
        }

    }

    public Fragment getFragment(int index) {
        return index < fragments.size() ? fragments.get(index) : null;
    }

    public void switchToFragment(int index) {
        fragmentTransaction = fragmentManager.beginTransaction();

        for (int i = 0; i < fragments.size(); i++) {
            fragmentTransaction.hide(fragments.get(i));
            fragments.get(i).setUserVisibleHint(false);
        }

        fragmentTransaction.show(fragments.get(index));
        fragments.get(index).setUserVisibleHint(true);
        fragmentTransaction.commit();
    }
}
