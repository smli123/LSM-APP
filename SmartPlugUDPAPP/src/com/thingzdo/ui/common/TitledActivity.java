package com.thingzdo.ui.common;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.thingzdo.internet.AsyncResult;
import com.thingzdo.internet.SendMsgProxy;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.smartplug.AppServerReposeDefine;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;


public abstract class TitledActivity extends Activity {
    private TextView mTitleContent = null;
    private Button mLeftButton = null;
    private Button mRightButton = null;
    private ImageView mLeftImage = null;
    private ImageView mRightImage = null;    
    protected boolean mBack2Exit = false;
    protected SmartPlugProgressDlg mProgress = null;
    
    
//    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent.getAction().equals(PubDefine.SOCKET_CONNECT_FAIL_BROADCAST)) {
//				timeoutHandler.removeCallbacks(timeoutProcess);				
//				if (null != mProgress) {
//					mProgress.dismiss();
//				}				
//			}
//		}
//    	
//    };
    
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	setupTitleViews();
    }*/
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//mView_Command.clear();
	}
	   
    protected void onCreate(Bundle savedInstanceState, int layoutResID, boolean backToExit) {
    	mBack2Exit = backToExit;
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);  
    	super.setContentView(R.layout.view_common_titlebar);
    	setContentView(layoutResID);
    	
		/*if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } */   	
    	
    	//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, layoutResID);
    	setupTitleViews();
    	
