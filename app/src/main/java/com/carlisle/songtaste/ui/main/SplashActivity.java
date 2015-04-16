package com.carlisle.songtaste.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.cmpts.services.MusicService;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 4/15/15.
 */
public class SplashActivity extends BaseActivity implements Animation.AnimationListener {

    Handler handler;
    Animation anim;
    Animation anim1;
    Animation anim2;

    @InjectView(R.id.start_logo)
    ImageView startLogo;
    @InjectView(R.id.start_bg)
    FrameLayout startBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);

        anim = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in);
        anim1 = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in);
        anim2 = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in);

        anim.setAnimationListener(this);
        startBg.startAnimation(anim2);
        startLogo.startAnimation(anim);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Intent intent1 = new Intent();
        intent1.setClass(this, MainActivity.class);
        this.startActivity(intent1);
        this.finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

}
