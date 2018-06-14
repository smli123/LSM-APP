package com.thingzdo.ui.control;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class ShakeHandler extends Handler {
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		
		switch (msg.what) {
		case 0:
			PubFunc.thinzdoToast(SmartPlugApplication.getContext(), (String)msg.obj);
			Intent intent = new Intent(PubDefine.PLUG_SHAKE_FAIL_ACTION);
			SmartPlugApplication.getContext().sendBroadcast(intent);
			break;
		case 1:
		default:
			PubFunc.thinzdoToast(SmartPlugApplication.getContext(), 
					SmartPlugApplication.getContext().getString(R.string.oper_error));
			break;
		}		
	}
}
