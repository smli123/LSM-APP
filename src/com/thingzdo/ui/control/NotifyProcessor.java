package com.thingzdo.ui.control;

import android.content.Context;
import android.content.Intent;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;

public class NotifyProcessor {
    public static boolean onlineNotify(Context context, Intent intent) {
    	SmartPlugHelper plugHelper = new SmartPlugHelper(context);
		String plugid = intent.getStringExtra("PLUGID");
		int online = intent.getIntExtra("ONLINE", 0);
		SmartPlugDefine plug = plugHelper.getSmartPlug(plugid);
		if (null == plug) {
			return false;
		}
		
		plug.mIsOnline = (online == 1 ? true : false);
		if (0 < plugHelper.modifySmartPlug(plug)) {
			return true;
		}

		return true;
    }
    
    public static boolean powerNotify(Context context, Intent intent) {
    	SmartPlugHelper plugHelper = new SmartPlugHelper(context);
		String plugid = intent.getStringExtra("PLUGID");
		int value = intent.getIntExtra("POWER", 0);
		SmartPlugDefine plug = plugHelper.getSmartPlug(plugid);
		if (null == plug) {
			return false;
		}

		plug.mDeviceStatus = value;
		if (0 < plugHelper.modifySmartPlug(plug)) {
			return true;
		}    	
		
		return false;
    }  

    public static boolean curtainNotify(Context context, Intent intent) {
    	SmartPlugHelper plugHelper = new SmartPlugHelper(context);
		String plugid = intent.getStringExtra("PLUGID");
		SmartPlugDefine plug = plugHelper.getSmartPlug(plugid);
		if (null == plug) {
			return false;
		}

		int value = 0;
		if (plug.mSubDeviceType == PubDefine.DEVICE_SMART_CURTAIN) {		// 类型为窗帘
			value = intent.getIntExtra("CURTAIN", 0);
			plug.mPosition = value;
		} else if (plug.mSubDeviceType == PubDefine.DEVICE_SMART_KETTLE) {	// 类型为智能烧水壶
		//} else if (plug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG) {	// 【测试用】：类型为智能烧水壶
			value = intent.getIntExtra("TEMPERATURE", 0);
			plug.mColor_R = value;
			value = intent.getIntExtra("POWER", 0);
			plug.mColor_G = value;
		}
				
		if (0 < plugHelper.modifySmartPlug(plug)) {
			return true;
		}    	
		
		return false;
    }  

    public static boolean kettleNotify(Context context, Intent intent) {
    	SmartPlugHelper plugHelper = new SmartPlugHelper(context);
		String plugid = intent.getStringExtra("PLUGID");
		SmartPlugDefine plug = plugHelper.getSmartPlug(plugid);
		if (null == plug) {
			return false;
		}

		int value = 0;
		if (plug.mSubDeviceType == PubDefine.DEVICE_SMART_CURTAIN) {		// 类型为窗帘
			value = intent.getIntExtra("CURTAIN", 0);
			plug.mPosition = value;
		} else if (plug.mSubDeviceType == PubDefine.DEVICE_SMART_KETTLE) {	// 类型为智能烧水壶
			value = intent.getIntExtra("TEMPERATURE", 0);
			plug.mColor_R = value;
			value = intent.getIntExtra("POWER", 0);
			plug.mColor_G = value;
		}
				
		if (0 < plugHelper.modifySmartPlug(plug)) {
			return true;
		}    	
		
		return false;
    }      
    public static boolean lightNotify(Context context, Intent intent) {
    	SmartPlugHelper plugHelper = new SmartPlugHelper(context);
		String plugid = intent.getStringExtra("PLUGID");
		int light = intent.getIntExtra("LIGHT", 0);
		SmartPlugDefine plug = plugHelper.getSmartPlug(plugid);
		if (null == plug) {
			return false;
		}
		if (light == 0) {
			plug.mDeviceStatus = plug.mDeviceStatus & 0x1101;
		} else {
			plug.mDeviceStatus = plug.mDeviceStatus | 0x0010;
		}
		if (0 < plugHelper.modifySmartPlug(plug)) {
			return true;
		}    	
		
		return false;
    }
}
