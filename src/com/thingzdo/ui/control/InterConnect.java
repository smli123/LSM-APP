package com.thingzdo.ui.control;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.dataprovider.SmartPlugTimerHelper;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.DeviceStatus;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.login.RegisterActivity;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.wifi.SmartPlugWifiMgr;

public class InterConnect extends TitledActivity 
    implements OnClickListener{
	private Button mBtnConnect;
	private EditText mTxtIP;
	private String mPlugId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState, R.layout.activity_inputip, false);
		
		setTitleLeftButton(R.string.smartplug_cancel,R.drawable.title_btn_selector, backClick);
		setTitle(R.string.login_login);		
		
		mBtnConnect = (Button)findViewById(R.id.btn_interlogin);
		mTxtIP = (EditText)findViewById(R.id.login_ip);
		
		mBtnConnect.setOnClickListener(this);
	}
	
	private View.OnClickListener backClick = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			finish();
		}
	};		

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (R.id.btn_interlogin == view.getId()) {
			mProgress = PubFunc.createProgressDialog(InterConnect.this, getString(R.string.login_logining), false);
        	mProgress.show(); 			
			
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					SmartPlugWifiMgr.createWifiSocket(mHandler, mTxtIP.getText().toString(), 0);	
				}
				
			}, 2000);		// lishimin, ԭ��2000�����Ը�Ϊ10000���ԣ�
		}
	}
	
	private DeviceStatus mWifiDevice = null;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (null != mProgress) {
				mProgress.dismiss();
				mProgress = null;
			}				
			
			if (0 == msg.what) {
				PubFunc.thinzdoToast(InterConnect.this, 
						getString(R.string.smartplug_oper_connect_fail));
			} else {
				mWifiDevice = (DeviceStatus)msg.obj;
				mPlugId = mWifiDevice.mModuleId;				
				/*RevCmdFromSocketThread.getInstance().setRunning(true);
		    	if (null != RevCmdFromSocketThread.getInstance()) {
		    		RevCmdFromSocketThread.getInstance().setSocket(PubDefine.global_socket);
		    	}	*/
			
				new UpdateTableTask().execute();
			}
		};
	};	
	
	public class UpdateTableTask extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			addNewPlugToDB();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Intent intent = new Intent();
			intent.setClass(InterConnect.this, PlugDetailActivity.class);
			intent.putExtra("PLUGID", mPlugId);
			startActivity(intent);
			finish();			
		}
		
	};
	
	private boolean addNewPlugToDB() {
		SmartPlugHelper plugProvider = new SmartPlugHelper(this);
		plugProvider.clearSmartPlug();
		
		SmartPlugDefine plug = new SmartPlugDefine();
		plug.mUserName  = PubStatus.g_CurUserName;
		plug.mPlugName  = mTxtIP.getText().toString();
		plug.mPlugId    = mPlugId;
		plug.mIsOnline  = false;
		//plug.mIsPoweron = PubFunc.getPowerStatus(mWifiDevice.mPwrStatus).mIsPwrOn;
		//plug.mIsLighton = PubFunc.getPowerStatus(mWifiDevice.mPwrStatus).mIsLightOn;
		//plug.mIsUsbon   = PubFunc.getPowerStatus(mWifiDevice.mPwrStatus).mIsUsbOn;
		plug.mDeviceStatus = mWifiDevice.mPwrStatus;
		plug.mVersion = mWifiDevice.mVersion;
		plug.mMAC = mWifiDevice.mPlugMac;
		plug.mModuleType = mWifiDevice.mModuleType;

		plug.mSubDeviceType = PubFunc.getDeviceTypeFromModuleType(plug.mModuleType);
		plug.mSubProductType = PubFunc.getProductTypeFromModuleType(plug.mModuleType);
	    
		plug.mFlashMode = mWifiDevice.mFlashMode;
		plug.mProtocolMode = mWifiDevice.mProtocolMode;
		
		plug.mColor_R   = mWifiDevice.mColorRed;
		plug.mColor_G   = mWifiDevice.mColorGreen;
		plug.mColor_B   = mWifiDevice.mColorBlue;
		plugProvider.addSmartPlug(plug);
		
		SmartPlugTimerHelper timerHelper = new SmartPlugTimerHelper(this);
		timerHelper.clearTimer(mPlugId);
		for (int i = 0; i < mWifiDevice.mTimer.size(); i++) {
			timerHelper.addTimer(mWifiDevice.mTimer.get(i));	
		}
		
		timerHelper = null;
		plugProvider = null;		
		return true;
	}		
}
