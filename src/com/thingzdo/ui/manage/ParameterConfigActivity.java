package com.thingzdo.ui.manage;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.VoiceWakeuper;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.control.PlugTimerActivity;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.ui.wheelutils.ActionSheetDialog;
import com.thingzdo.ui.wheelutils.MyAlertDialog;
import com.thingzdo.ui.wheelutils.ScreenInfo;
import com.thingzdo.ui.wheelutils.WheelMain;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.OnSheetItemClickListener;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.SheetItemColor;

public class ParameterConfigActivity extends TitledActivity implements OnClickListener {
	public static final String action = "jason.broadcast.action";  
	
	private final int MSG_UPDATE_CITY 		= 1;
	private final String DEFAULT_SPEEK_TIME = "07:30:00";
	
	private TextView tv_smartplug_city;
	private TextView tv_speek_time;
	private RelativeLayout rl_speek_time;
	private Context mContext = null;
	private WheelMain wheelMain;
	
	private ArrayList<String> citys = new ArrayList<String> ();
	
	// 公共变量
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_parameterconfig, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		
		mSharedPreferences = getSharedPreferences("PARAMETERCONFIG" + PubStatus.g_CurUserName, Activity.MODE_PRIVATE);
		
		mContext = this;
		
		tv_smartplug_city = (TextView)findViewById(R.id.tv_smartplug_city);
		tv_speek_time = (TextView)findViewById(R.id.tv_speek_time);
		rl_speek_time = (RelativeLayout)findViewById(R.id.rl_speek_time);
		rl_speek_time.setOnClickListener(this);
		
		RelativeLayout rl_smartplug = (RelativeLayout)findViewById(R.id.rl_smartplug);
		rl_smartplug.setOnClickListener(this);
		
		initCitys();
		
		loadData();
	}
	
	@Override
	protected void onDestroy() {
		saveData();

		super.onDestroy();
	}
	
	private void saveData() {
		editor = mSharedPreferences.edit();
		
        editor.putString("CITY", tv_smartplug_city.getText().toString());
        editor.putString("SPEEK_TIME", tv_speek_time.getText().toString());
        
		editor.commit();
	}
	
	private void loadData() {
		String cur_city = mSharedPreferences.getString("CITY", "");
		String speek_time = mSharedPreferences.getString("SPEEK_TIME", DEFAULT_SPEEK_TIME);
		
		tv_smartplug_city.setText(cur_city);
		tv_speek_time.setText(speek_time);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:
			finish();
			break;
		case R.id.rl_smartplug: 		// web address
			// 启动菜单
			showAllCitys();
			break;
		case R.id.rl_speek_time:
			showDialog(tv_speek_time, tv_speek_time.getText().toString());
			break;
		}
	}
	
	private void showDialog(TextView view, String timer) {
		String[] time_val_t = timer.split(":");
		String[] time_val = {"00", "00", "00"};
		 
		if (time_val_t.length > 0) {
			time_val[0] = time_val_t[0];
		} 
		if (time_val_t.length > 1) {
			time_val[1] = time_val_t[1];
		} 
		if (time_val_t.length > 2) {
			time_val[2] = time_val_t[2];
		}
		final TextView mView = view;
		
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View timepickerview = inflater.inflate(R.layout.timepicker,
				null);
		ScreenInfo screenInfo = new ScreenInfo(ParameterConfigActivity.this);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		wheelMain.initDateTimePicker(Integer.parseInt(time_val[0]), Integer.parseInt(time_val[1]), Integer.parseInt(time_val[2]));
		final MyAlertDialog dialog = new MyAlertDialog(mContext)
				.builder()
				.setTitle(mContext.getString(R.string.timer_task_selecttime))
				.setView(timepickerview)
				.setNegativeButton(mContext.getString(R.string.smartplug_cancel) , new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});
		dialog.setPositiveButton(mContext.getString(R.string.smartplug_ok) , new OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] time = wheelMain.getTime().split(":");
				StringBuffer sb = new StringBuffer();
				if (1 == time[0].length()) {
					sb.append("0").append(time[0]).append(":");	
				} else {
					sb.append(time[0]).append(":");
				}
				if (1 == time[1].length()) {
					sb.append("0").append(time[1]).append(":");	
				} else {
					sb.append(time[1]).append(":");
				}	
				if (1 == time[2].length()) {
					sb.append("0").append(time[2]);	
				} else {
					sb.append(time[2]);
				}				
				
				mView.setText(sb);
				saveData();
				
				Intent intent = new Intent(PubDefine.CONFIG_MODIFY_SPEEK_TIMER);  
                intent.putExtra("SPEEK_TIME", String.valueOf(sb));
                intent.putExtra("CITY", String.valueOf(tv_smartplug_city.getText().toString()));
                sendBroadcast(intent);  
			}
		});
		dialog.show();
	}
	
	// -----------------------------------------------------------------------------
	// 消息处理机制
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String key = "";
			int index = msg.arg1;
			
			super.handleMessage(msg);
			switch(msg.what) {
			case MSG_UPDATE_CITY:
				String city = citys.get(index);
				tv_smartplug_city.setText(city);
				saveData();
				break;
			}
		}
	};
	
	private void initCitys() {
		citys.clear();

		citys.add("北京");
		citys.add("上海");
		citys.add("深圳");
		citys.add("广州");
		citys.add("天津");
		citys.add("济南");
		citys.add("武汉");
		citys.add("南京");
	}
	
	/*
	 * 显示所有城市
	 */
	private void showAllCitys() {
		ActionSheetDialog dlg = new ActionSheetDialog(ParameterConfigActivity.this)
		.builder()
		.setTitle(ParameterConfigActivity.this.getString(R.string.smartplug_oper_selectcity_title))
		.setCancelable(true)
		.setCanceledOnTouchOutside(true);
		
		for (int i = 0; i < citys.size(); i++) {
			
			dlg.addSheetItem(citys.get(i), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
			            	Message msg = new Message();
			            	msg.what = MSG_UPDATE_CITY;
			            	msg.arg1 = which - 1;
			            	mHandler.sendMessage(msg);
						}
			});
		}
		dlg.show();
	}
}
