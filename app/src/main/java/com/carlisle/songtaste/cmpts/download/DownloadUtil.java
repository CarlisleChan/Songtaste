package com.carlisle.songtaste.cmpts.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 将下载方法封装在此类 提供下载，暂停，删除，以及重置的方法
 */
public class DownloadUtil {
    private static DownloadUtil instance;

    public static DownloadUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DownloadUtil();
        }
        instance.context = context;
        return instance;
    }

    private DownloadHttpTool mDownloadHttpTool;
    private OnDownloadListener onDownloadListener;
    private List<SongDetailInfo> dataList;
    private Context context;

    private int fileSize;
    private int downloadedSize = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int length = msg.arg1;
            synchronized (this) {// 加锁保证已下载的正确性
                downloadedSize += length;

            }
            if (onDownloadListener != null) {
                onDownloadListener.downloadProgress(downloadedSize);
            }
            if (downloadedSize >= fileSize) {
                mDownloadHttpTool.compelete();
                if (onDownloadListener != null) {
                    onDownloadListener.downloadEnd();
                    dataList.remove(0);
                    start();
                }
            }
        }

    };

    public DownloadUtil() {
        dataList = new ArrayList<SongDetailInfo>();
    }

    public DownloadUtil add(SongDetailInfo songDetailInfo) {
        dataList.add(songDetailInfo);
        return instance;
    }

    // 下载之前首先异步线程调用ready方法获得文件大小信息，之后调用开始方法
    public void start() {
        if (dataList.isEmpty()) {
            return;
        } else {
            mDownloadHttpTool = new DownloadHttpTool(2, dataList.get(0).getUrl(),
                    FileUtils.getSDPath() + "/Songtaste", dataList.get(0).getSong_name() + ".mp3", context, mHandler);
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... arg0) {
                mDownloadHttpTool.ready();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                fileSize = mDownloadHttpTool.getFileSize();
                downloadedSize = mDownloadHttpTool.getCompeleteSize();
                Log.d("Tag", "downloadedSize::" + downloadedSize);
                if (onDownloadListener != null) {
                    onDownloadListener.downloadStart(fileSize);
                }
                mDownloadHttpTool.start();
            }
        }.execute();
    }

    public void pause() {
        mDownloadHttpTool.pause();
    }

    public void delete() {
        mDownloadHttpTool.delete();
    }

    public void reset() {
        mDownloadHttpTool.delete();
        start();
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    // 下载回调接口
    public interface OnDownloadListener {
        public void downloadStart(int fileSize);

        public void downloadProgress(int downloadedSize);

        public void downloadEnd();
    }
}