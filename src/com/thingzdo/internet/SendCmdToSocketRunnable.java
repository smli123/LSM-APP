package com.thingzdo.internet;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.R;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.thingzdo.processhandler.SmartPlugEventHandler;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.smartplug.AppServerReposeDefine;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;


public class SendCmdToSocketRunnable  implements Runnable{
//    private BufferedWriter  mOutStream = null;
    private Handler mSendHandler = null;
    private String mMsg = "";
    private byte[] mMsgBin = null;
	private AsyncResult mRet = new AsyncResult(); 

    public SendCmdToSocketRunnable(Handler handler, String msg) {
    	mSendHandler = handler;	
		mMsg = msg;
		mMsgBin = mMsg.getBytes();
    }
    
    public SendCmdToSocketRunnable(Handler handler, byte[] msgBin) {
    	mSendHandler = handler;	
		mMsgBin = new byte[msgBin.length];
		for (int i = 0; i < msgBin.length; i++) {
			mMsgBin[i] = msgBin[i];
		}
    }

	@Override
	public void run() {
		// lishimin TCP Socket
//		send2Server();
		
		new Thread(send_recv_thread).start();
	}
	
	private Runnable send_recv_thread = new Runnable() {
		@Override
		public void run() {
			
			try {
				PubFunc.log("send: create socket ok...");
				
				if (false == SocketMgr.createSocket("192.168.4.1", 6002)) {
					mSendHandler.sendEmptyMessage(0);
					if (null != mSendHandler) {
						mRet.mErrorId = 0;
						mRet.mMessage = "create socket fail";
						mRet.mStates = AppServerReposeDefine.Socket_Connect_FAIL;
						mSendHandler.obtainMessage(AppServerReposeDefine.Socket_Connect_FAIL, mRet).sendToTarget();
						}
					PubFunc.log("send: create socket fail");
					return;
				} else {
					PubFunc.log("send: create socket ok");
				}
				
				PubFunc.log("SEND_Tip: global_tcp: [" + 
						PubDefine.global_tcp_socket.getLocalAddress().toString() + ":" + PubDefine.global_tcp_socket.getLocalPort() + "->" +
						PubDefine.global_tcp_socket.getInetAddress().toString()  + ":" + PubDefine.global_tcp_socket.getPort() + "]");
				
				DataOutputStream outsocket = new DataOutputStream(PubDefine.global_tcp_socket.getOutputStream());
				DataInputStream insocket  = new DataInputStream(PubDefine.global_tcp_socket.getInputStream());
	            
				// Send Data 
//	            outsocket.write(mMsg.getBytes());
				outsocket.write(mMsgBin);
	            outsocket.flush();
	            PubFunc.log("SEND_TCP_M:" + mMsgBin.toString());
	            
	            // Recv Data
	            String text;
				int i_length = 0;
				byte[] buffer = new byte[2048];
			
				i_length = insocket.read(buffer);
				if (0 == i_length) {
					PubFunc.log("RECV_TCP_M:" + "");
				}
				
				text = new String(buffer);
				
				String str_tmp = text;
	        	if (-1 == str_tmp.indexOf("#")) {
	        		PubFunc.log("RECV_TCP_M:" + str_tmp);
	        	} else {
	        		PubFunc.log("RECV_TCP_M:" + str_tmp.substring(0, str_tmp.lastIndexOf("#") + 1));
	        	}

				text = text.substring(0, text.indexOf(StringUtils.PACKAGE_END_SYMBOL));
				String[] msg_buffer = text.split(StringUtils.PACKAGE_RET_SPLIT_SYMBOL);
				if (5 > msg_buffer.length) {
					PubFunc.log("RECV_TCP_M: message from module");
				}
				
				PubStatus.g_userCookie  = msg_buffer[0];
				String cmd              = msg_buffer[1];
				//PubStatus.g_CurUserName = msg_buffer[2];
				PubStatus.g_moduleId    = msg_buffer[3];
					
				String[] txt_buffer = new String[msg_buffer.length - 4];
				for (int i = 4; i < msg_buffer.length; i++) {
					txt_buffer[i - 4] = msg_buffer[i];
				}
					
				SmartPlugEventHandler handler = SmartPlugEventHandler.getInstance()
						                         .getTheEventHandler(SmartPlugMessage.getEvent(cmd));
				if (null != handler) {
					handler.obtainMessage(0, msg_buffer).sendToTarget();
				}
				msg_buffer = null;
				txt_buffer = null;
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
	
    private void send2Server() {
    	SmartPlugApplication.resetTask();
    	
    	try {
			if (null == PubDefine.global_tcp_socket) {
				PubFunc.log("SEND_Tip: " + "create socket fail.");
				if (null != mSendHandler) {
					mRet.mErrorId = 0;
					mRet.mMessage = "create socket fail";
					mRet.mStates = AppServerReposeDefine.Socket_Connect_FAIL;
					mSendHandler.obtainMessage(AppServerReposeDefine.Socket_Connect_FAIL, mRet).sendToTarget();
				}
				return;
			}
			if (!PubDefine.global_tcp_socket.isConnected()) {
				PubFunc.log("SEND_Tip: tcp socket is not connected.");
				if (null != mSendHandler) {
				mRet.mErrorId = 0;
				mRet.mMessage = "create socket fail";
				mRet.mStates = AppServerReposeDefine.Socket_Connect_FAIL;
				mSendHandler.obtainMessage(AppServerReposeDefine.Socket_Connect_FAIL, mRet).sendToTarget();
				}
				return;	
			}
			if (PubDefine.global_tcp_socket.isOutputShutdown()) {
				PubFunc.log("SEND_Tip: tcp socket's outputshutdown is true");
				if (null != mSendHandler) {
				mRet.mErrorId = 0;
				mRet.mMessage = "create socket fail";
				mRet.mStates = AppServerReposeDefine.Socket_Connect_FAIL;
				mSendHandler.obtainMessage(AppServerReposeDefine.Socket_Connect_FAIL, mRet).sendToTarget();
				}
				return;	
			}

			PubFunc.log("SEND_Tip: global_tcp: [" + 
					PubDefine.global_tcp_socket.getLocalAddress().toString() + ":" + PubDefine.global_tcp_socket.getLocalPort() + "->" +
					PubDefine.global_tcp_socket.getInetAddress().toString()  + ":" + PubDefine.global_tcp_socket.getPort() + "]");
			
//			if (null  != mOutStream) {
//				mOutStream.close();
//				mOutStream = null;
//			}
//			PubFunc.log("SEND_Tip: getOutputStream=" + PubDefine.global_tcp_socket.getOutputStream());
//			SystemClock.sleep(1000);
			
			/* --------------------------------------------------------------------*/
//			PrintWriter socketWriter = new PrintWriter(PubDefine.global_tcp_socket.getOutputStream());
//			socketWriter.write(mMsg);
//			socketWriter.flush();
//			
//        	PubFunc.log("SEND_TCP: " + mMsg);
//        	
//        	if (null != mSendHandler) {
//				mRet.mErrorId = 0;
//				mRet.mMessage = "ok";
//				mRet.mStates = AppServerReposeDefine.Socket_Send_OK;
//				mSendHandler.obtainMessage(AppServerReposeDefine.Socket_Send_OK, mRet).sendToTarget();
//			}
			
			/* --------------------------------------------------------------------*/
			
			
        	BufferedWriter mOutStream = new BufferedWriter(new OutputStreamWriter(PubDefine.global_tcp_socket.getOutputStream(),  "UTF-8"));
        
        	mOutStream.write(mMsg.replace("\n", ""));
        	mOutStream.flush();
        	
        	PubFunc.log("SEND_TCP: " + mMsg);
        	
        	if (null != mSendHandler) {
				mRet.mErrorId = 0;
				mRet.mMessage = "ok";
				mRet.mStates = AppServerReposeDefine.Socket_Send_OK;
				mSendHandler.obtainMessage(AppServerReposeDefine.Socket_Send_OK, mRet).sendToTarget();
			}
			
			mOutStream = null;
    	} catch (SocketTimeoutException se) {
    		if (null != mSendHandler) {
				mRet.mErrorId = 0;
				mRet.mMessage = se.toString();
				mRet.mStates = AppServerReposeDefine.Socket_TCP_TIMEOUT;
				mSendHandler.obtainMessage(AppServerReposeDefine.Socket_TCP_TIMEOUT, mRet).sendToTarget();
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		
//    		if (null != PubDefine.global_tcp_socket) {
//    			try {
//					PubDefine.global_tcp_socket.close();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//    			PubDefine.global_tcp_socket = null;
//    			PubFunc.log("SEND_Tip: tcp socket set to null.");
//    		}
    		
    		if (null != mSendHandler) {
				mRet.mErrorId = 0;
				mRet.mMessage = e.toString();
				mRet.mStates = AppServerReposeDefine.Socket_Connect_FAIL;
				mSendHandler.obtainMessage(AppServerReposeDefine.Socket_Connect_FAIL, mRet).sendToTarget(); 
    		}
    	}   
    }
}
