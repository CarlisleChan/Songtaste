package com.carlisle.songtaste.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.events.PauseEvent;
import com.carlisle.songtaste.events.PlayEvent;
import com.carlisle.songtaste.events.ProgressEvent;
import com.carlisle.songtaste.events.SkipToNextEvent;
import com.carlisle.songtaste.events.SkipToPrevEvent;
import com.carlisle.songtaste.events.UpdateUIEvent;
import com.carlisle.songtaste.modle.SongDetailInfo;
import com.carlisle.songtaste.services.Playback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by carlisle on 3/5/15.
 */
public class NowPlayingActivity extends BaseActivity {
    public static final String NOW_PLAYING = "now playing";

    @InjectView(R.id.background_image)
    ImageView backgroundImage;
    @InjectView(R.id.tv_song_name)
    TextView songName;
    @InjectView(R.id.tv_singer_name)
    TextView singerName;
    @InjectView(R.id.tv_start_time)
    TextView startTime;
    @InjectView(R.id.seekbar)
    SeekBar seekbar;
    @InjectView(R.id.tv_end_time)
    TextView endTime;
    @InjectView(R.id.im_prev)
    ImageView prevButton;
    @InjectView(R.id.cb_play_pause)
    CheckBox playOrPause;
    @InjectView(R.id.im_next)
    ImageView nextButton;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    SongDetailInfo songDetailInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_player);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);

        if (getIntent() != null) {
            songDetailInfo = (SongDetailInfo) getIntent().getParcelableExtra(NOW_PLAYING);
            Log.d("now playing", JSON.toJSONString(songDetailInfo));
            if (songDetailInfo != null) {
                songName.setText(songDetailInfo.getSong_name());
                singerName.setText(songDetailInfo.getSinger_name());
            }
        }

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    EventBus.getDefault().post(new ProgressEvent(progress, seekBar.getMax(), true));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @OnClick({R.id.im_prev, R.id.cb_play_pause, R.id.im_next})
    public void onControllerClick(View view) {
        switch (view.getId()) {
            case R.id.im_prev:
                EventBus.getDefault().post(new SkipToPrevEvent());
                break;
            case R.id.cb_play_pause:
                if (!((CheckBox)view).isChecked()) {
                    EventBus.getDefault().post(new PlayEvent());
                } else {
                    EventBus.getDefault().post(new PauseEvent());
                }
                break;
            case R.id.im_next:
                EventBus.getDefault().post(new SkipToNextEvent());
                break;
        }
    }

    public void onEvent(UpdateUIEvent event) {
        switch (event.state) {
            case Playback.STATE_PAUSED:
                playOrPause.setChecked(true);
                break;
            case Playback.STATE_STOPPED:
                playOrPause.setChecked(false);
                break;
            default:
                songDetailInfo = event.songDetailInfo;
                songName.setText(songDetailInfo.getSong_name());
                singerName.setText(songDetailInfo.getSinger_name());
                playOrPause.setChecked(false);
                break;
        }
    }

    public int position = 0;
    public void onEvent(ProgressEvent progressEvent) {
        Log.d("onEvent=====>","time:" + progressEvent.currentPosition);
        if (progressEvent.trackTouch) return;
        if (progressEvent.currentPosition == position) {
            // loading, show progress
            Log.d("loading=====>","=====");
        } else {
            position = progressEvent.currentPosition;
        }

        seekbar.setProgress(progressEvent.currentPosition);
        seekbar.setMax(progressEvent.maxPosition);
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(PlaybackControlsFragment.PLAYBACK_STATE, playOrPause.isChecked());
        setResult(RESULT_OK, intent);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
