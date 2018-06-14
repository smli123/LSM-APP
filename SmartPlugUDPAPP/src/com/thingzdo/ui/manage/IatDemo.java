package com.thingzdo.ui.manage;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.WelcomeActivity;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.control.PlugDetailActivity;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.ui.wheelutils.ActionSheetDialog;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.OnSheetItemClickListener;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.SheetItemColor;
import com.thingzdo.util.DateUtils;
import com.thingzdo.util.FucUtil;
import com.thingzdo.util.JsonParser;

public class IatDemo extends TitledActivity implements OnClickListener, TextToSpeech.OnInitListener {
	private static String TAG = "IatDemo";
	
	private final String CONTROL_COMMAND_OPEN = "开";
	private final String CONTROL_COMMAND_CLOSE = "关";
	private final String CONTROL_COMMAND_PAUSE = "暂停";
	
	public final static int  MSG_NOTIFY_INIT					= 0;
	public final static int  MSG_NOTIFY_SUCCESS					= 1;
	public final static int  MSG_NOTIFY_FAIL					= 2;
	public final static int  MSG_NOTIFY_NETWORK_FAIL			= 3;
	public final static int  MSG_UPDATE_LISTVIEW_SCANPORT		= 4;
	public final static int  MSG_UPDATE_LISTVIEW_SCANPORT_END	= 5;
	public final static int  MSG_UPDATE_SCANBTN_ENABLE			= 6;
	public final static int  MSG_UPDATE_SCANBTN_DISABLE			= 7;
	public final static int  MSG_UPDATE_SHOW_INFO				= 8;
	public final static int  MSG_SHOW_MESSAGE 					= 9;
	public final static int  MSG_UPDATE_TJDATA 					= 10;
	public final static int  MSG_UPDATE_TJDATA_BAK 				= 11;
	private final static int MSG_READ_CITY_WEATHER				= 12;

	public final static int  MSG_UPDATE_TEXTVIEW				= 20;
//	public final static int  MSG_UPDATE_TEXTVIEW_PC				= 21;
	public final static int  MSG_UPDATE_ZHUWO_KONGTIAO			= 22;
	public final static int  MSG_UPDATE_CIWO_KONGTIAO			= 23;
	public final static int  MSG_UPDATE_SHUFANG_KONGTIAO		= 24;
	public final static int  MSG_UPDATE_KETING_KONGTIAO			= 25;
	public final static int  MSG_UPDATE_ZHUWO_CURTAIN			= 26;
	public final static int  MSG_UPDATE_CIWO_CURTAIN			= 27;
	public final static int  MSG_UPDATE_SHUFANG_CURTAIN			= 28;
	public final static int  MSG_UPDATE_KETING_CURTAIN			= 29;
	public final static int  MSG_UPDATE_ZHUWO_SMARTPLUG			= 30;
	public final static int  MSG_UPDATE_CIWO_SMARTPLUG			= 31;
	public final static int  MSG_UPDATE_SHUFANG_SMARTPLUG		= 32;
	public final static int  MSG_UPDATE_KETING_SMARTPLUG		= 33;
	public final static int  MSG_UPDATE_KETING_PC				= 34;
	
	private final static int THREAD_SLEEP_TIME					= 500;

	private final static String DEFAULT_KONGTIAO_NAME			= "not set";
	private int msg_notify = MSG_NOTIFY_INIT;
	
	private SmartPlugHelper mPlugHelper = null;
	ArrayList<SmartPlugDefine> moduleids;
	ArrayList<String> PC_modulenames = new ArrayList<String>();
	ArrayList<String> PC_moduleids = new ArrayList<String>();
	ArrayList<String> PC_modulenamemacs = new ArrayList<String>();
	ArrayList<String> PC_moduleidmacs = new ArrayList<String>();
	ArrayList<String> PC_modulemacs = new ArrayList<String>();
	
	private TextToSpeech tSpeech = null;
	private String str_welcome = "我在";
	
	private boolean b_speech = true;
	private int i_reg_delay_time = 1000;	// 唤醒词后延迟的时间

	private SpeechRecognizer mIat;			// 语音听写对象
	private RecognizerDialog iatDialog;		// 语音听写UI
	
	private TextView mResultText;			// 听写结果内容控件
	private Button btn_wakeup;
	private boolean flag_voice_recognize = true;
	
	private Map<String, String> map_Key_ModuleID = new HashMap<String, String>();
	private Map<String, String> map_ModuleID_KongtiaoType = new HashMap<String, String>();

	private TextView tv_name_zhuwo_kongtiao;
	private TextView tv_name_ciwo_kongtiao;
	private TextView tv_name_shufang_kongtiao;
	private TextView tv_name_keting_kongtiao;
	private TextView tv_name_zhuwo_curtain;
	private TextView tv_name_ciwo_curtain;
	private TextView tv_name_shufang_curtain;
	private TextView tv_name_keting_curtain;
	private TextView tv_name_zhuwo_smartplug;
	private TextView tv_name_ciwo_smartplug;
	private TextView tv_name_shufang_smartplug;
	private TextView tv_name_keting_smartplug;
	private TextView tv_name_keting_pc;
	
	private TextView tv_moduleid_zhuwo_kongtiao;
	private TextView tv_moduleid_ciwo_kongtiao;
	private TextView tv_moduleid_shufang_kongtiao;
	private TextView tv_moduleid_keting_kongtiao;
	private TextView tv_moduleid_zhuwo_curtain;
	private TextView tv_moduleid_ciwo_curtain;
	private TextView tv_moduleid_shufang_curtain;
	private TextView tv_moduleid_keting_curtain;
	private TextView tv_moduleid_zhuwo_smartplug;
	private TextView tv_moduleid_ciwo_smartplug;
	private TextView tv_moduleid_shufang_smartplug;
	private TextView tv_moduleid_keting_smartplug;
	private TextView tv_moduleid_keting_pc;

	private Toast mToast;

	private VoiceWakeuper mIvw;					// 语音唤醒对象
	private SpeechRecognizer mAsr;				// 语音识别对象
	private String resultString;				// 唤醒结果内容
	private String recoString;					// 识别结果内容

	// 设置门限值 ： 门限值越低越容易被唤醒
	private int curThresh = 10;
	private int oneshot_curThresh = -20;			//-20;

	private String keep_alive = "1";
	/**
	 * 闭环优化网络模式有三种： 模式0：关闭闭环优化功能
	 * 
	 * 模式1：开启闭环优化功能，允许上传优化数据。需开发者自行管理优化资源。
	 * sdk提供相应的查询和下载接口，请开发者参考API文档，具体使用请参考本示例 queryResource及downloadResource方法；
	 * 
	 * 模式2：开启闭环优化功能，允许上传优化数据及启动唤醒时进行资源查询下载； 本示例为方便开发者使用仅展示模式0和模式2；
	 */
	private String ivwNetMode = "0";

	// 唤醒+识别
	private String mCloudGrammar = null;	// 云端语法文件
	private String mLocalGrammar = null;	// 本地语法文件
	private String mCloudGrammarID;			// 云端语法id
	private String mLocalGrammarID;			// 本地语法id
	
	// 本地语法构建路径
	private String grmPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test";
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;

	// 公共变量
	private SharedPreferences mSharedPreferences;
	private SharedPreferences mSharedPreferences_parameter;
	private SharedPreferences mSharedPreferences_ir_data;
	private SharedPreferences.Editor editor;
	
	String cur_city = "";
	String note_content = "";

