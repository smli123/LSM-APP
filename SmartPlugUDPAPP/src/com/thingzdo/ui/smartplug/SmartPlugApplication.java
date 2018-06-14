package com.thingzdo.ui.smartplug;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.thingzdo.processhandler.SmartPlugEventHandler;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.wheelutils.MyAlertDialog;
import com.thingzdo.util.AppTimeoutTask;
import com.thingzdo.util.CrashHandler;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.view.View;

public class SmartPlugApplication extends Application {
    private static SmartPlugApplication sMe;
    private static boolean mLogined = false;
    
    private static Timer mCheckTimer = null;
    private static AppTimeoutTask mTimeoutTask = null;
    
    public static int mCounter = 0;
    private final static long TIMEOUT = 3600000;  //60分钟，若60分钟内没有操作，APP便自动退出。
    
    private static boolean mFirstUse = true;
    
    private List<Activity> mList = new ArrayList<Activity>(); 
    
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;
    
    @Override
    public void onCreate() {
    	super.onCreate();
    	
    	sMe = this;
    	mCheckTimer = new Timer();
    	mFirstUse = false;
    	CrashHandler.getInstance().init(getApplicationContext());  
    	SmartPlugEventHandler.getInstance().init();
    	
    	mSharedPreferences = getSharedPreferences("SmartPlug", Activity.MODE_PRIVATE);
		loadAPPConfig();
		
		// 启动语音控制
		// 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
		// 设置你申请的应用appid
		StringBuffer param = new StringBuffer();
		param.append("appid="+getString(R.string.app_id));
		param.append(",");
		// 设置使用v5+
		param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
		SpeechUtility.createUtility(SmartPlugApplication.this, param.toString());
		
	}
	
	private void loadAPPConfig() {
		PubDefine.THINGZDO_HOST_NAME = mSharedPreferences.getString("serverip", PubDefine.SERVERIP_HANGZHOU);

		// DEBUG using Shenzhen server ip.
//		PubDefine.THINGZDO_HOST_NAME = PubDefine.SERVERIP_SHENZHEN;
	}
    
    @Override
    public void onTerminate() {
    	// TODO Auto-generated method stub
    	super.onTerminate();
    }
    
    public static SmartPlugApplication getInstance() {
    	return sMe;
    }
    
	public static Context getContext(){
		return getInstance().getApplicationContext();
	}    
    
    public static boolean isLogined() {
    	return mLogined; 
    }
    
    public static void setLogined(boolean isLogined) {
    	mLogined = isLogined; 
    	if (mLogined) {
    		mCounter = 0;
    	}
    }
    
    public static void setFirstUse(boolean isFirstUse) {
    	mFirstUse = isFirstUse;	
    }
    
    public static boolean getFirstUse() {
    	return mFirstUse;
    }
    
    // add Activity  
    public void addActivity(Activity activity) { 
        mList.add(activity); 
    } 
    
    public void exit() { 
        try { 
            for (Activity activity : mList) { 
                if (activity != null && !(activity instanceof LoginActivity))
                    activity.finish(); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0); 
        } 
    } 
    
    public void finishSmartPlugActivity() {
        try { 
            for (Activity activity : mList) { 
                if ((activity instanceof SmartPlugActivity)) {
                    activity.finish();
                    break;
                }
                
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0); 
        }     	
    }
    
    
    public void onLowMemory() { 
        super.onLowMemory();     
        System.gc(); 
    }   
    
    public static void resetTask() {
    	if (null != mCheckTimer) {
    		mCheckTimer.cancel();
    	}
    	if (null != mTimeoutTask) {
    		mTimeoutTask.cancel();
    	}
    	
    	mCheckTimer = null;
    	mTimeoutTask = null;
    	mCheckTimer = new Timer();
    	mTimeoutTask = new AppTimeoutTask();
    	mCheckTimer.schedule(mTimeoutTask, TIMEOUT, TIMEOUT);
    }
    
    public static void closeTask() {
    	if (null != mCheckTimer) {
    		mCheckTimer.cancel();
    	}
    	if (null != mTimeoutTask) {
    		mTimeoutTask.cancel();
    	}
    	
    	mCheckTimer = null;
    	mTimeoutTask = null;
    }
   
}
