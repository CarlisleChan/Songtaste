package com.carlisle.songtaste.base;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UninstalledObserver {
	private static UninstalledObserver instance;
	private Context context;
	private static final String TAG = "UninstalledObserver";
	private static final String WEBSITE = "http://carlislechan.github.io";
	private int mObserverProcessPid = -1;
	
	public native int init(String userSerial, String webSite);

	static {
		try {
			Log.d(TAG, "load lib --> uninstalled_observer");
			System.loadLibrary("uninstalled_observer");	
		} catch (UnsatisfiedLinkError ule) {
			System.err.println("WARNING: Could not load library!");
		}
	}
	
	public static UninstalledObserver getInstance(Context context) {
		if (instance == null) {
			instance = new UninstalledObserver();
		}
		instance.context = context;
		return instance;
	}
	
	public void setup() {
		createFile();
		
		// API level小于17，不需要获取userSerialNumber
		if (Build.VERSION.SDK_INT < 17) {
			mObserverProcessPid = init(null, WEBSITE);
		}
		// 否则，需要获取userSerialNumber
		else {
			mObserverProcessPid = init(getUserSerial(), WEBSITE);
		}
	}

	private void createFile() {
		File file = new File("/data/data/com.carlisle.songtaste/files/observedFile");
		if (!file.exists()) {
			try {
				File dir = new File("/data/data/com.carlisle.songtaste/files");
				if (!dir.exists()) {
					if (dir.mkdir()) {
						Log.e(TAG, "创建files目录成功");
					} else {
						Log.e(TAG, "创建files目录失败");
						return;
					}
				}
				if (file.createNewFile()) {
					Log.e(TAG, "创建observedFile成功");
					return;
				}
				Log.e(TAG, "创建observedFile失败");
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "创建observedFile失败");
			}
		} else {
			Log.e(TAG, "observedFile存在");
		}
	}

	// 由于targetSdkVersion低于17，只能通过反射获取
	private String getUserSerial() {
		Object userManager = context.getSystemService("user");
		if (userManager == null) {
			Log.e(TAG, "userManager not exsit !!!");
			return null;
		}

		try {
			Method myUserHandleMethod = android.os.Process.class.getMethod(
					"myUserHandle", (Class<?>[]) null);
			Object myUserHandle = myUserHandleMethod.invoke(
					android.os.Process.class, (Object[]) null);

			Method getSerialNumberForUser = userManager.getClass().getMethod(
					"getSerialNumberForUser", myUserHandle.getClass());
			long userSerial = (Long) getSerialNumberForUser.invoke(userManager,
					myUserHandle);
			return String.valueOf(userSerial);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "", e);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "", e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, "", e);
		} catch (InvocationTargetException e) {
			Log.e(TAG, "", e);
		}

		return null;
	}
}
