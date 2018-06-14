package com.thingzdo.processhandler;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.dataprovider.SmartPlugIRSceneHelper;
import com.thingzdo.dataprovider.SmartPlugTimerHelper;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.IRSceneDefine;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.TimerDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerBatteryNotifyEnerge extends SmartPlugEventHandler {
	private Intent mIntent = new Intent(PubDefine.PLUG_BATTERY_NOTIFYENERGE_ACTION);
	
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		if ((EVENT_MESSAGE_HEADER+2) > buffer.length){
			return;
		}

		mIntent.putExtra("PLUGID", buffer[3]);
		
		//例子： 20180227172549, NOTIFYBATTERYENERGY, smli123hz, 64792801, 00000000, 21.3#
		int status = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER]);
		String energe = buffer[EVENT_MESSAGE_HEADER+1];
		
		mIntent.putExtra("ENERGE", energe);
		mIntent.putExtra("STATUS", status);
		
		SmartPlugApplication.getContext().sendBroadcast(mIntent);
	}
}
