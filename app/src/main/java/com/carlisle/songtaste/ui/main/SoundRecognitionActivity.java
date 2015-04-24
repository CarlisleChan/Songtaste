package com.carlisle.songtaste.ui.main;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.cmpts.SoundRecognition.DoresoRecord;
import com.carlisle.songtaste.cmpts.SoundRecognition.DoresoRecordListener;
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

/**
 * Created by chengxin on 4/21/15.
 */
public class SoundRecognitionActivity extends BaseActivity implements DoresoRecordListener, DoresoListener {
    private final String TAG = "SoundRecognitionActivity";
    @InjectView(R.id.tv_result)
    TextView result;
    @InjectView(R.id.tv_song_name)
    TextView songName;
    @InjectView(R.id.tv_singer_name)
    TextView singerName;
    @InjectView(R.id.btn_start)
    Button start;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private DoresoManager mDoresoManager;
    private DoresoRecord mDoresoRecord;
    private boolean mProcessing = false;

    final private static String APPKEY = "UHQEQKeZmMqcjVFIm49pEqIBGU03wzfnZybidhnIDe8";
    final private static String APPSECRET = "55a519d61a06a1c56bec750cf377fef3";

    private DoresoConfig mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_recognition);
        ButterKnife.inject(this);
        mConfig = new DoresoConfig();
        mConfig.appkey = APPKEY;
        mConfig.appSecret = APPSECRET;
        mConfig.listener = this;
        mConfig.context = this;
        mDoresoManager = new DoresoManager(mConfig);
        mDoresoRecord = new DoresoRecord(this, 16 * 1000);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("歌曲识别");

    }

    @OnClick(R.id.btn_start)
    public void onStartClick(View button) {
        if (mProcessing) {
            stop();
            ((Button) button).setText("开始识别");
        } else {
            start();
            ((Button) button).setText("暂停识别");
            songName.setVisibility(View.INVISIBLE);
            singerName.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDoresoRecord.reqCancel();
        mDoresoManager.cancel();
    }

    @Override
    public MenuInflater getMenuInflater() {
        return super.getMenuInflater();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void start() {
        if (!mProcessing) {
            mProcessing = true;
            result.setText(getResources().getString(R.string.recording));
            if (mDoresoRecord != null) {
                mDoresoRecord.reqCancel();
                mDoresoRecord = null;
            }
            mDoresoRecord = new DoresoRecord(this, 15 * 1024);
            mDoresoRecord.start();
            if (!mDoresoManager.startRecognize()) {
                Toast.makeText(this, "无网络,无法识别", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void stop() {
        if (mProcessing) {
            mDoresoRecord.reqStop();
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.clickrecord),
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void cancel() {
        if (mProcessing) {
            mDoresoManager.cancel();
            mProcessing = false;
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.clickrecord),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecording(byte[] buffer) {
        double volume = DoresoUtils.computeDb(buffer, buffer.length);
        mDoresoManager.doRecognize(buffer);
    }

    @Override
    public void onRecordEnd() {
        mDoresoManager.stopRecognize();
        // mResult.setText(getResources().getString(R.string.recordend));
        mProcessing = false;
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
            mDoresoManager.recognize_mp3(mp3path);
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
            mDoresoManager.recognize_pcm(data, data.length, true);
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
        mDoresoManager.stopRecognize();
        songName.setVisibility(View.VISIBLE);
        singerName.setVisibility(View.VISIBLE);
        songName.setText(tracks[0].getName());
        singerName.setText(tracks[0].getArtist());

        mProcessing = false;
    }

    @Override
    public void onRecognizeFail(int errorcode, String msg) {
        mDoresoManager.cancel();

        result.setText(msg);
        mProcessing = false;
    }

    @Override
    public void onRecognizeEnd() {
        mProcessing = false;
        mDoresoRecord.reqCancel();
    }

    @Override
    public void onRecordError(int errorcode, String msg) {
    }

}