	private Context mContext = null;

	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_iat, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);	
		
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		setTitleRightButton(R.string.smartplug_ok, R.drawable.title_btn_selector, this);
		setTitle("语音控制");
		
		mContext = this;

		mPlugHelper = new SmartPlugHelper(this);
		moduleids = mPlugHelper.getAllSmartPlug();
		getPCModuleMac();
		
		initLayout();
		
		// 初始化识别对象
		 mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
		// 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
		 iatDialog = new RecognizerDialog(this, mInitListener);

		mSharedPreferences = getSharedPreferences("IAT_SETTING" + PubStatus.g_CurUserName, Activity.MODE_PRIVATE);
		mSharedPreferences_parameter = getSharedPreferences("PARAMETERCONFIG" + PubStatus.g_CurUserName, Activity.MODE_PRIVATE);
		mSharedPreferences_ir_data = getSharedPreferences("IR_AIRCON" + PubStatus.g_CurUserName, Activity.MODE_PRIVATE);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mResultText = (TextView) findViewById(R.id.iat_text);
		mResultText.setVisibility(View.VISIBLE);

		// 初始化唤醒对象
		mIvw = VoiceWakeuper.createWakeuper(this, null);
		// 初始化识别对象---唤醒+识别,用来构建语法
		mAsr = SpeechRecognizer.createRecognizer(this, null);
		
		// 初始化语法文件
		mCloudGrammar = FucUtil.readFile(this, "wake_grammar_sample.abnf", "utf-8");
		mLocalGrammar = FucUtil.readFile(this, "wake.bnf", "utf-8");
		buildGrammer();
		
		loadData();
		
		showValue();
		
		if (mIat == null || iatDialog == null || mIvw == null || mAsr == null) {
			showTip("mIat null");
		}

		// 延迟方法二
		new Handler().postDelayed(new Runnable() {

			public void run() {
				btn_wakeup.performClick();
			}
		}, 2000);
	}
	
	private void getPCModuleMac() {
		for (int i = 0; i < moduleids.size(); i++) {
			SmartPlugDefine  module = moduleids.get(i);
			ArrayList<String> macs = mPlugHelper.getAllSmartPlugMac(module.mPlugId);
			ArrayList<String> macs_show = mPlugHelper.getAllSmartPlugMacShow(module.mPlugId);
			ArrayList<String> macs_id = mPlugHelper.getAllSmartPlugMacID(module.mPlugId);
			if (module.mPlugId.equals("") == false) {
				if (macs.size() > 0) {
					for (int j = 0; j < macs_id.size(); j++) {
						PC_modulenames.add(module.mPlugName);
						PC_moduleids.add(module.mPlugId);
						PC_modulenamemacs.add(macs_show.get(j));
						PC_moduleidmacs.add(macs_id.get(j));
						PC_modulemacs.add(macs.get(j));
					}
				}
			}
		}
	}
	
	private void showValue() {
		String key = "";
		key = "主卧空调";
		if (map_Key_ModuleID.containsKey(key)) {
			String moduleID = map_Key_ModuleID.get(key);
			SmartPlugDefine module = getSmartPlug(moduleID);
			if (module != null) {
				String kongtiao_type = map_ModuleID_KongtiaoType.get(moduleID);
				if (kongtiao_type == null) {
					kongtiao_type = DEFAULT_KONGTIAO_NAME;
				}
				String show_name = module.mPlugName + "(" + kongtiao_type + ")";
				tv_name_zhuwo_kongtiao.setText(show_name);
				tv_moduleid_zhuwo_kongtiao.setText(module.mPlugId);
			}
		}

		key = "次卧空调";
		if (map_Key_ModuleID.containsKey(key)) {
			String moduleID = map_Key_ModuleID.get(key);
			SmartPlugDefine module = getSmartPlug(moduleID);
			if (module != null) {
				String kongtiao_type = map_ModuleID_KongtiaoType.get(moduleID);
				if (kongtiao_type == null) {
					kongtiao_type = DEFAULT_KONGTIAO_NAME;
				}
				String show_name = module.mPlugName + "(" + kongtiao_type + ")";
				tv_name_ciwo_kongtiao.setText(show_name);
				tv_moduleid_ciwo_kongtiao.setText(module.mPlugId);
			}
		}

		key = "书房空调";
		if (map_Key_ModuleID.containsKey(key)) {
			String moduleID = map_Key_ModuleID.get(key);
			SmartPlugDefine module = getSmartPlug(moduleID);
			if (module != null) {
				String kongtiao_type = map_ModuleID_KongtiaoType.get(moduleID);
				if (kongtiao_type == null) {
					kongtiao_type = DEFAULT_KONGTIAO_NAME;
				}
				String show_name = module.mPlugName + "(" + kongtiao_type + ")";
				tv_name_shufang_kongtiao.setText(show_name);
				tv_moduleid_shufang_kongtiao.setText(module.mPlugId);
			}
		}

		key = "客厅空调";
		if (map_Key_ModuleID.containsKey(key)) {
			String moduleID = map_Key_ModuleID.get(key);
			SmartPlugDefine module = getSmartPlug(moduleID);
			if (module != null) {
				String kongtiao_type = map_ModuleID_KongtiaoType.get(moduleID);
				if (kongtiao_type == null) {
					kongtiao_type = DEFAULT_KONGTIAO_NAME;
				}
				String show_name = module.mPlugName + "(" + kongtiao_type + ")";
				tv_name_keting_kongtiao.setText(show_name);
				tv_moduleid_keting_kongtiao.setText(module.mPlugId);
			}
		}

		key = "主卧窗帘";
		if (map_Key_ModuleID.containsKey(key)) {
			String moduleID = map_Key_ModuleID.get(key);
			SmartPlugDefine module = getSmartPlug(moduleID);
			if (module != null) {
				tv_name_zhuwo_curtain.setText(module.mPlugName);
				tv_moduleid_zhuwo_curtain.setText(module.mPlugId);
			}
		}

		key = "次卧窗帘";
		if (map_Key_ModuleID.containsKey(key)) {
			String moduleID = map_Key_ModuleID.get(key);
			SmartPlugDefine module = getSmartPlug(moduleID);
			if (module != null) {
				tv_name_ciwo_curtain.setText(module.mPlugName);
				tv_moduleid_ciwo_curtain.setText(module.mPlugId);
			}
		}

		key = "书房窗帘";
		if (map_Key_ModuleID.containsKey(key)) {
			String moduleID = map_Key_ModuleID.get(key);
			SmartPlugDefine module = getSmartPlug(moduleID);
			if (module != null) {
				tv_name_shufang_curtain.setText(module.mPlugName);
				tv_moduleid_shufang_curtain.setText(module.mPlugId);
			}
		}

		key = "客厅窗帘";
		if (map_Key_ModuleID.containsKey(key)) {
			String moduleID = map_Key_ModuleID.get(key);
			SmartPlugDefine module = getSmartPlug(moduleID);
			if (module != null) {
				tv_name_keting_curtain.setText(module.mPlugName);
				tv_moduleid_keting_curtain.setText(module.mPlugId);
			}
		}
		
		key = "主卧插座";
		if (map_Key_ModuleID.containsKey(key)) {
			String moduleID = map_Key_ModuleID.get(key);
			SmartPlugDefine module = getSmartPlug(moduleID);
			if (module != null) {
				tv_name_zhuwo_smartplug.setText(module.mPlugName);
				tv_moduleid_zhuwo_smartplug.setText(module.mPlugId);
			}
		}

		key = "次卧插座";
		if (map_Key_ModuleID.containsKey(key)) {
			String moduleID = map_Key_ModuleID.get(key);
			SmartPlugDefine module = getSmartPlug(moduleID);
			if (module != null) {
				tv_name_ciwo_smartplug.setText(module.mPlugName);
				tv_moduleid_ciwo_smartplug.setText(module.mPlugId);
			}
		}

		key = "书房插座";
		if (map_Key_ModuleID.containsKey(key)) {
			String moduleID = map_Key_ModuleID.get(key);
			SmartPlugDefine module = getSmartPlug(moduleID);
			if (module != null) {
				tv_name_shufang_smartplug.setText(module.mPlugName);
				tv_moduleid_shufang_smartplug.setText(module.mPlugId);
			}
		}

		key = "客厅插座";
		if (map_Key_ModuleID.containsKey(key)) {
			String moduleID = map_Key_ModuleID.get(key);
			SmartPlugDefine module = getSmartPlug(moduleID);
			if (module != null) {
				tv_name_keting_smartplug.setText(module.mPlugName);
				tv_moduleid_keting_smartplug.setText(module.mPlugId);
			}
		}
		
		
		key = "电脑";// lishimin
		if (map_Key_ModuleID.containsKey(key)) {
			String moduleID_mac = map_Key_ModuleID.get(key);
			String[] cmds = moduleID_mac.split(",");
			if (cmds.length >= 2) {
				String tempModuleID = cmds[0];
				String tempMacID = cmds[1];
				SmartPlugDefine temp = mPlugHelper.getSmartPlug(tempMacID);
				String tempMac = temp.mMAC;
				
				SmartPlugDefine module = getSmartPlug(tempModuleID);
				if (module != null) {
					String show_name = module.mPlugName + "(" + temp.mPlugName + ")";
					String module_mac = module.mPlugId + "," + temp.mPlugId;
					tv_name_keting_pc.setText(show_name);
					tv_moduleid_keting_pc.setText(module_mac);
				}
			}
		}
		
	}
	
	private SmartPlugDefine getSmartPlug(String moduleID) {
		for (int i = 0; i < moduleids.size(); i++) {
			if (moduleids.get(i).mPlugId.equals(moduleID) == true) {
				return moduleids.get(i);
			}
		}
		return null;
	}
	
	private void saveData() {
		editor = mSharedPreferences.edit();
		
        for (String key : map_Key_ModuleID.keySet()) {
            editor.putString(key, map_Key_ModuleID.get(key));
		}
		
		editor.commit();
	}
	
	private void loadData() {
		// Parameter CITY
		cur_city = mSharedPreferences_parameter.getString("CITY", "");
		
		
		String kongtiao_type = "";		// 空调模块对应的空调类型
		String value = null;
		String key = "主卧空调";
		value = mSharedPreferences.getString(key, null); 
		if (value != null) {
			map_Key_ModuleID.put(key, value);
			kongtiao_type = mSharedPreferences_ir_data.getString("STRING_ID_" + value, null);
			if (kongtiao_type != null) {
				map_ModuleID_KongtiaoType.put(value, kongtiao_type);
			}
		}
		
		key = "次卧空调";
		value = mSharedPreferences.getString(key, null);
		if (value != null) {
			map_Key_ModuleID.put(key, value);
			kongtiao_type = mSharedPreferences_ir_data.getString("STRING_ID_" + value, null);
			if (kongtiao_type != null) {
				map_ModuleID_KongtiaoType.put(value, kongtiao_type);
			}
			
		}
		
		key = "书房空调";
		value = mSharedPreferences.getString(key, null);
		if (value != null) {
			map_Key_ModuleID.put(key, value);
			kongtiao_type = mSharedPreferences_ir_data.getString("STRING_ID_" + value, null);
			if (kongtiao_type != null) {
				map_ModuleID_KongtiaoType.put(value, kongtiao_type);
			}
		}
		
		key = "客厅空调";
		value = mSharedPreferences.getString(key, null);
		if (value != null) {
			map_Key_ModuleID.put(key, value);
			kongtiao_type = mSharedPreferences_ir_data.getString("STRING_ID_" + value, null);
			if (kongtiao_type != null) {
				map_ModuleID_KongtiaoType.put(value, kongtiao_type);
			}
		}
		
		key = "主卧窗帘";
		value = mSharedPreferences.getString(key, null);
		if (value != null) {
			map_Key_ModuleID.put(key, value);
		}
		key = "次卧窗帘";
		value = mSharedPreferences.getString(key, null);
		if (value != null) {
			map_Key_ModuleID.put(key, value);
		}
		key = "书房窗帘";
		value = mSharedPreferences.getString(key, null);
		if (value != null) {
			map_Key_ModuleID.put(key, value);
		}
		key = "客厅窗帘";
		value = mSharedPreferences.getString(key, null);
		if (value != null) {
			map_Key_ModuleID.put(key, value);
		}

		key = "主卧插座";
		value = mSharedPreferences.getString(key, null);
		if (value != null) {
			map_Key_ModuleID.put(key, value);
		}
		key = "次卧插座";
		value = mSharedPreferences.getString(key, null);
		if (value != null) {
			map_Key_ModuleID.put(key, value);
		}
		key = "书房插座";
		value = mSharedPreferences.getString(key, null);
		if (value != null) {
			map_Key_ModuleID.put(key, value);
		}
		key = "客厅插座";
		value = mSharedPreferences.getString(key, null);
		if (value != null) {
			map_Key_ModuleID.put(key, value);
		}
		key = "电脑";
		value = mSharedPreferences.getString(key, null);
		if (value != null) {
			map_Key_ModuleID.put(key, value);
		}
	}
	
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tSpeech.setLanguage(Locale.CHINA);	
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Toast.makeText(getApplicationContext(), "Language is not available.", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "init failed", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		tSpeech = new TextToSpeech(getApplicationContext(), this);
		if (tSpeech == null) {
			showTip("tSpeech null");
		}
	}
	
	@Override
	protected void onDestroy() {
		saveData();

		if (tSpeech != null) {
			tSpeech.stop();
			tSpeech.shutdown();
		}

		// 退出时释放连接
		mIat.cancel();
		mIat.destroy();

		// 销毁合成对象
		mIvw = VoiceWakeuper.getWakeuper();
		if (mIvw != null) {
			mIvw.destroy();
		}
		
		super.onDestroy();
	}

	private void ParseCommand_Speech(String cmd_text, Handler mHandler, boolean b_speech) {
		String moduleID = getModuleIDFromMap(cmd_text);
		if (moduleID != null) {
			if (moduleID.isEmpty() == false) {
				parseCommand(cmd_text, moduleID, mHandler, b_speech, tSpeech);
			}
		} else {
			parseCommand_Common(cmd_text, mHandler, b_speech, tSpeech);
		}
			
	}
	
	private void MySpeech(String command, boolean b_speech, TextToSpeech tSpeech) {
		if (b_speech == true && tSpeech != null) {
			String speech_text = "正在为您" + command;
			tSpeech.speak(speech_text,  TextToSpeech.QUEUE_FLUSH, null);
		}
	}
	
	public void parseCommand_Common(String command, Handler mHandler, boolean b_speech, TextToSpeech tSpeech) {
	  if(command.contains("天气") == true) {
		  String city = PubFunc.getWeatherCity(command);
		  if (city.isEmpty()) {
			  tSpeech.speak("没有该城市数据",  TextToSpeech.QUEUE_FLUSH, null);
			  return;
		  }
		  new SendCommand_Async(city).execute();
	  } else if(command.contains("日期") == true ||
			  command.contains("今天几号") == true  ||
			  command.contains("农历几号") == true ) {
		  String week = FucUtil.getWeek();
		  
		  if(command.contains("农历") == true) {
			  String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
			  String month = DateUtils.getLunarMonth();
			  String day = DateUtils.getLunarDay();
			  String cur_date = "农历日期 " + year + "年" + month + day + "日" + week;
			  
			  tSpeech.speak(cur_date,  TextToSpeech.QUEUE_FLUSH, null);
		  } else {
			  SimpleDateFormat  formatter = new  SimpleDateFormat("yyyy年MM月dd日");     
			  Date  curDate   = new   Date(System.currentTimeMillis());  
			  String   cur_time   =   formatter.format(curDate);
			  String cur_date = "公历日期 " + cur_time + week;
			  
			  tSpeech.speak(cur_date,  TextToSpeech.QUEUE_FLUSH, null);
		  }
		  
	  } else if(command.contains("时间") == true ||
			  command.contains("几点") == true) {
		  SimpleDateFormat  formatter = new  SimpleDateFormat("HH点mm分ss秒");
		  Date  curDate   = new   Date(System.currentTimeMillis());
		  String   cur_time   =   formatter.format(curDate); 
		  tSpeech.speak("当前时间 " + cur_time,  TextToSpeech.QUEUE_FLUSH, null);
	  } else if (command.contains("关闭自己") == true ||
		  command.contains("杀了自己") == true ||
		  command.contains("自杀") == true) {
		  finish();
		  SmartPlugApplication.getInstance().exit();
	  } else if (command.contains("提醒") == true) {
		  List<Long> listNum = FucUtil.getDigit(command);
		  if (listNum.size() >= 2) {
			  int year = Calendar.getInstance().get(Calendar.YEAR);
			  int month = Calendar.getInstance().get(Calendar.MONTH);
			  int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			  
			  note_content = command.replace("提醒", "");
			  note_content = note_content.replace("我", "");
			  note_content = note_content.replace(String.valueOf(listNum.get(0)), "");
			  note_content = note_content.replace(String.valueOf(listNum.get(1)), "");
			  
//			  String note_date = year + "-" + month + "-" + day + " " + String.valueOf(listNum.get(0)) + ":" + String.valueOf(listNum.get(1)) + ":00";  //yyyy-MM-dd HH:mm:ss
//			  Date this_date = FucUtil.StrToDate(note_date);
			  String note_date = String.valueOf(listNum.get(0)) + ":" + String.valueOf(listNum.get(1)) + ":00";  //yyyyMMddHH:mm:ss
			  Date this_date = PubFunc.stringToDate(note_date);
//			  
			  Timer noteTimer = new Timer();
			  TimerTask noteTimerTask = new TimerTask() {
				@Override
				public void run() {
					note_alarm(note_content);
				}
			  };
			  noteTimer.schedule(noteTimerTask, this_date);
		  } else {
			  // do nothing..
		  }
	  }
	}
	
	private void note_alarm(String note_content) {
		tSpeech.speak(note_content, TextToSpeech.QUEUE_FLUSH, null);
	}
	

	// 根据 不同的语音内容 发送不同的命令
	@SuppressLint("NewApi") 
	public void parseCommand(String command, String moduleID, Handler mHandler, boolean b_speech, TextToSpeech tSpeech) {
		String control = "";
		String location = "";
		String object = "";
		boolean b_read = true;
		if(command.contains(CONTROL_COMMAND_OPEN)) {
			control = "打开";
		} else if(command.contains(CONTROL_COMMAND_CLOSE)) {
			control = "关闭";
		} else if(command.contains(CONTROL_COMMAND_PAUSE)) {
			control = "暂停";
		} else {
			b_read = false;
		}

		if(command.contains("空调")) {
			object = "空调";
		} else if(command.contains("窗帘")) {
			object = "窗帘";
		} else if(command.contains("插座")) {
			object = "插座";
		} else if(command.contains("所有电脑")) {
			location = "";
			object = "所有电脑";
		} else if(command.contains("电脑")) {
			location = "";
			object = "电脑";
		} else {
			b_read = false;
		}
		
		if(command.contains("主卧")) {
			location = "主卧";
		} else if(command.contains("次卧")) {
			location = "次卧";
		} else if(command.contains("书房")) {
			location = "书房";
		} else  if(command.contains("客厅")) {
			location = "客厅";
		} else {
			if (object.equals("电脑") == false) {
				b_read = false;
			}
		}
		

//		
//		if (b_read == true) {
//			MySpeech(control + location + object, b_speech, tSpeech);
//		}
//		
		if(command.contains("窗帘") == true) {			// 窗帘控制
			String userName = PubStatus.g_CurUserName;
			String status = "3";
			if (command.contains(CONTROL_COMMAND_OPEN)) {
				status = "1";
			} else if (command.contains(CONTROL_COMMAND_CLOSE)) {
				status = "2";
			} else if (command.contains(CONTROL_COMMAND_PAUSE)) {
				status = "0";
			} else {
				status = "3";
			}
			
			MySpeech(control + location + object, b_speech, tSpeech);
			
			StringBuilder sb = new StringBuilder();
			sb.append(SmartPlugMessage.CMD_SP_CURTAIN).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
					.append(userName).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
					.append(moduleID).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
					.append(status);
			sendMsg(true, sb.toString(), true);
			
		} else if(command.contains("空调") == true) {			// 空调控制
			String power_on 		= "063";
			String power_off 		= "064";
			String mode_auto 		= "001";
			String mode_cool 		= "002";
			String mode_wet 		= "003";
			String mode_wind 		= "004";
			String mode_warm 		= "005";
			String wind_swing 	= "010";
			String wind_volume 	= "012";
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
			String cur_temperature = "16";

			String command_com = "";
			 if (command.contains(CONTROL_COMMAND_OPEN)) {
				 command_com = power_on;
			 } else if (command.contains(CONTROL_COMMAND_CLOSE)) {
				 command_com = power_off;
			 } else if (command.contains("温度")) {
				 if (command.contains("16")) {
					 command_com = temperatures_16;
					 cur_temperature = "16";
				 } else if (command.contains("17")) {
					 command_com = temperatures_17;
					 cur_temperature = "17";
				 } else if (command.contains("18")) {
					 command_com = temperatures_18;
					 cur_temperature = "18";
				 } else if (command.contains("19")) {
					 command_com = temperatures_19;
					 cur_temperature = "19";
				 } else if (command.contains("20")) {
					 command_com = temperatures_20;
					 cur_temperature = "20";
				 } else if (command.contains("21")) {
					 command_com = temperatures_21;
					 cur_temperature = "21";
				 } else if (command.contains("22")) {
					 command_com = temperatures_22;
					 cur_temperature = "22";
				 } else if (command.contains("23")) {
					 command_com = temperatures_23;
					 cur_temperature = "23";
				 } else if (command.contains("24")) {
					 command_com = temperatures_24;
					 cur_temperature = "24";
				 } else if (command.contains("25")) {
					 command_com = temperatures_25;
					 cur_temperature = "25";
				 } else if (command.contains("26")) {
					 command_com = temperatures_26;
					 cur_temperature = "26";
				 } else if (command.contains("27")) {
					 command_com = temperatures_27;
					 cur_temperature = "27";
				 } else if (command.contains("28")) {
					 command_com = temperatures_28;
					 cur_temperature = "28";
				 } else if (command.contains("29")) {
					 command_com = temperatures_29;
					 cur_temperature = "29";
				 } else if (command.contains("30")) {
					 command_com = temperatures_30;
					 cur_temperature = "30";
				 } else {
					 return ;
				 }
			 } else {
				 return ;
			 }
			 
			 if (command_com.equals(power_on) || command_com.equals(power_off)) {
				 MySpeech(control + location + object, b_speech, tSpeech);
			 } else {
				 MySpeech("把" + location + object + "的温度调到" + cur_temperature + "度", b_speech, tSpeech);
			 }
			 
			 String kongtiao_type = map_ModuleID_KongtiaoType.get(moduleID);
			 
			 //0,AIRCONSERVER,test,12345678,格力,021#
			 StringBuffer sb = new StringBuffer();
			 sb.append(SmartPlugMessage.CMD_SP_AIRCONSERVER).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
			  .append(PubStatus.g_CurUserName).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
			  .append(moduleID).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
			  .append(kongtiao_type).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
			  .append(command_com);
	    	
	    	if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
	    		sendMsg(true, sb.toString(), true);
	    	} else {  // 非Internet模式下，不处理； 
	    		// do nothing.
	    	}
	    	
		}  else if(command.contains("插座") == true) {			// 插座控制
			String userName = PubStatus.g_CurUserName;
			boolean status = false;
			if (command.contains(CONTROL_COMMAND_OPEN)) {
				status = true;
			} else if (command.contains(CONTROL_COMMAND_CLOSE)) {
				status = false;
			} else {
				return;
			}
			
			String value_command = "";
			String command_option = "";
			if (command.contains("电源") == true) {
				value_command = SmartPlugMessage.CMD_SP_POWER;
				command_option = "电源";
			} else if (command.contains("夜灯") == true) {
				value_command = SmartPlugMessage.CMD_SP_LIGHT;
				command_option = "夜灯";
			} else if (command.contains("USB") == true) {
				value_command = SmartPlugMessage.CMD_SP_USB;
				command_option = "USB";
			} else {
				return;
			}
			
			MySpeech(control + location + object + "的" + command_option, b_speech, tSpeech);
			
			SmartPlugControl(value_command, moduleID, status);
			
		}  else if(command.contains("所有电脑") == true) {			// 电脑控制
			//0,APPPASSTHROUGH,smli123hz,6044712,15,0,0,BACK2AP,smli123hz,6044712#
			//0,test,12345678,MAGICPACKET,MAC(AA-BB-CC-DD-EE-FF)#
			String[] cmds = moduleID.split(",");
			if (cmds.length < 2) {
				return;
			}
			
			MySpeech(control + location + object, b_speech, tSpeech);
			
			String tempModuleID = cmds[0];
			ArrayList<SmartPlugDefine> allPCIDs = mPlugHelper.getAllSmartPlugPCs(tempModuleID);
			for (int i = 0; i < allPCIDs.size(); i++) {
				SmartPlugDefine tempPCID = allPCIDs.get(i);
				String tempMacID = tempPCID.mPlugId;
				String tempMac = tempPCID.mMAC;
				
				try {
					String str_command = "";
					if (command.contains(CONTROL_COMMAND_OPEN) == true) {
						str_command = "0,MAGICPACKET," + PubStatus.g_CurUserName + "," + tempModuleID + "," + tempMac;
						OpenPC_PassThrought(tempModuleID, str_command);
						
						Thread.sleep(5000);
					} else if (command.contains(CONTROL_COMMAND_CLOSE) == true) {
						str_command = "0,APPPOWER," + PubStatus.g_CurUserName + "," + tempMacID + ",0";
						OpenPC_PassThrought(tempMacID, str_command);
						
						Thread.sleep(1000);
					} else {
						// do nothing...
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}  else if(command.contains("电脑") == true) {			// 电脑控制
			//0,APPPASSTHROUGH,smli123hz,6044712,15,0,0,BACK2AP,smli123hz,6044712#
			//0,test,12345678,MAGICPACKET,MAC(AA-BB-CC-DD-EE-FF)#
			String[] cmds = moduleID.split(",");
			if (cmds.length < 2) {
				return;
			}
			
			MySpeech(control + location + object, b_speech, tSpeech);
			
			String tempModuleID = cmds[0];
			String tempMacID = cmds[1];
			SmartPlugDefine temp = mPlugHelper.getSmartPlug(tempMacID);
			String tempMac = temp.mMAC;
			
			String str_command = "";
			if (command.contains(CONTROL_COMMAND_OPEN) == true) {
				str_command = "0,MAGICPACKET," + PubStatus.g_CurUserName + "," + tempModuleID + "," + tempMac;
				OpenPC_PassThrought(tempModuleID, str_command);
			} else if (command.contains(CONTROL_COMMAND_CLOSE) == true) {
				str_command = "0,APPPOWER," + PubStatus.g_CurUserName + "," + tempMacID + ",0";
				OpenPC_PassThrought(tempMacID, str_command);
			} else {
				return;
			}
		}
			 
	}
	
	private void OpenPC_PassThrought(String moduleID, String command) {
		int command_length = command.getBytes().length + 1;
	
		StringBuffer sb = new StringBuffer();
		sb.append("APPPASSTHROUGH")
		  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
		  .append(PubStatus.g_CurUserName)
		  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
		  .append(moduleID)
		  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
		  .append(String.valueOf(command_length))
		  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
		  .append(command);
	
		sendMsg(true, sb.toString(), true);
	}
	
	// only for Power/Light/USB
	private void SmartPlugControl(String command, String plugId, boolean power) {
    	String str_power = power ? "1" : "0";
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append(command)
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(PubStatus.getUserName())
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(plugId).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(str_power);    	
    	
    	sendMsg(true, sb.toString(), true);
	}
	

	// 保证锁屏后仍然可以监听声音加/减键;
	WakeLock wakeLock = null;
	private void acquire() {
		if (null == wakeLock) {
			PowerManager pm = (PowerManager) this
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, IatDemo.class.getName());
			if (null != wakeLock) {
				wakeLock.acquire();
			}
		}
	}

	private void release() {
		if (null != wakeLock) {
			wakeLock.release();
			wakeLock = null;
		}
	}

	GrammarListener grammarListener = new GrammarListener() {
		@Override
		public void onBuildFinish(String grammarId, SpeechError error) {
			if (error == null) {
				if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
					mCloudGrammarID = grammarId;
				} else {
					mLocalGrammarID = grammarId;
				}
//				showTip("语法构建成功：" + grammarId);
			} else {
				showTip(error.getErrorDescription() + " 错误码：" + error.getErrorCode());
			}
		}
	};

	private void buildGrammer() {
		int ret = 0;
		if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
			// 设置参数
			mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
			mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
			// 开始构建语法
			ret = mAsr.buildGrammar("abnf", mCloudGrammar, grammarListener);
			if (ret != ErrorCode.SUCCESS) {
				showTip("语法构建失败,错误码：" + ret);
			}
		} else {
			mAsr.setParameter(SpeechConstant.PARAMS, null);
			mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
			// 设置引擎类型
			mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
			// 设置语法构建路径
			mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
			// 设置资源路径
			mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
			ret = mAsr.buildGrammar("bnf", mLocalGrammar, grammarListener);
			if (ret != ErrorCode.SUCCESS) {
				showTip("语法构建失败,错误码：" + ret);
			}
		}
		return;
	}

	/**
	 * 初始化Layout。
	 */
	private void initLayout() {
		btn_wakeup = (Button) findViewById(R.id.btn_wakeup);
		btn_wakeup.setOnClickListener(this);
		
		tv_name_zhuwo_kongtiao = (TextView) findViewById(R.id.tv_name_zhuwo_kongtiao);
		tv_name_ciwo_kongtiao = (TextView) findViewById(R.id.tv_name_ciwo_kongtiao);
		tv_name_shufang_kongtiao = (TextView) findViewById(R.id.tv_name_shufang_kongtiao);
		tv_name_keting_kongtiao = (TextView) findViewById(R.id.tv_name_keting_kongtiao);
		tv_name_zhuwo_curtain = (TextView) findViewById(R.id.tv_name_zhuwo_curtain);
		tv_name_ciwo_curtain = (TextView) findViewById(R.id.tv_name_ciwo_curtain);
		tv_name_shufang_curtain = (TextView) findViewById(R.id.tv_name_shufang_curtain);
		tv_name_keting_curtain = (TextView) findViewById(R.id.tv_name_keting_curtain);

		tv_name_zhuwo_smartplug = (TextView) findViewById(R.id.tv_name_zhuwo_smartplug);
		tv_name_ciwo_smartplug = (TextView) findViewById(R.id.tv_name_ciwo_smartplug);
		tv_name_shufang_smartplug = (TextView) findViewById(R.id.tv_name_shufang_smartplug);
		tv_name_keting_smartplug = (TextView) findViewById(R.id.tv_name_keting_smartplug);

		tv_name_keting_pc = (TextView) findViewById(R.id.tv_name_keting_pc);
		

		tv_moduleid_zhuwo_kongtiao = (TextView) findViewById(R.id.tv_moduleid_zhuwo_kongtiao);
		tv_moduleid_ciwo_kongtiao = (TextView) findViewById(R.id.tv_moduleid_ciwo_kongtiao);
		tv_moduleid_shufang_kongtiao = (TextView) findViewById(R.id.tv_moduleid_shufang_kongtiao);
		tv_moduleid_keting_kongtiao = (TextView) findViewById(R.id.tv_moduleid_keting_kongtiao);
		tv_moduleid_zhuwo_curtain = (TextView) findViewById(R.id.tv_moduleid_zhuwo_curtain);
		tv_moduleid_ciwo_curtain = (TextView) findViewById(R.id.tv_moduleid_ciwo_curtain);
		tv_moduleid_shufang_curtain = (TextView) findViewById(R.id.tv_moduleid_shufang_curtain);
		tv_moduleid_keting_curtain = (TextView) findViewById(R.id.tv_moduleid_keting_curtain);
		
		tv_moduleid_zhuwo_smartplug = (TextView) findViewById(R.id.tv_moduleid_zhuwo_smartplug);
		tv_moduleid_ciwo_smartplug = (TextView) findViewById(R.id.tv_moduleid_ciwo_smartplug);
		tv_moduleid_shufang_smartplug = (TextView) findViewById(R.id.tv_moduleid_shufang_smartplug);
		tv_moduleid_keting_smartplug = (TextView) findViewById(R.id.tv_moduleid_keting_smartplug);
		
		tv_moduleid_keting_pc = (TextView) findViewById(R.id.tv_moduleid_keting_pc);
		
		tv_name_zhuwo_kongtiao.setOnClickListener(this);
		tv_name_ciwo_kongtiao.setOnClickListener(this);
		tv_name_shufang_kongtiao.setOnClickListener(this);
		tv_name_keting_kongtiao.setOnClickListener(this);
		tv_name_zhuwo_curtain.setOnClickListener(this);
		tv_name_ciwo_curtain.setOnClickListener(this);
		tv_name_shufang_curtain.setOnClickListener(this);
		tv_name_keting_curtain.setOnClickListener(this);
		
		tv_name_zhuwo_smartplug.setOnClickListener(this);
		tv_name_ciwo_smartplug.setOnClickListener(this);
		tv_name_shufang_smartplug.setOnClickListener(this);
		tv_name_keting_smartplug.setOnClickListener(this);

		tv_name_keting_pc.setOnClickListener(this);
		
	}

	int ret = 0;// 函数调用返回值

	@SuppressLint("NewApi")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:
			finish();
			break;
		case R.id.titlebar_rightbutton:	
			finish();
			break;
		case R.id.btn_wakeup:
			if (btn_wakeup.getText().toString().equals("启动语音控制") == true) {
				ResetWakeup();
				btn_wakeup.setText("关闭语音控制");
			} else {
				if (mIvw != null) {
					mIvw.stopListening();
				}
//				showTip("停止唤醒");
				flag_voice_recognize = false;
				btn_wakeup.setText("启动语音控制");
			}
			break;
		case R.id.tv_name_zhuwo_kongtiao:
			showAllModules(MSG_UPDATE_ZHUWO_KONGTIAO);
			break;
		case R.id.tv_name_ciwo_kongtiao:
			showAllModules(MSG_UPDATE_CIWO_KONGTIAO);
			break;
		case R.id.tv_name_shufang_kongtiao:
			showAllModules(MSG_UPDATE_SHUFANG_KONGTIAO);
			break;
		case R.id.tv_name_keting_kongtiao:
			showAllModules(MSG_UPDATE_KETING_KONGTIAO);
			break;
		case R.id.tv_name_zhuwo_curtain:
			showAllModules(MSG_UPDATE_ZHUWO_CURTAIN);
			break;
		case R.id.tv_name_ciwo_curtain:
			showAllModules(MSG_UPDATE_CIWO_CURTAIN);
			break;
		case R.id.tv_name_shufang_curtain:
			showAllModules(MSG_UPDATE_SHUFANG_CURTAIN);
			break;
		case R.id.tv_name_keting_curtain:
			showAllModules(MSG_UPDATE_KETING_CURTAIN);
			break;
		case R.id.tv_name_zhuwo_smartplug:
			showAllModules(MSG_UPDATE_ZHUWO_SMARTPLUG);
			break;
		case R.id.tv_name_ciwo_smartplug:
			showAllModules(MSG_UPDATE_CIWO_SMARTPLUG);
			break;
		case R.id.tv_name_shufang_smartplug:
			showAllModules(MSG_UPDATE_SHUFANG_SMARTPLUG);
			break;
		case R.id.tv_name_keting_smartplug:
			showAllModules(MSG_UPDATE_KETING_SMARTPLUG);
			break;
		case R.id.tv_name_keting_pc:
			showAllModulesMacs(MSG_UPDATE_KETING_PC);
			break;
		default:
			break;
		}
	}
	
	/*
	 * 显示所有模块
	 */
	private void showAllModules(final int cmd_type) {
		ActionSheetDialog dlg = new ActionSheetDialog(IatDemo.this)
		.builder()
		.setTitle(IatDemo.this.getString(R.string.smartplug_oper_selectmodule_title))
		.setCancelable(true)
		.setCanceledOnTouchOutside(true);
		
		for (int i = 0; i < moduleids.size(); i++) {
			
			dlg.addSheetItem(moduleids.get(i).mPlugName, SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
			            	Message msg = new Message();
			            	msg.what = MSG_UPDATE_TEXTVIEW;
			            	msg.arg1 = cmd_type;
			            	msg.arg2 = which - 1;
			            	mHandler.sendMessage(msg);
						}
			});
		}
		dlg.show();
	}	
	/*
	 * 显示所有模块和Mac地址
	 */
	private void showAllModulesMacs(final int cmd_type) {
		ActionSheetDialog dlg = new ActionSheetDialog(IatDemo.this)
		.builder()
		.setTitle(IatDemo.this.getString(R.string.smartplug_oper_selectmodule_title))
		.setCancelable(true)
		.setCanceledOnTouchOutside(true);
		
		for (int i = 0; i < PC_modulenames.size(); i++) {
			SmartPlugDefine tempMac = mPlugHelper.getSmartPlug(PC_moduleidmacs.get(i));
			dlg.addSheetItem(PC_modulenames.get(i) + "(" + tempMac.mPlugName + ")", SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
			            	Message msg = new Message();
			            	msg.what = MSG_UPDATE_TEXTVIEW;
			            	msg.arg1 = cmd_type;
			            	msg.arg2 = which - 1;
			            	mHandler.sendMessage(msg);
						}
			});
		}
		dlg.show();
	}

	private void ResetWakeup() {
		// if (mIvw != null) {
		// mIvw.stopListening();
		// }
		mIvw = VoiceWakeuper.getWakeuper();
		if (mIvw != null) {
			resultString = "";
			recoString = "";
			// mResultText.setText(resultString);

			String resPath = ResourceUtil.generateResourcePath(IatDemo.this,
					RESOURCE_TYPE.assets, "ivw/" + getString(R.string.app_id)
							+ ".jet");
			// 清空参数
			mIvw.setParameter(SpeechConstant.PARAMS, null);
			// 设置识别引擎
			mIvw.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
			// 设置唤醒资源路径
			mIvw.setParameter(ResourceUtil.IVW_RES_PATH, resPath);
			// 设置持续进行唤醒
			mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
			/**
			 * 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
			 * 示例demo默认设置第一个唤醒词，建议开发者根据定制资源中唤醒词个数进行设置
			 */
			mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"
					+ oneshot_curThresh);
			// 设置唤醒+识别模式
			mIvw.setParameter(SpeechConstant.IVW_SST, "oneshot");
			// 设置返回结果格式
			mIvw.setParameter(SpeechConstant.RESULT_TYPE, "json");
			//
			// mIvw.setParameter(SpeechConstant.IVW_SHOT_WORD, "0");

			if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
				if (!TextUtils.isEmpty(mCloudGrammarID)) {
					// 设置云端识别使用的语法id
					mIvw.setParameter(SpeechConstant.CLOUD_GRAMMAR,
							mCloudGrammarID);
					mIvw.startListening(mWakeuperListener);
				} else {
					showTip("请先构建语法");
				}
			} else {
				if (!TextUtils.isEmpty(mLocalGrammarID)) {
					// 设置本地识别资源
					mIvw.setParameter(ResourceUtil.ASR_RES_PATH,
							getResourcePath());
					// 设置语法构建路径
					mIvw.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
					// 设置本地识别使用语法id
					mIvw.setParameter(SpeechConstant.LOCAL_GRAMMAR,
							mLocalGrammarID);
					mIvw.startListening(mWakeuperListener);

				} else {
					showTip("请先构建语法");
				}
			}

		} else {
			showTip("唤醒未初始化");
		}
	}

	private WakeuperListener mWakeuperListener = new WakeuperListener() {

		@Override
		public void onResult(WakeuperResult result) {
			try {
				String text = result.getResultString();
				JSONObject object;
				object = new JSONObject(text);
				StringBuffer buffer = new StringBuffer();
				buffer.append("【RAW】 " + text);
				buffer.append("\n");
				buffer.append("【操作类型】" + object.optString("sst"));
				buffer.append("\n");
				buffer.append("【唤醒词id】" + object.optString("id"));
				buffer.append("\n");
				buffer.append("【得分】" + object.optString("score"));
				buffer.append("\n");
				buffer.append("【前端点】" + object.optString("bos"));
				buffer.append("\n");
				buffer.append("【尾端点】" + object.optString("eos"));
				resultString = buffer.toString();

				List<String> ips = null;

				// 启动语音控制接口
				int command = Integer.valueOf(object.optString("id"));
				switch (command) {
				case 0: // 我的荣耀
					tSpeech.speak(str_welcome,  TextToSpeech.QUEUE_FLUSH, null);
					
//					if (!(text.equals("。") || text.equals(""))) {
//						mResultText.setText(text);
//						ParseCommand_Speech(text, mHandler, b_speech);
//					}
					
					start_recognize();	// 
					break;
				default:
					break;
				}

				// 重新启动唤醒词
//				mIvw.stopListening();
//				ResetWakeup();
//				mIvw.startListening(mWakeuperListener);

			} catch (JSONException e) {
				resultString = "结果解析出错";
				e.printStackTrace();
			}
//			mResultText.setText(resultString);
		}

		@Override
		public void onError(SpeechError error) {
			// showTip(error.getPlainDescription(true));
			// mResultText.setText("解析时出错");
		}

		@Override
		public void onBeginOfSpeech() {
			// mResultText.setText("开始解析");
		}

		public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
			Log.d(TAG, "eventType:" + eventType + "arg1:" + isLast + "arg2:"
					+ arg2);
			// 识别结果
			if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
				RecognizerResult reslut = ((RecognizerResult) obj
						.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
				recoString = JsonParser.parseGrammarResultSum(reslut
						.getResultString());
				// mResultText.setText(recoString);

				// 启动语音控制接口
				// start_recognize();
				String command = recoString;
//				String[] cmdArray = { "打开电源", "关闭电源", "打开夜灯", "关闭夜灯", "打开USB",
//						"关闭USB" };
//
//				List<String> ips = getSelectIP();
//				for (int i = 0; i < cmdArray.length; i++) {
//					if (command.contains(cmdArray[i])) {
//						mResultText.setText(cmdArray[i]);
//						showTip(cmdArray[i]);
//
//						ParseCommand_Speech(cmdArray[i], ips, mHandler, b_speech);
//						break;
//					}
//				}
				if (command.equalsIgnoreCase("没有匹配结果.") == true) {
					ResetWakeup();
					// mIvw.startListening(mWakeuperListener);
				}
			}
		}

		@Override
		public void onVolumeChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	/*
	 * 点击 开始 按钮，执行语音解析
	 */
	private void start_recognize() {
		try {
			Thread.sleep(i_reg_delay_time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		flag_voice_recognize = true;
		setParam();
		boolean isShowDialog = mSharedPreferences.getBoolean(
				getString(R.string.pref_key_iat_show), true);
		isShowDialog = true; // 强制不显示语音对话框
		if (isShowDialog) {
			// 显示听写对话框
			iatDialog.setListener(recognizerDialogListener);
			iatDialog.show();
			// showTip(getString(R.string.text_begin));
		} else {
			// 不显示听写对话框
			ret = mIat.startListening(recognizerListener);
			if (ret != ErrorCode.SUCCESS) {
				showTip("听写失败,错误码：" + ret);
			} else {
				showTip(getString(R.string.text_begin));
			}
		}

		return;
	}

	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败,错误码：" + code);
			}
		}
	};

	/**
	 * 听写监听器。
	 */
	private RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
