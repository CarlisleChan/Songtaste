package com.carlisle.songtaste.cmpts.SoundRecognition;

public interface DoresoRecordListener {
	
	/**
	 * 实时录音数据片段	
	 * @param buffer
	 */
	public abstract void onRecording(byte[] buffer);
	
	
	/**
	 * 录音异常
	 * @param errorcode
	 * @param msg
	 */
	public abstract void onRecordError(int errorcode, String msg);

	/**
	 * 录音结束
	 */
	public abstract void onRecordEnd();
}
