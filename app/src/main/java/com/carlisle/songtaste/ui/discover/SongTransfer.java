package com.carlisle.songtaste.ui.discover;

import android.content.Context;
import android.content.Intent;

import com.carlisle.songtaste.modle.SongDetailInfo;
import com.carlisle.songtaste.provider.ApiFactory;
import com.carlisle.songtaste.provider.converter.XmlConverter;
import com.carlisle.songtaste.services.MusicService;

import rx.Observer;

/**
 * Created by carlisle on 3/21/15.
 */
public class SongTransfer {

    private static SongTransfer songTransfer;
    public static SongTransfer getInstance() {
        if (songTransfer == null) {
            songTransfer = new SongTransfer();
        }
        return songTransfer;
    }

    public void getSongAndStartMusicService(final Context context, String songId) {
        new ApiFactory().getSongtasteApi(new XmlConverter(XmlConverter.ConvterType.SONG))
                .songUrl(songId, "")
                .subscribe(new Observer<SongDetailInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SongDetailInfo songDetailInfo) {
                        startMusicService(context, songDetailInfo);
                    }
                });
    }

    public void startMusicService(Context context, SongDetailInfo songDetailInfo) {
        Intent i = new Intent(context, MusicService.class);
        i.putExtra(MusicService.LAUNCH_NOW_PLAYING_ACTION, songDetailInfo);
        context.startService(i);
    }

}
