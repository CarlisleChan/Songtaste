package com.carlisle.songtaste.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.cmpts.events.PauseEvent;
import com.carlisle.songtaste.cmpts.events.PlayEvent;
import com.carlisle.songtaste.cmpts.events.ProgressEvent;
import com.carlisle.songtaste.cmpts.events.SkipToNextEvent;
import com.carlisle.songtaste.cmpts.events.UpdateUIEvent;
import com.carlisle.songtaste.cmpts.modle.Result;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.provider.ApiFactory;
import com.carlisle.songtaste.cmpts.provider.converter.XmlConverter;
import com.carlisle.songtaste.cmpts.services.Playback;
import com.carlisle.songtaste.utils.LocalSongHelper;
import com.carlisle.songtaste.utils.PreferencesHelper;
import com.squareup.picasso.Picasso;

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
public class BottomControlsFragment extends BaseFragment {
    private static final String TAG = BottomControlsFragment.class.getSimpleName();

    @InjectView(R.id.im_album_art)
    ImageButton albumArt;
    @InjectView(R.id.tv_song_name)
    TextView songName;
    @InjectView(R.id.tv_singer_name)
    TextView singerName;
    @InjectView(R.id.im_next)
    ImageButton next;
    @InjectView(R.id.im_favorite)
    ImageButton favorite;
    @InjectView(R.id.container)
    RelativeLayout container;
    @InjectView(R.id.cb_play_pause)
    CheckBox playOrPause;
    @InjectView(R.id.seekbar)
    SeekBar seekBar;

    SongDetailInfo songDetailInfo;
    Subscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_control, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @OnClick(R.id.im_next)
    public void onNextClick() {
        EventBus.getDefault().post(new SkipToNextEvent());
    }

    @OnClick(R.id.im_favorite)
    public void onFavoriteClick(View view) {

        subscription = AndroidObservable.bindFragment(this, new ApiFactory().getSongtasteApi(new XmlConverter(XmlConverter.ConvterType.COLLECTION))
                .collection(PreferencesHelper.getInstance(getActivity()).getUID(), songDetailInfo.getMediaId(), "xml"))
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Result result) {
                        Log.d("result====>", JSON.toJSONString(result));
                        Log.d("songDetailInfo====>", JSON.toJSONString(songDetailInfo));

                        if (result.code == 1) {
                            songDetailInfo.setIscollection("1");
                            favorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_loved));
                            Toast.makeText(getActivity(), result.msg, Toast.LENGTH_SHORT).show();
                        } else if (result.code == 2) {
                            songDetailInfo.setIscollection("0");
                            favorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_love));
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
                if (event.songDetailInfo != null) {
                    songDetailInfo = event.songDetailInfo;
                    songName.setText(songDetailInfo.getSong_name());
                    singerName.setText(songDetailInfo.getSinger_name());
                    playOrPause.setChecked(false);
                    setAlbumArt(songDetailInfo);

                    if (songDetailInfo.getIscollection().equals("1")) {
                        favorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_loved));
                    } else {
                        favorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_love));
                    }

                    if (songDetailInfo.getSongType() == SongDetailInfo.SongType.LOCAL_SONG) {
                        favorite.setVisibility(View.INVISIBLE);
                    } else {
                        favorite.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }

    }

    public int position = 0;

    public void onEvent(ProgressEvent progressEvent) {
        if (progressEvent.trackTouch) return;
        if (progressEvent.currentPosition == position) {
            // loading, show progress
            Log.d("loading=====>", "=====");
        } else {
            position = progressEvent.currentPosition;
        }

        seekBar.setProgress(progressEvent.currentPosition);
        seekBar.setMax(progressEvent.maxPosition);
    }

    public void setAlbumArt(SongDetailInfo songDetailInfo) {
        if (songDetailInfo.songType == SongDetailInfo.SongType.LOCAL_SONG) {
            albumArt.setImageBitmap(LocalSongHelper.getArtwork(getActivity(), Long.parseLong(songDetailInfo.mediaId),
                    Long.parseLong(songDetailInfo.albumid), true));
        } else {
            Picasso.with(getActivity())
                    .load(songDetailInfo.getAlbumArt())
                    .placeholder(R.drawable.default_artist)
                    .into(albumArt);
        }
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
