package com.thingzdo.processhandler;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

import android.content.Intent;
import android.os.Message;

public class SmartPlugEventHandlerRGB extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_RGB_ACTION);
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
		
		if (0 == ret) {
			mIntent.putExtra("RESULT", 0);
		} else {
	    	mIntent.putExtra("RESULT", ret);
	    	mIntent.putExtra("MESSAGE", 
    				SmartPlugApplication.getContext().getString(R.string.smartplug_oper_light_fail));	    	
	    	/*int resid = AppServerReposeDefine.getServerResponse(ret);
	    	if (0 != resid) {
	    		mIntent.putExtra("MESSAGE", SmartPlugApplication.getContext().getString(resid));
	    	} else {
	    		mIntent.putExtra("MESSAGE", 
	    				SmartPlugApplication.getContext().getString(R.string.smartplug_oper_light_fail));
	    	}*/
	    }
		SmartPlugApplication.getContext().sendBroadcast(mIntent);
		
	}
}
