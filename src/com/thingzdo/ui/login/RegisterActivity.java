package com.thingzdo.ui.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.thingzdo.dataprovider.UserHelper;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.RegisterInfo;
import com.thingzdo.ui.UserDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugActivity;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class RegisterActivity extends TitledActivity 
    implements OnClickListener{
	
    private int mState = 0;
    private EditText mEdtUserName = null;
    private EditText mEdtPwd = null;
    private EditText mEdtPwdCfg = null;
    private EditText mEdtEmail = null;
    
    private ImageView mImgDelName = null;
    private ImageView mImgDelPwd = null;
    private ImageView mImgDelPwdCfg = null;
    private ImageView mImgDelEmail = null;    
    private Button    mBtnOk       = null;
       
    private RegisterInfo mInfo = new RegisterInfo();
    private UserHelper mUserHelper = null;
    private UserDefine m_CurUser = null;
    
	private BroadcastReceiver mRegisterRev = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(PubDefine.REGISTER_BROADCAST)) {
				if (null != mProgress) {
					mProgress.dismiss();
				}
				timeoutHandler.removeCallbacks(timeoutProcess);
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
				case 0:
					doBackgroundSave();
					Intent act = new Intent();
					act.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					act.setClass(RegisterActivity.this, SmartPlugActivity.class);
					RegisterActivity.this.startActivity(act);					
					break;
				default:
					PubFunc.thinzdoToast(RegisterActivity.this, message);					
					break;
				}				
			}
		}
	};  

    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState, R.layout.activity_register, false);
    	SmartPlugApplication.resetTask();
    	SmartPlugApplication.getInstance().addActivity(this);
    	
    	setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this); 
    	//setTitleRightButton(R.string.carguard_ok, R.drawable.title_btn_selector, this);
    	 
    	mUserHelper = new UserHelper(this);
		mEdtUserName = (EditText)findViewById(R.id.edtUserName);
		mEdtPwd = (EditText)findViewById(R.id.edtPwd);
		mEdtPwdCfg = (EditText)findViewById(R.id.edtConfirmPwd);
		mEdtEmail = (EditText)findViewById(R.id.edtEmail);
		mBtnOk = (Button)findViewById(R.id.register_ok);
		mBtnOk.setOnClickListener(this);
		
	    mImgDelName = (ImageView)findViewById(R.id.image_delname);
	    mImgDelName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mEdtUserName.setText("");	
			}
		});
	    
	    mImgDelPwd = (ImageView)findViewById(R.id.image_delpwd);
	    mImgDelPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mEdtPwd.setText("");	
			}
		});	    
	    mImgDelPwdCfg = (ImageView)findViewById(R.id.image_delconfirmpwd);
	    mImgDelPwdCfg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mEdtPwdCfg.setText("");	
			}
		});	    
	    mImgDelEmail = (ImageView)findViewById(R.id.image_delemail); 
	    mImgDelEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mEdtEmail.setText("");	
			}
		});	    
		
		m_CurUser = new UserDefine();
		
		updateEdit(mEdtUserName, mImgDelName);
		updateEdit(mEdtPwd, mImgDelPwd);
		updateEdit(mEdtPwdCfg, mImgDelPwdCfg);
		updateEdit(mEdtEmail, mImgDelEmail);
		
		mEdtUserName.addTextChangedListener(nameTxtWatcher);
		mEdtPwd.addTextChangedListener(pwdTxtWatcher);
		mEdtPwdCfg.addTextChangedListener(pwdcfgTxtWatcher);
		mEdtEmail.addTextChangedListener(emailTxtWatcher);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.REGISTER_BROADCAST);
		filter.addAction(PubDefine.LOGIN_BROADCAST);
		registerReceiver(mRegisterRev, filter);		
    }
	
	private TextWatcher nameTxtWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable edt) {
			updateEdit(mEdtUserName, mImgDelName);	
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
	
	private TextWatcher pwdTxtWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
			updateEdit(mEdtPwd, mImgDelPwd);
			
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
	
	private TextWatcher pwdcfgTxtWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
			updateEdit(mEdtPwdCfg, mImgDelPwdCfg);
			
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
	
	private TextWatcher emailTxtWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
			updateEdit(mEdtEmail, mImgDelEmail);
			
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
		unregisterReceiver(mRegisterRev);
	}
	
	private boolean checkInput() {
		String error = null;
		
		if (mEdtUserName.getText().toString().isEmpty()) {
		    PubFunc.thinzdoToast(this, getString(R.string.register_info_nousername));
		    return false;					
		}

		if (mEdtPwd.getText().toString().isEmpty()) {
		    PubFunc.thinzdoToast(this, getString(R.string.register_info_nopwd));
		    return false;					
		}
		
		error = PubFunc.isPasswordValid(this, mEdtPwd.getText().toString());
		if (null != error) {
			PubFunc.thinzdoToast(this, error);
			return false;
		}
		
		if (mEdtPwdCfg.getText().toString().isEmpty()) {
		    PubFunc.thinzdoToast(this, getString(R.string.register_info_noconfirmpwd));
		    return false;					
		}				
	
		if (false == mEdtEmail.getText().toString().isEmpty() && 
				false == PubFunc.isEmailValid(mEdtEmail.getText().toString())) {
			PubFunc.thinzdoToast(this, getString(R.string.register_info_invalidemail));
		    return false;			
		}
		
		if (!mEdtPwd.getText().toString()
				.equals(mEdtPwdCfg.getText().toString())) {
			PubFunc.thinzdoToast(this, getString(R.string.register_info_pwdconfirmerror));
		    return false;					
		}
		
		//��������һ�email��û�����룬������
		if (mEdtEmail.getText().toString().isEmpty()) {
		    PubFunc.thinzdoToast(this, getString(R.string.register_info_nofindpwdandemail));
		    return false;						
		}	
		
		if (mEdtUserName.getText().toString().contains("#")) {
		    PubFunc.thinzdoToast(this, getString(R.string.smartplug_nosharp_username));
		    return false;				
		}
		
		if (mEdtPwd.getText().toString().contains("#") || 
				mEdtPwdCfg.getText().toString().contains("#")) {
		    PubFunc.thinzdoToast(this, getString(R.string.smartplug_nosharp_pwd));
		    return false;				
		}	
		
		if (mEdtEmail.getText().toString().contains("#")) {
		    PubFunc.thinzdoToast(this, getString(R.string.smartplug_nosharp_email));
		    return false;				
		}			
		
		return true;
	}
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	    case R.id.titlebar_leftbutton:
	    	finish();
	        break;	
	    case R.id.titlebar_rightbutton:
    		if (false == checkInput()) {
    			return;
    		} 	    	
	    	doConnectServer();
	    	break;
	    case R.id.register_ok:
    		if (false == checkInput()) {
    			return;
    		} 	    	
			if (false == PubFunc.isNetworkAvailable(RegisterActivity.this)) {
				PubFunc.thinzdoToast(RegisterActivity.this, getString(R.string.login_pwd_network_invalid).toString());
				return;
			} else {
            	//new Thread(runnable).start();
				doConnectServer();
			}    		
	    	break;
		default:
			break;				
		}
	}
	
    private Handler doRegisterHander = new Handler() {
        public void handleMessage(Message msg) {
       		register();
        };	
    };	
	
	private void register() {
		m_CurUser.mName = mEdtUserName.getText().toString().trim();
		m_CurUser.mPwd =  mEdtPwd.getText().toString().trim();
		m_CurUser.mEMail = mEdtEmail.getText().toString().trim();

		mInfo.mUserName = m_CurUser.mName;
		mInfo.mPassword = m_CurUser.mPwd;
		mInfo.mState = mState;

		if (1 == mState) {
			SmartPlugApplication.setFirstUse(true);
		}

		String version = PubFunc.getAppVersion();
		
    	StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_REGISTER)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
  	  	  .append(mEdtUserName.getText().toString())
  	  	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)    	  
  	  	  .append(version)
  	  	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
  	  	  .append(mEdtPwd.getText().toString())
  	  	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
  	  	  .append(mEdtEmail.getText().toString());
		
    	sendMsg(false, sb.toString(), true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
    	Intent intent = getIntent();
    	mState = intent.getIntExtra("State", 1);
    	
    	if (1 == mState) {
    		setTitle(R.string.register_title_register);
    	} else {
    		setTitle(R.string.register_title_modify);  		
    		UserDefine user = mUserHelper.getUser(PubStatus.g_CurUserName);
    	    if (null != user) {
    	    	mImgDelName.setVisibility(View.GONE);
    	    	mEdtUserName.setEnabled(false);
    	    	mEdtUserName.setClickable(false);
    	    	mEdtUserName.setText(user.mName);
    	    	mEdtUserName.setBackgroundColor(Color.GRAY);
    	    	mEdtPwd.setText(user.mPwd);
    	    	mEdtPwdCfg.setText(user.mPwd);
    	    	mEdtEmail.setText(user.mEMail);
    	    }
    	}		
	}
	
	private void doBackgroundSave() {
		new AsyncTask<Void, Integer, Void>() {
            protected void onPreExecute() {

            };
			
			@Override
			protected Void doInBackground(Void... arg) {
	    		mUserHelper.deleteUser(m_CurUser.mName);
	    		mUserHelper.addUser(m_CurUser);
	    		PubStatus.g_CurUserName = m_CurUser.mName;
				return null;
			}
			
			protected void onPostExecute(Void result) {				
			};
			
		}.execute();
	}
	
/*	private void updateEdit(EditText edt, ImageView img) {
		if (edt.getText().toString().isEmpty()) {
			img.setVisibility(View.INVISIBLE);
		} else {
			img.setVisibility(View.VISIBLE);
		}		
	}*/	
	
	private void doConnectServer() {
		AsyncTask<Void,Void,Void> connect = new AsyncTask<Void,Void,Void>() {
            @Override
            protected void onPreExecute() {
            	super.onPreExecute();
            	mProgress = PubFunc.createProgressDialog(RegisterActivity.this, 
            			getString(R.string.register_register_now), false);
            	mProgress.show(); 
            }
			@Override
			protected Void doInBackground(Void... arg0) {
				doRegisterHander.sendMessage(doRegisterHander.obtainMessage(1, ""));
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
