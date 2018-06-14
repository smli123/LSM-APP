package com.thingzdo.ui.control;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thingzdo.SwipeMenuListView.SwipeMenu;
import com.thingzdo.SwipeMenuListView.SwipeMenuCreator;
import com.thingzdo.SwipeMenuListView.SwipeMenuItem;
import com.thingzdo.SwipeMenuListView.SwipeMenuListView;
import com.thingzdo.SwipeMenuListView.SwipeMenuListView.OnMenuItemClickListener;
import com.thingzdo.SwipeMenuListView.SwipeMenuListView.OnSwipeListener;
import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.dataprovider.SmartPlugTimerHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.TimerDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.ui.wheelutils.ActionSheetDialog;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.OnSheetItemClickListener;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.SheetItemColor;
import com.thingzdo.ui.wheelutils.MyAlertDialog;
import com.thingzdo.ui.wifi.SmartPlugWifiMgr;

public class PlugDetailActivity extends TitledActivity
		implements
			OnClickListener,
			SeekBar.OnSeekBarChangeListener {
	private static int PROTOCOL_TCP = 1;
	private static int PROTOCOL_UDP = 2;

	private ImageView mImgAddTimer = null;
	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugTimerHelper mTimerHelper = null;
	private SmartPlugDefine mPlug = null;
	private TimerDefine mTimer = null;
	private String mPlugSSID = "";
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";
	private PlugTimerlistAdapter mAdapter;

	private LinearLayout ll_PowerCtrl;
	private LinearLayout ll_LightCtrl;
	private LinearLayout ll_UsbCtrl;
	private LinearLayout ll_LockCtrl;
	private LinearLayout ll_PCCtrl;

	private TextView mImgPowerCtrl = null;
	private boolean mIsPoweOn = false;
	private TextView mImgLightCtrl = null;
	private boolean mIsLightOn = false;
	private boolean mIsParentCtrlOn = false;
	// private TextView mImgBellCtrl = null;
	private TextView mImgUsbCtrl = null;
	private TextView mImgLockCtrl = null;
	private boolean mIsUsbOn = false;
	private TextView mImgPCCtrl = null;
	private boolean mIsPCOn = false;
	private String mErrorMsg = "";

	private RelativeLayout mRlColor = null;
	private final String LIGHT_COLOR = "LightColor";
	private final String RED_COLOR = "Red";
	private final String GREEN_COLOR = "Green";
	private final String BLUE_COLOR = "Blue";
	private TextView mTxtRed = null;
	private TextView mTxtGreen = null;
	private TextView mTxtBlue = null;

	private ViewPager pagedViewTop;
	private LinearLayout lldot;
	private ArrayList<View> mCtrlViewList = new ArrayList<View>();
	private CtrlViewPagerAdapter ctrlAdapter;

	private SwipeMenuListView mListView;

	// *****************************
	private RelativeLayout mLayoutTimer = null;
	private RelativeLayout layout_light_setting = null;
	private RelativeLayout mLayoutStatic = null;
	private RelativeLayout mLayoutStatic_power = null;
	private RelativeLayout mLayoutStatic_light = null;
	private RelativeLayout mLayoutStatic_usb = null;
	private RelativeLayout mLayoutStatic_openpc = null;
	private RelativeLayout mLayoutStatic_closepc = null;
	private TextView mTxtStatic_power = null;
	private TextView mTxtStatic_light = null;
	private TextView mTxtStatic_usb = null;
	private TextView mTxtStatic_openpc = null;
	private TextView mTxtStatic_closepc = null;

	// *****************************
	private SharedPreferences mRouter = null;
	private SharedPreferences mWifiInfo = null;
	// *****************************

	// 给Wifi直连模式使用的存储变量
	private String mStored_SSID = "";
	private String mStored_PLUGID = "";

	// lishimin TCP Socket
	private RevCmdFromSocketThread mTcpSocketThread = null;
	// private RevSocketThread mTcpSocketThread = null;

	// lishimin 保存 温度变化范围
	private String mStored_Temperature = "0";

	// 公共变量
	private SharedPreferences mSharedPreferences;
	private String smartPlugID_ToPC = "";
	private String pcID = "";

	ArrayList<String> pcIDs = null;
	private Context mContext;

	private BroadcastReceiver mPlugDetailRev = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_SETTIMER_ENABLED)) {
				timeoutHandler.removeCallbacks(timeoutProcess);
				if (null != mProgress) {
					mProgress.dismiss();
				}
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
					case 0 :
						if (null != mTimer) {
							mTimer.mEnable = mFocusTimerEnabled;
							if (0 < mTimerHelper.modifyTimer(mTimer)) {
								init();
							}
						}

						break;
					default :
						PubFunc.thinzdoToast(PlugDetailActivity.this, message);
						break;
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_DEL_TIMERTASK)) {
				timeoutHandler.removeCallbacks(timeoutProcess);
				if (null != mProgress) {
					mProgress.dismiss();
				}
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
					case 0 :
						timeoutHandler.removeCallbacks(timeoutProcess);

						if (null != mTimer) {
							if (true == mTimerHelper.deleteTimer(mTimer.mId)) {
								init();
							}
						}

						break;
					default :
						PubFunc.thinzdoToast(PlugDetailActivity.this, message);
						break;
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						PlugDetailActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(
						PlugDetailActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_CURTAIN)) {
				if (true == NotifyProcessor.curtainNotify(
						PlugDetailActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_LIGHT)) {
				if (true == NotifyProcessor.lightNotify(
						PlugDetailActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_POWER_ACTION)) {
				updateStatus(intent);
			}

			if (intent.getAction().equals(PubDefine.PLUG_LIGHT_ACTION)
					&& true == PubDefine.g_First_LightOn) {
				updateStatus(intent);
			}

			if (intent.getAction().equals(PubDefine.PLUG_USB_ACTION)) {
				updateStatus(intent);
			}

			if (intent.getAction().equals(PubDefine.PLUG_PARENTCTRL_ACTION)) {
				updateStatus(intent);
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFYTIMER)) {
				init();
			}

			if (intent.getAction().equals(PubDefine.PLUG_RGB_ACTION)) {
				if (null != mProgress) {
					mProgress.dismiss();
				}
				timeoutHandler.removeCallbacks(timeoutProcess);
				int ret = intent.getIntExtra("RESULT", 0);
				switch (ret) {
					case 0 :
						mChangeColorHandler.sendEmptyMessage(1);
						break;
					default :
						mChangeColorHandler.sendEmptyMessage(0);
						break;
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_SHAKE_FAIL_ACTION)) {
				if (null != mProgress) {
					mProgress.dismiss();
				}
			}
			if (intent.getAction().equals(PubDefine.PLUG_BACK2AP_ACTION)) {
				int code = intent.getIntExtra("RESULT", 0);
				switch (code) {
					case 0 :
						Button left_button = (Button) findViewById(R.id.titlebar_leftbutton);
						left_button.performClick();
						break;
					default :
						break;
				}

			}

		}
	};

	/*
	 * 更新控制按钮状态
	 */
	private void updateStatus(Intent intent) {
		timeoutHandler.removeCallbacks(timeoutProcess);
		if (null != mProgress) {
			mProgress.dismiss();
		}
		int code = intent.getIntExtra("RESULT", 0);
		int status = intent.getIntExtra("STATUS", 0);
		String message = intent.getStringExtra("MESSAGE");
		switch (code) {
			case 0 :
				SmartPlugDefine plug = mPlugHelper.getSmartPlug(mPlugId);
				if (null != plug) {
					plug.mDeviceStatus = status;
					if (0 < mPlugHelper.modifySmartPlug(plug)) {
						updateUI();
					}
				}

				break;
			default :
				PubFunc.thinzdoToast(PlugDetailActivity.this, message);
				break;
		}
	}

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

	private void loadData() {
		String value = null;
		String key = "电脑";
		value = mSharedPreferences.getString(key, null);
		if (value != null) {
			String[] strs = value.split(",");
			if (strs.length >= 2) {
				smartPlugID_ToPC = strs[0];
				pcID = strs[1];
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_plug_detail, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		// setTitleLeftButton(R.string.smartplug_goback,
		// R.drawable.title_btn_selector, this);

		mContext = this;

		mSharedPreferences = getSharedPreferences("DUEROS_SETTING"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);
		loadData();

		mWifiInfo = getSharedPreferences("WiFiStore", Activity.MODE_PRIVATE);
		mRouter = getSharedPreferences("Router", Activity.MODE_PRIVATE);
		// lishimin 增加 “详情”功能; UDP 功能已经调通，去掉界面中的“详情”按钮；
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
		// setTitleLeftImage(R.drawable.smp_goback, this);

		// mImgAddTimer = (ImageView)findViewById(R.id.img_timeradd);

		mRlColor = (RelativeLayout) findViewById(R.id.layout_light_color);
		mRlColor.setOnClickListener(this);

		mTxtRed = (TextView) findViewById(R.id.txtRed);
		mTxtGreen = (TextView) findViewById(R.id.txtGreen);
		mTxtBlue = (TextView) findViewById(R.id.txtBlue);

		layout_light_setting = (RelativeLayout) findViewById(R.id.layout_light_setting);
		mLayoutStatic = (RelativeLayout) findViewById(R.id.layout_timer_static);
		mLayoutStatic_power = (RelativeLayout) findViewById(R.id.layout_power_static);
		mLayoutStatic_light = (RelativeLayout) findViewById(R.id.layout_light_static);
		mLayoutStatic_usb = (RelativeLayout) findViewById(R.id.layout_usb_static);
		mLayoutStatic_openpc = (RelativeLayout) findViewById(R.id.layout_openpc_static);
		mLayoutStatic_closepc = (RelativeLayout) findViewById(R.id.layout_closepc_static);
		mTxtStatic_power = (TextView) findViewById(R.id.txt_power_static);
		mTxtStatic_light = (TextView) findViewById(R.id.txt_light_static);
		mTxtStatic_usb = (TextView) findViewById(R.id.txt_usb_static);
		mTxtStatic_openpc = (TextView) findViewById(R.id.txt_openpc_static);
		mTxtStatic_closepc = (TextView) findViewById(R.id.txt_closepc_static);
		mLayoutTimer = (RelativeLayout) findViewById(R.id.layout_timer_all);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_SETTIMER_ENABLED);
		filter.addAction(PubDefine.PLUG_DEL_TIMERTASK);
		filter.addAction(PubDefine.PLUG_POWER_ACTION);
		filter.addAction(PubDefine.PLUG_LIGHT_ACTION);
		filter.addAction(PubDefine.PLUG_USB_ACTION);
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_NOTIFY_POWER);
		filter.addAction(PubDefine.PLUG_NOTIFY_LIGHT);
		filter.addAction(PubDefine.PLUG_NOTIFYTIMER);
		filter.addAction(PubDefine.PLUG_NOTIFY_CURTAIN);
		filter.addAction(PubDefine.PLUG_RGB_ACTION);
		filter.addAction(PubDefine.PLUG_PARENTCTRL_ACTION);
		filter.addAction(PubDefine.PLUG_SHAKE_FAIL_ACTION);
		filter.addAction(PubDefine.PLUG_BACK2AP_ACTION);
		registerReceiver(mPlugDetailRev, filter);

		mPlugHelper = new SmartPlugHelper(this);
		mTimerHelper = new SmartPlugTimerHelper(this);
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";
		}

		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		mPlugIp = mPlug.mIPAddress;

		pcIDs = mPlugHelper.getAllSmartPlugPCID(mPlug.mPlugId);

		initCtrlPager();
		updateUI();
		init();

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		} else {
			// ShakeSocketMgr.createUDPSocket(new ShakeHandler(), mPlugIp);
			UDPClient.getInstance().setIPAddress(mPlugIp);
		}

		loadWiFiStore();
	}

	private void initCtrlPager() {
		pagedViewTop = (ViewPager) findViewById(R.id.pagedViewTop);

		lldot = (LinearLayout) findViewById(R.id.lldot);

		ctrlAdapter = new CtrlViewPagerAdapter();
		pagedViewTop.setAdapter(ctrlAdapter);
		addView();
		pagedViewTop.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				invalidateTopDot(arg0);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	private void addView() {
		// --------------------------------------------------------
		LayoutInflater mInflater = getLayoutInflater();
		View viewCtrl_1 = mInflater.inflate(R.layout.activity_ctrl_pwrandlight,
				null);

		ll_PowerCtrl = (LinearLayout) viewCtrl_1
				.findViewById(R.id.ll_PowerCtrl);
		ll_LightCtrl = (LinearLayout) viewCtrl_1
				.findViewById(R.id.ll_LightCtrl);
		ll_UsbCtrl = (LinearLayout) viewCtrl_1.findViewById(R.id.ll_UsbCtrl);
		ll_LockCtrl = (LinearLayout) viewCtrl_1.findViewById(R.id.ll_LockCtrl);
		ll_PCCtrl = (LinearLayout) viewCtrl_1.findViewById(R.id.ll_PCCtrl);

		mImgPowerCtrl = (TextView) viewCtrl_1.findViewById(R.id.imgPowerCtrl);
		mImgPowerCtrl.setOnClickListener(this);
		mImgLightCtrl = (TextView) viewCtrl_1.findViewById(R.id.imgLightCtrl);
		mImgLightCtrl.setOnClickListener(this);
		mCtrlViewList.add(viewCtrl_1);

		// ImageView ivDot = new ImageView(this);
		// ivDot.setImageResource(R.drawable.icon_other_active);
		// ivDot.setPadding(10, 0, 10, 0);
		// lldot.addView(ivDot);
		// ivDot = null;

		// ------------------------------------------------------
		// View viewCtrl_2 =
		// mInflater.inflate(R.layout.activity_ctrl_bellandfee, null);
		mImgLockCtrl = (TextView) viewCtrl_1.findViewById(R.id.imgLockCtrl);
		mImgLockCtrl.setOnClickListener(this);
		mImgUsbCtrl = (TextView) viewCtrl_1.findViewById(R.id.imgUsbCtrl);
		mImgUsbCtrl.setOnClickListener(this);

		mImgPCCtrl = (TextView) viewCtrl_1.findViewById(R.id.imgPCCtrl);
		mImgPCCtrl.setOnClickListener(this);

		if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_SIMULATION_PC
				&& mPlug.mSubProductType == PubDefine.PRODUCT_PLUG) { // 7_1
			ll_PowerCtrl.setVisibility(View.VISIBLE);
			ll_LightCtrl.setVisibility(View.GONE);
			ll_UsbCtrl.setVisibility(View.GONE);
			ll_LockCtrl.setVisibility(View.GONE);

			layout_light_setting.setVisibility(View.GONE);

			mLayoutStatic_power.setVisibility(View.GONE);
			mLayoutStatic_light.setVisibility(View.GONE);
			mLayoutStatic_usb.setVisibility(View.GONE);
			mLayoutStatic_openpc.setVisibility(View.GONE);
			mLayoutStatic_closepc.setVisibility(View.VISIBLE);

			Drawable drawable = getResources().getDrawable(
					R.drawable.smp_power_on_big);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			mImgPowerCtrl.setCompoundDrawables(null, drawable, null, null);
			mImgPowerCtrl.setText(this.getString(R.string.shutdownpc));
		}
		// 对于智能电灯控制器（5_1），只显示Power
		else if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_SWITCH
				&& mPlug.mSubProductType == PubDefine.PRODUCT_PLUG) { // 5_1
			ll_PowerCtrl.setVisibility(View.VISIBLE);
			ll_LightCtrl.setVisibility(View.GONE);
			ll_UsbCtrl.setVisibility(View.GONE);
			ll_LockCtrl.setVisibility(View.GONE);

			layout_light_setting.setVisibility(View.GONE);

			mLayoutStatic_light.setVisibility(View.GONE);
			mLayoutStatic_usb.setVisibility(View.GONE);
			mLayoutStatic_openpc.setVisibility(View.GONE);
			mLayoutStatic_closepc.setVisibility(View.GONE);
		} else if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG
				&& mPlug.mSubProductType == PubDefine.PRODUCT_SMART_PLUG_AIRCON) { // 1_2

			ll_PowerCtrl.setVisibility(View.VISIBLE);
			ll_LightCtrl.setVisibility(View.VISIBLE);
			ll_UsbCtrl.setVisibility(View.VISIBLE);
			ll_LockCtrl.setVisibility(View.VISIBLE);
			ll_PCCtrl.setVisibility(View.VISIBLE);

			layout_light_setting.setVisibility(View.VISIBLE);

			mLayoutStatic_light.setVisibility(View.VISIBLE);
			mLayoutStatic_usb.setVisibility(View.VISIBLE);
			mLayoutStatic_openpc.setVisibility(View.VISIBLE);
			mLayoutStatic_closepc.setVisibility(View.VISIBLE);
		} else if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG
				&& mPlug.mSubProductType == PubDefine.PRODUCT_SMART_PLUG_ENERGE) { // 1_3
																					// 功率显示

			ll_PowerCtrl.setVisibility(View.VISIBLE);
			ll_LightCtrl.setVisibility(View.VISIBLE);
			ll_UsbCtrl.setVisibility(View.VISIBLE);
			ll_LockCtrl.setVisibility(View.VISIBLE);
			ll_PCCtrl.setVisibility(View.VISIBLE);

			layout_light_setting.setVisibility(View.GONE);

			mLayoutStatic_light.setVisibility(View.VISIBLE);
			mLayoutStatic_usb.setVisibility(View.VISIBLE);
			mLayoutStatic_openpc.setVisibility(View.VISIBLE);
			mLayoutStatic_closepc.setVisibility(View.GONE);
		} else {
			ll_PowerCtrl.setVisibility(View.VISIBLE);
			ll_LightCtrl.setVisibility(View.VISIBLE);
			ll_UsbCtrl.setVisibility(View.VISIBLE);
			ll_LockCtrl.setVisibility(View.VISIBLE);
			ll_PCCtrl.setVisibility(View.VISIBLE);

			layout_light_setting.setVisibility(View.VISIBLE);

			mLayoutStatic_light.setVisibility(View.VISIBLE);
			mLayoutStatic_usb.setVisibility(View.VISIBLE);
			mLayoutStatic_openpc.setVisibility(View.GONE);
			mLayoutStatic_closepc.setVisibility(View.GONE);
		}

		// mCtrlViewList.add(viewCtrl_2);

		// ImageView ivDot2 = new ImageView(this);
		// ivDot2.setImageResource(R.drawable.icon_other_active);
		// ivDot2.setPadding(10, 0, 10, 0);
		// lldot.addView(ivDot2);
		// ivDot2 = null;
		//
		// invalidateTopDot(0);
		ctrlAdapter.notifyDataSetChanged();

	}

	private void invalidateTopDot(int currentPage) {
		for (int i = 0; i < lldot.getChildCount(); i++) {
			ImageView iv = (ImageView) lldot.getChildAt(i);
			if (i == currentPage) {
				iv.setImageResource(R.drawable.icon_other_active);
			} else {
				iv.setImageResource(R.drawable.icon_other_deactive);
			}
		}
	}

	private void updateUI() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}
		setTitle(mPlug.mPlugName);
		mIsPoweOn = (1 == (mPlug.mDeviceStatus & 1) ? true : false);
		mIsLightOn = (2 == (mPlug.mDeviceStatus & 2) ? true : false);
		mIsUsbOn = (4 == (mPlug.mDeviceStatus & 4) ? true : false);
		mIsParentCtrlOn = (8 == (mPlug.mDeviceStatus & 8) ? true : false);

		mImgPowerCtrl.setEnabled(true);
		mImgLightCtrl.setEnabled(true);
		mImgUsbCtrl.setEnabled(true);
		mImgLockCtrl.setEnabled(true);

		// 新增 对RGB显示值得刷新
		mRed = mPlug.mColor_R == 0 ? 255 : mPlug.mColor_R;
		mGreen = mPlug.mColor_G == 0 ? 255 : mPlug.mColor_G;
		mBlue = mPlug.mColor_B == 0 ? 255 : mPlug.mColor_B;
		mTxtRed.setText(String.valueOf(mRed));
		mTxtGreen.setText(String.valueOf(mGreen));
		mTxtBlue.setText(String.valueOf(mBlue));

		mStored_Temperature += "," + String.valueOf(mBlue);
		PubFunc.log(mStored_Temperature);

		Drawable drawable = null;
		drawable = getResources().getDrawable(
				mIsPoweOn
						? R.drawable.smp_power_on_big
						: R.drawable.smp_power_off_big);
		// 对于智能电灯控制器（7_1），只显示Power
		if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_SIMULATION_PC
				&& mPlug.mSubProductType == PubDefine.PRODUCT_PLUG) { // 7_1
			drawable = getResources().getDrawable(R.drawable.smp_power_on_big);
		}

		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		mImgPowerCtrl.setCompoundDrawables(null, drawable, null, null);

		drawable = getResources().getDrawable(
				mIsLightOn
						? R.drawable.smp_light_on_big
						: R.drawable.smp_light_off_big);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		mImgLightCtrl.setCompoundDrawables(null, drawable, null, null);

		if (mPlug.mSubProductType
				.equalsIgnoreCase(PubDefine.PRODUCT_SMART_PLUG_AIRCON)) { // Aircon
			drawable = getResources().getDrawable(R.drawable.smp_aircon_on_big);
			mImgUsbCtrl.setCompoundDrawablesWithIntrinsicBounds(null, drawable,
					null, null);
			mImgUsbCtrl.setText(this
					.getString(R.string.str_btn_aircon_controller));
		} else { // USB
			drawable = getResources().getDrawable(
					mIsUsbOn
							? R.drawable.smp_usb_on_big
							: R.drawable.smp_usb_off_big);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			mImgUsbCtrl.setCompoundDrawables(null, drawable, null, null);
			mImgUsbCtrl.setText(this.getString(R.string.usb));
		}

		drawable = getResources().getDrawable(
				mIsParentCtrlOn
						? R.drawable.smp_parentctrl_active_big
						: R.drawable.smp_parentctrl_deactive_big);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		mImgLockCtrl.setCompoundDrawables(null, drawable, null, null);

		// if (mIsPCOn == true) {
		mImgPCCtrl.setText(getString(R.string.smartplug_ctrl_openpc_cmd));
		drawable = getResources().getDrawable(
				R.drawable.smp_openpcctrl_active_big);
		mImgPCCtrl.setCompoundDrawablesWithIntrinsicBounds(null, drawable,
				null, null);
		// } else {
		// mImgPCCtrl.setText(getString(R.string.smartplug_ctrl_closepc_cmd));
		// drawable =
		// getResources().getDrawable(R.drawable.smp_closepcctrl_active_big);
		// mImgPCCtrl.setCompoundDrawablesWithIntrinsicBounds(null, drawable,
		// null, null);
		// }

		// // 颜色条必须满足灯打开和插座在线：
		// mRlColor.setClickable(mIsLightOn && mPlug.mIsOnline);
		// mRlColor.setBackgroundColor((mIsLightOn && mPlug.mIsOnline) ?
		// Color.WHITE : Color.LTGRAY);
		mRlColor.setClickable(mIsLightOn);
		mRlColor.setBackgroundColor(mIsLightOn ? Color.WHITE : Color.LTGRAY);

		if (PubDefine.SmartPlug_Connect_Mode.WiFi == PubDefine.g_Connect_Mode) {
			// wifi
			mImgPowerCtrl.setEnabled(true);
			mImgLightCtrl.setEnabled(true);
			mImgUsbCtrl.setEnabled(true);
			mImgLockCtrl.setEnabled(true);
			if (false == mPlug.mIsOnline) {
				mLayoutTimer.setOnClickListener(null);
			} else {
				mLayoutTimer.setOnClickListener(addTimerClick);
			}
		} else if (PubDefine.SmartPlug_Connect_Mode.Internet == PubDefine.g_Connect_Mode) {
			// internet
			if (false == mPlug.mIsOnline) {
				mLayoutTimer.setOnClickListener(null);
				mImgPowerCtrl.setEnabled(false);
				mImgLightCtrl.setEnabled(false);
				mImgUsbCtrl.setEnabled(false);
				mImgLockCtrl.setEnabled(false);
			} else {
				mLayoutTimer.setOnClickListener(addTimerClick);
				mImgPowerCtrl.setEnabled(true);
				mImgLightCtrl.setEnabled(true);
				mImgUsbCtrl.setEnabled(true);
				mImgLockCtrl.setEnabled(true);
			}
			// 临时调试使用，后续需要删除
			mImgUsbCtrl.setEnabled(true);
			mImgLightCtrl.setEnabled(true);
		} else {
			mLayoutTimer.setOnClickListener(addTimerClick);
			mImgPowerCtrl.setEnabled(true);
			mImgLightCtrl.setEnabled(true);
			mImgUsbCtrl.setEnabled(true);
			mImgLockCtrl.setEnabled(true);
		}
	}

	OnClickListener addTimerClick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if (null != mTimerHelper) {
				if (null != mTimerHelper.getAllTimer(mPlug.mPlugId)) {
					int count = mTimerHelper.getAllTimer(mPlug.mPlugId).size();
					if (10 <= count) {
						PubFunc.thinzdoToast(
								PlugDetailActivity.this,
								PlugDetailActivity.this
										.getString(R.string.smartplug_oper_timer_full));
						return;
					}
				}

			}

			showTimerTypeDlg();
		}

	};

	OnClickListener editCLick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (null != mAdapter) {
				// mAdapter.setEditable(mIsEditable);
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	private void showTimerTypeDlg() {
		ActionSheetDialog dlg = new ActionSheetDialog(PlugDetailActivity.this);
		dlg.builder();
		dlg.setCancelable(true);
		dlg.setCanceledOnTouchOutside(true);
		// 对于智能电灯控制器（7_1），只显示Power
		if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_SIMULATION_PC
				&& mPlug.mSubProductType == PubDefine.PRODUCT_PLUG) { // 7_1
			dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
					PubDefine.TIMER_TYPE_CLOSEPC), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent();
							intent.putExtra("PLUGID", mPlugId);
							intent.putExtra("PLUGIP", mPlugIp);
							intent.putExtra("TIMER_TYPE",
									PubDefine.TIMER_TYPE_CLOSEPC);
							intent.setClass(PlugDetailActivity.this,
									PlugTimerActivity.class);
							startActivity(intent);
						}
					});
		}
		// 对于智能电灯控制器（5_1），只显示Power
		else if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_SWITCH
				&& mPlug.mSubProductType == PubDefine.PRODUCT_PLUG) { // 5_1
			dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
					PubDefine.TIMER_TYPE_POWER), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent();
							intent.putExtra("PLUGID", mPlugId);
							intent.putExtra("PLUGIP", mPlugIp);
							intent.putExtra("TIMER_TYPE",
									PubDefine.TIMER_TYPE_POWER);
							intent.setClass(PlugDetailActivity.this,
									PlugTimerActivity.class);
							startActivity(intent);
						}
					});
		} else if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG
				&& mPlug.mSubProductType == PubDefine.PRODUCT_SMART_PLUG_AIRCON) { // 1_2

			dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
					PubDefine.TIMER_TYPE_POWER), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent();
							intent.putExtra("PLUGID", mPlugId);
							intent.putExtra("PLUGIP", mPlugIp);
							intent.putExtra("TIMER_TYPE",
									PubDefine.TIMER_TYPE_POWER);
							intent.setClass(PlugDetailActivity.this,
									PlugTimerActivity.class);
							startActivity(intent);
						}
					});
			dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
					PubDefine.TIMER_TYPE_LIGHT), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent();
							intent.putExtra("PLUGID", mPlugId);
							intent.putExtra("PLUGIP", mPlugIp);
							intent.putExtra("TIMER_TYPE",
									PubDefine.TIMER_TYPE_LIGHT);
							intent.setClass(PlugDetailActivity.this,
									PlugTimerActivity.class);
							startActivity(intent);
						}
					});
			dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
					PubDefine.TIMER_TYPE_USB), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent();
							intent.putExtra("PLUGID", mPlugId);
							intent.putExtra("PLUGIP", mPlugIp);
							intent.putExtra("TIMER_TYPE",
									PubDefine.TIMER_TYPE_USB);
							intent.setClass(PlugDetailActivity.this,
									PlugTimerActivity.class);
							startActivity(intent);
						}
					});

			dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
					PubDefine.TIMER_TYPE_OPENPC), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent();
							intent.putExtra("PLUGID", mPlugId);
							intent.putExtra("PLUGIP", mPlugIp);
							intent.putExtra("TIMER_TYPE",
									PubDefine.TIMER_TYPE_OPENPC);
							intent.setClass(PlugDetailActivity.this,
									PlugTimerActivity.class);
							startActivity(intent);
						}
					});
			dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
					PubDefine.TIMER_TYPE_CLOSEPC), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent();
							intent.putExtra("PLUGID", mPlugId);
							intent.putExtra("PLUGIP", mPlugIp);
							intent.putExtra("TIMER_TYPE",
									PubDefine.TIMER_TYPE_CLOSEPC);
							intent.setClass(PlugDetailActivity.this,
									PlugTimerActivity.class);
							startActivity(intent);
						}
					});

		} else if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG
				&& mPlug.mSubProductType == PubDefine.PRODUCT_SMART_PLUG_ENERGE) { // 1_3

			dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
					PubDefine.TIMER_TYPE_POWER), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent();
							intent.putExtra("PLUGID", mPlugId);
							intent.putExtra("PLUGIP", mPlugIp);
							intent.putExtra("TIMER_TYPE",
									PubDefine.TIMER_TYPE_POWER);
							intent.setClass(PlugDetailActivity.this,
									PlugTimerActivity.class);
							startActivity(intent);
						}
					});
			// 1_3 不支持LIGHT
			// dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
			// PubDefine.TIMER_TYPE_LIGHT), SheetItemColor.Blue,
			// new OnSheetItemClickListener() {
			// @Override
			// public void onClick(int which) {
			// Intent intent = new Intent();
			// intent.putExtra("PLUGID", mPlugId);
			// intent.putExtra("PLUGIP", mPlugIp);
			// intent.putExtra("TIMER_TYPE", PubDefine.TIMER_TYPE_LIGHT);
			// intent.setClass(PlugDetailActivity.this,
			// PlugTimerActivity.class);
			// startActivity(intent);
			// }
			// });
			dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
					PubDefine.TIMER_TYPE_USB), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent();
							intent.putExtra("PLUGID", mPlugId);
							intent.putExtra("PLUGIP", mPlugIp);
							intent.putExtra("TIMER_TYPE",
									PubDefine.TIMER_TYPE_USB);
							intent.setClass(PlugDetailActivity.this,
									PlugTimerActivity.class);
							startActivity(intent);
						}
					});

			dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
					PubDefine.TIMER_TYPE_OPENPC), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent();
							intent.putExtra("PLUGID", mPlugId);
							intent.putExtra("PLUGIP", mPlugIp);
							intent.putExtra("TIMER_TYPE",
									PubDefine.TIMER_TYPE_OPENPC);
							intent.setClass(PlugDetailActivity.this,
									PlugTimerActivity.class);
							startActivity(intent);
						}
					});

		} else {

			dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
					PubDefine.TIMER_TYPE_POWER), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent();
							intent.putExtra("PLUGID", mPlugId);
							intent.putExtra("PLUGIP", mPlugIp);
							intent.putExtra("TIMER_TYPE",
									PubDefine.TIMER_TYPE_POWER);
							intent.setClass(PlugDetailActivity.this,
									PlugTimerActivity.class);
							startActivity(intent);
						}
					});
			dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
					PubDefine.TIMER_TYPE_LIGHT), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent();
							intent.putExtra("PLUGID", mPlugId);
							intent.putExtra("PLUGIP", mPlugIp);
							intent.putExtra("TIMER_TYPE",
									PubDefine.TIMER_TYPE_LIGHT);
							intent.setClass(PlugDetailActivity.this,
									PlugTimerActivity.class);
							startActivity(intent);
						}
					});
			dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
					PubDefine.TIMER_TYPE_USB), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent();
							intent.putExtra("PLUGID", mPlugId);
							intent.putExtra("PLUGIP", mPlugIp);
							intent.putExtra("TIMER_TYPE",
									PubDefine.TIMER_TYPE_USB);
							intent.setClass(PlugDetailActivity.this,
									PlugTimerActivity.class);
							startActivity(intent);
						}
					});
		}

		/*
		 * dlg.addSheetItem(PubFunc.getTimerTypeLabel(PlugDetailActivity.this,
		 * PubDefine.TIMER_TYPE_BELL), SheetItemColor.Blue, new
		 * OnSheetItemClickListener() {
		 * 
		 * @Override public void onClick(int which) { Intent intent = new
		 * Intent(); intent.putExtra("PLUGID", mPlugId);
		 * intent.putExtra("TIMER_TYPE", PubDefine.TIMER_TYPE_BELL);
		 * intent.setClass(PlugDetailActivity.this, PlugTimerActivity.class);
		 * startActivity(intent); } });
		 */
		dlg.show();
	}

	private int getTaskCount(final int type, final ArrayList<TimerDefine> timers) {
		int count = 0;
		for (TimerDefine timer : timers) {
			if (type == timer.mType) {
				count++;
			}
		}
		return count;
	}

	private void initStatics(final ArrayList<TimerDefine> timers) {
		/*
		 * if (null == timers || timers.isEmpty()) {
		 * mLayoutStatic.setVisibility(View.GONE); } else {
		 * mLayoutStatic.setVisibility(View.VISIBLE); }
		 */

		int count = getTaskCount(0, timers);
		/*
		 * if (0 == count) { mLayoutStatic_power.setVisibility(View.GONE); }
		 * else {
		 */
		// mLayoutStatic_power.setVisibility(View.VISIBLE);
		mTxtStatic_power.setText(String.valueOf(count));
		// }
		count = getTaskCount(1, timers);
		/*
		 * if (0 == count) { mLayoutStatic_light.setVisibility(View.GONE); }
		 * else {
		 */
		// mLayoutStatic_light.setVisibility(View.VISIBLE);
		mTxtStatic_light.setText(String.valueOf(count));
		// }
		count = getTaskCount(3, timers);
		/*
		 * if (0 == count) { mLayoutStatic_usb.setVisibility(View.GONE); } else
		 * {
		 */
		// mLayoutStatic_usb.setVisibility(View.VISIBLE);
		mTxtStatic_usb.setText(String.valueOf(count));
		// }
		count = getTaskCount(4, timers);
		mTxtStatic_openpc.setText(String.valueOf(count));
		/*
		 * if (0 == count) { mLayoutStatic_usb.setVisibility(View.GONE); } else
		 * {
		 */
		// mLayoutStatic_usb.setVisibility(View.VISIBLE);
		mTxtStatic_openpc.setText(String.valueOf(count));
		// }
		count = getTaskCount(5, timers);
		mTxtStatic_closepc.setText(String.valueOf(count));
	}

	// private ListView mTaskList = null;
	private int mOldRed = 1;
	private int mOldGreen = 1;
	private int mOldBlue = 1;
	private int mRed = 1;
	private int mGreen = 1;
	private int mBlue = 1;
	private int mFlashMode = 0;
	private int mProtocolMode = 1;
	public void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}
		mRed = mPlug.mColor_R == 0 ? 255 : mPlug.mColor_R;
		mGreen = mPlug.mColor_G == 0 ? 255 : mPlug.mColor_G;
		mBlue = mPlug.mColor_B == 0 ? 255 : mPlug.mColor_B;
		mFlashMode = mPlug.mFlashMode;
		mOldRed = mRed;
		mOldGreen = mGreen;
		mOldBlue = mBlue;
		mProtocolMode = mPlug.mProtocolMode;

		mTxtRed.setText(String.valueOf(mRed));
		mTxtGreen.setText(String.valueOf(mGreen));
		mTxtBlue.setText(String.valueOf(mBlue));

		// 初始化 所有的定时任务，包括对应PC的定时任务（关机）
		ArrayList<TimerDefine> timers = mTimerHelper.getAllTimer(mPlug.mPlugId);
		for (int i = timers.size() - 1; i >= 0; i--) {
			if (timers.get(i).mType == PubDefine.TIMER_TYPE_OPENPC) {
				String pcID = mPlugHelper
						.getPlugIDFromMac(timers.get(i).mPowerOffTime);
				String pcName = mPlugHelper.getSmartPlugMacShowName(pcID);
				timers.get(i).mPowerOffTime = "[" + pcName + "] "
						+ timers.get(i).mPowerOffTime;
			}
			if (timers.get(i).mPeriod.isEmpty()
					|| timers.get(i).mPowerOnTime.isEmpty()
					|| timers.get(i).mPowerOffTime.isEmpty()) {
				timers.remove(i);
			}
		}
		// 对应PC的定时任务
		for (int i = 0; i < pcIDs.size(); i++) {
			String pcID = pcIDs.get(i);
			ArrayList<TimerDefine> tempTimers = mTimerHelper.getAllTimer(pcID);
			String pcName = mPlugHelper.getSmartPlugMacShowName(pcID);
			for (int j = 0; j < tempTimers.size(); j++) {
				tempTimers.get(j).mPCName = "[" + pcName + "]";
				timers.add(tempTimers.get(j));
			}
		}

		mAdapter = new PlugTimerlistAdapter(this, mPlugId, mPlugIp, timers,
				mTimerHandler, mPlug.mIsOnline);
		// mAdapter.setEditable(mIsEditable);
		initStatics(timers);

		mListView = (SwipeMenuListView) findViewById(R.id.timer_list);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos,
					long arg3) {
				Intent intent = new Intent(PlugDetailActivity.this,
						PlugTimerActivity.class);
				// intent.putExtra("PLUGID", mPlugId);
				intent.putExtra("PLUGIP", mPlugIp);
				ImageView imgType = (ImageView) v
						.findViewById(R.id.plug_img_type);
				ImageView imgEnable = (ImageView) v
						.findViewById(R.id.plug_img_enabled);
				RelativeLayout layout_Whole = (RelativeLayout) v
						.findViewById(R.id.lay_plug_whole);
				TextView txtDay = (TextView) v
						.findViewById(R.id.txtTimerPeriod);
				intent.putExtra("PLUGID", txtDay.getContentDescription()
						.toString());
				intent.putExtra("TIMER_TYPE", Integer.parseInt(imgType
						.getContentDescription().toString()));
				intent.putExtra("TIMERID", layout_Whole.getContentDescription()
						.toString());
				boolean isActive = true; // Integer.parseInt(imgEnable.getContentDescription().toString())
											// == 1 ? true : false;
				intent.putExtra("ACTIVE", isActive);
				PlugDetailActivity.this.startActivity(intent);

			}

		});

		mListView.setAdapter(mAdapter);

		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				WindowManager wm = (WindowManager) PlugDetailActivity.this
						.getSystemService(Context.WINDOW_SERVICE);

				int width = wm.getDefaultDisplay().getWidth();
				deleteItem.setWidth(width / 5/* (180) */);
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);

			}
		};
		// set creator
		mListView.setMenuCreator(creator);

		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
					case 0 :
						// delete timer
						TimerDefine timer = (TimerDefine) mListView
								.getItemAtPosition(position);
						Message msg = new Message();
						msg.what = 2;
						msg.obj = timer.mPlugId + " "
								+ String.valueOf(timer.mTimerId);
						mTimerHandler.sendMessage(msg);
						// deleteTimer();
						// mAdapter.notifyDataSetChanged();
						break;
					default :
						break;
				}
			}
		});

		// set SwipeListener
		mListView.setOnSwipeListener(new OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});
	}

	private void disconnectSocket() {
		// WiFi 直连模式下，退出或者重选时，必须close TCP连接；
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			SmartPlugWifiMgr.disconnectSocket();
		}

		return;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Button back_button = (Button) findViewById(R.id.titlebar_leftbutton);
			back_button.performClick();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (view.getId()) {
			case R.id.titlebar_leftbutton : // WiFi模式 退出时，需要close掉 TCP连接
				disconnectSocket();

				if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
					intent = new Intent();
					intent.setClass(PlugDetailActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}
				finish();

				// // 退出整个APP程序
				// if (PubDefine.g_Connect_Mode ==
				// PubDefine.SmartPlug_Connect_Mode.WiFi) {
				// SmartPlugApplication.getInstance().exit();
				// }
				break;
			case R.id.titlebar_rightbutton :
				// 配置界面
				Button btn_TitleRight = (Button) findViewById(R.id.titlebar_rightbutton);
				if (btn_TitleRight.getText().equals(
						getString(R.string.smartplug_title_settings))) { // Internet场景中，按钮功能为：配置
					// lishimin 增加 “配置”功能
					String str_plug_mac = mRouter.getString(mPlugId + "_MAC",
							"");

					// 初始化 配置对话框
					View config_view = View.inflate(PlugDetailActivity.this,
							R.layout.activity_plug_detail_config, null);
					EditText plug_mac = (EditText) config_view
							.findViewById(R.id.plug_mac);
					plug_mac.setText(str_plug_mac);
					RadioGroup rg_tcp_udp = (RadioGroup) config_view
							.findViewById(R.id.rg_tcp_udp);
					final RadioButton rb_tcp = (RadioButton) config_view
							.findViewById(R.id.rb_tcp);
					final RadioButton rb_udp = (RadioButton) config_view
							.findViewById(R.id.rb_udp);

					int rb_protocal_tcp = mProtocolMode;
					rb_tcp.setTextColor(Color.BLACK);
					rb_udp.setTextColor(Color.BLACK);
					if (rb_protocal_tcp == PROTOCOL_TCP)
						rb_tcp.setChecked(true);
					else if (rb_protocal_tcp == PROTOCOL_UDP)
						rb_udp.setChecked(true);
					else { // mProtocolMode 在设备还没有上报任何信息的时候，回出现0；
						rb_tcp.setChecked(false);
						rb_udp.setChecked(false);
					}

					new MyAlertDialog(PlugDetailActivity.this)
							.builder()
							.setView(config_view)
							.setTitle(
									PlugDetailActivity.this
											.getString(R.string.smartplug_title_settings))
							.setPositiveButton(
									PlugDetailActivity.this
											.getString(android.R.string.ok),
									new OnClickListener() {
										@Override
										public void onClick(View arg0) {
											// 若TCP/UDP的值有变化，需要Set到设备上；
											int new_protocol_mode = rb_tcp
													.isChecked()
													? PROTOCOL_TCP
													: PROTOCOL_UDP;
											if (new_protocol_mode != mProtocolMode)
												protocolPlug(mPlugId,
														new_protocol_mode);
											// dismiss();
										}
									})
							.setNegativeButton(
									PlugDetailActivity.this
											.getString(android.R.string.cancel),
									new OnClickListener() {
										@Override
										public void onClick(View arg0) {
											// dialog.dismiss();
										}
									}).show();

					/*
					 * AlertDialog.Builder builder = new
					 * AlertDialog.Builder(PlugDetailActivity.this);
					 * builder.setView(config_view);
					 * builder.setTitle(R.string.smartplug_title_settings);
					 * builder.setPositiveButton(android.R.string.ok, new
					 * DialogInterface.OnClickListener() { public void
					 * onClick(DialogInterface dialog, int which) { //
					 * 若TCP/UDP的值有变化，需要Set到设备上； int new_protocol_mode =
					 * rb_tcp.isChecked() ? PROTOCOL_TCP : PROTOCOL_UDP;
					 * 
					 * if (new_protocol_mode != mProtocolMode)
					 * protocolPlug(mPlugId, new_protocol_mode);
					 * 
					 * dialog.dismiss(); } });
					 * 
					 * builder.setNegativeButton(android.R.string.cancel, new
					 * DialogInterface.OnClickListener() { public void
					 * onClick(DialogInterface dialog, int which) {
					 * dialog.dismiss(); } }); builder.create().show();
					 */

				} else if (btn_TitleRight.getText().equals(
						getString(R.string.smartplug_title_plug_detail))) {
					intent = new Intent();
					intent.putExtra("PLUGID", mPlugId);
					intent.putExtra("ONLINE", mPlug.mIsOnline);
					intent.setClass(PlugDetailActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);

				} else { // WiFi直连场景中，按钮功能为：重选
					saveWiFiStore();
					// PubDefine.disconnect();
					// disconnectSocket();
					intent = new Intent();
					intent.putExtra("SOURCE", "PlugDetailActivity");
					intent.putExtra("PLUG_SSID", mPlugSSID);
					intent.putExtra("PLUGID", mPlugId);
					intent.setClass(PlugDetailActivity.this,
							AddSocketActivity2.class);
					startActivity(intent);
					finish();
				}
				break;
			case R.id.imgPowerCtrl :
				// TestCommand(mPlugId);
				powerPlug(mPlugId, !mIsPoweOn);
				break;
			case R.id.imgLightCtrl :
				PubDefine.g_First_LightOn = true;
				if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG
						&& mPlug.mSubProductType == PubDefine.PRODUCT_SMART_PLUG_ENERGE) { // 1_3
																							// 功率显示
					intent = new Intent();
					intent.putExtra("SOURCE", "PlugDetailActivity");
					intent.putExtra("PLUG_SSID", mPlugSSID);
					intent.putExtra("PLUGID", mPlugId);
					intent.setClass(PlugDetailActivity.this,
							EnergeActivity.class);
					startActivity(intent);
				} else {
					lightPlug(mPlugId, !mIsLightOn);
				}
				break;
			/*
			 * case R.id.imgBellCtrl: bellPlug(mPlugId); break;
			 */
			case R.id.imgLockCtrl :
				lockPlug(mPlugId, !mIsParentCtrlOn);
				break;
			case R.id.imgUsbCtrl :
				if (mPlug.mSubProductType
						.equalsIgnoreCase(PubDefine.PRODUCT_SMART_PLUG_AIRCON)) {
					onIRclick();

				} else {
					usbPlug(mPlugId, !mIsUsbOn);
				}
				break;
			case R.id.layout_light_color :
				showTANC("");
				break;
			case R.id.imgPCCtrl :
				if (pcPlug(mPlugId, !mIsPCOn) == true) {
					mIsPCOn = !mIsPCOn;
					// if (mIsPCOn == true) {
					// mImgPCCtrl.setText(getString(R.string.smartplug_ctrl_openpc_cmd));
					// Drawable drawable =
					// getResources().getDrawable(R.drawable.smp_openpcctrl_active_big);
					// mImgPCCtrl.setCompoundDrawablesWithIntrinsicBounds(null,
					// drawable, null, null);
					// } else {
					// mImgPCCtrl.setText(getString(R.string.smartplug_ctrl_closepc_cmd));
					// Drawable drawable =
					// getResources().getDrawable(R.drawable.smp_closepcctrl_active_big);
					// mImgPCCtrl.setCompoundDrawablesWithIntrinsicBounds(null,
					// drawable, null, null);
					// }

					// 根据hexiaoxu的需求，改成新的操作方式 lishiimin
					intent = new Intent();
					intent.putExtra("PLUGID", mPlugId);
					intent.putExtra("PLUGIP", mPlugIp);
					intent.setClass(PlugDetailActivity.this,
							DetailPCCtrlActivity.class);
					startActivity(intent);

				}
				break;
		}
	}
	private void onIRclick() {
		new ActionSheetDialog(mContext)
				.builder()
				.setCancelable(true)
				.setCanceledOnTouchOutside(true)
				.addSheetItem("空调", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								Intent intent = new Intent();
								intent.putExtra("PLUGID", mPlugId);
								intent.putExtra("PLUGIP", mPlugIp);
								intent.setClass(PlugDetailActivity.this,
										DetailAirCon2Activity.class);
								startActivity(intent);
							}
						})
				.addSheetItem("电视", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								Intent intent = new Intent();
								intent.putExtra("PLUGID", mPlugId);
								intent.putExtra("PLUGIP", mPlugIp);
								intent.setClass(PlugDetailActivity.this,
										DetailTVActivity.class);
								startActivity(intent);
							}
						}).show();
		return;
	}

	private SeekBar mRedBar = null;
	private SeekBar mGreenBar = null;
	private SeekBar mBlueBar = null;
	// private SeekBar mBrightBar = null;
	private void showTANC(String header) {
		final Dialog dialog = new Dialog(this, R.style.AlertDialogStyle);
		dialog.setContentView(R.layout.activity_nightlight_color_setting);
		dialog.setTitle(header);
		dialog.setCancelable(true);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

		mRedBar = (SeekBar) dialog.findViewById(R.id.seekBarRed);
		mGreenBar = (SeekBar) dialog.findViewById(R.id.seekBarGreen);
		mBlueBar = (SeekBar) dialog.findViewById(R.id.seekBarBlue);
		// mBrightBar = (SeekBar)dialog.findViewById(R.id.seekBarBright);

		if (null != mRedBar) {
			mRedBar.setProgress(mRed);
			mRedBar.setOnSeekBarChangeListener(this);
		}
		if (null != mGreenBar) {
			mGreenBar.setProgress(mGreen);
			mGreenBar.setOnSeekBarChangeListener(this);
		}
		if (null != mBlueBar) {
			mBlueBar.setProgress(mBlue);
			mBlueBar.setOnSeekBarChangeListener(this);
		}
		/*
		 * if (null != mBrightBar) { int brightness = (int) (0.6 * mRed + 0.3 *
		 * mGreen + 0.1 * mBlue); mBrightBar.setProgress(brightness);
		 * mBrightBar.setOnSeekBarChangeListener(this); }
		 */

		btnCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				dialog.cancel();
			}
		});
		dialog.show();
	}

	// lishimin
	/*
	 * 功能：配置模块的通信协议模式 参数： protocolmode： 1，TCP； 2，UDP
	 */
	private void protocolPlug(String plugId, int protocolmode) {
		mErrorMsg = getString(R.string.smartplug_ctrl_protocol_fail);
		mProgress = PubFunc.createProgressDialog(PlugDetailActivity.this,
				PlugDetailActivity.this
						.getString(R.string.smartplug_ctrl_config_protocol),
				false);
		mProgress.show();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_PROTOCOL)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(protocolmode == PROTOCOL_TCP ? "1" : "2");

		/*
		 * if (SmartPlug_Connect_Mode.Shake == PubDefine.g_Connect_Mode) {
		 * ShakeSocketMgr.sendCommandRequestWithUDPSocket(sb.toString());
		 * return; }
		 */

		// RevCmdFromSocketThread.getInstance().setRunning(true);
		sendMsg(true, sb.toString(), true);
	}

	private void powerPlug(String plugId, boolean power) {
		mErrorMsg = power
				? getString(R.string.smartplug_ctrl_poweron_fail)
				: getString(R.string.smartplug_ctrl_poweroff_fail);
		mProgress = PubFunc.createProgressDialog(
				PlugDetailActivity.this,
				(power
						? PlugDetailActivity.this
								.getString(R.string.smartplug_ctrl_poweron)
						: PlugDetailActivity.this
								.getString(R.string.smartplug_ctrl_poweroff)),
				false);
		mProgress.show();

		String str_power = power ? "1" : "0";
		if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_SIMULATION_PC
				&& mPlug.mSubProductType == PubDefine.PRODUCT_PLUG) { // 7_1
			str_power = "0";
		}

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_POWER)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(str_power);

		/*
		 * if (SmartPlug_Connect_Mode.Shake == PubDefine.g_Connect_Mode) {
		 * ShakeSocketMgr.sendCommandRequestWithUDPSocket(sb.toString());
		 * return; }
		 */

		// RevCmdFromSocketThread.getInstance().setRunning(true);
		sendMsg(true, sb.toString(), true);
	}

	private void lightPlug(String plugId, boolean light) {
		mErrorMsg = light
				? getString(R.string.smartplug_ctrl_lighton_fail)
				: getString(R.string.smartplug_ctrl_lightoff_fail);
		mProgress = PubFunc.createProgressDialog(
				PlugDetailActivity.this,
				(light
						? PlugDetailActivity.this
								.getString(R.string.smartplug_ctrl_lighton)
						: PlugDetailActivity.this
								.getString(R.string.smartplug_ctrl_lightoff)),
				false);
		mProgress.show();

		int lighton = light ? 1 : 0;
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_LIGHT)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(lighton);

		/*
		 * if (SmartPlug_Connect_Mode.Shake == PubDefine.g_Connect_Mode) {
		 * ShakeSocketMgr.sendCommandRequestWithUDPSocket(sb.toString());
		 * return; }
		 */

		// RevCmdFromSocketThread.getInstance().setRunning(true);
		sendMsg(true, sb.toString(), true);
	}

	private void sound() {
		ToneGenerator mToneGenerator = new ToneGenerator(
				AudioManager.STREAM_NOTIFICATION, 100);
		if (null != mToneGenerator) {
			mToneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 100);
			SystemClock.sleep(150);
			mToneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 100);
			SystemClock.sleep(150);
			mToneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 100);
		}
	}

	private void bellPlug(String plugId) {
		sound();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_BELL)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId);
		/*
		 * if (SmartPlug_Connect_Mode.Shake == PubDefine.g_Connect_Mode) {
		 * ShakeSocketMgr.sendCommandRequestWithUDPSocket(sb.toString());
		 * return; }
		 */

		// RevCmdFromSocketThread.getInstance().setRunning(true);
		sendMsg(true, sb.toString(), false);
	}

	/*
	 * 设置家长锁
	 */
	private void lockPlug(String plugId, boolean locked) {
		mErrorMsg = locked
				? getString(R.string.smartplug_ctrl_lock_on)
				: getString(R.string.smartplug_ctrl_lock_off);
		mProgress = PubFunc.createProgressDialog(
				PlugDetailActivity.this,
				(locked
						? PlugDetailActivity.this
								.getString(R.string.smartplug_ctrl_lock_on)
						: PlugDetailActivity.this
								.getString(R.string.smartplug_ctrl_lock_off)),
				false);
		mProgress.show();

		int lock = locked ? 1 : 0;
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_PARENTCTRL)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(lock);

		/*
		 * if (SmartPlug_Connect_Mode.Shake == PubDefine.g_Connect_Mode) {
		 * ShakeSocketMgr.sendCommandRequestWithUDPSocket(sb.toString());
		 * return; }
		 */

		// RevCmdFromSocketThread.getInstance().setRunning(true);
		sendMsg(true, sb.toString(), true);
	}

	/*
	 * 閿熸枻鎷烽敓鏂ゆ嫹USB
	 */
	private void usbPlug(String plugId, boolean usb) {
		mErrorMsg = usb
				? getString(R.string.smartplug_ctrl_usbon_fail)
				: getString(R.string.smartplug_ctrl_usboff_fail);
		mProgress = PubFunc.createProgressDialog(
				PlugDetailActivity.this,
				(usb
						? PlugDetailActivity.this
								.getString(R.string.smartplug_ctrl_usbon)
						: PlugDetailActivity.this
								.getString(R.string.smartplug_ctrl_usboff)),
				false);
		mProgress.show();

		int lighton = usb ? 1 : 0;
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_USB)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(lighton);

		/*
		 * if (SmartPlug_Connect_Mode.Shake == PubDefine.g_Connect_Mode) {
		 * ShakeSocketMgr.sendCommandRequestWithUDPSocket(sb.toString());
		 * return; }
		 */

		// RevCmdFromSocketThread.getInstance().setRunning(true);
		sendMsg(true, sb.toString(), true);
	}

	private boolean pcPlug(String plugId, boolean usb) {
		// if (smartPlugID_ToPC.equals(mPlugId) == true) {
		// mErrorMsg = usb ? getString(R.string.smartplug_ctrl_pcon_fail) :
		// getString(R.string.smartplug_ctrl_pcoff_fail);
		// mProgress = PubFunc.createProgressDialog(PlugDetailActivity.this,
		// (usb ?
		// PlugDetailActivity.this.getString(R.string.smartplug_ctrl_pcon)
		// : PlugDetailActivity.this.getString(R.string.smartplug_ctrl_pcoff)),
		// false);
		// mProgress.show();

		StringBuffer sb = new StringBuffer();
		SmartPlugDefine pc = mPlugHelper.getSmartPlug(pcID);
		if (pc != null) {
			if (usb == true) {
				sb.append("APPPASSTHROUGH")
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append(PubStatus.getUserName())
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append(mPlugId)
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append("50")
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append("0")
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append("MAGICPACKET")
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append(PubStatus.getUserName())
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append(mPlugId)
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append(pc.mMAC)
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append("1");
			} else {
				sb.append(SmartPlugMessage.CMD_SP_POWER)
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append(PubStatus.getUserName())
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append(pcID)
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append("0");
			}

			sendMsg(true, sb.toString(), true);
		}
		return true;
		// } else {
		// PubFunc.thinzdoToast(PlugDetailActivity.this, "未配置电脑参数，请在智能音响中配置");
		// return false;
		// }
	}

	private void TestCommand(String plugId) {
		// 0,AIRCONSERVER,test,12345678,格力,021#
		StringBuffer sb = new StringBuffer();
		sb.append("APPADDSCENE,smli123hz," + plugId
				+ ",104,1,0,0,0,26,11:33,1111111,海尔,1");

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
			sendMsg(true, sb.toString(), true);
		} else { // 非Internet模式下，不处理；
			// do nothing.
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		saveWiFiStore();
		unregisterReceiver(mPlugDetailRev);
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread.setRunning(false);
		}
	}

	private int mFocusTimerId = 0;
	private boolean mFocusTimerEnabled = true;
	private Handler mTimerHandler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;
			// mFocusTimerId = msg.arg1;
			String temp = (String) msg.obj;
			int location = temp.indexOf(" ");
			if (location != -1) {
				String plugID = temp.substring(0, location);
				mFocusTimerId = Integer.parseInt(temp.substring(location + 1));
				mTimer = mTimerHelper.getTimer(plugID, mFocusTimerId);
			}

			// mTimer = mTimerHelper.getTimer(mPlugId, mFocusTimerId);
			if (null == mTimer) {
				return;
			}

			if (1 == what) {
				// enabled
				mFocusTimerEnabled = !mTimer.mEnable;
				enabledTimer(mFocusTimerEnabled);

			} else {
				// delete
				deleteTimer();
			}
		};
	};

	private void enabledTimer(boolean enabled) {

		mProgress = PubFunc
				.createProgressDialog(
						PlugDetailActivity.this,
						(enabled
								? PlugDetailActivity.this
										.getString(R.string.active)
								: PlugDetailActivity.this
										.getString(R.string.deactive)), false);
		mProgress.show();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_TIMERENABLED)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mTimerId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(enabled ? "1" : "0");

		/*
		 * if (SmartPlug_Connect_Mode.Shake == PubDefine.g_Connect_Mode) {
		 * ShakeSocketMgr.sendCommandRequestWithUDPSocket(sb.toString());
		 * return; }
		 */

		// RevCmdFromSocketThread.getInstance().setRunning(true);
		sendMsg(true, sb.toString(), true);
	}

	private void deleteTimer() {
		mProgress = PubFunc.createProgressDialog(PlugDetailActivity.this,
				PlugDetailActivity.this.getString(R.string.deleting), false);
		mProgress.show();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_DELTIMER)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mTimerId);

		/*
		 * if (SmartPlug_Connect_Mode.Shake == PubDefine.g_Connect_Mode) {
		 * ShakeSocketMgr.sendCommandRequestWithUDPSocket(sb.toString());
		 * return; }
		 */

		// RevCmdFromSocketThread.getInstance().setRunning(true);
		sendMsg(true, sb.toString(), true);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();
		// mIsEditable = false;
		init();
	}

	public class CtrlViewPagerAdapter extends PagerAdapter {

		public CtrlViewPagerAdapter() {
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mCtrlViewList.get(position));// 鍒犻敓鏂ゆ嫹椤甸敓鏂ゆ嫹
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) { // 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓缁炵鎷烽敓鎻鎷烽敓锟�
			container.addView(mCtrlViewList.get(position), 0);// 閿熸枻鎷烽敓鎻鎷烽敓锟�
			return mCtrlViewList.get(position);
		}

		@Override
		public int getCount() {
			return mCtrlViewList.size();// 閿熸枻鎷烽敓鏂ゆ嫹椤甸敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;// 閿熷姭鍑ゆ嫹閿熸枻鎷风ず閿熸枻鎷烽敓鏂ゆ嫹鍐�
		}
	}

	int mBrightness = 1;
	@Override
	public void onProgressChanged(SeekBar bar, int pos, boolean arg2) {
		if (0 == bar.getProgress()) {
			bar.setProgress(1);
		}

		switch (bar.getId()) {
			case R.id.seekBarRed :
				// mTxtRed.setText(String.valueOf(bar.getProgress()));
				// break;
			case R.id.seekBarGreen :
				// mTxtGreen.setText(String.valueOf(bar.getProgress()));
				// break;
			case R.id.seekBarBlue :
				// mTxtBlue.setText(String.valueOf(bar.getProgress()));
				mBrightness = (int) (0.6 * mRedBar.getProgress() + 0.3
						* mGreenBar.getProgress() + 0.1 * mBlueBar
						.getProgress());
				if (0 == mBrightness) {
					mBrightness = 1;
				}
				// mBrightBar.setProgress(mBrightness);
				break;
			/*
			 * case R.id.seekBarBright: int brightness =
			 * mBrightBar.getProgress(); int offset = brightness - mBrightness;
			 * 
			 * if (offset > 0) { int rightDelta = 255 - getMaxColor(); if
			 * (offset > rightDelta) { offset = rightDelta; } } else { int
			 * leftDelta = 1 - getMinColor(); if (offset < leftDelta) { offset =
			 * leftDelta; } }
			 * 
			 * mRed = (int)(mRedBar.getProgress() + offset);
			 * mRedBar.setProgress(mRed);
			 * 
			 * mGreen = (int)(mGreenBar.getProgress() + offset);
			 * mGreenBar.setProgress(mGreen);
			 * 
			 * mBlue = (int)(mBlueBar.getProgress() + offset);
			 * mBlueBar.setProgress(mBlue); break;
			 */
			default :
				break;
		}

		// mBrightBar.setProgress(brightness);
	}

	private int getMaxColor() {
		int max = java.lang.Math.max(mGreen, mBlue);
		if (mRed > max)
			max = mRed;
		return max;
	}

	private int getMinColor() {
		int min = java.lang.Math.min(mGreen, mBlue);
		if (mRed < min)
			min = mRed;
		return min;
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar bar) {
		if (true == mIsLightOn) {

			PubDefine.g_First_LightOn = false;
			switch (bar.getId()) {
				case R.id.seekBarRed :
					mRed = bar.getProgress();
					break;
				case R.id.seekBarGreen :
					mGreen = bar.getProgress();
					break;
				case R.id.seekBarBlue :
					mBlue = bar.getProgress();
					break;
				/*
				 * case R.id.seekBarBright: mBrightness = (int) (0.6 *
				 * mRedBar.getProgress() + 0.3 * mGreenBar.getProgress() + 0.1 *
				 * mBlueBar.getProgress()); if (0 == mBrightness) { mBrightness
				 * = 1; } mBrightBar.setProgress(mBrightness); break;
				 */
				default :
					break;
			}
			changeLightColor(mRed, mGreen, mBlue);
		}
	}

	private Handler mChangeColorHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				saveColorSettings();
				mOldRed = mRed;
				mOldGreen = mGreen;
				mOldBlue = mBlue;
			}

			restoreColorSettings();
		};
	};

	private void restoreColorSettings() {
		mTxtRed.setText(String.valueOf(mOldRed));
		mTxtGreen.setText(String.valueOf(mOldGreen));
		mTxtBlue.setText(String.valueOf(mOldBlue));
		if (null == mRedBar) {
			mRedBar.setProgress(mOldRed);
		}
		if (null == mGreenBar) {
			mGreenBar.setProgress(mOldGreen);
		}
		if (null == mBlueBar) {
			mBlueBar.setProgress(mOldBlue);
		}

	}

	private void saveColorSettings() {
		AsyncTask<Void, Void, Void> save = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... arg0) {

				mPlugHelper.modifySmartPlugColor(mPlugId, mFlashMode, mRed,
						mGreen, mBlue);
				return null;
			}

		};
		save.execute();
	}

	private void changeLightColor(int r, int g, int b) {
		if (null == mPlugId || mPlugId.equals("0") || mPlugId.isEmpty()) {
			return;
		}

		if (mOldRed == mRed && mOldGreen == mGreen && mOldBlue == mBlue) {
			return;
		}

		if (null != mProgress) {
			mProgress = null;
		}
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
			mProgress = PubFunc.createProgressDialog(PlugDetailActivity.this,
					PlugDetailActivity.this
							.getString(R.string.smartplug_ctrl_adjustcolor),
					false);
			mProgress.show();
		}
		// 初始化保存的值；
		mStored_Temperature = "0";

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_RGB)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append("0")
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(r))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(g))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(b));

		/*
		 * if (SmartPlug_Connect_Mode.Shake == PubDefine.g_Connect_Mode) {
		 * ShakeSocketMgr.sendCommandRequestWithUDPSocket(sb.toString());
		 * return; }
		 */

		// RevCmdFromSocketThread.getInstance().setRunning(true);
		sendMsg(true,
				sb.toString(),
				PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet
						? true
						: false);
	}

}
