package com.thingzdo.ui.control;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.manage.AddSocketActivity3;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class DetailCurtainActivity extends TitledActivity implements OnClickListener, SeekBar.OnSeekBarChangeListener{
	
	private SmartPlugHelper  mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";
	private boolean mOnline = false;
	
	private String mErrorMsg =  "";
	
	private SharedPreferences mCurtainInfo = null;
	private int value_curtain_pos = 100;
	
	// Layout widget Define
	private Button   btn_windowopen;
	private Button   btn_windowclose;
	private Button   btn_windowpause;
	private Button   btn_windowinit;
	
	private SeekBar  sb_curtain_max_length;
	private TextView tv_curtain_max_length;	
	private TextView tv_wifiinfo = null;
	
	private RevCmdFromSocketThread mTcpSocketThread = null;
	
	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(DetailCurtainActivity.this, intent)) {
					updateUI();
				}
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_CURTAIN_ACTION)) {
				timeoutHandler.removeCallbacks(timeoutProcess);				
				if (null != mProgress) {
					mProgress.dismiss();
				}
				
				String plugid = intent.getStringExtra("PLUGID");
				int result = intent.getIntExtra("RESULT", 0);
				int status = intent.getIntExtra("STATUS", 0);
				String message = intent.getStringExtra("MESSAGE");
				if (result == 0) {
//					PubFunc.thinzdoToast(DetailCurtainActivity.this, getString(R.string.oper_success));
				} else {
					PubFunc.thinzdoToast(DetailCurtainActivity.this, message);
				}
				
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_CURTAIN)) {
				if (true == NotifyProcessor.curtainNotify(DetailCurtainActivity.this, intent)) {
					updateUI();
				}
			}
			
			if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				String routeName = "";
				WifiManager Wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				if (Wifi != null && Wifi.isWifiEnabled()) {
					WifiInfo wifiInfo = Wifi.getConnectionInfo();
					if (wifiInfo != null && wifiInfo.getSSID() != null) {
						routeName = wifiInfo.getSSID().replace("\"", "");						
					}
				}
				tv_wifiinfo.setText(routeName);
	        }
			
			if (intent.getAction().equals(PubDefine.PLUG_BACK2AP_ACTION)) {
				int code = intent.getIntExtra("RESULT", 0);
				switch (code) {
					case 0:
						Button left_button = (Button) findViewById(R.id.titlebar_leftbutton);
						left_button.performClick();
						break;
					default:
						break;
				}
			}
		}
	};

	private void saveInfo() {
		SharedPreferences.Editor editor = mCurtainInfo.edit(); 
		editor.putInt(mPlugId + "_value_curtain_pos", value_curtain_pos);
		editor.commit();
	}

	private void loadInfo() {
		value_curtain_pos = mCurtainInfo.getInt(mPlugId + "_value_curtain_pos", 100);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_detail_curtain, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);		
		setTitleRightButton(R.string.smartplug_title_plug_detail, R.drawable.title_btn_selector, this);
		
		mPlugHelper = new SmartPlugHelper(this);
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";	
		} 
		mPlugIp = intent.getStringExtra("PLUGIP");
		mOnline = intent.getBooleanExtra("ONLINE", false);
		
		mCurtainInfo = getSharedPreferences("CurtainInfo" + PubStatus.g_CurUserName, Activity.MODE_PRIVATE);
		loadInfo();
		
		init();
		if (mPlug != null) {
			value_curtain_pos = mPlug.mPosition;
		}
				
		if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
			setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		} else {
			setTitleLeftButton(R.string.smartplug_esc, R.drawable.title_btn_selector, this);
		}		
		
		if (PubDefine.SmartPlug_Connect_Mode.WiFi == PubDefine.g_Connect_Mode) {
			setTitleRightButton(R.string.smartplug_mgr_reselect, R.drawable.title_btn_selector, this);
		}
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_CURTAIN_ACTION);
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_NOTIFY_CURTAIN);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(PubDefine.PLUG_BACK2AP_ACTION);
		registerReceiver(mDetailRev, filter);

		UDPClient.getInstance().setIPAddress(mPlugIp);
		
		
		
        btn_windowopen  = (Button)findViewById(R.id.btn_windowopen);
        btn_windowclose = (Button)findViewById(R.id.btn_windowclose);
        btn_windowpause = (Button)findViewById(R.id.btn_windowpause);
        btn_windowinit = (Button)findViewById(R.id.btn_windowinit);
        
        if (mOnline == true) {
        	btn_windowopen.setEnabled(true);
        	btn_windowclose.setEnabled(true);
        	btn_windowpause.setEnabled(true);
        } else {
        	btn_windowopen.setEnabled(false);
        	btn_windowclose.setEnabled(false);
        	btn_windowpause.setEnabled(false);
        }
        
        btn_windowopen.setOnClickListener(DetailCurtainActivity.this);
        btn_windowclose.setOnClickListener(DetailCurtainActivity.this);
        btn_windowpause.setOnClickListener(DetailCurtainActivity.this);
        btn_windowinit.setOnClickListener(DetailCurtainActivity.this);
        
        tv_curtain_max_length = (TextView)findViewById(R.id.tv_curtain_max_length);
        sb_curtain_max_length = (SeekBar)findViewById(R.id.sb_curtain_max_length);
        if (null != sb_curtain_max_length) {
        	sb_curtain_max_length.setProgress(value_curtain_pos);
        	sb_curtain_max_length.setOnSeekBarChangeListener(this);
        	tv_curtain_max_length.setText(String.valueOf(value_curtain_pos));
        }
		
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}
		
        // 无线路由器
		tv_wifiinfo = (TextView)findViewById(R.id.tv_wifiinfo);

		setTitle(mPlug.mPlugName);
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
	public void onProgressChanged(SeekBar bar, int pos, boolean arg2) {
		switch(bar.getId()) {
			case R.id.sb_curtain_max_length:
				tv_curtain_max_length.setText(String.valueOf(bar.getProgress()));
				break;
			default:
				break;
		}
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar bar) {
		switch (bar.getId()) {
		case R.id.sb_curtain_max_length:
			// adjust Bar's progress position. step = 5;
			adjust_progressbar(bar);
			
			value_curtain_pos = bar.getProgress();
			tv_curtain_max_length.setText(String.valueOf(value_curtain_pos));
			saveInfo();
			
			windowPos(tv_curtain_max_length.getText().toString());
			break;
		default:
			break;
		}	
	}
	
	private void adjust_progressbar(SeekBar bar) {
		int cur_pos = bar.getProgress();
		cur_pos = Math.round(cur_pos / 5) * 5;
		bar.setProgress(cur_pos);
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
				intent.putExtra("ONLINE", mOnline);
				intent.setClass(DetailCurtainActivity.this, PlugDetailInfoActivity.class);
				startActivity(intent);
			} else {
			// WiFi直连：“重选”界面
				//PubDefine.disconnect();
				disconnectSocket();
				Intent intent = new Intent();
				intent.setClass(DetailCurtainActivity.this, AddSocketActivity2.class);
				startActivity(intent);
				finish();
			}
			break;
			// Window Controller
		case R.id.btn_windowopen:
			windowopen();
			break;
		case R.id.btn_windowclose:
			windowclose();
			break;
		case R.id.btn_windowpause:
			windowpause();
			break;
		case R.id.btn_windowinit:
			windowInit();
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
		mOnline = mPlug.mIsOnline;
		
		if (mOnline == true) {
        	btn_windowopen.setEnabled(true);
        	btn_windowclose.setEnabled(true);
        	btn_windowpause.setEnabled(true);
        	
        	// 根据NotifyCurtain信息更新进度条的位置
        	value_curtain_pos = mPlug.mPosition;
        	sb_curtain_max_length.setProgress(value_curtain_pos);
			tv_curtain_max_length.setText(String.valueOf(value_curtain_pos));
			saveInfo();
        } else {
        	btn_windowopen.setEnabled(false);
        	btn_windowclose.setEnabled(false);
        	btn_windowpause.setEnabled(false);
        }
	}

	private void windowopen() {
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailCurtainActivity.this, getString(R.string.str_btn_curtainopen), false);
    	mProgress.show();
    	
		sendcommandtoWindowController("1");	
	}
	private void windowclose() {
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailCurtainActivity.this, getString(R.string.str_btn_curtainclose), false);
    	mProgress.show();
    	
		sendcommandtoWindowController("2");	
	}
	private void windowpause() {
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailCurtainActivity.this, getString(R.string.str_btn_curtainpause), false);
    	mProgress.show();
    	
		sendcommandtoWindowController("0");	
	}
	
	// For Window Controller
	private void sendcommandtoWindowController(String part_command) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_CURTAIN)
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(PubStatus.getUserName())
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(mPlugId)
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(part_command);
    	
    	//RevCmdFromSocketThread.getInstance().setRunning(true);
    	sendMsg(true, sb.toString(), true);

	}
	
