package com.thingzdo.ui.control;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugContentDefine;
import com.thingzdo.dataprovider.SmartPlugIRSceneHelper;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.IRSceneDefine;
import com.thingzdo.ui.TimerDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.control.DetailAirCon2Activity.SerializableMap;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.ui.wheelutils.ActionSheetDialog;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.OnSheetItemClickListener;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.SheetItemColor;
import com.thingzdo.ui.wheelutils.MyAlertDialog;
import com.thingzdo.ui.wheelutils.ScreenInfo;
import com.thingzdo.ui.wheelutils.WheelMain;

public class DetailIRSceneActivity extends TitledActivity implements OnClickListener{
	private String mPlugID = "";
	private String mPlugIP = "";
	
	private static final int ENABLE_ON = 1;
	private static final int ENABLE_OFF = 0;
	
	// 空间定义
	private RadioGroup  rg_power;
	private RadioButton	rb_power_on;
	private RadioButton rb_power_off;

	private RadioGroup  rg_mode;
	private RadioButton	rb_mode_auto;
	private RadioButton	rb_mode_cool;
	private RadioButton	rb_mode_wet;
	private RadioButton	rb_mode_wind;
	private RadioButton	rb_mode_hot;

	private RadioGroup  rg_swing;
	private RadioButton	rb_swing_on;
	private RadioButton	rb_swing_off;
    
	private RadioGroup  rg_volume;
	private RadioButton	rb_volume_auto;
	private RadioButton	rb_volume_small;
	private RadioButton	rb_volume_middle;
	private RadioButton	rb_volume_big;
    
	private TextView    tv_temperature;
    private Spinner     sp_temperature;
    
	private CheckBox      cb_peroid_7;
	private CheckBox      cb_peroid_1;
	private CheckBox      cb_peroid_2;
	private CheckBox      cb_peroid_3;
	private CheckBox      cb_peroid_4;
	private CheckBox      cb_peroid_5;
	private CheckBox      cb_peroid_6;
    
	private TextView      tv_alarm;
	private ImageView     img_temperature;
	private RelativeLayout  ll_temperature;
    
	private RadioGroup  rg_enable;
	private RadioButton	rb_enable_on;
	private RadioButton	rb_enable_off;
	
	private Button btn_deletescene;
	
	boolean bDefault = true;
	
	private int i_irsceneid = 0;
	private int i_power = 0;
	private int i_mode = 0;
	private int i_swing = 0;
	private int i_volume = 0;
	private int i_temperature = 25;
	private int i_period_1 = 1;
	private int i_period_2 = 1;
	private int i_period_3 = 1;
	private int i_period_4 = 1;
	private int i_period_5 = 1;
	private int i_period_6 = 1;
	private int i_period_7 = 1;
	private int i_enable = ENABLE_ON;
	private String str_alarm = "12:00:00";
	private String str_IRName = "";
	
	List<String> irTemperaturelist = new ArrayList<String>();
	
	private SmartPlugIRSceneHelper mIRSceneHelper = null;
	
	private Context     mContext;
	private WheelMain wheelMain;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_ir_scene_timer, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		mContext = this;
		
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		setTitleRightButton(R.string.smartplug_confirm, R.drawable.title_btn_selector, this);
		
		mIRSceneHelper = new SmartPlugIRSceneHelper(this);
		
