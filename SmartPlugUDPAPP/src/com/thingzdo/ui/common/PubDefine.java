package com.thingzdo.ui.common;

import java.net.Socket;

import android.view.View;

public class PubDefine {
	public static final int TEN_MINUTES = 600000; // 10分钟
	public static final int MAX_ALERT_TIMES = 20; // 最大告警次数
	public static final int SOCKET_TIMEOUT = 5000; // Socket超时 5秒
	public static int CmdStatus = -2; // 0:send, 1:receive, -1:timeout,
										// -2:Unknown
	public static View CtrlView = null;
	public static final int WAIT_SER_RESPONSE = 6000; // 服务器超时时间
	public static final int WAIT_WIFI_RESPONSE = 3000; // 设备WIFI超时时间
	public static final String COMPANY_NAME = "Thingzdo";
	public final static String SERVERIP_HANGZHOU = "121.41.19.6"; // 杭州服务器IP地址
	public final static String SERVERIP_SHENZHEN = "112.126.65.122"; // 深圳服务器IP地址
	public final static String SERVERIP_DEBUG = "192.168.0.102"; // 本地调试服务器IP地址
	public static String THINGZDO_HOST_NAME = SERVERIP_HANGZHOU;

	public static boolean AUTO_RUN_SMARTPLUG = false; // android开机后自动启动
	public static boolean AUTO_RUN_SMARTPLUG_IATDEMO = false; // android开机后自动启动语音控制页面

	public static final String SOCKET_CONNECT_FAIL_BROADCAST = "com.thingzdo.socket.connect.fail";

	public static final String INTENT_SET_ENABLED = "com.thingzdo.ui.setfunctionenable";
	public static final String INTENT_SET_DISABLED = "com.thingzdo.ui.setfunctiondisable";
	public static final String LOGIN_BROADCAST = "com.thingzdo.login.broadcast";
	public static final String PLUG_POWER_ACTION = "com.thingzdo.ctrl.plugpower";
	public static final String PLUG_BACK2AP_ACTION = "com.thingzdo.ctrl.plugback2ap";
	public static final String PLUG_LIGHT_ACTION = "com.thingzdo.ctrl.pluglight";
	public static final String PLUG_USB_ACTION = "com.thingzdo.ctrl.plugusb";
	public static final String PLUG_PARENTCTRL_ACTION = "com.thingzdo.ctrl.plugparent";
	public static final String REGISTER_BROADCAST = "com.thingzdo.register.broadcast";
	public static final String LOGOUT_BROADCAST = "com.thingzdo.logout.broadcast";
	public static final String FINDPWD_BROADCAST = "com.thingzdo.findpwd.broadcast";
	public static final String PLUG_UPDATE = "com.thingzdo.smartplug.update";
	public static final String PLUG_ADD_TASK = "com.thingzdo.smartplug.plugaddone";

	public static final String PLUG_CURTAIN_ACTION = "com.thingzdo.ctrl.plugcurtain";
	public static final String PLUG_WINDOW_ACTION = "com.thingzdo.ctrl.plugwindow";
	public static final String PLUG_ALED_ACTION = "com.thingzdo.ctrl.plugaled";
	public static final String PLUG_AIRCON_ACTION = "com.thingzdo.ctrl.plugaircon";

	public static final String PLUG_BATTERY_QUERYENERGE_ACTION = "com.thingzdo.ctrl.plugbatteryqueryenerge";
	public static final String PLUG_BATTERY_QUERYTRAIL_ACTION = "com.thingzdo.ctrl.plugbatteryquerytrail";
	public static final String PLUG_BATTERY_NOTIFYENERGE_ACTION = "com.thingzdo.ctrl.plugbatterynotifyenerge";

	public static final String PLUG_QRYGONGLV = "com.thingzdo.ctrl.plugquerygonglv";
	public static final String PLUG_ENABLEENERGE = "com.thingzdo.ctrl.plugenableenerge";
	public static final String PLUG_QRYENERGE = "com.thingzdo.ctrl.plugqueryenerge";

	public static final String PLUG_AIRCON_ADDSCENE_ACTION = "com.thingzdo.ctrl.plugairconaddscene";
	public static final String PLUG_AIRCON_MODIFYSCENE_ACTION = "com.thingzdo.ctrl.plugairconmodifyscene";
	public static final String PLUG_AIRCON_DELSCENE_ACTION = "com.thingzdo.ctrl.plugaircondelscene";
	public static final String PLUG_AIRCON_ENABLESCENE_ACTION = "com.thingzdo.ctrl.plugairconenalbescene";
	public static final String PLUG_AIRCON_QUERYSCENE_ACTION = "com.thingzdo.ctrl.plugairconqueryscene";

	// 空调红外遥控
	public static final String PLUG_AIRCON_IRDATA_ACTION = "com.thingzdo.ctrl.irdata";
	public static final String PLUG_AIRCON_SERVER_ACTION = "com.thingzdo.ctrl.airconserver";
	// 电视红外遥控
	public static final String PLUG_TV_IRDATA_ACTION = "com.thingzdo.ctrl.tv.irdata";
	public static final String PLUG_TV_SERVER_ACTION = "com.thingzdo.ctrl.tvserver";

