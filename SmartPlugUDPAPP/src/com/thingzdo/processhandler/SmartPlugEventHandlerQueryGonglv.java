package com.thingzdo.processhandler;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

import android.content.Intent;
import android.os.Message;

public class SmartPlugEventHandlerQueryGonglv extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_QRYGONGLV);		// 查询实时功率
	@Override
	public void handleMessage(Message msg) {
		
		mIntent.putExtra("PLUGID", PubStatus.g_moduleId);
		
		String[] buffer = (String[]) msg.obj;
		try{
			int code   = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
			if (2 > buffer.length) {
		    	mIntent.putExtra("RESULT", code);
		    	mIntent.putExtra("MESSAGE", 
	    				SmartPlugApplication.getContext().getString(R.string.smartplug_query_energe_fail));
		    	SmartPlugApplication.getContext().sendBroadcast(mIntent);
				return;
			}			
			
			String oper_date = buffer[EVENT_MESSAGE_HEADER+1];
			double immediate_power = Double.parseDouble(buffer[EVENT_MESSAGE_HEADER+2]);
			
			if (0 == code) {
				//success
				mIntent.putExtra("RESULT", 0);
				mIntent.putExtra("DATE", oper_date);
				mIntent.putExtra("GONGLV", immediate_power);
		    	
			} else {
				//fail
		    	mIntent.putExtra("RESULT", code);
		    	mIntent.putExtra("STATUS", -1);
		    	mIntent.putExtra("MESSAGE", 
	    				SmartPlugApplication.getContext().getString(R.string.smartplug_query_energe_fail));		    	
		    }
			SmartPlugApplication.getContext().sendBroadcast(mIntent);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
