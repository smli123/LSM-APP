package com.thingzdo.processhandler;

import java.util.HashMap;
import java.util.Map;

import com.thingzdo.ui.common.PubDefine;

public class SmartPlugMessage {
	// 只跟服务器有交互的命令
	public static final String CMD_SP_REGISTER = "APPREG";
	public static final String CMD_SP_MODPWD = "APPMODPWD";
	public static final String CMD_SP_MODEMAIL = "APPMODEMAIL";
	public static final String CMD_SP_FINDPWD = "APPFINDPWD";
	public static final String CMD_SP_LOGIN_SERVER = "APPLOGIN";
	public static final String CMD_SP_LOGIN_MODULE = "LOGIN"; // 登录模块的命令，用于直连方式登录
	public static final String CMD_SP_LOGINOUT = "APPLOGOUT";
	public static final String CMD_SP_ADDPLUG = "APPADDPLUG";
	public static final String CMD_SP_DELPLUG = "APPDELPLUG";
	public static final String CMD_SP_MODYPLUG = "APPMODPLUG";
	public static final String CMD_SP_QRYPLUG = "APPQRYPLUG";

	public static final String CMD_SP_APP_QRYGONGLV = "APPQRYGONGLV";
	public static final String CMD_SP_APP_ENABLEENERGE = "APPENABLEENERGE";
	public static final String CMD_SP_APP_QRYENERGE = "APPQRYENERGE";
	public static final String CMD_SP_QRYGONGLV = "QRYGONGLV";
	public static final String CMD_SP_ENABLEENERGE = "ENABLEENERGE";
	public static final String CMD_SP_QRYENERGE = "QRYENERGE";

	// public static final String CMD_SP_REGISTER = "REG";
	// public static final String CMD_SP_MODPWD = "MODPWD";
	// public static final String CMD_SP_MODEMAIL = "MODEMAIL";
	// public static final String CMD_SP_FINDPWD = "FINDPWD";
	// public static final String CMD_SP_LOGIN_SERVER = "APPLOGIN";
	// public static final String CMD_SP_LOGIN_MODULE = "LOGIN";
	// //登录模块的命令，用于直连方式登录
	// public static final String CMD_SP_LOGINOUT = "LOGOUT";
	// public static final String CMD_SP_ADDPLUG = "ADDPLUG";
	// public static final String CMD_SP_DELPLUG = "DELPLUG";
	// public static final String CMD_SP_MODYPLUG = "MODPLUG";
	// public static final String CMD_SP_QRYPLUG = "QRYPLUG";

	// 跟模块有交互的命令
	public static final String CMD_SP_ADDTIMER = "APPADDTIMER";
	public static final String CMD_SP_MODTIMER = "APPMODTIMER";
	public static final String CMD_SP_DELTIMER = "APPDELTIMER";
	public static final String CMD_SP_TIMERENABLED = "APPSETTIMERENABLE";
	public static final String CMD_SP_POWER = "APPPOWER";
	public static final String CMD_SP_LIGHT = "APPLIGHT";
	public static final String CMD_SP_RGB = "APPRGB";
	public static final String CMD_SP_BELL = "APPBELL";
	public static final String CMD_SP_PARENTCTRL = "APPPARENTCTRL";
	public static final String CMD_SP_USB = "APPUSB";
	public static final String CMD_SP_PROTOCOL = "APPTCPUDP";
	public static final String CMD_SP_BACK2AP = "APPBACK2AP";

	public static final String CMD_SP_CURTAIN = "APPCURTAIN_ACTION";
	public static final String CMD_SP_WINDOW = "APPWINDOW_ACTION";
	public static final String CMD_SP_ALED = "APPALED";
	public static final String CMD_SP_AIRCON = "APPAIRCON";
	public final static String CMD_QRY_IRDATA = "APPQRYIRDATA"; // 获取所有红外遥控器的数据
	public static final String CMD_SP_AIRCONSERVER = "APPAIRCONSERVER"; // 控制遥控器的红外命令

	// 电视红外遥控命令
	public final static String CMD_QRY_TVIRDATA = "APPQRYTVIRDATA"; // 获取所有电视红外遥控器的数据
	public static final String CMD_SP_TVSERVER = "APPTVSERVER"; // 电视控制遥控器的红外命令

