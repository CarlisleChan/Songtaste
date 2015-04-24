package com.carlisle.songtaste.ui.main;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.LinearLayout;
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
import com.carlisle.songtaste.cmpts.modle.Result;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.oldEvents.PauseEvent;
import com.carlisle.songtaste.cmpts.oldEvents.PlayEvent;
import com.carlisle.songtaste.cmpts.oldEvents.SkipToNextEvent;
import com.carlisle.songtaste.cmpts.oldEvents.SkipToPrevEvent;
import com.carlisle.songtaste.cmpts.oldEvents.UpdatePlaybackEvent;
import com.carlisle.songtaste.cmpts.oldServices.Playback;
import com.carlisle.songtaste.cmpts.provider.ApiFactory;
import com.carlisle.songtaste.cmpts.provider.converter.XmlConverter;
import com.carlisle.songtaste.cmpts.services.DataAccessor;
import com.carlisle.songtaste.cmpts.services.MusicService;
import com.carlisle.songtaste.utils.LocalSongHelper;
import com.carlisle.songtaste.utils.PreferencesHelper;
import com.carlisle.songtaste.utils.QueueHelper;
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
    @InjectView(R.id.background)
    View background;
    @InjectView(R.id.controllers)
    LinearLayout controllers;
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
    private long downloadReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        if (DataAccessor.SINGLE_INSTANCE.getmDataHandler() != this) {
            DataAccessor.SINGLE_INSTANCE.setmDataHandler(this);
        }

        Intent intent = new Intent(getActivity().getApplicationContext(), MusicService.class);
        getActivity().startService(intent);

        getActivity().registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_player, container, false);
        ButterKnife.inject(this, view);
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
                if (((CheckBox) view).isChecked()) {
                    pausePlay();
                    EventBus.getDefault().post(new PauseEvent());
                } else {
                    play();
                    EventBus.getDefault().post(new PlayEvent());
                }
                break;
            case R.id.im_bottom_next:
            case R.id.im_next:
                playNextTune();
                break;
        }
    }

    @OnClick(R.id.im_download)
    public void onDownloadClick() {
        DownloadManager dm = (DownloadManager) getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
        DownloadManager.Request down=new DownloadManager.Request (Uri.parse(DataAccessor.SINGLE_INSTANCE.getPlayingSong().getUrl()));
        down.setVisibleInDownloadsUi(true);
        down.setDestinationInExternalFilesDir(getActivity(), Environment.DIRECTORY_MUSIC, DataAccessor.SINGLE_INSTANCE.getPlayingSong().getSong_name());
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadReference = dm.enqueue(down);
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if (downloadReference == reference) {
                    QueueHelper.getInstance().getOfflineQueue().add(DataAccessor.SINGLE_INSTANCE.getPlayingSong());
                }
            }
        }
    };

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
                Log.d("songtaste", "media player will prepare!!!!");
                setPlayButtonPlaying(true);
                updateCover();
                updateListViewSelection();
                showLoading();
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PREPARED:
                Log.d("songtaste", "media player prepared!!!!");
                hideLoading();
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_PLAY:
                Log.d("songtaste", "media player will play!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PLAYING:
                setPlayButtonPlaying(true);
                Log.d("songtaste", "media player is playing!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_PAUSE:
                Log.d("songtaste", "media player will pause!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_PAUSED:
                setPlayButtonPlaying(false);
                Log.d("songtaste", "media player is paused!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_WILL_STOP:
                Log.d("songtaste", "media player will stop!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_STOPPED:
                setPlayButtonPlaying(false);
                Log.d("songtaste", "media player is stopped!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_ERROR_OCCURRED:
                setPlayButtonPlaying(false);
                hideLoading();
                Toast.makeText(getActivity().getApplicationContext(), playerSendingEvent.errorKey, Toast.LENGTH_LONG).show();
                Log.d("songtaste", "media player error occurred!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_COMPLETE:
                Log.d("songtaste", "media player complete!!!!");
                break;
            case PlayerSendingEvent.PLAYER_SENDING_BROADCAST_CATEGORY_PLAYER_STATE_REPORT:
                boolean isPlaying = playerSendingEvent.playStateKey;
                Log.d("songtaste", "media player state report " + isPlaying + " !!!!");
                setPlayButtonPlaying(isPlaying);
                break;
        }
    }

    public void onEvent(DownloadCompleteEvent downloadCompleteEvent) {
        Log.d("songtaste", "data list download complete!!!!");
    }

    public void onEvent(DownloadFailedEvent downloadFailedEvent) {
        Log.d("songtaste", "data list download failed!!!!");
    }

    private void showLoading() {
        Log.d("songtaste", "set progress bar indeterminate!!!!");
        seekBar.setIndeterminate(true);
    }

    private void hideLoading() {
        Log.d("songtaste", "set progress bar determinate!!!!");
        seekBar.setIndeterminate(false);
    }

    private void setCurrentPosition(final int currentPosition, final int length) {
        seekBar.setProgress(currentPosition);
        seekBar.setMax(length);
        bottomSeekBar.setProgress(currentPosition);
        bottomSeekBar.setMax(length);
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

    private void updateCover() {
        songDetailInfo = DataAccessor.SINGLE_INSTANCE.getPlayingSong();
        songName.setText(songDetailInfo.getSong_name());
        singerName.setText(songDetailInfo.getSinger_name());
        bottomSongName.setText(songDetailInfo.getSong_name());
        bottomSingerName.setText(songDetailInfo.getSinger_name());
        setAlbumArt(songDetailInfo);

        if (songDetailInfo.getSongType() == SongDetailInfo.SongType.LOCAL_SONG) {
            favorite.setVisibility(View.GONE);
            bottomFavorite.setVisibility(View.GONE);
        } else {
            if (songDetailInfo.getIscollection().equals("1")) {
                favorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_loved));
                bottomFavorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_loved));
            } else {
                favorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_love_white));
                bottomFavorite.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_btn_love));
            }
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

    @Override
    public void onSuccess(JSONObject jsonObject) {
//        hideGreetingView();
    }

    @Override
    public void onFailure(Throwable throwable, JSONObject jsonObject) {
        Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
    }

    private void setPlayButtonPlaying(boolean isPlaying) {
        playOrPause.setChecked(!isPlaying);
        bottomPlayOrPause.setChecked(!isPlaying);
    }

    private void play() {
        EventBus.getDefault().post(new PlayerReceivingEvent(PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_PLAY));
    }

    private void pausePlay() {
        EventBus.getDefault().post(new PlayerReceivingEvent(PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_PAUSE));
    }

    private void playNextTune() {
        DataAccessor.SINGLE_INSTANCE.playNextSong();
        play();

        EventBus.getDefault().post(new SkipToNextEvent());
    }

    private void playPrevTune() {
        DataAccessor.SINGLE_INSTANCE.playPrevSong();
        play();

        EventBus.getDefault().post(new SkipToPrevEvent());
    }

    private void playTuneAtIndex(int index) {
        DataAccessor.SINGLE_INSTANCE.playSongAtIndex(index);
        play();
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
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(downloadReceiver);
    }
}
