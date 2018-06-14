package com.thingzdo.processhandler;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class smartPlugEventHandlerTVServerIRData extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_TV_IRDATA_ACTION);
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		try {
			int code = PubFunc
					.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER + 0]);
			if (2 > buffer.length) {
				mIntent.putExtra("RESULT", code);
				mIntent.putExtra("MESSAGE", SmartPlugApplication.getContext()
						.getString(R.string.smartplug_oper_aircon_server_fail));
				SmartPlugApplication.getContext().sendBroadcast(mIntent);
				return;
			}

			int status = 0; // Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);

			if (0 == code) {
				// success
				mIntent.putExtra("RESULT", 0);
				mIntent.putExtra("STATUS", status);
				int ir_type = Integer.valueOf(buffer[5]);
				String irdatas = buffer[6];
				mIntent.putExtra("IRTYPE", ir_type);
				mIntent.putExtra("IRDATA", irdatas);

			} else {
				// fail
				mIntent.putExtra("RESULT", code);
				mIntent.putExtra("STATUS", status);
				mIntent.putExtra("MESSAGE", SmartPlugApplication.getContext()
						.getString(R.string.smartplug_oper_aircon_server_fail));
				/*
				 * int resid = AppServerReposeDefine.getServerResponse(code); if
				 * (0 != resid) { mIntent.putExtra("MESSAGE",
				 * SmartPlugApplication.getContext().getString(resid)); } else {
				 * mIntent.putExtra("MESSAGE",
				 * SmartPlugApplication.getContext().
				 * getString(R.string.smartplug_oper_power_fail)); }
				 */
			}
			SmartPlugApplication.getContext().sendBroadcast(mIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
