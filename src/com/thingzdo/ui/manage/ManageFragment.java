package com.thingzdo.ui.manage;

import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.SmartPlugFragment;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class ManageFragment extends SmartPlugFragment  implements View.OnClickListener{
	//private static ManageFragment mFragment = null;
	
	private TextView tvUserName = null;
	
	private Button mBtnLogout = null;
	private static ManageFragment mFragment = null;
	
    /*public static ManageFragment newInstance() {
    	if (null == mFragment) {
    		mFragment = new ManageFragment();
    	}
        return mFragment;
    }*/	
	
	private Handler mHandler = null;
	//public ManageFragment(Handler handler) {
	//	mHandler = handler;	
	//}
	
	
	public void setHandler(Handler handler) {
			mHandler = handler;	
	}
	
    public static ManageFragment newInstance() {
    	if (null == mFragment) {
    		mFragment = new ManageFragment();
    	}
        return mFragment;
    }
    
    public static void delete() {
    	if (null != mFragment) {
    		mFragment = null;
    	}
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreateView(inflater, container, savedInstanceState);
		mFragmentView = inflater.inflate(R.layout.fragment_manage, container, false);
		
		RelativeLayout layout1 = (RelativeLayout) mFragmentView.findViewById(R.id.detail_person_num_layout);
		layout1.setOnClickListener(this);
		
		RelativeLayout layoutFeedback = (RelativeLayout) mFragmentView.findViewById(R.id.lay_mgr_feedback);
		layoutFeedback.setOnClickListener(this);

		RelativeLayout layoutUpdate = (RelativeLayout) mFragmentView.findViewById(R.id.lay_mgr_update);
		layoutUpdate.setOnClickListener(this);

		RelativeLayout layouthelp = (RelativeLayout) mFragmentView.findViewById(R.id.lay_mgr_help);
		layouthelp.setOnClickListener(this);
		
		// -- For Debug Version
		final RelativeLayout layoutiat = (RelativeLayout) mFragmentView.findViewById(R.id.lay_mgr_iat);
		layoutiat.setOnClickListener(this);

		RelativeLayout layoutconfig = (RelativeLayout) mFragmentView.findViewById(R.id.lay_mgr_config);
		layoutconfig.setOnClickListener(this);
		
		RelativeLayout layoutdueros = (RelativeLayout) mFragmentView.findViewById(R.id.lay_dueros_config);
		layoutdueros.setOnClickListener(this);
		
		if (true == PubDefine.RELEASE_VERSION || true) {
			layoutiat.setVisibility(View.GONE);
			layoutconfig.setVisibility(View.GONE);
			layoutdueros.setVisibility(View.GONE);
			((RelativeLayout)mFragmentView.findViewById(R.id.layout_items3)).setVisibility(View.GONE);
		}
		
		RelativeLayout layoutAboutus = (RelativeLayout) mFragmentView.findViewById(R.id.lay_mgr_about);
		layoutAboutus.setOnClickListener(this);
		
		RelativeLayout layoutShare = (RelativeLayout) mFragmentView.findViewById(R.id.lay_mgr_share);
		layoutShare.setOnClickListener(this);		
		
		TextView layoutLogout = (TextView) mFragmentView.findViewById(R.id.detail_user_logout);
		layoutLogout.setOnClickListener(this);		
		
		tvUserName = (TextView) mFragmentView.findViewById(R.id.detail_user_name);
		if(PubStatus.g_CurUserName == null || PubStatus.g_CurUserName.isEmpty() ){
			tvUserName.setText("Test");
		}else{
			tvUserName.setText(PubStatus.g_CurUserName);
		}
		
		mBtnLogout = (Button)mFragmentView.findViewById(R.id.login_out);
		mBtnLogout.setOnClickListener(this);
		
		// 是否自动启动语音控制
		if (PubDefine.AUTO_RUN_SMARTPLUG_IATDEMO == true) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					layoutiat.performClick();
				}
	    	}, 1000);
		}
		
		return mFragmentView;
	}

    @Override
	public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	SmartPlugApplication.resetTask();
    }
    
	@Override
	public void onClick(View v) {
    	
		Intent intent = null;
		switch (v.getId()) {
	        case R.id.titlebar_leftbutton:
	    	    //finish();
	        break;		
	    	
	        case R.id.detail_person_num_layout:
	        	intent = new Intent();
	        	intent.setClass(this.getActivity(), AccountSecurityActivity.class);//ModifyPasswdActivity.class);
	        	startActivity(intent);
	        	break;
	        case R.id.lay_mgr_feedback:
	        	intent = new Intent();
	        	intent.setClass(this.getActivity(), FeedbackActivity.class);
	        	startActivity(intent);	        	
	        	break;
	        case R.id.lay_mgr_help:
	        	intent = new Intent();
	        	intent.setClass(this.getActivity(), HelpActivity.class);
	        	startActivity(intent);	        	
	        	break;
	        case R.id.lay_mgr_iat:
	        	intent = new Intent();
	        	intent.setClass(this.getActivity(), IatDemo.class);
	        	startActivity(intent);	        	
	        	break;
	        case R.id.lay_mgr_config:
	        	intent = new Intent();
	        	intent.setClass(this.getActivity(), IatDemo.class);
	        	startActivity(intent);	        	
	        	break;
	        case R.id.lay_dueros_config:
	        	intent = new Intent();
	        	intent.setClass(this.getActivity(), DuerOSActivity.class);
	        	startActivity(intent);
	        	break;
	        case R.id.lay_modify_email:
	        	intent = new Intent();
	        	intent.setClass(this.getActivity(), ModifyEmailActivity.class);
	        	startActivity(intent);
	        	break;
	        case R.id.lay_mgr_about:
	        	intent = new Intent();
	        	intent.setClass(this.getActivity(), AboutUs_v2Activity.class);
	        	startActivity(intent);
	        	break;
	        case R.id.lay_mgr_update:
	            this.update();
	            break;
	        case R.id.detail_user_logout:
	        	intent = new Intent();
	        	intent.setClass(this.getActivity(), LoginActivity.class);
	        	startActivity(intent);	
	        case R.id.lay_mgr_share:
	        	break;
			case R.id.login_out:
				logout();
	        	/*intent = new Intent();
	        	//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	intent.setClass(this.getActivity(), LoginActivity.class);
	        	startActivity(intent);
	        	SmartPlugApplication.getInstance().finishSmartPlugActivity();*/
				break;	        	
		    default:
		    	break;
		}
	}	
    public void update(){
    	UmengUpdateAgent.setUpdateAutoPopup(false);
    	UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				// TODO Auto-generated method stub
				 switch (updateStatus) {
	    	        case UpdateStatus.Yes: // has update
	    	            UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
	    	            break;
	    	        case UpdateStatus.No: // has no update
	    	        	PubFunc.thinzdoToast(mContext, getString(R.string.smartplug_mgr_newver));
	    	            break;
	    	        case UpdateStatus.NoneWifi: // none wifi
	    	        	PubFunc.thinzdoToast(mContext, getString(R.string.smartplug_mgr_nowifi));
	    	            break;
	    	        case UpdateStatus.Timeout: // time out
	    	        	PubFunc.thinzdoToast(mContext, getString(R.string.smartplug_mgr_updatetimeout));
	    	            break;
	    	        }
			}
    	});
    	UmengUpdateAgent.update(mContext);
    }
    
    @Override
	public void onDestroy() {
    	super.onDestroy();
    };
    
	private void logout() {
		mHandler.sendEmptyMessage(0);
    	StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_LOGINOUT).append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(PubStatus.g_CurUserName);
    	sendMsg(true, sb.toString(), false);
	}    


}


