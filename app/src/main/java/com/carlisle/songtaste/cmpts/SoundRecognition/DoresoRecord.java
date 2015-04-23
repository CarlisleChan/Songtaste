package com.carlisle.songtaste.cmpts.SoundRecognition;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;

import com.doreso.sdk.utils.DoresoErrorCode;
import com.doreso.sdk.utils.DoresoErrorMsg;

public class DoresoRecord extends Thread {

	private final String TAG = "DoresoRecord";

	protected final static int SAMPLE_RATE = 8000;
	protected final static int BUFFERLENGTH = 1280;

	protected boolean stop;
	protected boolean cancel;

	protected DoresoRecordListener mListener;

	protected long mMaxByte;
	protected long mByteCount;

	private AudioRecord mAudioRecord;
	private final int RECORD_RECORDING = 1001;
	private final int RECORD_END = 1002;
	private final int RECORD_ERROR = 1003;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (mListener == null) {
				return;
			}
			switch (msg.what) {
			case RECORD_END:
				mListener.onRecordEnd();
				break;
			case RECORD_ERROR:
				int errorCode = msg.arg1;
				String errorMsg = msg.obj.toString();
				mListener.onRecordError(errorCode, errorMsg);
				break;
			case RECORD_RECORDING:
				byte[] data = (byte[]) msg.obj;
				mListener.onRecording(data);
				break;
			default:
				break;
			}
		}
	};

	public DoresoRecord(DoresoRecordListener listener, long duration) {
		mListener = listener;
		mMaxByte = duration * 16;
	}

	public void reqStop() {
		this.stop = true;
	}

	public void reqCancel() {
		this.cancel = true;
	}

	@Override
	public void run() {
		if (!initAutoRecord()) {
			onError(DoresoErrorCode.RECORD_INIT_FAIL, DoresoErrorMsg.MSG_RECORD_INIT);
			return;
		}
		try {
			byte[] buffer = new byte[BUFFERLENGTH];
			mAudioRecord.startRecording();
			while (!stop && !cancel) {
				int len = mAudioRecord.read(buffer, 0, buffer.length);
				if (len > 0) {
					byte[] data = new byte[len];
					System.arraycopy(buffer, 0, data, 0, len);
					if (onRecording(data))
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			onError(DoresoErrorCode.UNKNOWN, e.getMessage());
		} finally {
			releaseAutoRecord();
		}
		onEnd();
	}

	protected boolean initAutoRecord() {
		int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mAudioRecord = new AudioRecord(
				MediaRecorder.AudioSource.VOICE_RECOGNITION, SAMPLE_RATE,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
				bufferSize);
		if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
			releaseAutoRecord();
			return false;
		}
		return true;
	}

	protected void releaseAutoRecord() {
		if (mAudioRecord != null) {
			mAudioRecord.release();
			mAudioRecord = null;
		}
	}

	protected boolean onRecording(byte[] buffer) {
		Message msg = new Message();
		msg.obj = buffer;
		msg.what = RECORD_RECORDING;
		handler.sendMessage(msg);

		mByteCount += buffer.length;
		if (mMaxByte != 0 && mByteCount >= mMaxByte)
			reqStop();
		return false;
	}

	protected void onError(int errorcode, String msg1) {
		Message msg = new Message();
		msg.obj = msg1;
		msg.what = RECORD_ERROR;
		handler.sendMessage(msg);
	}

	protected void onEnd() {
		handler.sendEmptyMessage(RECORD_END);
	}

}
