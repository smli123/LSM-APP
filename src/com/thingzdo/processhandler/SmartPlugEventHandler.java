package com.thingzdo.processhandler;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

/**
 * 消息注册和分发
 * */
public class SmartPlugEventHandler extends Handler {
	public static final int EVENT_START_ACTIVITY = 0;
	public static final int EVENT_FINISH_ACTIVITY = 1;
	public static final int EVENT_MESSAGE_HEADER = 4;

	public interface IActivityDestroyListener {
		public void onActivityDestroy(Activity act);
	}

	private static SmartPlugEventHandler s_Me = null;
	private ArrayList<SmartPlugEventHandlerMap> mEventHandlerMaps = new ArrayList<SmartPlugEventHandlerMap>();

	public static SmartPlugEventHandler getInstance() {
		if (s_Me == null) {
			s_Me = new SmartPlugEventHandler();
		}

		return s_Me;
	}

	/*
	 * 根据事件编码获得对应的处理Handler
	 */
	public SmartPlugEventHandler getTheEventHandler(int event) {
		SmartPlugEventHandler eventHandler = null;
		for (SmartPlugEventHandlerMap eventHandlerMap : mEventHandlerMaps) {
			if (eventHandlerMap.getEvent() == event) {
				eventHandler = eventHandlerMap.getHandler();
				break;
			}
		}

		return eventHandler;
	}

	@Override
	public void handleMessage(Message msg) {
		do {
			Looper.prepare();
			int event = msg.what;
			// PubFunc.log("received MESSAGE.what=" + event);
			SmartPlugEventHandler eventHandler = getTheEventHandler(event);
			if (eventHandler != null) {
				eventHandler.handleMessage(msg);
			} else {
				PubFunc.log("Message: invalid Event! MSG:" + event);
			}
			Looper.loop();
		} while (false);
	}

	public void init() {
		// PubFunc.log("init MyCallEventHandler!!!");
		registerEvent();
	}

	private void registerEvent() {
		mEventHandlerMaps
				.add(new SmartPlugEventHandlerMap(
						SmartPlugMessage.EVT_SP_LOGIN,
						new SmartPlugEventHandlerLogin()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_LOGINOUT,
				new SmartPlugEventHandlerLogout()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_REGISTER,
				new SmartPlugEventHandlerRegister()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_MODPWD,
				new SmartPlugEventHandlerModifyPwd()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_MODEMAIL,
				new SmartPlugEventHandlerModifyEmail()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_FINDPWD,
				new SmartPlugEventHandlerResetPwd()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_ADDPLUG,
				new SmartPlugEventHandlerAddPlug()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_DELPLUG,
				new SmartPlugEventHandlerDeletePlug()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_MODYPLUG,
				new SmartPlugEventHandlerModifyPlugName()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_ADDTIMER,
				new SmartPlugEventHandlerAddTimer()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_MODTIMER,
				new SmartPlugEventHandlerModifyTimer()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_DELTIMER,
				new SmartPlugEventHandlerDeleteTimer()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_QRYPLUG,
				new SmartPlugEventHandlerQuery()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_TIMERENABLED,
				new SmartPlugEventHandlerSetTimerEnabled()));

		mEventHandlerMaps
				.add(new SmartPlugEventHandlerMap(
						SmartPlugMessage.EVT_SP_POWER,
						new SmartPlugEventHandlerPower()));

		mEventHandlerMaps
				.add(new SmartPlugEventHandlerMap(
						SmartPlugMessage.EVT_SP_LIGHT,
						new SmartPlugEventHandlerLight()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_BACK2AP,
				new SmartPlugEventHandlerBACK2AP()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_PROTOCOL,
				new SmartPlugEventHandlerProtocol()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_CURTAIN,
				new SmartPlugEventHandlerCurtain()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_WINDOW,
				new SmartPlugEventHandlerWindow()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_ALED, new SmartPlugEventHandlerALED()));

		// 空调红外数据接口
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_AIRCON,
				new SmartPlugEventHandlerAirCon()));
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_AIRCONSERVER,
				new SmartPlugEventHandlerAirConServer()));
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_AIRCONSERVER_IRDATA,
				new smartPlugEventHandlerAirConServerIRData()));

		// 电视红外数据接口
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_TVSERVER,
				new SmartPlugEventHandlerTVServer()));
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_TVSERVER_IRDATA,
				new smartPlugEventHandlerTVServerIRData()));

		// usb
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_USB, new SmartPlugEventHandlerUSB()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_NOTIFYONLINE,
				new SmartPlugEventHandlerNotifyOnline()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_NOTIFYPOWER,
				new SmartPlugEventHandlerNotifyPower()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_NOTIFYLIGHT,
				new SmartPlugEventHandlerNotifyLight()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_RGB, new SmartPlugEventHandlerRGB()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_NOTIFYTIMER,
				new SmartPlugEventHandlerNotifyTimer()));

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_PARENTCTRL,
				new SmartPlugEventHandlerParentCtrl()));

		// 红外场景

		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_AIRCON_ADDSCENE,
				new SmartPlugEventHandlerAddScene()));
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_AIRCON_MODIFYSCENE,
				new SmartPlugEventHandlerModifyScene()));
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_AIRCON_DELSCENE,
				new SmartPlugEventHandlerDeleteScene()));
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_AIRCON_ENABLESCENE,
				new SmartPlugEventHandlerEnableScene()));
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_AIRCON_QUERYSCENE,
				new SmartPlugEventHandlerQueryScene()));

		// Curtain
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_NOTIFYCURTAIN,
				new SmartPlugEventHandlerNotifyCurtain()));

		// Curtain
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_NOTIFYUPGRADEAP,
				new SmartPlugEventHandlerNotifyUpgradeAPStatus()));

		// Kettle
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_NOTIFYKETTLE,
				new SmartPlugEventHandlerNotifyKettle()));

		// 功率
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_QUERYGONGLV,
				new SmartPlugEventHandlerQueryGonglv()));
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_QUERYENERGE,
				new SmartPlugEventHandlerQueryEnerge()));
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_ENABLEENERGE,
				new SmartPlugEventHandlerEnableEnerge()));

		// 智能电池
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_BATTERY_QUERY_ENERGE,
				new SmartPlugEventHandlerBatteryQueryEnerge()));
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_BATTERY_QUERY_TRAIL,
				new SmartPlugEventHandlerBatteryQueryTrail()));
		mEventHandlerMaps.add(new SmartPlugEventHandlerMap(
				SmartPlugMessage.EVT_SP_BATTERY_NOTIFY_ENERGE,
				new SmartPlugEventHandlerBatteryNotifyEnerge()));
	}

	protected void log(String logString) {
		Log.d(getClass().getName(), logString);
	}

	protected String getUnknowErrorMsg() {
		return SmartPlugApplication
				.getContext()
				.getString(
						PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet
								? R.string.app_response_login_fail_devices
								: R.string.app_response_login_fail);
	}
}
