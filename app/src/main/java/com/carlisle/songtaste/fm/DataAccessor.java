package com.carlisle.songtaste.fm;

import android.content.Context;

import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dong on 13-9-9.
 */
public enum DataAccessor {
    SINGLE_INSTANCE;

    private List<SongDetailInfo> mDataList;
    private int mPlayingTuneIndex;
    private WeakReference<DataAccessorHandler> mDataHandler;

    public DataAccessorHandler getmDataHandler() {
        return mDataHandler == null ? null : mDataHandler.get();
    }

    public void setmDataHandler(DataAccessorHandler mDataHandler) {
        this.mDataHandler = new WeakReference<DataAccessorHandler>(mDataHandler);
    }

    public List<SongDetailInfo> getDataList() {
        return mDataList;
    }

    public SongDetailInfo getPlayingTune() {
        return mDataList.get(mPlayingTuneIndex);
    }

    public int getPlayingTuneIndex() {
        return mPlayingTuneIndex;
    }

    public synchronized void playTuneAtIndex(int index) {
        if (index >= 0 && index < mDataList.size()) {
            mPlayingTuneIndex = index;
        } else {
            throw new IndexOutOfBoundsException("index " + index + " is out of data list bounds!");
        }
    }

    public synchronized SongDetailInfo playNextTune() {
        if (mDataList == null || mPlayingTuneIndex + 1 >= mDataList.size()) {
            return null;
        }
        mPlayingTuneIndex += 1;
        return getPlayingTune();
    }

    public int indexOfTune(CharSequence tuneName) {
        int index = -1;
        if (mDataList != null) {
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getSong_name().contains(tuneName)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public void shot(final Context context, List<SongDetailInfo> dataList) {
        mDataList = dataList;
    }

    private DataAccessor() {
        mDataList = new ArrayList<SongDetailInfo>(200);
    }

    public static interface DataAccessorHandler {
        public void onSuccess(final JSONObject jsonObject);

        public void onFailure(final Throwable throwable, final JSONObject jsonObject);
    }

}
