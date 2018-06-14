package com.thingzdo.internet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.os.Handler;

import com.thingzdo.processhandler.SmartPlugEventHandler;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.smartplug.AppServerReposeDefine;

public class UDPClient {
	private static DatagramSocket dSocket = null;
	
	private static UDPClient mInstance = null;
	
	private String  mMessage = "";
	private byte[] mMessageBin = null; 
	private Handler mHandler = null;
	private static String mIP = "";
	
	/**
	 * @param msg
	 */
	private UDPClient() {
		super();
	}

	public static UDPClient getInstance() {
		if (null == mInstance) {
			mInstance = new UDPClient();
		}
		return mInstance;
	}
	
	public void setIPAddress(String ip) {
		mIP = ip;
	}
	
	
	static SmartPlugEventHandler handler = null;
	private Runnable sendRunnable  = new Runnable() {

		@Override
		public void run() {
			AsyncResult ret = new AsyncResult();
		
			InetAddress address = null;
			try {
				if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) { 
					address = InetAddress.getByName(PubDefine.THINGZDO_HOST_NAME);
				} else {
					address = InetAddress.getByName(mIP);
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
//			try {
				dSocket = UDPReceiver.getUDPSocket();   //new DatagramSocket(PubDefine.LOCAL_PORT-1); 
//			} catch (SocketException e) {
//				e.printStackTrace();
//				ret.mErrorId = 0;
//				ret.mMessage = "create socket fail";
//				ret.mStates = AppServerReposeDefine.Socket_Connect_FAIL;
//				mHandler.obtainMessage(AppServerReposeDefine.Socket_Connect_FAIL, ret).sendToTarget();	
//				//dSocket.close();
//				return;
//			}
			if (dSocket == null) {
				ret.mErrorId = 0;
				ret.mMessage = "create socket fail";
				ret.mStates = AppServerReposeDefine.Socket_Connect_FAIL;
				mHandler.obtainMessage(AppServerReposeDefine.Socket_Connect_FAIL, ret).sendToTarget();	
				return;
			}
			// 针对 Shake模式，直接下发到模块，根据模块要求，需要单独增加IP地址和端口号信息；（原因：模块UDP协议无法解析到UDP Socket的IP和Port）
			if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Shake) {
				String strLocalIP = PubDefine.global_local_ip == null ? "" : PubDefine.global_local_ip;
				String strLocalPort = String.valueOf(dSocket.getLocalPort());
				mMessage = mMessage.substring(0, mMessage.indexOf(StringUtils.PACKAGE_END_SYMBOL));
				mMessage = mMessage + "," + strLocalIP + "," + strLocalPort + StringUtils.PACKAGE_END_SYMBOL;
			}

	    	PubFunc.log("SEND_UDP:" + mMessage);
			try {
				byte[] msg_temp =  mMessage.getBytes();	// 解决 支持 中文问题
				int len = mMessage == null ? 0 : msg_temp.length;
				DatagramPacket dPacket = new DatagramPacket(mMessage.getBytes(), len, address,
						PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet ?  
								PubDefine.SERVER_PORT : PubDefine.MODULE_PORT);

				dSocket.send(dPacket);
				
				ret.mErrorId = 0;
				ret.mMessage = "OK";
				ret.mStates = AppServerReposeDefine.Socket_Send_OK;
				mHandler.obtainMessage(AppServerReposeDefine.Socket_Send_OK, ret).sendToTarget();
				
//				// 注释掉 原来的接收，统一在UDP接收线程中；
//			    DatagramPacket receivePacket =  new DatagramPacket(new byte[2048], 2048);				
//				dSocket.receive(receivePacket); 		
//		        receivePacket.getAddress().getHostAddress();
//		        String receiveData = new String(receivePacket.getData());
//		        receiveData = receiveData.substring(0, receiveData.indexOf("#"));
//		        PubFunc.log(receiveData);
//		        String[] cmds = receiveData.split(",");		
//		        // 检查返回的命令字格式是否正确；
//		        if(cmds == null || cmds.length < 5){
//		        	//dSocket.close();
//					return;
//				}
//		        
//		        String[] buffer = new String[cmds.length - 4];
//		        for (int i = 4; i < cmds.length; i++) {
//		        	buffer[i - 4] = cmds[i];
//		        }		
//		        PubStatus.g_userCookie  = cmds[0];
//		        String cmd_name 	= cmds[1];
//		        handler = SmartPlugEventHandler.getInstance()
//                         .getTheEventHandler(SmartPlugMessage.getEvent(cmd_name));
//				if (null != handler) {
//					PubFunc.log("udp msg:" + cmd_name.toString());
//					handler.obtainMessage(0, buffer).sendToTarget();
//				}
			} catch (IOException e) {
				e.printStackTrace();
				ret.mErrorId = 0;
				ret.mMessage = e.toString();
				ret.mStates = AppServerReposeDefine.Socket_Send_Fail;
				mHandler.obtainMessage(AppServerReposeDefine.Socket_Send_Fail, ret).sendToTarget(); 			
			} finally {
				//dSocket.close();
			}
		}
	};	

