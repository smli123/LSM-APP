package com.thingzdo.ui;

public class SmartPlugDefine {
	public int    mID; 
	public String mUserName;
	public String mPlugId;
    public String mPlugName;
    
    public String  mIPAddress;
    public String  mVersion;
    
    public boolean mIsOnline;
    public int     mDeviceStatus; 
    public int     mFlashMode;   //0:light on   1:flash
    
    public int mColor_R;
    public int mColor_G;
    public int mColor_B;
    
    public int mProtocolMode;	//1:TCP	2:UDP
    public String mMAC;
    public String mModuleType;

    public int     mPosition;	//窗帘的位置 
    
    // 把ModuleType区分为设备类型和产品类型
    public String mSubDeviceType;
    public String mSubProductType;
    
}
