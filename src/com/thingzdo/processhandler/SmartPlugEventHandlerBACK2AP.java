package com.thingzdo.processhandler;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

import android.content.Intent;
import android.os.Message;

public class SmartPlugEventHandlerBACK2AP extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_BACK2AP_ACTION);
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		try{
			int code   = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
			if (2 > buffer.length) {
		    	mIntent.putExtra("RESULT", code);
		    	mIntent.putExtra("MESSAGE", 
	    				SmartPlugApplication.getContext().getString(R.string.smartplug_oper_back2ap_fail));
		    	SmartPlugApplication.getContext().sendBroadcast(mIntent);
				return;
			}			
			
			mIntent.putExtra("PLUGID", buffer[3]);

			int status = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);
			
			if (0 == code) {
				//success
				mIntent.putExtra("RESULT", 0);
				mIntent.putExtra("STATUS", status);
		    	
			} else {
				//fail
		    	mIntent.putExtra("RESULT", code);
		    	mIntent.putExtra("STATUS", status);
		    	mIntent.putExtra("MESSAGE", 
	    				SmartPlugApplication.getContext().getString(R.string.smartplug_oper_back2ap_fail));		    	
		    	/*int resid = AppServerReposeDefine.getServerResponse(code);
		    	if (0 != resid) {
		    		mIntent.putExtra("MESSAGE", SmartPlugApplication.getContext().getString(resid));
		    	} else {
		    		mIntent.putExtra("MESSAGE", 
		    				SmartPlugApplication.getContext().getString(R.string.smartplug_oper_power_fail));
		    	}*/
		    }
			SmartPlugApplication.getContext().sendBroadcast(mIntent);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}