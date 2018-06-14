package com.thingzdo.internet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.SystemClock;

import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;

public class SocketMgr {

	public static boolean createSocket(String ip, int port) throws IOException {
	     try {
	    	 disconnectSocket();
	    	 
	    	 PubDefine.global_tcp_socket = new Socket(ip, port);
			 PubDefine.global_tcp_socket.setSoTimeout(5000);
			 
	        return true;
	     } catch (UnknownHostException e) {
	    	 e.printStackTrace();
	     } catch (IOException e) {    
	    	 e.printStackTrace();
	     }

	     PubFunc.log("SocketMgr: create socket failed. IP=" + ip  + ", Port=" + String.valueOf(port));
	     return false;
	}
	
	public static void disconnectSocket() {
		try {
			if (PubDefine.global_tcp_socket != null) {
	    		PubFunc.log("SocketMgr: close socket");
				PubDefine.global_tcp_socket.close();
			}
			PubDefine.global_tcp_socket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
