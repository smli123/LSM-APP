package com.thingzdo.RevMsgNotification;

import java.util.ArrayList;

import com.thingzdo.ui.common.PubDefine;

import android.content.Context;
import android.os.Looper;
import android.os.Message;

public class MsgDelayThread extends Thread {
    private Context mContext = null;
    private long mCommand; 
    private String mSms;
    private ArrayList<String> mParamsArray; 
    private int mDelayTime = PubDefine.TEN_MINUTES;

    public MsgDelayThread(Context context) {
    	mContext = context;	
    }
    
    public void setParam(long command, 
            String sms, ArrayList<String> paramsArray) {
    	mCommand = command;
    	mSms = sms;
    	mParamsArray = paramsArray;    	
    }
    
    public void setDelayTime(int timeMS) {
    	mDelayTime = timeMS;	
    }
	
	@Override
    public void run() {
    	super.run();
    	Looper.prepare();
    	try {
			sleep(mDelayTime);

			NotifyHandler handler = new NotifyHandler(mContext);
		    NotifyMsgDefine define = new NotifyMsgDefine();
		    define.mCommand = mCommand;
		    define.mSms = mSms;
		    define.mParamsArray = mParamsArray;
		    Message msg = new Message();
		    msg.obj = define;
		    handler.sendMessage(msg);			   
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	Looper.loop();
    }
}