	/**
	 * 发送信息到服务器
	 */
	public void send(String msg, Handler handler) {
		mMessage = msg;
		mHandler = handler;
		
		new Thread(sendRunnable).start();
	}

	private Runnable sendRunnableBin  = new Runnable() {

		@Override
		public void run() {
			AsyncResult ret = new AsyncResult();

			InetAddress address = null;
			try {
				if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) { 
					address = InetAddress.getByName(PubDefine.THINGZDO_HOST_NAME);
				} else {
					address = InetAddress.getByName(mIP);
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

			dSocket = UDPReceiver.getUDPSocket();   //new DatagramSocket(PubDefine.LOCAL_PORT-1); 
			if (dSocket == null) {
				ret.mErrorId = 0;
				ret.mMessage = "create socket fail";
				ret.mStates = AppServerReposeDefine.Socket_Connect_FAIL;
				mHandler.obtainMessage(AppServerReposeDefine.Socket_Connect_FAIL, ret).sendToTarget();	
				return;
			}
//			// 针对 Shake模式，直接下发到模块，根据模块要求，需要单独增加IP地址和端口号信息；（原因：模块UDP协议无法解析到UDP Socket的IP和Port）
//			if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Shake) {
//				String strLocalIP = PubDefine.global_local_ip == null ? "" : PubDefine.global_local_ip;
//				String strLocalPort = String.valueOf(dSocket.getLocalPort());
//				mMessage = mMessage.substring(0, mMessage.indexOf(StringUtils.PACKAGE_END_SYMBOL));
//				mMessage = mMessage + "," + strLocalIP + "," + strLocalPort + StringUtils.PACKAGE_END_SYMBOL;
//			}

	    	PubFunc.log("SEND_UDP_Bin:" + mMessageBin);
			try {
				DatagramPacket dPacket = new DatagramPacket(mMessageBin, mMessageBin.length, address,
						PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet ?  
								PubDefine.SERVER_PORT : PubDefine.MODULE_PORT);

				dSocket.send(dPacket);
				
				ret.mErrorId = 0;
				ret.mMessage = "OK";
				ret.mStates = AppServerReposeDefine.Socket_Send_OK;
				mHandler.obtainMessage(AppServerReposeDefine.Socket_Send_OK, ret).sendToTarget();
				
			} catch (IOException e) {
				e.printStackTrace();
				ret.mErrorId = 0;
				ret.mMessage = e.toString();
				ret.mStates = AppServerReposeDefine.Socket_Send_Fail;
				mHandler.obtainMessage(AppServerReposeDefine.Socket_Send_Fail, ret).sendToTarget(); 			
			} finally {
				//dSocket.close();
			}
		}
	};	
	
	/**
	 * 发送二进制信息到服务器
	 */
	public void sendBin(byte[] msgBin, Handler handler) {
		mMessageBin = null;
		mMessageBin = new byte[msgBin.length];
		for (int i = 0; i < msgBin.length; i++) {
			mMessageBin[i] = msgBin[i];
		}
		mHandler = handler;
		
		new Thread(sendRunnableBin).start();
	}
	
}
