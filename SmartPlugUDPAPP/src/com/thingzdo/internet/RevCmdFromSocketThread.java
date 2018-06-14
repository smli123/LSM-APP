package com.thingzdo.internet;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Handler;

import com.thingzdo.processhandler.SmartPlugEventHandler;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.smartplug.PubStatus;

/*
 * tcp Socket接收线程
 */
public class RevCmdFromSocketThread extends Thread{
	private static Handler mHandler = null; 
	private DataInputStream mInStream = null;
	private int BUFFER_SIZE = 2048;
	
	//private static RevCmdFromSocketThread mInstance = null;
	private static boolean mRuning = true;
	private AsyncResult mRet = new AsyncResult();
	private String thread_name = this.toString();
	
	/*public static RevCmdFromSocketThread getInstance() {
		if (null == mInstance) {
			mInstance = new RevCmdFromSocketThread();
			mRuning = true;
		} 
		return mInstance;
	}*/
	
	public void setHandler(Handler handler) {
		mHandler = handler;
	}
	
	public void setRunning(boolean isRunning) {
		mRuning = isRunning;	
	}
	
	public RevCmdFromSocketThread() {
		mRuning = true;
	}
	
/*	public void setSocket(Socket socket) {
		mSocket = socket;	
		try {
			if (null != mSocket) {
				mInStream = new DataInputStream(mSocket.getInputStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}*/	
	
	@Override
	public void run() {
//		if (true) {
//			return;
//		}
		
		//mIsRunning = true;
//		byte[] buffer = new byte[BUFFER_SIZE];
		
		PubFunc.log("Start recv socket. Running. !!! 0. ID=" + this.toString());
		
//		try {
//			if (null != PubDefine.global_tcp_socket) {
//				mInStream = new DataInputStream(PubDefine.global_tcp_socket.getInputStream());
//			}
//		} catch (IOException e1) {
//			e1.printStackTrace();
//			mRuning = false;
//		}
		
		while (true) {
			if (false == mRuning) {
				PubFunc.log("Exit recv socket. Killed. !!! 1. ID=" + this.toString());
				return;
			}
			
			try {
				Thread.sleep(500);
				
				if (null == PubDefine.global_tcp_socket) {
					PubFunc.log("PubDefine.global_tcp_socket == null !!! 2");
					continue;
				}
	
				if (!PubDefine.global_tcp_socket.isConnected()) {
					PubFunc.log("PubDefine.global_tcp_socket is not connected !!! 3");
					continue;
				}
			
				if (true == PubDefine.global_tcp_socket.isClosed()) {
//					try {
//						PubDefine.global_tcp_socket.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
					PubFunc.log("PubDefine.global_tcp_socket is closed. !!! 4");
					continue;
				}
				
				if (PubDefine.global_tcp_socket.isInputShutdown()) {
					PubFunc.log("PubDefine.global_tcp_socket InputStream is Shutdown !!! 5");
					continue;
				}
			
				mInStream = new DataInputStream(PubDefine.global_tcp_socket.getInputStream());
				if (null == mInStream) {
					PubFunc.log("mInStream is null !!! 6");
					continue;
				}
				
				if (mInStream.available() > 0) {
					byte[] buffer;
					buffer = new byte[mInStream.available()];
					mInStream.read(buffer);
					String text = new String(buffer, "UTF-8");
					buffer = null;
					if (-1 == text.indexOf("#")) {
						PubFunc.log("RECV_TCP:invalid cmd. " + text);
						continue;
					}
						
					String str_tmp = text;
	            	if (-1 == str_tmp.indexOf("#")) {
	            		PubFunc.log("RECV_TCP:" + str_tmp);
	            	} else {
	            		PubFunc.log("RECV_TCP:" + str_tmp.substring(0, str_tmp.lastIndexOf("#") + 1));
	            	}

					text = text.substring(0, text.indexOf(StringUtils.PACKAGE_END_SYMBOL));
					String[] msg_buffer = text.split(StringUtils.PACKAGE_RET_SPLIT_SYMBOL);
					if (5 > msg_buffer.length) {
						PubFunc.log("RECV_TCP:invalid message from module");
						continue;
					}
					PubStatus.g_userCookie  = msg_buffer[0];
					String cmd              = msg_buffer[1];
					//PubStatus.g_CurUserName = msg_buffer[2];
					PubStatus.g_moduleId    = msg_buffer[3];
						
//					String[] txt_buffer = new String[msg_buffer.length - 4];
//					for (int i = 4; i < msg_buffer.length; i++) {
//						txt_buffer[i - 4] = msg_buffer[i];
//					}
						
					SmartPlugEventHandler handler = SmartPlugEventHandler.getInstance()
							                         .getTheEventHandler(SmartPlugMessage.getEvent(cmd));
					if (null != handler) {
						handler.obtainMessage(0, msg_buffer).sendToTarget();
					}
					msg_buffer = null;
//					txt_buffer = null;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				PubFunc.log("PubDefine.global_tcp_socket get Exception !!! 7");
				continue;
			} catch (Exception e) {
				e.printStackTrace();
				PubFunc.log("PubDefine.global_tcp_socket get Exception !!! 8");
				continue;
			}
	    }
	}		
}
