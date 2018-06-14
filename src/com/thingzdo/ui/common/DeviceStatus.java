package com.thingzdo.ui.common;

import java.util.ArrayList;

import com.thingzdo.ui.TimerDefine;

public class DeviceStatus {
	public String mModuleId;
	public String mPlugName;
	public String mPlugMac;
	public String mVersion;
	public String mModuleType;
	public int    mPwrStatus;
	public int    mFlashMode;
	public int    mColorRed;
	public int    mColorGreen;
	public int    mColorBlue;
	public int 	  mProtocolMode;
	
    // 把ModuleType区分为设备类型和产品类型
    public String mSubDeviceType;
    public String mSubProductType;
	
	public ArrayList<TimerDefine> mTimer;
}
