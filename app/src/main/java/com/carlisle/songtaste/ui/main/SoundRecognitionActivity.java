package com.carlisle.songtaste.ui.main;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.cmpts.SoundRecognition.DoresoRecord;
import com.carlisle.songtaste.cmpts.SoundRecognition.DoresoRecordListener;
import com.carlisle.songtaste.ui.view.wave.WaveformView;
import com.doreso.sdk.DoresoConfig;
import com.doreso.sdk.DoresoListener;
import com.doreso.sdk.DoresoManager;
import com.doreso.sdk.utils.DoresoMusicTrack;
import com.doreso.sdk.utils.DoresoUtils;
import com.doreso.sdk.utils.Logger;

import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * Created by chengxin on 4/21/15.
 */
public class SoundRecognitionActivity extends BaseActivity implements SwipeBackActivityBase, DoresoRecordListener, DoresoListener {
    private static final String TAG = SoundRecognitionActivity.class.getSimpleName();
    private static final int MSG_GET_VOLUME = 0x1001;
    private static final String APPKEY = "UHQEQKeZmMqcjVFIm49pEqIBGU03wzfnZybidhnIDe8";
    private static final String APPSECRET = "55a519d61a06a1c56bec750cf377fef3";

    @InjectView(R.id.waveform_view)
    WaveformView waveformView;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tv_result)
    TextView result;
    @InjectView(R.id.tv_singer_name)
    TextView singerName;
    @InjectView(R.id.tv_song_name)
    TextView songName;
    @InjectView(R.id.btn_start)
    Button startButton;

    private Handler handler;
    private RecordThread recordThread;
    private SwipeBackActivityHelper mHelper;

    private DoresoManager doresoManager;
    private DoresoRecord doresoRecord;
    private boolean processing;


    private DoresoConfig doresoConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_recognition);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("歌曲识别");

        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == MSG_GET_VOLUME) {
                    updateWaveView((Float) msg.obj);
                }
                return true;
            }
        });

        initDoresoRecord();

    }

    private void initDoresoRecord() {
        doresoConfig = new DoresoConfig();
        doresoConfig.appkey = APPKEY;
        doresoConfig.appSecret = APPSECRET;
        doresoConfig.listener = this;
        doresoConfig.context = this;
        doresoManager = new DoresoManager(doresoConfig);
        doresoRecord = new DoresoRecord(this, 16 * 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRecord();
        startRecognize();
    }

    private void startRecord() {
        recordThread = new RecordThread(handler);
        recordThread.start();
        startButton.setText("暂停识别");
    }

    private void pauseRecord() {
        if (recordThread != null) {
            recordThread.pause();
            recordThread = null;
        }
    }

    @Override
    protected void onPause() {
        pauseRecord();
        cancelRecognize();
        startButton.setText("开始识别");
        super.onPause();
        doresoRecord.reqCancel();
        doresoManager.cancel();
    }

    @OnClick(R.id.btn_start)
    public void onStartClick() {
        if (recordThread.isRunning) {
            pauseRecord();
            cancelRecognize();
        } else {
            startRecord();
            startRecognize();
        }
    }

    private void updateWaveView(final float volume) {
        waveformView.post(new Runnable() {
            @Override
            public void run() {
                waveformView.updateAmplitude(volume * 0.1f / 2000);
            }
        });
    }


    public void startRecognize() {
        if (!processing) {
            processing = true;
            result.setText(getResources().getString(R.string.recording));
            if (doresoRecord != null) {
                doresoRecord.reqCancel();
                doresoRecord = null;
            }
            doresoRecord = new DoresoRecord(this, 15 * 1024);
            doresoRecord.start();
            if (!doresoManager.startRecognize()) {
                Toast.makeText(this, "无网络,无法识别", Toast.LENGTH_SHORT).show();
                pauseRecord();
            }
        }
    }

    protected void stopRecognize() {
        if (processing) {
            doresoRecord.reqStop();
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.clickrecord),
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void cancelRecognize() {
        if (processing) {
            doresoManager.cancel();
            processing = false;
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.clickrecord),
                    Toast.LENGTH_SHORT).show();
        }
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


    /**
     * 识别本地MP3 demo
     *
     * @author jzx
     */
    class Mp3Recognizer extends Thread {

        @Override
        public void run() {
            super.run();
            String mp3path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath().toString()
                    + "/test.mp3";
            doresoManager.recognize_mp3(mp3path);
        }
    }

    /**
     * 识别本地PCM demo线程
     *
     * @author jzx
     */
    class PCMRecognizer extends Thread {

        @Override
        public void run() {
            super.run();
            byte[] buffer = getFromAssets("paomo.pcm");
            byte[] res = DoresoUtils.resample(buffer, buffer.length, 44100, 2,
                    true);
            // 提取指纹
            byte[] data = DoresoUtils.genNiceMatrix(res, res.length);
            if (data == null) {
                Logger.e(TAG, "data is null");
            } else {
                Logger.e(TAG, "data is not null:" + data.length);
            }
            doresoManager.recognize_pcm(data, data.length, true);
        }

    }

    private byte[] getFromAssets(String fileName) {
        byte[] buffer = new byte[]{};
        try {
            InputStream iStream = getResources().getAssets().open(fileName);
            int length = iStream.available();
            buffer = new byte[length];
            iStream.read(buffer, 0, length);
            iStream.close();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    @Override
    public void onRecognizeSuccess(DoresoMusicTrack[] tracks, String result) {
        // TODO Auto-generated method stub
        doresoManager.stopRecognize();

        this.result.setText(tracks.length + "\n"
                + getResources().getString(R.string.artist)
                + tracks[0].getArtist()
                + getResources().getString(R.string.title)
                + tracks[0].getName() + "\n" + result);
        processing = false;
        pauseRecord();
    }

    @Override
    public void onRecognizeFail(int errorcode, String msg) {
        doresoManager.cancel();

        result.setText(errorcode + ":" + msg);
        processing = false;
        pauseRecord();
    }

    @Override
    public void onRecognizeEnd() {
        Log.e(TAG, "onRecognizeEnd");
        processing = false;
        doresoRecord.reqCancel();
        pauseRecord();
    }

    @Override
    public void onRecording(byte[] buffer) {

    }

    @Override
    public void onRecordError(int errorcode, String msg) {
        Log.e(TAG, "onRecordError:" + msg + "//" + errorcode);
    }

    @Override
    public void onRecordEnd() {
        pauseRecord();
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
