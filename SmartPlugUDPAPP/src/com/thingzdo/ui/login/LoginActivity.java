package com.thingzdo.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.dataprovider.SmartPlugTimerHelper;
import com.thingzdo.internet.UDPReceiver;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.UserDefine;
import com.thingzdo.ui.common.DeviceStatus;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.SmartPlugProgressDlg;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.control.DetailAirCon2Activity;
import com.thingzdo.ui.control.DetailAirConActivity;
import com.thingzdo.ui.control.DetailCurtainActivity;
import com.thingzdo.ui.control.DetailKettleActivity;
import com.thingzdo.ui.control.DetailWindowActivity;
import com.thingzdo.ui.control.InterConnect;
import com.thingzdo.ui.control.PlugDetailActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.shakeshake.ShakeShake;
import com.thingzdo.ui.manage.WifiAdmin;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugActivity;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.ui.wheelutils.ActionSheetDialog;
import com.thingzdo.ui.wheelutils.MyAlertDialog;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.OnSheetItemClickListener;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.SheetItemColor;
import com.thingzdo.ui.wifi.SmartPlugWifiMgr;
import com.thingzdo.util.ThingzdoCheckBox;

public class LoginActivity extends TitledActivity {
    private static String TAG = "LoginActivity";
	
	private Button mBtnLogin    = null;
	private ThingzdoCheckBox mChkKeepPwd = null;
	private RelativeLayout mKeepPwdLayer = null;
	private ImageView mBtnDelPwd = null;
	private ImageView mBtnDelUser = null;
	private EditText mEdtUser = null;
	private EditText mEdtPwd = null;
	private TextView  mFindPwd = null;
	//private Button  mWiFi = null;
	private Button  mShakeShake = null;
	private Context mContext = null;
	
	private String mInputUser = "";
	private String mInputPwd = "";
	
	private SmartPlugHelper mPlugHelper = null;
	private String mEmail = "";
	
	private SharedPreferences mWifiInfo = null;
	private String mStored_SSID = "";
	private String mStored_PLUGID = "";
	
	private DeviceStatus mWifiDevice = null;
	
	private Button mBtnInterLogin    = null;
	
	private RadioGroup mRgLoginMode = null;
	private RadioButton mRdInternetLogin = null;
	private RadioButton mRdWiFiLogin = null;
	private RadioButton mRdShake = null;

	private RadioGroup  rg_serverip;
	private RadioButton rb_server_hz;
	private RadioButton rb_server_sz;
	private RadioButton rb_server_debug;
	
	private RelativeLayout mRlLoginInput = null;
	private SharedPreferences mLoginRef = null;
	
	private UserDefine mUserInfo = null;
	
	private static UDPReceiver mUDPServer = null;
	
	private String auto_login = "false";

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;
	
	private Handler updateHandler = new Handler() {
	    public void handleMessage(Message msg) {
			saveUserInfo();
			updatePlugOffline();
			
			Intent act = new Intent();
			act.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			act.setClass(LoginActivity.this, SmartPlugActivity.class);
			mContext.startActivity(act);	
			finish();
	    };	
	};
	
