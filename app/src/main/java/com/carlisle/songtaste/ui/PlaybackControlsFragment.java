package com.carlisle.songtaste.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.events.PauseEvent;
import com.carlisle.songtaste.events.PlayEvent;
import com.carlisle.songtaste.events.SkipToNextEvent;
import com.carlisle.songtaste.events.UpdateUIEvent;
import com.carlisle.songtaste.modle.Result;
import com.carlisle.songtaste.modle.SongDetailInfo;
import com.carlisle.songtaste.provider.ApiFactory;
import com.carlisle.songtaste.provider.converter.XmlConverter;
import com.carlisle.songtaste.services.Playback;
import com.carlisle.songtaste.utils.UserHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

/**
 * Created by carlisle on 3/22/15.
 */
public class PlaybackControlsFragment extends BaseFragment {
    private static final String TAG = PlaybackControlsFragment.class.getSimpleName();
    public static final String PLAYBACK_STATE = "playback state";

    @InjectView(R.id.album_art)
    ImageButton albumArt;
    @InjectView(R.id.tv_song_name)
    TextView songName;
    @InjectView(R.id.tv_singer_name)
    TextView singerName;
    @InjectView(R.id.content)
    LinearLayout content;
    @InjectView(R.id.im_next)
    ImageButton next;
    @InjectView(R.id.im_favorite)
    ImageButton favorite;
    @InjectView(R.id.container)
    RelativeLayout container;
    @InjectView(R.id.cb_play_pause)
    CheckBox playOrPause;

    SongDetailInfo songDetailInfo;
    Subscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playback_controls, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @OnClick(R.id.container)
    public void onContainerClick() {
        Intent intent = new Intent(getActivity(), NowPlayingActivity.class);
        intent.putExtra(NowPlayingActivity.NOW_PLAYING, songDetailInfo);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        boolean type = data.getBooleanExtra(PLAYBACK_STATE, false);
        Log.i(TAG, "==>");

    }

    @OnClick(R.id.im_next)
    public void onNextClick() {
        EventBus.getDefault().post(new SkipToNextEvent());
    }

    @OnClick(R.id.im_favorite)
    public void onFavoriteClick(View view) {
        subscription = AndroidObservable.bindFragment(this, new ApiFactory().getSongtasteApi(new XmlConverter(XmlConverter.ConvterType.COLLECTION))
                .collection(UserHelper.getInstance().getUID(), songDetailInfo.getMediaId(), "xml"))
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Result result) {
                        if (result.code == 1) {
//                    view.setBackground(getActivity().getResources().getDrawable());
                            Toast.makeText(getActivity(), result.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @OnClick(R.id.cb_play_pause)
    public void onPlayOrPauseClick(View view) {
        if (!((CheckBox) view).isChecked()) {
            EventBus.getDefault().post(new PlayEvent());
        } else {
            EventBus.getDefault().post(new PauseEvent());
        }
    }

    public void onEvent(UpdateUIEvent event) {
        container.setVisibility(View.VISIBLE);

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

//        Picasso.with(getActivity())
//                .load("")
//                .placeholder(R.drawable.ic_account_circle_grey600_24dp)
//                .into(albumArt);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
