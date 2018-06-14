package com.thingzdo.ui.control;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thingzdo.SwipeMenuListView.SwipeMenuListView;
import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.dataprovider.SmartPlugIRSceneHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.IRSceneDefine;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.util.PathView;

public class DetailAirCon2Activity extends TitledActivity
		implements
			OnClickListener {
	private static int IR_DATA_AIRCON_ALL_NAME = 1;
	private static int IR_DATA_AIRCON_SAVE_NAME = 2;
	private static int IR_DATA_AIRCON_LOAD_NAME = 3;

	private final static String DEFAULT_NOT_SELECT_KONGTAI = "not set";

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";
	private String str_Current_NO = "";

	private TextView tv_aircon_select_content;
	private ImageView iv_view_show_mode;
	private ImageView iv_view_show_wind_volume;
	private ImageView iv_view_show_wind_swing;

	private ImageView iv_view_control_low;
	private ImageView iv_view_control_high;
	private ImageView iv_view_show_temperature;

	private ImageView iv_view_power_on;
	private ImageView iv_view_power_off;
	private ImageView iv_view_wind_swing;
	private ImageView iv_view_wind_volume;
	private ImageView iv_view_mode_hot;
	private ImageView iv_view_mode_cool;
	private ImageView iv_view_mode_wet;
	private ImageView iv_view_mode_wind;

	private ImageView iv_view_scene_add;

	private Spinner spinner_ir;
	private Button btn_autosearch;

	private SwipeMenuListView lv_scene_show;
	private List<Map<String, Object>> m_tjSceneData = new ArrayList<Map<String, Object>>();
	private SceneAdapter sceneAdapter;
	private SmartPlugIRSceneHelper mIRSceneHelper = null;

	PathView pv_aircon_scene_pathview;

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;

	private List<Map<String, Object>> m_tjData = new ArrayList<Map<String, Object>>();
	private Set<String> m_idSet = new HashSet<String>();
	private List<String> list_NO_data = new ArrayList<String>();

	MediaPlayer mp3_player = null;

	private String[] str_timer_commands = new String[10];

	private int[] int_temperature_res = new int[15];
	private int[] int_mode_res = new int[5];
	private int[] int_wind_volume_res = new int[3];
	private int[] int_wind_swing_res = new int[2];

	private static int INT_ITEM_SCENE_POWER = 2;
	private static int INT_ITEM_SCENE_MODE = 5;
	private static int INT_ITEM_SCENE_SWING = 2;
	private static int INT_ITEM_SCENE_VOLUME = 4;
	private static int INT_ITEM_SCENE_WEEK_7 = 2;
	private static int INT_ITEM_SCENE_WEEK_1 = 2;
	private static int INT_ITEM_SCENE_WEEK_2 = 2;
	private static int INT_ITEM_SCENE_WEEK_3 = 2;
	private static int INT_ITEM_SCENE_WEEK_4 = 2;
	private static int INT_ITEM_SCENE_WEEK_5 = 2;
	private static int INT_ITEM_SCENE_WEEK_6 = 2;
	private static int INT_ITEM_SCENE_ENABLE = 2;

	private int[] int_item_scene_power = new int[INT_ITEM_SCENE_POWER];
	private int[] int_item_scene_mode = new int[INT_ITEM_SCENE_MODE];
	private int[] int_item_scene_swing = new int[INT_ITEM_SCENE_SWING];
	private int[] int_item_scene_volume = new int[INT_ITEM_SCENE_VOLUME];
	private int[] int_item_scene_week_7 = new int[INT_ITEM_SCENE_WEEK_7];
	private int[] int_item_scene_week_1 = new int[INT_ITEM_SCENE_WEEK_1];
	private int[] int_item_scene_week_2 = new int[INT_ITEM_SCENE_WEEK_2];
	private int[] int_item_scene_week_3 = new int[INT_ITEM_SCENE_WEEK_3];
	private int[] int_item_scene_week_4 = new int[INT_ITEM_SCENE_WEEK_4];
	private int[] int_item_scene_week_5 = new int[INT_ITEM_SCENE_WEEK_5];
	private int[] int_item_scene_week_6 = new int[INT_ITEM_SCENE_WEEK_6];
	private int[] int_item_scene_enable = new int[INT_ITEM_SCENE_ENABLE];

	private int i_int_item_scene_power = 0;
	private int i_int_item_scene_mode = 0;
	private int i_int_item_scene_swing = 0;
	private int i_int_item_scene_volume = 0;
	private int i_int_item_scene_week_7 = 1;
	private int i_int_item_scene_week_6 = 1;
	private int i_int_item_scene_week_5 = 1;
	private int i_int_item_scene_week_4 = 1;
	private int i_int_item_scene_week_3 = 1;
	private int i_int_item_scene_week_2 = 1;
	private int i_int_item_scene_week_1 = 1;
	private int i_int_item_scene_enable = 0;

	private int i_int_item_scene_temperature = 16;

	private int i_current_temperature = 26; //
	private int i_current_wind_volume = 0; // 0,1,2
	private boolean i_current_wind_swing = true; // 0: off, 1: on

	private Timer timer = null;
	private TimerTask task = null;
	private boolean start_search = false;
	private List<String> irlist = new ArrayList<String>();
	private int search_index = -1;

	// 测试温度控制旋转控件
	// private TempControlView tempControl;

	public String[] getTimerCommand() {
		return str_timer_commands;
	}

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						DetailAirCon2Activity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(
						DetailAirCon2Activity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_AIRCON_IRDATA_ACTION)) {
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

			if (intent.getAction().equals(PubDefine.PLUG_AIRCON_SERVER_ACTION)) {
				int status = intent.getIntExtra("STATUS", -1);
				PubFunc.log("发射红外数据: " + status);
				PubFunc.thinzdoToast(context,
						"发射红外数据: " + String.valueOf(status));
			}

			// if
			// (intent.getAction().equals(PubDefine.PLUG_AIRCON_ADDSCENE_ACTION))
			// {
			// String[] infors = intent.getExtra("DATA");
			// IRSceneDefine ti = new IRSceneDefine();
			// ti.mPlugId = infors[3];
			// ti.mIRSceneId = Integer.parseInt(infors[4]);
			// ti.mPower = Integer.parseInt(infors[5]);
			// ti.mMode = Integer.parseInt(infors[6]);
			// ti.mDirection = Integer.parseInt(infors[7]);
			// ti.mScale = Integer.parseInt(infors[8]);
			// ti.mTemperature = Integer.parseInt(infors[9]);
			// ti.mTime = infors[10];
			// ti.mPeriod = infors[11];
			// ti.mIRName = infors[12];
			// ti.mEnable = Integer.parseInt(infors[13]);
			// addScene2DB(ti);
			// }
			// if
			// (intent.getAction().equals(PubDefine.PLUG_AIRCON_MODIFYSCENE_ACTION))
			// {
			// String[] infors = intent.getStringExtra("DATA");
			// IRSceneDefine ti = new IRSceneDefine();
			// ti.mPlugId = infors[3];
			// ti.mIRSceneId = Integer.parseInt(infors[4]);
			// ti.mPower = Integer.parseInt(infors[5]);
			// ti.mMode = Integer.parseInt(infors[6]);
			// ti.mDirection = Integer.parseInt(infors[7]);
			// ti.mScale = Integer.parseInt(infors[8]);
			// ti.mTemperature = Integer.parseInt(infors[9]);
			// ti.mTime = infors[10];
			// ti.mPeriod = infors[11];
			// ti.mIRName = infors[12];
			// ti.mEnable = Integer.parseInt(infors[13]);
			// modifyScene2DB(ti);
			// }
			// if
			// (intent.getAction().equals(PubDefine.PLUG_AIRCON_DELSCENE_ACTION))
			// {
			// String[] infors = intent.getStringExtra("DATA");
			// IRSceneDefine ti = new IRSceneDefine();
			// ti.mPlugId = infors[3];
			// ti.mIRSceneId = Integer.parseInt(infors[4]);
			// ti.mPower = Integer.parseInt(infors[5]);
			// ti.mMode = Integer.parseInt(infors[6]);
			// ti.mDirection = Integer.parseInt(infors[7]);
			// ti.mScale = Integer.parseInt(infors[8]);
			// ti.mTemperature = Integer.parseInt(infors[9]);
			// ti.mTime = infors[10];
			// ti.mPeriod = infors[11];
			// ti.mIRName = infors[12];
			// ti.mEnable = Integer.parseInt(infors[13]);
			// deleteScene2DB(ti);
			// }
			// if
			// (intent.getAction().equals(PubDefine.PLUG_AIRCON_ENABLESCENE_ACTION))
			// {
			// String[] infors = intent.getStringExtra("DATA");
			// IRSceneDefine ti = new IRSceneDefine();
			// ti.mPlugId = infors[3];
			// ti.mIRSceneId = Integer.parseInt(infors[4]);
			// ti.mPower = Integer.parseInt(infors[5]);
			// ti.mMode = Integer.parseInt(infors[6]);
			// ti.mDirection = Integer.parseInt(infors[7]);
			// ti.mScale = Integer.parseInt(infors[8]);
			// ti.mTemperature = Integer.parseInt(infors[9]);
			// ti.mTime = infors[10];
			// ti.mPeriod = infors[11];
			// ti.mIRName = infors[12];
			// ti.mEnable = Integer.parseInt(infors[13]);
			// enableScene2DB(ti);
			// }
			//
			// if
			// (intent.getAction().equals(PubDefine.PLUG_AIRCON_ADDSCENE_ACTION)
			// ||
			// intent.getAction().equals(PubDefine.PLUG_AIRCON_MODIFYSCENE_ACTION)
			// ||
			// intent.getAction().equals(PubDefine.PLUG_AIRCON_DELSCENE_ACTION)
			// ||
			// intent.getAction().equals(PubDefine.PLUG_AIRCON_ENABLESCENE_ACTION))
			// {
			//
			// querySceneServerData();
			// }

			// if
			// (intent.getAction().equals(PubDefine.PLUG_AIRCON_ADDSCENE_ACTION))
			// {
			// querySceneServerData();
			// }
			// if
			// (intent.getAction().equals(PubDefine.PLUG_AIRCON_MODIFYSCENE_ACTION))
			// {
			// querySceneServerData();
			// }
			// if
			// (intent.getAction().equals(PubDefine.PLUG_AIRCON_DELSCENE_ACTION))
			// {
			// querySceneServerData();
			// }
			// if
			// (intent.getAction().equals(PubDefine.PLUG_AIRCON_ENABLESCENE_ACTION))
			// {
			// querySceneServerData();
			// }
			//
			if (intent.getAction().equals(
					PubDefine.PLUG_AIRCON_QUERYSCENE_ACTION)) {
				clearScene2DB();

				Bundle bu = intent.getExtras();
				String[] infors = bu.getStringArray("DATA");
				// int count = Integer.parseInt(infors[5]);

				int baseIdx = 5;
				int irCount = Integer.parseInt(infors[baseIdx]);
				baseIdx++;
				for (int j = 0; j < irCount; j++) {
					IRSceneDefine ti = new IRSceneDefine();
					ti.mPlugId = infors[3];
					ti.mIRSceneId = Integer.parseInt(infors[baseIdx + j * 10
							+ 0]);
					ti.mPower = Integer.parseInt(infors[baseIdx + j * 10 + 1]);
					ti.mMode = Integer.parseInt(infors[baseIdx + j * 10 + 2]);
					ti.mDirection = Integer.parseInt(infors[baseIdx + j * 10
							+ 3]);
					ti.mScale = Integer.parseInt(infors[baseIdx + j * 10 + 4]);
					ti.mTemperature = Integer.parseInt(infors[baseIdx + j * 10
							+ 5]);
					ti.mTime = infors[baseIdx + j * 10 + 6];
					ti.mPeriod = infors[baseIdx + j * 10 + 7];
					ti.mIRName = infors[baseIdx + j * 10 + 8];
					ti.mEnable = Integer.parseInt(infors[baseIdx + j * 10 + 9]);
					addScene2DB(ti);
				}

				refreshIRScene();
			}
		}
	};

	void refreshIRScene() {
		// Init m_tjSceneData
		m_tjSceneData.clear();
		ArrayList<IRSceneDefine> timers = mIRSceneHelper.getAllIRScene(mPlugId);
		for (IRSceneDefine ir : timers) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("iv_item_irsceneid", ir.mIRSceneId);
			data.put("iv_item_power", ir.mPower);
			data.put("iv_item_mode", ir.mMode);
			data.put("iv_item_swing", ir.mDirection);
			data.put("iv_item_volume", ir.mScale);
			data.put("tv_item_temperature", ir.mTemperature);

			data.put("iv_item_week_7",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(0))));
			data.put("iv_item_week_1",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(1))));
			data.put("iv_item_week_2",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(2))));
			data.put("iv_item_week_3",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(3))));
			data.put("iv_item_week_4",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(4))));
			data.put("iv_item_week_5",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(5))));
			data.put("iv_item_week_6",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(6))));

			data.put("tv_item_alarm", ir.mTime);
			data.put("iv_item_enable", ir.mEnable);

			m_tjSceneData.add(data);
		}

		sceneAdapter.notifyDataSetChanged();
	}

	private void clearScene2DB() {
		SmartPlugIRSceneHelper mIRSceneHelper = new SmartPlugIRSceneHelper(
				SmartPlugApplication.getContext());
		mIRSceneHelper.clearIRScene(mPlugId);

		mIRSceneHelper = null;
	}
	private void addScene2DB(IRSceneDefine ir) {
		SmartPlugIRSceneHelper mIRSceneHelper = new SmartPlugIRSceneHelper(
				SmartPlugApplication.getContext());
		if (ir.mPlugId == mPlugId) {
			mIRSceneHelper.addIRScene(ir);
		}
		mIRSceneHelper = null;
	}

	private void deleteScene2DB(IRSceneDefine ir) {
		SmartPlugIRSceneHelper mIRSceneHelper = new SmartPlugIRSceneHelper(
				SmartPlugApplication.getContext());
		if (ir.mPlugId == mPlugId) {
			mIRSceneHelper.deleteIRScene(ir.mIRSceneId);
		}
		mIRSceneHelper = null;
	}

	private void enableScene2DB(IRSceneDefine ir) {
		SmartPlugIRSceneHelper mIRSceneHelper = new SmartPlugIRSceneHelper(
				SmartPlugApplication.getContext());
		if (ir.mPlugId == mPlugId) {
			mIRSceneHelper.modifyIRScene(ir);
		}
		mIRSceneHelper = null;
	}
	private void modifyScene2DB(IRSceneDefine ir) {
		SmartPlugIRSceneHelper mIRSceneHelper = new SmartPlugIRSceneHelper(
				SmartPlugApplication.getContext());
		if (ir.mPlugId == mPlugId) {
			mIRSceneHelper.modifyIRScene(ir);
		}
		mIRSceneHelper = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_detail_aircon2,
				false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

		setTitleLeftButton(R.string.smartplug_goback,
				R.drawable.title_btn_selector, this);

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

		filter.addAction(PubDefine.PLUG_AIRCON_ADDSCENE_ACTION);
		filter.addAction(PubDefine.PLUG_AIRCON_MODIFYSCENE_ACTION);
		filter.addAction(PubDefine.PLUG_AIRCON_DELSCENE_ACTION);
		filter.addAction(PubDefine.PLUG_AIRCON_ENABLESCENE_ACTION);
		filter.addAction(PubDefine.PLUG_AIRCON_QUERYSCENE_ACTION);

		registerReceiver(mDetailRev, filter);

		mPlugHelper = new SmartPlugHelper(this);
		mIRSceneHelper = new SmartPlugIRSceneHelper(this);

		mSharedPreferences = getSharedPreferences("IR_AIRCON"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);

		UDPClient.getInstance().setIPAddress(mPlugIp);

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}

		// 从服务器获取m_idSet 空调数据
		getServerIRData(IR_DATA_AIRCON_LOAD_NAME);

		// 使用定时器来 更新 时间
		new Timer("GetAirConName").schedule(new TimerTask() {
			@Override
			public void run() {
				getServerIRData(IR_DATA_AIRCON_ALL_NAME);
			}
		}, 1000, 600000);
	}

	private void getServerIRData(int data_type) {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_QRY_IRDATA)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(data_type);

		sendMsg(true, sb.toString(), true);
	}

	private void saveServerIRData(int data_type) {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_QRY_IRDATA)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(data_type)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(str_Current_NO);

		sendMsg(true, sb.toString(), true);
	}

	private void addSceneServerData(IRSceneDefine ir) {
		String info = String.valueOf(ir.mIRSceneId) + ","
				+ String.valueOf(ir.mPower) + "," + String.valueOf(ir.mMode)
				+ "," + String.valueOf(ir.mDirection) + ","
				+ String.valueOf(ir.mScale) + ","
				+ String.valueOf(ir.mTemperature) + ","
				+ String.valueOf(ir.mTime) + "," + String.valueOf(ir.mPeriod)
				+ "," + String.valueOf(ir.mIRName) + ","
				+ String.valueOf(ir.mEnable);

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_ADDSCENE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(info);

		sendMsg(true, sb.toString(), true);
	}

	private void deleteSceneServerData(IRSceneDefine ir) {
		String info = String.valueOf(ir.mIRSceneId) + ","
				+ String.valueOf(ir.mPower) + "," + String.valueOf(ir.mMode)
				+ "," + String.valueOf(ir.mDirection) + ","
				+ String.valueOf(ir.mScale) + ","
				+ String.valueOf(ir.mTemperature) + ","
				+ String.valueOf(ir.mTime) + "," + String.valueOf(ir.mPeriod)
				+ "," + String.valueOf(ir.mIRName) + ","
				+ String.valueOf(ir.mEnable);

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_DELSCENE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(info);

		sendMsg(true, sb.toString(), true);
	}

	private void modifySceneServerData(IRSceneDefine ir) {
		String info = String.valueOf(ir.mIRSceneId) + ","
				+ String.valueOf(ir.mPower) + "," + String.valueOf(ir.mMode)
				+ "," + String.valueOf(ir.mDirection) + ","
				+ String.valueOf(ir.mScale) + ","
				+ String.valueOf(ir.mTemperature) + ","
				+ String.valueOf(ir.mTime) + "," + String.valueOf(ir.mPeriod)
				+ "," + String.valueOf(ir.mIRName) + ","
				+ String.valueOf(ir.mEnable);

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_MODIFYSCENE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(info);

		sendMsg(true, sb.toString(), true);
	}

	private void enableSceneServerData(IRSceneDefine ir) {
		String info = String.valueOf(ir.mIRSceneId) + ","
				+ String.valueOf(ir.mPower) + "," + String.valueOf(ir.mMode)
				+ "," + String.valueOf(ir.mDirection) + ","
				+ String.valueOf(ir.mScale) + ","
				+ String.valueOf(ir.mTemperature) + ","
				+ String.valueOf(ir.mTime) + "," + String.valueOf(ir.mPeriod)
				+ "," + String.valueOf(ir.mIRName) + ","
				+ String.valueOf(ir.mEnable);

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_ENABLESCENE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(info);

		sendMsg(true, sb.toString(), true);
	}

	private void querySceneServerData() {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_QUERYSCENE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId);

		sendMsg(true, sb.toString(), true);
	}

	private void saveData() {
		editor = mSharedPreferences.edit();
		editor.putString("STRING_ID_" + mPlugId, str_Current_NO);
		// editor.putInt("INT_TEMPERATURE_" + mPlugId, i_current_temperature);

		// editor.putInt("I_SCENE_1H_TEMPATURE" + mPlugId, 1);
		editor.commit();

		saveServerIRData(IR_DATA_AIRCON_SAVE_NAME);
	}

	private void loadData() {
		// str_Current_NO = mSharedPreferences.getString("STRING_ID_" + mPlugId,
		// "0001");
		// i_current_temperature = mSharedPreferences.getInt("INT_TEMPERATURE_"
		// + mPlugId, 26);

		saveServerIRData(IR_DATA_AIRCON_LOAD_NAME);

		// i_scene_1h_tempature =
		// mSharedPreferences.getInt("I_SCENE_1H_TEMPATURE" + mPlugId, 26);

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
		// StartTestTimer();
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
		// saveData();

		unregisterReceiver(mDetailRev);
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread.setRunning(false);
		}

		// 测试模块红外插件（关闭定时），测试临时使用，必须关闭
		// StopTestTimer();

		super.onDestroy();
	}

	private void updatePath() {
		// int[] temp_data = null;
		// // if (b_scene_name_enable == false) {
		// // temp_data = new int[]{0,0,0,0,0,0,0,0,0,0};
		// // } else {
		// temp_data = new int[10];
		// temp_data[0] = b_scene_1h_enable ? i_scene_1h_tempature : 0;
		// temp_data[1] = b_scene_2h_enable ? i_scene_2h_tempature : 0;
		// temp_data[2] = b_scene_3h_enable ? i_scene_3h_tempature : 0;
		// temp_data[3] = b_scene_4h_enable ? i_scene_4h_tempature : 0;
		// temp_data[4] = b_scene_5h_enable ? i_scene_5h_tempature : 0;
		// temp_data[5] = b_scene_6h_enable ? i_scene_6h_tempature : 0;
		// temp_data[6] = b_scene_7h_enable ? i_scene_7h_tempature : 0;
		// temp_data[7] = b_scene_8h_enable ? i_scene_8h_tempature : 0;
		// temp_data[8] = b_scene_9h_enable ? i_scene_9h_tempature : 0;
		// temp_data[9] = b_scene_10h_enable ? i_scene_10h_tempature : 0;
		// // }

		// for (int i = 0; i < temp_data.length; i++) {
		// if (temp_data[i] == 0)
		// str_timer_commands[i] = "关闭空调";
		// else
		// str_timer_commands[i] = String.valueOf(temp_data[i]) + "度";
		// }

		// pv_aircon_scene_pathview.setLinkPaintColor(b_scene_name_enable?
		// Color.BLUE : Color.GRAY);
		// pv_aircon_scene_pathview.setDate(temp_data);
	}

	private void temperature_increase() {
		parsecommand("增加温度");
		updateImageTemperature();
	}

	private void temperature_descrease() {
		parsecommand("减少温度");
		updateImageTemperature();
	}

	private void aircon_power(boolean b_power) {
		parsecommand(b_power ? "打开空调" : "关闭空调");
	}

	private void aircon_swing() {
		parsecommand("摆风");
		i_current_wind_swing = !i_current_wind_swing;
		updateImageWindSwing();
	}

	private void aircon_volume() {
		parsecommand("风量");
		i_current_wind_volume = (i_current_wind_volume + 1) % 3;
		updateImageWindVolume();
	}

	private void aircon_mode(int i_mode_status) {
		String str_mode = "模式自动";
		switch (i_mode_status) {
			case 0 : // AUTO
				str_mode = "模式自动";
				break;
			case 1 : // COOL
				str_mode = "模式冷风";
				break;
			case 2 : // WET
				str_mode = "模式除湿";
				break;
			case 3 : // WIND
				str_mode = "模式送风";
				break;
			case 4 : // WARM
				str_mode = "模式暖气";
				break;
			default :
				break;
		}

		parsecommand(str_mode);
		updateImageMode(i_mode_status);
	}

	private void updateImageMode(int iMode) {
		iv_view_show_mode.setBackgroundResource(int_mode_res[iMode]);
	}
	private void updateImageWindVolume() {
		iv_view_show_wind_volume
				.setBackgroundResource(int_wind_volume_res[i_current_wind_volume]);
	}
	private void updateImageWindSwing() {
		iv_view_show_wind_swing
				.setBackgroundResource(int_wind_swing_res[i_current_wind_swing == true
						? 0
						: 1]);
	}
	private void updateImageTemperature() {
		iv_view_show_temperature
				.setBackgroundResource(int_temperature_res[i_current_temperature - 16]);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.titlebar_leftbutton : // WiFi模式 退出时，需要close掉 TCP连接
				disconnectSocket();
				finish();
				break;
			case R.id.iv_view_control_low :
				temperature_descrease();
				break;
			case R.id.iv_view_control_high :
				temperature_increase();
				break;
			case R.id.iv_view_power_on :
				aircon_power(true);
				break;
			case R.id.iv_view_power_off :
				aircon_power(false);
				break;
			case R.id.iv_view_wind_swing :
				aircon_swing();
				break;
			case R.id.iv_view_wind_volume :
				aircon_volume();
				break;
			// case R.id.btn_aircon_mode_auto:
			// aircon_mode(0);
			// break;
			case R.id.iv_view_mode_cool :
				aircon_mode(1);
				break;
			case R.id.iv_view_mode_wet :
				aircon_mode(2);
				break;
			case R.id.iv_view_mode_wind :
				aircon_mode(3);
				break;
			case R.id.iv_view_mode_hot :
				aircon_mode(4);
				break;
			case R.id.iv_view_scene_add :
				Intent intent = new Intent();
				intent.setClass(DetailAirCon2Activity.this,
						DetailIRSceneActivity.class);
				intent.putExtra("PLUGID", mPlugId);
				intent.putExtra("PLUGIP", mPlugIp);
				intent.putExtra("DEFAULT", true);
				int irsceneid = mIRSceneHelper.getMaxSceneId(mPlugId);
				intent.putExtra("IRSCENEID", irsceneid);
				intent.putExtra("IRNAME", str_Current_NO);

				startActivity(intent);
				// finish();
				break;
			case R.id.btn_autosearch :
				auto_search();
				break;
			default :
				break;
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

							// int select_no = irlist.indexOf(str_Current_NO);
							// if (select_no < irlist.size() - 1) {
							// // spinner_ir.setSelection(select_no + 1);
							// str_Current_NO = irlist.get(select_no + 1);
							// Message msg = new Message();
							// msg.what = 0;
							// msg.arg1 = select_no + 1;
							// mHandler.sendMessage(msg);
							//
							// } else {
							// this.cancel();
							// timer.cancel();
							// start_search = false;
							// btn_autosearch.setText("开始搜索");
							// }
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
					parsecommand("打开空调");
					break;
				case 1 :
					break;
				default :
					break;
			}
		};
	};

	public class SerializableMap implements Serializable {

		private Map<String, Object> map;

		public Map<String, Object> getMap() {
			return map;
		}

		public void setMap(Map<String, Object> map) {
			this.map = map;
		}
	}

	private String ReadCurrentAR(String id, String sub_id) {
		for (int i = 0; i < m_tjData.size(); i++) {
			if (m_tjData.get(i).get("id").toString().equals(id)
					&& m_tjData.get(i).get("sub_id").toString().equals(sub_id)) {
				return m_tjData.get(i).get("value").toString();
			}
		}
		return null;
	}

	// APP 下发红外命令给serveer，server下发具体的红外数据给模块
	public void parsecommand(String cmd_name) {
		// 播放声音
		mp3_player.start();

		String power_on = "063";
		String power_off = "064";
		String mode_auto = "001";
		String mode_cool = "002";
		String mode_wet = "003";
		String mode_wind = "004";
		String mode_warm = "005";
		String wind_swing = "010";
		String wind_volume = "012";
		String temperatures_16 = "048";
		String temperatures_17 = "049";
		String temperatures_18 = "050";
		String temperatures_19 = "051";
		String temperatures_20 = "052";
		String temperatures_21 = "053";
		String temperatures_22 = "054";
		String temperatures_23 = "055";
		String temperatures_24 = "056";
		String temperatures_25 = "057";
		String temperatures_26 = "058";
		String temperatures_27 = "059";
		String temperatures_28 = "060";
		String temperatures_29 = "061";
		String temperatures_30 = "062";

		// 测试数据
		String tv_mute = "5900,1950,550,1100,600,1050,550,550,550,550,550,1100,600,500,600,1050,550,550,550,600,550,1050,600,550,600,1050,600,1000,600,550,550,1050,600,550,550,1050,600,550,550,1050,600,1050,600,550,550,550,550,1100,550,550,3600,3550,600,550,600,500,550,1100,550,550,600,1050,550,600,550,1050,550,600,550,550,600,1050,550,550,600,500,600,1000,650,1050,550,550,550,1100,600,29914";

		String temperatures[] = {temperatures_16, temperatures_17,
				temperatures_18, temperatures_19, temperatures_20,
				temperatures_21, temperatures_22, temperatures_23,
				temperatures_24, temperatures_25, temperatures_26,
				temperatures_27, temperatures_28, temperatures_29,
				temperatures_30};
		// power_on = temperatures[i_current_temperature - 16];

		String command_com = "";
		if (cmd_name.equalsIgnoreCase("打开空调")) {
			command_com = power_on;
		} else if (cmd_name.equalsIgnoreCase("关闭空调")) {
			command_com = power_off;
		} else if (cmd_name.equalsIgnoreCase("减少温度")) {
			if (i_current_temperature > 16)
				i_current_temperature--;
			updateImageTemperature();
			command_com = temperatures[i_current_temperature - 16];
		} else if (cmd_name.equalsIgnoreCase("增加温度")) {
			if (i_current_temperature < 30)
				i_current_temperature++;
			updateImageTemperature();
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
		} else if (cmd_name.equalsIgnoreCase("摆风")) {
			command_com = wind_swing;
		} else if (cmd_name.equalsIgnoreCase("风量")) {
			command_com = wind_volume;
		} else if (cmd_name.equalsIgnoreCase("电视静音")) {
			command_com = tv_mute;
		} else {
			return;
		}

		PubFunc.log("Cur Temp:" + i_current_temperature);

		// 0,APPAIRCONSERVER,test,12345678,格力,021#
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_AIRCONSERVER)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(str_Current_NO)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(command_com);

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
			sendMsg(true, sb.toString(), true);
		} else { // 非Internet模式下，不处理；
			// do nothing.
		}
	}

	// APP 直接下发红外数据，server只转发
	public void parsecommand_bak(String cmd_name) {
		// 播放声音
		mp3_player.start();

		String power_on = ReadCurrentAR(str_Current_NO, "063");
		String power_off = ReadCurrentAR(str_Current_NO, "064");
		String mode_auto = ReadCurrentAR(str_Current_NO, "001");
		String mode_cool = ReadCurrentAR(str_Current_NO, "002");
		String mode_wet = ReadCurrentAR(str_Current_NO, "003");
		String mode_wind = ReadCurrentAR(str_Current_NO, "004");
		String mode_warm = ReadCurrentAR(str_Current_NO, "005");
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

		String temperatures[] = {temperatures_16, temperatures_17,
				temperatures_18, temperatures_19, temperatures_20,
				temperatures_21, temperatures_22, temperatures_23,
				temperatures_24, temperatures_25, temperatures_26,
				temperatures_27, temperatures_28, temperatures_29,
				temperatures_30};
		power_on = temperatures[i_current_temperature - 16];

		String command_com = "";
		if (cmd_name.equalsIgnoreCase("打开空调")) {
			command_com = power_on;
		} else if (cmd_name.equalsIgnoreCase("关闭空调")) {
			command_com = power_off;
		} else if (cmd_name.equalsIgnoreCase("减少温度")) {
			if (i_current_temperature > 16)
				i_current_temperature--;
			updateImageTemperature();
			command_com = temperatures[i_current_temperature - 16];
		} else if (cmd_name.equalsIgnoreCase("增加温度")) {
			if (i_current_temperature < 30)
				i_current_temperature++;
			updateImageTemperature();
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

		// 0,AIRCON,test,12345678,0,0,150,3000,3000,3050,...#
		String[] temp_strs = command_com.split(",");
		int i_len = temp_strs.length;
		String command_base = "AIRCON," + PubStatus.g_CurUserName + ","
				+ mPlugId + ",0,0," + String.valueOf(i_len) + "," + command_com
				+ "#";
		String command = "0," + command_base;
		byte[] temp_change = changeCommand(command);

		StringBuffer sb = new StringBuffer();
		sb.append("APPPASSTHROUGH")
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
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
			// temp_command = new byte[temp_change.length - 2];
			// for (int k = 0; k < temp_change.length - 2; k++) {
			// temp_command[k] = temp_change[k+2];
			// }
			// sendMsgBin(false, temp_command, true);
			sendMsgBin(false, temp_change, true);
		}
	}

	private byte[] changeCommand(String cmd_text) {
		// final int BUF_SIZE = 1024; // 定义发送的二进制BUFFER的大小。

		byte[] byte_info;
		String revMsg = cmd_text.substring(0, cmd_text.indexOf("#"));
		String arrays[] = revMsg.split(",");
		String cmd = arrays[1];
		int i = 0;
		int j = 0;

		// 红外命令单独处理
		if (arrays[1].equals("AIRCON") == true) {
			// byte_info = new byte[BUF_SIZE];

			// 临时Debug，要删除
			int count = 0;
			int length = Integer.parseInt(arrays[6]);
			for (j = 0; j < length; j++) {
				int value = Integer.parseInt(arrays[7 + j]);
				char a = (char) ((value >> 8) & 0xFF);
				char b = (char) (value & 0xFF);
				count = count + a + b;
			}

			String strHeader = arrays[0] + "," + arrays[1] + "," + arrays[2]
					+ "," + arrays[3] + "," + arrays[4] + "," + arrays[5] + ","
					+ arrays[6] + "," + count + ",";
			byte[] temp = strHeader.getBytes();

			byte_info = new byte[temp.length + Integer.parseInt(arrays[6]) * 2
					+ 1];

			for (i = 0; i < temp.length; i++) {
				byte_info[i] = temp[i];
			}

			length = Integer.parseInt(arrays[6]);
			for (j = 0; j < length; j++) {
				int value = Integer.parseInt(arrays[7 + j]);

				byte_info[i++] = (byte) ((value >> 8) & 0xFF);
				byte_info[i++] = (byte) (value & 0xFF);
			}

			byte_info[i++] = '#';

			// // 增加 \0 空数据
			// for (; i < BUF_SIZE; i++) {
			// byte_info[i] = 0;
			// }

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

		irlist.add(DEFAULT_NOT_SELECT_KONGTAI);
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
				str_Current_NO = DEFAULT_NOT_SELECT_KONGTAI;
				select_no = irlist.indexOf(str_Current_NO);
			}

			spinner_ir.setSelection(select_no);
		}

		// pv_AirCon_NO = (ThingzdoPickerView) findViewById(R.id.pv_AirCon_NO);
		// list_NO_data.clear();
		// list_NO_data.addAll(m_idSet);
		// Collections.sort(list_NO_data);
		// pv_AirCon_NO.setData(list_NO_data);
		// pv_AirCon_NO.setVisibility(View.VISIBLE);
		//
		// pv_AirCon_NO.setOnSelectListener(new onSelectListener()
		// {
		// @Override
		// public void onSelect(String text)
		// {
		// str_Current_NO = text;
		// tv_aircon_select_content.setText(str_Current_NO);
		// }
		// });
		//
		//
		// int select_no = list_NO_data.indexOf(str_Current_NO);
		// if (select_no == -1) {
		// str_Current_NO = "0001";
		// select_no = list_NO_data.indexOf(str_Current_NO);
		// }
		// pv_AirCon_NO.setSelected(select_no);
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

	private Handler mIRScenerHandler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;
			// mFocusTimerId = msg.arg1;
			// mTimer = mIRScenerHelper.getTimer(mPlugId, mFocusTimerId);
			// if (null == mTimer) {
			// return;
			// }
			//
			// if (1 == what) {
			// //enabled
			// mFocusTimerEnabled = !mTimer.mEnable;
			// enabledTimer(mFocusTimerEnabled);
			//
			// } else {
			// //delete
			// deleteTimer();
			// }
		};
	};

	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}

		// first　init Aircon
		// list_NO_data.addAll(m_idSet);
		// Collections.sort(list_NO_data);
		// pv_AirCon_NO.setData(list_NO_data);
		// pv_AirCon_NO.setVisibility(View.GONE);
		//
		// pv_AirCon_NO.setOnSelectListener(new onSelectListener()
		// {
		// @Override
		// public void onSelect(String text)
		// {
		// str_Current_NO = text;
		// tv_aircon_select_content.setText(str_Current_NO);
		// }
		// });

		loadData();
		// int select_no = list_NO_data.indexOf(str_Current_NO);
		// if (select_no == -1) {
		// str_Current_NO = "0001";
		// select_no = list_NO_data.indexOf(str_Current_NO);
		// }
		// pv_AirCon_NO.setSelected(select_no);

		// 初始化模式图片资源
		int_mode_res[0] = R.drawable.mode_auto;
		int_mode_res[1] = R.drawable.mode_cool;
		int_mode_res[2] = R.drawable.mode_wet;
		int_mode_res[3] = R.drawable.mode_wind;
		int_mode_res[4] = R.drawable.mode_hot;

		// 初始化风量图片资源
		int_wind_volume_res[0] = R.drawable.volume_small;
		int_wind_volume_res[1] = R.drawable.volume_middle;
		int_wind_volume_res[2] = R.drawable.volume_big;

		// 初始化摆动图片资源
		int_wind_swing_res[0] = R.drawable.swing_on;
		int_wind_swing_res[1] = R.drawable.swing_off;

		// 初始化温度图片资源
		int_temperature_res[0] = R.drawable.temp_16;
		int_temperature_res[1] = R.drawable.temp_17;
		int_temperature_res[2] = R.drawable.temp_18;
		int_temperature_res[3] = R.drawable.temp_19;
		int_temperature_res[4] = R.drawable.temp_20;
		int_temperature_res[5] = R.drawable.temp_21;
		int_temperature_res[6] = R.drawable.temp_22;
		int_temperature_res[7] = R.drawable.temp_23;
		int_temperature_res[8] = R.drawable.temp_24;
		int_temperature_res[9] = R.drawable.temp_25;
		int_temperature_res[10] = R.drawable.temp_26;
		int_temperature_res[11] = R.drawable.temp_27;
		int_temperature_res[12] = R.drawable.temp_28;
		int_temperature_res[13] = R.drawable.temp_29;
		int_temperature_res[14] = R.drawable.temp_30;

		// 初始化场景列表的图片资源
		int_item_scene_power[0] = R.drawable.item_poweron;
		int_item_scene_power[1] = R.drawable.item_poweroff;

		int_item_scene_mode[0] = R.drawable.item_mode_auto;
		int_item_scene_mode[1] = R.drawable.item_mode_cool;
		int_item_scene_mode[2] = R.drawable.item_mode_wet;
		int_item_scene_mode[3] = R.drawable.item_mode_wind;
		int_item_scene_mode[4] = R.drawable.item_mode_hot;

		int_item_scene_swing[0] = R.drawable.item_swing_on;
		int_item_scene_swing[1] = R.drawable.item_swing_off;

		int_item_scene_volume[0] = R.drawable.item_volume_auto;
		int_item_scene_volume[1] = R.drawable.item_volume_small;
		int_item_scene_volume[2] = R.drawable.item_volume_middle;
		int_item_scene_volume[3] = R.drawable.item_volume_big;

		int_item_scene_week_7[0] = R.drawable.week_7_on;
		int_item_scene_week_7[1] = R.drawable.week_7_off;

		int_item_scene_week_6[0] = R.drawable.week_6_on;
		int_item_scene_week_6[1] = R.drawable.week_6_off;

		int_item_scene_week_5[0] = R.drawable.week_5_on;
		int_item_scene_week_5[1] = R.drawable.week_5_off;

		int_item_scene_week_4[0] = R.drawable.week_4_on;
		int_item_scene_week_4[1] = R.drawable.week_4_off;

		int_item_scene_week_3[0] = R.drawable.week_3_on;
		int_item_scene_week_3[1] = R.drawable.week_3_off;

		int_item_scene_week_2[0] = R.drawable.week_2_on;
		int_item_scene_week_2[1] = R.drawable.week_2_off;

		int_item_scene_week_1[0] = R.drawable.week_1_on;
		int_item_scene_week_1[1] = R.drawable.week_1_off;

		int_item_scene_enable[0] = R.drawable.smp_switcher_close;
		int_item_scene_enable[1] = R.drawable.smp_switcher_open;

		tv_aircon_select_content = (TextView) findViewById(R.id.tv_aircon_select_content);
		iv_view_show_mode = (ImageView) findViewById(R.id.iv_view_show_mode);
		iv_view_show_wind_volume = (ImageView) findViewById(R.id.iv_view_show_wind_volume);
		iv_view_show_wind_swing = (ImageView) findViewById(R.id.iv_view_show_wind_swing);
		iv_view_show_temperature = (ImageView) findViewById(R.id.iv_view_show_temperature);

		iv_view_control_low = (ImageView) findViewById(R.id.iv_view_control_low);
		iv_view_control_high = (ImageView) findViewById(R.id.iv_view_control_high);

		iv_view_power_on = (ImageView) findViewById(R.id.iv_view_power_on);
		iv_view_power_off = (ImageView) findViewById(R.id.iv_view_power_off);
		iv_view_wind_swing = (ImageView) findViewById(R.id.iv_view_wind_swing);
		iv_view_wind_volume = (ImageView) findViewById(R.id.iv_view_wind_volume);
		iv_view_mode_hot = (ImageView) findViewById(R.id.iv_view_mode_hot);
		iv_view_mode_cool = (ImageView) findViewById(R.id.iv_view_mode_cool);
		iv_view_mode_wet = (ImageView) findViewById(R.id.iv_view_mode_wet);
		iv_view_mode_wind = (ImageView) findViewById(R.id.iv_view_mode_wind);

		iv_view_scene_add = (ImageView) findViewById(R.id.iv_view_scene_add);

		iv_view_control_low.setOnClickListener(this);
		iv_view_control_high.setOnClickListener(this);
		iv_view_power_on.setOnClickListener(this);
		iv_view_power_off.setOnClickListener(this);
		iv_view_wind_swing.setOnClickListener(this);
		iv_view_wind_volume.setOnClickListener(this);
		iv_view_mode_hot.setOnClickListener(this);
		iv_view_mode_cool.setOnClickListener(this);
		iv_view_mode_wet.setOnClickListener(this);
		iv_view_mode_wind.setOnClickListener(this);
		iv_view_scene_add.setOnClickListener(this);

		spinner_ir = (Spinner) findViewById(R.id.spinner_ir);
		btn_autosearch = (Button) findViewById(R.id.btn_autosearch);
		btn_autosearch.setOnClickListener(this);

		lv_scene_show = (SwipeMenuListView) findViewById(R.id.lv_scene_show);

		// Init m_tjSceneData
		m_tjSceneData.clear();
		ArrayList<IRSceneDefine> timers = mIRSceneHelper.getAllIRScene(mPlugId);
		for (IRSceneDefine ir : timers) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("iv_item_irsceneid", ir.mIRSceneId);
			data.put("iv_item_power", ir.mPower);
			data.put("iv_item_mode", ir.mMode);
			data.put("iv_item_swing", ir.mDirection);
			data.put("iv_item_volume", ir.mScale);
			data.put("tv_item_temperature", ir.mTemperature);

			data.put("iv_item_week_7",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(0))));
			data.put("iv_item_week_1",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(1))));
			data.put("iv_item_week_2",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(2))));
			data.put("iv_item_week_3",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(3))));
			data.put("iv_item_week_4",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(4))));
			data.put("iv_item_week_5",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(5))));
			data.put("iv_item_week_6",
					Integer.valueOf(String.valueOf(ir.mPeriod.charAt(6))));

			data.put("ir.mPeriod.charAt(0)", "日一二三四五六");

			data.put("tv_item_alarm", ir.mTime);
			data.put("iv_item_enable", ir.mEnable);

			m_tjSceneData.add(data);
		}

		sceneAdapter = new SceneAdapter(this);
		lv_scene_show.setAdapter(sceneAdapter);

		lv_scene_show.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos,
					long arg3) {
				Intent intent = new Intent();
				intent.setClass(DetailAirCon2Activity.this,
						DetailIRSceneActivity.class);
				intent.putExtra("PLUGID", mPlugId);
				intent.putExtra("PLUGIP", mPlugIp);
				intent.putExtra("DEFAULT", false);
				intent.putExtra("IRNAME", str_Current_NO);

				// HashMap<String, Object> map = (HashMap<String, Object>)
				// arg0.getItemAtPosition(pos);
				HashMap<String, Object> map = (HashMap<String, Object>) m_tjSceneData
						.get(pos);
				intent.putExtra("MAPDATA", map);

				// intent.putExtra("IRSCENEID",
				// (Integer)map.get("iv_item_irsceneid"));

				// // 传递MAP对象
				// Map<String, Object> data = m_tjSceneData.get(pos);
				// SerializableMap myMap = new SerializableMap();
				// myMap.setMap(data);//将map数据添加到封装的myMap中
				// Bundle bundle = new Bundle();
				// bundle.putSerializable("MAPDATA", myMap);
				// intent.putExtras(bundle);
				DetailAirCon2Activity.this.startActivity(intent);
			}
		});

		// //ListView item的点击事件
		// lv_scene_show.setOnItemClickListener(new
		// SceneAdapter.OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> adapterView, View view, int i,
		// long l) {
		//
		// }
		// });
		// //ListView item 中的删除按钮的点击事件
		// lv_scene_show.setOnItemDeleteClickListener(new
		// SceneAdapter.onItemDeleteListener() {
		// @Override
		// public void onDeleteClick(int i) {
		// lv_scene_show.remove(i);
		// sceneAdapter.notifyDataSetChanged();
		// }
		// });

		// PathView
		// pv_aircon_scene_pathview =
		// (PathView)findViewById(R.id.pv_aircon_scene_pathview);
		// pv_aircon_scene_pathview.setXCount(30, 10);
		// pv_aircon_scene_pathview.setType(PathView.HOUR_DAY);
		// updatePath();

		updateImageTemperature();

		// tempControl = (TempControlView) findViewById(R.id.temp_control);
		// tempControl.setTemp(15, 30, 15);
		//
		// tempControl.setOnTempChangeListener(new
		// TempControlView.OnTempChangeListener() {
		// @Override
		// public void change(int temp) {
		// Toast.makeText(DetailAirCon2Activity.this, temp + "°",
		// Toast.LENGTH_SHORT).show();
		// }
		// });
	}

	private void updateImageScene() {
		sceneAdapter.notifyDataSetChanged();
	}

	public class SceneAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public SceneAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return m_tjSceneData.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder_listitem holder = null;
			if (convertView == null) {
				holder = new ViewHolder_listitem();

				convertView = mInflater.inflate(
						R.layout.activity_detail_aircon2_scene_item, null);

				holder.tv_item_no = (TextView) convertView
						.findViewById(R.id.tv_item_no);
				holder.iv_item_power = (ImageView) convertView
						.findViewById(R.id.iv_item_power);
				holder.iv_item_mode = (ImageView) convertView
						.findViewById(R.id.iv_item_mode);
				holder.iv_item_swing = (ImageView) convertView
						.findViewById(R.id.iv_item_swing);
				holder.iv_item_volume = (ImageView) convertView
						.findViewById(R.id.iv_item_volume);
				holder.tv_item_temperature = (TextView) convertView
						.findViewById(R.id.tv_item_temperature);
				holder.iv_item_week_7 = (ImageView) convertView
						.findViewById(R.id.iv_item_week_7);
				holder.iv_item_week_6 = (ImageView) convertView
						.findViewById(R.id.iv_item_week_6);
				holder.iv_item_week_5 = (ImageView) convertView
						.findViewById(R.id.iv_item_week_5);
				holder.iv_item_week_4 = (ImageView) convertView
						.findViewById(R.id.iv_item_week_4);
				holder.iv_item_week_3 = (ImageView) convertView
						.findViewById(R.id.iv_item_week_3);
				holder.iv_item_week_2 = (ImageView) convertView
						.findViewById(R.id.iv_item_week_2);
				holder.iv_item_week_1 = (ImageView) convertView
						.findViewById(R.id.iv_item_week_1);
				holder.tv_timer_period = (TextView) convertView
						.findViewById(R.id.tv_timer_period);
				holder.tv_item_alarm = (TextView) convertView
						.findViewById(R.id.tv_item_alarm);
				holder.iv_item_enable = (ImageView) convertView
						.findViewById(R.id.iv_item_enable);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder_listitem) convertView.getTag();
			}

			if (holder != null && m_tjSceneData != null
					&& position < m_tjSceneData.size()) {
				Map<String, Object> data = m_tjSceneData.get(position);
				convertView.setBackgroundColor(Color.TRANSPARENT);
				holder.ViewData(data, position);
			}

			// holder.mButton.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// mOnItemDeleteListener.onDeleteClick(i);
			// }
			// });

			return convertView;
		}
	}

	/**
	 * 删除按钮的监听接口
	 */
	public interface onItemDeleteListener {
		void onDeleteClick(int i);
	}

	private onItemDeleteListener mOnItemDeleteListener;

	public void setOnItemDeleteClickListener(
			onItemDeleteListener mOnItemDeleteListener) {
		this.mOnItemDeleteListener = mOnItemDeleteListener;
	}

	// ------------------------------------------------------
	public final class ViewHolder_listitem {
		public TextView tv_item_no;
		public ImageView iv_item_power;
		public ImageView iv_item_mode;
		public ImageView iv_item_swing;
		public ImageView iv_item_volume;
		public ImageView iv_item_temperature;
		public TextView tv_item_temperature;

		public ImageView iv_item_week_7;
		public ImageView iv_item_week_1;
		public ImageView iv_item_week_2;
		public ImageView iv_item_week_3;
		public ImageView iv_item_week_4;
		public ImageView iv_item_week_5;
		public ImageView iv_item_week_6;

		public TextView tv_timer_period;

		public ImageView iv_item_alarm;
		public TextView tv_item_alarm;
		public ImageView iv_item_enable;

		public void ViewData(Map<String, Object> data, final int position) {
			tv_item_no.setText(String.valueOf(position + 1));

			iv_item_power
					.setBackgroundResource(int_item_scene_power[(Integer) data
							.get("iv_item_power")]);
			iv_item_mode
					.setBackgroundResource(int_item_scene_mode[(Integer) data
							.get("iv_item_mode")]);
			iv_item_swing
					.setBackgroundResource(int_item_scene_swing[(Integer) data
							.get("iv_item_swing")]);
			iv_item_volume
					.setBackgroundResource(int_item_scene_volume[(Integer) data
							.get("iv_item_volume")]);
			tv_item_temperature.setText(String.valueOf((Integer) data
					.get("tv_item_temperature")) + "℃");
			// tv_item_temperature.setText(String.valueOf((Integer)data.get("tv_item_temperature"))
			// + "℃ -" +
			// String.valueOf((Integer)data.get("iv_item_irsceneid")));

			// 周期显示方式一
			// iv_item_week_7.setBackgroundResource(int_item_scene_week_7[(Integer)data.get("iv_item_week_7")]);
			// iv_item_week_1.setBackgroundResource(int_item_scene_week_1[(Integer)data.get("iv_item_week_1")]);
			// iv_item_week_2.setBackgroundResource(int_item_scene_week_2[(Integer)data.get("iv_item_week_2")]);
			// iv_item_week_3.setBackgroundResource(int_item_scene_week_3[(Integer)data.get("iv_item_week_3")]);
			// iv_item_week_4.setBackgroundResource(int_item_scene_week_4[(Integer)data.get("iv_item_week_4")]);
			// iv_item_week_5.setBackgroundResource(int_item_scene_week_5[(Integer)data.get("iv_item_week_5")]);
			// iv_item_week_6.setBackgroundResource(int_item_scene_week_6[(Integer)data.get("iv_item_week_6")]);

			String str_tv_timer_period = "";
			// 周期显示方式二
			if ((Integer) data.get("iv_item_week_7") == 1) {
				iv_item_week_7.setVisibility(View.VISIBLE);
				iv_item_week_7.setBackgroundResource(int_item_scene_week_7[0]);
				str_tv_timer_period += "日";
			} else {
				iv_item_week_7.setVisibility(View.GONE);
			}
			if ((Integer) data.get("iv_item_week_1") == 1) {
				iv_item_week_1.setVisibility(View.VISIBLE);
				iv_item_week_1.setBackgroundResource(int_item_scene_week_1[0]);
				str_tv_timer_period += "一";
			} else {
				iv_item_week_1.setVisibility(View.GONE);
			}
			if ((Integer) data.get("iv_item_week_2") == 1) {
				iv_item_week_2.setVisibility(View.VISIBLE);
				iv_item_week_2.setBackgroundResource(int_item_scene_week_2[0]);
				str_tv_timer_period += "二";
			} else {
				iv_item_week_2.setVisibility(View.GONE);
			}
			if ((Integer) data.get("iv_item_week_3") == 1) {
				iv_item_week_3.setVisibility(View.VISIBLE);
				iv_item_week_3.setBackgroundResource(int_item_scene_week_3[0]);
				str_tv_timer_period += "三";
			} else {
				iv_item_week_3.setVisibility(View.GONE);
			}
			if ((Integer) data.get("iv_item_week_4") == 1) {
				iv_item_week_4.setVisibility(View.VISIBLE);
				iv_item_week_4.setBackgroundResource(int_item_scene_week_4[0]);
				str_tv_timer_period += "四";
			} else {
				iv_item_week_4.setVisibility(View.GONE);
			}
			if ((Integer) data.get("iv_item_week_5") == 1) {
				iv_item_week_5.setVisibility(View.VISIBLE);
				iv_item_week_5.setBackgroundResource(int_item_scene_week_5[0]);
				str_tv_timer_period += "五";
			} else {
				iv_item_week_5.setVisibility(View.GONE);
			}
			if ((Integer) data.get("iv_item_week_6") == 1) {
				iv_item_week_6.setVisibility(View.VISIBLE);
				iv_item_week_6.setBackgroundResource(int_item_scene_week_6[0]);
				str_tv_timer_period += "六";
			} else {
				iv_item_week_6.setVisibility(View.GONE);
			}

			iv_item_week_7.setVisibility(View.GONE);
			iv_item_week_1.setVisibility(View.GONE);
			iv_item_week_2.setVisibility(View.GONE);
			iv_item_week_3.setVisibility(View.GONE);
			iv_item_week_4.setVisibility(View.GONE);
			iv_item_week_5.setVisibility(View.GONE);
			iv_item_week_6.setVisibility(View.GONE);

			tv_timer_period.setText(str_tv_timer_period);

			tv_item_alarm.setText((String) data.get("tv_item_alarm"));
			iv_item_enable
					.setBackgroundResource(int_item_scene_enable[(Integer) data
							.get("iv_item_enable")]);

			// iv_item_power.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// int value =
			// (Integer)m_tjSceneData.get(position).get("iv_item_power");
			// m_tjSceneData.get(position).put("iv_item_power", (value + 1) %
			// INT_ITEM_SCENE_POWER);
			// updateImageScene();
			// }
			// });
			//
			// iv_item_mode.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// int value =
			// (Integer)m_tjSceneData.get(position).get("iv_item_mode");
			// m_tjSceneData.get(position).put("iv_item_mode", (value + 1) %
			// INT_ITEM_SCENE_MODE);
			// updateImageScene();
			// }
			// });
			//
			// iv_item_swing.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// int value =
			// (Integer)m_tjSceneData.get(position).get("iv_item_swing");
			// m_tjSceneData.get(position).put("iv_item_swing", (value + 1) %
			// INT_ITEM_SCENE_SWING);
			// updateImageScene();
			// }
			// });
			//
			//
			// iv_item_volume.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// int value =
			// (Integer)m_tjSceneData.get(position).get("iv_item_volume");
			// m_tjSceneData.get(position).put("iv_item_volume", (value + 1) %
			// INT_ITEM_SCENE_VOLUME);
			// updateImageScene();
			// }
			// });
			//
			// iv_item_week_7.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// int value =
			// (Integer)m_tjSceneData.get(position).get("iv_item_week_7");
			// m_tjSceneData.get(position).put("iv_item_week_7", (value + 1) %
			// INT_ITEM_SCENE_WEEK_7);
			// updateImageScene();
			// }
			// });
			//
			//
			// iv_item_week_6.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// int value =
			// (Integer)m_tjSceneData.get(position).get("iv_item_week_6");
			// m_tjSceneData.get(position).put("iv_item_week_6", (value + 1) %
			// INT_ITEM_SCENE_WEEK_6);
			// updateImageScene();
			// }
			// });
			//
			//
			// iv_item_week_5.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// int value =
			// (Integer)m_tjSceneData.get(position).get("iv_item_week_5");
			// m_tjSceneData.get(position).put("iv_item_week_5", (value + 1) %
			// INT_ITEM_SCENE_WEEK_5);
			// updateImageScene();
			// }
			// });
			//
			//
			// iv_item_week_4.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// int value =
			// (Integer)m_tjSceneData.get(position).get("iv_item_week_4");
			// m_tjSceneData.get(position).put("iv_item_week_4", (value + 1) %
			// INT_ITEM_SCENE_WEEK_4);
			// updateImageScene();
			// }
			// });
			//
			//
			// iv_item_week_3.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// int value =
			// (Integer)m_tjSceneData.get(position).get("iv_item_week_3");
			// m_tjSceneData.get(position).put("iv_item_week_3", (value + 1) %
			// INT_ITEM_SCENE_WEEK_3);
			// updateImageScene();
			// }
			// });
			//
			//
			// iv_item_week_2.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// int value =
			// (Integer)m_tjSceneData.get(position).get("iv_item_week_2");
			// m_tjSceneData.get(position).put("iv_item_week_2", (value + 1) %
			// INT_ITEM_SCENE_WEEK_2);
			// updateImageScene();
			// }
			// });
			//
			//
			// iv_item_week_1.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// int value =
			// (Integer)m_tjSceneData.get(position).get("iv_item_week_1");
			// m_tjSceneData.get(position).put("iv_item_week_1", (value + 1) %
			// INT_ITEM_SCENE_WEEK_1);
			// updateImageScene();
			// }
			// });
			//
			// iv_item_enable.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// int value =
			// (Integer)m_tjSceneData.get(position).get("iv_item_enable");
			// m_tjSceneData.get(position).put("iv_item_enable", (value + 1) %
			// INT_ITEM_SCENE_ENABLE);
			// updateImageScene();
			// }
			// });
		}
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
		updateImageTemperature();
	}

	private void parseJSONWithGSON(String jsonData) {
		Gson gson = new Gson();
		List<GSON_IR_AIRCON> itemsList = gson.fromJson(jsonData,
				new TypeToken<List<GSON_IR_AIRCON>>() {
				}.getType());
		Map<String, Object> map = null;

		m_tjData.clear();

		for (GSON_IR_AIRCON item : itemsList) {
			map = new HashMap<String, Object>();

			map.put("id", item.getId());
			map.put("sub_id", item.getSub_id());
			map.put("value", item.getValue());

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

}
