package com.thingzdo.processhandler;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerModifyEmail extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.USER_MODIFY_EMAIL);
	
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		try{
			int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
			
			if (0 == ret) {
		    	mIntent.putExtra("RESULT", 0);
		    	SmartPlugApplication.getContext().sendBroadcast(mIntent);
			} else {
		    	mIntent.putExtra("RESULT", 1);
		    	SmartPlugApplication.getContext().sendBroadcast(mIntent);	    	
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