	private BroadcastReceiver mLoginRev = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(PubDefine.LOGIN_BROADCAST) && 
					true == PubDefine.g_First_Login) {
				PubDefine.g_Connect_Mode = PubDefine.SmartPlug_Connect_Mode.Internet;
				
				if (null != mProgress) {
					mProgress.dismiss();
				}
				timeoutHandler.removeCallbacks(timeoutProcess);
				int ret = intent.getIntExtra("RESULT", 0);
				PubFunc.log("Message: app login. ret:=" + String.valueOf(ret));
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
				case 0:
					mEmail = message;
					SmartPlugApplication.setLogined(true);
			    	
			    	updateHandler.sendEmptyMessage(0);
					
					break;
				default:
					SmartPlugApplication.setLogined(false);
					PubFunc.thinzdoToast(LoginActivity.this, message);					
					break;
				}
			}
			
			if (intent.getAction().equals(PubDefine.LOGOUT_BROADCAST)) {
				int ret = intent.getIntExtra("LOGOUT", 0);
				PubFunc.log("Message: app logout. ret:=" + String.valueOf(ret));
				
				if (1 == ret) {
					new MyAlertDialog(SmartPlugApplication.getContext()).builder()
					.setMsg("Forced to Quit")
					.setPositiveButton(mContext.getString(R.string.smartplug_ok), new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							
						}
					})
					.setCancelable(false).show(); 					
				}
			}
		}
	};
	
	// Wifi 直连模式下，SSID和PlugID
	private void saveWiFiStore() {
		SharedPreferences.Editor editor = mWifiInfo.edit(); 
		editor.putString("WIFI_PLUGSSID", mStored_SSID);
		editor.putString("WIFI_PLUGID", mStored_PLUGID);
		editor.commit();
	}
	
	private void loadWiFiStore() {
		mStored_SSID = mWifiInfo.getString("WIFI_PLUGSSID", "");
		mStored_PLUGID = mWifiInfo.getString("WIFI_PLUGID", "0");
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		PubFunc.log("login start");
		super.onCreate(savedInstanceState, R.layout.activity_login, false);
		SmartPlugApplication.getInstance().addActivity(this);
		
		setTitleRightButton(R.string.login_register,R.drawable.title_btn_selector/*top_right*/, registerClick);
		setTitle(R.string.login_login);
		
		mUserInfo  = new UserDefine();
		
		mSharedPreferences = getSharedPreferences("SmartPlug", Activity.MODE_PRIVATE);
		loadServerIP();
		
/*		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
			
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()    
            .detectDiskReads().detectDiskWrites().detectNetwork()    
            .penaltyLog().build());    
    
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()    
            .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()    
            .build()); 			
		}*/		
		
		mContext = this;
		//mUserHelper = new UserHelper(mContext);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.LOGIN_BROADCAST);
		registerReceiver(mLoginRev, filter);
		
		mChkKeepPwd = (ThingzdoCheckBox)findViewById(R.id.chkSavePwd);
		mKeepPwdLayer = (RelativeLayout)findViewById(R.id.layout_keeppwd);
		
		mBtnLogin = (Button)findViewById(R.id.login_ok);
		mBtnLogin.setOnClickListener(onLoginClick);
		
		mBtnDelPwd = (ImageView) findViewById(R.id.image_delete);
		mBtnDelPwd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mEdtPwd.setText("");
			}
		});		
		
		mBtnDelUser = (ImageView) findViewById(R.id.image_deluser);
		mBtnDelUser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mEdtUser.setText("");
			}
		});
		
		mEdtUser = (EditText)findViewById(R.id.login_username);
		mEdtPwd = (EditText)findViewById(R.id.login_userpwd);
		mFindPwd = (TextView)findViewById(R.id.login_findpwd);
		mFindPwd.setOnClickListener(findPwdClick);		
		
		mPlugHelper = new SmartPlugHelper(this);
    	
    	mEdtUser.addTextChangedListener(userTextWatcher); 
    	mEdtPwd.addTextChangedListener(pwdTextWatcher);
    	
    	mWifiInfo = getSharedPreferences("WiFiStore", Activity.MODE_PRIVATE); 
    	mLoginRef  = getSharedPreferences("UserInfo", Activity.MODE_PRIVATE);
    	
    	mShakeShake = (Button)findViewById(R.id.btn_shake_shake);
    	mShakeShake.setOnClickListener(onShakeShake);
    	
    	mBtnInterLogin = (Button)findViewById(R.id.btn_interlogin);
    	mBtnInterLogin.setOnClickListener(onInterLoginClick);
    	
    	updateEdit(mEdtUser, mBtnDelUser);
    	updateEdit(mEdtPwd, mBtnDelPwd);
    	
    	mRgLoginMode = (RadioGroup) findViewById(R.id.rg_login_mode);
    	mRgLoginMode.setOnCheckedChangeListener(onChangeRadio);
    	mRdInternetLogin = (RadioButton) findViewById(R.id.rb_internet);
    	mRdWiFiLogin = (RadioButton) findViewById(R.id.rb_wifi);
    	mRdShake = (RadioButton) findViewById(R.id.rb_shake);
    	mRlLoginInput = (RelativeLayout) findViewById(R.id.rlLoginInput);
    	
    	rg_serverip = (RadioGroup) findViewById(R.id.rg_serverip);
		rb_server_hz = (RadioButton) findViewById(R.id.rb_server_hz);
		rb_server_sz = (RadioButton) findViewById(R.id.rb_server_sz);
		rb_server_debug = (RadioButton) findViewById(R.id.rb_server_debug);
		
		// set radio button
		rb_server_hz.setChecked(false);
		rb_server_sz.setChecked(false);
		rb_server_debug.setChecked(false);
		if (PubDefine.THINGZDO_HOST_NAME.equals(PubDefine.SERVERIP_HANGZHOU)) {
			rb_server_hz.setChecked(true);
		} else if (PubDefine.THINGZDO_HOST_NAME.equals(PubDefine.SERVERIP_SHENZHEN)) {
			rb_server_sz.setChecked(true);
		} else if (PubDefine.THINGZDO_HOST_NAME.equals(PubDefine.SERVERIP_DEBUG)) {
			rb_server_debug.setChecked(true);
		}
		
    	if (PubDefine.RELEASE_VERSION == true) {
    		PubDefine.THINGZDO_HOST_NAME = PubDefine.SERVERIP_HANGZHOU;
    		rg_serverip.setVisibility(View.GONE);
    	} else {
    		rg_serverip.setVisibility(View.VISIBLE);
    	}
    	
		//启动UDP端口监听线程
		if (null == mUDPServer) {
			mUDPServer = new UDPReceiver(connectHandler);
			new Thread(mUDPServer).start();
		}
		
		Intent mIntent = getIntent();
		auto_login = mIntent.getStringExtra("AUTO_LOGIN");
		if (auto_login == null) {
			auto_login = "false";
		}
		
		// 是否显示全部控件；
		String str_show = mIntent.getStringExtra("LOGIN_SHOW");
		if (str_show == null) {
			str_show = "true";
		}
		if (str_show.equals("false")) {
			updateUI_Hide();
		} else {
			updateUI_Show();
		}
	}

	private void updateUI_Hide() {
		RelativeLayout relative_layout_all = (RelativeLayout)findViewById(R.id.relative_layout_all);
		relative_layout_all.setVisibility(View.INVISIBLE);
	}
	
	private void updateUI_Show() {
		RelativeLayout relative_layout_all = (RelativeLayout)findViewById(R.id.relative_layout_all);
		relative_layout_all.setVisibility(View.VISIBLE);
	}
	
	private void updateUI() {
		int id = R.id.rb_internet;
		if (mUserInfo.mLoginMode ==  PubDefine.SmartPlug_Connect_Mode.Internet.ordinal()) {
			 id = R.id.rb_internet;
		} else if (mUserInfo.mLoginMode ==  PubDefine.SmartPlug_Connect_Mode.WiFi.ordinal()) {
			 id = R.id.rb_wifi;
		} else {
			 id = R.id.rb_shake;
		}
		mRgLoginMode.check(id);
	}
	
	private RadioGroup.OnCheckedChangeListener onChangeRadio = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.rb_internet:
				mEdtUser.setEnabled(true);
				mEdtPwd.setEnabled(true);			
				mBtnLogin.setText(R.string.login_login);
				mUserInfo.mLoginMode = PubDefine.SmartPlug_Connect_Mode.Internet.ordinal();
				mRlLoginInput.setVisibility(View.VISIBLE);
				mFindPwd.setVisibility(View.VISIBLE);
				setTitleRightButtonVisible(true);
				setTitle(R.string.login_login);
				break;
			case R.id.rb_wifi:
				mEdtUser.setEnabled(false);
				mEdtPwd.setEnabled(false);			
				mBtnLogin.setText(R.string.smartplug_wifi_login);
				mUserInfo.mLoginMode = PubDefine.SmartPlug_Connect_Mode.WiFi.ordinal();;
				mRlLoginInput.setVisibility(View.INVISIBLE);
				mFindPwd.setVisibility(View.INVISIBLE);
				setTitleRightButtonVisible(false);
				setTitle(R.string.smartplug_wifi_login);
				break;
			case R.id.rb_shake:
				mEdtUser.setEnabled(false);
				mEdtPwd.setEnabled(false);			
				mBtnLogin.setText(R.string.smartplug_shake_shake);
				mUserInfo.mLoginMode = PubDefine.SmartPlug_Connect_Mode.Shake.ordinal();
				mRlLoginInput.setVisibility(View.INVISIBLE);
				mFindPwd.setVisibility(View.INVISIBLE);
				setTitleRightButtonVisible(false);
				setTitle(R.string.smartplug_shake_shake);
				break;
			default:
				mEdtUser.setEnabled(true);
				mEdtPwd.setEnabled(true);			
				mBtnLogin.setText(R.string.login_login);
				mUserInfo.mLoginMode = PubDefine.SmartPlug_Connect_Mode.Internet.ordinal();
				mRlLoginInput.setVisibility(View.VISIBLE);
				mFindPwd.setVisibility(View.VISIBLE);
				setTitleRightButtonVisible(true);
				setTitle(R.string.login_login);
				break;
			}
			
			SharedPreferences.Editor editor = mLoginRef.edit(); 
			editor.putInt("connect_mode", mUserInfo.mLoginMode);
			editor.commit();
			
			refreshUI() ;
		}
	};	
	
	private View.OnClickListener onInterLoginClick = new View.OnClickListener(){

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, InterConnect.class);
			startActivity(intent);
		}
	};
	
	private TextWatcher userTextWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
			updateEdit(mEdtUser, mBtnDelUser);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
		
	};	
	
	private TextWatcher pwdTextWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
			updateEdit(mEdtPwd, mBtnDelPwd);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
		
	};	
	
	@Override
	protected void onResume() {
		super.onResume();
		mUserInfo.mName = mLoginRef.getString("username", "");
		mUserInfo.mPwd = mLoginRef.getString("pwd", "");
		mUserInfo.mKeepPwd = mLoginRef.getBoolean("keeppwd", false);
		mUserInfo.mEMail= mLoginRef.getString("email", "");
		mUserInfo.mLoginMode = mLoginRef.getInt("connect_mode", PubDefine.SmartPlug_Connect_Mode.Internet.ordinal());
		
		refreshUI();
		updateUI();
		//PubDefine.disconnect();
		
		//增加自动登陆功能
		if (mUserInfo.mLoginMode ==  PubDefine.SmartPlug_Connect_Mode.Internet.ordinal()) {
	    	new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (auto_login.equals("true") == true && !mEdtPwd.getText().toString().isEmpty()) {
						mBtnLogin.performClick();
					} else {
						//clear_pwd();
					}
				}
	    	}, 100);
		} else if (mUserInfo.mLoginMode ==  PubDefine.SmartPlug_Connect_Mode.WiFi.ordinal()) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (auto_login.equals("true") == true) {
						mBtnLogin.performClick();
					}
				}
	    	}, 100);
		} else {
			// do nothing...
		}
	}
	
	
	private void refreshUI() {
		switch (mUserInfo.mLoginMode) {
		case 0:
			mRdInternetLogin.setChecked(true);
			if (null != mUserInfo) {
				mEdtUser.setText(mUserInfo.mName);
				mChkKeepPwd.setChecked(mUserInfo.mKeepPwd);
				if (true == mChkKeepPwd.isChecked()) {
					if (!mUserInfo.mName.isEmpty()) {
						mEdtPwd.setText(mUserInfo.mPwd);
					}
				} else {
					mEdtPwd.setText("");
					mEdtPwd.setHint(getString(R.string.login_inputpwd));
				}					
			}
			mEdtUser.requestFocus();
			mEdtUser.setSelection(mEdtUser.getText().length());				
			break;
		case 1:
			mRdWiFiLogin.setChecked(true);
			break;
		case 2:
			mRdShake.setChecked(true);
			break;
		default:
			mRdInternetLogin.setChecked(true);
			break;
		}
	}
	
	// ҡһҡ ��ť����ʵ��
	private View.OnClickListener onShakeShake = new View.OnClickListener(){

		@Override
		public void onClick(View view) {
			
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, ShakeShake.class);
			startActivity(intent);
//			finish();
			
		}
	};
	
	// 登录界面，使用直连方式WiFi模式的主入口函数；
	private void connectWithWiFi() {
		WifiAdmin wifiAdmin = new WifiAdmin(LoginActivity.this);
		if (false == wifiAdmin.isWifiEnabled()) {
			PubFunc.thinzdoToast(LoginActivity.this, 
					LoginActivity.this.getString(R.string.smartplug_wifi_notopen));	
			return;
		}
		
		PubDefine.g_Connect_Mode = PubDefine.SmartPlug_Connect_Mode.WiFi;

		loadWiFiStore();
		if (null == mStored_SSID || mStored_SSID.isEmpty()) {
			
			PubStatus.g_CurUserName = PubDefine.COMPANY_NAME;
			Intent intent = new Intent();
			intent.putExtra("SOURCE", "LoginActivity");
			intent.setClass(LoginActivity.this, AddSocketActivity2.class);
			startActivity(intent);	
			finish();
		} else {
			WifiManager WifiManager = (WifiManager) LoginActivity.this
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = WifiManager.getConnectionInfo();
			String currentSSID = info.getSSID().replace("\"", ""); 
			if (currentSSID.equals(mStored_SSID)) {
				mDlg = PubFunc.createProgressDialog(LoginActivity.this, 
						LoginActivity.this.getString(R.string.smartplug_oper_connect_plug), false);
				mDlg.show();
				SmartPlugWifiMgr.createWifiSocket(connectWifiHandler, "", 0);
			} else {
				PubStatus.g_CurUserName = "Thingzdo";					
				Intent intent = new Intent();
				intent.putExtra("SOURCE", "LoginActivity");
				intent.setClass(LoginActivity.this, AddSocketActivity2.class);
				startActivity(intent);
				finish();
			}
		}
	}
	
	private SmartPlugProgressDlg mDlg = null; 
	/*private View.OnClickListener onWifiConnect = new View.OnClickListener(){

		@Override
		public void onClick(View view) {
			WifiAdmin wifiAdmin = new WifiAdmin(view.getContext());
			if (false == wifiAdmin.isWifiEnabled()) {
				PubFunc.thinzdoToast(LoginActivity.this, 
						LoginActivity.this.getString(R.string.smartplug_wifi_notopen));	
				return;
			}
			
			mStored_SSID   = mWifiInfo.getString("SSID", "");
			mStored_PLUGID = mWifiInfo.getString("PLUGID", "");
			if (null == mStored_SSID || mStored_SSID.isEmpty()) {
				PubDefine.g_Connect_Mode = 1;
				PubStatus.g_CurUserName = PubDefine.COMPANY_NAME;
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, AddSocketActivity2.class);
				startActivity(intent);	
				finish();
			} else {
				WifiManager WifiManager = (WifiManager) view.getContext()
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = WifiManager.getConnectionInfo();
				String currentSSID = info.getSSID().replace("\"", ""); 
				if (currentSSID.equals(mStored_SSID)) {
					mDlg = PubFunc.createProgressDialog(view.getContext(), 
							view.getContext().getString(R.string.smartplug_oper_connect_plug), false);
					mDlg.show();
					SmartPlugWifiMgr.createWifiSocket(connectWifiHandler, "");
				} else {
					PubDefine.g_Connect_Mode = 1;
					PubStatus.g_CurUserName = "Thingzdo";					
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, AddSocketActivity2.class);
					startActivity(intent);
					finish();
				}
			}
		}
	};*/
	
	private Handler connectWifiHandler = new Handler() {
		public void handleMessage(Message msg) {
			PubDefine.g_Connect_Mode = PubDefine.SmartPlug_Connect_Mode.WiFi;
			PubStatus.g_CurUserName = "Thingzdo";
			
			if (null != mDlg) {
				mDlg.dismiss();
				mDlg = null;
			}
			
			if (1 == msg.what) {
		    	mWifiDevice = (DeviceStatus)msg.obj;
		    	mStored_PLUGID = mWifiDevice.mModuleId;
		    	new UpdateTableTask().execute();
		    } else {
		    	PubFunc.thinzdoToast(LoginActivity.this, 
		    			LoginActivity.this.getString(R.string.smartplug_oper_connect_fail));
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, AddSocketActivity2.class);
				startActivity(intent);	
				finish();
		    }
		};
	};
	
	// UpdateTableTask 只在WiFi模式下调用
	class UpdateTableTask extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			addNewPlugToDB();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Intent intent = new Intent();
			if (null == mStored_PLUGID || mStored_PLUGID.isEmpty()) {
				intent.setClass(LoginActivity.this, AddSocketActivity2.class);	
			} else {
				intent.putExtra("PLUGID", mStored_PLUGID);
				intent.putExtra("PLUGIP", "");				// IP是 插座作为AP自动分配的；
				intent.putExtra("PLUG_SSID", mStored_SSID);
				intent.putExtra("ONLINE", true);
				
				String deviceType = PubFunc.getDeviceTypeFromSSID(mStored_SSID);
				String productType = PubFunc.getProductTypeFromSSID(mStored_SSID);
				
		  	  	if ( (deviceType == PubDefine.DEVICE_SMART_PLUG && productType == PubDefine.PRODUCT_PLUG) || 
					 (deviceType == PubDefine.DEVICE_SMART_PLUG_BOARD) ) {																	// 1_1, 2_
		  	  		intent.setClass(mContext, PlugDetailActivity.class);
				} else if (deviceType == PubDefine.DEVICE_SMART_KETTLE) {																	// 6_1
					intent.setClass(mContext, DetailKettleActivity.class);
				} else if (deviceType == PubDefine.DEVICE_SMART_CURTAIN) {																	// 3_1
					intent.setClass(mContext, DetailCurtainActivity.class);
				} else if (deviceType == PubDefine.DEVICE_SMART_WINDOW) {
					intent.setClass(mContext, DetailWindowActivity.class);
				} else if (deviceType == PubDefine.DEVICE_SMART_PLUG && productType == PubDefine.PRODUCT_SMART_PLUG_AIRCON) {		// 1_2
					intent.setClass(mContext, PlugDetailActivity.class);
				} else if (deviceType == PubDefine.DEVICE_SMART_PLUG && productType == PubDefine.PRODUCT_SMART_PLUG_ENERGE) {		// 1_3
					intent.setClass(mContext, PlugDetailActivity.class);
				} else {
					return ;
				}
			}
			
			startActivity(intent);
			finish();			
		}
		
	};
	
	private boolean addNewPlugToDB() {
		SmartPlugHelper plugProvider = new SmartPlugHelper(this);
		plugProvider.clearSmartPlug();
		
		SmartPlugDefine plug = new SmartPlugDefine();
		plug.mUserName  = PubStatus.g_CurUserName;
		plug.mPlugName  = mStored_SSID;
		plug.mPlugId    = mStored_PLUGID;
		plug.mIsOnline  = true;
		plug.mDeviceStatus = mWifiDevice.mPwrStatus;
		plug.mVersion   = mWifiDevice.mVersion;
		plug.mMAC 		= mWifiDevice.mPlugMac;
		plug.mModuleType = mWifiDevice.mModuleType;
		plug.mPosition  = mWifiDevice.mPwrStatus;
		
		plug.mSubDeviceType = PubFunc.getDeviceTypeFromModuleType(plug.mModuleType);
		plug.mSubProductType = PubFunc.getProductTypeFromModuleType(plug.mModuleType);
		
		plug.mFlashMode = mWifiDevice.mFlashMode;
		plug.mProtocolMode = mWifiDevice.mProtocolMode;
		
		plug.mColor_R   = mWifiDevice.mColorRed;
		plug.mColor_G   = mWifiDevice.mColorGreen;
		plug.mColor_B   = mWifiDevice.mColorBlue;
		
		
		plugProvider.addSmartPlug(plug);
		
		SmartPlugTimerHelper timerHelper = new SmartPlugTimerHelper(this);
		timerHelper.clearTimer(mStored_PLUGID);
		for (int i = 0; i < mWifiDevice.mTimer.size(); i++) {
			timerHelper.addTimer(mWifiDevice.mTimer.get(i));	
		}
		
		timerHelper = null;
		plugProvider = null;
		return true;
	}
	
	private int saveLoginMode() {
		int redId = R.string.login_pwd_network_invalid;
		if (mRdInternetLogin.isChecked()) {
			 PubDefine.g_Connect_Mode = PubDefine.SmartPlug_Connect_Mode.Internet;
		} 
		if (mRdWiFiLogin.isChecked()) {
			PubDefine.g_Connect_Mode = PubDefine.SmartPlug_Connect_Mode.WiFi;
			redId = R.string.login_pwd_wifi_notopen;
		}
		if (mRdShake.isChecked()) {
			PubDefine.g_Connect_Mode = PubDefine.SmartPlug_Connect_Mode.Shake;
			redId = R.string.login_pwd_wifi_notopen;
		}
		
		saveUserInfo();	
		
		return redId;
	}
	
	private View.OnClickListener onLoginClick = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			saveServerIP();
			int resId = saveLoginMode();
			if (false == PubFunc.isNetworkAvailable(mContext)) {
				PubFunc.thinzdoToast(LoginActivity.this, getString(resId).toString());
				return;
			} 			
			
			// lishimin add wifi direct checkbox
			if (mRdWiFiLogin.isChecked() == true) {
				connectWithWiFi();
			} else if (mRdInternetLogin.isChecked() == true) {
				
				mInputPwd = mEdtPwd.getText().toString().trim();
				mInputUser = mEdtUser.getText().toString().trim();
				if (TextUtils.isEmpty(mInputPwd)) {
					PubFunc.thinzdoToast(LoginActivity.this, getString(R.string.login_inputpwd).toString());
					return;
				}
				if (null != mProgress) {
					mProgress.dismiss();
					mProgress = null;
				}
            	mProgress = PubFunc.createProgressDialog(mContext, getString(R.string.login_logining), false);
            	mProgress.show(); 				
            	new Thread(runnable).start();
			} else {
				//shake login
				shakePlugLogin();
			}
		}
	};
	
	private void shakePlugLogin() {
		new ActionSheetDialog(mContext)
		.builder()
		.setCancelable(true)
		.setCanceledOnTouchOutside(true)
		.addSheetItem(LoginActivity.this.getString(R.string.login_shake_addplug), SheetItemColor.Blue,
				new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						PubDefine.g_Connect_Mode = PubDefine.SmartPlug_Connect_Mode.Shake;
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, AddSocketActivity2.class);
						startActivity(intent);
						finish();						
					}
		})			
		.addSheetItem(LoginActivity.this.getString(R.string.login_shake_enter), SheetItemColor.Blue,
				new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						PubDefine.g_Connect_Mode = PubDefine.SmartPlug_Connect_Mode.Shake;
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, ShakeShake.class);
						startActivity(intent);
						finish();							
					}
		}).show();			
		
	}
	
	private View.OnClickListener registerClick = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			if (false == PubFunc.isNetworkAvailable(LoginActivity.this)) {
				PubFunc.thinzdoToast(mContext, 
						mContext.getString(R.string.register_network_vaild) );
				return;
			}
			
			
			Intent intent = new Intent();
			intent.setClass(mContext, RegisterActivity.class);
			intent.putExtra("State", 1); 
			startActivity(intent);
		}
	};	
	
    private Handler doLoginHander = new Handler() {
        public void handleMessage(Message msg) {
        	if (1 == msg.what) {
        		onlineLogin((String)msg.obj);
        	} else if (0 == msg.what) {
        		PubFunc.thinzdoToast(LoginActivity.this, 
        				mContext.getString(R.string.login_cmd_socket_timeout));       		
        	} else {
        		PubFunc.thinzdoToast(LoginActivity.this, 
        				mContext.getString(R.string.login_cmd_socket_timeout));         		
        	}
        };	
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//PubDefine.disconnect();
		unregisterReceiver(mLoginRev);
    	//RevCmdFromSocketThread.getInstance().setRunning(false);
	}
	
	public LoginActivity() {
	}
	
	private View.OnClickListener findPwdClick = new View.OnClickListener(){
		@Override
		public void onClick(View arg0) {
			if (!PubFunc.isNetworkAvailable(mContext)) {
				PubFunc.thinzdoToast(mContext, mContext.getString(R.string.login_pwd_network_invalid));
				return;
			}
			
			Intent intAct = new Intent();
			intAct.setClass(LoginActivity.this, FindPwdActivity.class);
			startActivity(intAct);
		} 
	};
	
	
	private void onlineLogin(String strpwd) {    
		String userName = mEdtUser.getText().toString().trim(); 
		if (!userName.isEmpty() && !strpwd.isEmpty()) {

	   		String version = PubFunc.getAppVersion();

			PubDefine.g_First_Login = true;
			StringBuffer sb = new StringBuffer();
	    	sb.append(SmartPlugMessage.CMD_SP_LOGIN_SERVER)
	    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
	    	  .append(userName)
	    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)	    	  
	    	  .append(version)
	    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
	    	  .append(strpwd);
	    	  	    	
	    	PubStatus.g_userPwd  = strpwd;
	    	sendMsg(true, sb.toString(), true);
	    }
	}

	
	private void doConnectServer() {
		AsyncTask<Void,Void,Void> connect = new AsyncTask<Void,Void,Void>() {
            @Override
            protected void onPreExecute() {
            	//mProgress = PubFunc.createProgressDialog(mContext, getString(R.string.login_logining), false);
            	//mProgress.show();            	
            	super.onPreExecute();
           	
            }
			@Override
			protected Void doInBackground(Void... arg0) {
		    	//if (null == PubDefine.global_socket) {
		    	//	doLoginHander.sendMessage(doLoginHander.obtainMessage(0, ""));
		    	//	mProgress.dismiss();
		    	//} else {				
		    		doLoginHander.sendMessage(doLoginHander.obtainMessage(1, mInputPwd));
		    	//}
		    	return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
			}
			
		};
		connect.execute();
	}
	
	private void saveUserInfo() {
		UserDefine loginUser = new UserDefine();
		loginUser.mName = mEdtUser.getText().toString().trim();
		loginUser.mPwd  = mEdtPwd.getText().toString().trim();
		loginUser.mEMail = mEmail;
		loginUser.mKeepPwd = mChkKeepPwd.isChecked() ? true : false;
		loginUser.mLoginMode =  PubDefine.g_Connect_Mode.ordinal();
		
		SharedPreferences.Editor editor = mLoginRef.edit(); 
		if ( PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
			editor.putString("username", loginUser.mName);
			editor.putBoolean("keeppwd", loginUser.mKeepPwd);
			editor.putString("pwd", loginUser.mPwd);
			editor.putString("email", loginUser.mEMail);
		}
		editor.putInt("connect_mode", loginUser.mLoginMode);
		editor.commit();

		PubStatus.g_CurUserName = loginUser.mName;
		PubStatus.g_userEmail = loginUser.mEMail;
	}
	
	private void updatePlugOffline() {
		mPlugHelper.setAllPlugsOffline();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			SmartPlugApplication.getInstance().exit();
			Intent intent = new Intent(Intent.ACTION_MAIN);  
			intent.addCategory(Intent.CATEGORY_HOME);  
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
			startActivity(intent);  
			android.os.Process.killProcess(android.os.Process.myPid());
			
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private Runnable runnable = new Runnable(){
	     @Override
	     public void run()  {
		     connectHandler.sendEmptyMessage(1);
	     }
	 };	
	 
	 private Handler connectHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				PubDefine.g_Connect_Mode = PubDefine.SmartPlug_Connect_Mode.Internet;
				doConnectServer();	
			} else {
				if (null != mProgress) {
					mProgress.dismiss();
				}				
				PubFunc.thinzdoToast(mContext, mContext.getString(R.string.login_cmd_socket_timeout));
				timeoutHandler.removeCallbacks(timeoutProcess);						
			}
		}; 
	 };
	 
	 protected void onNewIntent(Intent intent) {
		 int forceLogout = intent.getIntExtra("FORCE_LOGOUT", 0);
		 if (1 == forceLogout) {
			 PubFunc.thinzdoToast(SmartPlugApplication.getContext(), 
		    			SmartPlugApplication.getContext().getString(R.string.user_force_logout));
		 }
	 }
	 
	 protected void clear_pwd() {
		SharedPreferences.Editor editor = mLoginRef.edit(); 
		editor.putString("pwd", "");
		editor.commit();
		
		mUserInfo.mPwd = "";
		mEdtPwd.setText("");
	 }
	 
		private void saveServerIP() {
			editor = mSharedPreferences.edit();

			if (PubDefine.RELEASE_VERSION == true) {
	    		PubDefine.THINGZDO_HOST_NAME = PubDefine.SERVERIP_HANGZHOU;
			} else {
				if (rb_server_sz.isChecked() == true) {
					PubDefine.THINGZDO_HOST_NAME = PubDefine.SERVERIP_SHENZHEN;
				} else if (rb_server_hz.isChecked() == true) {
					PubDefine.THINGZDO_HOST_NAME = PubDefine.SERVERIP_HANGZHOU;
				} else if (rb_server_debug.isChecked() == true) {
					PubDefine.THINGZDO_HOST_NAME = PubDefine.SERVERIP_DEBUG;
				}
			}
			editor.putString("serverip", PubDefine.THINGZDO_HOST_NAME);

			editor.commit();
		}
		
		private void loadServerIP() {
			PubDefine.THINGZDO_HOST_NAME = mSharedPreferences.getString("serverip", PubDefine.SERVERIP_HANGZHOU);
		}
}
