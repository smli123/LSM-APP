package com.thingzdo.ui.control;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class DetailKettleActivity extends TitledActivity implements OnClickListener, SeekBar.OnSeekBarChangeListener{
	
	private SmartPlugHelper  mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";
	private boolean mOnline = false;
	
	private String mErrorMsg =  "";
	
	private SharedPreferences mCurtainInfo = null;
	private int value_cur_temp = 0;				// 当前温度
	private String value_cur_mode = "SHAOSHUI";	// 当前模式; SHAOSHUI,SUANNAI,HUACHA
	private int value_cur_warm_temp_pos = 80;	// 保温温度
	private int value_cur_power_pos = 400;		// 当前功率
	private int value_left_time_pos = 60;		// 保温剩余时间
	private int value_notify_temperature_pos = 40;		// 提醒温度
	private boolean value_start_baowen = true;		// 启动保温
	private boolean value_start_notify = true;		// 启动提醒
	
	// Layout widget Define
	private Button   btn_mode_shaoshui;
	private Button   btn_mode_suannai;
	private Button   btn_mode_huacha;
	private Button   btn_open;
	private Button   btn_close;
	private Button   btn_add_water;
	
	private SeekBar  sb_cur_warm_temp;
	private SeekBar  sb_cur_power;
	private SeekBar  sb_left_time;
	private SeekBar  sb_notify_temperature;
	
	private TextView tv_cur_warm_temp;	
	private TextView tv_cur_power;
	private TextView tv_current_temp;
	private TextView tv_current_mode;
	private TextView tv_notify_temperature;
	
	private CheckBox cb_cur_warm_temp = null;
	private CheckBox cb_notify_temperature = null;
	private TextView tv_left_time;
	private TextView tv_cur_left_time;
	
	private RevCmdFromSocketThread mTcpSocketThread = null;
	
	MediaPlayer mp3_player = null;
	
	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(DetailKettleActivity.this, intent)) {
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
//					PubFunc.thinzdoToast(DetailKettleActivity.this, getString(R.string.oper_success));
				} else {
					PubFunc.thinzdoToast(DetailKettleActivity.this, message);
				}
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				updateUI();
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_KETTLE)) {
				notify_ui(intent);
//				if (true == NotifyProcessor.curtainNotify(DetailKettleActivity.this, intent)) {
//					updateUI();
//				}
			}
		}
	};

	private void saveInfo() {
		SharedPreferences.Editor editor = mCurtainInfo.edit();
		editor.putInt(mPlugId + "_value_cur_temp", value_cur_temp);
		editor.putInt(mPlugId + "_value_cur_warm_temp_pos", value_cur_warm_temp_pos);
		editor.putInt(mPlugId + "_value_cur_power_pos", value_cur_power_pos);
		editor.putInt(mPlugId + "_value_left_time_pos", value_left_time_pos);
		editor.putInt(mPlugId + "_value_notify_temperature_pos", value_notify_temperature_pos);
		editor.putString(mPlugId + "_value_cur_mode", value_cur_mode);
		value_start_baowen = cb_cur_warm_temp.isChecked();
		editor.putBoolean(mPlugId + "_value_start_baowen", value_start_baowen);
		value_start_notify = cb_notify_temperature.isChecked();
		editor.putBoolean(mPlugId + "_value_notify_temperature", value_start_notify);
		editor.commit();
	}
 
	private void loadInfo() {
		value_cur_temp = mCurtainInfo.getInt(mPlugId + "_value_cur_temp", 20);
		value_cur_warm_temp_pos = mCurtainInfo.getInt(mPlugId + "_value_cur_warm_temp_pos", 80);
		value_cur_power_pos = mCurtainInfo.getInt(mPlugId + "_value_cur_power_pos", 400);
		value_left_time_pos = mCurtainInfo.getInt(mPlugId + "_value_left_time_pos", 60);
		value_notify_temperature_pos = mCurtainInfo.getInt(mPlugId + "_value_notify_temperature_pos", 40);
		value_cur_mode = mCurtainInfo.getString(mPlugId + "_value_cur_mode", "SHAOSHUI");
		value_start_baowen = mCurtainInfo.getBoolean(mPlugId + "_value_start_baowen", true);
		value_start_notify = mCurtainInfo.getBoolean(mPlugId + "_value_notify_temperature", true);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_detail_kettle, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);		
		setTitleRightButton(R.string.smartplug_title_plug_detail, R.drawable.title_btn_selector, this);
		
		mPlugHelper = new SmartPlugHelper(this);
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";	
		} 