	public static final String PLUG_ADD_TIMERTASK = "com.thingzdo.smartplug.addtimertask";
	public static final String PLUG_MOD_TIMERTASK = "com.thingzdo.smartplug.modifytimertask";
	public static final String PLUG_DEL_TIMERTASK = "com.thingzdo.smartplug.deltimertask";

	public static final String SETTINGS_TIMELESS_BROADCAST = "com.thingzdo.settings.timeless";
	public static final String SETTINGS_GPRS_BROADCAST = "com.thingzdo.settings.gprs";
	public static final String SETTINGS_MAINTAIN_BROADCAST = "com.thingzdo.settings.maintain";

	public static final String PLUG_MODIFY_PLUGNAME = "com.thingzdo.smartplug.modifyname";
	public static final String PLUG_DELETE = "com.thingzdo.smartplug.delete";
	public static final String PLUG_SETTIMER_ENABLED = "com.thingzdo.smartplug.settimerenabled";

	public static final String USER_MODIFY_PASSWORD = "com.thingzdo.smartplug.modifypassword";
	public static final String USER_MODIFY_EMAIL = "com.thingzdo.smartplug.modifyemail";

	public static final String PLUG_NOTIFY_POWER = "com.thingzdo.smartplug.notifypower";
	public static final String PLUG_NOTIFY_LIGHT = "com.thingzdo.smartplug.notifylight";
	public static final String PLUG_NOTIFY_ONLINE = "com.thingzdo.smartplug.notifyonline";
	public static final String PLUG_NOTIFYTIMER = "com.thingzdo.smartplug.notifytimer";
	public static final String PLUG_RGB_ACTION = "com.thingzdo.ctrl.rgb";

	public static final String PLUG_SHAKE_FAIL_ACTION = "com.thingzdo.shakeshake.fail";

	public static final String PLUG_NOTIFY_CURTAIN = "com.thingzdo.smartplug.notifycurtain";
	public static final String PLUG_NOTIFY_KETTLE = "com.thingzdo.smartplug.notifykettle";
	public static final String PLUG_NOTIFY_UPGRADEAP = "com.thingzdo.smartplug.notifyupgradeap";

	public static final String CONFIG_MODIFY_SPEEK_TIMER = "com.thingzdo.smartplug.config.speek";

	// 设备类型
	public static final String DEVICE_UNKNOWN = "UNKNOWN"; // 0_
	public static final String DEVICE_NULL = "NULL"; // 0_
	public static final String DEVICE_SMART_PLUG = "SmartPlug"; // 1_ 智能插座
	public static final String DEVICE_SMART_PLUG_BOARD = "SmartPlug(Board)"; // 2_
																				// 智能插座（新）
	public static final String DEVICE_SMART_CURTAIN = "SmartCurtain"; // 3_ 智能窗帘
	public static final String DEVICE_SMART_WINDOW = "SmartWindow"; // 4_ 智能窗口
	public static final String DEVICE_SMART_SWITCH = "SmartSwitch"; // 5_ 智能开关
	public static final String DEVICE_SMART_KETTLE = "SmartKettle"; // 6_ 智能水壶
	public static final String DEVICE_SMART_SIMULATION_PC = "SmartSimulationPC"; // 7_
																					// 模拟PC模块软件
	public static final String DEVICE_SMART_BATTERY = "SmartBattery"; // 8_ 智能电池
	public static final String DEVICE_SMART_AIRCONSTUDY = "SmartAirConStudy"; // 9_
																				// 红外学习模块
	public static final String DEVICE_SMART_STEELYARD = "SmartSteelYard"; // 10_
																			// 智能电子秤
	public static final String DEVICE_SMART_CAR = "SmartCar"; // 11_ 智能小车

	// 产品类型
	public static final String PRODUCT_UNKNOWN = "UNKNOWN"; // _0
	public static final String PRODUCT_PLUG = "Plug"; // _1
	public static final String PRODUCT_SMART_PLUG_AIRCON = "SmartPlug_AirCon"; // _2
	public static final String PRODUCT_SMART_PLUG_ENERGE = "SmartPlug_Energe"; // _3
	public static final String PRODUCT_SMART_PLUG_TIME = "SmartPlug_Time"; // _4

	public static enum CarGuard_Message_Type {
		SMS, Package
	};

	public static enum SmartPlug_Connect_Mode {
		Internet, WiFi, Shake
	};

	public static final int TIMER_TYPE_POWER = 0;
	public static final int TIMER_TYPE_LIGHT = 1;
	public static final int TIMER_TYPE_BELL = 2;
	public static final int TIMER_TYPE_USB = 3;
	public static final int TIMER_TYPE_OPENPC = 4;
	public static final int TIMER_TYPE_CLOSEPC = 5;

	public static final int CONNECT_MODE_INTERNET = 0;
	public static final int CONNECT_MODE_WIFI = 1;
	public static SmartPlug_Connect_Mode g_Connect_Mode = SmartPlug_Connect_Mode.Internet; // 0:internet
																							// 1:wifi
																							// 2:shake

	public static boolean RELEASE_VERSION = true;

	public static Socket global_tcp_socket = null;
	public static String global_local_ip = "";

	public static int SERVER_PORT = 5000;
	public static int LOCAL_PORT = 5002;
	public static int MODULE_PORT = 5003;

	/*
	 * public static void disconnect() { if (global_socket != null) {
	 * global_socket.close(); } global_socket = null; }
	 */

	public static boolean g_First_Login = false;
	public static boolean g_First_LightOn = false;
}
