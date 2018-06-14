package com.thingzdo.RevMsgNotification;

import java.util.ArrayList;

import com.thingzdo.smartplug_udp.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class NormalMsgDlg  extends MsgDlgInterface{

	NormalMsgDlg(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void showDlg(long command, String sms, ArrayList<String> paramsArray) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);  
        builder.setMessage(sms)  
               .setTitle(mContext.getString(R.string.revcmd_title))  
               .setCancelable(false)
               .setPositiveButton(mContext.getString(R.string.smartplug_ok), new DialogInterface.OnClickListener() {  
                    @Override  
                    public void onClick(DialogInterface dialog, int which) {  
  
                    }  
                });  
        show(builder); 		
	}
	
	@Override
	public void playSound(long command) {
		// TODO Auto-generated method stub
		mSounder.playOne();
	}	

}
