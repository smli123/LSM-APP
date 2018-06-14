package com.thingzdo.util;

import java.util.TimerTask;

import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.control.DetailAirConActivity;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class AirConTestTimerTask extends TimerTask{
	private int i_hours_no = 0;
	private DetailAirConActivity mActivity;

	public AirConTestTimerTask(DetailAirConActivity activity) {
		i_hours_no = 0;
		mActivity = activity;
	}
	
	@Override
	public void run() {
		
		String command = "关闭空调";
		mActivity.parsecommand(command);
		i_hours_no++;
		
		PubFunc.log("[TEST] AirCon Timer Command: " + command + " ; NO. " + String.valueOf(i_hours_no));
		
//		if (i_hours_no >= 10000) {
//			mActivity.StopSceneTimer();
//			PubFunc.log("AirCon Timer is Cancelled");
//		}
//		mActivity.updateTimerUI(i_hours_no);
	}

}
