package com.thingzdo.ui.control;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.manage.DuerOSActivity.SendMsgUDP;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class EnergeActivity extends TitledActivity  implements OnClickListener {
	private TextView tv_plugname;
	private TextView tv_immediate_power;
	private TextView tv_one_hour;
	private TextView tv_one_day;
	private TextView tv_one_week;
	private TextView tv_one_month;
	private Button   btn_update;	
	
	private SmartPlugHelper  mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";
	private boolean mOnline = false;
	
	Timer timer = null;
	TimerTask task = null;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_ENABLEENERGE)) {
				// do nothing...
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_QRYENERGE)) {
//				timeoutHandler.removeCallbacks(timeoutProcess);				
//				if (null != mProgress) {
//					mProgress.dismiss();
//				}
				
				int result = intent.getIntExtra("RESULT", 0);
				if (result == 0) {
					double one_hour = intent.getDoubleExtra("CHARGE_ONE_HOUR", 0.0);
					double one_day = intent.getDoubleExtra("CHARGE_ONE_DAY", 0.0);
					double one_week = intent.getDoubleExtra("CHARGE_ONE_WEEK", 0.0);
					double one_month = intent.getDoubleExtra("CHARGE_ONE_MONTH", 0.0);
					
					tv_one_hour.setText(String.valueOf(one_hour) + "度");
					tv_one_day.setText(String.valueOf(one_day) + "度");
					tv_one_week.setText(String.valueOf(one_week) + "度");
					tv_one_month.setText(String.valueOf(one_month) + "度");
				} else {
					String message = intent.getStringExtra("MESSAGE");
					PubFunc.thinzdoToast(EnergeActivity.this, message);
				}
				
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_QRYGONGLV)) {
				String oper_date = intent.getStringExtra("DATE");
				double dGonglv = intent.getDoubleExtra("GONGLV", 0.0);
				tv_immediate_power.setText(String.valueOf(dGonglv) + "瓦");
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_plug_energe, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		
		mPlugHelper = new SmartPlugHelper(this);
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";	
		} 
		mPlugIp = intent.getStringExtra("PLUGIP");
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_ENABLEENERGE);
		filter.addAction(PubDefine.PLUG_QRYGONGLV);
		filter.addAction(PubDefine.PLUG_QRYENERGE);
		registerReceiver(mDetailRev, filter);

		UDPClient.getInstance().setIPAddress(mPlugIp);
		
		initview();
		
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				qryEnergeFromServer();
			}
		};
		
		timer.schedule(task, 1000, 60000);
	}
	
	@Override
	protected void onDestroy() {
		if (task != null) {
			task.cancel();
		}
		if (timer != null) {
			timer.cancel();
		}
		
		super.onDestroy();
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_update:
			qryEnergeFromServer();
			break;
		default:
			break;
		}
	}
	
	private void initview() {
		tv_plugname = (TextView)findViewById(R.id.tv_plugname);
		tv_immediate_power = (TextView)findViewById(R.id.tv_immediate_power);
		tv_one_hour = (TextView)findViewById(R.id.tv_one_hour);
		tv_one_day = (TextView)findViewById(R.id.tv_one_day);
		tv_one_week = (TextView)findViewById(R.id.tv_one_week);
		tv_one_month = (TextView)findViewById(R.id.tv_one_month);
		
		btn_update = (Button)findViewById(R.id.btn_update);
		btn_update.setOnClickListener(this);
		
		initNumber();
	}
	
	private void initNumber() {
		tv_immediate_power.setText("0.0瓦");
		tv_one_hour.setText("0.0度");
		tv_one_day.setText("0.0度");
		tv_one_week.setText("0.0度");
		tv_one_month.setText("0.0度");
	}
	
	private void qryEnergeFromServer() {
		mHandler.sendEmptyMessage(0);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		new QueryGonglv().run();
		
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	new QueryEnerge().run();
	}
	
   public class QueryGonglv extends Thread
   {
        public void run()
        {
        	StringBuffer sb1 = new StringBuffer();
        	sb1.append(SmartPlugMessage.CMD_SP_APP_QRYGONGLV)
        	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
        	  .append(PubStatus.g_CurUserName)
        	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
        	  .append(mPlugId);
        	
        	sendMsg(true, sb1.toString(), true);
        }
   }
   
   public class QueryEnerge extends Thread
   {
        public void run()
        {
        	StringBuffer sb2 = new StringBuffer();
        	sb2 = new StringBuffer();
        	sb2.append(SmartPlugMessage.CMD_SP_APP_QRYENERGE)
        	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
        	  .append(PubStatus.g_CurUserName)
        	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
        	  .append(mPlugId)
        	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
        	  .append("0");									// 0: all type, include hour/day/week/month
        	
        	sendMsg(true, sb2.toString(), true);
        }
   }
	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 0:
				initNumber();
				break;
			case 1:
				break;
			default:
				break;								
			}
		}
	};
	
}