/* --------------------------APPPASSTHROUGH模式------------------------------- */
	private void windowInit() {
//		mProgress = PubFunc.createProgressDialog(DetailCurtainActivity.this, getString(R.string.str_btn_curtaininit), false);
//    	mProgress.show();
    	
		//0,HUOER_INIT,smli123hz,6044712,15,0,0,BACK2AP,smli123hz,6044712#
		String command_base = "CURTAIN_INIT," + PubStatus.g_CurUserName + "," + mPlugId;
		String command = "0," + command_base;
		int command_length = command.getBytes().length + 1;

		StringBuffer sb = new StringBuffer();
    	sb.append("APPPASSTHROUGH")
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(PubStatus.g_CurUserName)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(mPlugId)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(String.valueOf(command_length))
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(command);
    	
    	if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
    		sendMsg(true, sb.toString(), true);
    	} else {
    		sendMsg(false, command_base, true);
    	}
	}

	private void windowPos(String pos_value) {
//		mProgress = PubFunc.createProgressDialog(DetailCurtainActivity.this, getString(R.string.str_introduct_curtain_max_length), false);
//    	mProgress.show();
    	
		//0,HUOER_POS,smli123hz,6044712,15,0,0,BACK2AP,smli123hz,6044712,100#
		String command_base = "CURTAIN_POS," + PubStatus.g_CurUserName + "," + mPlugId + "," + pos_value;
		String command = "0," + command_base;
		int command_length = command.getBytes().length + 1;

		StringBuffer sb = new StringBuffer();
    	sb.append("APPPASSTHROUGH")
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(PubStatus.g_CurUserName)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(mPlugId)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(String.valueOf(command_length))
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(command);
    	
    	if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
    		sendMsg(true, sb.toString(), true);
    	} else {
    		sendMsg(false, command_base, true);
    	}
	}
}
