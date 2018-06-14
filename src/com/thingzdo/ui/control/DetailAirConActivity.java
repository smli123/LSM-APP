package com.thingzdo.ui.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.util.AirConSceneTimerTask;
import com.thingzdo.util.AirConTestTimerTask;
import com.thingzdo.util.AppJsonFileReader;
import com.thingzdo.util.PathView;
import com.thingzdo.util.TempControlView;
import com.thingzdo.util.ThingzdoPickerView;
import com.thingzdo.util.ThingzdoPickerView.onSelectListener; 

public class DetailAirConActivity extends TitledActivity implements OnClickListener{
	
	private SmartPlugHelper  mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";
	private String str_Current_NO = "0001";

	private Button btn_aircon_open;
	private Button btn_aircon_close;
	private Button btn_aircon_temp_descrease;
	private Button btn_aircon_temp_increase;
	private Button btn_aircon_mode_auto;
	private Button btn_aircon_mode_cool;
	private Button btn_aircon_mode_wet;
	private Button btn_aircon_mode_wind;
	private Button btn_aircon_mode_warm;
	
	private Button btn_aircon_temp_descrease_scene_1h;
	private Button btn_aircon_temp_descrease_scene_2h;
	private Button btn_aircon_temp_descrease_scene_3h;
	private Button btn_aircon_temp_descrease_scene_4h;
	private Button btn_aircon_temp_descrease_scene_5h;
	private Button btn_aircon_temp_descrease_scene_6h;
	private Button btn_aircon_temp_descrease_scene_7h;
	private Button btn_aircon_temp_descrease_scene_8h;
	private Button btn_aircon_temp_descrease_scene_9h;
	private Button btn_aircon_temp_descrease_scene_10h;
	
	private Button btn_aircon_temp_increase_scene_1h;
	private Button btn_aircon_temp_increase_scene_2h;
	private Button btn_aircon_temp_increase_scene_3h;
	private Button btn_aircon_temp_increase_scene_4h;
	private Button btn_aircon_temp_increase_scene_5h;
	private Button btn_aircon_temp_increase_scene_6h;
	private Button btn_aircon_temp_increase_scene_7h;
	private Button btn_aircon_temp_increase_scene_8h;
	private Button btn_aircon_temp_increase_scene_9h;
	private Button btn_aircon_temp_increase_scene_10h;
	
	private ImageView iv_aircon_scene_enable;
	private ImageView iv_aircon_scene_visible;
	
	private ImageView iv_aircon_scene_1h;
	private ImageView iv_aircon_scene_2h;
	private ImageView iv_aircon_scene_3h;
	private ImageView iv_aircon_scene_4h;
	private ImageView iv_aircon_scene_5h;
	private ImageView iv_aircon_scene_6h;
	private ImageView iv_aircon_scene_7h;
	private ImageView iv_aircon_scene_8h;
	private ImageView iv_aircon_scene_9h;
	private ImageView iv_aircon_scene_10h;
	
	private ImageView iv_view_show_01_mode_show;
	private ImageView iv_view_show_02_temperature;
	private ImageView iv_view_show_03_wind;
	private ImageView iv_view_show_04_increase;
	private ImageView iv_view_show_05_descease;
	private ImageView iv_view_show_06_power;
	private ImageView iv_view_show_07_mode;
	
	private boolean b_aircon_power = false;
	private int 	i_mode_status = 0;  		// 0:auto, 1:cool, 2:wet, 3:wind, 4:warm
	
	
	private LinearLayout ll_aircon_scene_hours;
	
	private boolean b_scene_name_enable = false;	
	private boolean b_scene_1h_enable = true;
	private boolean b_scene_2h_enable = true;
	private boolean b_scene_3h_enable = true;
	private boolean b_scene_4h_enable = true;
	private boolean b_scene_5h_enable = true;
	private boolean b_scene_6h_enable = true;
	private boolean b_scene_7h_enable = true;
	private boolean b_scene_8h_enable = true;
	private boolean b_scene_9h_enable = true;
	private boolean b_scene_10h_enable = true;

	private int i_scene_1h_tempature = 26;
	private int i_scene_2h_tempature = 26;
	private int i_scene_3h_tempature = 26;
	private int i_scene_4h_tempature = 26;
	private int i_scene_5h_tempature = 26;
	private int i_scene_6h_tempature = 26;
	private int i_scene_7h_tempature = 26;
	private int i_scene_8h_tempature = 26;
	private int i_scene_9h_tempature = 26;
	private int i_scene_10h_tempature = 26;

	private TextView tv_aircon_select_content;
	private TextView tv_aircon_temperature_scene_1h;
	private TextView tv_aircon_temperature_scene_2h;
	private TextView tv_aircon_temperature_scene_3h;
	private TextView tv_aircon_temperature_scene_4h;
	private TextView tv_aircon_temperature_scene_5h;
	private TextView tv_aircon_temperature_scene_6h;
	private TextView tv_aircon_temperature_scene_7h;
	private TextView tv_aircon_temperature_scene_8h;
	private TextView tv_aircon_temperature_scene_9h;
	private TextView tv_aircon_temperature_scene_10h;

	PathView pv_aircon_scene_pathview;
	
	private ThingzdoPickerView pv_AirCon_NO;
	private TextView tv_aircon_temperature;
	private int i_current_temperature = 26;
	
	private RevCmdFromSocketThread mTcpSocketThread = null;
	
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;
	
	private final static String IR_AirCon_FileName = "IR_AirCon.json";
	private List<Map<String, Object>> m_tjData = new ArrayList<Map<String, Object>>();
	private Set<String> m_idSet = new HashSet<String>();
	private List<String> list_NO_data = new ArrayList<String>();
	
	MediaPlayer mp3_player = null;

	private Timer timer_scene = null;
	private AirConSceneTimerTask timer_scene_task = null;
	private Timer timer_test = null;
	private AirConTestTimerTask timer_test_task = null;
	
	private String[] str_timer_commands = new String[10];
	
	private int[] int_temperature_res = new int[15];
	private int[] int_mode_res = new int[5];
	
	// 测试温度控制旋转控件
	private TempControlView tempControl;
	
	public String[] getTimerCommand() {
		return str_timer_commands;
	}
	
	public void StartSceneTimer() {
		timer_scene = new Timer();
		timer_scene_task = new AirConSceneTimerTask(this);
		timer_scene.schedule(timer_scene_task, 1000, 1000*60*60);	// 1Hours
	}
	
	public void StopSceneTimer() {
		if (timer_scene_task != null) {
			timer_scene_task.cancel();
		}
		if (timer_scene != null) {
			timer_scene.cancel();
		}
		timer_scene_task = null;
		timer_scene = null;
	}
	
	public void StartTestTimer() {
		timer_test = new Timer();
		timer_test_task = new AirConTestTimerTask(this);
		timer_test.schedule(timer_test_task, 1000, 1000*60);	// 1Hours
	}
	
	public void StopTestTimer() {
		if (timer_test_task != null) {
			timer_test_task.cancel();
		}
		if (timer_test != null) {
			timer_test.cancel();
		}
		timer_test_task = null;
		timer_test = null;
	}
	
	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(DetailAirConActivity.this, intent)) {
					updateUI();
				}
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(DetailAirConActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_AIRCON_IRDATA_ACTION)) {
				String strirdata = intent.getStringExtra("IRDATA");
				String[] irdata = strirdata.split("@");
				m_idSet.clear();
				for (int i = 0; i < irdata.length; i++) {
					m_idSet.add(irdata[i]);
				}
				
				// after get IR data from server, must init ir_data;
				init_IR_Data();
			}

			if (intent.getAction().equals(PubDefine.PLUG_AIRCON_SERVER_ACTION)) {
				int status = intent.getIntExtra("STATUS", -1);
				PubFunc.log("发射红外数据: " + status);
				PubFunc.thinzdoToast(context, "发射红外数据: " + String.valueOf(status));
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_POWER_ACTION)) {
				// nothing to do;
			}	
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_detail_aircon, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		
		//setTitleRightButton(R.string.smartplug_title_plug_detail, R.drawable.title_btn_selector, this);
		
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
//		if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
//			setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
//		} else {
//			setTitleLeftButton(R.string.smartplug_esc, R.drawable.title_btn_selector, this);
//		}		
		