//			showTip("开始说话");
		}

		@Override
		public void onError(SpeechError error) {
//			showTip(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
//			showTip("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			// mResultText.setText(text);
			if (isLast) {
				// TODO 最后的结果

			}
		}

		// @Override
		public void onVolumeChanged(int volume) {
//			showTip("当前正在说话，音量大小：" + volume);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

		}

		@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			// TODO Auto-generated method stub
//			showTip("当前正在说话，音量大小：" + arg0);
		}
	};

	private String getModuleIDFromMap(String command) {
        for (String key : map_Key_ModuleID.keySet()) {
            if (command.contains(key)) {
            	return map_Key_ModuleID.get(key);
            }
		}
		return null;
	}
	
	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult result, boolean isLast) {
			Log.d(TAG, "recognizer result：" + result.getResultString());
			String text = JsonParser.parseIatResult(result.getResultString());
			if (!(text.equals("。") || text.equals(""))) {
				mResultText.setText(text);
				ParseCommand_Speech(text, mHandler, b_speech);
			}

			// try {
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }

			if (flag_voice_recognize == true) {
//				btn_start_recognize.performClick(); // 暂时关闭掉循环 开始功能； lishimin
				ResetWakeup();
			}

		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			// showTip(error.getPlainDescription(true));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (flag_voice_recognize == true) {
				// btn_start_recognize.performClick(); // 暂时关闭掉循环 开始功能； lishimin
				ResetWakeup();
			}
		}

	};

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		String lag = mSharedPreferences.getString("iat_language_preference",
				"mandarin");
		// 设置引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);

		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}

		// 设置语音前端点
		mIat.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("iat_vadbos_preference", "4000"));
		// 设置语音后端点
		mIat.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("iat_vadeos_preference", "1000"));
		// 设置标点符号
		mIat.setParameter(SpeechConstant.ASR_PTT,
				mSharedPreferences.getString("iat_punc_preference", "1"));
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory()
						+ "/iflytek/wavaudio.pcm");
	}

	// 获取识别资源路径
	private String getResourcePath() {
		StringBuffer tempBuffer = new StringBuffer();
		// 识别通用资源
		tempBuffer.append(ResourceUtil.generateResourcePath(this,
				RESOURCE_TYPE.assets, "asr/common.jet"));
		return tempBuffer.toString();
	}

	// -----------------------------------------------------------------------------
	// -----------------------------------------------------------------------------
	// 消息处理机制
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String key = "";
			int cmd_type = msg.arg1;
			int index = msg.arg2;
			
			super.handleMessage(msg);
			switch(msg.what) {
			case MSG_UPDATE_SHOW_INFO:
				Toast.makeText(IatDemo.this, (String) msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			case MSG_NOTIFY_SUCCESS:
				// Toast.makeText(IatDemo.this, "发送成功",
				// Toast.LENGTH_SHORT).show();
				break;
			case MSG_NOTIFY_FAIL:
				Toast.makeText(IatDemo.this, "发送失败", Toast.LENGTH_SHORT).show();
				break;
			case MSG_NOTIFY_NETWORK_FAIL:
				Toast.makeText(IatDemo.this, "网络错误", Toast.LENGTH_SHORT).show();
				break;
			case MSG_NOTIFY_INIT:
				Toast.makeText(IatDemo.this, "未知的初始化消息", Toast.LENGTH_SHORT)
						.show();
				break;
			case MSG_SHOW_MESSAGE:
				String str = (String) msg.obj;
				Toast.makeText(IatDemo.this, str, Toast.LENGTH_SHORT).show();
				break;
			case MSG_UPDATE_TEXTVIEW:
				if (cmd_type == MSG_UPDATE_ZHUWO_KONGTIAO) {
					key = "主卧空调";
					String kongtiao_type = map_ModuleID_KongtiaoType.get(moduleids.get(index).mPlugId);
					if (kongtiao_type == null) {
						kongtiao_type = DEFAULT_KONGTIAO_NAME;
					}
					String show_name = moduleids.get(index).mPlugName + "(" + kongtiao_type + ")";
					
					tv_name_zhuwo_kongtiao.setText(show_name);
					tv_moduleid_zhuwo_kongtiao.setText(moduleids.get(index).mPlugId);
					
					map_Key_ModuleID.put(key, moduleids.get(index).mPlugId);
					
				} else if (cmd_type == MSG_UPDATE_CIWO_KONGTIAO) {
					key = "次卧空调";
					String kongtiao_type = map_ModuleID_KongtiaoType.get(moduleids.get(index).mPlugId);
					if (kongtiao_type == null) {
						kongtiao_type = DEFAULT_KONGTIAO_NAME;
					}
					String show_name = moduleids.get(index).mPlugName + "(" + kongtiao_type + ")";
					
					tv_name_ciwo_kongtiao.setText(show_name);
					tv_moduleid_ciwo_kongtiao.setText(moduleids.get(index).mPlugId);
					
					map_Key_ModuleID.put(key, moduleids.get(index).mPlugId);
					
				} else if (cmd_type == MSG_UPDATE_SHUFANG_KONGTIAO) {
					key = "书房空调";
					String kongtiao_type = map_ModuleID_KongtiaoType.get(moduleids.get(index).mPlugId);
					if (kongtiao_type == null) {
						kongtiao_type = DEFAULT_KONGTIAO_NAME;
					}
					String show_name = moduleids.get(index).mPlugName + "(" + kongtiao_type + ")";
					
					tv_name_shufang_kongtiao.setText(show_name);
					tv_moduleid_shufang_kongtiao.setText(moduleids.get(index).mPlugId);
					
					map_Key_ModuleID.put(key, moduleids.get(index).mPlugId);
					
				} else if (cmd_type == MSG_UPDATE_KETING_KONGTIAO) {
					key = "客厅空调";
					String kongtiao_type = map_ModuleID_KongtiaoType.get(moduleids.get(index).mPlugId);
					if (kongtiao_type == null) {
						kongtiao_type = DEFAULT_KONGTIAO_NAME;
					}
					String show_name = moduleids.get(index).mPlugName + "(" + kongtiao_type + ")";
					
					tv_name_keting_kongtiao.setText(show_name);
					tv_moduleid_keting_kongtiao.setText(moduleids.get(index).mPlugId);
					
					map_Key_ModuleID.put(key, moduleids.get(index).mPlugId);
					
				} else if (cmd_type == MSG_UPDATE_ZHUWO_CURTAIN) {
					key = "主卧窗帘";
					tv_name_zhuwo_curtain.setText(moduleids.get(index).mPlugName);
					tv_moduleid_zhuwo_curtain.setText(moduleids.get(index).mPlugId);

					map_Key_ModuleID.put(key, moduleids.get(index).mPlugId);
					
				} else if (cmd_type == MSG_UPDATE_CIWO_CURTAIN) {
					key = "次卧窗帘";
					tv_name_ciwo_curtain.setText(moduleids.get(index).mPlugName);
					tv_moduleid_ciwo_curtain.setText(moduleids.get(index).mPlugId);

					map_Key_ModuleID.put(key, moduleids.get(index).mPlugId);
					
				} else if (cmd_type == MSG_UPDATE_SHUFANG_CURTAIN) {
					key = "书房窗帘";
					tv_name_shufang_curtain.setText(moduleids.get(index).mPlugName);
					tv_moduleid_shufang_curtain.setText(moduleids.get(index).mPlugId);

					map_Key_ModuleID.put(key, moduleids.get(index).mPlugId);
					
					
				} else if (cmd_type == MSG_UPDATE_KETING_CURTAIN) {
					key = "客厅窗帘";
					tv_name_keting_curtain.setText(moduleids.get(index).mPlugName);
					tv_moduleid_keting_curtain.setText(moduleids.get(index).mPlugId);
					
					map_Key_ModuleID.put(key, moduleids.get(index).mPlugId);
					
				} else if (cmd_type == MSG_UPDATE_ZHUWO_SMARTPLUG) {
						key = "主卧插座";
						tv_name_zhuwo_smartplug.setText(moduleids.get(index).mPlugName);
						tv_moduleid_zhuwo_smartplug.setText(moduleids.get(index).mPlugId);

						map_Key_ModuleID.put(key, moduleids.get(index).mPlugId);
						
					} else if (cmd_type == MSG_UPDATE_CIWO_SMARTPLUG) {
						key = "次卧插座";
						tv_name_ciwo_smartplug.setText(moduleids.get(index).mPlugName);
						tv_moduleid_ciwo_smartplug.setText(moduleids.get(index).mPlugId);

						map_Key_ModuleID.put(key, moduleids.get(index).mPlugId);
						
					} else if (cmd_type == MSG_UPDATE_SHUFANG_SMARTPLUG) {
						key = "书房插座";
						tv_name_shufang_smartplug.setText(moduleids.get(index).mPlugName);
						tv_moduleid_shufang_smartplug.setText(moduleids.get(index).mPlugId);

						map_Key_ModuleID.put(key, moduleids.get(index).mPlugId);
						
						
					} else if (cmd_type == MSG_UPDATE_KETING_SMARTPLUG) {
						key = "客厅插座";
						tv_name_keting_smartplug.setText(moduleids.get(index).mPlugName);
						tv_moduleid_keting_smartplug.setText(moduleids.get(index).mPlugId);
						
						map_Key_ModuleID.put(key, moduleids.get(index).mPlugId);
					} else if (cmd_type == MSG_UPDATE_KETING_PC) {
						key = "电脑";
						SmartPlugDefine moduleMac = mPlugHelper.getSmartPlug(PC_moduleidmacs.get(index));
						String show_name = PC_modulenames.get(index) + "(" + moduleMac.mPlugName + ")";
						String module_mac = PC_moduleids.get(index) + "," + PC_moduleidmacs.get(index);
						tv_name_keting_pc.setText(show_name);
						tv_moduleid_keting_pc.setText(module_mac);
						
						map_Key_ModuleID.put(key, module_mac);
					}
				saveData();
				break;
			case MSG_READ_CITY_WEATHER:
    			String output = (String)msg.obj;
    			tSpeech.speak(output, TextToSpeech.QUEUE_FLUSH, null);
    			break;
			default:
				Toast.makeText(IatDemo.this, "不可能的消息", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	// ----------------------------------------------------------
	// UDP 方式处理函数
	// ----------------------------------------------------------
//	private void scanportWithUDPMode_Button() {
//		InetAddress inetAddress;
//
//		try {
//			String dest_mask = inetAddress.getHostAddress();
//			new scanportWithUDP_Async(dest_mask, UDP_SRC_PORT,
//					UDP_DEST_PORT, UDP_TIME_OUT,
//					UDP_SCANPORT_COMMAND).execute();
//
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//	}

	// 扫描插座类
	class scanportWithUDP_Async extends AsyncTask<Void, Void, Void> {

		// UDP 匹配模式
		private String remoteAddress;
		private int localPort, remotePort;
		private int timeout; // 线程数，这是第几个线程，超时时间
		private String sendText;
		private boolean b_findedIP = false;
		private boolean b_addLocalIP = true;

		public scanportWithUDP_Async(String remoteAddress, int localPort,
				int remotePort, int timeout, String sendText) {
			this.remoteAddress = remoteAddress;
			this.localPort = localPort;
			this.remotePort = remotePort;
			this.timeout = timeout;
			this.sendText = sendText;
		}

		@Override
		protected Void doInBackground(Void... params) {
			mHandler.sendEmptyMessage(MSG_UPDATE_SCANBTN_DISABLE);

			mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW_SCANPORT);
			
			try {
				Thread.sleep(THREAD_SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW_SCANPORT_END);
			mHandler.sendEmptyMessage(MSG_UPDATE_SCANBTN_ENABLE);
		}

	}

	// ------------------------------------------------------------------------------
	/*
	 * 工具类函数
	 */
	private void showTip(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mToast.setText(str);
				mToast.show();
			}
		});
	}
	
	//----------------------------------------------------------
	class SendCommand_Async extends AsyncTask<Void, Void, Void>  {
		String str_city = "";
		
		SendCommand_Async(String city) {
			str_city = city;
		}
		
		@Override
		protected Void doInBackground(Void... params) {

			PubFunc.getWeatherWithHTTP(str_city, mHandler);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			 
		}
	}
    
}
