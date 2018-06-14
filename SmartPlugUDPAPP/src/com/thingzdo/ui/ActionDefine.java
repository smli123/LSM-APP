package com.thingzdo.ui;

import java.util.Date;

public class ActionDefine {
	public int    mID;
    public int    mDirection; //短信方向 0:发送 1:收到
    public String mTargetSIM;
    public String mSourceSIM;
    public long    mActionCode;
    public String mActionString;
    public Date   mActionTime;
    public int    mReceiveType; //收到短信返回值的类型 0:无返回; 1:经纬度;2:话费 
    public String mReceiveValue;
}
