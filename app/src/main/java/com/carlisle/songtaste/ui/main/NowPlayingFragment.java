package com.carlisle.songtaste.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.cmpts.events.PauseEvent;
import com.carlisle.songtaste.cmpts.events.PlayEvent;
import com.carlisle.songtaste.cmpts.events.ProgressEvent;
import com.carlisle.songtaste.cmpts.events.SkipToNextEvent;
import com.carlisle.songtaste.cmpts.events.SkipToPrevEvent;
import com.carlisle.songtaste.cmpts.events.UpdateUIEvent;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.services.Playback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by carlisle on 4/3/15.
 */
public class NowPlayingFragment extends BaseFragment {

    @InjectView(R.id.toolbar_container)
    View toolbarContainer;
    @InjectView(R.id.tv_song_name)
    TextView songName;
    @InjectView(R.id.tv_singer_name)
    TextView singerName;
    @InjectView(R.id.seekbar)
    SeekBar seekbar;
    @InjectView(R.id.im_prev)
    ImageView prevButton;
    @InjectView(R.id.cb_play_pause)
    CheckBox playOrPause;
    @InjectView(R.id.im_next)
    ImageView nextButton;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.bottom_control)
    View bottonControl;

    SongDetailInfo songDetailInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_player, container, false);

//        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser == true) {
//                    EventBus.getDefault().post(new ProgressEvent(progress, seekBar.getMax(), true));
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
        ButterKnife.inject(this, view);
        return view;
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
                if (event.songDetailInfo != null) {
                    songDetailInfo = event.songDetailInfo;
                    songName.setText(songDetailInfo.getSong_name());
                    singerName.setText(songDetailInfo.getSinger_name());
                    playOrPause.setChecked(false);
                }
                break;
        }
    }

    public int position = 0;
    public void onEvent(ProgressEvent progressEvent) {
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

    public void hideBottomControl(float v) {
        bottonControl.setAlpha(1-v);
        toolbarContainer.setAlpha(v);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_local, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(getActivity(), "index is"  + " && menu text is " + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}
