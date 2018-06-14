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

public class SmartPlugEventHandlerBatteryQueryTrail extends SmartPlugEventHandler {
	private Intent mIntent = new Intent(PubDefine.PLUG_BATTERY_QUERYTRAIL_ACTION);
	
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		try{
			int code   = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
			if (2 > buffer.length) {
		    	mIntent.putExtra("RESULT", code);
		    	mIntent.putExtra("MESSAGE", 
	    				SmartPlugApplication.getContext().getString(R.string.smartplug_oper_battery_query_trail_fail));
		    	SmartPlugApplication.getContext().sendBroadcast(mIntent);
				return;
			}			
			
			int status = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);
			
			if (0 == code) {
				//success
				String trails = "";
				for (int i = EVENT_MESSAGE_HEADER + 2; i < buffer.length; i++) {
					trails += buffer[i];
					if (i < buffer.length - 1) {
						trails += ",";
					}
				}
				
				mIntent.putExtra("RESULT", 0);
				mIntent.putExtra("STATUS", status);
				mIntent.putExtra("MESSAGE", trails);
			} else {
				//fail
		    	mIntent.putExtra("RESULT", code);
		    	mIntent.putExtra("STATUS", status);
		    	mIntent.putExtra("MESSAGE", 
	    				SmartPlugApplication.getContext().getString(R.string.smartplug_oper_battery_query_trail_fail));		    	
		    }
			SmartPlugApplication.getContext().sendBroadcast(mIntent);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