	// 智能电池
	public static final String CMD_SP_BATTERY_QUERYENERGE = "APPQRYBATTERYENERGE";
	public static final String CMD_SP_BATTERY_QUERYTRAIL = "APPQRYBATTERYLOCATON";
	public static final String CMD_SP_BATTERY_NOTIFYENERGE = "NOTIFYBATTERYENERGY";

	// 红外场景命令字
	public static final String CMD_SP_ADDSCENE = "APPADDSCENE";
	public static final String CMD_SP_MODIFYSCENE = "APPMODIFYSCENE";
	public static final String CMD_SP_DELSCENE = "APPDELSCENE";
	public static final String CMD_SP_ENABLESCENE = "APPENABLESCENE";
	public static final String CMD_SP_QUERYSCENE = "APPQUERYSCENE";

	// 服务器主动通知的命令
	public static final String CMD_SP_NOTIFYONLINE = "APPNOTIFYONLINE";
	public static final String CMD_SP_NOTIFYPOWER = "APPNOTIFYPOWER";
	public static final String CMD_SP_NOTIFYLIGHT = "APPNOTIFYLIGHT";
	public static final String CMD_SP_NOTIFYTIMER = "APPTIMER";

	public static final String CMD_SP_NOTIFYCURTAIN = "APPNOTIFYCURTAIN";
	public static final String CMD_SP_NOTIFYKETTLE = "APPNOTIFYKETTLE";
	public static final String CMD_SP_NOTIFYUPGRADEAP = "APPNOTIFYUPGRADEAP";

	// -------------------------------------------------------
	public static final int EVT_SP_REGISTER = 10;
	public static final int EVT_SP_MODPWD = 11;
	public static final int EVT_SP_MODEMAIL = 12;
	public static final int EVT_SP_FINDPWD = 13;

	public static final int EVT_SP_LOGIN = 50;
	public static final int EVT_SP_LOGINOUT = 51;

	public static final int EVT_SP_ADDPLUG = 100;
	public static final int EVT_SP_DELPLUG = 101;
	public static final int EVT_SP_MODYPLUG = 102;
	public static final int EVT_SP_ADDTIMER = 103;
	public static final int EVT_SP_MODTIMER = 104;
	public static final int EVT_SP_DELTIMER = 105;
	public static final int EVT_SP_QRYPLUG = 106;
	public static final int EVT_SP_TIMERENABLED = 107;
	public static final int EVT_SP_POWER = 108;
	public static final int EVT_SP_LIGHT = 109;
	public static final int EVT_SP_RGB = 110;
	public static final int EVT_SP_BELL = 111;
	public static final int EVT_SP_PARENTCTRL = 112;
	public static final int EVT_SP_USB = 113;
	public static final int EVT_SP_PROTOCOL = 114;

	public static final int EVT_SP_CURTAIN = 115;
	public static final int EVT_SP_WINDOW = 116;
	public static final int EVT_SP_ALED = 117;
	public static final int EVT_SP_AIRCON = 118;
	public static final int EVT_SP_BACK2AP = 119;

	public static final int EVT_SP_NOTIFYONLINE = 200;
	public static final int EVT_SP_NOTIFYPOWER = 201;
	public static final int EVT_SP_NOTIFYLIGHT = 202;
	public static final int EVT_SP_NOTIFYTIMER = 203;

	public static final int EVT_SP_NOTIFYCURTAIN = 204;
	public static final int EVT_SP_NOTIFYWINDOW = 205;
	public static final int EVT_SP_NOTIFYALED = 206;
	public static final int EVT_SP_NOTIFYAIRCON = 207;

	public static final int EVT_SP_AIRCONSERVER = 210;
	public static final int EVT_SP_AIRCONSERVER_IRDATA = 211;

	public static final int EVT_SP_AIRCON_ADDSCENE = 220;
	public static final int EVT_SP_AIRCON_MODIFYSCENE = 221;
	public static final int EVT_SP_AIRCON_DELSCENE = 222;
	public static final int EVT_SP_AIRCON_ENABLESCENE = 223;
	public static final int EVT_SP_AIRCON_QUERYSCENE = 224;