//    	IntentFilter filter = new IntentFilter();
//    	filter.addAction(PubDefine.SOCKET_CONNECT_FAIL_BROADCAST);
//		registerReceiver(mBroadcastReceiver, filter);    	
    }  
    
    protected void onCreateBaiduMap(Bundle savedInstanceState, int layoutResID, boolean backToExit) {
    	mBack2Exit = backToExit;
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	Context context = SmartPlugApplication.getContext();
    	SDKInitializer.initialize(context);
    	super.setContentView(R.layout.view_common_titlebar);
    	setContentView(layoutResID);
    	
		/*if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } */   	
    	
    	//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, layoutResID);
    	setupTitleViews();
    	
//    	IntentFilter filter = new IntentFilter();
//    	filter.addAction(PubDefine.SOCKET_CONNECT_FAIL_BROADCAST);
//		registerReceiver(mBroadcastReceiver, filter);    	
    }
    
    private void setupTitleViews() {
    	mTitleContent = (TextView)findViewById(R.id.titlebar_caption);
    	setTitle(R.string.app_name);
    	mLeftButton = (Button)findViewById(R.id.titlebar_leftbutton);
    	mRightButton = (Button)findViewById(R.id.titlebar_rightbutton);
    	
    	//mLeftImage = (ImageView)findViewById(R.id.titlebar_leftimage);
    	//mRightImage = (ImageView)findViewById(R.id.titlebar_rightimage);
    }
    
    @Override
    public void setTitle(int titleId) {
    	// TODO Auto-generated method stub
    	super.setTitle(titleId);
    	if (null != mTitleContent) {
    		mTitleContent.setText(getString(titleId));
    	}    	
    }
    
    @Override
    public void setTitle(CharSequence title) {
    	// TODO Auto-generated method stub
    	super.setTitle(title);
    	if (null != mTitleContent) {
    		mTitleContent.setText(title);
    	}    	
    }
    
    protected void setTitleLeftButton(int textResId, int bgResId,
    		final OnClickListener onClick) {
    	if (null != mLeftButton) {
    		setButtonInfo(mLeftButton, textResId, bgResId, onClick);
    	}
    }
    
    protected void setTitleRightButton(int textResId, int bgResId,
    		final OnClickListener onClick) {
    	if (null != mRightButton) {
    		setButtonInfo(mRightButton, textResId, bgResId, onClick);
    	}
    }   
    
    protected void setTitleRightButtonVisible(boolean visible) {
    	if (null != mRightButton) {
    		mRightButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    	}
    }       
    
    private void setButtonInfo(Button button, int textResId, int bgResId, OnClickListener onClick) {
    	Drawable backgroud = null;
    	CharSequence text = null;
    	if (null != button) {
    		button.setVisibility(View.VISIBLE);
    		if (0 != textResId) {
    			text = getResources().getText(textResId);	
    		}
    		if (0 != bgResId) {
    			backgroud = getResources().getDrawable(bgResId);
    		}
    		
    		setButtonInfo(button, text, backgroud, onClick);
    	}
    }    
    
    private void setButtonInfo(Button button, CharSequence text, Drawable background, OnClickListener onClick) {
    	if (null != button) {
    		button.setVisibility(View.VISIBLE);
    		if (null != text) {
    			button.setText(text);
    		}
    		if (null != background) {
    			button.setBackgroundDrawable(background);
    		}
    		button.setOnClickListener(onClick);
    	}
    }
    
    /*protected void setButtonStatus() {
    	Iterator iter = mView_Command.entrySet().iterator();  
    	while (iter.hasNext()) {  
    	    Map.Entry entry = (Map.Entry) iter.next();  
    	    Integer key = (Integer)entry.getKey();  
    	    Integer val = (Integer)entry.getValue(); 
    	    if (true == CarGuardSmsSets.isCommandSend(key.intValue())){
        		Button btn = (Button)findViewById(val.intValue());
        		if (null != btn) {
        			btn.setEnabled(false);
        			PubFunc.startFlick(btn);
        		}
    	    }
    	}    	
    }*/
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (true == mBack2Exit) {

    		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 

    			new AlertDialog.Builder(this)
    				.setMessage(this.getString(R.string.smartplug_exit))
    				.setPositiveButton(R.string.smartplug_ok, okListener)
    				.setNegativeButton(R.string.smartplug_cancel, null)
    				.setCancelable(true).show();    		
    			return true;
    		}
    	}
    	return super.onKeyDown(keyCode, event);
    } 
    
    final DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
    @Override
	    public void onClick(DialogInterface dialog, int which) {		
			Intent intent = new Intent(Intent.ACTION_MAIN);  
			intent.addCategory(Intent.CATEGORY_HOME);  
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
			startActivity(intent);  
			android.os.Process.killProcess(android.os.Process.myPid());     	    
		}
    }; 
    
	protected Handler timeoutHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	// 统一处理，首先去掉进度条
	    	if (null != mProgress) {
				mProgress.dismiss();
			}
    		this.removeCallbacks(timeoutProcess);
    		
	    	if (msg.what == AppServerReposeDefine.Socket_Connect_FAIL) {
	    		AsyncResult ret = (AsyncResult)msg.obj;
	    		Log.e("socketExceptionHandler", ret.mMessage);
//	    		SmartPlugApplication.getContext().sendBroadcast(new Intent(PubDefine.SOCKET_CONNECT_FAIL_BROADCAST));
//	    		String error = ret.mMessage;
	    		String error = SmartPlugApplication.getContext().getString(R.string.login_timeout);
	    		if (PubDefine.g_Connect_Mode != PubDefine.SmartPlug_Connect_Mode.Internet) {
	    			error = SmartPlugApplication.getContext().getString(R.string.oper_error);
	    		}
	    		PubFunc.thinzdoToast(SmartPlugApplication.getContext(), error);
	    		
	    	} else if (msg.what == AppServerReposeDefine.Socket_Send_Fail) {
	    		AsyncResult ret = (AsyncResult)msg.obj;
	    		Log.e("socketExceptionHandler", ret.mMessage);
//	    		SmartPlugApplication.getContext().sendBroadcast(new Intent(PubDefine.SOCKET_CONNECT_FAIL_BROADCAST));
//	    		String error = ret.mMessage;
	    		String error = SmartPlugApplication.getContext().getString(R.string.login_timeout);
	    		if (PubDefine.g_Connect_Mode != PubDefine.SmartPlug_Connect_Mode.Internet) {
	    			error = SmartPlugApplication.getContext().getString(R.string.oper_error);
	    		}
	    		PubFunc.thinzdoToast(SmartPlugApplication.getContext(), error);
	    		
	    	} else if (msg.what == AppServerReposeDefine.Socket_Send_OK) {
//	    		if (null != mProgress) {
//					mProgress.dismiss();
//				}
//	    		this.removeCallbacks(timeoutProcess);
//	    		PubFunc.thinzdoToast(SmartPlugApplication.getContext(), "Send OK");
	    		
	    	} else if (msg.what == AppServerReposeDefine.Socket_TCP_TIMEOUT) {
//	    		if (null != mProgress) {
//					mProgress.dismiss();
//				}
//	    		this.removeCallbacks(timeoutProcess);
	    		String error = SmartPlugApplication.getContext().getString(R.string.login_cmd_socket_timeout_devices);
	    		PubFunc.thinzdoToast(SmartPlugApplication.getContext(), error);
	    		
	    	} else {
	    		//do nothing...
	    	}
	    	
	    };	
	}; 
	
	protected Runnable timeoutProcess = new Runnable() {

		@Override
		public void run() {
			if (null != mProgress) {
				mProgress.dismiss();
			}	
    		String error = SmartPlugApplication.getContext().getString(R.string.login_timeout);
    		if (PubDefine.g_Connect_Mode != PubDefine.SmartPlug_Connect_Mode.Internet) {
    			error = SmartPlugApplication.getContext().getString(R.string.oper_error);
    		}
    		PubFunc.thinzdoToast(SmartPlugApplication.getContext(), error);
		}
		
	};

    protected void updateEdit(EditText edt, ImageView img){
		if (edt.getText().toString().isEmpty()) {
			img.setVisibility(View.INVISIBLE);
		} else {
			img.setVisibility(View.VISIBLE);
		}    	
    }	
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
//    	unregisterReceiver(mBroadcastReceiver);
    }
    
    protected void setTitleLeftImage(int bgResId,
    		final OnClickListener onClick) {
    	if (null != mLeftImage) {
    		setImageInfo(mLeftImage, bgResId, onClick);
    	}
    }
    
    protected void setTitleRightImage(int bgResId,
    		final OnClickListener onClick) {
    	if (null != mRightImage) {
    		setImageInfo(mRightImage, bgResId, onClick);
    	}
    } 
    
    private void setImageInfo(ImageView image, int bgResId, OnClickListener onClick) {
    	if (null != image) {
    		image.setVisibility(View.VISIBLE);
   			image.setImageResource(bgResId);
    		image.setOnClickListener(onClick);
    	}
    } 
    
    protected void sendMsg(boolean containCookie, String msg, boolean needDelay) {
    	SendMsgProxy.sendCtrlMsg(containCookie, msg,  timeoutHandler);
    	
		if (true == needDelay) {
			timeoutHandler.removeCallbacks(timeoutProcess);
			timeoutHandler.postDelayed(timeoutProcess, 
					PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet ? 
							PubDefine.WAIT_SER_RESPONSE : PubDefine.WAIT_WIFI_RESPONSE);
		}    	
    };
    
    protected void sendMsgBin(boolean containCookie, byte[] msgBin, boolean needDelay) {
    	SendMsgProxy.sendCtrlMsgBin(containCookie, msgBin,  timeoutHandler);
    	
		if (true == needDelay) {
			timeoutHandler.removeCallbacks(timeoutProcess);
			timeoutHandler.postDelayed(timeoutProcess, 
					PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet ? 
							PubDefine.WAIT_SER_RESPONSE : PubDefine.WAIT_WIFI_RESPONSE);
		}    	
    }; 
    
}
