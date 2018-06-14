package com.thingzdo.util;

import java.util.TimerTask;

import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class AppTimeoutTask extends TimerTask{

	@Override
	public void run() {
		SmartPlugApplication.setLogined(false);
		SmartPlugApplication.getInstance().exit();
	}

}
