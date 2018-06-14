package com.thingzdo.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class WelcomeActivity extends Activity {
    private TextView mBtn = null;
    private Handler mHandler = null;
    
    private String DEFAULT_AUTOLOGIN = "true";
    
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
	        //启动应用，参数为需要自动启动的应用的包名
	        String packageName = "com.thingzdo.smartplug_udp";
		    Intent intent2 = getPackageManager().getLaunchIntentForPackage(packageName); 
		    context.startActivity(intent2);
		}
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//    	PubFunc.log("welcome start");
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	SmartPlugApplication.getInstance().addActivity(this);
    	super.setContentView(R.layout.activity_welcome);
    	mBtn = (TextView) findViewById(R.id.btn_welcome);
    	mHandler = new Handler();
    	
		/*if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }*/     	

    	mBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mHandler.removeCallbacks(startRunnable);
				start();
			}
		});
    	
    	if (PubDefine.AUTO_RUN_SMARTPLUG == true) {
			IntentFilter filter = new IntentFilter();
			filter.addAction("android.intent.action.BOOT_COMPLETED");
			registerReceiver(mBroadcastReceiver, filter);
    	}
    	
    	Intent intent = getIntent();
    	boolean autologin = intent.getBooleanExtra("AUTOLOGIN", false);
    	
    	if (autologin == true) {
    		Intent it = new Intent();
    		it.putExtra("AUTO_LOGIN", "true");
    		it.putExtra("LOGIN_SHOW", "false");
    		it.setClass(WelcomeActivity.this, LoginActivity.class);
    		startActivity(it); 
    		finish();
    		
    	} else {
    		mHandler.postDelayed(startRunnable, 1000);
    	}
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (PubDefine.AUTO_RUN_SMARTPLUG == true) {
    		unregisterReceiver(mBroadcastReceiver);
    	}
    }
    
    private Runnable startRunnable = new Runnable() {

		@Override
		public void run() {
			start();
		}
    };
    
    private void start() {
		Intent it = new Intent();
		it.putExtra("AUTO_LOGIN", DEFAULT_AUTOLOGIN);
		it.putExtra("LOGIN_SHOW", "true");
		it.setClass(WelcomeActivity.this, LoginActivity.class);
		startActivity(it); 
		finish();
    }
}
