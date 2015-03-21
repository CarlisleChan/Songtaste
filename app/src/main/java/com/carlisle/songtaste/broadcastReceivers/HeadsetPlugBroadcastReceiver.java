/*
 * Copyright (C) 2014 Saravan Pantham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.carlisle.songtaste.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * BroadcastReceiver that handles and processes all headset 
 * unplug/plug actions and events.
 * 
 * @author Carlisle Chan
 */
public class HeadsetPlugBroadcastReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {

 	    if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
	        int state = intent.getIntExtra("state", -1);
	        switch (state) {
	        case 0:
	            //Headset unplug event.
//	        	BaseApplication.getInstance().getService().pausePlayback();
	            break;
	        case 1:
	            //Headset plug-in event.
//                BaseApplication.getInstance().getService().startPlayback();
	            break;
	        default:
	            //No idea what just happened.
	        }

		}
	    
	}
	  
}
