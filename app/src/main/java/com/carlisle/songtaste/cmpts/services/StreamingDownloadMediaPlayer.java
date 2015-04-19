package com.carlisle.songtaste.cmpts.services;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.carlisle.songtaste.utils.DiskLruCache;
import com.carlisle.songtaste.utils.MD5Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;


/**
 * Created by dong on 13-6-17.
 */
public class StreamingDownloadMediaPlayer {

    public enum PlayerState {
        IDLE,
        INITIALIZED,
        PREPARING,
        PREPARED,
        STARTED,
        STOPPED,
        PAUSED,
        COMPLETED,
        ERROR;

        @Override
        public String toString() {
            return readableMap.get(this);
        }

        private static Map<PlayerState, String> readableMap;

        static {
            readableMap = new HashMap<PlayerState, String>(9);
            readableMap.put(IDLE, "IDLE");
            readableMap.put(INITIALIZED, "INITIALIZED");
            readableMap.put(PREPARING, "PREPARING");
            readableMap.put(PREPARED, "PREPARED");
            readableMap.put(STARTED, "STARTED");
            readableMap.put(STOPPED, "STOPPED");
            readableMap.put(PAUSED, "PAUSED");
            readableMap.put(COMPLETED, "COMPLETED");
            readableMap.put(ERROR, "ERROR");
        }
    }

    private String mURL;

    private PlayerState mState;
    private boolean mLooping;
    private File mCacheDir;
    private AudioTrack mAudioTrack;
    private DiskLruCache mDiskCache;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private StreamingAsyncTask mStreamingTask;
    private int mBufferSize = 2 * AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
    private OnPreparedListener mPreparedListener;
    private OnCompletionListener mCompletionListener;
    private OnErrorListener mErrorListener;

    private static final int DISK_FILE_CACHE_INDEX = 0;
    private static final int DISK_FILE_CACHE_VERSION = 1;

    private abstract class StreamingAsyncTask extends AsyncTask<URL, Void, Void> {
        boolean isPaused = false;
        boolean isStopped = false;
        boolean isPlaying = false;
        ReentrantLock pauseLock = new ReentrantLock();
        Condition unpaused = pauseLock.newCondition();
        float playedTimeInMS;
        float mLength;

        public void pause() {
            pauseLock.lock();
            try {
                isPaused = true;
                isPlaying = false;
            } finally {
                pauseLock.unlock();
            }
        }

        public void start() {
            if (isCancelled()) {
                throw new IllegalStateException("play task has been stopped and cancelled");
            }
            resume();
        }

        public void stop() {
            if (isPaused) {
                resume();
            }
            isStopped = true;
            isPlaying = false;
            cancel(true);
        }

        public void resume() {
            pauseLock.lock();
            isPaused = false;
            isPlaying = true;
            try {
                unpaused.signalAll();
            } finally {
                pauseLock.unlock();
            }
        }

        public float getPlayedTimeInMS() {
            return playedTimeInMS;
        }

        public float getLength() {
            return mLength;
        }
    }

    static public interface OnPreparedListener {
        abstract void onPrepared(StreamingDownloadMediaPlayer mediaPlayer);
    }

    static public interface OnCompletionListener {
        abstract void onCompletion(StreamingDownloadMediaPlayer mediaPlayer);
    }

    static public interface OnErrorListener {
        abstract void onError(StreamingDownloadMediaPlayer mediaPlayer, Throwable error);
    }

    public PlayerState getState() {
        return mState;
    }

    public boolean isLooping() {
        return mLooping;
    }

    public File getCacheDir() {
        if (mCacheDir != null && mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }
        return mCacheDir;
    }

