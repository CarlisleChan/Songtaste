package com.carlisle.songtaste.cmpts.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.carlisle.songtaste.cmpts.events.PauseEvent;
import com.carlisle.songtaste.cmpts.services.MusicService;
import com.carlisle.songtaste.utils.Common;

import de.greenrobot.event.EventBus;

public class HeadsetPlugReceiver extends BroadcastReceiver {
	private boolean HEADPLUGSTATUS = false;
    private MusicService musicService;

    public HeadsetPlugReceiver() {
    }

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.hasExtra("state")) {
			if (intent.getIntExtra("state", 0) == 0) {
				if (HEADPLUGSTATUS) {
                    EventBus.getDefault().post(new PauseEvent());
					HEADPLUGSTATUS = false;
				}
			}else if (intent.getIntExtra("state", 0) == 1) {
				HEADPLUGSTATUS = true;
			}
		}
	}

    public void register(MusicService musicService) {
        this.musicService = musicService;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Common.HeadPlug.HEADSET_PLUG);
        this.musicService.registerReceiver(this, intentFilter);
    }

    public void unRegister() {
        musicService.unregisterReceiver(this);
    }
}