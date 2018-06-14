package com.thingzdo.processhandler;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerNotifyUpgradeAPStatus extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_NOTIFY_UPGRADEAP);
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		if ((EVENT_MESSAGE_HEADER+2) > buffer.length){
			return;
		}

		mIntent.putExtra("PLUGID", buffer[3]);
		
		//例子： 20170509175231,NOTIFYUPGRADEAP,smli123hz,648469,0,
		//      01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20#
		//      INDEXBLOCK（索引块）,TOTALBLOCK（全部块）,...
		try{
			int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
			if (0 == ret) {
		    	mIntent.putExtra("RESULT", 0);
			
				int value = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);
				mIntent.putExtra("INDEXBLOCK", value);
				
				value = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+2]);
				mIntent.putExtra("TOTALBLOCK", value);
			} else {
				mIntent.putExtra("RESULT", ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SmartPlugApplication.getContext().sendBroadcast(mIntent);
	}
}
