package com.thingzdo.processhandler;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.AppServerReposeDefine;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerLogin  extends SmartPlugEventHandler{
	Intent mIntent = new Intent(PubDefine.LOGIN_BROADCAST);
	
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		try{
			int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
			if (0 == ret) {
		    	mIntent.putExtra("RESULT", 0);
		    	if (buffer.length >= 2) {
		    		mIntent.putExtra("MESSAGE", buffer[EVENT_MESSAGE_HEADER+1]);
		    	} else {
		    		mIntent.putExtra("MESSAGE", "");
		    	}
		    	SmartPlugApplication.getContext().sendBroadcast(mIntent);
			} else {
		    	mIntent.putExtra("RESULT", 1);
		    	int resid = AppServerReposeDefine.getServerResponse(ret);
		    	if (0 != resid) {
		    		mIntent.putExtra("MESSAGE", SmartPlugApplication.getContext().getString(resid));
		    	} else {
		    		mIntent.putExtra("MESSAGE", getUnknowErrorMsg());
		    		
		    	}
		    	SmartPlugApplication.getContext().sendBroadcast(mIntent);	    	
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