//		mPlugIp = intent.getStringExtra("PLUGIP");
		mOnline = intent.getBooleanExtra("ONLINE", false);
		
		mCurtainInfo = getSharedPreferences("KettleInfo" + PubStatus.g_CurUserName, Activity.MODE_PRIVATE);
		loadInfo();
		
		init();
		mPlugIp = mPlug.mIPAddress;
		
		if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
			setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		} else {
			setTitleLeftButton(R.string.smartplug_esc, R.drawable.title_btn_selector, this);
		}		
		
		if (PubDefine.SmartPlug_Connect_Mode.WiFi == PubDefine.g_Connect_Mode) {
			setTitleRightButton(R.string.smartplug_mgr_reselect, R.drawable.title_btn_selector, this);
		}
		
		UDPClient.getInstance().setIPAddress(mPlugIp);
		
        btn_open  = (Button)findViewById(R.id.btn_open);
        btn_close = (Button)findViewById(R.id.btn_close);
        btn_mode_shaoshui = (Button)findViewById(R.id.btn_mode_shaoshui);
        btn_mode_suannai = (Button)findViewById(R.id.btn_mode_suannai);
        btn_mode_huacha = (Button)findViewById(R.id.btn_mode_huacha);
        
        if (mOnline == true) {
        	btn_open.setEnabled(true);
        	btn_close.setEnabled(true);
        } else {
        	btn_open.setEnabled(false);
        	btn_close.setEnabled(false);
        }
        
        // 临时增加注水命令
        btn_add_water = (Button)findViewById(R.id.btn_add_water);
        btn_add_water.setOnClickListener(DetailKettleActivity.this);
        
        btn_open.setOnClickListener(DetailKettleActivity.this);
        btn_close.setOnClickListener(DetailKettleActivity.this);
        
        btn_mode_shaoshui.setOnClickListener(DetailKettleActivity.this);
        btn_mode_suannai.setOnClickListener(DetailKettleActivity.this);
        btn_mode_huacha.setOnClickListener(DetailKettleActivity.this);
        
        tv_current_temp = (TextView)findViewById(R.id.tv_current_temp);
        tv_current_temp.setText(trans_temperature(value_cur_temp));
        tv_current_mode = (TextView)findViewById(R.id.tv_current_mode);
        tv_current_mode.setText(value_cur_mode);

        tv_cur_warm_temp = (TextView)findViewById(R.id.tv_cur_warm_temp);
        sb_cur_warm_temp = (SeekBar)findViewById(R.id.sb_cur_warm_temp);
        if (null != sb_cur_warm_temp) {
        	sb_cur_warm_temp.setProgress(value_cur_warm_temp_pos);
        	sb_cur_warm_temp.setOnSeekBarChangeListener(this);
        	tv_cur_warm_temp.setText(trans_temperature(value_cur_warm_temp_pos));
        }
        
        tv_cur_power = (TextView)findViewById(R.id.tv_cur_power);
        sb_cur_power = (SeekBar)findViewById(R.id.sb_cur_power);
        if (null != sb_cur_power) {
        	sb_cur_power.setProgress(value_cur_power_pos);
        	sb_cur_power.setOnSeekBarChangeListener(this);
        	tv_cur_power.setText(trans_power(value_cur_power_pos));
        }

        cb_cur_warm_temp = (CheckBox)findViewById(R.id.cb_cur_warm_temp);
        cb_cur_warm_temp.setChecked(value_start_baowen);
        cb_cur_warm_temp.setOnClickListener(this);

        cb_notify_temperature = (CheckBox)findViewById(R.id.cb_notify_temperature);
        cb_notify_temperature.setChecked(value_start_notify);
        cb_notify_temperature.setOnClickListener(this);
        
        tv_left_time = (TextView)findViewById(R.id.tv_left_time);
        sb_left_time = (SeekBar)findViewById(R.id.sb_left_time);
        tv_cur_left_time = (TextView)findViewById(R.id.tv_cur_left_time);
        if (null != sb_left_time) {
        	sb_left_time.setProgress(value_left_time_pos);
        	sb_left_time.setOnSeekBarChangeListener(this);
        	tv_left_time.setText(trans_time(value_left_time_pos));
        }
        
        sb_notify_temperature = (SeekBar)findViewById(R.id.sb_notify_temperature);
        tv_notify_temperature = (TextView)findViewById(R.id.tv_notify_temperature);
        if (null != sb_left_time) {
        	sb_notify_temperature.setProgress(value_notify_temperature_pos);
        	sb_notify_temperature.setOnSeekBarChangeListener(this);
        	tv_notify_temperature.setText(trans_temperature(value_notify_temperature_pos));
        }
        
        
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}
		
		setTitle(mPlug.mPlugName);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();
		init();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_NOTIFY_KETTLE);
		registerReceiver(mDetailRev, filter);
		
		// 播放声音的初始化
		if (mp3_player == null) {
			mp3_player = new MediaPlayer();
			mp3_player = MediaPlayer.create(this, R.raw.notify_temperature);
			mp3_player.setLooping(false);
		}
		
		if (value_cur_temp == 1000) {
			tv_current_temp.setText("水壶不在啦");
		} else {
			tv_current_temp.setText(trans_temperature(value_cur_temp));
		}
