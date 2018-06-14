package com.thingzdo.processhandler;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.AppServerReposeDefine;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerDeleteScene extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_AIRCON_DELSCENE_ACTION);
	
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		String[] buffer = (String[]) msg.obj;
		
		int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
		
		if (0 == ret) {
	    	mIntent.putExtra("RESULT", 0);
	    	SmartPlugApplication.getContext().sendBroadcast(mIntent);
		} else {
	    	mIntent.putExtra("RESULT", ret);
	    	int resid = AppServerReposeDefine.getServerResponse(ret);
	    	if (0 == resid) {
	    		mIntent.putExtra("MESSAGE", 
	    				SmartPlugApplication.getContext().getString(R.string.smartplug_ctrl_deletetimer_fail));
	    	} else {
	    		mIntent.putExtra("MESSAGE", 
	    				SmartPlugApplication.getContext().getString(resid));
	    	}
	    	SmartPlugApplication.getContext().sendBroadcast(mIntent);	    	
	    }		
	}
}