	public static final int EVT_SP_QUERYGONGLV = 225;
	public static final int EVT_SP_QUERYENERGE = 226;
	public static final int EVT_SP_ENABLEENERGE = 227;

	public static final int EVT_SP_BATTERY_QUERY_TRAIL = 228;
	public static final int EVT_SP_BATTERY_NOTIFY_ENERGE = 229;
	public static final int EVT_SP_BATTERY_QUERY_ENERGE = 230;
	public static final int EVT_SP_NOTIFYKETTLE = 231;
	public static final int EVT_SP_NOTIFYUPGRADEAP = 232;

	public static final int EVT_SP_TVSERVER = 233;
	public static final int EVT_SP_TVSERVER_IRDATA = 234;

	public static Map<String, Integer> mEventCommand = new HashMap<String, Integer>();
	static {
		mEventCommand.put(CMD_SP_REGISTER, EVT_SP_REGISTER);
		mEventCommand.put(CMD_SP_MODPWD, EVT_SP_MODPWD);
		mEventCommand.put(CMD_SP_MODEMAIL, EVT_SP_MODEMAIL);
		mEventCommand.put(CMD_SP_FINDPWD, EVT_SP_FINDPWD);
		mEventCommand.put(CMD_SP_LOGIN_SERVER, EVT_SP_LOGIN);
		mEventCommand.put(CMD_SP_LOGINOUT, EVT_SP_LOGINOUT);
		mEventCommand.put(CMD_SP_ADDPLUG, EVT_SP_ADDPLUG);
		mEventCommand.put(CMD_SP_DELPLUG, EVT_SP_DELPLUG);
		mEventCommand.put(CMD_SP_MODYPLUG, EVT_SP_MODYPLUG);
		mEventCommand.put(CMD_SP_ADDTIMER, EVT_SP_ADDTIMER);
		mEventCommand.put(CMD_SP_MODTIMER, EVT_SP_MODTIMER);
		mEventCommand.put(CMD_SP_DELTIMER, EVT_SP_DELTIMER);
		mEventCommand.put(CMD_SP_QRYPLUG, EVT_SP_QRYPLUG);
		mEventCommand.put(CMD_SP_TIMERENABLED, EVT_SP_TIMERENABLED);
		mEventCommand.put(CMD_SP_POWER, EVT_SP_POWER);
		mEventCommand.put(CMD_SP_PROTOCOL, EVT_SP_PROTOCOL);
		mEventCommand.put(CMD_SP_LIGHT, EVT_SP_LIGHT);
		mEventCommand.put(CMD_SP_RGB, EVT_SP_RGB);
		mEventCommand.put(CMD_SP_BELL, EVT_SP_BELL);
		mEventCommand.put(CMD_SP_BACK2AP, EVT_SP_BACK2AP);
		mEventCommand.put(CMD_SP_PARENTCTRL, EVT_SP_PARENTCTRL);
		mEventCommand.put(CMD_SP_USB, EVT_SP_USB);
		mEventCommand.put(CMD_SP_NOTIFYONLINE, EVT_SP_NOTIFYONLINE);
		mEventCommand.put(CMD_SP_NOTIFYPOWER, EVT_SP_NOTIFYPOWER);
		mEventCommand.put(CMD_SP_NOTIFYLIGHT, EVT_SP_NOTIFYLIGHT);
		mEventCommand.put(CMD_SP_NOTIFYTIMER, EVT_SP_NOTIFYTIMER);
		mEventCommand.put(CMD_SP_NOTIFYCURTAIN, EVT_SP_NOTIFYCURTAIN);
		mEventCommand.put(CMD_SP_NOTIFYKETTLE, EVT_SP_NOTIFYKETTLE);
		mEventCommand.put(CMD_SP_NOTIFYUPGRADEAP, EVT_SP_NOTIFYUPGRADEAP);

		mEventCommand.put(CMD_SP_CURTAIN, EVT_SP_CURTAIN);
		mEventCommand.put(CMD_SP_WINDOW, EVT_SP_WINDOW);
		mEventCommand.put(CMD_SP_ALED, EVT_SP_ALED);
		mEventCommand.put(CMD_SP_AIRCON, EVT_SP_AIRCON);
		mEventCommand.put(CMD_SP_AIRCONSERVER, EVT_SP_AIRCONSERVER);
		mEventCommand.put(CMD_QRY_IRDATA, EVT_SP_AIRCONSERVER_IRDATA);
		mEventCommand.put(CMD_SP_TVSERVER, EVT_SP_TVSERVER);
		mEventCommand.put(CMD_QRY_TVIRDATA, EVT_SP_TVSERVER_IRDATA);

		mEventCommand.put(CMD_SP_ADDSCENE, EVT_SP_AIRCON_ADDSCENE);
		mEventCommand.put(CMD_SP_MODIFYSCENE, EVT_SP_AIRCON_MODIFYSCENE);
		mEventCommand.put(CMD_SP_DELSCENE, EVT_SP_AIRCON_DELSCENE);
		mEventCommand.put(CMD_SP_ENABLESCENE, EVT_SP_AIRCON_ENABLESCENE);
		mEventCommand.put(CMD_SP_QUERYSCENE, EVT_SP_AIRCON_QUERYSCENE);

		mEventCommand.put(CMD_SP_APP_QRYGONGLV, EVT_SP_QUERYGONGLV);
		mEventCommand.put(CMD_SP_APP_QRYENERGE, EVT_SP_QUERYENERGE);
		mEventCommand.put(CMD_SP_APP_ENABLEENERGE, EVT_SP_ENABLEENERGE);
		mEventCommand.put(CMD_SP_QRYGONGLV, EVT_SP_QUERYGONGLV);
		mEventCommand.put(CMD_SP_QRYENERGE, EVT_SP_QUERYENERGE);
		mEventCommand.put(CMD_SP_ENABLEENERGE, EVT_SP_ENABLEENERGE);

		mEventCommand.put(CMD_SP_BATTERY_QUERYENERGE,
				EVT_SP_BATTERY_QUERY_ENERGE);
		mEventCommand
				.put(CMD_SP_BATTERY_QUERYTRAIL, EVT_SP_BATTERY_QUERY_TRAIL);
		mEventCommand.put(CMD_SP_BATTERY_NOTIFYENERGE,
				EVT_SP_BATTERY_NOTIFY_ENERGE);

	};

