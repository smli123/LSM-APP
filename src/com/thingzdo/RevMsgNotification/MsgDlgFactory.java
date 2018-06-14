package com.thingzdo.RevMsgNotification;

import android.content.Context;

public class MsgDlgFactory {
	long mCommand;
	Context mContext;
	
	public MsgDlgFactory(Context context, long command) {
		mCommand = command;
		mContext = context;
	}
	
	public MsgDlgInterface getMsgDlg() {
		
		MsgDlgInterface msgShow = null;
		/*switch ((int)mCommand) {
		case SmartPlugCmdSets.RECEIVE_QUERY_GPSPOSITION:
			msgShow = new LocationMsgDlg(mContext); 	
			break;
		case SmartPlugCmdSets.RECEIVE_QUERY_PHONEFEE:
			msgShow = new FeeMsgDlg(mContext); 	
			break;			
		case SmartPlugCmdSets.RECEIVE_CONTROL_NOTIGTON1:
		case SmartPlugCmdSets.RECEIVE_CONTROL_NOTDOOROPEN1:
		case SmartPlugCmdSets.RECEIVE_CONTROL_ONEKEYALERT1:
			msgShow = new EmergencyMsgDlg(mContext); 	
			break;
		default:
       	    msgShow = new NormalMsgDlg(mContext);
       	    break;
		}*/
		return msgShow;
	}
}