//		tv_current_mode.setText(value_cur_mode);
		updateUI();					// Update 当前模式
		
        cb_cur_warm_temp.setChecked(value_start_baowen);
		tv_cur_warm_temp.setText(trans_temperature(value_cur_warm_temp_pos));

		sb_left_time.setProgress(value_left_time_pos);
		tv_left_time.setText(trans_time(value_left_time_pos));
		
		tv_cur_power.setText(trans_power(value_cur_power_pos));
		sb_cur_power.setProgress(value_cur_power_pos);
		
		cb_notify_temperature.setChecked(value_start_notify);
    	sb_notify_temperature.setProgress(value_notify_temperature_pos);
    	tv_notify_temperature.setText(trans_temperature(value_notify_temperature_pos));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (mp3_player != null) {
			mp3_player.release();
			mp3_player = null;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		saveInfo();
		
		super.onDestroy();
		unregisterReceiver(mDetailRev);
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread.setRunning(false);
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar bar, int pos, boolean arg2) {
		switch(bar.getId()) {
			case R.id.sb_cur_warm_temp:
				tv_cur_warm_temp.setText(trans_temperature(bar.getProgress()));
				break;
			case R.id.sb_cur_power:
				tv_cur_power.setText(trans_power(bar.getProgress()));
				break;
			case R.id.sb_left_time:
				tv_left_time.setText(trans_time(bar.getProgress()));
				break;
			case R.id.sb_notify_temperature:
				tv_notify_temperature.setText(trans_temperature(bar.getProgress()));
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
		String str_time = "";
		switch (bar.getId()) {
		case R.id.sb_cur_warm_temp:
			// adjust Bar's progress position. step = 5;
//			adjust_progressbar(bar);
			
			value_cur_warm_temp_pos = bar.getProgress();
			tv_cur_warm_temp.setText(trans_temperature(value_cur_warm_temp_pos));
			saveInfo();
			
			kettle_baowen();
			break;
		case R.id.sb_cur_power:
			value_cur_power_pos = bar.getProgress();
			tv_cur_power.setText(trans_power(value_cur_power_pos));
			saveInfo();
			
			kettle_power();
			break;
		case R.id.sb_left_time:
			value_left_time_pos = bar.getProgress();
			str_time = trans_time(value_left_time_pos);
			tv_left_time.setText(str_time);
			saveInfo();
			
			kettle_baowen();
			break;
		case R.id.sb_notify_temperature:
			value_notify_temperature_pos = bar.getProgress();
			str_time = trans_temperature(value_notify_temperature_pos);
			tv_notify_temperature.setText(str_time);
			saveInfo();
			
			break;
		default:
			break;
		}	
	}

	private String trans_temperature(int value) {
		String str_time = "";
		str_time = String.format("%d度", value);			//℃
		return str_time;
	}

	private String trans_power(int value) {
		String str_time = "";
		str_time = String.format("%d瓦", value);
		return str_time;
	}

	
	private String trans_time(int value) {
		String str_time = "";

		int hours = value / 60;
		int seconds = value % 60;
		
		if (hours > 0) {
			str_time = String.format("%2d时%2d分", hours, seconds);
		} else {
			str_time = String.format("%2d分", seconds);
		}
		
		return str_time;
		
	}
	
	private String trans_time_detail(int value) {
		String str_time = "";

		int hours = value / 3600;
		int minuts = (value - hours * 3600) / 60;
		int seconds = value % 60;
		
//		str_time = String.format("%d:%d:%d", hours, minuts, seconds);
		
		if (hours == 0) {
			str_time = String.format("%2d:%2d", minuts, seconds);
		} else {
			str_time = String.format("%2d:%2d:%2d", hours, minuts, seconds);
		}
		
		return str_time;
		
	}
	
//	private void adjust_progressbar(SeekBar bar) {
//		int cur_pos = bar.getProgress();
//		cur_pos = Math.round(cur_pos / 5) * 5;
//		bar.setProgress(cur_pos);
//	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:  	// WiFi模式 退出时，需要close掉 TCP连接
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
				intent.setClass(DetailKettleActivity.this, PlugDetailInfoActivity.class);
				startActivity(intent);
			} else {
			// WiFi直连：“重选”界面
				//PubDefine.disconnect();
				disconnectSocket();
				Intent intent = new Intent();
				intent.setClass(DetailKettleActivity.this, AddSocketActivity2.class);
				startActivity(intent);
				finish();
			}
			break;
			// Window Controller
		case R.id.cb_cur_warm_temp:
			value_start_baowen = cb_cur_warm_temp.isChecked();
			saveInfo();
			
			kettle_baowen();
			break;
		case R.id.cb_notify_temperature:
			value_start_notify = cb_notify_temperature.isChecked();
			saveInfo();
			break;
		case R.id.btn_open:
			kettle_open();
			break;
		case R.id.btn_close:
			kettle_close();
			break;
		case R.id.btn_mode_shaoshui:
			kettle_shaoshui();
			break;
		case R.id.btn_mode_huacha:
			kettle_huacha();
			break;
		case R.id.btn_mode_suannai:
			kettle_suannai();
			break;
		case R.id.btn_add_water:
			kettle_add_water();
			break;
		default:
			break;
		}
		updateUI();
	}
	
	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}
		
		mPlug.mColor_R = 0;
		mPlug.mColor_G = 0;
		mPlug.mColor_B = 0;
		
		mPlugHelper.modifySmartPlug(mPlug);
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
        	btn_open.setEnabled(true);
        	btn_close.setEnabled(true);
        	
        	// 根据NotifyKettle信息更新进度条的位置