		Intent intent = getIntent();
		bDefault = intent.getBooleanExtra("DEFAULT", true);
		mPlugID = intent.getStringExtra("PLUGID");
		mPlugIP = intent.getStringExtra("PLUGIP");
		i_irsceneid = intent.getIntExtra("IRSCENEID", 0);
		str_IRName = intent.getStringExtra("IRNAME");
		if (bDefault == false) {
			HashMap<String, Object> data = (HashMap<String, Object>)intent.getSerializableExtra("MAPDATA");
			i_irsceneid = (Integer)data.get("iv_item_irsceneid");
	        i_power = (Integer)data.get("iv_item_power");
	        i_mode = (Integer)data.get("iv_item_mode");
	        i_swing = (Integer)data.get("iv_item_swing");
	        i_volume = (Integer)data.get("iv_item_volume");
	        i_temperature = (Integer)data.get("tv_item_temperature");

	        i_period_1 = (Integer)data.get("iv_item_week_1");
	        i_period_2 = (Integer)data.get("iv_item_week_2");
	        i_period_3 = (Integer)data.get("iv_item_week_3");
	        i_period_4 = (Integer)data.get("iv_item_week_4");
	        i_period_5 = (Integer)data.get("iv_item_week_5");
	        i_period_6 = (Integer)data.get("iv_item_week_6");
	        i_period_7 = (Integer)data.get("iv_item_week_7");
	        str_alarm = (String)data.get("tv_item_alarm");
	        i_enable = (Integer)data.get("iv_item_enable");
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();
		init();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void init() {

		// Initial Control
		rg_power = (RadioGroup)findViewById(R.id.rg_power);
		rb_power_on = (RadioButton)findViewById(R.id.rb_power_on);
		rb_power_off = (RadioButton)findViewById(R.id.rb_power_off);

		rg_mode = (RadioGroup)findViewById(R.id.rg_mode);
		rb_mode_auto = (RadioButton)findViewById(R.id.rb_mode_auto);
		rb_mode_cool = (RadioButton)findViewById(R.id.rb_mode_cool);
		rb_mode_wet = (RadioButton)findViewById(R.id.rb_mode_wet);
		rb_mode_wind = (RadioButton)findViewById(R.id.rb_mode_wind);
		rb_mode_hot = (RadioButton)findViewById(R.id.rb_mode_hot);

		rg_swing = (RadioGroup)findViewById(R.id.rg_swing);
		rb_swing_on = (RadioButton)findViewById(R.id.rb_swing_on);
		rb_swing_off = (RadioButton)findViewById(R.id.rb_swing_off);
	    
		rg_volume = (RadioGroup)findViewById(R.id.rg_volume);
		rb_volume_auto = (RadioButton)findViewById(R.id.rb_volume_auto);
		rb_volume_small = (RadioButton)findViewById(R.id.rb_volume_small);
		rb_volume_middle = (RadioButton)findViewById(R.id.rb_volume_middle);
		rb_volume_big = (RadioButton)findViewById(R.id.rb_volume_big);
	    
		tv_temperature = (TextView)findViewById(R.id.tv_temperature);
	    sp_temperature = (Spinner)findViewById(R.id.sp_temperature);
	    
		cb_peroid_7 = (CheckBox)findViewById(R.id.cb_peroid_7);
		cb_peroid_1 = (CheckBox)findViewById(R.id.cb_peroid_1);
		cb_peroid_2 = (CheckBox)findViewById(R.id.cb_peroid_2);
		cb_peroid_3 = (CheckBox)findViewById(R.id.cb_peroid_3);
		cb_peroid_4 = (CheckBox)findViewById(R.id.cb_peroid_4);
		cb_peroid_5 = (CheckBox)findViewById(R.id.cb_peroid_5);
		cb_peroid_6 = (CheckBox)findViewById(R.id.cb_peroid_6);
	    
		tv_alarm = (TextView)findViewById(R.id.tv_alarm);

		img_temperature = (ImageView)findViewById(R.id.img_temperature);
		img_temperature.setOnClickListener(this);
		
		ll_temperature = (RelativeLayout)findViewById(R.id.ll_temperature);
		ll_temperature.setOnClickListener(this);
	    
		rg_enable = (RadioGroup)findViewById(R.id.rg_enable);
		rb_enable_on = (RadioButton)findViewById(R.id.rb_enable_on);
		rb_enable_off = (RadioButton)findViewById(R.id.rb_enable_off);
		
		tv_alarm.setOnClickListener(this);
		
		btn_deletescene = (Button)findViewById(R.id.btn_deletescene);
		btn_deletescene.setOnClickListener(this);
		if(bDefault == true) {
			btn_deletescene.setVisibility(View.GONE);
		} else {
			btn_deletescene.setVisibility(View.VISIBLE);
		}

		// Init Spinner的 数据
		for (int i = 16; i <= 30; i++) {
			irTemperaturelist.add(String.valueOf(i));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.activity_detail_aircon2item, R.id.tv_item, irTemperaturelist);  
		sp_temperature.setAdapter(adapter);
		sp_temperature.setPrompt("测试");
		sp_temperature.setOnItemSelectedListener(new IRSceneTemperatureListener());
		
		// 初始化数据
		refreshUI();
	}
	
	private void refreshUI() {
		switch (i_power)
		{
		case 0:	// on
			rb_power_on.setChecked(true);
			break;
		case 1:	// off
			rb_power_off.setChecked(true);
			break;
		}
		
		switch (i_mode)
		{
		case 0:	// on
			rb_mode_auto.setChecked(true);
			break;
		case 1:	// off
			rb_mode_cool.setChecked(true);
			break;
		case 2:	// on
			rb_mode_wet.setChecked(true);
			break;
		case 3:	// off
			rb_mode_wind.setChecked(true);
			break;
		case 4:	// on
			rb_mode_hot.setChecked(true);
			break;
		}

		switch (i_swing)
		{
		case 0:	// on
			rb_swing_on.setChecked(true);
			break;
		case 1:	// off
			rb_swing_off.setChecked(true);
			break;
		}

		switch (i_volume)
		{
		case 0:	// on
			rb_volume_auto.setChecked(true);
			break;
		case 1:	// off
			rb_volume_small.setChecked(true);
			break;
		case 2:	// off
			rb_volume_middle.setChecked(true);
			break;
		case 3:	// off
			rb_volume_big.setChecked(true);
			break;
		}

        tv_temperature.setText(String.valueOf(i_temperature));
		int select_no = irTemperaturelist.indexOf(String.valueOf(i_temperature));
		if (select_no == -1) {
			select_no = irTemperaturelist.indexOf("25");
		}
		sp_temperature.setSelection(select_no);
        
       	cb_peroid_1.setChecked(i_period_1 == 0 ? false : true);
       	cb_peroid_2.setChecked(i_period_2 == 0 ? false : true);
       	cb_peroid_3.setChecked(i_period_3 == 0 ? false : true);
       	cb_peroid_4.setChecked(i_period_4 == 0 ? false : true);
       	cb_peroid_5.setChecked(i_period_5 == 0 ? false : true);
       	cb_peroid_6.setChecked(i_period_6 == 0 ? false : true);
       	cb_peroid_7.setChecked(i_period_7 == 0 ? false : true);
       	
        tv_alarm.setText(str_alarm);
        
		switch (i_enable)
		{
		case ENABLE_ON:	// on
			rb_enable_on.setChecked(true);
			break;
		case ENABLE_OFF:	// off
			rb_enable_off.setChecked(true);
			break;
		}
	}
	
	class IRSceneTemperatureListener implements android.widget.AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        	String str_Current_NO = parent.getItemAtPosition(position).toString();
        	tv_temperature.setText(str_Current_NO);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        	
        }
    }

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:
			finish();
			break;
		case R.id.titlebar_rightbutton:
			getValues();
			
			if (bDefault == true) {	// Add
				addIRScene();
			} else {	// Modify
				modifyIRScene();
			}
			finish();
			break;
		case R.id.tv_alarm:
			showDialog(tv_alarm, tv_alarm.getText().toString());
			break;
		case R.id.btn_deletescene:
			deleteIRScene();
			finish();
			break;
		case R.id.img_temperature:
			showTemperatures();
			break;
		case R.id.ll_temperature:
			showTemperatures();
			break;
		default:
			break;
		}
	}
	
	private Handler updateHandler = new Handler() {
		public void handleMessage(Message msg) {
		    if (0 == msg.what) {			// update Mac Address
		    	tv_temperature.setText(String.valueOf(msg.arg1));
		    	
		    } else if (1 == msg.what) {		// 
		    	// do nothing...
		    } else {
		    	// do nothing...
		    }
		};
	};
	
	/*
	 * 显示所有温度
	 */
	private void showTemperatures() {
		ActionSheetDialog dlg = new ActionSheetDialog(DetailIRSceneActivity.this)
		.builder()
		.setTitle(DetailIRSceneActivity.this.getString(R.string.smartplug_oper_selecttemperature_title))
		.setCancelable(true)
		.setCanceledOnTouchOutside(true);
		
		for (int i = 16; i <= 30; i++) {
			dlg.addSheetItem(String.valueOf(i), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
			            	Message msg = new Message();
			            	msg.what = 0;
			            	msg.arg1 = which + 15;
			            	updateHandler.sendMessage(msg);							
						}
			});
		}
		dlg.show();
	}
	
	private void getValues() {
		i_power = rb_power_on.isChecked() == true ? 0 : 1;

		if (rb_mode_auto.isChecked() == true) {
			i_mode = 0;
		} else if (rb_mode_cool.isChecked() == true) {
			i_mode = 1;
		} else if (rb_mode_wet.isChecked() == true) {
			i_mode = 2;
		} else if (rb_mode_wind.isChecked() == true) {
			i_mode = 3;
		} else if (rb_mode_hot.isChecked() == true) {
			i_mode = 4;
		}
		
		if (rb_swing_on.isChecked() == true) {
			i_swing = 0;
		} else if (rb_swing_off.isChecked() == true) {
			i_swing = 1;
		}
		
		if (rb_volume_auto.isChecked() == true) {
			i_volume = 0;
		} else if (rb_volume_small.isChecked() == true) {
			i_volume = 1;
		} else if (rb_volume_middle.isChecked() == true) {
			i_volume = 2;
		} else if (rb_volume_big.isChecked() == true) {
			i_volume = 3;
		}
		
		i_temperature = Integer.valueOf(tv_temperature.getText().toString());

		i_period_1 = (cb_peroid_1.isChecked() == true) ? 1 : 0;
		i_period_2 = (cb_peroid_2.isChecked() == true) ? 1 : 0;
		i_period_3 = (cb_peroid_3.isChecked() == true) ? 1 : 0;
		i_period_4 = (cb_peroid_4.isChecked() == true) ? 1 : 0;
		i_period_5 = (cb_peroid_5.isChecked() == true) ? 1 : 0;
		i_period_6 = (cb_peroid_6.isChecked() == true) ? 1 : 0;
		i_period_7 = (cb_peroid_7.isChecked() == true) ? 1 : 0;
        
		str_alarm = tv_alarm.getText().toString();
		
		if (rb_enable_on.isChecked() == true) {
			i_enable = ENABLE_ON;
		} else if (rb_enable_off.isChecked() == true) {
			i_enable = ENABLE_OFF;
		}
	}

	private void addIRScene() {
		IRSceneDefine ir = new IRSceneDefine();
		ir.mIRSceneId = i_irsceneid;
		ir.mPlugId = mPlugID;
		ir.mPower = i_power;
		ir.mMode = i_mode;
		ir.mDirection = i_swing;
		ir.mScale = i_volume;
		ir.mTemperature = i_temperature;
		ir.mPeriod = String.valueOf(i_period_7) + 
					 String.valueOf(i_period_1) + 
					 String.valueOf(i_period_2) + 
					 String.valueOf(i_period_3) + 
					 String.valueOf(i_period_4) + 
					 String.valueOf(i_period_5) + 
					 String.valueOf(i_period_6) ;
		ir.mTime = str_alarm;
		ir.mIRName = str_IRName;
		ir.mEnable = i_enable;
        
		addSceneServerData(ir);
		mIRSceneHelper.addIRScene(ir);
	}
	

	private void addSceneServerData(IRSceneDefine ir) {
    	String info = String.valueOf(ir.mIRSceneId) + "," +
    				  String.valueOf(ir.mPower) + "," +
    				  String.valueOf(ir.mMode) + "," +
    				  String.valueOf(ir.mDirection) + "," +
    				  String.valueOf(ir.mScale) + "," +
    				  String.valueOf(ir.mTemperature) + "," +
    				  String.valueOf(ir.mTime) + "," +
    				  String.valueOf(ir.mPeriod) + "," +
    				  String.valueOf(ir.mIRName) + "," +
    				  String.valueOf(ir.mEnable);
    	
		StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_ADDSCENE)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(PubStatus.g_CurUserName)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(mPlugID)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(info);
    				  
    	sendMsg(true, sb.toString(), true);
	}

	private void deleteSceneServerData(IRSceneDefine ir) {
    	String info = String.valueOf(ir.mIRSceneId) + "," +
				  String.valueOf(ir.mPower) + "," +
				  String.valueOf(ir.mMode) + "," +
				  String.valueOf(ir.mDirection) + "," +
				  String.valueOf(ir.mScale) + "," +
				  String.valueOf(ir.mTemperature) + "," +
				  String.valueOf(ir.mTime) + "," +
				  String.valueOf(ir.mPeriod) + "," +
				  String.valueOf(ir.mIRName) + "," +
				  String.valueOf(ir.mEnable);
    	
		StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_DELSCENE)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(PubStatus.g_CurUserName)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(mPlugID)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(info);
    	
    	sendMsg(true, sb.toString(), true);
	}
	
	private void modifySceneServerData(IRSceneDefine ir) {
    	String info = String.valueOf(ir.mIRSceneId) + "," +
				  String.valueOf(ir.mPower) + "," +
				  String.valueOf(ir.mMode) + "," +
				  String.valueOf(ir.mDirection) + "," +
				  String.valueOf(ir.mScale) + "," +
				  String.valueOf(ir.mTemperature) + "," +
				  String.valueOf(ir.mTime) + "," +
				  String.valueOf(ir.mPeriod) + "," +
				  String.valueOf(ir.mIRName) + "," +
				  String.valueOf(ir.mEnable);
    	
		StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_MODIFYSCENE)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(PubStatus.g_CurUserName)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(mPlugID)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(info);
    	
    	sendMsg(true, sb.toString(), true);
	}
	

	private void enableSceneServerData(IRSceneDefine ir) {
    	String info = String.valueOf(ir.mIRSceneId) + "," +
				  String.valueOf(ir.mPower) + "," +
				  String.valueOf(ir.mMode) + "," +
				  String.valueOf(ir.mDirection) + "," +
				  String.valueOf(ir.mScale) + "," +
				  String.valueOf(ir.mTemperature) + "," +
				  String.valueOf(ir.mTime) + "," +
				  String.valueOf(ir.mPeriod) + "," +
				  String.valueOf(ir.mIRName) + "," +
				  String.valueOf(ir.mEnable);
  	
		StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_ENABLESCENE)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(PubStatus.g_CurUserName)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(mPlugID)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(info);
    	
    	sendMsg(true, sb.toString(), true);
	}
	
	private void TestCommand(String plugId) {
		 //0,AIRCONSERVER,test,12345678,格力,021#
		 StringBuffer sb = new StringBuffer();
		 sb.append("APPADDSCENE,smli123hz," + plugId + ",104,1,0,0,0,26,11:33,1111111,海尔,1");
		
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
			sendMsg(true, sb.toString(), true);
		} else {  // 非Internet模式下，不处理； 
			// do nothing.
		}
	}
	
	private void modifyIRScene() {
		IRSceneDefine ir = new IRSceneDefine();
		ir.mIRSceneId = i_irsceneid;
		ir.mPlugId = mPlugID;
		ir.mPower = i_power;
		ir.mMode = i_mode;
		ir.mDirection = i_swing;
		ir.mScale = i_volume;
		ir.mTemperature = i_temperature;
		ir.mPeriod = String.valueOf(i_period_7) + 
					 String.valueOf(i_period_1) + 
					 String.valueOf(i_period_2) + 
					 String.valueOf(i_period_3) + 
					 String.valueOf(i_period_4) + 
					 String.valueOf(i_period_5) + 
					 String.valueOf(i_period_6) ;
		ir.mTime = str_alarm;
		ir.mIRName = str_IRName;
		ir.mEnable = i_enable;
        
		modifySceneServerData(ir);
		mIRSceneHelper.modifyIRScene(ir);
	}
	
	private void deleteIRScene() {
		IRSceneDefine ir = new IRSceneDefine();
		ir.mIRSceneId = i_irsceneid;
		ir.mPlugId = mPlugID;
		ir.mPower = i_power;
		ir.mMode = i_mode;
		ir.mDirection = i_swing;
		ir.mScale = i_volume;
		ir.mTemperature = i_temperature;
		ir.mPeriod = String.valueOf(i_period_7) + 
					 String.valueOf(i_period_1) + 
					 String.valueOf(i_period_2) + 
					 String.valueOf(i_period_3) + 
					 String.valueOf(i_period_4) + 
					 String.valueOf(i_period_5) + 
					 String.valueOf(i_period_6) ;
		ir.mTime = str_alarm;
		ir.mIRName = str_IRName;
		ir.mEnable = i_enable;
        
		deleteSceneServerData(ir);
		mIRSceneHelper.deleteIRScene(ir.mIRSceneId);
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
		final View timepickerview = inflater.inflate(R.layout.timepicker,null);
		ScreenInfo screenInfo = new ScreenInfo(DetailIRSceneActivity.this);
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
			}
		});
		dialog.show();	
	}

}
