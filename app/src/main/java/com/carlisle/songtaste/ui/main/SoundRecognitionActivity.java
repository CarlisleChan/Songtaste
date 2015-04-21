package com.carlisle.songtaste.ui.main;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.ui.view.wave.WaveformView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 4/21/15.
 */
public class SoundRecognitionActivity extends BaseActivity {
    private static final int MSG_GET_VOLUME = 0x1001;

    @InjectView(R.id.waveform_view)
    WaveformView waveformView;

    private Handler handler;
    private RecordThread recordThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_recognition);
        ButterKnife.inject(this);

        waveformView = (WaveformView) findViewById(R.id.waveform_view);
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
}
