package com.carlisle.songtaste.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.carlisle.songtaste.R;

/**
 * Created by chengxin on 2/13/15.
 */
public class BaseActivity extends ActionBarActivity {

    protected DoucleClick doucleClick = new DoucleClick();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(null);
        super.onCreate(savedInstanceState);
    }

    public void pushFragment(BaseFragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_content, fragment, tag);
        ft.addToBackStack(fragment.getName());
        ft.commitAllowingStateLoss();
    }

    protected boolean popFragment() {
        FragmentManager fm = getSupportFragmentManager();
        final int entryCount = fm.getBackStackEntryCount();
        FragmentTransaction ft = fm.beginTransaction();
        boolean popSucceed = fm.popBackStackImmediate();
        ft.commit();
        return popSucceed;
    }

    protected void onFragmentEmpty() {
        finish();
    }

    protected BaseFragment getCurrentFragment() {
        FragmentManager fm = getSupportFragmentManager();
        return (BaseFragment) fm.findFragmentById(R.id.fragment_content);
    }

    protected void handleBack() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        BaseFragment currentFragment = getCurrentFragment();

        try {
            if (currentFragment != null) {
                if (!popFragment()) {
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DoucleClick {
        private static final int TIME_GAP = 5000;
        private long lastBackEventTime;

        public boolean requestDoubleClick() {
            long currentTime = System.currentTimeMillis();
            if (lastBackEventTime == 0 || currentTime <= lastBackEventTime || (currentTime - lastBackEventTime) >= TIME_GAP) {
                lastBackEventTime = currentTime;
                return false;
            }

            try {
                return true;
            } finally {
                lastBackEventTime = 0;
            }
        }
    }
    public int setStatusBarColor() {
        return getResources().getColor(android.R.color.transparent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.push_right_in_no_alpha, R.anim.push_right_out_no_alpha);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        overridePendingTransition(R.anim.push_right_in_no_alpha, R.anim.push_right_out_no_alpha);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
//        overridePendingTransition(R.anim.push_left_in_no_alpha, R.anim.push_left_out_no_alpha);
    }

}
