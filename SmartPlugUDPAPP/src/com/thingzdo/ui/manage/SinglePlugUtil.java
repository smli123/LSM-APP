package com.thingzdo.ui.manage;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public class SinglePlugUtil{
	private static final String TAG = "SinglePlugUtil";
	
	public static final int COLLECT_INFO_ERROR    = 130938;
	public static final int COLLECT_INFO_SUCCESS  = 1070938;
	public static final int AP_TO_STATION_ERROR   = 123938;
	public static final int AP_TO_STATION_SUCCESS = 1239399;
	public static final int AP_TO_STATION_ERROR2  = 1239400;
	
	
	public static String plugChipID = null;
	public static String macAddress = null;
	public static String plug_name	= null;
	
	private static String ssidLocal = null;
	private static String passwd = null;
	private static int    clearTimer   = 0;
	private static int    protocolmode = 0;
	private static Handler mHandler = null;
	
	private static String mCookie;
	public static void collectInfo(Handler handler){
		plugChipID = null;
		macAddress = null;
		mHandler = handler;
		PubFunc.thinzdoToastCurTime(SmartPlugApplication.getContext(), "collectInfo_before");
		new Thread(taskQuryID).start();
		PubFunc.thinzdoToastCurTime(SmartPlugApplication.getContext(), "collectInfo_after");
	}	
	public static void apToStation(Handler handler,String in_plug_name, String ssid,String pwd,int t_cleartimer, int t_protocolmode){
		plug_name = in_plug_name;
		ssidLocal = ssid;
		passwd = pwd;
		clearTimer = t_cleartimer;
		protocolmode = t_protocolmode;
		mHandler = handler;
		//new Thread(taskSetApToStation).start();
		setApToStation();
	}
	private static Runnable taskQuryID = new Runnable() {
		@Override
		public void run() {
			
			PubFunc.thinzdoToastCurTime(SmartPlugApplication.getContext(), "collectInfo_createSocket_send");

			byte[] buffer = new byte[80];
			Socket socket;
			try {
				socket = new Socket("192.168.4.1", 6002);
				socket.setSoTimeout(5000);
				PrintWriter socketWriter = new PrintWriter(
						socket.getOutputStream());
				InputStream isSocket = socket.getInputStream();
				String commandQuery = "0" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
						            + "QRYCHIPID" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
				                    + PubDefine.COMPANY_NAME + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
				                    + PubStatus.g_moduleId + StringUtils.PACKAGE_END_SYMBOL;
				socketWriter.write(commandQuery);
				socketWriter.flush();
				PubFunc.log("SEND_TCP_M:" + commandQuery);
				
				PubFunc.thinzdoToastCurTime(SmartPlugApplication.getContext(), "collectInfo_read");

				isSocket.read(buffer);				
				String retStr = new String(buffer);
				
				String str_tmp = retStr;
            	if (-1 == str_tmp.indexOf("#")) {
            		PubFunc.log("RECV_TCP_M:" + str_tmp);
            	} else {
            		PubFunc.log("RECV_TCP_M:" + str_tmp.substring(0, str_tmp.lastIndexOf("#") + 1));
            	}
            	
				retStr = retStr.substring(0, retStr.indexOf(StringUtils.PACKAGE_END_SYMBOL));
				String arrays[] = retStr.split(StringUtils.PACKAGE_RET_SPLIT_SYMBOL);
				if(arrays == null ||arrays.length < 6){
					mHandler.sendEmptyMessage(COLLECT_INFO_ERROR);
					return;
				}
				mCookie    = arrays[0];
				plugChipID = arrays[3];
				macAddress = arrays[5];
				mHandler.sendEmptyMessage(COLLECT_INFO_SUCCESS);
				socketWriter.close();
				isSocket.close();
				socket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(COLLECT_INFO_ERROR);
			} catch (IOException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(COLLECT_INFO_ERROR);
			}
		}
	};
	
	private static Runnable  taskSetApToStation = new Runnable() {  
		  
	    @Override  
	    public void run() {  
	        Socket socket = null;
			try {
				
				socket = new Socket("192.168.4.1", 6002);
				socket.setSoTimeout(5000);
			    PrintWriter socketWriter = new PrintWriter(socket.getOutputStream());			    
			    String command = "0" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
			    		       + "SETRT" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL
	                    	   + PubDefine.COMPANY_NAME + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
	                           + PubStatus.g_moduleId + StringUtils.PACKAGE_RET_SPLIT_SYMBOL			    		
			                   + ssidLocal + StringUtils.PACKAGE_RET_SPLIT_SYMBOL
	                           + passwd + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
			                   + clearTimer + StringUtils.PACKAGE_RET_SPLIT_SYMBOL		// lishimin add TCP/UDP Mode for Debug 
			                   + protocolmode + StringUtils.PACKAGE_RET_SPLIT_SYMBOL	// lishimin add plug's name
			                   + plug_name + StringUtils.PACKAGE_END_SYMBOL;
			    String successResult = "0" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
			    		            + "SETRT" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
                 	   				+ PubDefine.COMPANY_NAME + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
                 	   				+ plugChipID + StringUtils.PACKAGE_RET_SPLIT_SYMBOL + "0";	
			    socketWriter.write(command);
			    socketWriter.flush();
			    PubFunc.log("SEND_TCP_M:" + command);
			    
			    InputStream isSocket = socket.getInputStream();
				byte[] buffer = new byte[80];
				isSocket.read(buffer);
			    
				String strResult = new String(buffer);
				
				String str_tmp = strResult;
            	if (-1 == str_tmp.indexOf("#")) {
            		PubFunc.log("RECV_TCP_M:" + str_tmp);
            	} else {
            		PubFunc.log("RECV_TCP_M:" + str_tmp.substring(0, str_tmp.lastIndexOf("#") + 1));
            	}
            	
				String[] arry = strResult.split(StringUtils.PACKAGE_RET_SPLIT_SYMBOL);
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < arry.length; i++) {
					if (0 == i) {
						sb.append("0").append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL);
					} else {
						if (i == arry.length - 1) {
							sb.append(arry[i]);
						} else {
							sb.append(arry[i]).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL);
						}
					}
				}
				
				strResult = sb.toString();				
				strResult = strResult.substring(0, strResult.indexOf(StringUtils.PACKAGE_END_SYMBOL));
				if(strResult.toString().contains(successResult)){
					
//					SystemClock.sleep(1000);
					String ok = "0" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL + "OK" 
					        + StringUtils.PACKAGE_RET_SPLIT_SYMBOL + PubDefine.COMPANY_NAME 
							+ StringUtils.PACKAGE_RET_SPLIT_SYMBOL + plugChipID + StringUtils.PACKAGE_END_SYMBOL;
				    socketWriter.write(ok);
				    socketWriter.flush();					
				    PubFunc.log("SEND_TCP_M:" + ok);
				    
					mHandler.sendEmptyMessage(AP_TO_STATION_SUCCESS);
				} else {
					mHandler.sendEmptyMessage(AP_TO_STATION_ERROR);
				}

				SystemClock.sleep(1000);
				
				socketWriter.close();
			    isSocket.close();
			    socket.close();	
			} catch (UnknownHostException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(AP_TO_STATION_ERROR);
			} catch (IOException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(AP_TO_STATION_ERROR);
			} finally {
				try {
					if (socket != null) {
						socket.close();	
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    }  
	};  
	
	private static void setApToStation() {
		Socket socket = null;
		try {
			SystemClock.sleep(3000);
			
			socket = new Socket("192.168.4.1", 6002);
			socket.setSoTimeout(5000);
		    PrintWriter socketWriter = new PrintWriter(socket.getOutputStream());			    
		    String command = "0" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
		    		       + "SETRT" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL
                    	   + PubDefine.COMPANY_NAME + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
                           + PubStatus.g_moduleId + StringUtils.PACKAGE_RET_SPLIT_SYMBOL			    		
		                   + ssidLocal + StringUtils.PACKAGE_RET_SPLIT_SYMBOL
                           + passwd + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
		                   + clearTimer 
		                   + StringUtils.PACKAGE_RET_SPLIT_SYMBOL	// lishimin add TCP/UDP Mode for Debug 
		                   + protocolmode 
		                   + StringUtils.PACKAGE_RET_SPLIT_SYMBOL	// lishimin add plug's name
		                   + plug_name
		                   + StringUtils.PACKAGE_END_SYMBOL;
		    String successResult = "0" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
		    		            + "SETRT" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
             	   				+ PubDefine.COMPANY_NAME + StringUtils.PACKAGE_RET_SPLIT_SYMBOL 
             	   				+ plugChipID + StringUtils.PACKAGE_RET_SPLIT_SYMBOL + "0";	
		    socketWriter.write(command);
		    socketWriter.flush();
		    PubFunc.log("SEND_TCP_M:" + command);
		    Log.e(TAG, "ok_send[1/3]: " + command);
		    
		    InputStream isSocket = socket.getInputStream();
			byte[] buffer = new byte[80];
			isSocket.read(buffer);
		    
			String strResult = new String(buffer);
			
			String str_tmp = strResult;
        	if (-1 == str_tmp.indexOf("#")) {
        		PubFunc.log("RECV_TCP_M:" + str_tmp);
        	} else {
        		PubFunc.log("RECV_TCP_M:" + str_tmp.substring(0, str_tmp.lastIndexOf("#") + 1));
        	}
			Log.e(TAG, "ok_recv[2/3]: " + strResult);
			
			String[] arry = strResult.split(StringUtils.PACKAGE_RET_SPLIT_SYMBOL);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < arry.length; i++) {
				if (0 == i) {
					sb.append("0").append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL);
				} else {
					if (i == arry.length - 1) {
						sb.append(arry[i]);
					} else {
						sb.append(arry[i]).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL);
					}
				}
			}
			
			strResult = sb.toString();				
			strResult = strResult.substring(0, strResult.indexOf(StringUtils.PACKAGE_END_SYMBOL));
			String ok = "0" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL + "OK" 
			        + StringUtils.PACKAGE_RET_SPLIT_SYMBOL + PubDefine.COMPANY_NAME 
					+ StringUtils.PACKAGE_RET_SPLIT_SYMBOL + plugChipID + StringUtils.PACKAGE_END_SYMBOL;
			
			if(strResult.toString().contains(successResult)){
				SystemClock.sleep(1000);
			    socketWriter.write(ok);
			    socketWriter.flush();
			    PubFunc.log("SEND_TCP_M:" + ok);
			    Log.e(TAG, "ok_send[3/3]: " + ok);
			    
				mHandler.sendEmptyMessage(AP_TO_STATION_SUCCESS);
			} else {
				Log.e(TAG, "not_send[3/3]: " + ok);
				mHandler.sendEmptyMessage(AP_TO_STATION_ERROR);
			}

			SystemClock.sleep(3000);
			
			socketWriter.close();
		    isSocket.close();
		    socket.close();	
		} catch (UnknownHostException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(AP_TO_STATION_ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(AP_TO_STATION_ERROR);
		} finally {
			try {
				if (socket != null) {
					socket.close();	
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    } 		
}