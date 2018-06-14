package com.thingzdo.processhandler;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

import android.content.Intent;
import android.os.Message;

public class SmartPlugEventHandlerQueryEnerge extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_QRYENERGE);	// 历史统计电量
	@Override
	public void handleMessage(Message msg) {
		
		mIntent.putExtra("PLUGID", PubStatus.g_moduleId);
		
		String[] buffer = (String[]) msg.obj;
		try{
			int code   = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
			if (2 > buffer.length) {
		    	mIntent.putExtra("RESULT", code);
		    	mIntent.putExtra("MESSAGE", 
	    				SmartPlugApplication.getContext().getString(R.string.smartplug_query_charge_fail));
		    	SmartPlugApplication.getContext().sendBroadcast(mIntent);
				return;
			}			
			
			double one_hour = Double.parseDouble(buffer[EVENT_MESSAGE_HEADER+1]);
			double one_day = Double.parseDouble(buffer[EVENT_MESSAGE_HEADER+2]);
			double one_week = Double.parseDouble(buffer[EVENT_MESSAGE_HEADER+3]);
			double one_month = Double.parseDouble(buffer[EVENT_MESSAGE_HEADER+4]);
			
			if (0 == code) {
				//success
				mIntent.putExtra("RESULT", 0);
				mIntent.putExtra("CHARGE_ONE_HOUR", one_hour);
				mIntent.putExtra("CHARGE_ONE_DAY", one_day);
				mIntent.putExtra("CHARGE_ONE_WEEK", one_week);
				mIntent.putExtra("CHARGE_ONE_MONTH", one_month);
		    	
			} else {
				//fail
		    	mIntent.putExtra("RESULT", code);
		    	mIntent.putExtra("STATUS", -1);
		    	mIntent.putExtra("MESSAGE", 
	    				SmartPlugApplication.getContext().getString(R.string.smartplug_query_charge_fail));		    	
		    }
			SmartPlugApplication.getContext().sendBroadcast(mIntent);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
