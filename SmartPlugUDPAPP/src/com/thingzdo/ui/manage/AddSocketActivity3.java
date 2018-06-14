package com.thingzdo.ui.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.shakeshake.ShakeShake;
import com.thingzdo.ui.smartplug.AppServerReposeDefine;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.ui.wheelutils.ActionSheetDialog;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.OnSheetItemClickListener;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.SheetItemColor;
import com.thingzdo.ui.wheelutils.MyAlertDialog;
import com.thingzdo.util.ThingzdoCheckBox;

public class AddSocketActivity3 extends TitledActivity implements
		OnClickListener {
	private final int SEARCH_WIFI_EMPTY = 93723;
	private final int SEARCH_WIFI_NOTEMPTY = 9374223;
	private final int CMD_SOCKET_ERROR = 2213;
	private final int ADD_PLUG_TIMEOUT = 221545;
	private final int COLLECT_INFO_TIMEOUT = 237973;
//	private final int CMD_SOCKET_OK = 22133;
	private final int ADD_PLUG_TIMEOUT00 = 2221545;
	private final int  ADD_PLUG_TIMEOUT01 = 783;

	private EditText mNameView;
	private EditText mMACView;
	private EditText mPasswdView;

	private WifiAdmin wifiAdmin;
	private List<ScanResult> mRouterList;

	private SmartPlugHelper mPlugProvider = null;

	private ImageView mImgDelPlugName = null;
	private ThingzdoCheckBox checkPwd = null;
	private ThingzdoCheckBox checkClearTimer = null;
	private ThingzdoCheckBox checkProtocolMode = null;
	private Button mBtnFinish = null;
	private TextView mViewSSID = null;
	//private TextView mViewRouterMode = null;
	private ImageView mImgRouter = null;
	private String plugSSID = "";
	private String plugSSID_Mac = "";
	ConnectivityManager connManager = null;
	private String connect_mode = "SmartPlug_Connect_Mode.Internet";	// 调用此类的来源界面；
	
	private String mPlugName = "";
	private String mPlugID = "";
	private int  mExistPlugCount = 1;
	
	private SharedPreferences mRouter = null;
	private String mStoredRouter = "";
	private String mStoredRouterMode = "";
	private RelativeLayout mLayoutRouter = null;
	//private RelativeLayout mLayoutRouterMode = null;
	
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;
	private List<Map<String, Object>> m_rpData;			// 路由器SSID和密码的信息
	
	private static ArrayList<String> mRouterMode = new ArrayList<String>();
	static {
		mRouterMode.add("Open");
		mRouterMode.add("WAP");
		mRouterMode.add("WEP");	
	};
	
	public int m_pushMessageOK = 0;
	
	// 保存点击确认按钮后，后台执行的步骤；
	private static int STATUS_INIT_OK 		= 0;
	private static int STATUS_AP_CONFIG_OK 	= 1;
	private static int STATUS_SERVER_ADD_OK = 2;
	private int current_step 				= STATUS_INIT_OK;
	
	private BroadcastReceiver mAddPlugRev = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(PubDefine.LOGIN_BROADCAST) && 
					false  == PubDefine.g_First_Login) {
				int ret = intent.getIntExtra("RESULT", 1);
				timeoutHandler.removeCallbacks(timeoutProcess);
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
				case 0:
					//addPlugHandler.sendEmptyMessage(0);	
					break;
				default:
					if (mProgress!=null){
						mProgress.dismiss();
					}					
					PubFunc.thinzdoToast(AddSocketActivity3.this, message);
					break;
				}
			}			
			
			
			if (intent.getAction().equals(PubDefine.PLUG_ADD_TASK)) {
				if (mProgress!=null){
					mProgress.dismiss();
				}
				timeoutHandler.removeCallbacks(timeoutProcess);
				int ret = intent.getIntExtra("RESULT", 1);
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
				case 0:
					current_step = STATUS_SERVER_ADD_OK;
					PubFunc.thinzdoToast(AddSocketActivity3.this, getString(R.string.smartplug_add_success));
					addPlugBackground.execute();
					break;
				default:
					mBtnFinish.setEnabled(true);
					PubFunc.thinzdoToast(AddSocketActivity3.this, message);
					Intent i = new Intent();
					if (1 < mExistPlugCount) {
					    i.setClass(AddSocketActivity3.this, AddSocketActivity2.class);
					    startActivity(i);
					 }   
					 finish();
					break;
				}
			}
			
			if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
	        	//wifi连接上与否  
	            System.out.println("网络状态改变");  
	            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);  
	            if(info.getState().equals(NetworkInfo.State.DISCONNECTED)){  
	                System.out.println("wifi网络连接断开");  
	            }  
	            else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {  
	                WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);  
	                WifiInfo wifiInfo = wifiManager.getConnectionInfo();                  
	                  
	                //获取当前wifi名称  
	                System.out.println("连接到网络 " + wifiInfo.getSSID());
	                
	                if (m_pushMessageOK != 2) {
	                	if (m_pushMessageOK == 1) {
	                		m_pushMessageOK = 2;
	                	}
	                	Message msg = new Message();
				        msg.what = 1;
				        msg.obj = whetherToRemoveTheDoubleQuotationMarks(wifiInfo.getSSID());
				        procConnectHandler.sendMessage(msg) ;	
	                }
	            }  
	        }
			
	        if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
	        	//wifi打开与否
	            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);  
	              
	            if(wifistate == WifiManager.WIFI_STATE_DISABLED){  
	                System.out.println("系统关闭wifi");  
	            }  
	            else if(wifistate == WifiManager.WIFI_STATE_ENABLED){  
	                System.out.println("系统开启wifi");  
	            }  
	        }			
		}
	};
	
	/*
	 * 增加插座的异步任务
	 */
	AsyncTask<Void, Void, Void> addPlugBackground = new AsyncTask<Void, Void, Void>() {

		@Override
		protected Void doInBackground(Void... arg0) {
			addNewPlug2LocalDB();
			return null;
		}
		
		protected void onPostExecute(Void result) {
		    //Intent it = new Intent();
		    //it.putExtra("new_plug", SinglePlugUtil.plugChipID);
		    //it.setClass(AddSocketActivity3.this, SmartPlugActivity.class);
		    //startActivity(it);
		    finish();			
		};
		
	};
	

	/*
	 * 处理插座切换、连接等操作结果
	 */
	private Handler mHandlerPlug = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == SEARCH_WIFI_EMPTY) {
				if (mProgress != null){
					mProgress.dismiss();
				}				
				PubFunc.thinzdoToast(AddSocketActivity3.this, 
						getString(R.string.smartplug_oper_noscanrouter));
			} else if (msg.what == SEARCH_WIFI_NOTEMPTY) {
				initRouters();
			} else if (msg.what == COLLECT_INFO_TIMEOUT) {
				PubFunc.thinzdoToast(AddSocketActivity3.this, 
						getString(R.string.smartplug_oper_getplugfail));
				if(mProgress != null){
				   mProgress.dismiss();
				} 
			} else if( msg.what == SinglePlugUtil.COLLECT_INFO_SUCCESS){
				if (mProgress != null){
				   mProgress.dismiss();
				} 
				
				if (null == SinglePlugUtil.macAddress) {
					return;
				}
				
				String mac = SinglePlugUtil.macAddress.toUpperCase(Locale.getDefault());
				plugSSID_Mac = mac;
				mMACView.setText(mac);
				//mNameView.setText(PubDefine.COMPANY_NAME + StringUtils.PACKAGE_UNDERLINE_SYMBOL + SinglePlugUtil.plugChipID);
			} else if (msg.what == SinglePlugUtil.AP_TO_STATION_ERROR) {
				PubFunc.thinzdoToast(AddSocketActivity3.this, 
						getString(R.string.smartplug_oper_connectrouterfail));
				if (mProgress != null){
				    mProgress.dismiss();
				} 
			} else if(msg.what == SinglePlugUtil.AP_TO_STATION_SUCCESS){
				PubFunc.log("AP to station success");
				current_step = STATUS_AP_CONFIG_OK;
				openWiFi();
				if (PubDefine.SmartPlug_Connect_Mode.Internet == PubDefine.g_Connect_Mode) {
					setPlugParameter();
				} else {
					doShake();
				}
			} else if (msg.what == AppServerReposeDefine.Socket_Connect_FAIL) {

			} else if(msg.what == CMD_SOCKET_ERROR){
				PubFunc.thinzdoToast(AddSocketActivity3.this, 
						getString(R.string.login_cmd_socket_timeout));
				if (mProgress != null){
				    mProgress.dismiss();
				} 
			} else if(msg.what == ADD_PLUG_TIMEOUT){
				PubFunc.log("ADD_PLUG_TIMEOUT");
				PubFunc.thinzdoToast(AddSocketActivity3.this, 
						getString(R.string.smartplug_add_fail));
				if (mProgress != null){
				    mProgress.dismiss();
				} 
			} else if(msg.what == ADD_PLUG_TIMEOUT00){
				PubFunc.thinzdoToast(AddSocketActivity3.this, 
						getString(R.string.smartplug_oper_nowifi));
				if (mProgress != null){
				    mProgress.dismiss();
				} 
			} else if(msg.what == ADD_PLUG_TIMEOUT01){
				PubFunc.thinzdoToast(AddSocketActivity3.this, 
						getString(R.string.smartplug_oper_socketexception));
				if (mProgress != null){
				    mProgress.dismiss();
				} 
			} else if (msg.what == SinglePlugUtil.COLLECT_INFO_ERROR) {
				qryPlugHandler.sendEmptyMessage(0);
			} else if (msg.what == SinglePlugUtil.AP_TO_STATION_ERROR2) {
				PubFunc.thinzdoToast(AddSocketActivity3.this, 
						getString(R.string.smartplug_ap2station_fail));
				if (mProgress != null){
				    mProgress.dismiss();
				} 				
			} else {
				PubFunc.log("ADD_PLUG_TIMEOUT msg.what" + msg.what);
				PubFunc.thinzdoToast(AddSocketActivity3.this, 
						getString(R.string.smartplug_add_fail));
				if (mProgress != null){
				    mProgress.dismiss();
				} 				
			}
		}
	};
	
	/*
	 * 连接WiFi
	 */
	private boolean openWiFi() {
		if (wifiAdmin == null) {
			return false;
		}
		
		if (!wifiAdmin.isWifiEnabled()) {
			PubFunc.thinzdoToast(this, getString(R.string.smartplug_oper_openwifi));
			wifiAdmin.openWifi();
		}
	    String ssid = mViewSSID.getText().toString();  
		String passwd = this.mPasswdView.getText().toString();
		int type = getRouterSecType(ssid);

		WifiConfiguration localConfig = null;
		/*if (passwd== null || passwd.isEmpty()){
			localConfig = wifiAdmin
					.CreateWifiInfo(ssid, "", 1);
			wifiAdmin.addNetwork(localConfig);
		} else{
			localConfig = wifiAdmin
					.CreateWifiInfo(ssid, passwd, 3);
			wifiAdmin.addNetwork(localConfig);
		}*/
		localConfig = wifiAdmin.CreateWifiInfo(ssid, passwd, type);	
		if (false == wifiAdmin.addNetwork(localConfig)) {
//			PubFunc.thinzdoToast(this, "切换到" + ssid + "失败，请手工切换");
			return false;
		}	
		
		return true;
		}	

    /** These values are matched in string arrays -- changes must be kept in sync */
    static final int SECURITY_NONE  = 1;
    static final int SECURITY_WEP   = 2;
    static final int SECURITY_WAP   = 3;
	/*
	 * 获得路由器加密
	 */
	private int getRouterSecType(String ssid) {
		ScanResult wifi = getRouter(ssid);
		if (null == wifi) {
			return SECURITY_NONE;
		}
		if (wifi.capabilities.contains("WPA")) {
			return  SECURITY_WAP;
		}
		if (wifi.capabilities.contains("WEP")) {
			return  SECURITY_WEP;
		}		
		
		return SECURITY_NONE;
		
	}
	
	/*
	 * 执行摇一摇操作
	 */
	private void doShake() {
		new MyAlertDialog(AddSocketActivity3.this).builder()
		.setCancelable(true)
		.setMsg(getString(R.string.smartplug_mgr_entershake))
		.setPositiveButton(AddSocketActivity3.this.getString(R.string.smartplug_ok), new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(AddSocketActivity3.this, ShakeShake.class);
				startActivity(intent);
				finish();
			}
		})
		.setNegativeButton(AddSocketActivity3.this.getString(R.string.smartplug_mgr_addshake),  new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(AddSocketActivity3.this, AddSocketActivity2.class);
				startActivity(intent);
				finish();
			}
		})			
		.show();		
	}
	
    /*
     * 查询插座信息的Handler
     * 尝试3次查询
     */
	private int qryCounter = 0;
	private Handler qryPlugHandler = new Handler() {
		public void handleMessage(Message msg) {
			qryCounter++;
			if (qryCounter < 2) {
				SystemClock.sleep(1000);
				SinglePlugUtil.collectInfo(AddSocketActivity3.this.mHandlerPlug);	
			} else {
				qryCounter = 0;
				PubFunc.thinzdoToast(AddSocketActivity3.this, 
						getString(R.string.smartplug_oper_getpluginfofail));
				if (mProgress != null){
				    mProgress.dismiss();
				}				
			}
					
		};
	};
	
	private int deviceVersion;
	/*
	 * 根据Android的版本判断获取到的SSID是否有双引号
	 */
	public String whetherToRemoveTheDoubleQuotationMarks(String ssid) {
	  //获取Android版本号
	  deviceVersion = Build.VERSION.SDK_INT;
	  if (deviceVersion >= 17) {
		   if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
		    ssid = ssid.substring(1, ssid.length() - 1);
		   }
	  }
	  return ssid;
	}
	
	/*
	 * WiFi连接上插座或服务器后的处理 
	 */
    private Handler procConnectHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		if (1 == msg.what) {
    			String ssid = (String)msg.obj;
    			if (null == mViewSSID) {
    				return;
    			}
    			if (ssid.equals(mViewSSID.getText().toString())) {
	               
	                if (PubDefine.SmartPlug_Connect_Mode.Internet == PubDefine.g_Connect_Mode) {
	                	 //如果当前是互联网模式，连接上wifi后向服务器增加插座                	
	                	SystemClock.sleep(6000);
	                	addPlug2Server();
	                } else if (PubDefine.SmartPlug_Connect_Mode.Shake == PubDefine.g_Connect_Mode) {
	                    doShake();	
	                }
    			} else if (ssid.equals(plugSSID)) {
    				SinglePlugUtil.collectInfo(mHandlerPlug);
    			}
    		}
    	};
    };	
	

	private void saveRouterPWDData(String Router_SSID, String Router_pwd) {
		
		editor = mSharedPreferences.edit();
		editor.putString("SSID_" + Router_SSID, Router_pwd);
		editor.commit();
	}

	private String loadRouterPWDData(String Router_SSID) {
	
		return mSharedPreferences.getString("SSID_" + Router_SSID, "");
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_add_socket3, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		mPlugProvider = new SmartPlugHelper(this);
		plugSSID = this.getIntent().getStringExtra("SSID");
		mExistPlugCount = getIntent().getIntExtra("PLUGCOUNT", 1);
		connect_mode = getIntent().getStringExtra("CONNECT_MODE");
		
		mSharedPreferences = getSharedPreferences("ROUTER_SSID", Activity.MODE_PRIVATE);

		//注册广播监听
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_ADD_TASK);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(PubDefine.LOGIN_BROADCAST);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		this.registerReceiver(mAddPlugRev, filter);

		mRouter = getSharedPreferences("Router", Activity.MODE_PRIVATE);
		mStoredRouter = mRouter.getString("SSID", "");
		mStoredRouterMode = mRouter.getString("ROUTER_MODE", mRouterMode.get(0));
		mLayoutRouter = (RelativeLayout) findViewById(R.id.layout_router);
		mLayoutRouter.setOnClickListener(this);
		//mLayoutRouterMode = (RelativeLayout) findViewById(R.id.layout_routermode);
		//mLayoutRouterMode.setOnClickListener(this);		
		
		wifiAdmin = new WifiAdmin(this);
		initView();
    	 
    	new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				searchLocalWiFi();
			}
    	}, 500);
    	
		connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		

	}
	
	/*
	 * 判断WiFi是否连接上
	 */
	public boolean isWifiConnected(){
		if(connManager == null){
			return false;
		}
		NetworkInfo mWifi  = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(mWifi == null){
			return false;
		}
		return mWifi.isConnected();
	}

	/*
	 * 连接到插座
	 */
	private void connectToPlug() {
		if (wifiAdmin == null) {
			return;
		}
		if (!wifiAdmin.isWifiEnabled()) {
			PubFunc.thinzdoToast(this, 
					getString(R.string.smartplug_oper_openwifi));
			wifiAdmin.openWifi();
		}
		// lishimin 切换到插座；
		WifiConfiguration plugConfig = wifiAdmin.CreateWifiInfo(plugSSID, "", 1);
		boolean ret = wifiAdmin.addNetwork(plugConfig);
		if(ret == false) {
			mHandlerPlug.sendEmptyMessage(SinglePlugUtil.AP_TO_STATION_ERROR2);
		}
		
		qryCounter = 0;
	}
	
	/*
	 * 初始化路由器列表
	 */
	public void initRouters() {
		mViewSSID = (TextView) this.findViewById(R.id.spinnerWiFiSSID);
		//mViewRouterMode = (TextView) this.findViewById(R.id.txtRouterMode);
		ArrayList<String> ssidLocal = new ArrayList<String>();
		for (int i = 0; i < this.mRouterList.size(); ++i) {
			ssidLocal.add(mRouterList.get(i).SSID);
		}
		
		if (mStoredRouter.isEmpty()) {
			if (0 < ssidLocal.size()) {
				mViewSSID.setText(ssidLocal.get(0).toString());
			} else {
				mViewSSID.setText("");
			}
		} else {
			if (true == ssidLocal.contains(mStoredRouter)) {
				mViewSSID.setText(mStoredRouter);	
			} else {
				if (0 < ssidLocal.size()) {
					mViewSSID.setText(ssidLocal.get(0).toString());
				} else {
					mViewSSID.setText("");
				}
			}
		}

		mPasswdView.setText(loadRouterPWDData(mViewSSID.getText().toString()));
		
		//mViewRouterMode.setText(mStoredRouterMode);
		//mViewRouterMode.setTag(getouterModeTag(mStoredRouterMode));		
	}
	
	/*
	 * 扫描本地WiFi列表
	 */
	public void searchLocalWiFi() {
		if (wifiAdmin == null) {
			return;
		}
		
		if (!wifiAdmin.isWifiEnabled()) {
			PubFunc.thinzdoToast(this, getString(R.string.smartplug_oper_openwifi));
			wifiAdmin.openWifi();
		}
		mProgress = PubFunc.createProgressDialog(this, 
				getString(R.string.smartplug_oper_initplug), false);		

		mRouterList = new ArrayList<ScanResult>();
		AsyncTask<Void, Void, Void> localAP = new AsyncTask<Void, Void, Void>() {
					
			private boolean filterScanResult(List<ScanResult> list){
				if (list == null) {
					return false;
				}
				for (int i = 0;list != null && i < list.size(); ++i) {
					if (list.get(i).SSID.startsWith(PubDefine.COMPANY_NAME)) {				
						continue;
					} 
					String ssid = list.get(i).SSID;
					
					if (true == isSSIDExist(mRouterList, ssid)) {
						continue;
					}
					PubFunc.log("SSID:" +list.get(i).SSID + ",capacity:" + list.get(i).capabilities );
					mRouterList.add(list.get(i));
				}
				return mRouterList.size() == 0 ? true :false;
			}
			
			private boolean isSSIDExist(final List<ScanResult> list, final String ssid) {
				for (int i = 0; i < mRouterList.size(); ++i){							
					if (mRouterList.get(i).SSID.equals(ssid)){
						return true;
					}							
				}
				return false;
			}			
			
			@Override
			protected Void doInBackground(Void... arg0) {
				int count = 0;
				List<ScanResult> list = null;
				do {
					try {
						wifiAdmin.startScan();
						list = wifiAdmin.getWifiList();	
						if (null != list && 0 < list.size()) {
							if (true == filterScanResult(list)) {
								break;
							}
						}
						count++;
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				} while (count < 1);
				if (mRouterList.size() == 0) {
					mHandlerPlug.sendEmptyMessage(SEARCH_WIFI_EMPTY);
				} else {
					mHandlerPlug.sendEmptyMessage(SEARCH_WIFI_NOTEMPTY);
				}
				return null;
			}
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mProgress.show();
			}
			
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				//连接插座的WiFi
				connectToPlug();
			}
			
		};
		localAP.execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SmartPlugApplication.resetTask();
	}
	
    protected void updateEdit(EditText edt, ImageView img){
		if (edt.getText().toString().isEmpty()) {
			img.setVisibility(View.INVISIBLE);
		} else {
			img.setVisibility(View.VISIBLE);
		}    	
    }	

    /*
     * 初始化界面显示
     */
	private void initView() {
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		mNameView = (EditText) findViewById(R.id.edit_plug_name);
		mNameView.setText(plugSSID);
		
		mMACView =  (EditText) findViewById(R.id.plug_mac);

		mImgDelPlugName = (ImageView) findViewById(R.id.image_delplugname);

		mBtnFinish = (Button) this.findViewById(R.id.btn_finish);
		mBtnFinish.setOnClickListener(this);

		mImgDelPlugName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mNameView.setText("");
			}
		});
		
		mNameView.addTextChangedListener(nameTextWatcher); 
		mPasswdView = (EditText) findViewById(R.id.edit_wifi_pwd);
		checkPwd = (ThingzdoCheckBox) findViewById(R.id.chkViewPwd);
		checkPwd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				boolean checked = checkPwd.isChecked(); 
				if (checked) {
					mPasswdView
							.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());
				} else {
					mPasswdView
							.setTransformationMethod(PasswordTransformationMethod
									.getInstance());
				}
				
			}
		});
		
		checkClearTimer = (ThingzdoCheckBox) findViewById(R.id.chkClearTimer);
		checkProtocolMode = (ThingzdoCheckBox) findViewById(R.id.chkProtocolMode);
		if (connect_mode.equals("PubDefine.SmartPlug_Connect_Mode.Shake")) {	// 摇一摇模式下，不可配置；默认下发UDP模式
//			checkProtocolMode.setEnabled(false);
//			((TextView)findViewById(R.id.txtProtocolMode)).setEnabled(false);
			
			checkProtocolMode.setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.txtProtocolMode)).setVisibility(View.INVISIBLE);
		} else {																// 其他模式下，可配置；可以配置TCP、UDP模式
//			checkProtocolMode.setEnabled(true);
//			((TextView)findViewById(R.id.txtProtocolMode)).setEnabled(true);
			
			checkProtocolMode.setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.txtProtocolMode)).setVisibility(View.VISIBLE);
		}
		
		mImgRouter = (ImageView) findViewById(R.id.image_morerouter);
		mImgRouter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private TextWatcher nameTextWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
			updateEdit(mNameView, mImgDelPlugName);
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
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:
			finish();
			break;
		case R.id.titlebar_rightbutton:
			if (false == checkInput()) {
				return;
			}
			break;
		case R.id.btn_finish: 
			m_pushMessageOK = 1;
			PubFunc.thinzdoToastCurTime(AddSocketActivity3.this, "1_Begin");
			
			// 检测字符串中是否包含中文字符、特殊英文字符；			
			boolean b_result = PubFunc.isRouterNameValid(mViewSSID.getText().toString());
			if (false == b_result) {
				PubFunc.thinzdoToast(AddSocketActivity3.this, 
                		AddSocketActivity3.this.getString(R.string.smartplug_oper_router_not_support_chinese));
                return;
			}

			// 校验 NewPlugName：中英文占用的字节数必须小于20（最大20个byte）
			if (mNameView.getText().toString().getBytes().length > 20) {
				PubFunc.thinzdoToast(this, getString(R.string.smartplug_ctrl_mod_plugname_length_too_long));
				return;
			}
			mBtnFinish.setEnabled(false);
			
			saveRouterPWDData(mViewSSID.getText().toString(), mPasswdView.getText().toString());
			
			//把插座切换成Station
			if (current_step == STATUS_INIT_OK) {
				// 从第一步开始执行：首先与AP联系，传递参数，然后自动与服务器连接，添加插座；
				apToStation();
			} else if (current_step == STATUS_AP_CONFIG_OK) {
				// 从第二步开始执行：直接与服务器连接，重新要求添加 插座；
            	Message msg = new Message();
		        msg.what = 1;
		        msg.obj = whetherToRemoveTheDoubleQuotationMarks(plugSSID);
		        procConnectHandler.sendMessage(msg);
			} else {
				// do nothing... because it's OK. never do here.
			}
			break;
		case R.id.layout_router:
			showRouters();
			break;
		//case R.id.layout_routermode:
		//	showRouterModeDlg();
		//	break;
		default:
			break;
		}
	}	
	
	/*
	 * 设置插座名称和ID
	 */
	private void setPlugParameter() {	
		String devName = this.mNameView.getText().toString();
		String devID = SinglePlugUtil.plugChipID;

		mPlugName = devName;
		mPlugID = devID;
	}
	
	/*
	 * 以线程方式连接服务器
	 */
	private void addPlug2Server() {
		//连接服务器线程
		if (null == mPlugName || mPlugName.isEmpty() ||
				null == mPlugID || mPlugID.isEmpty()) {
		    return;	  
		}
			
		new Thread(sendAddPlugCmd).start();		
	}

	/*
	 * 检查输入是否合法
	 */
	
	private boolean checkInput() {
		if (TextUtils.isEmpty(mNameView.getText().toString())) {
			PubFunc.thinzdoToast(AddSocketActivity3.this,
					AddSocketActivity3.this
							.getString(R.string.str_info_plugname_empty_error));
			return false;
		}
		if (TextUtils.isEmpty(mPasswdView.getText().toString())) {
			PubFunc.thinzdoToast(AddSocketActivity3.this,
					AddSocketActivity3.this
							.getString(R.string.str_info_pwd_empty_error));
			return false;
		}
		return true;
	}

	/*
	 * 把插座从AP状态切换成Station状态
	 */
	public void apToStation() {
		AsyncTask<Void, Void, Void> AP2StationTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
//				if (null == mProgress) {
//					mProgress = PubFunc.createProgressDialog(AddSocketActivity3.this, 
//							getString(R.string.smartplug_oper_execconfig), false); 
//				} else {
//					mProgress.setMessage(getString(R.string.smartplug_oper_execconfig));
//				}
				mProgress = PubFunc.createProgressDialog(AddSocketActivity3.this, 
						getString(R.string.smartplug_oper_execconfig), false); 
				mProgress.show(); 
			}
			@Override
			protected Void doInBackground(Void... arg0) {
			    if (mViewSSID.getText().toString() != null
						&& mPasswdView.getText() != null) {
					 
			    	mStoredRouter = mViewSSID.getText().toString();
			    	//mStoredRouterMode = mViewRouterMode.getText().toString();
					SharedPreferences.Editor editor = mRouter.edit(); 
					editor.putString("SSID", mStoredRouter);
					editor.putString("ROUTER_MODE", mStoredRouterMode);
					// lishimin add MacAddress
					editor.putString(SinglePlugUtil.plugChipID + "_MAC", plugSSID_Mac);
					editor.commit();
					
					PubFunc.thinzdoToastCurTime(AddSocketActivity3.this, "2_ApToSation_before");
						 
					SinglePlugUtil.apToStation(mHandlerPlug, 
							mNameView.getText().toString(),
							mViewSSID.getText().toString(),
							mPasswdView.getText().toString(),
							checkClearTimer.isChecked() ? 1 : 0,
							checkProtocolMode.isChecked() ? 1 : 2);		// lishimin 协议模式： TCP: 1, UDP: 2;
					
					PubFunc.thinzdoToastCurTime(AddSocketActivity3.this, "3_ApToSation_after");
				}
				return null;
			} 
			@Override
			protected void onPostExecute(Void result) {
				//if (null != mProgress) { 
				//	mProgress.dismiss();
				//}
			}
		};
		AP2StationTask.execute();
	 }

	/*
	 * 向本地数据库中增加一个新的插座
	 */
	private boolean addNewPlug2LocalDB() {
		SmartPlugDefine plug = new SmartPlugDefine();
		plug.mUserName  = PubStatus.g_CurUserName;
		plug.mPlugName  = this.mNameView.getText().toString();
		plug.mPlugId    = SinglePlugUtil.plugChipID;
		plug.mIsOnline  = false;
		plug.mDeviceStatus = 4;
		plug.mProtocolMode = this.checkProtocolMode.isChecked() ? 1 : 2;
		plug.mMAC = mMACView.getText().toString();
		// default version & module type
		plug.mVersion = "unknown";
		plug.mModuleType = "0_0";
		mPlugProvider.addSmartPlug(plug);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(this.mAddPlugRev);
		m_pushMessageOK = 0;
	}
	
	/*
	 * 发送增加插座命令
	 */
	int connectCount = 0;
	private Runnable sendAddPlugCmd = new Runnable() {
		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();
			sb.append(SmartPlugMessage.CMD_SP_ADDPLUG)
			        .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
					.append(PubStatus.g_CurUserName)
					.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
					.append(mPlugName)
					.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
					.append(mPlugID)
					.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
					.append(mMACView.getText().toString());
			sendMsg(true, sb.toString(), true);
		}
	};
	
	/*
	 * 显示所有搜索到的路由器
	 */
	private void showRouters() {
		ActionSheetDialog dlg = new ActionSheetDialog(AddSocketActivity3.this)
		.builder()
		.setTitle(AddSocketActivity3.this.getString(R.string.smartplug_oper_selectrouter_title))
		.setCancelable(true)
		.setCanceledOnTouchOutside(true);
		
		for (int i = 0; i < this.mRouterList.size(); ++i) {
			dlg.addSheetItem(mRouterList.get(i).SSID, SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
			            	Message msg = new Message();
			            	msg.what = 0;  
			            	msg.obj = mRouterList.get(which - 1).SSID;
			            	updateRouter.sendMessage(msg);							
						}
			});
		}
		dlg.show();
	}
	
	/*
	 * 更新路由器的SSID显示
	 */
	private Handler updateRouter = new Handler() {
		public void handleMessage(Message msg) {
			mViewSSID.setText((String)msg.obj);	
		};
	};
	
	/*
	 * 显示路由器加密方式
	 */
	/*private void showRouterModeDlg() {
		ActionSheetDialog dlg = new ActionSheetDialog(AddSocketActivity3.this)
		.builder()
		.setTitle(AddSocketActivity3.this.getString(R.string.smartplug_oper_routermode_title))
		.setCancelable(true)
		.setCanceledOnTouchOutside(true);

		for (int i = 0; i < mRouterMode.size(); i++) {
			dlg.addSheetItem(mRouterMode.get(i), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							mViewRouterMode.setText(mRouterMode.get(which -  1));
							mViewRouterMode.setTag(which);
						}
			});
		}
				
		dlg.show();
	}	*/
	
   /*
    * 根据加密方式获得Tag
    */
	private int getouterModeTag(String mode) {
		for (int i = 0; i < mRouterMode.size(); i++) {
			if (mRouterMode.get(i).equals(mode)) {
				return i + 1;
			}
		}
		
		return 1;
	}
 
	/*
	 * 根据wifi的ssid获得wifi的详细信息
	 */
	private ScanResult getRouter(String ssid) {
		for (int i = 0; i < mRouterList.size(); i++) {
			PubFunc.log("SSID:" +mRouterList.get(i).SSID);
			if (mRouterList.get(i).SSID.equalsIgnoreCase(ssid)) {
				return mRouterList.get(i);
			}
		}
		return null;
	}
	
}
