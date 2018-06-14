package com.thingzdo.ui.control;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.util.ThingzdoCheckBox;

public class DetailPCCtrlActivity extends TitledActivity implements OnClickListener{
	
	private SmartPlugHelper  mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";
	
	private ListView lv_pc_macs;
	private Button btn_pc_ctrl_open;
	private Button btn_pc_ctrl_close;
	
	private QuestionListAdapter questioni_dapter = null;
	private ArrayList<Question> m_QuestionsList = new ArrayList<Question>();
	private ArrayList<String> m_PCIDList = new ArrayList<String>();
	private Context mContext;
	
	private boolean b_btn_pc_ctrl_open = true;  // true: 开机； false: 关机
	
	private RevCmdFromSocketThread mTcpSocketThread = null;
	
	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(DetailPCCtrlActivity.this, intent)) {
					String plugid = intent.getStringExtra("PLUGID");
					int online = intent.getIntExtra("ONLINE", 0);
					if (m_PCIDList.indexOf(plugid) != -1) {
						for (int i = 0; i < m_QuestionsList.size(); i++) {
							String pcID = m_QuestionsList.get(i).getPc_id();
							if (pcID.equals(plugid) == true) {
								if (online == 1) {
									m_QuestionsList.get(i).setTv_pc_online("开机");
								} else {
									m_QuestionsList.get(i).setTv_pc_online("关机");
								}
							}
						}
						mHandler.sendEmptyMessage(0);
					}
				}
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(DetailPCCtrlActivity.this, intent)) {
					updateUI();
				}
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_POWER_ACTION)) {
				// nothing to do;
			}	
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_detail_pcctrl, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		mContext = this;
		
