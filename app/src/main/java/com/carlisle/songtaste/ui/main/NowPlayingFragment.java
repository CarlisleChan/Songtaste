package com.carlisle.songtaste.ui.main;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.cmpts.events.FavoriteEvent;
import com.carlisle.songtaste.cmpts.events.PauseEvent;
import com.carlisle.songtaste.cmpts.events.PlayEvent;
import com.carlisle.songtaste.cmpts.events.ProgressEvent;
import com.carlisle.songtaste.cmpts.events.SkipToNextEvent;
import com.carlisle.songtaste.cmpts.events.SkipToPrevEvent;
import com.carlisle.songtaste.cmpts.events.UpdatePlaybackEvent;
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
 * Created by carlisle on 4/3/15.
 */
public class NowPlayingFragment extends BaseFragment {

    @InjectView(R.id.im_bottom_album_art)
    ImageButton bottomAlbumArt;
    @InjectView(R.id.tv_bottom_song_name)
    TextView bottomSongName;
    @InjectView(R.id.tv_bottom_singer_name)
    TextView bottomSingerName;
    @InjectView(R.id.im_bottom_favorite)
    ImageButton bottomFavorite;
    @InjectView(R.id.bottom_container)
    RelativeLayout bottomContainer;
    @InjectView(R.id.cb_bottom_play_pause)
    CheckBox bottomPlayOrPause;
    @InjectView(R.id.bottom_seekbar)
    SeekBar bottomSeekBar;

    @InjectView(R.id.toolbar_container)
    View toolbarContainer;
    @InjectView(R.id.tv_song_name)
    TextView songName;
    @InjectView(R.id.tv_singer_name)
    TextView singerName;
    @InjectView(R.id.im_share)
    ImageView share;
    @InjectView(R.id.im_download)
    ImageView download;
    @InjectView(R.id.im_favorite)
    ImageButton favorite;
    @InjectView(R.id.seekbar)
    SeekBar seekBar;
    @InjectView(R.id.im_prev)
    ImageView prevButton;
    @InjectView(R.id.cb_play_pause)
    CheckBox playOrPause;
    @InjectView(R.id.im_next)
    ImageView nextButton;

    private SongDetailInfo songDetailInfo;
    private Subscription subscription;
    private boolean isFavoriteRequest = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_player, container, false);
        ButterKnife.inject(this, view);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        return view;
    }

    @OnClick({R.id.im_prev, R.id.cb_play_pause, R.id.cb_bottom_play_pause, R.id.im_next, R.id.im_bottom_next})
    public void onControllerClick(View view) {
        switch (view.getId()) {
            case R.id.im_prev:
                EventBus.getDefault().post(new SkipToPrevEvent());
                break;
            case R.id.cb_bottom_play_pause:
            case R.id.cb_play_pause:
                if (!((CheckBox) view).isChecked()) {
                    EventBus.getDefault().post(new PlayEvent());
                } else {
                    EventBus.getDefault().post(new PauseEvent());
                }
                break;
            case R.id.im_bottom_next:
            case R.id.im_next:
                EventBus.getDefault().post(new SkipToNextEvent());
                break;
        }
    }

    @OnClick(R.id.im_share)
    public void onShareClick() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void onEvent(UpdatePlaybackEvent event) {
        switch (event.state) {
            case Playback.STATE_PAUSED:
                playOrPause.setChecked(true);
                bottomPlayOrPause.setChecked(true);
                break;
            case Playback.STATE_STOPPED:
                playOrPause.setChecked(false);
                bottomPlayOrPause.setChecked(false);
                break;
            default:
                if (event.songDetailInfo != null) {
                    songDetailInfo = event.songDetailInfo;
                    songName.setText(songDetailInfo.getSong_name());
                    singerName.setText(songDetailInfo.getSinger_name());
                    playOrPause.setChecked(false);
                    bottomSongName.setText(songDetailInfo.getSong_name());
                    bottomSingerName.setText(songDetailInfo.getSinger_name());
                    bottomPlayOrPause.setChecked(false);
                    setAlbumArt(songDetailInfo);

                    if (songDetailInfo.getIscollection().equals("1")) {
                        favorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_loved));
                        bottomFavorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_loved));
                    } else {
                        favorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_love_white));
                        bottomFavorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_love));
                    }

                    if (songDetailInfo.getSongType() == SongDetailInfo.SongType.LOCAL_SONG) {
                        favorite.setVisibility(View.GONE);
                        bottomFavorite.setVisibility(View.GONE);
                    } else {
                        favorite.setVisibility(View.VISIBLE);
                        bottomFavorite.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }
    }

    public void onEvent(FavoriteEvent event) {
        if (!isFavoriteRequest) {
            onFavoriteClick();
        } else {
            isFavoriteRequest = false;
        }
    }

    public void setAlbumArt(SongDetailInfo songDetailInfo) {
        if (songDetailInfo.songType == SongDetailInfo.SongType.LOCAL_SONG) {
            bottomAlbumArt.setImageBitmap(LocalSongHelper.getArtwork(getActivity(), Long.parseLong(songDetailInfo.mediaId),
                    Long.parseLong(songDetailInfo.albumid), true));
        } else {
            Picasso.with(getActivity())
                    .load(songDetailInfo.getAlbumArt())
                    .placeholder(R.drawable.default_artist)
                    .into(bottomAlbumArt);
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

        bottomSeekBar.setProgress(progressEvent.currentPosition);
        bottomSeekBar.setMax(progressEvent.maxPosition);

    }

    @OnClick({R.id.im_favorite, R.id.im_bottom_favorite})
    public void onFavoriteClick() {
        subscription = AndroidObservable.bindFragment(this, new ApiFactory().getSongtasteApi(new XmlConverter(XmlConverter.ConvterType.COLLECTION))
                .collection(PreferencesHelper.getInstance(getActivity()).getUID(), songDetailInfo.getMediaId(), "xml"))
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "网络出现问题, 请稍后重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Result result) {
                        if (result.code == 1) {
                            songDetailInfo.setIscollection("1");
                            favorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_loved));
                            bottomFavorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_loved));
                            Toast.makeText(getActivity(), result.msg, Toast.LENGTH_SHORT).show();
                        } else if (result.code == 2) {
                            songDetailInfo.setIscollection("0");
                            favorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_love_white));
                            bottomFavorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_love));
                            Toast.makeText(getActivity(), result.msg, Toast.LENGTH_SHORT).show();
                        }
                        isFavoriteRequest = true;
                        EventBus.getDefault().post(new FavoriteEvent(songDetailInfo.getIscollection().equals("1")));
                    }
                });
    }

    public void hideBottomControl(float v) {
        bottomContainer.setAlpha(1 - v);
        toolbarContainer.setAlpha(v);

        if (v == 1) {
            bottomContainer.setVisibility(View.GONE);
        } else {
            bottomContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_local, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(getActivity(), "index is" + " && menu text is " + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
