package com.thingzdo.ui.smartplug;

import com.thingzdo.ui.common.PubDefine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShutdownReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN")) {
			//PubDefine.disconnect();	
		}
	}
}
