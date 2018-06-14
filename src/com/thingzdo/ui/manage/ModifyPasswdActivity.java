package com.thingzdo.ui.manage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.thingzdo.internet.AsyncResult;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.smartplug.AppServerReposeDefine;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.util.ThingzdoCheckBox;

public class ModifyPasswdActivity extends TitledActivity implements
		OnClickListener {

	//private EditText oldPwd;
	private EditText newPwd1;
	private EditText newPwd2;
	private ThingzdoCheckBox checkPwd = null;
	
    //private ImageView mImgDelOldPwd = null;
    private ImageView mImgDelNewPwd = null;
    private ImageView mImgDelCfmPwd = null;	

	private Button btnOK;

	private BroadcastReceiver mModifyPasswdRev = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(PubDefine.USER_MODIFY_PASSWORD)) {

				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
				case 0:
					PubStatus.g_userPwd = newPwd1.getText().toString();
					timeoutHandler.removeCallbacks(timeoutProcess);
					PubFunc.thinzdoToast(
							ModifyPasswdActivity.this,
							SmartPlugApplication.getContext().getString(
									R.string.app_mod_pwd_ok));
					ModifyPasswdActivity.this.finish();
					break;
				case 1:
					PubFunc.thinzdoToast(SmartPlugApplication.getContext(),
							message);
					timeoutHandler.removeCallbacks(timeoutProcess);
					PubFunc.thinzdoToast(
							ModifyPasswdActivity.this,
							SmartPlugApplication.getContext().getString(
									R.string.app_mod_pwd_fail));
					break;
				case AppServerReposeDefine.Cmd_Unknown:
					PubFunc.thinzdoToast(
							ModifyPasswdActivity.this,
							SmartPlugApplication.getContext().getString(
									R.string.login_timeout));
					break;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_modify_pwd, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		// setContentView(R.layout.activity_add_socket2);
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		this.setTitle(R.string.app_modify_pwd);

		//oldPwd = (EditText) this.findViewById(R.id.edit_old_pwd);
		newPwd1 = (EditText) this.findViewById(R.id.edit_new_pwd1);
		newPwd2 = (EditText) this.findViewById(R.id.edit_new_pwd2);
		//mImgDelOldPwd = (ImageView) findViewById(R.id.image_deloldpwd);
		/*mImgDelOldPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				oldPwd.setText("");	
			}
		});
		*/
		mImgDelNewPwd = (ImageView) findViewById(R.id.image_delnewpwd);
		mImgDelNewPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				newPwd1.setText("");	
			}
		});		
		mImgDelCfmPwd = (ImageView) findViewById(R.id.image_delconfirmpwd);
		mImgDelCfmPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				newPwd2.setText("");	
			}
		});	
		//updateEdit(oldPwd, mImgDelOldPwd);
		updateEdit(newPwd1, mImgDelNewPwd);
		updateEdit(newPwd2, mImgDelCfmPwd);
		
		//oldPwd.addTextChangedListener(oldPwdTxtWatcher);
		newPwd1.addTextChangedListener(newPwdTxtWatcher);
		newPwd2.addTextChangedListener(cfmPwdTxtWatcher);		
		
		
		btnOK = (Button) this.findViewById(R.id.btn_finish);
		btnOK.setOnClickListener(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.USER_MODIFY_PASSWORD);
		this.registerReceiver(mModifyPasswdRev, filter);

		checkPwd = (ThingzdoCheckBox) findViewById(R.id.chkViewPwd);
		checkPwd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				boolean checked = checkPwd.isChecked(); 
						
				if (checked) {
					//oldPwd.setTransformationMethod(HideReturnsTransformationMethod
					//		.getInstance());
					newPwd1.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
					newPwd2.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
				} else {
					//oldPwd.setTransformationMethod(PasswordTransformationMethod
					//		.getInstance());
					newPwd1.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
					newPwd2.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
				}
			}
		});
	}
	
	private TextWatcher newPwdTxtWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable edt) {
			updateEdit(newPwd1, mImgDelNewPwd);	
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
	
	private TextWatcher cfmPwdTxtWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable edt) {
			updateEdit(newPwd2, mImgDelCfmPwd);	
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
		this.unregisterReceiver(mModifyPasswdRev);
	}

	private boolean checkOldPasswd() {
		/*if (oldPwd.getText().toString().isEmpty()) {
			PubFunc.thinzdoToast(this, "�ף�û������ԭ����Ŷ");
			return false;
		}*/
		return true;
	}

	private boolean checkNewPasswd() {
		if (newPwd1.getText().toString().isEmpty()) {
			PubFunc.thinzdoToast(this, this.getString(R.string.app_mod_pwd_nonewpwd));
			return false;
		}
		if (newPwd2.getText().toString().isEmpty()) {
			PubFunc.thinzdoToast(this, this.getString(R.string.app_mod_pwd_noconfirmpwd));
			return false;
		}
		
		if (newPwd1.getText().toString().contains("#") || 
				newPwd2.getText().toString().contains("#")) {
		    PubFunc.thinzdoToast(this, getString(R.string.smartplug_nosharp_pwd));
		    return false;				
		}		
		
		String strPwd1 = newPwd1.getText().toString();
		String strPwd2 = newPwd2.getText().toString();
		if (!strPwd1.equals(strPwd2)) {
			PubFunc.thinzdoToast(this, this.getString(R.string.app_mod_pwd_differentpwd));
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		switch (view.getId()) {
		case R.id.titlebar_leftbutton:
			finish();
			break;
		case R.id.btn_finish:
			if (checkOldPasswd() && checkNewPasswd()) {
				// �ύ������ �޸�����
				doConnectServer();
			}
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void modfiyPasswd() {
		String userName = PubStatus.g_CurUserName;
		String strOldPwd = PubStatus.g_userPwd;
		String strNewPwd = this.newPwd1.getText().toString();


		StringBuilder sb = new StringBuilder();
		sb.append(SmartPlugMessage.CMD_SP_MODPWD).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(userName).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(strOldPwd).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(strNewPwd);		

		sendMsg(true, sb.toString(), true);
	}

	private Runnable timeoutProcess = new Runnable() {

		@Override
		public void run() {

			PubFunc.thinzdoToast(
					ModifyPasswdActivity.this,
					SmartPlugApplication.getContext().getString(
							R.string.login_timeout));
		}

	};

	private Handler doModPwdHander = new Handler() {
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				modfiyPasswd();
			} else if (0 == msg.what) {
				PubFunc.thinzdoToast(
						ModifyPasswdActivity.this,
						SmartPlugApplication.getContext().getString(
								R.string.login_cmd_socket_timeout));
			} else {
				PubFunc.thinzdoToast(
						ModifyPasswdActivity.this,
						SmartPlugApplication.getContext().getString(
								R.string.login_cmd_socket_timeout));
			}
		};
	};

	private Handler timeoutHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == AppServerReposeDefine.Socket_Connect_FAIL) {
				AsyncResult ret = (AsyncResult) msg.obj;
				Log.e("socketExceptionHandler", ret.mMessage);
				PubFunc.thinzdoToast(
						ModifyPasswdActivity.this,
						SmartPlugApplication.getContext().getString(
								R.string.login_timeout));
			}
		};
	};

	private void doConnectServer() {
		AsyncTask<Void, Void, Void> connect = new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();

			}

			@Override
			protected Void doInBackground(Void... arg0) {
				//if (null == PubDefine.global_socket) {
				//	doModPwdHander.sendMessage(doModPwdHander.obtainMessage(0,
				//			""));
				//} else {
					doModPwdHander.sendMessage(doModPwdHander.obtainMessage(1,
							""));
				//}
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
