package com.thingzdo.processhandler;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import android.content.Intent;
import android.os.Message;

public class SmartPlugEventHandlerLogout extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.LOGOUT_BROADCAST);	
	
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		
		int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
		mIntent.putExtra("LOGOUT", ret);
    	SmartPlugApplication.getContext().sendBroadcast(mIntent);
	}
}
