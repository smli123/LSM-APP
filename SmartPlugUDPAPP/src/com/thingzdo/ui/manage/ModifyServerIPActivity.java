package com.thingzdo.ui.manage;

import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class ModifyServerIPActivity extends TitledActivity
    implements OnClickListener {
	
	private RadioGroup  rg_serverip;
	private RadioButton rb_server_hz;
	private RadioButton rb_server_sz;
	private RadioButton rb_server_debug;
	
	private Button      btn_finish;
	
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;
	
	private String serverip = PubDefine.SERVERIP_HANGZHOU;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_modify_serverip, false);
		SmartPlugApplication.getInstance().addActivity(this);
		
		rg_serverip = (RadioGroup) findViewById(R.id.rg_serverip);
		rb_server_hz = (RadioButton) findViewById(R.id.rb_server_hz);
		rb_server_sz = (RadioButton) findViewById(R.id.rb_server_sz);
		rb_server_debug = (RadioButton) findViewById(R.id.rb_server_debug);
		
		btn_finish  = (Button) findViewById(R.id.btn_finish);
		btn_finish.setOnClickListener(this);
		
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		setTitle(R.string.app_modify_serverip);

		mSharedPreferences = getSharedPreferences("SmartPlug", Activity.MODE_PRIVATE);
		loadServerIP();
		
		// set radio button
		rb_server_hz.setChecked(false);
		rb_server_sz.setChecked(false);
		rb_server_debug.setChecked(false);
		if (serverip.equals(PubDefine.SERVERIP_HANGZHOU)) {
			rb_server_hz.setChecked(true);
		} else if (serverip.equals(PubDefine.SERVERIP_SHENZHEN)) {
			rb_server_sz.setChecked(true);
		} else if (serverip.equals(PubDefine.SERVERIP_DEBUG)) {
			rb_server_debug.setChecked(true);
		}
	}

	@Override
	public void onClick(View v) {
		if (R.id.titlebar_leftbutton == v.getId()) {
			finish();
		} else if (R.id.btn_finish == v.getId()) {
			saveServerIP();
			
			if (PubDefine.THINGZDO_HOST_NAME.equals(serverip) != true) {
				PubDefine.THINGZDO_HOST_NAME = serverip;
				// 注销，退出；重新登录；
				logout();
				Intent intent = new Intent();
	        	intent.setClass(this, LoginActivity.class);
	        	startActivity(intent);
			}
			finish();
		}
		
	}
	
	// 注销，重新登录
	private void logout() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_LOGINOUT).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(PubStatus.g_CurUserName);
    	sendMsg(true, sb.toString(), false);
	}	
	
	private void saveServerIP() {
		editor = mSharedPreferences.edit();

		if (rb_server_sz.isChecked() == true) {
			serverip = PubDefine.SERVERIP_SHENZHEN;
		} else if (rb_server_hz.isChecked() == true) {
			serverip = PubDefine.SERVERIP_HANGZHOU;
		} else if (rb_server_debug.isChecked() == true) {
			serverip = PubDefine.SERVERIP_DEBUG;
		}
		editor.putString("serverip", serverip);

		editor.commit();
	}
	
	private void loadServerIP() {
		serverip = mSharedPreferences.getString("serverip", PubDefine.SERVERIP_HANGZHOU);
	}
	  
}
