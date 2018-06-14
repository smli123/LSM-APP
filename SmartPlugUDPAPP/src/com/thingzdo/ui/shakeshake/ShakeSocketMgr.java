package com.thingzdo.ui.shakeshake;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import com.thingzdo.processhandler.SmartPlugEventHandler;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import android.os.Handler;
import android.util.Log;

/*
 * 摇一摇进入详细界面进行控制
 */
public class ShakeSocketMgr {
	private final static  String Device_IP = "192.168.1.1"; 
	private static String mIP = Device_IP;
	private static String mSendMsg = "";
	private final static  int UDP_MAX_RETRIES_SEND = 2;
	
	public static void createUDPSocket(Handler handler, String ip) {
		if (null == ip || ip.isEmpty()) {
			mIP = Device_IP;
		} else {
			mIP = ip;
		}
	}
	
	static SmartPlugEventHandler handler = null;
	private static Runnable sendTo = new Runnable() {

		@Override
		public void run() {
			int tries = 0;    
			do {
				try {
					InetAddress remoteInetAddress = InetAddress.getByName(mIP); // 服务器地址
				    //发送的信息
				    byte[] bytesToSend = mSendMsg.getBytes();
	
				    DatagramSocket socket = new DatagramSocket();
				    socket.setSoTimeout(PubDefine.SOCKET_TIMEOUT);
				
				    DatagramPacket sendPacket = new DatagramPacket(bytesToSend,
				        bytesToSend.length, remoteInetAddress, PubDefine.MODULE_PORT);
				    
				    DatagramPacket receivePacket =                              	
					        new DatagramPacket(new byte[2048], 2048);
				    
				    socket.send(sendPacket);          						// 发送信息
				    Log.e("send msg:",  mSendMsg);
				    
				    socket.receive(receivePacket); 		
			        receivePacket.getAddress().getHostAddress();
			        String receiveData = new String(receivePacket.getData());
			        receiveData = receiveData.substring(0, receiveData.indexOf("#"));
			        String[] cmds = receiveData.split(",");		
			        // 检查返回的命令字格式是否正确；
			        if(cmds == null || cmds.length < 5){
						break;
					}
			        
			        String[] buffer = new String[cmds.length - 4];
			        for (int i = 4; i < cmds.length; i++) {
			        	buffer[i - 4] = cmds[i];
			        }		        
			        String cmd_name 	= cmds[1];
			        handler = SmartPlugEventHandler.getInstance()
	                         .getTheEventHandler(SmartPlugMessage.getEvent(cmd_name));
					if (null != handler) {
						PubFunc.log("udp msg:" + cmd_name.toString());
						handler.obtainMessage(0, buffer).sendToTarget();
					}	
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
					tries++;
				}			    
			} while (tries >= UDP_MAX_RETRIES_SEND);
		}
	};
	
	public static void sendCommandRequestWithUDPSocket(String str_Text) {
		mSendMsg =  str_Text;
		new Thread(sendTo).start();
	}
}
