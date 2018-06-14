package com.thingzdo.processhandler;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerNotifyKettle extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_NOTIFY_KETTLE);
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		if ((EVENT_MESSAGE_HEADER+2) > buffer.length){
			return;
		}

		mIntent.putExtra("PLUGID", buffer[3]);
		
		//例子： 20170509175231,NOTIFYKETTLE,smli123hz,648469,0,
		//      01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20#
		//      CURTAIN（窗帘位置）,TEMPERATURE（温度）,POWER（功率）,...
		int value = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);
		mIntent.putExtra("KETTLE", value);

		try{
			value = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);	// 当前温度
			mIntent.putExtra("TEMPERATURE", value);
			value = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+2]);	// 保温温度
			mIntent.putExtra("WARM_TEMPERATURE", value);
			value = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+3]);	// 当前功率
			mIntent.putExtra("CUR_POWER", value);
			value = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+4]);	// 当前倒计时时常分钟
			mIntent.putExtra("LEFT_TIME", value);
			value = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+5]);	// 当前开关，0：关，1：开
			mIntent.putExtra("SWITCH_POWER", value);
			String temp = buffer[EVENT_MESSAGE_HEADER+6];				// 当前模式; SHAOSHUI,SUANNAI,HUACHA
			mIntent.putExtra("CUR_MODE", temp);
			value = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+7]);	// 当前保温开关，0：关，1：开
			mIntent.putExtra("SWITCH_WARM_POWER", value);
			value = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+8]);	// 是否已经烧开，0：未烧开，1：已经烧开
			mIntent.putExtra("BOILED", value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SmartPlugApplication.getContext().sendBroadcast(mIntent);
	}
}
