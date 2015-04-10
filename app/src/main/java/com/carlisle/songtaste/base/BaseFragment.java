package com.carlisle.songtaste.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.provider.ApiFactory;
import com.carlisle.songtaste.cmpts.provider.converter.XmlConverter;
import com.carlisle.songtaste.utils.PreferencesHelper;

import rx.Observer;


/**
 * Created by chengxin on 2/13/15.
 */
public class BaseFragment extends Fragment {
    private boolean getQueueDone = true;
    private int currentIndex = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public String getName() {
        return this.getClass().getName() + this.hashCode();
    }

    public void setSongtasteQueue(String songId) {
        new ApiFactory().getSongtasteApi(new XmlConverter(XmlConverter.ConvterType.SONG))
                .songUrl(songId, PreferencesHelper.getInstance(getActivity()).getUID(), "")
                .subscribe(new Observer<SongDetailInfo>() {
                    @Override
                    public void onCompleted() {
                        onAnalysisCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onAnalysisError(e);
                    }

                    @Override
                    public void onNext(SongDetailInfo songDetailInfo) {
                        onAnalysisNext(songDetailInfo);
                    }
                });
    }

    public void onAnalysisCompleted() {

    }

    public void onAnalysisError(Throwable e) {

    }

    public void onAnalysisNext(SongDetailInfo songDetailInfo) {

    }

}