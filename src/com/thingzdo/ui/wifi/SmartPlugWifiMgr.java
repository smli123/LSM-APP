package com.thingzdo.ui.wifi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;

import com.thingzdo.internet.SocketMgr;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.ui.TimerDefine;
import com.thingzdo.ui.common.DeviceStatus;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.smartplug.PubStatus;

public class SmartPlugWifiMgr {

	private static Handler mConHandler = null;
	private static DeviceStatus mDevice = new DeviceStatus();
	private final static  String Device_IP = "192.168.4.1"; 
	private static String mIP = Device_IP;
	private static int mPort = 6002;
	
	// MODIFY
	private static DataOutputStream outsocket;
	private static DataInputStream insocket;
	private static String str_msg;
	
	public static void createWifiSocket(Handler handler, String ip, int port) {
		mConHandler = handler;
		if (null == ip || ip.isEmpty()) {
			mIP = Device_IP;
		} else {
			mIP = ip;
		}
		
		if (0 != port) {
			mPort = port;
		}
		
		new Thread(taskSetApToStation).start();
	}
	
	public static void disconnectSocket() {
		SocketMgr.disconnectSocket();
	}
	
	private static Runnable taskSetApToStation = new Runnable() {
		@Override
		public void run() {
			str_msg = "[01]before new Socket";
			
			try {
				PubFunc.log("Login: connect plug...");
				
				if (false == SocketMgr.createSocket(mIP, mPort)) {
					mConHandler.sendEmptyMessage(0);
					return;
				}
				
				// MODIFY	
				str_msg = "[02]after new Socket";
				outsocket = new DataOutputStream(PubDefine.global_tcp_socket.getOutputStream());
				insocket  = new DataInputStream(PubDefine.global_tcp_socket.getInputStream());
	            
	            String gText = "0" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
	            		+ SmartPlugMessage.CMD_SP_LOGIN_MODULE + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
	            		+ PubFunc.getTimeString() + StringUtils.PACKAGE_END_SYMBOL;
	            
	            str_msg = "[03]before send Socket";
	            outsocket.write(gText.getBytes());
	            
	            str_msg = "[04]after send Socket";

	            outsocket.flush();
	            PubFunc.log("SEND_TCP_M:" + gText);
	            
	            str_msg = "[05]before login";
				if (false == login()) {
					mConHandler.sendEmptyMessage(0);
					str_msg = str_msg + "\n[10] Fail, after login";			
				} else {
					str_msg = str_msg + "\n[11] Success, after login";		
				
					Message msg = new Message();
					msg.obj  = mDevice;
					msg.what = 1;
					mConHandler.sendMessage(msg);
					PubFunc.log("connect plug OK");
				}
				
			} catch(Exception e) {
				e.printStackTrace();
				mConHandler.sendEmptyMessage(0);
			}
		}
	};
	
	private static boolean login() {
		String revMsg;
		int i_length = 0;
		byte[] buffer = new byte[2048];
		
		try{
			str_msg = "[06]before login->read";
			i_length = insocket.read(buffer);
			if (0 == i_length) {
				return false;
			}
			
			str_msg = "[07]after login->read";
						
			revMsg = new String(buffer);
			
			String str_tmp = revMsg;
        	if (-1 == str_tmp.indexOf("#")) {
        		PubFunc.log("RECV_TCP_M:" + str_tmp);
        	} else {
        		PubFunc.log("RECV_TCP_M:" + str_tmp.substring(0, str_tmp.lastIndexOf("#") + 1));
        	}
        	
			revMsg = revMsg.substring(0, revMsg.indexOf(StringUtils.PACKAGE_END_SYMBOL));
			String arrays[] = revMsg.split(StringUtils.PACKAGE_RET_SPLIT_SYMBOL);
			if(arrays == null || arrays.length < 15){
				mConHandler.sendEmptyMessage(0);
				return false;
			}
			
			str_msg = "[08]before login->array";
			
			PubStatus.g_userCookie = arrays[0]; 
			PubStatus.g_moduleId   = arrays[3];
			PubStatus.g_moduleType = arrays[7];

			mDevice.mModuleId   = PubStatus.g_moduleId;
			mDevice.mPlugName   = arrays[4];
			mDevice.mPlugMac 	= arrays[5];
			mDevice.mVersion    = arrays[6];
			mDevice.mModuleType = arrays[7];
			
			mDevice.mSubDeviceType = PubFunc.getDeviceTypeFromModuleType(mDevice.mModuleType);
			mDevice.mSubProductType = PubFunc.getProductTypeFromModuleType(mDevice.mModuleType);
			
			mDevice.mProtocolMode  = Integer.parseInt(arrays[8]);
			mDevice.mPwrStatus  = Integer.parseInt(arrays[9]);
			mDevice.mFlashMode  = Integer.parseInt(arrays[10]);
			mDevice.mColorRed   = Integer.parseInt(arrays[11]);
			mDevice.mColorGreen = Integer.parseInt(arrays[12]);
			mDevice.mColorBlue  = Integer.parseInt(arrays[13]);
			
			int timerCount = Integer.parseInt(arrays[14]);
			ArrayList<TimerDefine> timerList = new ArrayList<TimerDefine>();
			int baseIdx = 15;
		    for (int j = 0; j < timerCount; j++) {
		    	TimerDefine ti   = new TimerDefine();
		    	ti.mPlugId       = PubStatus.g_moduleId;
		    	ti.mTimerId      = Integer.parseInt(arrays[baseIdx + j * 6 + 0]);
		    	ti.mType         = Integer.parseInt(arrays[baseIdx + j * 6 + 1]);
		    	ti.mPeriod       = arrays[baseIdx + j * 6 + 2];
		    	ti.mPowerOnTime  = arrays[baseIdx + j * 6 + 3];
		    	ti.mPowerOffTime = arrays[baseIdx + j * 6 + 4];
		    	ti.mEnable       = arrays[baseIdx + j * 6 + 5].equals("1") ? true : false;
		    	timerList.add(ti);		    	
		    }	
		    mDevice.mTimer = timerList;
		    
		    str_msg = "[09]after login->array";		    
		    
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
