package com.thingzdo.ui.control;


import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.dataprovider.SmartPlugTimerHelper;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PowerOption;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.SmartPlugFragment;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.ui.wheelutils.MyAlertDialog;
import com.thingzdo.util.RefreshableView;
import com.thingzdo.util.RefreshableView.PullToRefreshListener;

public class ControlFragment extends SmartPlugFragment     
    implements View.OnClickListener{
	private ListView mPlugList = null;
	private SmartPlugHelper  mPlugHelper = null;
	private SmartPlugTimerHelper  mTimerHelper = null;
	
	private String mFocusPlugId = "0";  
	private boolean mFocusPlugPower = false;
	
	private RefreshableView mRefreshableView = null; 
	
	private static ControlFragment mFragment = null;
	
	private String mNewPlugName = "";
	private MyAlertDialog mModifyDlg = null;
	
    public static ControlFragment newInstance() {
    	if (null == mFragment) {
    		mFragment = new ControlFragment();
    	}
        return mFragment;
    }
    
    public static void delete() {
    	if (null != mFragment) {
    		mFragment = null;
    	}
    }
    
    private BroadcastReceiver mLoadPlugReveiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (null != mProgress) {
				mProgress.dismiss();
			}			
			if (intent.getAction().equals(PubDefine.PLUG_POWER_ACTION) || 
					intent.getAction().equals(PubDefine.PLUG_LIGHT_ACTION) || 
					intent.getAction().equals(PubDefine.PLUG_USB_ACTION)) {
				timeoutHandler.removeCallbacks(timeoutProcess);	
				int code = intent.getIntExtra("RESULT", 0);
				int status = intent.getIntExtra("STATUS", 0);
				String message = intent.getStringExtra("MESSAGE");
				switch (code) {
				case 0:
					SmartPlugDefine plug = mPlugHelper.getSmartPlug(mFocusPlugId);
					if (null != plug) {
						plug.mDeviceStatus = status;
						if (0 < mPlugHelper.modifySmartPlug(plug)) {
							doBackgroundLoad();	
						}
					}
					
					break;
				default:
					PubFunc.thinzdoToast(mContext, message);
					break;
				}				
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_UPDATE)) {
				timeoutHandler.removeCallbacks(timeoutProcess);				
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				if (0 == ret) {
					mPlugHelper.clearSmartPlug();
				}
				if (null != message && !message.isEmpty()) {
					PubFunc.thinzdoToast(mContext, message);					
				}
				doBackgroundLoad();
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(mContext, intent)) {				
					doBackgroundLoad();
				}
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(mContext, intent)) {
					qryPlugsFromServer();
//					doBackgroundLoad();
				}
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_LIGHT)) {
				if (true == NotifyProcessor.lightNotify(mContext, intent)) {
					doBackgroundLoad();
				}
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_CURTAIN)) {
				if (true == NotifyProcessor.curtainNotify(mContext, intent)) {				
					doBackgroundLoad();
				}
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_DELETE)) {
				timeoutHandler.removeCallbacks(timeoutProcess);	
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
				case 0:
					if (true == mPlugHelper.deleteSmartPlug(mFocusPlugId)) {
						doBackgroundLoad();	
					}
					
					break;
				default:
					PubFunc.thinzdoToast(mContext, 
							message);
					break;
				}				
			}
			if (intent.getAction().equals(PubDefine.PLUG_MODIFY_PLUGNAME)){
				if (null != mProgress) {
					mProgress.dismiss();
				}
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				timeoutHandler.removeCallbacks(timeoutProcess);	
				SmartPlugDefine plug = mPlugHelper.getSmartPlug(mFocusPlugId);
				switch (ret) {
				case 0:
					plug.mPlugName = mNewPlugName;
					if (0 < mPlugHelper.modifySmartPlug(plug)) {
						doBackgroundLoad();
					}
					
					break;
				default:
					PubFunc.thinzdoToast(mContext, message);
					break;
				}				
			}
			if (intent.getAction().equals(PubDefine.PLUG_BACK2AP_ACTION)) {
				String plugId = intent.getStringExtra("PLUGID");
				deletePlug(plugId);
				
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						qryPlugsFromServer();
					}
				}, 1);
			}
		}
    };
    

    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getActivity();
		
		mPlugHelper = new SmartPlugHelper(mContext);
		mTimerHelper =  new SmartPlugTimerHelper(mContext);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_POWER_ACTION);
		filter.addAction(PubDefine.PLUG_UPDATE);
		filter.addAction(PubDefine.PLUG_DELETE);
		filter.addAction(PubDefine.PLUG_NOTIFY_POWER);
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_MODIFY_PLUGNAME);
		filter.addAction(PubDefine.PLUG_NOTIFY_CURTAIN);
		filter.addAction(PubDefine.PLUG_BACK2AP_ACTION);
		mContext.registerReceiver(mLoadPlugReveiver, filter);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				qryPlugsFromServer();
			}
		}, 1);
	}
	
	private Handler mTimeoutHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (0 == msg.what) {
				timeoutHandler.removeCallbacks(timeoutProcess);
				if (null != mRefreshableView) {
					mRefreshableView.finishRefreshing();
				}				
			}
		}
	};
	
	private void qryPlugsFromServer() {
    	registerTimeoutHandler(mTimeoutHandler);

		StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_QRYPLUG)
    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	  .append(PubStatus.g_CurUserName);
    	
    	sendMsg(true, sb.toString(), true);
	}	
	
	private void setPlugsOffline(){
		mPlugHelper.setAllPlugsOffline();
		doBackgroundLoad();
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mFragmentView = inflater.inflate(R.layout.fragment_control, container, false);
		
		mPlugList = (ListView)mFragmentView.findViewById(R.id.plug_list);
		
		mRefreshableView = (RefreshableView) mFragmentView.findViewById(R.id.refreshable_view);  
		mRefreshableView.setOnRefreshListener(new PullToRefreshListener() {  
            @Override  
            public void onRefresh() {  
               	qryPlugsFromServer(); 
            }  
        }, 0);		
		
		return mFragmentView;
	}
	
	private Handler updateHandler = new Handler() {
		public void handleMessage(Message msg) {
			setPlugsOffline();	
		};
	};

	@Override
	public void onResume() {
		super.onResume();
		SmartPlugApplication.resetTask();
		PubFunc.log("ControlFragment onResume"); 
		doBackgroundLoad();
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mContext.unregisterReceiver(mLoadPlugReveiver);
	}

	
	private void doBackgroundLoad() {
		AsyncTask<Void, Void, ArrayList<SmartPlugDefine>> loadData = new AsyncTask<Void, Void, ArrayList<SmartPlugDefine>>() {
			private ArrayList<Integer> hasTimer = new ArrayList<Integer>();
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
			
			
			@Override
			protected ArrayList<SmartPlugDefine> doInBackground(Void... arg0) {
				ArrayList<SmartPlugDefine> plugs = mPlugHelper.getAllSmartPlug(PubStatus.g_CurUserName);
				if (null != plugs) {
					for (int i = 0; i < plugs.size(); i++) {
						hasTimer.add(mTimerHelper.getAllTimer(plugs.get(i).mPlugId).size());	
					}
				}
				
				return plugs;
			}
			
			@Override
			protected void onPostExecute(ArrayList<SmartPlugDefine> result) {
				super.onPostExecute(result);
				if (null != result) {
					PluglistAdapter adapter = new PluglistAdapter(mContext, 
							result, 
							hasTimer, 
							mPressHandler);
					mPlugList.setAdapter(adapter);
				}
				
				if (null != mRefreshableView) {
					mRefreshableView.finishRefreshing();
				}
			}
			
		};
		loadData.execute();
	}
	
	private Handler mPressHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	mFocusPlugId = (String)msg.obj;
	    	SmartPlugDefine plug = mPlugHelper.getSmartPlug(mFocusPlugId);
	    	PubFunc.log("mFocusPlugId=" + mFocusPlugId);
	    	if (0 == msg.what) {
		    	if (null != plug) {
		    		modifyName(plug.mPlugName);
		    	}
	    	}
	    	if (1 == msg.what) {
		    	if (null != plug) {
		    		deletePlug(mFocusPlugId);
		    	}	    		
	    	}
	    };	
	};
	
	View.OnClickListener modifyClick = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (null == mModifyDlg) {
				return;
			}
			String text = mModifyDlg.getResult();
			if (!text.isEmpty()) {
				mNewPlugName = mModifyDlg.getResult();
				
				// 校验 NewPlugName：中英文占用的字节数必须小于20（最大20个byte）
				if (mNewPlugName.getBytes().length > 20) {
					PubFunc.thinzdoToast(mContext, getString(R.string.smartplug_ctrl_mod_plugname_length_too_long));
					return;
				}
				
				if (true == mPlugHelper.isPlugExists(PubStatus.g_CurUserName, mNewPlugName)) {
					PubFunc.thinzdoToast(mContext, getString(R.string.smartplug_ctrl_samename_exist));
					return;
				}
				
		    	mProgress = PubFunc.createProgressDialog(mContext, getString(R.string.smartplug_ctrl_moding_name), false);
		    	mProgress.show();		
				

		    	StringBuffer sb = new StringBuffer();
		    	sb.append(SmartPlugMessage.CMD_SP_MODYPLUG)
		    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
		    	  .append(PubStatus.g_CurUserName)
		    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
		    	  .append(mFocusPlugId)
		    	  .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
		    	  .append(mNewPlugName);
		    	sendMsg(true, sb.toString(), true);
			}
		}
	};
			
	
	
	private void modifyName(String name) {
		mModifyDlg = new MyAlertDialog(mContext);
		mModifyDlg.builder()
			.setCancelable(true)
			.setTitle(getString(R.string.smartplug_ctrl_mod_name))
			.setEditText(name)
			.setPositiveButton(mContext.getString(R.string.smartplug_ok), modifyClick)
			.setNegativeButton(mContext.getString(R.string.smartplug_cancel),  new View.OnClickListener(){
				@Override
				public void onClick(View arg0) {
					
				}
				
			})			
			.show();
	}
	
	private void deletePlug(String plugId) {
		mErrorMsg = getString(R.string.smartplug_ctrl_delete_fail);
    	mProgress = PubFunc.createProgressDialog(mContext, mContext.getString(R.string.smartplug_ctrl_delete), false);
    	mProgress.show();	
    	
    	if (mFocusPlugId.equalsIgnoreCase("0")) {
    		int a = 0;
    	}

    	StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_DELPLUG)
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(PubStatus.g_CurUserName)
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(plugId);
    	sendMsg(true, sb.toString(), true);
	}
}
