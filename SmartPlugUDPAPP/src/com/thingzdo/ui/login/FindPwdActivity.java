package com.thingzdo.ui.login;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class FindPwdActivity extends TitledActivity { 
	private Button mBtnOk   = null;
	private EditText mEmail = null;
	private ImageView mDelEmail = null;	
	
	private BroadcastReceiver mRestPwdRev = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(PubDefine.FINDPWD_BROADCAST)) {
				if (null != mProgress) {
					mProgress.dismiss();
				}
				timeoutHandler.removeCallbacks(timeoutProcess);
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
				case 0:
					PubFunc.thinzdoToast(FindPwdActivity.this, 
							SmartPlugApplication.getContext().getString(R.string.smartplug_login_getpwd));
					
					
					break;
				default:
					PubFunc.thinzdoToast(SmartPlugApplication.getContext(), message);
					break;
				}
			}
		}
	}; 	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState, R.layout.activity_find_pwd_req, false);
    	SmartPlugApplication.resetTask();
    	SmartPlugApplication.getInstance().addActivity(this);   	
    	
    	setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, cancel);
		setTitle(R.string.login_pwd_findback);
		
		mBtnOk = (Button)findViewById(R.id.findpwd_ok);
		mBtnOk.setOnClickListener(findPwd);
		
		mEmail = (EditText)findViewById(R.id.login_userpwd);
		
		mDelEmail = (ImageView)findViewById(R.id.findpwd_delete_email);
		mDelEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mEmail.setText("");
				
			}
		});	
		
		mEmail.addTextChangedListener(pwdTextWatcher);
		updateEdit(mEmail, mDelEmail);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.FINDPWD_BROADCAST);
		registerReceiver(mRestPwdRev, filter);			
		
    }
	
	private TextWatcher pwdTextWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
			updateEdit(mEmail, mDelEmail);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}

		
	};	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	OnClickListener findPwd = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (false == checkInput()) {
				return;
			}
			
			if (false == PubFunc.isNetworkAvailable(FindPwdActivity.this)) {
				PubFunc.thinzdoToast(FindPwdActivity.this, getString(R.string.login_pwd_network_invalid).toString());
				return;
			} else {
            	//new Thread(runnable).start();
				doConnectServer();
			}			
		}
	};
	
	
	private void findPwd() {
		StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_FINDPWD)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(PubStatus.g_CurUserName)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(mEmail.getText().toString());    	

    	sendMsg(false, sb.toString(), true);
	}
	
    private Handler doFindHander = new Handler() {
        public void handleMessage(Message msg) {
        	if (1 == msg.what) {
        		findPwd();
        	} else if (0 == msg.what) {
        		PubFunc.thinzdoToast(FindPwdActivity.this, 
    					SmartPlugApplication.getContext().getString(R.string.login_cmd_socket_timeout));       		
        	} else {
        		PubFunc.thinzdoToast(FindPwdActivity.this, 
    					SmartPlugApplication.getContext().getString(R.string.login_cmd_socket_timeout));         		
        	}
        };	
    };	
	

	OnClickListener cancel = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			finish();
		}
		
	};	
	
	private boolean checkInput() {
	
		if (TextUtils.isEmpty(mEmail.getText().toString())) {
			PubFunc.thinzdoToast(this, getString(R.string.login_info_no_email));
			return false;
		}
		
		if (false == PubFunc.isEmailValid(mEmail.getText().toString())) {
			PubFunc.thinzdoToast(this, getString(R.string.register_info_invalidemail));
		    return false;			
		}		
		
		return true;
	}
	
	private void doConnectServer() {
		AsyncTask<Void,Void,Void> connect = new AsyncTask<Void,Void,Void>() {
            @Override
            protected void onPreExecute() {
            	super.onPreExecute();
            	mProgress = PubFunc.createProgressDialog(FindPwdActivity.this, 
            			getString(R.string.smartplug_login_findpwd), false);
            	mProgress.show();
            }
			@Override
			protected Void doInBackground(Void... arg0) {
	    		doFindHander.sendMessage(doFindHander.obtainMessage(1, ""));
		    	return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
					
			}
			
		};
		connect.execute();	
	}
}
