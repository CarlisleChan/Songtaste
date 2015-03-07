package com.carlisle.songtaste.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;

import com.carlisle.songtaste.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by chengxin on 2/13/15.
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        initStatusBar();
    }

    protected void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
            tintManager.setNavigationBarTintEnabled(true);

            tintManager.setTintColor(setStatusBarColor());

        }
    }

    protected void replaceFragment(BaseFragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
//        ft.setCustomAnimations(R.anim.right_to_left_enter, R.anim.left_to_right_exit, R.anim.pop_left_to_right_enter, R.anim.pop_left_to_right_exit);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_content, fragment, tag);
//        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commit();
//        ft.commitAllowingStateLoss();
    }

    protected int setStatusBarColor() {
        return getResources().getColor(android.R.color.transparent);
    }
}