//		if (PubDefine.SmartPlug_Connect_Mode.WiFi == PubDefine.g_Connect_Mode) {
//			setTitleRightButton(R.string.smartplug_mgr_reselect, R.drawable.title_btn_selector, this);
//		}
		
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";	
		} 
		mPlugIp = intent.getStringExtra("PLUGIP");
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_POWER_ACTION);
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_NOTIFY_POWER);
		filter.addAction(PubDefine.PLUG_AIRCON_IRDATA_ACTION);
		filter.addAction(PubDefine.PLUG_AIRCON_SERVER_ACTION);
		registerReceiver(mDetailRev, filter);
		
		mPlugHelper = new SmartPlugHelper(this);
		
		mSharedPreferences = getSharedPreferences("IR_AIRCON" + PubStatus.g_CurUserName, Activity.MODE_PRIVATE);
			
		UDPClient.getInstance().setIPAddress(mPlugIp);
		
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}
		
		// 方法一、读取IR_AirCon红外数据
//		String jsonStr = AppJsonFileReader.getJson(getBaseContext(), IR_AirCon_FileName);
//		parseJSONWithGSON(jsonStr);
		
		// 方法二、从服务器获取m_idSet 空调数据
		getServerIRData();
	}

	private void getServerIRData() {
		StringBuffer sb = new StringBuffer();
//    	sb.append(SmartPlugMessage.CMD_QRY_IRDATA)
//    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
//    	  .append(PubStatus.g_CurUserName)
//    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
//    	  .append(mPlugId);
//    	
    	//sb.append("APPQUERYSCENE,smli123hz,648495");
    	sb.append("APPMODIFYSCENE,smli123hz,648495,5,1,1,1,1,30,16:11,1111111,创维,1");
    	
    	sendMsg(true, sb.toString(), true);
	}
	
	private void saveData() {
		editor = mSharedPreferences.edit();
		editor.putString("STRING_ID_" + mPlugId, str_Current_NO);
		editor.putInt("INT_TEMPERATURE_" + mPlugId, i_current_temperature);
		
		editor.putInt("I_SCENE_1H_TEMPATURE" + mPlugId, i_scene_1h_tempature);
		editor.putInt("I_SCENE_2H_TEMPATURE" + mPlugId, i_scene_2h_tempature);
		editor.putInt("I_SCENE_3H_TEMPATURE" + mPlugId, i_scene_3h_tempature);
		editor.putInt("I_SCENE_4H_TEMPATURE" + mPlugId, i_scene_4h_tempature);
		editor.putInt("I_SCENE_5H_TEMPATURE" + mPlugId, i_scene_5h_tempature);
		editor.putInt("I_SCENE_6H_TEMPATURE" + mPlugId, i_scene_6h_tempature);
		editor.putInt("I_SCENE_7H_TEMPATURE" + mPlugId, i_scene_7h_tempature);
		editor.putInt("I_SCENE_8H_TEMPATURE" + mPlugId, i_scene_8h_tempature);
		editor.putInt("I_SCENE_9H_TEMPATURE" + mPlugId, i_scene_9h_tempature);
		editor.putInt("I_SCENE_10H_TEMPATURE" + mPlugId, i_scene_10h_tempature);
		
		editor.putBoolean("B_SCENE_NAME_ENABLE" + mPlugId, false); 				//b_scene_name_enable);
		editor.putBoolean("B_SCENE_1H_ENABLE" + mPlugId, b_scene_1h_enable);
		editor.putBoolean("B_SCENE_2H_ENABLE" + mPlugId, b_scene_2h_enable);
		editor.putBoolean("B_SCENE_3H_ENABLE" + mPlugId, b_scene_3h_enable);
		editor.putBoolean("B_SCENE_4H_ENABLE" + mPlugId, b_scene_4h_enable);
		editor.putBoolean("B_SCENE_5H_ENABLE" + mPlugId, b_scene_5h_enable);
		editor.putBoolean("B_SCENE_6H_ENABLE" + mPlugId, b_scene_6h_enable);
		editor.putBoolean("B_SCENE_7H_ENABLE" + mPlugId, b_scene_7h_enable);
		editor.putBoolean("B_SCENE_8H_ENABLE" + mPlugId, b_scene_8h_enable);
		editor.putBoolean("B_SCENE_9H_ENABLE" + mPlugId, b_scene_9h_enable);
		editor.putBoolean("B_SCENE_10H_ENABLE" + mPlugId, b_scene_10h_enable);
		
		editor.commit();
	}

	private void loadData() {
		str_Current_NO = mSharedPreferences.getString("STRING_ID_" + mPlugId, "0001");
		
		i_scene_1h_tempature = mSharedPreferences.getInt("I_SCENE_1H_TEMPATURE" + mPlugId, 26);
		i_scene_2h_tempature = mSharedPreferences.getInt("I_SCENE_2H_TEMPATURE" + mPlugId, 26);
		i_scene_3h_tempature = mSharedPreferences.getInt("I_SCENE_3H_TEMPATURE" + mPlugId, 26);
		i_scene_4h_tempature = mSharedPreferences.getInt("I_SCENE_4H_TEMPATURE" + mPlugId, 26);
		i_scene_5h_tempature = mSharedPreferences.getInt("I_SCENE_5H_TEMPATURE" + mPlugId, 26);
		i_scene_6h_tempature = mSharedPreferences.getInt("I_SCENE_6H_TEMPATURE" + mPlugId, 26);
		i_scene_7h_tempature = mSharedPreferences.getInt("I_SCENE_7H_TEMPATURE" + mPlugId, 26);
		i_scene_8h_tempature = mSharedPreferences.getInt("I_SCENE_8H_TEMPATURE" + mPlugId, 26);
		i_scene_9h_tempature = mSharedPreferences.getInt("I_SCENE_9H_TEMPATURE" + mPlugId, 26);
		i_scene_10h_tempature = mSharedPreferences.getInt("I_SCENE_10H_TEMPATURE" + mPlugId, 26);

		b_scene_name_enable = mSharedPreferences.getBoolean("B_SCENE_NAME_ENABLE" + mPlugId, false);
		b_scene_1h_enable = mSharedPreferences.getBoolean("B_SCENE_1H_ENABLE" + mPlugId, false);
		b_scene_2h_enable = mSharedPreferences.getBoolean("B_SCENE_2H_ENABLE" + mPlugId, false);
		b_scene_3h_enable = mSharedPreferences.getBoolean("B_SCENE_3H_ENABLE" + mPlugId, false);
		b_scene_4h_enable = mSharedPreferences.getBoolean("B_SCENE_4H_ENABLE" + mPlugId, false);
		b_scene_5h_enable = mSharedPreferences.getBoolean("B_SCENE_5H_ENABLE" + mPlugId, false);
		b_scene_6h_enable = mSharedPreferences.getBoolean("B_SCENE_6H_ENABLE" + mPlugId, false);
		b_scene_7h_enable = mSharedPreferences.getBoolean("B_SCENE_7H_ENABLE" + mPlugId, false);
		b_scene_8h_enable = mSharedPreferences.getBoolean("B_SCENE_8H_ENABLE" + mPlugId, false);
		b_scene_9h_enable = mSharedPreferences.getBoolean("B_SCENE_9H_ENABLE" + mPlugId, false);
		b_scene_10h_enable = mSharedPreferences.getBoolean("B_SCENE_10H_ENABLE" + mPlugId, false);
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();
		init();
		
		// 播放声音的初始化
		if (mp3_player == null) {
			mp3_player = new MediaPlayer();
			mp3_player = MediaPlayer.create(this, R.raw.aircondi);
			mp3_player.setLooping(false);
				
		}
		
		// 测试模块红外插件（开启定时），测试临时使用，必须关闭
		//StartTestTimer();
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
		saveData();
		
		super.onDestroy();
		unregisterReceiver(mDetailRev);
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread.setRunning(false);
		}
		StopSceneTimer();
		
		// 测试模块红外插件（关闭定时），测试临时使用，必须关闭
		//StopTestTimer();

	}
	
	private void updatePath() {
		int[] temp_data = null;
//		if (b_scene_name_enable == false) {
//			temp_data = new int[]{0,0,0,0,0,0,0,0,0,0};
//		} else {
			temp_data = new int[10];
			temp_data[0] = b_scene_1h_enable ?  i_scene_1h_tempature : 0;
			temp_data[1] = b_scene_2h_enable ?  i_scene_2h_tempature : 0;
			temp_data[2] = b_scene_3h_enable ?  i_scene_3h_tempature : 0;
			temp_data[3] = b_scene_4h_enable ?  i_scene_4h_tempature : 0;
			temp_data[4] = b_scene_5h_enable ?  i_scene_5h_tempature : 0;
			temp_data[5] = b_scene_6h_enable ?  i_scene_6h_tempature : 0;
			temp_data[6] = b_scene_7h_enable ?  i_scene_7h_tempature : 0;
			temp_data[7] = b_scene_8h_enable ?  i_scene_8h_tempature : 0;
			temp_data[8] = b_scene_9h_enable ?  i_scene_9h_tempature : 0;
			temp_data[9] = b_scene_10h_enable ?  i_scene_10h_tempature : 0;
//		}
		
		for (int i = 0; i < temp_data.length; i++) {
			if (temp_data[i] == 0)
				str_timer_commands[i] = "关闭空调";
			else
				str_timer_commands[i] = String.valueOf(temp_data[i]) + "度";
		}

		pv_aircon_scene_pathview.setLinkPaintColor(b_scene_name_enable? Color.BLUE : Color.GRAY);
		pv_aircon_scene_pathview.setDate(temp_data);
	}

	private void temperature_increase() {
		parsecommand("增加温度");
		updateImageTemperature();
	}
	
	private void temperature_descrease() {
		parsecommand("减少温度");
		updateImageTemperature();
	}
	
	private void aircon_power() {
		parsecommand(b_aircon_power ? "打开空调" : "关闭空调");
		b_aircon_power = !b_aircon_power;
	}
	
	private void aircon_mode() {
		String str_mode = "模式自动";
		switch (i_mode_status) {
			case 0:  // AUTO
				str_mode = "模式自动";
				break;
			case 1:  // COOL
				str_mode = "模式冷风";
				break;
			case 2:  // WET
				str_mode = "模式除湿";
				break;
			case 3:  // WIND
				str_mode = "模式送风";
				break;
			case 4:  // WARM
				str_mode = "模式暖气";
				break;
			default:
				break;
		}
		
		parsecommand(str_mode);
		updateImageMode();
		i_mode_status = (i_mode_status + 1) % 5;
	}
	
	private void updateImageMode() {
		iv_view_show_01_mode_show.setBackgroundResource(int_mode_res[i_mode_status]);
	}
	
	private void updateImageTemperature() {
		iv_view_show_02_temperature.setBackgroundResource(int_temperature_res[i_current_temperature - 16]);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:  // WiFi模式 退出时，需要close掉 TCP连接
			disconnectSocket();
			finish();
			break;
		// 测试按钮
		case R.id.btn_aircon_test:
			Button btn_aircon_test = (Button)findViewById(R.id.btn_aircon_test);
			parsecommand("电视静音");
			break;
		case R.id.iv_view_show_04_increase:
			temperature_increase();
			break;
		case R.id.iv_view_show_05_descease:
			temperature_descrease();
			break;
		case R.id.iv_view_show_06_power:
			aircon_power();
			break;
		case R.id.iv_view_show_07_mode:
			aircon_mode();
			break;
		case R.id.tv_aircon_select_content:
			if (pv_AirCon_NO.getVisibility() == View.VISIBLE)
				pv_AirCon_NO.setVisibility(View.GONE);
			else
				pv_AirCon_NO.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_aircon_scene_visible:
			if (ll_aircon_scene_hours.getVisibility() == View.VISIBLE)
				ll_aircon_scene_hours.setVisibility(View.GONE);
			else
				ll_aircon_scene_hours.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_aircon_scene_enable:
			iv_aircon_scene_enable.setBackgroundResource(b_scene_name_enable? R.drawable.smp_switcher_close : R.drawable.smp_switcher_open);
			b_scene_name_enable = !b_scene_name_enable;
			updatePath();
			
			if (b_scene_name_enable) {
				StartSceneTimer();
			} else {
				StopSceneTimer();
				
				parsecommand("关闭空调");
				mTimerHandler.sendEmptyMessage(11);   // Init Configurature.
			}
			
			break;
		case R.id.iv_aircon_scene_1h:
			iv_aircon_scene_1h.setBackgroundResource(b_scene_1h_enable? R.drawable.smp_switcher_close : R.drawable.smp_switcher_open);
			b_scene_1h_enable = !b_scene_1h_enable;
			updatePath();
			break;
		case R.id.iv_aircon_scene_2h:
			iv_aircon_scene_2h.setBackgroundResource(b_scene_2h_enable? R.drawable.smp_switcher_close : R.drawable.smp_switcher_open);
			b_scene_2h_enable = !b_scene_2h_enable;
			updatePath();
			break;		
		case R.id.iv_aircon_scene_3h:
				iv_aircon_scene_3h.setBackgroundResource(b_scene_3h_enable? R.drawable.smp_switcher_close : R.drawable.smp_switcher_open);
				b_scene_3h_enable = !b_scene_3h_enable;
				updatePath();
			break;		
		case R.id.iv_aircon_scene_4h:
			iv_aircon_scene_4h.setBackgroundResource(b_scene_4h_enable? R.drawable.smp_switcher_close : R.drawable.smp_switcher_open);
			b_scene_4h_enable = !b_scene_4h_enable;
			updatePath();
		break;		
		case R.id.iv_aircon_scene_5h:
			iv_aircon_scene_5h.setBackgroundResource(b_scene_5h_enable? R.drawable.smp_switcher_close : R.drawable.smp_switcher_open);
			b_scene_5h_enable = !b_scene_5h_enable;
			updatePath();
		break;		
		case R.id.iv_aircon_scene_6h:
			iv_aircon_scene_6h.setBackgroundResource(b_scene_6h_enable? R.drawable.smp_switcher_close : R.drawable.smp_switcher_open);
			b_scene_6h_enable = !b_scene_6h_enable;
			updatePath();
		break;		
		case R.id.iv_aircon_scene_7h:
			iv_aircon_scene_7h.setBackgroundResource(b_scene_7h_enable? R.drawable.smp_switcher_close : R.drawable.smp_switcher_open);
			b_scene_7h_enable = !b_scene_7h_enable;
			updatePath();
		break;		
		case R.id.iv_aircon_scene_8h:
			iv_aircon_scene_8h.setBackgroundResource(b_scene_8h_enable? R.drawable.smp_switcher_close : R.drawable.smp_switcher_open);
			b_scene_8h_enable = !b_scene_8h_enable;
			updatePath();
		break;		
		case R.id.iv_aircon_scene_9h:
			iv_aircon_scene_9h.setBackgroundResource(b_scene_9h_enable? R.drawable.smp_switcher_close : R.drawable.smp_switcher_open);
			b_scene_9h_enable = !b_scene_9h_enable;
			updatePath();
		break;		
		case R.id.iv_aircon_scene_10h:
			iv_aircon_scene_10h.setBackgroundResource(b_scene_10h_enable? R.drawable.smp_switcher_close : R.drawable.smp_switcher_open);
			b_scene_10h_enable = !b_scene_10h_enable;
			updatePath();
		break;
		
		
		case R.id.btn_aircon_temp_descrease_scene_1h:
			 if (i_scene_1h_tempature > 16)
				 i_scene_1h_tempature--;
			 tv_aircon_temperature_scene_1h.setText(String.valueOf(i_scene_1h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_descrease_scene_2h:
			 if (i_scene_2h_tempature > 16)
				 i_scene_2h_tempature--;
			 tv_aircon_temperature_scene_2h.setText(String.valueOf(i_scene_2h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_descrease_scene_3h:
			 if (i_scene_3h_tempature > 16)
				 i_scene_3h_tempature--;
			 tv_aircon_temperature_scene_3h.setText(String.valueOf(i_scene_3h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_descrease_scene_4h:
			 if (i_scene_4h_tempature > 16)
				 i_scene_4h_tempature--;
			 tv_aircon_temperature_scene_4h.setText(String.valueOf(i_scene_4h_tempature));
				updatePath();
			break;   
		case R.id.btn_aircon_temp_descrease_scene_5h:
			 if (i_scene_5h_tempature > 16)
				 i_scene_5h_tempature--;
			 tv_aircon_temperature_scene_5h.setText(String.valueOf(i_scene_5h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_descrease_scene_6h:
			 if (i_scene_6h_tempature > 16)
				 i_scene_6h_tempature--;
			 tv_aircon_temperature_scene_6h.setText(String.valueOf(i_scene_6h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_descrease_scene_7h:
			 if (i_scene_7h_tempature > 16)
				 i_scene_7h_tempature--;
			 tv_aircon_temperature_scene_7h.setText(String.valueOf(i_scene_7h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_descrease_scene_8h:
			 if (i_scene_8h_tempature > 16)
				 i_scene_8h_tempature--;
			 tv_aircon_temperature_scene_8h.setText(String.valueOf(i_scene_8h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_descrease_scene_9h:
			 if (i_scene_9h_tempature > 16)
				 i_scene_9h_tempature--;
			 tv_aircon_temperature_scene_9h.setText(String.valueOf(i_scene_9h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_descrease_scene_10h:
			 if (i_scene_10h_tempature > 16)
				 i_scene_10h_tempature--;
			 tv_aircon_temperature_scene_10h.setText(String.valueOf(i_scene_10h_tempature));
				updatePath();
			break;


		case R.id.btn_aircon_temp_increase_scene_1h:
			 if (i_scene_1h_tempature < 30)
				 i_scene_1h_tempature++;
			 tv_aircon_temperature_scene_1h.setText(String.valueOf(i_scene_1h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_increase_scene_2h:
			 if (i_scene_2h_tempature < 30)
				 i_scene_2h_tempature++;
			 tv_aircon_temperature_scene_2h.setText(String.valueOf(i_scene_2h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_increase_scene_3h:
			 if (i_scene_3h_tempature < 30)
				 i_scene_3h_tempature++;
			 tv_aircon_temperature_scene_3h.setText(String.valueOf(i_scene_3h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_increase_scene_4h:
			 if (i_scene_4h_tempature < 30)
				 i_scene_4h_tempature++;
			 tv_aircon_temperature_scene_4h.setText(String.valueOf(i_scene_4h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_increase_scene_5h:
			 if (i_scene_5h_tempature < 30)
				 i_scene_5h_tempature++;
			 tv_aircon_temperature_scene_5h.setText(String.valueOf(i_scene_5h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_increase_scene_6h:
			 if (i_scene_6h_tempature < 30)
				 i_scene_6h_tempature++;
			 tv_aircon_temperature_scene_6h.setText(String.valueOf(i_scene_6h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_increase_scene_7h:
			 if (i_scene_7h_tempature < 30)
				 i_scene_7h_tempature++;
			 tv_aircon_temperature_scene_7h.setText(String.valueOf(i_scene_7h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_increase_scene_8h:
			 if (i_scene_8h_tempature < 30)
				 i_scene_8h_tempature++;
			 tv_aircon_temperature_scene_8h.setText(String.valueOf(i_scene_8h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_increase_scene_9h:
			 if (i_scene_9h_tempature < 30)
				 i_scene_9h_tempature++;
			 tv_aircon_temperature_scene_9h.setText(String.valueOf(i_scene_9h_tempature));
				updatePath();
			break;
		case R.id.btn_aircon_temp_increase_scene_10h:
			 if (i_scene_10h_tempature < 30)
				 i_scene_10h_tempature++;
			 tv_aircon_temperature_scene_10h.setText(String.valueOf(i_scene_10h_tempature));
				updatePath();
			break;
			
		case R.id.btn_aircon_open:
			parsecommand("打开空调");
			break;
		case R.id.btn_aircon_close:
			parsecommand("关闭空调");
			break;
		case R.id.btn_aircon_temp_descrease:
			parsecommand("减少温度");
			break;
		case R.id.btn_aircon_temp_increase:
			parsecommand("增加温度");
			break;
		case R.id.btn_aircon_mode_auto:
			parsecommand("模式自动");
			break;
		case R.id.btn_aircon_mode_cool:
			parsecommand("模式冷风");
			break;
		case R.id.btn_aircon_mode_wet:
			parsecommand("模式除湿");
			break;
		case R.id.btn_aircon_mode_wind:
			parsecommand("模式送风");
			break;
		case R.id.btn_aircon_mode_warm:
			parsecommand("模式暖气");
			break;
		default:
			break;
		}
	}
	
	private String ReadCurrentAR(String id, String sub_id) {
		for (int i = 0; i < m_tjData.size(); i++) {
			if (m_tjData.get(i).get("id").toString().equals(id) && m_tjData.get(i).get("sub_id").toString().equals(sub_id)) {
				return m_tjData.get(i).get("value").toString();
			}
		}
		return null;
	}

	// APP 下发红外命令给serveer，server下发具体的红外数据给模块
	public void parsecommand(String cmd_name) {
		// 播放声音
	    mp3_player.start();

	    String power_on 		= "063";
		String power_off 		= "064";
		String mode_auto 		= "001";
		String mode_cool 		= "002";
		String mode_wet 		= "003";
		String mode_wind 		= "004";
		String mode_warm 		= "005";
		String temperatures_16 	= "048";
		String temperatures_17 	= "049";
		String temperatures_18 	= "050";
		String temperatures_19 	= "051";
		String temperatures_20 	= "052";
		String temperatures_21 	= "053";
		String temperatures_22 	= "054";
		String temperatures_23 	= "055";
		String temperatures_24 	= "056";
		String temperatures_25 	= "057";
		String temperatures_26 	= "058";
		String temperatures_27 	= "059";
		String temperatures_28 	= "060";
		String temperatures_29 	= "061";
		String temperatures_30 	= "062";
		
		// 测试数据
		String tv_mute = "5900,1950,550,1100,600,1050,550,550,550,550,550,1100,600,500,600,1050,550,550,550,600,550,1050,600,550,600,1050,600,1000,600,550,550,1050,600,550,550,1050,600,550,550,1050,600,1050,600,550,550,550,550,1100,550,550,3600,3550,600,550,600,500,550,1100,550,550,600,1050,550,600,550,1050,550,600,550,550,600,1050,550,550,600,500,600,1000,650,1050,550,550,550,1100,600,29914";
		
		String temperatures[] = {temperatures_16, temperatures_17, temperatures_18, temperatures_19, temperatures_20, temperatures_21, temperatures_22, temperatures_23, temperatures_24, temperatures_25, temperatures_26, temperatures_27, temperatures_28, temperatures_29, temperatures_30};
		power_on = temperatures[i_current_temperature - 16];
		
		 String command_com = "";
		 if (cmd_name.equalsIgnoreCase("打开空调")) {
			 command_com = power_on;
		 } else if (cmd_name.equalsIgnoreCase("关闭空调")) {
			 command_com = power_off;
		 } else if (cmd_name.equalsIgnoreCase("减少温度")) {
			 if (i_current_temperature > 16)
				 i_current_temperature--;
			 tv_aircon_temperature.setText(String.valueOf(i_current_temperature));
			 command_com = temperatures[i_current_temperature - 16];
		 } else if (cmd_name.equalsIgnoreCase("增加温度")) {
			 if (i_current_temperature < 30)
				 i_current_temperature++;
			 tv_aircon_temperature.setText(String.valueOf(i_current_temperature));
			 command_com = temperatures[i_current_temperature - 16];
		 } else if (cmd_name.equalsIgnoreCase("模式自动")) {
			 command_com = mode_auto;
		 } else if (cmd_name.equalsIgnoreCase("模式冷风")) {
			 command_com = mode_cool;
		 } else if (cmd_name.equalsIgnoreCase("模式除湿")) {
			 command_com = mode_wet;
		 } else if (cmd_name.equalsIgnoreCase("模式送风")) {
			 command_com = mode_wind;
		 } else if (cmd_name.equalsIgnoreCase("模式暖气")) {
			 command_com = mode_warm;
		 } else if (cmd_name.equalsIgnoreCase("16度")) {
			 command_com = temperatures_16;
		 } else if (cmd_name.equalsIgnoreCase("17度")) {
			 command_com = temperatures_17;
		 } else if (cmd_name.equalsIgnoreCase("18度")) {
			 command_com = temperatures_18;
		 } else if (cmd_name.equalsIgnoreCase("19度")) {
			 command_com = temperatures_19;
		 } else if (cmd_name.equalsIgnoreCase("20度")) {
			 command_com = temperatures_20;
		 } else if (cmd_name.equalsIgnoreCase("21度")) {
			 command_com = temperatures_21;
		 } else if (cmd_name.equalsIgnoreCase("22度")) {
			 command_com = temperatures_22;
		 } else if (cmd_name.equalsIgnoreCase("23度")) {
			 command_com = temperatures_23;
		 } else if (cmd_name.equalsIgnoreCase("24度")) {
			 command_com = temperatures_24;
		 } else if (cmd_name.equalsIgnoreCase("25度")) {
			 command_com = temperatures_25;
		 } else if (cmd_name.equalsIgnoreCase("26度")) {
			 command_com = temperatures_26;
		 } else if (cmd_name.equalsIgnoreCase("27度")) {
			 command_com = temperatures_27;
		 } else if (cmd_name.equalsIgnoreCase("28度")) {
			 command_com = temperatures_28;
		 } else if (cmd_name.equalsIgnoreCase("29度")) {
			 command_com = temperatures_29;
		 } else if (cmd_name.equalsIgnoreCase("30度")) {
			 command_com = temperatures_30;
		 } else if (cmd_name.equalsIgnoreCase("电视静音")) {
			 command_com = tv_mute;
		 } else {
			 ;
		 } 
		 
		 PubFunc.log("Cur Temp:" + i_current_temperature);
		
		 //0,AIRCONSERVER,test,12345678,格力,021#
		 StringBuffer sb = new StringBuffer();
		 sb.append(SmartPlugMessage.CMD_SP_AIRCONSERVER)
		  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
		  .append(PubStatus.g_CurUserName)
		  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
		  .append(mPlugId)
		  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
		  .append(str_Current_NO)
		  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
		  .append(command_com);
    	
    	if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
    		sendMsg(true, sb.toString(), true);
    	} else {  // 非Internet模式下，不处理； 
    		// do nothing.
    	}
	}
	
	// APP 直接下发红外数据，server只转发
	public void parsecommand_bak(String cmd_name) {
		// 播放声音
	    mp3_player.start();

	    String power_on 	= ReadCurrentAR(str_Current_NO, "063");
		String power_off 	= ReadCurrentAR(str_Current_NO, "064");
		String mode_auto 	= ReadCurrentAR(str_Current_NO, "001");
		String mode_cool 	= ReadCurrentAR(str_Current_NO, "002");
		String mode_wet 	= ReadCurrentAR(str_Current_NO, "003");
		String mode_wind 	= ReadCurrentAR(str_Current_NO, "004");
		String mode_warm 	= ReadCurrentAR(str_Current_NO, "005");
		String temperatures_16 = ReadCurrentAR(str_Current_NO, "048");
		String temperatures_17 = ReadCurrentAR(str_Current_NO, "049");
		String temperatures_18 = ReadCurrentAR(str_Current_NO, "050");
		String temperatures_19 = ReadCurrentAR(str_Current_NO, "051");
		String temperatures_20 = ReadCurrentAR(str_Current_NO, "052");
		String temperatures_21 = ReadCurrentAR(str_Current_NO, "053");
		String temperatures_22 = ReadCurrentAR(str_Current_NO, "054");
		String temperatures_23 = ReadCurrentAR(str_Current_NO, "055");
		String temperatures_24 = ReadCurrentAR(str_Current_NO, "056");
		String temperatures_25 = ReadCurrentAR(str_Current_NO, "057");
		String temperatures_26 = ReadCurrentAR(str_Current_NO, "058");
		String temperatures_27 = ReadCurrentAR(str_Current_NO, "059");
		String temperatures_28 = ReadCurrentAR(str_Current_NO, "060");
		String temperatures_29 = ReadCurrentAR(str_Current_NO, "061");
		String temperatures_30 = ReadCurrentAR(str_Current_NO, "062");
		
		// 测试数据
		String tv_mute = "5900,1950,550,1100,600,1050,550,550,550,550,550,1100,600,500,600,1050,550,550,550,600,550,1050,600,550,600,1050,600,1000,600,550,550,1050,600,550,550,1050,600,550,550,1050,600,1050,600,550,550,550,550,1100,550,550,3600,3550,600,550,600,500,550,1100,550,550,600,1050,550,600,550,1050,550,600,550,550,600,1050,550,550,600,500,600,1000,650,1050,550,550,550,1100,600,29914";
		
		String temperatures[] = {temperatures_16, temperatures_17, temperatures_18, temperatures_19, temperatures_20, temperatures_21, temperatures_22, temperatures_23, temperatures_24, temperatures_25, temperatures_26, temperatures_27, temperatures_28, temperatures_29, temperatures_30};
		power_on = temperatures[i_current_temperature - 16];
		
		 String command_com = "";
		 if (cmd_name.equalsIgnoreCase("打开空调")) {
			 command_com = power_on;
		 } else if (cmd_name.equalsIgnoreCase("关闭空调")) {
			 command_com = power_off;
		 } else if (cmd_name.equalsIgnoreCase("减少温度")) {
			 if (i_current_temperature > 16)
				 i_current_temperature--;
			 tv_aircon_temperature.setText(String.valueOf(i_current_temperature));
			 command_com = temperatures[i_current_temperature - 16];
		 } else if (cmd_name.equalsIgnoreCase("增加温度")) {
			 if (i_current_temperature < 30)
				 i_current_temperature++;
			 tv_aircon_temperature.setText(String.valueOf(i_current_temperature));
			 command_com = temperatures[i_current_temperature - 16];
		 } else if (cmd_name.equalsIgnoreCase("模式自动")) {
			 command_com = mode_auto;
		 } else if (cmd_name.equalsIgnoreCase("模式冷风")) {
			 command_com = mode_cool;
		 } else if (cmd_name.equalsIgnoreCase("模式除湿")) {
			 command_com = mode_wet;
		 } else if (cmd_name.equalsIgnoreCase("模式送风")) {
			 command_com = mode_wind;
		 } else if (cmd_name.equalsIgnoreCase("模式暖气")) {
			 command_com = mode_warm;
		 } else if (cmd_name.equalsIgnoreCase("16度")) {
			 command_com = temperatures_16;
		 } else if (cmd_name.equalsIgnoreCase("17度")) {
			 command_com = temperatures_17;
		 } else if (cmd_name.equalsIgnoreCase("18度")) {
			 command_com = temperatures_18;
		 } else if (cmd_name.equalsIgnoreCase("19度")) {
			 command_com = temperatures_19;
		 } else if (cmd_name.equalsIgnoreCase("20度")) {
			 command_com = temperatures_20;
		 } else if (cmd_name.equalsIgnoreCase("21度")) {
			 command_com = temperatures_21;
		 } else if (cmd_name.equalsIgnoreCase("22度")) {
			 command_com = temperatures_22;
		 } else if (cmd_name.equalsIgnoreCase("23度")) {
			 command_com = temperatures_23;
		 } else if (cmd_name.equalsIgnoreCase("24度")) {
			 command_com = temperatures_24;
		 } else if (cmd_name.equalsIgnoreCase("25度")) {
			 command_com = temperatures_25;
		 } else if (cmd_name.equalsIgnoreCase("26度")) {
			 command_com = temperatures_26;
		 } else if (cmd_name.equalsIgnoreCase("27度")) {
			 command_com = temperatures_27;
		 } else if (cmd_name.equalsIgnoreCase("28度")) {
			 command_com = temperatures_28;
		 } else if (cmd_name.equalsIgnoreCase("29度")) {
			 command_com = temperatures_29;
		 } else if (cmd_name.equalsIgnoreCase("30度")) {
			 command_com = temperatures_30;
		 } else if (cmd_name.equalsIgnoreCase("电视静音")) {
			 command_com = tv_mute;
		 } else {
			 ;
		 } 
		 
		 PubFunc.log("Cur Temp:" + i_current_temperature);
		
		 //0,AIRCON,test,12345678,0,0,150,3000,3000,3050,...#
		 String[] temp_strs = command_com.split(",");
		int i_len = temp_strs.length;
		String command_base = "AIRCON," + PubStatus.g_CurUserName + "," + mPlugId + ",0,0," + String.valueOf(i_len) + "," + command_com + "#";
		String command = "0," + command_base;
		byte[] temp_change = changeCommand(command);
		
		StringBuffer sb = new StringBuffer();
    	sb.append("APPPASSTHROUGH")
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(PubStatus.g_CurUserName)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(mPlugId)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(String.valueOf(temp_change.length))
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL);
    	
    	byte[] temp_command = null;
		
    	if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
    		byte[] temp_sb = sb.toString().getBytes();
    		temp_command = new byte[temp_sb.length + temp_change.length];
    		int i = 0;
    		for (i = 0; i < temp_sb.length; i++) {
    			temp_command[i] = temp_sb[i];
    		}
    		for (int j = 0; j < temp_change.length; j++) {
    			temp_command[i++] = temp_change[j];
    		}
    		
    		sendMsgBin(true, temp_command, true);
    	} else {
//    		temp_command = new byte[temp_change.length - 2];
//    		for (int k = 0; k < temp_change.length - 2; k++) {
//    			temp_command[k] = temp_change[k+2];
//    		}
//    		sendMsgBin(false, temp_command, true);
    		sendMsgBin(false, temp_change, true);
    	}		
	}
	
	private byte[] changeCommand(String cmd_text) {
//		final int BUF_SIZE = 1024;  	// 定义发送的二进制BUFFER的大小。
		
		byte[] byte_info;		
		String revMsg = cmd_text.substring(0, cmd_text.indexOf("#"));
		String arrays[] = revMsg.split(",");
		String cmd = arrays[1];
		int i = 0;
		int j = 0;
		
		// 红外命令单独处理
		if (arrays[1].equals("AIRCON") == true) {
			//byte_info = new byte[BUF_SIZE];
			
			// 临时Debug，要删除
			int count = 0;
			int length = Integer.parseInt(arrays[6]);
			for (j = 0; j < length; j++) {
				int value = Integer.parseInt(arrays[7 + j]);
				char a = (char)((value >> 8) & 0xFF);
				char b = (char)(value & 0xFF);
				count = count + a + b;
			}
			
			String strHeader = arrays[0] + "," + arrays[1] + "," + arrays[2] + "," + arrays[3] + "," + arrays[4] + "," + arrays[5] + "," +  arrays[6] + "," + count + ",";
			byte[] temp = strHeader.getBytes();
			
			byte_info = new byte[temp.length + Integer.parseInt(arrays[6]) * 2 + 1];
			
			for (i = 0; i < temp.length; i++) {
				byte_info[i] = temp[i];
			}
			
			length = Integer.parseInt(arrays[6]);
			for (j = 0; j < length; j++) {
				int value = Integer.parseInt(arrays[7 + j]);
				
				byte_info[i++] = (byte)((value >> 8) & 0xFF);
				byte_info[i++] = (byte)(value & 0xFF);
			}
			
			byte_info[i++] = '#';
			
//			// 增加 \0 空数据
//			for (; i < BUF_SIZE; i++) {
//				byte_info[i] = 0;
//			}
			
			// DEBUG： 只是为了打印调试信息
			String print_str = "";
			for (i = 0; i < byte_info.length; i++) {
				int v = byte_info[i] & 0xFF;
		        String hv = Integer.toHexString(v);
		        
				print_str = print_str + hv + ",";
			}
			Log.v("IR_Send", "IR_Send command:" + new String(byte_info));
			Log.v("IR_Send", "IR_Send command:" + print_str);
			
		} else {
			 byte_info = cmd_text.getBytes();
			 Log.v("IR_Send", "Normal command:" + new String(byte_info));
		}
		
		return byte_info;
	}

	private void init_IR_Data() {
		pv_AirCon_NO = (ThingzdoPickerView) findViewById(R.id.pv_AirCon_NO);
		list_NO_data.clear();
		list_NO_data.addAll(m_idSet);
		Collections.sort(list_NO_data);
		pv_AirCon_NO.setData(list_NO_data);
		pv_AirCon_NO.setVisibility(View.VISIBLE);
		
		pv_AirCon_NO.setOnSelectListener(new onSelectListener() 
		{
		  @Override 
		  public void onSelect(String text) 
		  {
			  str_Current_NO = text;
			  tv_aircon_select_content.setText(str_Current_NO);
		  } 
		});
		

		int select_no = list_NO_data.indexOf(str_Current_NO);
		if (select_no == -1) {
			str_Current_NO = "0001";
			select_no = list_NO_data.indexOf(str_Current_NO);
		}
		pv_AirCon_NO.setSelected(select_no);
	}
		
	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}
		
		pv_AirCon_NO = (ThingzdoPickerView) findViewById(R.id.pv_AirCon_NO);
		// first　init Aircon 
		list_NO_data.addAll(m_idSet);
		Collections.sort(list_NO_data);
		pv_AirCon_NO.setData(list_NO_data);
		pv_AirCon_NO.setVisibility(View.GONE);
		
		pv_AirCon_NO.setOnSelectListener(new onSelectListener() 
		{
		  @Override 
		  public void onSelect(String text) 
		  {
			  str_Current_NO = text;
			  tv_aircon_select_content.setText(str_Current_NO);
		  } 
		});

		loadData();
//		int select_no = list_NO_data.indexOf(str_Current_NO);
//		if (select_no == -1) {
//			str_Current_NO = "0001";
//			select_no = list_NO_data.indexOf(str_Current_NO);
//		}
//		pv_AirCon_NO.setSelected(select_no);
		

		// 初始化模式图片资源
		int_mode_res[0] = R.drawable.aircon_ui_show_01_auto;
		int_mode_res[1] = R.drawable.aircon_ui_show_01_cool;
		int_mode_res[2] = R.drawable.aircon_ui_show_01_wet;
		int_mode_res[3] = R.drawable.aircon_ui_show_01_wind;
		int_mode_res[4] = R.drawable.aircon_ui_show_01_warm;
				
		// 初始化温度图片资源
		int_temperature_res[0] = R.drawable.aircon_ui_show_02_16;
		int_temperature_res[1] = R.drawable.aircon_ui_show_02_17;
		int_temperature_res[2] = R.drawable.aircon_ui_show_02_18;
		int_temperature_res[3] = R.drawable.aircon_ui_show_02_19;
		int_temperature_res[4] = R.drawable.aircon_ui_show_02_20;
		int_temperature_res[5] = R.drawable.aircon_ui_show_02_21;
		int_temperature_res[6] = R.drawable.aircon_ui_show_02_22;
		int_temperature_res[7] = R.drawable.aircon_ui_show_02_23;
		int_temperature_res[8] = R.drawable.aircon_ui_show_02_24;
		int_temperature_res[9] = R.drawable.aircon_ui_show_02_25;
		int_temperature_res[10] = R.drawable.aircon_ui_show_02_26;
		int_temperature_res[11] = R.drawable.aircon_ui_show_02_27;
		int_temperature_res[12] = R.drawable.aircon_ui_show_02_28;
		int_temperature_res[13] = R.drawable.aircon_ui_show_02_29;
		int_temperature_res[14] = R.drawable.aircon_ui_show_02_30;
		
		btn_aircon_open = (Button)findViewById(R.id.btn_aircon_open);
		btn_aircon_close = (Button)findViewById(R.id.btn_aircon_close);
		btn_aircon_temp_descrease = (Button)findViewById(R.id.btn_aircon_temp_descrease);
		btn_aircon_temp_increase = (Button)findViewById(R.id.btn_aircon_temp_increase);
		btn_aircon_mode_auto = (Button)findViewById(R.id.btn_aircon_mode_auto);
		btn_aircon_mode_cool = (Button)findViewById(R.id.btn_aircon_mode_cool);
		btn_aircon_mode_wet = (Button)findViewById(R.id.btn_aircon_mode_wet);
		btn_aircon_mode_wind = (Button)findViewById(R.id.btn_aircon_mode_wind);
		btn_aircon_mode_warm = (Button)findViewById(R.id.btn_aircon_mode_warm);
		
		btn_aircon_open.setOnClickListener(this);
		btn_aircon_close.setOnClickListener(this);
		btn_aircon_temp_descrease.setOnClickListener(this);
		btn_aircon_temp_increase.setOnClickListener(this);
		btn_aircon_mode_auto.setOnClickListener(this);
		btn_aircon_mode_cool.setOnClickListener(this);
		btn_aircon_mode_wet.setOnClickListener(this);
		btn_aircon_mode_wind.setOnClickListener(this);
		btn_aircon_mode_warm.setOnClickListener(this);
		
		tv_aircon_temperature = (TextView)findViewById(R.id.tv_aircon_temperature);
		tv_aircon_temperature.setText(String.valueOf(i_current_temperature));
		
		LinearLayout ll_view_button_control = (LinearLayout)findViewById(R.id.ll_view_button_control);
		ll_view_button_control.setVisibility(View.GONE);
		
		//图片显示&控制区
		iv_view_show_01_mode_show = (ImageView)findViewById(R.id.iv_view_show_01_mode_show);
		iv_view_show_02_temperature = (ImageView)findViewById(R.id.iv_view_show_02_temperature);
		iv_view_show_03_wind = (ImageView)findViewById(R.id.iv_view_show_03_wind);
		iv_view_show_04_increase = (ImageView)findViewById(R.id.iv_view_show_04_increase);
		iv_view_show_05_descease = (ImageView)findViewById(R.id.iv_view_show_05_descease);
		iv_view_show_06_power = (ImageView)findViewById(R.id.iv_view_show_06_power);
		iv_view_show_07_mode = (ImageView)findViewById(R.id.iv_view_show_07_mode);
		
		iv_view_show_04_increase.setOnClickListener(this);
		iv_view_show_05_descease.setOnClickListener(this);
		iv_view_show_06_power.setOnClickListener(this);
		iv_view_show_07_mode.setOnClickListener(this);
		
		
		// 场景控制区
		btn_aircon_temp_descrease_scene_1h = (Button)findViewById(R.id.btn_aircon_temp_descrease_scene_1h);
		btn_aircon_temp_descrease_scene_2h = (Button)findViewById(R.id.btn_aircon_temp_descrease_scene_2h);
		btn_aircon_temp_descrease_scene_3h = (Button)findViewById(R.id.btn_aircon_temp_descrease_scene_3h);
		btn_aircon_temp_descrease_scene_4h = (Button)findViewById(R.id.btn_aircon_temp_descrease_scene_4h);
		btn_aircon_temp_descrease_scene_5h = (Button)findViewById(R.id.btn_aircon_temp_descrease_scene_5h);
		btn_aircon_temp_descrease_scene_6h = (Button)findViewById(R.id.btn_aircon_temp_descrease_scene_6h);
		btn_aircon_temp_descrease_scene_7h = (Button)findViewById(R.id.btn_aircon_temp_descrease_scene_7h);
		btn_aircon_temp_descrease_scene_8h = (Button)findViewById(R.id.btn_aircon_temp_descrease_scene_8h);
		btn_aircon_temp_descrease_scene_9h = (Button)findViewById(R.id.btn_aircon_temp_descrease_scene_9h);
		btn_aircon_temp_descrease_scene_10h = (Button)findViewById(R.id.btn_aircon_temp_descrease_scene_10h);
		
		btn_aircon_temp_increase_scene_1h = (Button)findViewById(R.id.btn_aircon_temp_increase_scene_1h);
		btn_aircon_temp_increase_scene_2h = (Button)findViewById(R.id.btn_aircon_temp_increase_scene_2h);
		btn_aircon_temp_increase_scene_3h = (Button)findViewById(R.id.btn_aircon_temp_increase_scene_3h);
		btn_aircon_temp_increase_scene_4h = (Button)findViewById(R.id.btn_aircon_temp_increase_scene_4h);
		btn_aircon_temp_increase_scene_5h = (Button)findViewById(R.id.btn_aircon_temp_increase_scene_5h);
		btn_aircon_temp_increase_scene_6h = (Button)findViewById(R.id.btn_aircon_temp_increase_scene_6h);
		btn_aircon_temp_increase_scene_7h = (Button)findViewById(R.id.btn_aircon_temp_increase_scene_7h);
		btn_aircon_temp_increase_scene_8h = (Button)findViewById(R.id.btn_aircon_temp_increase_scene_8h);
		btn_aircon_temp_increase_scene_9h = (Button)findViewById(R.id.btn_aircon_temp_increase_scene_9h);
		btn_aircon_temp_increase_scene_10h = (Button)findViewById(R.id.btn_aircon_temp_increase_scene_10h);

		btn_aircon_temp_descrease_scene_1h.setOnClickListener(this);
		btn_aircon_temp_descrease_scene_2h.setOnClickListener(this);
		btn_aircon_temp_descrease_scene_3h.setOnClickListener(this);
		btn_aircon_temp_descrease_scene_4h.setOnClickListener(this);
		btn_aircon_temp_descrease_scene_5h.setOnClickListener(this);
		btn_aircon_temp_descrease_scene_6h.setOnClickListener(this);
		btn_aircon_temp_descrease_scene_7h.setOnClickListener(this);
		btn_aircon_temp_descrease_scene_8h.setOnClickListener(this);
		btn_aircon_temp_descrease_scene_9h.setOnClickListener(this);
		btn_aircon_temp_descrease_scene_10h.setOnClickListener(this);
		
		btn_aircon_temp_increase_scene_1h.setOnClickListener(this);
		btn_aircon_temp_increase_scene_2h.setOnClickListener(this);
		btn_aircon_temp_increase_scene_3h.setOnClickListener(this);
		btn_aircon_temp_increase_scene_4h.setOnClickListener(this);
		btn_aircon_temp_increase_scene_5h.setOnClickListener(this);
		btn_aircon_temp_increase_scene_6h.setOnClickListener(this);
		btn_aircon_temp_increase_scene_7h.setOnClickListener(this);
		btn_aircon_temp_increase_scene_8h.setOnClickListener(this);
		btn_aircon_temp_increase_scene_9h.setOnClickListener(this);
		btn_aircon_temp_increase_scene_10h.setOnClickListener(this);
		

		iv_aircon_scene_enable = (ImageView)findViewById(R.id.iv_aircon_scene_enable);
		iv_aircon_scene_visible = (ImageView)findViewById(R.id.iv_aircon_scene_visible);		
		
		iv_aircon_scene_1h = (ImageView)findViewById(R.id.iv_aircon_scene_1h);
		iv_aircon_scene_2h = (ImageView)findViewById(R.id.iv_aircon_scene_2h);
		iv_aircon_scene_3h = (ImageView)findViewById(R.id.iv_aircon_scene_3h);
		iv_aircon_scene_4h = (ImageView)findViewById(R.id.iv_aircon_scene_4h);
		iv_aircon_scene_5h = (ImageView)findViewById(R.id.iv_aircon_scene_5h);
		iv_aircon_scene_6h = (ImageView)findViewById(R.id.iv_aircon_scene_6h);
		iv_aircon_scene_7h = (ImageView)findViewById(R.id.iv_aircon_scene_7h);
		iv_aircon_scene_8h = (ImageView)findViewById(R.id.iv_aircon_scene_8h);
		iv_aircon_scene_9h = (ImageView)findViewById(R.id.iv_aircon_scene_9h);
		iv_aircon_scene_10h = (ImageView)findViewById(R.id.iv_aircon_scene_10h);

		iv_aircon_scene_enable.setBackgroundResource(b_scene_name_enable? R.drawable.smp_switcher_open : R.drawable.smp_switcher_close);
		iv_aircon_scene_1h.setBackgroundResource(b_scene_1h_enable? R.drawable.smp_switcher_open : R.drawable.smp_switcher_close);
		iv_aircon_scene_2h.setBackgroundResource(b_scene_2h_enable? R.drawable.smp_switcher_open : R.drawable.smp_switcher_close);
		iv_aircon_scene_3h.setBackgroundResource(b_scene_3h_enable? R.drawable.smp_switcher_open : R.drawable.smp_switcher_close);
		iv_aircon_scene_4h.setBackgroundResource(b_scene_4h_enable? R.drawable.smp_switcher_open : R.drawable.smp_switcher_close);
		iv_aircon_scene_5h.setBackgroundResource(b_scene_5h_enable? R.drawable.smp_switcher_open : R.drawable.smp_switcher_close);
		iv_aircon_scene_6h.setBackgroundResource(b_scene_6h_enable? R.drawable.smp_switcher_open : R.drawable.smp_switcher_close);
		iv_aircon_scene_7h.setBackgroundResource(b_scene_7h_enable? R.drawable.smp_switcher_open : R.drawable.smp_switcher_close);
		iv_aircon_scene_8h.setBackgroundResource(b_scene_8h_enable? R.drawable.smp_switcher_open : R.drawable.smp_switcher_close);
		iv_aircon_scene_9h.setBackgroundResource(b_scene_9h_enable? R.drawable.smp_switcher_open : R.drawable.smp_switcher_close);
		iv_aircon_scene_10h.setBackgroundResource(b_scene_10h_enable? R.drawable.smp_switcher_open : R.drawable.smp_switcher_close);

		iv_aircon_scene_enable.setOnClickListener(this);
		iv_aircon_scene_visible.setOnClickListener(this);
		iv_aircon_scene_1h.setOnClickListener(this);
		iv_aircon_scene_2h.setOnClickListener(this);
		iv_aircon_scene_3h.setOnClickListener(this);
		iv_aircon_scene_4h.setOnClickListener(this);
		iv_aircon_scene_5h.setOnClickListener(this);
		iv_aircon_scene_6h.setOnClickListener(this);
		iv_aircon_scene_7h.setOnClickListener(this);
		iv_aircon_scene_8h.setOnClickListener(this);
		iv_aircon_scene_9h.setOnClickListener(this);
		iv_aircon_scene_10h.setOnClickListener(this);
		
		tv_aircon_select_content = (TextView)findViewById(R.id.tv_aircon_select_content);
		tv_aircon_select_content.setOnClickListener(this);
		tv_aircon_select_content.setText(str_Current_NO);

		tv_aircon_temperature_scene_1h = (TextView)findViewById(R.id.tv_aircon_temperature_scene_1h);
		tv_aircon_temperature_scene_2h = (TextView)findViewById(R.id.tv_aircon_temperature_scene_2h);
		tv_aircon_temperature_scene_3h = (TextView)findViewById(R.id.tv_aircon_temperature_scene_3h);
		tv_aircon_temperature_scene_4h = (TextView)findViewById(R.id.tv_aircon_temperature_scene_4h);
		tv_aircon_temperature_scene_5h = (TextView)findViewById(R.id.tv_aircon_temperature_scene_5h);
		tv_aircon_temperature_scene_6h = (TextView)findViewById(R.id.tv_aircon_temperature_scene_6h);
		tv_aircon_temperature_scene_7h = (TextView)findViewById(R.id.tv_aircon_temperature_scene_7h);
		tv_aircon_temperature_scene_8h = (TextView)findViewById(R.id.tv_aircon_temperature_scene_8h);
		tv_aircon_temperature_scene_9h = (TextView)findViewById(R.id.tv_aircon_temperature_scene_9h);
		tv_aircon_temperature_scene_10h = (TextView)findViewById(R.id.tv_aircon_temperature_scene_10h);

		tv_aircon_temperature_scene_1h.setText(String.valueOf(i_scene_1h_tempature));
		tv_aircon_temperature_scene_2h.setText(String.valueOf(i_scene_2h_tempature));
		tv_aircon_temperature_scene_3h.setText(String.valueOf(i_scene_3h_tempature));
		tv_aircon_temperature_scene_4h.setText(String.valueOf(i_scene_4h_tempature));
		tv_aircon_temperature_scene_5h.setText(String.valueOf(i_scene_5h_tempature));
		tv_aircon_temperature_scene_6h.setText(String.valueOf(i_scene_6h_tempature));
		tv_aircon_temperature_scene_7h.setText(String.valueOf(i_scene_7h_tempature));
		tv_aircon_temperature_scene_8h.setText(String.valueOf(i_scene_8h_tempature));
		tv_aircon_temperature_scene_9h.setText(String.valueOf(i_scene_9h_tempature));
		tv_aircon_temperature_scene_10h.setText(String.valueOf(i_scene_10h_tempature));
		
		
		pv_aircon_scene_pathview = (PathView)findViewById(R.id.pv_aircon_scene_pathview);
		pv_aircon_scene_pathview.setXCount(30, 10);
		pv_aircon_scene_pathview.setType(PathView.HOUR_DAY);
		updatePath();
		
		ll_aircon_scene_hours = (LinearLayout)findViewById(R.id.ll_aircon_scene_hours);
		ll_aircon_scene_hours.setVisibility(View.GONE);
		
		// 测试按钮代码，必须删除
		Button btn_aircon_test = (Button)findViewById(R.id.btn_aircon_test);
		btn_aircon_test.setOnClickListener(this);
		btn_aircon_test.setVisibility(View.GONE);
		
		updateImageMode();
		updateImageTemperature();
		
//		if (b_scene_name_enable) {
//			StartSceneTimer();
//		} else {
//			StopSceneTimer();
//		}
		
		tempControl = (TempControlView) findViewById(R.id.temp_control);
        tempControl.setTemp(15, 30, 15);

        tempControl.setOnTempChangeListener(new TempControlView.OnTempChangeListener() {
            @Override
            public void change(int temp) {
                Toast.makeText(DetailAirConActivity.this, temp + "°", Toast.LENGTH_SHORT).show();
            }
        });
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
		tv_aircon_temperature.setText(String.valueOf(i_current_temperature));
	}
	
	private void parseJSONWithGSON(String jsonData) {
		Gson gson = new Gson();
		List<GSON_IR_AIRCON> itemsList = gson.fromJson(jsonData, new TypeToken<List<GSON_IR_AIRCON>>() {}.getType());
        Map<String, Object> map = null;
        
        m_tjData.clear();
        
		for (GSON_IR_AIRCON item : itemsList) {
			map = new HashMap<String, Object>();
			
	        map.put("id", 		item.getId());
	        map.put("sub_id", 	item.getSub_id());
	        map.put("value", 	item.getValue());
	        
	        m_tjData.add(map);
	        m_idSet.add(item.getId());
		}
	}
	
	class GSON_IR_AIRCON {
		String id;
		String sub_id;
		String value;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getSub_id() {
			return sub_id;
		}
		public void setSub_id(String sub_id) {
			this.sub_id = sub_id;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	private Handler mTimerHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	int i_hours_no = msg.what;
			
			if (i_hours_no == 1)
				tv_aircon_temperature_scene_1h.setTextColor(Color.BLUE);
			else if (i_hours_no == 2)
				tv_aircon_temperature_scene_2h.setTextColor(Color.BLUE);
			else if (i_hours_no == 3)
				tv_aircon_temperature_scene_3h.setTextColor(Color.BLUE);
			else if (i_hours_no == 4)
				tv_aircon_temperature_scene_4h.setTextColor(Color.BLUE);
			else if (i_hours_no == 5)
				tv_aircon_temperature_scene_5h.setTextColor(Color.BLUE);
			else if (i_hours_no == 6)
				tv_aircon_temperature_scene_6h.setTextColor(Color.BLUE);
			else if (i_hours_no == 7)
				tv_aircon_temperature_scene_7h.setTextColor(Color.BLUE);
			else if (i_hours_no == 8)
				tv_aircon_temperature_scene_8h.setTextColor(Color.BLUE);
			else if (i_hours_no == 9)
				tv_aircon_temperature_scene_9h.setTextColor(Color.BLUE);
			else if (i_hours_no == 10) {
				tv_aircon_temperature_scene_10h.setTextColor(Color.BLUE);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// Restore Init Configure
				tv_aircon_temperature_scene_1h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_2h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_3h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_4h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_5h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_6h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_7h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_8h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_9h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_10h.setTextColor(Color.BLACK);
				
				b_scene_name_enable = false;
				iv_aircon_scene_enable.setBackgroundResource(R.drawable.smp_switcher_close);
				
				updatePath();
				
			} else {
				// Restore Init Configure
				tv_aircon_temperature_scene_1h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_2h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_3h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_4h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_5h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_6h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_7h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_8h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_9h.setTextColor(Color.BLACK);
				tv_aircon_temperature_scene_10h.setTextColor(Color.BLACK);
			}			
	    };	
	};

	public void updateTimerUI(int i_hours_no) {
		mTimerHandler.sendEmptyMessage(i_hours_no);

	}
}
