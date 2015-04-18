package com.carlisle.songtaste.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
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

import com.avos.avoscloud.AVAnalytics;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.cmpts.events.DownloadCompleteEvent;
import com.carlisle.songtaste.cmpts.events.DownloadFailedEvent;
import com.carlisle.songtaste.cmpts.events.FavoriteEvent;
import com.carlisle.songtaste.cmpts.events.PlayerReceivingEvent;
import com.carlisle.songtaste.cmpts.events.PlayerSendingEvent;
import com.carlisle.songtaste.cmpts.events.ProgressEvent;
import com.carlisle.songtaste.cmpts.modle.Result;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.provider.ApiFactory;
import com.carlisle.songtaste.cmpts.provider.converter.XmlConverter;
import com.carlisle.songtaste.cmpts.services.MusicService;
import com.carlisle.songtaste.fm.DataAccessor;
import com.carlisle.songtaste.utils.LocalSongHelper;
import com.carlisle.songtaste.utils.PreferencesHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

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
public class NowPlayingFragment extends BaseFragment implements DataAccessor.DataAccessorHandler {
    private static final String TAG = NowPlayingFragment.class.getSimpleName();

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

        if (DataAccessor.SINGLE_INSTANCE.getmDataHandler() != this) {
            DataAccessor.SINGLE_INSTANCE.setmDataHandler(this);
        }

        Intent intent = new Intent(getActivity().getApplicationContext(), MusicService.class);
        getActivity().startService(intent);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mNetworkStateReceiver, filter);

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

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd(TAG);
    }

    @OnClick({R.id.im_prev, R.id.cb_play_pause, R.id.cb_bottom_play_pause, R.id.im_next, R.id.im_bottom_next})
    public void onControllerClick(View view) {
        switch (view.getId()) {
            case R.id.im_prev:
                playPrevTune();
                break;
            case R.id.cb_bottom_play_pause:
            case R.id.cb_play_pause:
                if (playButtonIsPlayingState()) {
                    setPlayButtonPlaying(false);
                    pausePlay();
                } else {
                    setPlayButtonPlaying(true);
                    play();
                }
                break;
            case R.id.im_bottom_next:
            case R.id.im_next:
                playNextTune();
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

    public void onEvent(FavoriteEvent event) {
        if (!isFavoriteRequest) {
            onFavoriteClick();
        } else {
            isFavoriteRequest = false;
        }
    }

    public void onEvent(PlayerSendingEvent playerSendingEvent) {
        switch (playerSendingEvent.serviceCanSend) {
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_CURRENT_POSITION:
                long currentPosition = playerSendingEvent.currentPosition;
                long length = playerSendingEvent.length;
                setCurrentPosition((int) currentPosition, (int) length);
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_PREPARE:
                Log.d("yuedu", "media player will prepare!!!!");
                playOrPause.setChecked(false);
                bottomPlayOrPause.setChecked(false);
                updateCover();
                updateListViewSelection();
                showLoading();
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PREPARED:
                Log.d("yuedu", "media player prepared!!!!");
                hideLoading();
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_PLAY:
                Log.d("yuedu", "media player will play!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PLAYING:
                playOrPause.setChecked(false);
                bottomPlayOrPause.setChecked(false);
                Log.d("yuedu", "media player is playing!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_PAUSE:
                Log.d("yuedu", "media player will pause!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PAUSED:
                playOrPause.setChecked(true);
                bottomPlayOrPause.setChecked(true);
                Log.d("yuedu", "media player is paused!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_STOP:
                Log.d("yuedu", "media player will stop!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_STOPPED:
                playOrPause.setChecked(true);
                bottomPlayOrPause.setChecked(true);
                Log.d("yuedu", "media player is stopped!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_ERROR_OCCURRED:
                playOrPause.setChecked(true);
                bottomPlayOrPause.setChecked(true);
                hideLoading();
                Toast.makeText(getActivity().getApplicationContext(), playerSendingEvent.errorKey, Toast.LENGTH_LONG).show();
                Log.d("yuedu", "media player error occurred!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_COMPLETE:
                Log.d("yuedu", "media player complete!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_STATE_REPORT:
                boolean isPlaying = playerSendingEvent.playStateKey;
                Log.d("yuedu", "media player state report " + isPlaying + " !!!!");
                setPlayButtonPlaying(isPlaying);
                break;
        }
    }

    public void onEvent(DownloadCompleteEvent downloadCompleteEvent) {
        Log.d("yuedu", "data list download complete!!!!");
        updateUI();
    }

    public void onEvent(DownloadFailedEvent downloadFailedEvent) {
        Log.d("yuedu", "data list download failed!!!!");
    }

    private void showLoading() {
        Log.d("yuedu", "set progress bar indeterminate!!!!");
//        mProgressBar.setIndeterminate(true);
        //do not show progressbar progress animation 2013/09/23
    }

    private void hideLoading() {
        Log.d("yuedu", "set progress bar determinate!!!!");
//        mProgressBar.setIndeterminate(false);
        //do not show progressbar progress animation 2013/09/23
    }

    private void setCurrentPosition(final int currentPosition, final int length) {
        Log.d("88888",currentPosition + "//" + length);
        seekBar.setProgress(currentPosition);
        seekBar.setMax(length);
        bottomSeekBar.setProgress(currentPosition);
        bottomSeekBar.setMax(length);
    }

    private int getCurrentPlayingTuneDuration() {
        SongDetailInfo tune = DataAccessor.SINGLE_INSTANCE.getPlayingSong();
//        int min = tune.min;
//        int sec = tune.sec;
//        return (min * 60 + sec) * 1000;
        return 1;
    }

    private void updateCover() {
        songDetailInfo = DataAccessor.SINGLE_INSTANCE.getPlayingSong();
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

    private void updateListViewSelection() {
//        int playingIndex = DataAccessor.SINGLE_INSTANCE.getPlayingTuneIndex();
//        mListView.setSelection(playingIndex);
//        mListView.setItemChecked(playingIndex, true);
    }

    private void updateUI() {
        updateCover();
    }

    @Override
    public void onSuccess(JSONObject jsonObject) {
//        hideGreetingView();
    }

    @Override
    public void onFailure(Throwable throwable, JSONObject jsonObject) {
        Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
    }

    private boolean playButtonIsPlayingState() {
        return playOrPause.isSelected();
    }

    private void setPlayButtonPlaying(boolean isPlaying) {
        playOrPause.setSelected(isPlaying);
    }

    private void play() {
        EventBus.getDefault().post(new PlayerReceivingEvent(PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_PLAY));
        setPlayButtonPlaying(true);
    }

    private void pausePlay() {
        EventBus.getDefault().post(new PlayerReceivingEvent(PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_PAUSE));
    }

    private void playNextTune() {
        DataAccessor.SINGLE_INSTANCE.playNextSong();
        play();
    }

    private void playPrevTune() {
        DataAccessor.SINGLE_INSTANCE.playPrevSong();
        play();
    }

    private void playTuneAtIndex(int index) {
        DataAccessor.SINGLE_INSTANCE.playSongAtIndex(index);
        play();
    }

    public class RemoteControlReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        play();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        pausePlay();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        playNextTune();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            Log.w("yuedu", "Network Type Changed " + info);
            if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI && info.getState() == NetworkInfo.State.CONNECTED) {
                Log.d("yuedu", "wifi is connected");
            } else {
                Log.w("yuedu", "wifi is disconnected");
                Log.d("yuedu", "pause playing");
                pausePlay();
            }
        }
    };

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
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