//        	value_cur_warm_temp_pos = mPlug.mPosition;
//        	sb_cur_warm_temp.setProgress(value_cur_warm_temp_pos);
//			tv_cur_warm_temp.setText(String.valueOf(value_cur_warm_temp_pos));
//			saveInfo();
        } else {
        	btn_open.setEnabled(false);
        	btn_close.setEnabled(false);
        }
		
		if (value_cur_mode.equals("SHAOSHUI")) {
			tv_current_mode.setText("烧水");
		} else if (value_cur_mode.equals("SUANNAI")) {
			tv_current_mode.setText("酸奶");
		} else if (value_cur_mode.equals("HUACHA")) {
			tv_current_mode.setText("花茶");
		} else  if (value_cur_mode.equals("CLOSED")) {
			tv_current_mode.setText("关闭");
		} else {
			tv_current_mode.setText("未知");
		}
	}
	
	private void notify_ui(Intent intent) {
		int value = 0;
		
		value = intent.getIntExtra("SWITCH_WARM_POWER", 0);				// 当前保温开关，0：关，1：开
		boolean result = (value == 0 ? false : true);
		cb_cur_warm_temp.setChecked(result);
		
		value = intent.getIntExtra("SWITCH_POWER", 0);					// 当前开关，0：关，1：开
//		if (value == 0) {
//			value_cur_mode = "CLOSED";
//			tv_current_mode.setText("关闭");
//		}

		value_cur_mode = intent.getStringExtra("CUR_MODE");		// 当前模式; SHAOSHUI,SUANNAI,HUACHA
		if (value_cur_mode.equals("SHAOSHUI")) {
			tv_current_mode.setText("烧水");
		} else if (value_cur_mode.equals("SUANNAI")) {
			tv_current_mode.setText("酸奶");
		} else if (value_cur_mode.equals("HUACHA")) {
			tv_current_mode.setText("花茶");
		}
		value_cur_temp = intent.getIntExtra("TEMPERATURE", 0);			// 当前温度
		if (value_cur_temp == 1000) {
			tv_current_temp.setText("水壶不在啦");
		} else {
			tv_current_temp.setText(trans_temperature(value_cur_temp));
		}
		
		value_cur_warm_temp_pos = intent.getIntExtra("WARM_TEMPERATURE", 0);		// 保温温度
		tv_cur_warm_temp.setText(trans_temperature(value_cur_warm_temp_pos));
		sb_cur_warm_temp.setProgress(value_cur_warm_temp_pos);
		
		value_cur_power_pos = intent.getIntExtra("CUR_POWER", 0);				// 当前功率
		tv_cur_power.setText(trans_power(value_cur_power_pos));
		sb_cur_power.setProgress(value_cur_power_pos);			
		
		value = intent.getIntExtra("LEFT_TIME", 0);				// 当前倒计时时常分钟
		tv_cur_left_time.setText(trans_time_detail(value));
		
		value = intent.getIntExtra("BOILED", 0);				// 当前倒计时时常分钟
		if (value == 1) {
			if (value_cur_temp <= value_notify_temperature_pos &&  (value_cur_temp >= (value_notify_temperature_pos - 2)) ) {
				notify_mp3();
			}
		}
	}
	
	private void notify_mp3() {
		// 播放声音
	    mp3_player.start();
	}

	private void kettle_open() {
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailKettleActivity.this, getString(R.string.str_btn_curtainopen), false);
    	mProgress.show();
    	
    	String command_base = "SHUIHU," + PubStatus.g_CurUserName + "," + mPlugId + "," + "1," + String.valueOf(value_cur_power_pos) + ",HUACHA";
		apppassthrought_command(command_base);
	}
	private void kettle_close() {
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailKettleActivity.this, getString(R.string.str_btn_curtainclose), false);
    	mProgress.show();
    	
    	String command_base = "SHUIHU," + PubStatus.g_CurUserName + "," + mPlugId + "," + "0," + "600" + ",HUACHA";
		apppassthrought_command(command_base);
		
		value_cur_mode = "CLOSED";
	}
	private void kettle_shaoshui() {
		value_cur_mode = "SHAOSHUI";
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailKettleActivity.this, getString(R.string.str_btn_curtainpause), false);
    	mProgress.show();

    	String command_base = "SHUIHU," + PubStatus.g_CurUserName + "," + mPlugId + "," + "1," + String.valueOf(value_cur_power_pos) + "," + value_cur_mode;
		apppassthrought_command(command_base);
	}
	private void kettle_suannai() {
		value_cur_mode = "SUANNAI";
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailKettleActivity.this, getString(R.string.str_btn_curtainpause), false);
    	mProgress.show();

    	String command_base = "SHUIHU," + PubStatus.g_CurUserName + "," + mPlugId + "," + "1," + String.valueOf(value_cur_power_pos) + "," + value_cur_mode;
		apppassthrought_command(command_base);
	}
	private void kettle_huacha() {
		value_cur_mode = "HUACHA";
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailKettleActivity.this, getString(R.string.str_btn_curtainpause), false);
    	mProgress.show();

    	String command_base = "SHUIHU," + PubStatus.g_CurUserName + "," + mPlugId + "," + "1," + String.valueOf(value_cur_power_pos) + "," + value_cur_mode;
		apppassthrought_command(command_base);
	}

	private void kettle_baowen() {
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailKettleActivity.this, getString(R.string.str_btn_curtainpause), false);
    	mProgress.show();

    	String checked = (cb_cur_warm_temp.isChecked() == true) ? "1" : "0";
    	String command_base = "BAOWEN," + PubStatus.g_CurUserName + "," + mPlugId + "," + checked + "," + String.valueOf(value_cur_warm_temp_pos) + "," + String.valueOf(value_left_time_pos);
		apppassthrought_command(command_base);
	}
	
	private void kettle_power() {
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailKettleActivity.this, getString(R.string.str_btn_curtainpause), false);
    	mProgress.show();

    	String command_base = "SHUIHU," + PubStatus.g_CurUserName + "," + mPlugId + "," + "1," + String.valueOf(value_cur_power_pos) + "," + value_cur_mode;
		apppassthrought_command(command_base);	
	}
	
	private void kettle_add_water() {
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailKettleActivity.this, getString(R.string.str_btn_curtainpause), false);
    	mProgress.show();

    	String new_moduleid = "648541";
    	String command_base = "DELEY_W," + PubStatus.g_CurUserName + "," + new_moduleid + "," + "1,3000";
		apppassthrought_command(command_base, new_moduleid);
	}

/* --------------------------APPPASSTHROUGH模式------------------------------- */
	private void apppassthrought_command(String cmd) {
		String command_base = cmd;
		String command = "0," + cmd;
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
	
	private void apppassthrought_command(String cmd, String new_moduleid) {
		String command_base = cmd;
		String command = "0," + cmd;
		int command_length = command.getBytes().length + 1;
		
		StringBuffer sb = new StringBuffer();
    	sb.append("APPPASSTHROUGH")
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(PubStatus.g_CurUserName)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(new_moduleid)
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
