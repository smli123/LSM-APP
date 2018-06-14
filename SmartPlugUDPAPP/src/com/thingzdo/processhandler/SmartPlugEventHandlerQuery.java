package com.thingzdo.processhandler;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.dataprovider.SmartPlugIRSceneHelper;
import com.thingzdo.dataprovider.SmartPlugTimerHelper;
import com.thingzdo.ui.IRSceneDefine;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.TimerDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerQuery extends SmartPlugEventHandler {
	private Intent mIntent = new Intent(PubDefine.PLUG_UPDATE);
	
	private ArrayList<SmartPlugDefine> plugs  = new ArrayList<SmartPlugDefine>();
	private ArrayList<TimerDefine>  timerList = new ArrayList<TimerDefine>();
	private ArrayList<IRSceneDefine>  irSceneList = new ArrayList<IRSceneDefine>();	
	
	@Override
	public void handleMessage(Message msg) {
		plugs.clear();
		timerList.clear();
		irSceneList.clear();
		String[] buffer = (String[]) msg.obj;
		
		int code = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]); 
		if (0 != code) {
	    	return;
		}
		int plugCount = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);
		if (0 == plugCount) {
			SmartPlugHelper mPlugHelper = new SmartPlugHelper(SmartPlugApplication.getContext());
			mPlugHelper.clearSmartPlug();
			
			mIntent.putExtra("RESULT", 1);
			mIntent.putExtra("MESSAGE", "");
			SmartPlugApplication.getContext().sendBroadcast(mIntent);
			return;
		}
		
		int i_header = 2 + EVENT_MESSAGE_HEADER;
		String[] infors = new String[buffer.length - i_header];
		for (int i = i_header; i < buffer.length; i++) {
			infors[i - i_header] = buffer[i];
		}
		
		int baseIdx = 0;
		for (int i = 0; i < plugCount; i++) {
			SmartPlugDefine aPlug = new SmartPlugDefine();
		    aPlug.mUserName  = PubStatus.g_CurUserName;
		    aPlug.mPlugId    = infors[baseIdx + 0];
		    aPlug.mPlugName  = infors[baseIdx + 1];
		    aPlug.mMAC 		 = infors[baseIdx + 2];
		    
		    aPlug.mVersion 		 = infors[baseIdx + 3];
		    aPlug.mModuleType	 = infors[baseIdx + 4];
		    
		    aPlug.mSubDeviceType = PubFunc.getDeviceTypeFromModuleType(aPlug.mModuleType);
		    aPlug.mSubProductType = PubFunc.getProductTypeFromModuleType(aPlug.mModuleType);
		    
		    // lishimin 2016.4.17 ������һ����ģ�����ProtocolMode
		    aPlug.mProtocolMode   = Integer.parseInt(infors[baseIdx + 5]);
		    aPlug.mIsOnline  =  Integer.parseInt(infors[baseIdx + 6]) == 1 ? true : false;
		    int value = Integer.parseInt(infors[baseIdx + 7]);
		    aPlug.mDeviceStatus = value;
		    aPlug.mPosition = value;
		    aPlug.mIPAddress = infors[baseIdx + 8];
		    aPlug.mFlashMode = Integer.parseInt(infors[baseIdx + 9]);
		    aPlug.mColor_R   = Integer.parseInt(infors[baseIdx + 10]);
		    aPlug.mColor_G   = Integer.parseInt(infors[baseIdx + 11]);
		    aPlug.mColor_B   = Integer.parseInt(infors[baseIdx + 12]);
		    plugs.add(aPlug);
		    
		    int timerCount = Integer.parseInt(infors[baseIdx + 13]);
		    
		    baseIdx = baseIdx + 14;
		    int j = 0;
		    for (j = 0; j < timerCount; j++) {
		    	TimerDefine ti   = new TimerDefine();
		    	ti.mTimerId      = Integer.parseInt(infors[baseIdx + j * 6 + 0]);
		    	ti.mPlugId       = aPlug.mPlugId;
		    	ti.mType         = Integer.parseInt(infors[baseIdx + j * 6 + 1]);
		    	ti.mEnable       = infors[baseIdx + j * 6 + 2].equals("1") ? true : false;
		    	ti.mPeriod       = infors[baseIdx + j * 6 + 3];
		    	ti.mPowerOnTime  = infors[baseIdx + j * 6 + 4];
		    	ti.mPowerOffTime = infors[baseIdx + j * 6 + 5];
		    	timerList.add(ti);
		    }
		    baseIdx = baseIdx + 6 * timerCount;
		    
		    // 列出红外数据
		    int irCount = Integer.parseInt(infors[baseIdx]);
		    baseIdx++;
		    for (j = 0; j < irCount; j++) {
		    	IRSceneDefine ti   = new IRSceneDefine();
		    	ti.mPlugId  = aPlug.mPlugId;
		    	ti.mIRSceneId      = Integer.parseInt(infors[baseIdx + j * 10 + 0]);
		    	ti.mPower         = Integer.parseInt(infors[baseIdx + j * 10 + 1]);
		    	ti.mMode       = Integer.parseInt(infors[baseIdx + j * 10 + 2]);
		    	ti.mDirection       = Integer.parseInt(infors[baseIdx + j * 10 + 3]);
		    	ti.mScale  = Integer.parseInt(infors[baseIdx + j * 10 + 4]);
		    	ti.mTemperature = Integer.parseInt(infors[baseIdx + j * 10 + 5]);
		    	ti.mTime = infors[baseIdx + j * 10 + 6];
		    	ti.mPeriod = infors[baseIdx + j * 10 + 7];
		    	ti.mIRName = infors[baseIdx + j * 10 + 8];
		    	ti.mEnable = Integer.parseInt(infors[baseIdx + j * 10 + 9]);
		    	irSceneList.add(ti);
		    }
		    baseIdx = baseIdx + 10 * irCount;
		}
		
		add2DB(plugs, timerList, irSceneList);
		
		mIntent.putExtra("RESULT", 1);
		mIntent.putExtra("MESSAGE", "");
		SmartPlugApplication.getContext().sendBroadcast(mIntent);		
	}
	
	private void add2DB(ArrayList<SmartPlugDefine> plugs, ArrayList<TimerDefine> timers, ArrayList<IRSceneDefine> irlist) {
		SmartPlugHelper mPlugHelper = new SmartPlugHelper(SmartPlugApplication.getContext());
		SmartPlugTimerHelper mTimerHelper = new SmartPlugTimerHelper(SmartPlugApplication.getContext());
		SmartPlugIRSceneHelper mIRSceneHelper = new SmartPlugIRSceneHelper(SmartPlugApplication.getContext());
		mPlugHelper.clearSmartPlug();

		int i = 0, j = 0;
		for (i = 0; i < plugs.size(); i++) {
			long id = mPlugHelper.addSmartPlug(plugs.get(i));
//			PubFunc.log("addSmartPlug:" + id);	
			
			if (id  > 0) {
				for (j = 0; j < timers.size(); j++) {
					TimerDefine time = timers.get(j);
					if (time.mPlugId == plugs.get(i).mPlugId) {
						mTimerHelper.addTimer(time);
					}
				}
			}
			
			mIRSceneHelper.clearIRScene(plugs.get(i).mPlugId);
			if (id  > 0) {
				for (j = 0; j < irlist.size(); j++) {
					IRSceneDefine ir = irlist.get(j);
					if (ir.mPlugId == plugs.get(i).mPlugId) {
						mIRSceneHelper.addIRScene(ir);
					}
				}
			}
		}
		
		mPlugHelper = null;
		mTimerHelper = null;
		mIRSceneHelper = null;
	}
}
