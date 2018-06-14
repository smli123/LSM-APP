package com.thingzdo.ui.manage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class AccountSecurityActivity extends TitledActivity implements OnClickListener {
	private Button mBtnLogout = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_userinfo_mgr, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		setTitle(R.string.app_account_security);
		
		
		mBtnLogout = (Button)findViewById(R.id.login_out);
		mBtnLogout.setOnClickListener(this);
		
		RelativeLayout layoutModifyPwd = (RelativeLayout) findViewById(R.id.lay_modify_pwd);
		layoutModifyPwd.setOnClickListener(this);
		
		RelativeLayout layoutModifyEmail = (RelativeLayout) findViewById(R.id.lay_modify_email);
		layoutModifyEmail.setOnClickListener(this);	
		
		RelativeLayout layoutModifyServerIP = (RelativeLayout) findViewById(R.id.lay_modify_serverip);
		layoutModifyServerIP.setOnClickListener(this);	
		layoutModifyServerIP.setVisibility(View.GONE);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TextView txtEmail = (TextView)findViewById(R.id.txt_modify_email);
		txtEmail.setText(PubStatus.g_userEmail);		
		
		TextView txtServerIP = (TextView)findViewById(R.id.txt_modify_serverip);
		String server_ip = "";
		if (PubDefine.THINGZDO_HOST_NAME.equals(PubDefine.SERVERIP_SHENZHEN)) {
			server_ip = getString(R.string.smartplug_serverip_shenzhen);
		} else if (PubDefine.THINGZDO_HOST_NAME.equals(PubDefine.SERVERIP_HANGZHOU)) {
			server_ip = getString(R.string.smartplug_serverip_hangzhou);
		} else if (PubDefine.THINGZDO_HOST_NAME.equals(PubDefine.SERVERIP_DEBUG)) {
			server_ip = getString(R.string.smartplug_serverip_debug);
		}
		
		txtServerIP.setText(server_ip);		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:
			finish();
			break;
		case R.id.login_out:
			logout();
        	intent = new Intent();
        	intent.setClass(this, LoginActivity.class);
        	startActivity(intent);
        	finish();
			break;
        case R.id.lay_modify_pwd:
        	intent = new Intent();
        	intent.setClass(this, ModifyPasswdActivity.class);
        	startActivity(intent);
        	break;
        case R.id.lay_modify_email:
        	intent = new Intent();
        	intent.setClass(this, ModifyEmailActivity.class);
        	startActivity(intent);
        	break;	
        case R.id.lay_modify_serverip:
        	intent = new Intent();
        	intent.setClass(this, ModifyServerIPActivity.class);
        	startActivity(intent);
        	break;
		}
	}
	
	private void logout() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_LOGINOUT).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(PubStatus.g_CurUserName);
    	sendMsg(true, sb.toString(), false);
	}	
}