	/*
	 * 根据命令码获得对应的事件编码 由于发到模块的命令前加了APP前缀，而模块返回的命令码没有这个前缀，对于直连或摇一摇，需要进行适配
	 */
	public static int getEvent(final String cmd) {

		for (String key : mEventCommand.keySet()) {
			if (key.equals(cmd)) {
				return mEventCommand.get(cmd);
			}
		}

		for (String key : mEventCommand.keySet()) {
			if (key.equals("APP" + cmd)) {
				return mEventCommand.get("APP" + cmd);
			}
		}

		return -1;

		// return mEventCommand.get(cmd);
	}

	public static Map<String, String> mCommandAction = new HashMap<String, String>();
	static {
		mCommandAction.put(CMD_SP_REGISTER, PubDefine.REGISTER_BROADCAST);
		mCommandAction.put(CMD_SP_MODPWD, PubDefine.USER_MODIFY_PASSWORD);
		mCommandAction.put(CMD_SP_MODEMAIL, PubDefine.USER_MODIFY_EMAIL);
		mCommandAction.put(CMD_SP_FINDPWD, PubDefine.FINDPWD_BROADCAST);
		mCommandAction.put(CMD_SP_LOGIN_SERVER, PubDefine.LOGIN_BROADCAST);
		mCommandAction.put(CMD_SP_LOGINOUT, PubDefine.LOGOUT_BROADCAST);
		mCommandAction.put(CMD_SP_ADDPLUG, PubDefine.PLUG_ADD_TASK);
		mCommandAction.put(CMD_SP_DELPLUG, PubDefine.PLUG_DELETE);
		mCommandAction.put(CMD_SP_MODYPLUG, PubDefine.PLUG_MODIFY_PLUGNAME);
		mCommandAction.put(CMD_SP_ADDTIMER, PubDefine.PLUG_ADD_TIMERTASK);
		mCommandAction.put(CMD_SP_MODTIMER, PubDefine.PLUG_MOD_TIMERTASK);
		mCommandAction.put(CMD_SP_DELTIMER, PubDefine.PLUG_DEL_TIMERTASK);
		mCommandAction.put(CMD_SP_QRYPLUG, PubDefine.PLUG_UPDATE);
		mCommandAction
				.put(CMD_SP_TIMERENABLED, PubDefine.PLUG_SETTIMER_ENABLED);
		mCommandAction.put(CMD_SP_POWER, PubDefine.PLUG_POWER_ACTION);
		mCommandAction.put(CMD_SP_PROTOCOL, PubDefine.PLUG_POWER_ACTION);
		mCommandAction.put(CMD_SP_LIGHT, PubDefine.PLUG_LIGHT_ACTION);
		mCommandAction.put(CMD_SP_RGB, PubDefine.PLUG_RGB_ACTION);
		mCommandAction.put(CMD_SP_BACK2AP, PubDefine.PLUG_BACK2AP_ACTION);

		// mCommandAction.put(CMD_SP_BELL, EVT_SP_BELL);
		mCommandAction.put(CMD_SP_PARENTCTRL, PubDefine.PLUG_PARENTCTRL_ACTION);
		mCommandAction.put(CMD_SP_USB, PubDefine.PLUG_USB_ACTION);
		mCommandAction.put(CMD_SP_NOTIFYONLINE, PubDefine.PLUG_NOTIFY_ONLINE);
		mCommandAction.put(CMD_SP_NOTIFYPOWER, PubDefine.PLUG_NOTIFY_POWER);
		mCommandAction.put(CMD_SP_NOTIFYLIGHT, PubDefine.PLUG_NOTIFY_LIGHT);
		mCommandAction.put(CMD_SP_NOTIFYTIMER, PubDefine.PLUG_NOTIFYTIMER);
		// add new device type
		mCommandAction.put(CMD_SP_CURTAIN, PubDefine.PLUG_CURTAIN_ACTION);
		mCommandAction.put(CMD_QRY_IRDATA, PubDefine.PLUG_AIRCON_IRDATA_ACTION);
		mCommandAction.put(CMD_SP_AIRCONSERVER,
				PubDefine.PLUG_AIRCON_SERVER_ACTION);
		mCommandAction.put(CMD_SP_NOTIFYCURTAIN, PubDefine.PLUG_NOTIFY_CURTAIN);
		mCommandAction.put(CMD_SP_NOTIFYKETTLE, PubDefine.PLUG_NOTIFY_KETTLE);
		mCommandAction.put(CMD_SP_WINDOW, PubDefine.PLUG_WINDOW_ACTION);
		mCommandAction.put(CMD_SP_ALED, PubDefine.PLUG_ALED_ACTION);
		mCommandAction.put(CMD_SP_AIRCON, PubDefine.PLUG_AIRCON_ACTION);

		mCommandAction.put(CMD_SP_ADDSCENE,
				PubDefine.PLUG_AIRCON_ADDSCENE_ACTION);
		mCommandAction.put(CMD_SP_MODIFYSCENE,
				PubDefine.PLUG_AIRCON_MODIFYSCENE_ACTION);
		mCommandAction.put(CMD_SP_DELSCENE,
				PubDefine.PLUG_AIRCON_DELSCENE_ACTION);
		mCommandAction.put(CMD_SP_ENABLESCENE,
				PubDefine.PLUG_AIRCON_ENABLESCENE_ACTION);
		mCommandAction.put(CMD_SP_QUERYSCENE,
				PubDefine.PLUG_AIRCON_QUERYSCENE_ACTION);

		mCommandAction.put(CMD_SP_BATTERY_QUERYENERGE,
				PubDefine.PLUG_BATTERY_QUERYENERGE_ACTION);
		mCommandAction.put(CMD_SP_BATTERY_QUERYTRAIL,
				PubDefine.PLUG_BATTERY_QUERYTRAIL_ACTION);
		mCommandAction.put(CMD_SP_BATTERY_NOTIFYENERGE,
				PubDefine.PLUG_BATTERY_NOTIFYENERGE_ACTION);

	};

	public static String getAction(final String cmd) {
		return mCommandAction.get(cmd);
	}
}
