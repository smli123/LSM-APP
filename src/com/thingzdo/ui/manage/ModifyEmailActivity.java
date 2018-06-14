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

public class ModifyEmailActivity extends TitledActivity implements
		OnClickListener {

	//private EditText oldEmail;
	private EditText newEmail;
	//private ImageView delOldEmail;
	private ImageView delNewEmail;	
	private Button btnOK;
	private BroadcastReceiver mModifyEmailRev = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(PubDefine.USER_MODIFY_EMAIL)) {

				int ret = intent.getIntExtra("RESULT", 0);
				switch (ret) {
				case 0:
					PubStatus.g_userEmail = newEmail.getText().toString();
					timeoutHandler.removeCallbacks(timeoutProcess);
					PubFunc.thinzdoToast(
							ModifyEmailActivity.this,
							context.getString(R.string.app_mod_email_ok));
					ModifyEmailActivity.this.finish();
					break;
				case 1:
					timeoutHandler.removeCallbacks(timeoutProcess);
					PubFunc.thinzdoToast(
							ModifyEmailActivity.this,
							context.getString(R.string.app_mod_email_fail));
					break;
				case AppServerReposeDefine.Cmd_Unknown:
					PubFunc.thinzdoToast(
							ModifyEmailActivity.this,
							SmartPlugApplication.getContext().getString(
									R.string.login_timeout));
					break;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_modify_email,
				false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		this.setTitle(R.string.app_modify_email);
		
		//oldEmail = (EditText) this.findViewById(R.id.edit_old_email);
		newEmail = (EditText) this.findViewById(R.id.edit_new_email);
		
		//oldEmail.setText(PubStatus.g_userEmail);
		
		/*delOldEmail = (ImageView) findViewById(R.id.image_deloldemail);
		delOldEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				oldEmail.setText("");	
			}
		});
		*/		
		
		delNewEmail = (ImageView) findViewById(R.id.image_delnewemail);
		delNewEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				newEmail.setText("");	
			}
		});
		
		//updateEdit(oldEmail, delOldEmail);
		updateEdit(newEmail, delNewEmail);	
		
		//oldEmail.addTextChangedListener(oldEmailTxtWatcher);
		newEmail.addTextChangedListener(newEmailTxtWatcher);		

		btnOK = (Button) this.findViewById(R.id.btn_finish);
		btnOK.setOnClickListener(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.USER_MODIFY_EMAIL);
		this.registerReceiver(mModifyEmailRev, filter);
	}
	
	private TextWatcher newEmailTxtWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable edt) {
			updateEdit(newEmail, delNewEmail);	
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
		this.unregisterReceiver(mModifyEmailRev);
	}

	private boolean checkOldEmail() {
		/*if (oldEmail.getText().toString().isEmpty()) {
			PubFunc.thinzdoToast(this, "�ף�û������ԭ����Ŷ");
			return false;
		}*/
		/*if (!oldEmail.getText().toString().equals(PubStatus.g_userEmail)) {
			PubFunc.thinzdoToast(this, "�ף�ԭ������Ĳ���Ŷ");
			return false;
		}*/
		return true;
	}

	private boolean checkNewEmail() {
		if (newEmail.getText().toString().isEmpty()) {
			PubFunc.thinzdoToast(this, this.getString(R.string.app_mod_email_nonewemail));
			return false;
		}
		
		if (newEmail.getText().toString().contains("#")) {
		    PubFunc.thinzdoToast(this, getString(R.string.smartplug_nosharp_email));
		    return false;				
		}		

		//String strOld = oldEmail.getText().toString();
		String strNew = newEmail.getText().toString();
		if (strNew.equals(PubStatus.g_userEmail)) {
			PubFunc.thinzdoToast(this, this.getString(R.string.app_mod_email_samemail));
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
			if (checkOldEmail() && checkNewEmail()) {
				// �ύ������ �޸�Email
				doConnectServer();
			}
			break;
		}
	}

	private void modfiyEmail() {
		String userName = PubStatus.g_CurUserName;
		String strNewEmail = this.newEmail.getText().toString();

		StringBuilder sb = new StringBuilder();
		sb.append(SmartPlugMessage.CMD_SP_MODEMAIL).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(userName).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(strNewEmail);
		sendMsg(true, sb.toString(), true);
	}

	private Runnable timeoutProcess = new Runnable() {

		@Override
		public void run() {
			PubFunc.thinzdoToast(ModifyEmailActivity.this, SmartPlugApplication
					.getContext().getString(R.string.login_timeout));
		}

	};

	private Handler doModEmailHander = new Handler() {
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				modfiyEmail();
			} else if (0 == msg.what) {
				PubFunc.thinzdoToast(
						ModifyEmailActivity.this,
						SmartPlugApplication.getContext().getString(
								R.string.login_cmd_socket_timeout));
			} else {
				PubFunc.thinzdoToast(
						ModifyEmailActivity.this,
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
						ModifyEmailActivity.this,
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
				//	doModEmailHander.sendMessage(doModEmailHander
				//			.obtainMessage(0, ""));
				//} else {
					doModEmailHander.sendMessage(doModEmailHander
							.obtainMessage(1, ""));
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
