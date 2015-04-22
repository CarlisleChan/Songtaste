package com.carlisle.songtaste.ui.main;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.ui.view.wave.WaveformView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * Created by chengxin on 4/21/15.
 */
public class SoundRecognitionActivity extends BaseActivity implements SwipeBackActivityBase {
    private static final int MSG_GET_VOLUME = 0x1001;

    @InjectView(R.id.waveform_view)
    WaveformView waveformView;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private Handler handler;
    private RecordThread recordThread;
    private SwipeBackActivityHelper mHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_recognition);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("歌曲识别");

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == MSG_GET_VOLUME) {
                    update((Float) msg.obj);
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordThread = new RecordThread(handler);
        recordThread.start();
    }

    @Override
    protected void onPause() {
        if (recordThread != null) {
            recordThread.pause();
            recordThread = null;
        }
        super.onPause();
    }

    private void update(final float volume) {
        waveformView.post(new Runnable() {
            @Override
            public void run() {
                waveformView.updateAmplitude(volume * 0.1f / 2000);
            }
        });
    }

    static class RecordThread extends Thread {
        private AudioRecord audioRecord;
        private int bufferSizeInBytes;
        private final int SAMPLE_RATE_IN_HZ = 8000;
        private boolean isRunning = false;
        private Handler handler;

        public RecordThread(Handler handler) {
            super();
            bufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE_IN_HZ,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes);
            this.handler = handler;
        }

        public void run() {
            super.run();
            audioRecord.startRecording();
            byte[] buffer = new byte[bufferSizeInBytes];
            isRunning = true;
            while (running()) {
                int r = audioRecord.read(buffer, 0, bufferSizeInBytes);
                int v = 0;
                for (byte aBuffer : buffer) {
                    v += aBuffer * aBuffer;
                }

                Message msg = handler.obtainMessage(MSG_GET_VOLUME, v * 1f / r);
                handler.sendMessage(msg);
            }
            audioRecord.stop();
        }

        public synchronized void pause() {
            isRunning = false;
        }

        private synchronized boolean running() {
            return isRunning;
        }

        public void start() {
            if (!running()) {
                super.start();
            }
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v != null)
            return v;
        return mHelper.findViewById(id);
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        getSwipeBackLayout().scrollToFinishActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

}
