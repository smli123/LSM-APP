package com.thingzdo.RevMsgNotification;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class NotifyHandler extends Handler {
	Context mContext = null;
	public NotifyHandler(Context context) {
		mContext = context;	
	}
	
	@Override
    public void handleMessage(Message msg) {
    	super.handleMessage(msg);
    	
    	NotifyMsgDefine info = (NotifyMsgDefine)msg.obj;
    	
    	MsgDlgFactory msgFactory = new MsgDlgFactory(mContext, info.mCommand);
    	MsgDlgInterface msgShow = msgFactory.getMsgDlg();
    	if (null != msgShow) {
    		msgShow.showDlg(info.mCommand, info.mSms, info.mParamsArray);
    		msgShow.playSound(info.mCommand);
    	}    	
    	
    }
}
