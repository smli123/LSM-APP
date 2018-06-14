package com.thingzdo.ui.control;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class DetailTVActivity extends TitledActivity implements OnClickListener {
	private static int IR_DATA_AIRCON_ALL_NAME = 1;
	private static int IR_DATA_AIRCON_SAVE_NAME = 2;
	private static int IR_DATA_AIRCON_LOAD_NAME = 3;

	private static String DEFAULT_NOT_SELECT_TV = "not set";

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";

	private Spinner spinner_ir;
	private Button btn_autosearch;
	private TextView tv_aircon_select_content;

	private ImageView iv_tv_power;
	private ImageView iv_power;
	private ImageView iv_mute;
	private ImageView iv_volume_add;
	private ImageView iv_volume_reduce;
	private ImageView iv_channel_add;
	private ImageView iv_channel_reduce;

	private ImageView iv_number_1;
	private ImageView iv_number_2;
	private ImageView iv_number_3;
	private ImageView iv_number_4;
	private ImageView iv_number_5;
	private ImageView iv_number_6;
	private ImageView iv_number_7;
	private ImageView iv_number_8;
	private ImageView iv_number_9;
	private ImageView iv_number_0;
	private ImageView iv_number_cancel;
	private ImageView iv_number_ok;

	private Set<String> m_idSet = new HashSet<String>();
	private List<String> list_NO_data = new ArrayList<String>();
	MediaPlayer mp3_player = null;
	private String str_Current_NO = "";

	private Timer timer = null;
	private TimerTask task = null;
	private boolean start_search = false;
	private List<String> irlist = new ArrayList<String>();
	private int search_index = -1;

	private boolean b_tv_power = true;
	private boolean b_power = true;
	private boolean b_mute = true;

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(DetailTVActivity.this,
						intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(DetailTVActivity.this,
						intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_POWER_ACTION)) {
				// nothing to do;
			}

			if (intent.getAction().equals(PubDefine.PLUG_TV_IRDATA_ACTION)) {
				int irtype = intent.getIntExtra("IRTYPE", 1);
				if (irtype == IR_DATA_AIRCON_ALL_NAME) {
					String strirdata = intent.getStringExtra("IRDATA");
					String[] irdata = strirdata.split("@");
					m_idSet.clear();
					for (int i = 0; i < irdata.length; i++) {
						m_idSet.add(irdata[i]);
					}

					// after get IR data from server, must init ir_data;
					init_IR_Data();
				} else if (irtype == IR_DATA_AIRCON_SAVE_NAME) {
					// do nothing.
				} else if (irtype == IR_DATA_AIRCON_LOAD_NAME) {
					str_Current_NO = intent.getStringExtra("IRDATA");
				} else {
					// do nothing.
				}

			}

			if (intent.getAction().equals(PubDefine.PLUG_TV_SERVER_ACTION)) {
				int status = intent.getIntExtra("STATUS", -1);
				PubFunc.log("发射红外数据: " + status);
				PubFunc.thinzdoToast(context,
						"发射红外数据: " + String.valueOf(status));
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_detail_tv, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

		setTitleRightButton(R.string.smartplug_title_plug_detail,
				R.drawable.title_btn_selector, this);

		if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
			setTitleLeftButton(R.string.smartplug_goback,
					R.drawable.title_btn_selector, this);
		} else {
			setTitleLeftButton(R.string.smartplug_esc,
					R.drawable.title_btn_selector, this);
		}

		if (PubDefine.SmartPlug_Connect_Mode.WiFi == PubDefine.g_Connect_Mode) {
			setTitleRightButton(R.string.smartplug_mgr_reselect,
					R.drawable.title_btn_selector, this);
		}

		mSharedPreferences = getSharedPreferences("IR_TV"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_POWER_ACTION);
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_NOTIFY_POWER);
		filter.addAction(PubDefine.PLUG_TV_IRDATA_ACTION);
		filter.addAction(PubDefine.PLUG_TV_SERVER_ACTION);
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
		// 从服务器获取m_idSet 空调数据
		getServerIRData(IR_DATA_AIRCON_LOAD_NAME);

		// 使用定时器来 更新 时间
		new Timer("GeTVName").schedule(new TimerTask() {
			@Override
			public void run() {
				getServerIRData(IR_DATA_AIRCON_ALL_NAME);
			}
		}, 1000, 600000);
	}

	private void getServerIRData(int data_type) {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_QRY_TVIRDATA)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(data_type);

		sendMsg(true, sb.toString(), true);
	}

	private void saveServerIRData(int data_type) {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_QRY_TVIRDATA)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(data_type)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(str_Current_NO);

		sendMsg(true, sb.toString(), true);
	}

	public static boolean isChineseChar(char c)
			throws UnsupportedEncodingException {
		return String.valueOf(c).getBytes("UTF-8").length > 1;
	}

	private void init_IR_Data() {
		List<String> irlist_chinese = new ArrayList<String>();
		List<String> irlist_nonchinese = new ArrayList<String>();

		irlist.clear();
		for (String str : m_idSet) {
			char first = str.charAt(0);
			try {
				if (isChineseChar(first) == true) {
					irlist_chinese.add(str);
				} else {
					irlist_nonchinese.add(str);
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Collections.sort(irlist_chinese);
		Collections.sort(irlist_nonchinese);

		irlist.add(DEFAULT_NOT_SELECT_TV);
		for (String str : irlist_chinese) {
			irlist.add(str);
		}
		for (String str : irlist_nonchinese) {
			irlist.add(str);
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.activity_detail_aircon2item, R.id.tv_item, irlist);
		spinner_ir.setAdapter(adapter);
		spinner_ir.setPrompt("测试");
		spinner_ir.setOnItemSelectedListener(new spinnerIRListener());

		if (irlist.size() > 0 && str_Current_NO.equals("") == false) {
			int select_no = irlist.indexOf(str_Current_NO);
			if (select_no == -1) {
				str_Current_NO = DEFAULT_NOT_SELECT_TV;
				select_no = irlist.indexOf(str_Current_NO);
			}

			spinner_ir.setSelection(select_no);
		}
	}

	private void saveData() {
		editor = mSharedPreferences.edit();
		editor.putString("STRING_ID_" + mPlugId, str_Current_NO);
		editor.commit();

		saveServerIRData(IR_DATA_AIRCON_SAVE_NAME);
	}

	private void loadData() {
		saveServerIRData(IR_DATA_AIRCON_LOAD_NAME);
	}

	class spinnerIRListener
			implements
				android.widget.AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			str_Current_NO = parent.getItemAtPosition(position).toString();
			tv_aircon_select_content.setText(str_Current_NO);

			saveData();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	private void auto_search() {

		if (btn_autosearch.getText().toString().equals("开始搜索") == true) {
			start_search = true;
			btn_autosearch.setText("停止搜索");

			timer = new Timer();
			task = new TimerTask() {
				@Override
				public void run() {
					if (start_search == false) {
						this.cancel();
						timer.cancel();
					} else {
						if (irlist.size() > 0
								&& str_Current_NO.equals("") == false) {
							if (search_index < 0) {
								search_index = irlist.indexOf(str_Current_NO);
							}
							if (search_index == 0)
								search_index = 1;

							if (search_index < irlist.size()) {
								str_Current_NO = irlist.get(search_index);
								Message msg = new Message();
								msg.what = 0;
								msg.arg1 = search_index;
								mHandler.sendMessage(msg);
								search_index++;
							} else {
								search_index = -1;
								this.cancel();
								timer.cancel();
								start_search = false;
								btn_autosearch.setText("开始搜索");
							}
						}
					}
				}
			};
			timer.schedule(task, 100, 5000);
		} else {
			search_index = -1;
			start_search = false;
			btn_autosearch.setText("开始搜索");
		}

		return;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0 :
					int index = msg.arg1;
					spinner_ir.setSelection(index);
					parsecommand("001"); // 打开 电视
					break;
				case 1 :
					break;
				default :
					break;
			}
		};
	};

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
			case R.id.titlebar_leftbutton : // WiFi模式 退出时，需要close掉 TCP连接
				disconnectSocket();
				finish();
				break;
			case R.id.titlebar_rightbutton :
				Button btn_TitleRight = (Button) findViewById(R.id.titlebar_rightbutton);
				// Internet模式：“详情”界面
				if (btn_TitleRight.getText().equals(
						getString(R.string.smartplug_title_plug_detail))) {
					Intent intent = new Intent();
					intent.putExtra("PLUGID", mPlugId);
					intent.setClass(DetailTVActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(DetailTVActivity.this,
							AddSocketActivity2.class);
					startActivity(intent);
					finish();
				}
			case R.id.iv_tv_power :
				parsecommand(b_tv_power ? "001" : "002");
				b_tv_power = !b_tv_power;
				break;
			case R.id.iv_power :
				parsecommand(b_power ? "001" : "002");
				b_power = !b_power;
				break;
			case R.id.iv_mute :
				parsecommand(b_mute ? "003" : "004");
				b_mute = !b_mute;
				break;
			case R.id.iv_volume_add :
				parsecommand("005");
				break;
			case R.id.iv_volume_reduce :
				parsecommand("006");
				break;
			case R.id.iv_channel_add :
				parsecommand("007");
				break;
			case R.id.iv_channel_reduce :
				parsecommand("008");
				break;
			case R.id.iv_number_1 :
				parsecommand("009");
				break;
			case R.id.iv_number_2 :
				parsecommand("010");
				break;
			case R.id.iv_number_3 :
				parsecommand("011");
				break;
			case R.id.iv_number_4 :
				parsecommand("012");
				break;
			case R.id.iv_number_5 :
				parsecommand("013");
				break;
			case R.id.iv_number_6 :
				parsecommand("014");
				break;
			case R.id.iv_number_7 :
				parsecommand("015");
				break;
			case R.id.iv_number_8 :
				parsecommand("016");
				break;
			case R.id.iv_number_9 :
				parsecommand("017");
				break;
			case R.id.iv_number_0 :
				parsecommand("018");
				break;
			case R.id.iv_number_cancel :
				parsecommand("019");
				break;
			case R.id.iv_number_ok :
				parsecommand("020");
				break;
			default :
				break;
		}
	}

	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}

		spinner_ir = (Spinner) findViewById(R.id.spinner_ir);
		tv_aircon_select_content = (TextView) findViewById(R.id.tv_aircon_select_content);
		btn_autosearch = (Button) findViewById(R.id.btn_autosearch);

		iv_tv_power = (ImageView) findViewById(R.id.iv_tv_power);
		iv_power = (ImageView) findViewById(R.id.iv_power);
		iv_mute = (ImageView) findViewById(R.id.iv_mute);

		iv_volume_add = (ImageView) findViewById(R.id.iv_volume_add);
		iv_volume_reduce = (ImageView) findViewById(R.id.iv_volume_reduce);

		iv_channel_add = (ImageView) findViewById(R.id.iv_channel_add);
		iv_channel_reduce = (ImageView) findViewById(R.id.iv_channel_add);

		iv_number_0 = (ImageView) findViewById(R.id.iv_number_0);
		iv_number_1 = (ImageView) findViewById(R.id.iv_number_1);
		iv_number_2 = (ImageView) findViewById(R.id.iv_number_2);
		iv_number_3 = (ImageView) findViewById(R.id.iv_number_3);
		iv_number_4 = (ImageView) findViewById(R.id.iv_number_4);
		iv_number_5 = (ImageView) findViewById(R.id.iv_number_5);
		iv_number_6 = (ImageView) findViewById(R.id.iv_number_6);
		iv_number_7 = (ImageView) findViewById(R.id.iv_number_7);
		iv_number_8 = (ImageView) findViewById(R.id.iv_number_8);
		iv_number_9 = (ImageView) findViewById(R.id.iv_number_9);
		iv_number_cancel = (ImageView) findViewById(R.id.iv_number_cancel);
		iv_number_ok = (ImageView) findViewById(R.id.iv_number_ok);

		iv_tv_power.setOnClickListener(this);
		iv_power.setOnClickListener(this);
		iv_mute.setOnClickListener(this);

		iv_volume_add.setOnClickListener(this);
		iv_volume_reduce.setOnClickListener(this);
		iv_channel_add.setOnClickListener(this);
		iv_channel_reduce.setOnClickListener(this);

		iv_number_0.setOnClickListener(this);
		iv_number_1.setOnClickListener(this);
		iv_number_2.setOnClickListener(this);
		iv_number_3.setOnClickListener(this);
		iv_number_4.setOnClickListener(this);
		iv_number_5.setOnClickListener(this);
		iv_number_6.setOnClickListener(this);
		iv_number_7.setOnClickListener(this);
		iv_number_8.setOnClickListener(this);
		iv_number_9.setOnClickListener(this);
		iv_number_cancel.setOnClickListener(this);
		iv_number_ok.setOnClickListener(this);

	}

	private void disconnectSocket() {
		// WiFi 直连模式下，退出或者重选时，必须close TCP连接；
		/*
		 * if (PubDefine.g_Connect_Mode ==
		 * PubDefine.SmartPlug_Connect_Mode.WiFi) {
		 * SmartPlugWifiMgr.disconnectSocket(); }
		 */

		return;
	}

	private void updateUI() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}
		setTitle(mPlug.mPlugName);
	}

	public void parsecommand(String cmd_name) {
		// 播放声音
		mp3_player.start();

		// 电视子码对应关系表
		// 电源开："001"
		// 电源关："002"
		// 静音开："003"
		// 静音关："004"
		// 音量增加："005"
		// 音量减少："006"
		// 频道增加："007"
		// 频道减少："008"
		// 数字1："009"
		// 数字2："010"
		// 数字3："011"
		// 数字4："012"
		// 数字5："013"
		// 数字6："014"
		// 数字7："015"
		// 数字8："016"
		// 数字9："017"
		// 数字0："018"
		// 数字取消："019"
		// 数字OK："020"

		// 0,APPAIRCONSERVER,test,12345678,格力,021#
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_TVSERVER)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(str_Current_NO)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(cmd_name);

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
			sendMsg(true, sb.toString(), true);
		} else { // 非Internet模式下，不处理；
			// do nothing.
		}
	}
}