//		setTitleRightButton(R.string.smartplug_title_plug_detail, R.drawable.title_btn_selector, this);
		
		if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
			setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		} else {
			setTitleLeftButton(R.string.smartplug_esc, R.drawable.title_btn_selector, this);
		}		
		
		if (PubDefine.SmartPlug_Connect_Mode.WiFi == PubDefine.g_Connect_Mode) {
			setTitleRightButton(R.string.smartplug_mgr_reselect, R.drawable.title_btn_selector, this);
		}
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_POWER_ACTION);
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_NOTIFY_POWER);
		registerReceiver(mDetailRev, filter);
		
		mPlugHelper = new SmartPlugHelper(this);
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";	
		} 
		mPlugIp = intent.getStringExtra("PLUGIP");
			
		UDPClient.getInstance().setIPAddress(mPlugIp);
		
		init();
		
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}

		// 初始化Array数据
		m_QuestionsList.clear();
		m_PCIDList.clear();
		ArrayList<String> macs = getPCModuleMac();
		for (int i = 0; i < macs.size(); i++) {
			Question item = new Question();
			item.setPc_id("");
			item.setTv_pc_mac(macs.get(i));
			item.setTv_pc_online("未知");
			
			String macshow = macs.get(i);
			String macPC = getMac(macshow);
			if (macPC != null) {
				String pcID = mPlugHelper.getPlugIDFromMac(macPC);
				SmartPlugDefine pc = mPlugHelper.getSmartPlug(pcID);
				if (pc != null) {
					item.setPc_id(pcID);
					m_PCIDList.add(pcID);
					if (pc.mIsOnline == true) {
						item.setTv_pc_online("开机");
					} else {
						item.setTv_pc_online("关机");
					}
				}
			}
			
			item.setCb_pc_mac(false);
			m_QuestionsList.add(item);
		}
		
		questioni_dapter = new QuestionListAdapter(this, m_QuestionsList, mHandler);
    	lv_pc_macs.setAdapter(questioni_dapter);
    	
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();
		init();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mDetailRev);
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread.setRunning(false);
		}
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:  // WiFi模式 退出时，需要close掉 TCP连接
			disconnectSocket();			
			finish();
			break;
		case R.id.titlebar_rightbutton:
			Button btn_TitleRight = (Button) findViewById(R.id.titlebar_rightbutton);
			// Internet模式：“详情”界面 
			if (btn_TitleRight.getText().equals(getString(R.string.smartplug_title_plug_detail))) {
//				Intent intent = new Intent();
//				intent.putExtra("PLUGID", mPlugId);
//				intent.setClass(DetailPCCtrlActivity.this, PlugDetailInfoActivity.class);
//				startActivity(intent);
			} else {
			// WiFi直连：“重选”界面
				//PubDefine.disconnect();
				disconnectSocket();
				Intent intent = new Intent();
				intent.setClass(DetailPCCtrlActivity.this, AddSocketActivity2.class);
				startActivity(intent);
				finish();
			}
			break;
		case R.id.btn_pc_ctrl_open:
			pc_ctrl(true);
			break;
		case R.id.btn_pc_ctrl_close:
			pc_ctrl(false);
			break;
		default:
			break;
		}
	}
	
	private String getMac(String macshow) {
		int location = macshow.indexOf(" ") + 1;
		if (location != -1) {
			String mac = macshow.substring(location);
			return mac;
		}
		return null;
	}
	
	private void pc_ctrl(boolean ctrl_option) {
		b_btn_pc_ctrl_open = ctrl_option;
		updateUI();
		
		// do something...
		for (int i = 0; i < m_QuestionsList.size(); i++) {
			if (m_QuestionsList.get(i).isCb_pc_mac() == true) {
				StringBuffer sb = new StringBuffer();
				
				String temp = m_QuestionsList.get(i).getTv_pc_mac();
				String macPC = getMac(temp);
				if (macPC != null) {
					String pcID = mPlugHelper.getPlugIDFromMac(macPC);
					SmartPlugDefine pc = mPlugHelper.getSmartPlug(pcID);
		    		if (pc != null) {
				    	if (b_btn_pc_ctrl_open == true) {
				    		
				    		sb.append("APPPASSTHROUGH")
			    	         	 .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
			    	         	 .append(PubStatus.getUserName())
				    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				    	         .append(mPlugId)
				    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				    	         .append("50")
				    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				    	         .append("0")
				    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				         		 .append("MAGICPACKET")
				    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				    	         .append(PubStatus.getUserName())
				    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				    	         .append(mPlugId)
				    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				    	         .append(macPC)
				    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				    	         .append("1");
				    	} else {
				        	sb.append(SmartPlugMessage.CMD_SP_POWER)
						         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						         .append(PubStatus.getUserName())
						         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						         .append(pcID)
						         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						         .append("0");
				    	}
				    	
				    	sendMsg(true, sb.toString(), true);
		    		}
				}
			}
		}
	}
	
	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}
		
		lv_pc_macs = (ListView)findViewById(R.id.lv_pc_macs);
		btn_pc_ctrl_open = (Button)findViewById(R.id.btn_pc_ctrl_open);
		btn_pc_ctrl_open.setOnClickListener(this);
		btn_pc_ctrl_close = (Button)findViewById(R.id.btn_pc_ctrl_close);
		btn_pc_ctrl_close.setOnClickListener(this);
		
		updateUI();
	}
	
	private ArrayList<String> getPCModuleMac() {
		ArrayList<String> macs_show = mPlugHelper.getAllSmartPlugMacShow(mPlugId);
		return macs_show;
	}
	

	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	switch (msg.what) {
    		case 0:
    			questioni_dapter.notifyDataSetChanged();
    			break;
    		case 1:
    			break;
    		case 2:
    			break;
	    	default:
	    			break;
	    	}
	    }
	};
	
	private void disconnectSocket() {
		// WiFi 直连模式下，退出或者重选时，必须close TCP连接；
		/*if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			SmartPlugWifiMgr.disconnectSocket();	
		}*/
		
		return;
	}
	
	private void updateUI() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}		
		setTitle(mPlug.mPlugName);
		
