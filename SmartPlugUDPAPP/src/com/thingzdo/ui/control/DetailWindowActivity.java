package com.thingzdo.ui.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class DetailWindowActivity extends TitledActivity implements OnClickListener{
	
	private SmartPlugHelper  mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";
	
	private RevCmdFromSocketThread mTcpSocketThread = null;
	
	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(DetailWindowActivity.this, intent)) {
					updateUI();
				}
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(DetailWindowActivity.this, intent)) {
					updateUI();
				}
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_POWER_ACTION)) {
				// nothing to do;
			}	
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_plug_detail, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		
		setTitleRightButton(R.string.smartplug_title_plug_detail, R.drawable.title_btn_selector, this);
		
		if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
			setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		} else {
			setTitleLeftButton(R.string.smartplug_esc, R.drawable.title_btn_selector, this);
		}		
		
		if (PubDefine.SmartPlug_Connect_Mode.WiFi == PubDefine.g_Connect_Mode) {
			setTitleRightButton(R.string.smartplug_mgr_reselect, R.drawable.title_btn_selector, this);
		}
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_POWER_ACTION);
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_NOTIFY_POWER);
		registerReceiver(mDetailRev, filter);
		
		mPlugHelper = new SmartPlugHelper(this);
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";	
		} 
		mPlugIp = intent.getStringExtra("PLUGIP");
			
		UDPClient.getInstance().setIPAddress(mPlugIp);
		
		init();
		
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();
		init();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mDetailRev);
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread.setRunning(false);
		}
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:  // WiFi模式 退出时，需要close掉 TCP连接
			disconnectSocket();			
			finish();
			break;
		case R.id.titlebar_rightbutton:
			Button btn_TitleRight = (Button) findViewById(R.id.titlebar_rightbutton);
			// Internet模式：“详情”界面 
			if (btn_TitleRight.getText().equals(getString(R.string.smartplug_title_plug_detail))) {
				Intent intent = new Intent();
				intent.putExtra("PLUGID", mPlugId);
				intent.setClass(DetailWindowActivity.this, PlugDetailInfoActivity.class);
				startActivity(intent);
			} else {
			// WiFi直连：“重选”界面
				//PubDefine.disconnect();
				disconnectSocket();
				Intent intent = new Intent();
				intent.setClass(DetailWindowActivity.this, AddSocketActivity2.class);
				startActivity(intent);
				finish();
			}
			break;
		default:
			break;
		}
	}
	
	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}
	}
	
	private void disconnectSocket() {
		// WiFi 直连模式下，退出或者重选时，必须close TCP连接；
		/*if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			SmartPlugWifiMgr.disconnectSocket();	
		}*/
		
		return;
	}
	
	private void updateUI() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}		
		setTitle(mPlug.mPlugName);
	}
}
