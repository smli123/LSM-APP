package com.thingzdo.RevMsgNotification;

import java.util.ArrayList;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubFunc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class FeeMsgDlg extends MsgDlgInterface {

	FeeMsgDlg(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void showDlg(long command, String sms,
			ArrayList<String> paramsArray) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext); 
		if (0 < paramsArray.size()) {
			String fee = paramsArray.get(0).replace("\r", "");
			fee = fee.replace("\n", "");
			fee = PubFunc.Convert2Unicode(fee);
			fee = PubFunc.unicodeToString(fee);
			sms = sms + ":" + fee;
		}
		 
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
		mSounder.playOne();
	}

}
