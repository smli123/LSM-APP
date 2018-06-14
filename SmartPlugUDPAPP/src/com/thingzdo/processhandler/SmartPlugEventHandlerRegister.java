package com.thingzdo.processhandler;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.AppServerReposeDefine;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

import android.content.Intent;
import android.os.Message;

public class SmartPlugEventHandlerRegister extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.REGISTER_BROADCAST);
	
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		
		int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
		
	    if (0 == ret) {
	    	//success
	    	mIntent.putExtra("RESULT", 0);
	    	SmartPlugApplication.getContext().sendBroadcast(mIntent);	    		
	    } else {	
	    	//fail
	    	mIntent.putExtra("RESULT", ret);
	    	PubFunc.log("error:" + String.valueOf(ret));
	    	int resid = AppServerReposeDefine.getServerResponse(ret);
	    	PubFunc.log(String.valueOf(resid));
	    	if (0 != resid) {
	    		mIntent.putExtra("MESSAGE", 
	    				SmartPlugApplication.getContext().getString(resid));
	    	} else {
	    		mIntent.putExtra("MESSAGE", 
	    				SmartPlugApplication.getContext().getString(R.string.smartplug_ctrl_userregister_fail));
	    	}
	    	SmartPlugApplication.getContext().sendBroadcast(mIntent);	    	
	    }		
	}
}
