package com.thingzdo.ui.control;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.control.DetailAirCon2Activity.spinnerIRListener;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugActivity;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class PlugDetailInfoActivity extends TitledActivity implements OnClickListener {
	private SmartPlugHelper  mPlugHelper = null;
	private String mPlugId = "0";
	SmartPlugDefine mPlug = null;
	private boolean mOnline = false;
	private String mErrorMsg =  "";
	
	private TextView	tv_type_select_content;
	private TextView 	tv_sb_content;
	private SeekBar     sb_upgrade_progress;
	private Spinner		sp_smartplugtype;
	private List<String>	list_smartplugtype = new ArrayList<String>();
	
	private BroadcastReceiver mPlugDetailRev = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_BACK2AP_ACTION)) {
				updateStatus(intent);
			} else if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_UPGRADEAP)) {
				updateUpgradeAP(intent);
			}
		}
	};	
	
	/*
	 * 复位设备后的后续处理
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
		case 0:
			finish();
			break;
	    default:
			PubFunc.thinzdoToast(PlugDetailInfoActivity.this, message);
			break;					
		}		
	}
	
	/*
	 * 刷新进度条
	 */
	private void updateUpgradeAP(Intent intent) {
		timeoutHandler.removeCallbacks(timeoutProcess);	
		if (null != mProgress) {
			mProgress.dismiss();
		}

		int result = intent.getIntExtra("RESULT", 0);
		if (result == 0) {
			int index_block = intent.getIntExtra("INDEXBLOCK", 0);
			int total_block = intent.getIntExtra("TOTALBLOCK", 0);
			tv_sb_content.setText(String.format("%s/%s (%.2f%%)", index_block, total_block, ((double)index_block * 100.0) / total_block));
			sb_upgrade_progress.setMax(total_block);
			sb_upgrade_progress.setProgress(index_block);
			
			if (index_block == total_block) {
				tv_sb_content.setText("升级完成");
				PubFunc.thinzdoToast(PlugDetailInfoActivity.this, "升级完成");
			}
		} else {
			tv_sb_content.setText("模块返回错误:" + result);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_plug_detail_info, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		
		mPlugHelper = new SmartPlugHelper(this);
		
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		mOnline = intent.getBooleanExtra("ONLINE", false);
		
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		
		InitView();
		
		init_IR_Data();
		
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(PubDefine.PLUG_BACK2AP_ACTION);
//		filter.addAction(PubDefine.PLUG_NOTIFY_UPGRADEAP);
//		registerReceiver(mPlugDetailRev, filter);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_BACK2AP_ACTION);
		filter.addAction(PubDefine.PLUG_NOTIFY_UPGRADEAP);
		registerReceiver(mPlugDetailRev, filter);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
		unregisterReceiver(mPlugDetailRev);
	}
	
	private void init_IR_Data() {
		list_smartplugtype.clear();
		list_smartplugtype.add("智能插座(新)");
		list_smartplugtype.add("智能窗帘");
		list_smartplugtype.add("智能水壶");
		list_smartplugtype.add("智能电池");
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.activity_detail_aircon2item, R.id.tv_item, list_smartplugtype);  
        sp_smartplugtype.setAdapter(adapter);
        sp_smartplugtype.setPrompt("测试");
        sp_smartplugtype.setOnItemSelectedListener(new SPSmartPlugTypeListener());
        
        if (list_smartplugtype.size() > 0) {
        	String typeName = PubFunc.getDeviceNameFromModule(mPlug.mSubDeviceType);
	        int select_no = list_smartplugtype.indexOf(typeName);
			if (select_no == -1) {
				select_no = list_smartplugtype.indexOf("智能插座(新)");
				typeName = "智能插座(新)";
			}
	
			sp_smartplugtype.setSelection(select_no);
			tv_type_select_content.setText(typeName);
        }
	}
	
	class SPSmartPlugTypeListener implements android.widget.AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        	String str_Current_NO = parent.getItemAtPosition(position).toString();
        	tv_type_select_content.setText(str_Current_NO);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
	
	private void InitView() {
		SmartPlugDefine plug = mPlugHelper.getSmartPlug(mPlugId);
		
		if (null != plug) {
			setTitle(R.string.smartplug_title_plug_detail);
			setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
			
			// Device Type Image
			ImageView img_devicetype = (ImageView) findViewById(R.id.img_devicetype);
			if ( (plug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG && plug.mSubProductType == PubDefine.PRODUCT_PLUG) || 
				 (plug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG_BOARD) ) {																	// 1_1, 2_
				img_devicetype.setImageResource(R.drawable.smp_plug_small);
			} else if (plug.mSubDeviceType == PubDefine.DEVICE_SMART_KETTLE) {																	// 6_1
				img_devicetype.setImageResource(R.drawable.smp_curtain_small);
			} else if (plug.mSubDeviceType == PubDefine.DEVICE_SMART_CURTAIN) {																	// 3_1
				img_devicetype.setImageResource(R.drawable.smp_curtain_small);
			} else if (plug.mSubDeviceType == PubDefine.DEVICE_SMART_WINDOW) {
				img_devicetype.setImageResource(R.drawable.smp_unknown_small);
			} else if (plug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG && plug.mSubProductType == PubDefine.PRODUCT_SMART_PLUG_AIRCON) {		// 1_2
				img_devicetype.setImageResource(R.drawable.smp_plug_small);
			} else if (plug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG && plug.mSubProductType == PubDefine.PRODUCT_SMART_PLUG_ENERGE) {		// 1_3
				img_devicetype.setImageResource(R.drawable.smp_plug_small);
			} else {
				img_devicetype.setImageResource(R.drawable.smp_unknown_small);
			}
			
			// Plug Name
			TextView tv_plugname = (TextView) findViewById(R.id.tv_plugname);
			tv_plugname.setText(plug.mPlugName + "(" + plug.mPlugId + ")");
			
			// MAC
			TextView tv_mac = (TextView) findViewById(R.id.tv_mac);
			tv_mac.setText(plug.mMAC);

			// IP
			TextView tv_ip = (TextView) findViewById(R.id.tv_ip);
			tv_ip.setText(plug.mIPAddress);
			
			// Module Version
			TextView tv_moduleversion = (TextView) findViewById(R.id.tv_moduleversion);
			tv_moduleversion.setText(plug.mVersion);
			
			// Module Type
			TextView tv_moduletype = (TextView) findViewById(R.id.tv_moduletype);
			tv_moduletype.setText(plug.mSubDeviceType);
			
			tv_type_select_content = (TextView) findViewById(R.id.tv_type_select_content);

			// Command: Reset to AP
			Button btn_reset_to_AP = (Button) findViewById(R.id.btn_reset_to_AP);
			btn_reset_to_AP.setEnabled(mOnline);
			btn_reset_to_AP.setOnClickListener(this);
			
			// Command: Upgrade AP
			tv_sb_content = (TextView) findViewById(R.id.tv_sb_content);
			sb_upgrade_progress = (SeekBar) findViewById(R.id.sb_upgrade_progress);
			sb_upgrade_progress.setEnabled(false);
			Button btn_upgrade_ap = (Button) findViewById(R.id.btn_upgrade_ap);
			btn_upgrade_ap.setEnabled(mOnline);
			btn_upgrade_ap.setOnClickListener(this);
			
			sp_smartplugtype = (Spinner) findViewById(R.id.sp_smartplugtype);
			
			// DEBUG BUTTON
			if (true == PubDefine.RELEASE_VERSION  && false) {
				ImageView image_sep_5 = (ImageView) findViewById(R.id.image_sep_5);
				image_sep_5.setVisibility(View.GONE);
				RelativeLayout rl_resetmodule = (RelativeLayout) findViewById(R.id.rl_resetmodule);
				rl_resetmodule.setVisibility(View.GONE);
	    	}
		}
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.titlebar_leftbutton: 
			finish();
			break;
		case R.id.btn_reset_to_AP:
			Reset_to_AP();
			break;
		case R.id.btn_upgrade_ap:
			Upgrade_AP();
			PubFunc.thinzdoToast(PlugDetailInfoActivity.this, "开始升级");
			break;
		default:
			break;
		}
	}

	private void Reset_to_AP() {

//		mProgress = PubFunc.createProgressDialog(PlugDetailInfoActivity.this, getString(R.string.str_reset_module), false);
//    	mProgress.show();
//    	
//		//0,APPPASSTHROUGH,smli123hz,6044712,15,0,0,BACK2AP,smli123hz,6044712#
//		String command_base = "APPBACK2AP," + PubStatus.g_CurUserName + "," + mPlugId;
//		String command = "0," + command_base;
//		int command_length = command.getBytes().length + 1;
//
//		StringBuffer sb = new StringBuffer();
//    	sb.append("APPPASSTHROUGH")
//    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
//    	  .append(PubStatus.g_CurUserName)
//    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
//    	  .append(mPlugId)
//    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
//    	  .append(String.valueOf(command_length))
//    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
//    	  .append(command);
//		sb.append(command_base);
//		
//    	
//    	if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
//    		sendMsg(true, sb.toString(), true);
//    	} else {
//    		sendMsg(false, command_base, true);
//    	}
    	
    	mErrorMsg = getString(R.string.smartplug_oper_back2ap_fail);
		mProgress = PubFunc.createProgressDialog(PlugDetailInfoActivity.this, this.getString(R.string.str_reset_module), false);
    	mProgress.show();
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_BACK2AP)
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(PubStatus.getUserName())
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(mPlugId);    	
    	
    	sendMsg(true, sb.toString(), true);
	}

	private void Upgrade_AP() {
		sb_upgrade_progress.setProgress(0);
		//0,APPPASSTHROUGH,smli123hz,6044712,15,0,0,BACK2AP,smli123hz,6044712,3_1#
		String auxFileNo = "1024";
		String deviceType = PubFunc.getDeviceType(tv_type_select_content.getText().toString());

    	StringBuffer sb = new StringBuffer();
    	sb.append("APPUPGRADESTART")
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(PubStatus.getUserName())
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(mPlugId)
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(auxFileNo)  	
		         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
		         .append(deviceType);    // 设备类型
    	
    	sendMsg(true, sb.toString(), true);
	}
}