    public void setOnPreparedListener(OnPreparedListener mPreparedListener) {
        this.mPreparedListener = mPreparedListener;
    }

    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        this.mCompletionListener = onCompletionListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.mErrorListener = onErrorListener;
    }

    public void setCacheDir(File dir) {
        if (dir == null || !dir.isDirectory()) {
            throw new IllegalArgumentException("input dir is null or not a directory");
        }
        mCacheDir = dir;
    }

    private DiskLruCache getDiskCache() {
        File cacheDir = getCacheDir();
        if (cacheDir != null && mDiskCache == null) {
            try {
                mDiskCache = DiskLruCache.open(cacheDir, DISK_FILE_CACHE_VERSION, 1, 200 * 1024 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mDiskCache;
    }

    public void setDataSource(String url) throws MalformedURLException {
        if (url == null) {
            throw new IllegalArgumentException("input stream is null");
        }
        if (mState != PlayerState.IDLE) {
            throw new IllegalStateException("cannot setDataSource in [" + mState + "] state");
        }
        this.mURL = url;
        this.mState = PlayerState.INITIALIZED;
    }

    public String getDataSource() {
        return mURL;
    }

    public void reset() {
        mURL = null;
        mState = PlayerState.IDLE;
        mLooping = false;
        if (mAudioTrack != null) {
            mAudioTrack.pause();
            mAudioTrack.flush();
            mAudioTrack.stop();
            mAudioTrack.release();
        }

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize, AudioTrack.MODE_STREAM);
        mAudioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack audioTrack) {
                if (mCompletionListener != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCompletionListener.onCompletion(StreamingDownloadMediaPlayer.this);
                        }
                    });
                }


            }

            @Override
            public void onPeriodicNotification(AudioTrack audioTrack) {

            }
        });
    }

    public void prepare() throws IOException, BitstreamException, DecoderException, InterruptedException {
        if (mState == PlayerState.INITIALIZED || mState == PlayerState.STOPPED || mState == PlayerState.PREPARING) {
            throw new UnsupportedOperationException("not supported");
        } else {
            throw new IllegalStateException("cannot prepare in [" + mState + "] state");
        }
    }

    final private static class StreamingPipe extends InputStream {

        private InputStream inputStream;
        private OutputStream outputStream;

        private StreamingPipe(InputStream inputStream, OutputStream outputStream) {
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        @Override
        public int available() throws IOException {
            return inputStream.available();
        }

        @Override
        public void close() throws IOException {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }

        @Override
        public void mark(int readlimit) {
            inputStream.mark(readlimit);
        }

        @Override
        public boolean markSupported() {
            return inputStream.markSupported();
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            int result = inputStream.read(buffer, offset, length);
            if (outputStream != null) {
                outputStream.write(buffer, offset, length);
            }
            return result;
        }

        @Override
        public synchronized void reset() throws IOException {
            inputStream.reset();
        }

        @Override
        public long skip(long byteCount) throws IOException {
            return inputStream.skip(byteCount);
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }

    private boolean isUrl(String url) {
        URL url1;
        try {
            url1 = new URL(url);
            InputStream in = url1.openStream();
            return true;
        } catch (Exception e1) {
            return false;
        }
    }

    protected void handleInput(final URL url, final Decoder decoder) throws IOException, BitstreamException, DecoderException, InterruptedException {
        mStreamingTask = new StreamingAsyncTask() {
            @Override
            protected Void doInBackground(URL... params) {
                URL url1 = params[0];
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                Bitstream bitstream = null;
                DiskLruCache diskCache = getDiskCache();
                String key = MD5Util.md5(url.getPath());
                DiskLruCache.Editor diskEditor = null;
                try {
                    DiskLruCache.Snapshot mp3Snapshot = diskCache.get(key);
                    if (mp3Snapshot != null) {
                        inputStream = mp3Snapshot.getInputStream(DISK_FILE_CACHE_INDEX);
                        diskCache.flush();
                    } else {
                        connection = (HttpURLConnection) url1.openConnection();
                        connection.connect();
                        diskEditor = diskCache.edit(key);
                        inputStream = new StreamingPipe(connection.getInputStream(), diskEditor.newOutputStream(DISK_FILE_CACHE_INDEX));
                    }
                    bitstream = new Bitstream(inputStream);
                    Header header;
                    boolean firstPrepared = false;
                    int totalBytes = 0;
                    int oneshootBytes = 0;
                    int totalFrameSize = 0;
                    while ((header = bitstream.readFrame()) != null && !isStopped) {
                        if (isPaused) {
                            pauseLock.lock();
                            try {
                                unpaused.await();
                            } finally {
                                pauseLock.unlock();
                            }
                        }

                        if (mAudioTrack.getPlaybackRate() != header.frequency()) {
                            mAudioTrack.setPlaybackRate(header.frequency());
                        }

                        if (totalBytes >= mBufferSize - 2 * oneshootBytes && !firstPrepared) {
                            firstPrepared = true;
                            notifyPrepared();
                        }

                        SampleBuffer decoderBuffer = (SampleBuffer) decoder.decodeFrame(header, bitstream);

                        oneshootBytes = decoderBuffer.getBufferLength() * 2;
                        short[] copyBuffer = new short[decoderBuffer.getBufferLength()];
                        System.arraycopy(decoderBuffer.getBuffer(), 0, copyBuffer, 0, decoderBuffer.getBufferLength());
                        mAudioTrack.write(copyBuffer, 0, copyBuffer.length);
                        totalBytes += oneshootBytes;
                        totalFrameSize += 1;
                        mLength = header.total_ms((int) connection.getContentLength());
                        playedTimeInMS += header.ms_per_frame();
                        bitstream.closeFrame();
                    }

                    if (!isStopped) {
                        mAudioTrack.setNotificationMarkerPosition(totalFrameSize);
                    }

                    if (diskEditor != null) {
                        if (isStopped) {
                            diskEditor.abort();
                        } else {
                            diskEditor.commit();
                        }
                        diskCache.flush();
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    if (diskEditor != null) {
                        try {
                            //任何情况下播放异常中断，都撤销磁盘存储
                            diskEditor.abort();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    mState = PlayerState.ERROR;
                    reset();
                    if (mErrorListener != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mErrorListener.onError(StreamingDownloadMediaPlayer.this, e);
                            }
                        });
                    }
                } finally {
                    if (inputStream != null) {
                        DiskLruCache.closeQuietly(inputStream);
                    }
                    if (bitstream != null) {
                        try {
                            bitstream.close();
                        } catch (BitstreamException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }

                    isStopped = true;
                    isPaused = false;
                    isPlaying = false;
                }
                return null;
            }
        };
        mState = PlayerState.PREPARING;
        mStreamingTask.execute(url);
    }

    private void notifyPrepared() {
        mState = PlayerState.PREPARED;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPreparedListener != null) {
                    mPreparedListener.onPrepared(StreamingDownloadMediaPlayer.this);
                }
            }
        });
    }

    public void prepareAsync() throws DecoderException, InterruptedException, BitstreamException, IOException {
        if (mState == PlayerState.INITIALIZED || mState == PlayerState.STOPPED) {
            if(isUrl(mURL)) {
                handleInput(new URL(mURL), new Decoder());
            } else {

            }
        } else {
            throw new IllegalStateException("cannot prepareAsync in [" + mState + "] state");
        }
    }

    public void start() {
        if (mState == PlayerState.PREPARED || mState == PlayerState.COMPLETED || mState == PlayerState.PAUSED) {
            mState = PlayerState.STARTED;
            mAudioTrack.play();
            mStreamingTask.start();
        } else {
            throw new IllegalStateException("cannot start in [" + mState + "] state");
        }
    }

    public void pause() {
        if (mState == PlayerState.STARTED) {
            mState = PlayerState.PAUSED;
            mAudioTrack.pause();
            mStreamingTask.pause();
        } else {
            throw new IllegalStateException("cannot pause in [" + mState + "] state");
        }
    }

    public void stop() {
        if (mState == PlayerState.PREPARED || mState == PlayerState.COMPLETED || mState == PlayerState.PAUSED || mState == PlayerState.STARTED || mState == PlayerState.PREPARING) {
            mState = PlayerState.STOPPED;
            mStreamingTask.stop();
            mAudioTrack.pause();
            mAudioTrack.flush();
            mAudioTrack.stop();
        } else {
            throw new IllegalStateException("cannot stop in [" + mState + "] state");
        }
    }

    public void seekTo(long millisecond) {
        //TODO
    }

    public long getCurrentPosition() {
        return Math.round(mStreamingTask.getPlayedTimeInMS());
    }

    public long getLength() {
        return Math.round(mStreamingTask.getLength());
    }

    public boolean isPlaying() {
        return mState == PlayerState.STARTED;
    }

    public boolean isPaused() {
        return mState == PlayerState.PAUSED;
    }

    public boolean isCompleted() {
        return mState == PlayerState.COMPLETED;
    }

    public boolean isPreparing() {
        return mState == PlayerState.PREPARING;
    }

    public boolean isPrepared() {
        return mState == PlayerState.PREPARED;
    }

    public void release() {
        if (mAudioTrack != null) {
            mAudioTrack.pause();
            mAudioTrack.flush();
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
        mCacheDir = null;
        mHandler = null;
        mURL = null;
        mPreparedListener = null;
        if (mStreamingTask != null) {
            if (!mStreamingTask.isCancelled()) {
                mStreamingTask.cancel(true);
            }
            mStreamingTask = null;
        }
        mState = PlayerState.IDLE;
    }

}
