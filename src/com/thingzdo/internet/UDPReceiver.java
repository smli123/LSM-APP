package com.thingzdo.internet;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.os.Handler;

import com.thingzdo.processhandler.SmartPlugEventHandler;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.PubStatus;

public class UDPReceiver implements Runnable {
	
	private static Handler mConnectHandler = null;
	
	private static UDPReceiver mInstance = null;

	private boolean mActive = true;
	private boolean mStoped = false;
	private SmartPlugEventHandler handler = null;
	private static DatagramSocket dSocket = null;
	
	public UDPReceiver(Handler handler) {
		mConnectHandler = handler;
		mActive = true;
		mStoped = false;
	}
	
	public static DatagramSocket getUDPSocket() {
		return dSocket;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return mActive;
	}

	/**
	 * @param active
	 * the active to set
	 */
	public void setActive(boolean active) {
		this.mActive = active;
	}
	
	public void disconnect() {
		mStoped = true;
		if (null != dSocket) {
			dSocket.disconnect();
			dSocket.close();
			dSocket = null;
		}
	}

	@Override
	public void run() {
		try {
	    	if (null == dSocket) {
	    		dSocket = new DatagramSocket(PubDefine.LOCAL_PORT);
	    	}
	    } catch (BindException e) {
	    	PubFunc.log("RECV_UDP: UDP端口使用中...请重关闭程序启服务器");	            
	    } catch (SocketException e) {
	        e.printStackTrace();
	    }
        while (dSocket != null) {
        	PubFunc.log("RECV_UDP: dSocket run start");
        	byte[] buf = new byte[5096];
        	
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
            try {
            	dSocket.receive(receivePacket);
            	String str_tmp = new String(receivePacket.getData());
            	if (-1 == str_tmp.indexOf("#")) {
            		PubFunc.log("RECV_UDP:" + str_tmp);
            	} else {
            		PubFunc.log("RECV_UDP:" + str_tmp.substring(0, str_tmp.lastIndexOf("#") + 1));
//                	PubFunc.log("msg sever received(byte):" + Arrays.toString(receivePacket.getData()));
            	}
                
                String receiveData = new String(receivePacket.getData());
                
				if (-1 == receiveData.indexOf("#")) {
					buf = null;
					continue;
				}
		        receiveData = receiveData.substring(0, receiveData.indexOf("#"));
		        String[] cmds = receiveData.split(",");
		        
		        // 检查返回的命令字格式是否正确；
		        if(cmds == null || cmds.length < 5){
		        	buf = null;
					continue;
				}
		        
//		        // 方案一
//		        String[] buffer = new String[cmds.length - 4];
//		        for (int i = 4; i < cmds.length; i++) {
//		        	buffer[i - 4] = cmds[i];
//		        }
		        // 方案二		        
//		        String[] buffer = new String[cmds.length];
		        
				PubStatus.g_userCookie  = cmds[0];
				String cmd              = cmds[1];
				PubStatus.g_CurUserName = cmds[2];
				PubStatus.g_moduleId    = cmds[3];	

		        handler = SmartPlugEventHandler.getInstance()
                         .getTheEventHandler(SmartPlugMessage.getEvent(cmd));
				if (null != handler) {
//					PubFunc.log("Message: " + cmds.toString());
					handler.obtainMessage(0, cmds).sendToTarget();
				}
            } catch (IOException e) {
                e.printStackTrace();               
            }
            
            buf = null;
        }
    }
}
