package com.thingzdo.processhandler;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.dataprovider.SmartPlugTimerHelper;
import com.thingzdo.ui.TimerDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerNotifyTimer extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_NOTIFYTIMER);
	private ArrayList<TimerDefine>  timerList = new ArrayList<TimerDefine>();
	
	@Override
	public void handleMessage(Message msg) {
		timerList.clear();
		String[] buffer = (String[]) msg.obj;
		
	    int timerCount = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+0]);
		if (0 == timerCount) {
			return;
		}	    
		
		int i_header = 1 + EVENT_MESSAGE_HEADER;
		String[] infors = new String[buffer.length - i_header];
		for (int i = i_header; i < buffer.length; i++) {
			infors[i - i_header] = buffer[i];
		}	    
		    
	    for (int j = 0; j < timerCount; j++) {
	    	TimerDefine ti   = new TimerDefine();
	    	ti.mTimerId      = Integer.parseInt(infors[j * 6 + 0]);
	    	ti.mPlugId       = PubStatus.g_moduleId;
	    	ti.mType         = Integer.parseInt(infors[j * 6 + 1]);
	    	ti.mPeriod       = infors[j * 6 + 2];
	    	ti.mPowerOnTime  = infors[j * 6 + 3];
	    	ti.mPowerOffTime = infors[j * 6 + 4];
	    	ti.mEnable       = infors[j * 6 + 5].equals("1") ? true : false;
	    	timerList.add(ti);
	    }
	    
	    addTimer(timerList);
		
		mIntent.putExtra("RESULT", 1);
		SmartPlugApplication.getContext().sendBroadcast(mIntent);
	}

	private void addTimer(ArrayList<TimerDefine> timers) {
		SmartPlugTimerHelper mTimerHelper = new SmartPlugTimerHelper(SmartPlugApplication.getContext()); 
		mTimerHelper.clearTimer(PubStatus.g_moduleId);
		for (int j = 0; j < timers.size(); j++) {
			TimerDefine time = timers.get(j);
			mTimerHelper.addTimer(time);
		}
		
		mTimerHelper = null;
	}
}
