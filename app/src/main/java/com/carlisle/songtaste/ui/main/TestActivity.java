package com.carlisle.songtaste.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.cmpts.SoundRecognition.DoresoRecord;
import com.carlisle.songtaste.cmpts.SoundRecognition.DoresoRecordListener;
import com.doreso.sdk.DoresoConfig;
import com.doreso.sdk.DoresoListener;
import com.doreso.sdk.DoresoManager;
import com.doreso.sdk.utils.DoresoMusicTrack;
import com.doreso.sdk.utils.DoresoUtils;
import com.doreso.sdk.utils.Logger;

import java.io.InputStream;

/**
 * doresosdk demo
 *
 * @author jzx
 */
public class TestActivity extends Activity implements DoresoRecordListener,
        DoresoListener {

    private final String TAG = "MainActivity";

    private TextView mVolume;
    private TextView mResult, tv_time;
    private DoresoManager mDoresoManager;
    private DoresoRecord mDoresoRecord;
    private boolean mProcessing;

    final private static String APPKEY = "UHQEQKeZmMqcjVFIm49pEqIBGU03wzfnZybidhnIDe8";
    final private static String APPSECRET = "55a519d61a06a1c56bec750cf377fef3";

    private DoresoConfig mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mConfig = new DoresoConfig();
        mConfig.appkey = APPKEY;
        mConfig.appSecret = APPSECRET;
        mConfig.listener = this;
        mConfig.context = this;
        mDoresoManager = new DoresoManager(mConfig);
        mDoresoRecord = new DoresoRecord(this, 16 * 1000);

        mVolume = (TextView) findViewById(R.id.volume);
        mResult = (TextView) findViewById(R.id.result);
        tv_time = (TextView) findViewById(R.id.time);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                start();
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stop();
            }
        });

        findViewById(R.id.cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        cancel();
                    }
                });

        findViewById(R.id.recognize_mp3).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new Mp3Recognizer().start();
                    }
                });
        findViewById(R.id.recognize_pcm).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new PCMRecognizer().start();
                    }
                });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mDoresoRecord.reqCancel();
        mDoresoManager.cancel();
    }

    public void start() {
        tv_time.setText("");
        if (!mProcessing) {
            mProcessing = true;
            mResult.setText(getResources().getString(R.string.recording));
            if (mDoresoRecord!=null) {
                mDoresoRecord.reqCancel();
                mDoresoRecord = null;
            }
            mDoresoRecord = new DoresoRecord(this, 15*1024);
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
        mVolume.setText(getResources().getString(R.string.volume) + volume);
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
     *
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
        byte[] buffer = new byte[] {};
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

        mResult.setText(tracks.length + "\n"
                + getResources().getString(R.string.artist)
                + tracks[0].getArtist()
                + getResources().getString(R.string.title)
                + tracks[0].getName() + "\n" + result);
        mProcessing = false;
    }

    @Override
    public void onRecognizeFail(int errorcode, String msg) {
        mDoresoManager.cancel();

        mResult.setText(errorcode + ":" + msg);
        mProcessing = false;
    }

    @Override
    public void onRecognizeEnd() {
        Log.e(TAG, "onRecognizeEnd");
        mProcessing = false;
        mDoresoRecord.reqCancel();
    }

    @Override
    public void onRecordError(int errorcode, String msg) {
        Log.e(TAG, "onRecordError:" + msg + "//" + errorcode);
    }
}