//		if (b_btn_pc_ctrl_open == true) {
//			btn_pc_ctrl_open.setText("远程开机");
//		} else {
//			btn_pc_ctrl_open.setText("远程关机");
//		}
	}
	
	public class Question {
		private String pc_id;
		private String tv_pc_mac;
		private String tv_pc_online;
	    private boolean cb_pc_mac;

		public String getPc_id() {
			return pc_id;
		}
		public void setPc_id(String pc_id) {
			this.pc_id = pc_id;
		}
		public String getTv_pc_mac() {
			return tv_pc_mac;
		}
		public void setTv_pc_mac(String tv_pc_mac) {
			this.tv_pc_mac = tv_pc_mac;
		}
		public String getTv_pc_online() {
			return tv_pc_online;
		}
		public void setTv_pc_online(String tv_pc_online) {
			this.tv_pc_online = tv_pc_online;
		}
		public boolean isCb_pc_mac() {
			return cb_pc_mac;
		}
		public void setCb_pc_mac(boolean cb_pc_mac) {
			this.cb_pc_mac = cb_pc_mac;
		}
	}
	
	public class QuestionListAdapter extends BaseAdapter {
		private ArrayList<Question> mNewsList    = null;
		private LayoutInflater mInflater = null;
		private Context mContext = null;
		private Handler mHandler = null;
		
		@SuppressWarnings("unused")
		public QuestionListAdapter(Context context, 
				       ArrayList<Question> news, 
				       Handler handler){
			mContext = context;
			mInflater = LayoutInflater.from(context);
			mNewsList = news;
			mHandler = handler;
		}	
		
		@Override
		public int getCount() {
			return mNewsList.size();
		}

		@Override
		public Object getItem(int pos) {
			return mNewsList.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}
		
		private class ViewHolder
		{
			public TextView   tv_pc_mac;
			public TextView   tv_pc_online;
			public ThingzdoCheckBox   cb_pc_mac;
			
			public void ViewData(Question news, int position){
				tv_pc_mac.setText(String.valueOf(news.getTv_pc_mac()));
				tv_pc_online.setText(String.valueOf(news.getTv_pc_online()));
				cb_pc_mac.setChecked(news.isCb_pc_mac());
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if((mNewsList == null)|| (mNewsList.size()== 0)){
				return convertView;
			}
			if((position < 0)||(position > mNewsList.size())){
				return convertView;
			}
			if( mInflater == null){
				return convertView;
			}
		
		    ViewHolder holder = null;
		    if (convertView == null) {
		    	holder = new ViewHolder();
		    	convertView        	= mInflater.inflate(R.layout.item_pcctrl_mac_list, null);
	            holder.tv_pc_mac 	= (TextView)convertView.findViewById(R.id.tv_pc_mac);
	            holder.tv_pc_online 	= (TextView)convertView.findViewById(R.id.tv_pc_online);
	            holder.cb_pc_mac   = (ThingzdoCheckBox)convertView.findViewById(R.id.cb_pc_mac);
	            
	            //给Button添加单击事件 添加Button之后ListView将失去焦点 需要的直接把Button的焦点去掉
	            final int t_position = (int)(position);
	            holder.cb_pc_mac.setOnClickListener(new View.OnClickListener() {
	              @Override
	              public void onClick(View v) { 
	                boolean b_result = !mNewsList.get(t_position).isCb_pc_mac();
	                mNewsList.get(t_position).setCb_pc_mac(b_result);
	                mHandler.sendEmptyMessage(0);
	              }
	            });
	            
	            convertView.setTag(holder);
		    } else {
	      	    holder = (ViewHolder) convertView.getTag();
		    }
		    
		    if (holder != null && mNewsList != null && position < mNewsList.size()){
		    	Question FavoriteItem = mNewsList.get(position);
		    	convertView.setBackgroundColor(Color.TRANSPARENT);
	      	  	holder.ViewData(FavoriteItem, position);
		    }
		    return convertView;
		}
		
	}
}
