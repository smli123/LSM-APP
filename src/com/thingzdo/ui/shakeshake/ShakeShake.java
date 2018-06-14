package com.thingzdo.ui.shakeshake;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.dataprovider.SmartPlugTimerHelper;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.TimerDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.control.PlugDetailActivity;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.shakeshake.ShakeListener.OnShakeListener;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.util.ThingzdoCheckBox;


public class ShakeShake extends TitledActivity implements OnClickListener {
	private final static int  MSG_NOTIFY_INIT				= 0;
	private final static int  MSG_NOTIFY_SUCCESS			= 1;
	private final static int  MSG_NOTIFY_FAIL				= 2;
	private final static int  MSG_NOTIFY_NETWORK_FAIL		= 3;
	private final static int  MSG_UPDATE_LISTVIEW_SCANPORT	= 4;
	private final static int  MSG_UPDATE_LISTVIEW_SCANPORT_END	= 5;
	private final static int  MSG_UPDATE_SCANBTN_ENABLE		= 6;
	private final static int  MSG_UPDATE_SCANBTN_DISABLE	= 7;
	private final static int  MSG_UPDATE_SHOW_INFO			= 8;
	private final static int  MSG_START_VIBERATE			= 9;
	private final static int  MSG_STOP_VIBERATE				= 10;
	private final static int  MSG_OPER_FAIL  				= 11;
	
	private int msg_notify = MSG_NOTIFY_INIT;
	
	
	// UDP Scan Port Mode
	private static final int UDP_MAX_RETRIES_SEND		= 1; 	    				// 最大重发次数5次
	private static final int UDP_MAX_RETRIES_RECV		= 3; 	    				// 最大接收次数5次
	
	private final static int UDP_MAX_RECEIVE_IP_NUM  	= 256;						// 最大接收数据的次数
	private final static int UDP_RECEIVE_BUFFER_SIZE 	= 1024;						// 接收数据的缓冲器大小
	private final static int UDP_TIME_OUT				= 5000;						// UDP 网络访问超时时间 默认：5秒
	private final static int UDP_SRC_PORT 				= 50020;					// 本地UDP端口号
	private final static int UDP_DEST_PORT 				= 5003;						// 扫描UDP端口号：即：智能插座的UDP端口号
	private final static String UDP_SCANPORT_COMMAND	= "0,REPORTIP,test,null#";	// 扫描UDP端口号用的命令行语句
	
	// TCP Scan Port Mode
	private final static int TCP_TIME_OUT				= 5000;
	private final static int TCP_PORT_ID				= 6002;
	
	// Thread Sleep Time for ListView Show Data
	private final static int THREAD_SLEEP_TIME			= 500;	
	
	private TextView tv_routessid;
	private ListView lv_iplist;
	private ListItemAdapter adapter;
	private List<Map<String, Object>> m_tjData;			// ListView 显示的数据
	private List<Map<String, Object>> m_tjData_bak;		// 备份的数据
	
	private ThingzdoCheckBox cb_selectall;
	private RadioGroup rg_control_object;
	private RadioButton rb_power;
	private RadioButton rb_nightlight;
	private RadioButton rb_usb;
	
	// 摇一摇功能实现
	private ShakeListener mShakeListener = null;
	private Vibrator mVibrator;

	MediaPlayer mp3_player = null;
	
	private boolean b_switch = false;	
	
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;

	private ArrayList<SmartPlugDefine> plugs  = new ArrayList<SmartPlugDefine>();
	private ArrayList<TimerDefine>  timerList = new ArrayList<TimerDefine>();
	private ArrayList<SmartPlugDefine> mofidyplugs  = new ArrayList<SmartPlugDefine>();
	
//	// 注册广播信号	
//	private BroadcastReceiver mRegisterRev = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//
//			if (intent.getAction().equals(PubDefine.PLUG_POWER_ACTION)) {
//				if (mProgress!=null){
//					mProgress.dismiss();
//				}
//				RevCmdFromSocketThread.getInstance().setRunning(false);
//				timeoutHandler.removeCallbacks(timeoutProcess);
//				int ret = intent.getIntExtra("RESULT", 1);
//				String message = intent.getStringExtra("MESSAGE");
//				switch (ret) {
//				case 0:
//					break;
//				default:
//					
//					break;
//				}
//			}
//		}
//	};	
	
	/*
	 * 判断插座是否已经在扫描到的列表中存在
	 */
	private boolean isPlugExist(String plugid) {
		for (int i = 0; i < m_tjData.size(); i++) {
			String udp_moduleid = m_tjData.get(i).get("udp_moduleid").toString();
			if (plugid.equals(udp_moduleid)) {
				return true;
			}
		}
		return false;
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_shakeshake, false);
		SmartPlugApplication.getInstance().addActivity(this);
		
	//	// 注册广播信号
	//	IntentFilter filter = new IntentFilter();
	//	filter.addAction(PubDefine.PLUG_POWER_ACTION);
	//	filter.addAction(PubDefine.PLUG_LIGHT_ACTION);
	//	filter.addAction(PubDefine.PLUG_USB_ACTION);
	//	registerReceiver(mRegisterRev, filter);
		
		mSharedPreferences = getSharedPreferences("ShakeShake", Activity.MODE_PRIVATE);
		
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		setTitleRightButton(R.string.smartplug_oper_scan, R.drawable.title_btn_selector, this);
		
		String routeName = "";
		WifiManager Wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (Wifi.isWifiEnabled()) {
			WifiInfo wifiInfo = Wifi.getConnectionInfo();
			routeName = wifiInfo.getSSID().replace("\"", "");
		}
	
		// 针对 Shake模式，直接下发到模块，根据模块要求，需要单独增加IP地址和端口号信息；（原因：模块UDP协议无法解析到UDP Socket的IP和Port）
		PubDefine.global_local_ip = getLocalIP();
		if (PubDefine.global_local_ip == null) 
			PubDefine.global_local_ip = "";
		
		// 设置当前路由器的SSID
		tv_routessid = (TextView)findViewById(R.id.tv_routessid);
		tv_routessid.setText(routeName);
		
		rg_control_object = (RadioGroup)findViewById(R.id.rg_control_object);
		rb_power = (RadioButton)findViewById(R.id.rb_power);
		rb_nightlight = (RadioButton)findViewById(R.id.rb_nightlight);
		rb_usb = (RadioButton)findViewById(R.id.rb_usb);
		
		lv_iplist = (ListView)findViewById(R.id.lv_iplist);
	
		// 使用ListView的方法： 
		m_tjData = new ArrayList<Map<String,Object>>();
		m_tjData_bak = new ArrayList<Map<String,Object>>();
		adapter = new ListItemAdapter(this);
		lv_iplist.setAdapter(adapter);
		
		loadScanIPAddressData();
		
		// 实现全选/全不选功能
		cb_selectall = (ThingzdoCheckBox)findViewById(R.id.cb_selectall);
		cb_selectall.setChecked(true);
		cb_selectall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				boolean b_selectall = cb_selectall.isChecked();
				for (int i = 0; i < m_tjData.size(); i++) {
					m_tjData.get(i).put("cb_ipAddress", b_selectall);	
				}
				
				if (m_tjData.size() > 0) {
					mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW_SCANPORT);
				}
			}
		});
		
		
		this.setTitle(getString(R.string.smartplug_oper_scan_devices) + "(" + m_tjData.size() + ")");
		
		
	//	// 延迟 1秒钟，自动启动扫描
	//	new Handler().postDelayed(new Runnable(){
	//		public void run(){
	//			scanportWithUDPMode_Button();
	//		}
	//	}, 1000);
	}
	
	public void startVibrato(){	
		// 播放声音
	    mp3_player.start();
		
		//定义震动 ： 导致系统崩溃
		mVibrator.vibrate( new long[]{500,200,500,200}, -1); //第一个｛｝里面是节奏数组， 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
	}
	
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
	     @Override
	        public void handleMessage (Message msg) {
	            super.handleMessage(msg);
	            Button left_btn = (Button)findViewById(R.id.titlebar_leftbutton);
	            Button right_btn = (Button)findViewById(R.id.titlebar_rightbutton);
	            switch (msg.what) {
		            case MSG_UPDATE_SCANBTN_ENABLE:
		            	left_btn.setEnabled(true);
		            	right_btn.setEnabled(true);
		            	break;
		            case MSG_UPDATE_SCANBTN_DISABLE:
		            	left_btn.setEnabled(false);
		            	right_btn.setEnabled(false);
		            	break;
		            case MSG_START_VIBERATE:
		            	startVibrato();
		            	break;
		            case MSG_STOP_VIBERATE:
		            	mVibrator.cancel();
		            	break;	            	
		            case MSG_UPDATE_LISTVIEW_SCANPORT:
		            	adapter.notifyDataSetChanged();
		            	ShakeShake.this.setTitle(getString(R.string.smartplug_oper_scan_devices) + "(" + m_tjData.size() + ")");
		            	break;
	            	case MSG_UPDATE_LISTVIEW_SCANPORT_END:
	            		ShakeShake.this.setTitle(getString(R.string.smartplug_oper_scan_devices) + "(" + m_tjData.size() + ")");
		            	adapter.notifyDataSetChanged();
		            	PubFunc.thinzdoToast(ShakeShake.this,  "完成扫描");
	            	break;
	            	case MSG_UPDATE_SHOW_INFO:
	            		PubFunc.thinzdoToast(ShakeShake.this,  (String)msg.obj);
	            		break;
	                case MSG_NOTIFY_SUCCESS:
	                	PubFunc.thinzdoToast(ShakeShake.this,  "发送成功");
	                	break;
	                case MSG_NOTIFY_FAIL:
	                	PubFunc.thinzdoToast(ShakeShake.this,  "发送失败");
	                	break;
	                case MSG_NOTIFY_NETWORK_FAIL:
	                	PubFunc.thinzdoToast(ShakeShake.this,  "网络错误");
	                	break;
	                case MSG_NOTIFY_INIT:
	                	PubFunc.thinzdoToast(ShakeShake.this, "未知的初始化消息");
	                	break;
	                case MSG_OPER_FAIL:
	            		String error = SmartPlugApplication.getContext().getString(R.string.login_timeout);
	            		if (PubDefine.g_Connect_Mode != PubDefine.SmartPlug_Connect_Mode.Internet) {
	            			error = SmartPlugApplication.getContext().getString(R.string.oper_error);
	            		}
	            		PubFunc.thinzdoToast(SmartPlugApplication.getContext(), error);	
	            		break;
	                default:
	                	PubFunc.thinzdoToast(ShakeShake.this, "不可能的消息");;
	                    break;
	            }
	        }
	};
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// 摇一摇功能实现
		mVibrator = (Vibrator)getApplication().getSystemService(VIBRATOR_SERVICE);
	
		// 播放声音的初始化
		if (mp3_player == null) {
			mp3_player = new MediaPlayer();
			mp3_player = MediaPlayer.create(this, R.raw.poweron);
			mp3_player.setLooping(false);
				
		}
		
		if (mShakeListener == null) {
			mShakeListener = new ShakeListener(this);
			mShakeListener.setOnShakeListener(new OnShakeListener() {
		    	// 摇一摇 执行的操作
				public void onShake() {
					// 针对 Shake模式，直接下发到模块，根据模块要求，需要单独增加IP地址和端口号信息；（原因：模块UDP协议无法解析到UDP Socket的IP和Port）
					PubDefine.global_local_ip = getLocalIP();
					if (PubDefine.global_local_ip == null) 
						PubDefine.global_local_ip = "";
					
					// Lishimin 开电/关电
					String cmd_text = "";
					int resid_mp3_file = R.raw.poweron;
					if (rb_power.isChecked()) {
						cmd_text = (b_switch == true) ? "打开电源" : "关闭电源";	
						resid_mp3_file = (b_switch == true) ? R.raw.poweron : R.raw.poweroff;
						
					} else if (rb_nightlight.isChecked()) {
						cmd_text = (b_switch == true) ? "打开灯光" : "关闭灯光";
						resid_mp3_file = (b_switch == true) ? R.raw.lighton : R.raw.lightoff;
					} else {							// 默认先不显示，先关掉
						cmd_text = (b_switch == true) ? "打开USB" : "关闭USB";
						resid_mp3_file = (b_switch == true) ? R.raw.usbon : R.raw.usboff;
					}
					
					mp3_player.release();
					mp3_player = MediaPlayer.create(ShakeShake.this, resid_mp3_file);
					
					if (!cmd_text.isEmpty()) {
						// 必须有选择的对象才进行操作，否则不处理；
						boolean b_doShake = false;
						for (int i = 0; i < m_tjData.size(); i++) {
							if ((Boolean)m_tjData.get(i).get("cb_ipAddress") == true) {
								b_doShake = true;
								break;
							}
						}
						if (b_doShake == true) {
							mShakeListener.stop();
							startVibrato(); 				//开始 震动
							
							parseCommand(cmd_text);
							
							new Handler().postDelayed(new Runnable(){
								public void run(){
									if (null == mShakeListener || null == mVibrator) {
										return;
									}								
								   mVibrator.cancel();
								   mShakeListener.start();
								}
							}, 2000);
						} else {							//不处理 震动
							// 
							new Handler().postDelayed(new Runnable(){
								public void run(){
									if (null == mShakeListener) {
										return;
									}								
								   mShakeListener.start();
								}
							}, 2000);
						}
						
					}
					
				}
			});
		}
				
		// 从数据库中重新读取并刷新m_tjData
	    SmartPlugHelper mPlugHelper = new SmartPlugHelper(SmartPlugApplication.getContext());
	    SmartPlugTimerHelper mTimerHelper = new SmartPlugTimerHelper(SmartPlugApplication.getContext()); 
	    
	    for (int i = 0; i < m_tjData.size(); i++) {
	    	String udp_moduleid = m_tjData.get(i).get("udp_moduleid").toString();
	    	
	    	SmartPlugDefine db_plug = mPlugHelper.getSmartPlug(udp_moduleid);
	    	if (db_plug == null) {
	    		continue;
	    	}
	    	if (db_plug.mPlugId.equals(udp_moduleid)) {
	    		int pwr_status = db_plug.mDeviceStatus;
	    		
	    		m_tjData.get(i).put("plug_img_power", (Boolean)(((pwr_status & 0x0001) == 0) ? false : true));
	    		m_tjData.get(i).put("plug_img_light", (Boolean)(((pwr_status & 0x0002) == 0) ? false : true));
	    		m_tjData.get(i).put("plug_img_usb",   (Boolean)(((pwr_status & 0x0004) == 0) ? false : true));
	    		
	    		m_tjData.get(i).put("plug_img_online", db_plug.mIsOnline);
	    		m_tjData.get(i).put("plug_txt_name", db_plug.mPlugName);
	    		m_tjData.get(i).put("plug_txt_version", db_plug.mVersion);
	    		m_tjData.get(i).put("plug_txt_moduletype", db_plug.mModuleType);
	    		
	    		ArrayList<TimerDefine> timerList = mTimerHelper.getAllTimer(udp_moduleid);
	    		int timer_count = 0;
	    		if (timerList != null) {
	    			timer_count = timerList.size();
	    		}
	    		m_tjData.get(i).put("plug_img_timer", timer_count);
	    	}
	    }
	    
	    mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW_SCANPORT);
	    
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (mShakeListener != null) {
			mShakeListener.stop();
			mShakeListener = null;
		}
		if (mp3_player != null) {
			mp3_player.release();
			mp3_player = null;
		}
		
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
			Intent intent = new Intent();
			intent.setClass(ShakeShake.this, LoginActivity.class);
			startActivity(intent);
			finish();
			return true;
		}
    	return super.onKeyDown(keyCode, event);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		saveScanIPAddressData();
			
	//	unregisterReceiver(mRegisterRev);
		
		/*if (null != PubDefine.global_socket) {
			PubDefine.global_socket.close();
			PubDefine.global_socket = null;
		}*/
		
	}

	//-----------------------------------------------------------------------------
	// send_NetWorkData_Async 只用于 Power、Light、USB操作
	class send_NetWorkData_Async extends AsyncTask<Void, Void, Void>  {
		private String ipAddress;
		private String str_Text;
	//	private Socket socket = null;
	//	private String buffer = "";
	//	private int timeout = UDP_TIME_OUT;
		
		public send_NetWorkData_Async(String ipAddress, String str_Text) {
			this.ipAddress = ipAddress;
			this.str_Text = str_Text;
		}
	
		@Override
		protected Void doInBackground(Void... params) {
	//		// UDP Mode
			sendCommandRequestWithUDPSocket(ipAddress, UDP_SRC_PORT, UDP_DEST_PORT, UDP_TIME_OUT, UDP_MAX_RETRIES_SEND, str_Text, true, true);
	
	//		// TCP Mode
	//		sendCommandRequestWithTCPSocket(ipAddress, TCP_TIME_OUT, str_Text);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW_SCANPORT);
			
	//		// 测试使用，版本发布时，必须屏蔽；
	//		mHandler.sendEmptyMessage(msg_notify);
	
			// 写入APP数据库
	    	new ModifyUpdateTableTask(mofidyplugs).execute();
		}
	}    
	private void sendCommandRequestWithUDPSocket(String ipAddress, int localPort, int remotePort, 
			int timeout, int max_retries, String str_Text, boolean needReturn, boolean bAddLocalIP) {
		try {
			mofidyplugs.clear();
			
			InetAddress remoteInetAddress = InetAddress.getByName(ipAddress); // 服务器地址
		    // Convert the argument String to bytes using the default encoding

		    //DatagramSocket socket = new DatagramSocket(localPort);
		    DatagramSocket socket = new DatagramSocket();
		    socket.setSoTimeout(timeout); 									// 设置阻塞时间
			
			// 针对 Shake模式，直接下发到模块，根据模块要求，需要单独增加IP地址和端口号信息；（原因：模块UDP协议无法解析到UDP Socket的IP和Port）
		    if (bAddLocalIP == true) {
				String strLocalPort = String.valueOf(socket.getLocalPort());
				str_Text = str_Text.substring(0, str_Text.indexOf(StringUtils.PACKAGE_END_SYMBOL));
				str_Text = str_Text + "," + PubDefine.global_local_ip + "," + strLocalPort + StringUtils.PACKAGE_END_SYMBOL;
		    }
		    
		    //发送的信息
		    byte[] bytesToSend = str_Text.getBytes();
	
		    DatagramPacket sendPacket = new DatagramPacket(bytesToSend, 	// 相当于将发送的信息打包
		        bytesToSend.length, remoteInetAddress, remotePort);
		    
		    DatagramPacket receivePacket =                              	// 相当于空的接收包
			        new DatagramPacket(new byte[UDP_RECEIVE_BUFFER_SIZE], UDP_RECEIVE_BUFFER_SIZE);
		
		    int tries = 0;      											// Packets may be lost, so we have to keep trying
		    do {
		    	// Send Function
		    	try {
		    		socket.send(sendPacket);          						// 发送信息
		    		if (false == needReturn) {
		    			return;
		    		}
		    		
		    		// 分接收来的指令不同，而进行不同的处理
		    		// Receive Function
				    try {
				    	Map<String, Object> map = null;
				        socket.receive(receivePacket); 					// 接收信息
				        
				        String peerIP = receivePacket.getAddress().getHostAddress();
				        String peerPort = "0";
				        String receiveData = new String(receivePacket.getData());
				        receiveData = receiveData.substring(0, receiveData.indexOf("#"));
				        String[] cmds = receiveData.split(",");
				        
				        // 检查返回的命令字格式是否正确；
				        if(cmds == null || cmds.length < 6){
							continue;
						}
				        
				        String user_cookie 	= cmds[0];
				        String cmd_name 	= cmds[1];
				        String module_id 	= cmds[3];
				        int pwr_status	= Integer.parseInt(cmds[5]);
				        if (cmd_name.equals(SmartPlugMessage.CMD_SP_POWER)) {
				        	modifyListViewData(module_id, "plug_img_power", (Boolean)(((pwr_status & 0x0001) == 0) ? false : true));
				        } else if (cmd_name.equals(SmartPlugMessage.CMD_SP_LIGHT)) {
				        	modifyListViewData(module_id, "plug_img_light", (Boolean)(((pwr_status & 0x0002) == 0) ? false : true));
				        } else if (cmd_name.equals(SmartPlugMessage.CMD_SP_USB)) {
				        	modifyListViewData(module_id, "plug_img_usb", (Boolean)(((pwr_status & 0x0004) == 0) ? false : true));
				        } else {		// Do nothing
				        	
				        }
				        
				        // 为了写入APP数据库操作；
				        {
					        SmartPlugDefine plug = new SmartPlugDefine();
					        int i = 0;
					        for (i = 0; i < plugs.size(); i++) {
					        	plug = plugs.get(i);
					        	if (plug.mPlugId.equals(module_id)) {
					        		plug.mDeviceStatus = pwr_status;
					        		mofidyplugs.add(plug);
					        		break;
					        	}
					        }
				        }
				        
				        
				    } catch (Exception e) {
				    	e.printStackTrace();
				    	mHandler.sendEmptyMessage(MSG_OPER_FAIL);
				    }
				        
		    	 } catch (InterruptedIOException e) { 						// 当receive不到信息或者receive时间超过3秒时，就向服务器重发请求
				        tries += 1;
				        Log.v("ScanPort", "Send Timed out, " + (max_retries - tries) + " more tries...");
				 }
				    
		    } while (tries >= max_retries);
		
		    socket.close();
				
		    if (tries < max_retries) { 										// 发送成功
				msg_notify = MSG_NOTIFY_SUCCESS;
			} else {														// 发送失败
				msg_notify = MSG_NOTIFY_NETWORK_FAIL;
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg_notify = MSG_NOTIFY_NETWORK_FAIL;	
			mHandler.sendEmptyMessage(MSG_OPER_FAIL);
		}
	}

	private void sendCommandRequestWithTCPSocket(String ipAddress, int timeout, String str_Text) {
		Socket socket = null;
		String buffer = "";
		
		try {
			//连接服务器 并设置连接超时为5秒
			socket = new Socket();
			socket.connect(new InetSocketAddress(ipAddress, TCP_PORT_ID), timeout);
			
			//获取输入输出流
			OutputStream ou = socket.getOutputStream();
			
	//		BufferedReader bff = new BufferedReader(new InputStreamReader(
	//				socket.getInputStream()));
	
	//		//读取发来服务器信息
	//		String line = null;
	//		buffer = "";
	//		while ((line = bff.readLine()) != null) {
	//			buffer = line + buffer;
	//		}
			
			//向服务器发送信息
			ou.write((byte[])str_Text.getBytes());
			ou.flush();
			
			msg_notify = MSG_NOTIFY_SUCCESS;
			
	//		bff.close();
			ou.close();
			socket.close();
			
		} catch (SocketTimeoutException aa) {
			msg_notify = MSG_NOTIFY_NETWORK_FAIL;
		} catch (IOException e) {
			e.printStackTrace();
			msg_notify = MSG_NOTIFY_NETWORK_FAIL;
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbutton:
			Intent intent = new Intent();
			intent.setClass(ShakeShake.this, LoginActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.titlebar_rightbutton:
			scanportWithUDPMode_Button();
			break;
		default:
			break;
		}
	}


	//----------------------------------------------------------
	// UDP 方式处理函数
	//----------------------------------------------------------
	private void scanportWithUDPMode_Button() {
		InetAddress inetAddress;
	
		try {
			// 方法一
			inetAddress = getBroadcastAddress(this, "255.255.255.255");
			String dest_mask = inetAddress.getHostAddress();
			PubDefine.global_local_ip = getLocalIP();
			if (PubDefine.global_local_ip == null) 
				PubDefine.global_local_ip = "";
			
			new scanportWithUDP_Async(dest_mask, UDP_SRC_PORT, UDP_DEST_PORT, UDP_TIME_OUT, UDP_SCANPORT_COMMAND, true).execute();
			
			// 方法二
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private String getLocalIP() {
		WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE); 
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		if (ipAddress == 0)
			return null;
		return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."+(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
	}
	
	class scanportWithUDP_Async extends AsyncTask<Void, Void, Void>  {
		
		// UDP 匹配模式
		private String remoteAddress;
		private int localPort, remotePort; 
		private int timeout; 		// 线程数，这是第几个线程，超时时间
		private String sendText;
		private boolean b_findedIP = false;
		private boolean b_addLocalIP = false;
		
		public scanportWithUDP_Async(String remoteAddress, int localPort, int remotePort, int timeout, String sendText, boolean addLocalIP) {
			this.remoteAddress = remoteAddress;
			this.localPort = localPort;
			this.remotePort = remotePort;
			this.timeout = timeout;
			this.sendText = sendText;
			this.b_addLocalIP = addLocalIP;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgress = PubFunc.createProgressDialog(ShakeShake.this, getString(R.string.login_shake_scaning), false);
	    	mProgress.show(); 					
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			mHandler.sendEmptyMessage(MSG_UPDATE_SCANBTN_DISABLE);
			
			try {
				m_tjData.clear();
				mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW_SCANPORT);
				try {
					Thread.sleep(THREAD_SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
							
				scanPortWithUDP();
	
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (null != mProgress) {
				mProgress.dismiss();
				mProgress = null;
			}
			
			mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW_SCANPORT_END);
	
			mHandler.sendEmptyMessage(MSG_UPDATE_SCANBTN_ENABLE);
			
			// 写入APP数据库
		    if (b_findedIP == true) {
		    	new Add2DBUpdateTableTask(plugs, timerList).execute();
		    }
		}
		
		private void scanPortWithUDP() throws IOException {
			b_findedIP = false;
	//		m_tjData.clear();
			plugs.clear();
			timerList.clear();
	
		    InetAddress remoteInetAddress = InetAddress.getByName(this.remoteAddress); // 服务器地址
		    // Convert the argument String to bytes using the default encoding
	
		    DatagramSocket socket = new DatagramSocket(this.localPort);
		    //DatagramSocket socket = new DatagramSocket();
		    socket.setSoTimeout(this.timeout); 								// 设置阻塞时间
		    
		    if (this.b_addLocalIP == true) {
				String strLocalPort = String.valueOf(socket.getLocalPort());
				sendText = sendText.substring(0, sendText.indexOf(StringUtils.PACKAGE_END_SYMBOL));
				sendText = sendText + "," + PubDefine.global_local_ip + "," + strLocalPort + StringUtils.PACKAGE_END_SYMBOL;
		    }

		    //发送的信息
		    byte[] bytesToSend = sendText.getBytes();
		    
		    DatagramPacket sendPacket = new DatagramPacket(bytesToSend, 	// 相当于将发送的信息打包
		        bytesToSend.length, remoteInetAddress, this.remotePort);
		
		    DatagramPacket receivePacket =                              	// 相当于空的接收包
		        new DatagramPacket(new byte[UDP_RECEIVE_BUFFER_SIZE], UDP_RECEIVE_BUFFER_SIZE);
		
		    int tries_send = 0;      											// Send Packets may be lost, so we have to keep trying
		    int tries_recv = 0;      											// Recv Packets may be lost, so we have to keep trying
		    boolean receivedResponse = false;
		    int i = 0, j = 0, k = 0;
		    
		    do {
		    	
		    	// Send Function
		    	try {
		    		socket.send(sendPacket);          						// 发送信息
		    	
		    		// Receive Function
		    		i = 0;
				    try {
				    	for (i = 0; i < UDP_MAX_RECEIVE_IP_NUM; i++) {
				    		
					        socket.receive(receivePacket); 					// 接收信息
					        String peerIP = receivePacket.getAddress().getHostAddress();
					        String peerPort = "0";
					        String receiveData = new String(receivePacket.getData());
					        receiveData = receiveData.substring(0, receiveData.indexOf("#"));
					        String[] cmds = receiveData.split(",");
					        int ii = m_tjData.size();
					        Log.d("Map size", String.valueOf(ii));
					        
					        // 检查返回的命令字格式是否正确；
					        if (cmds == null || cmds.length < 15){
								continue;
							}
					        
					        //如果插座已经扫描过，不再扫描
					        if (true == isPlugExist(cmds[3])) {
					        	continue;
					        }				        
	
					        String cmd_name 	= cmds[1];
					        if (!cmd_name.equals(SmartPlugMessage.CMD_SP_LOGIN_MODULE)) {
					        	continue;
					        }
					        
	
					     // 写入APP数据库
					        {
								SmartPlugDefine aPlug = new SmartPlugDefine();
							    aPlug.mUserName  = "ShakeShake";
							    aPlug.mPlugId    = cmds[3];
							    aPlug.mPlugName  = cmds[4];
							    aPlug.mMAC 		 = cmds[5];
							    aPlug.mVersion   = cmds[6];
							    aPlug.mModuleType= cmds[7];
							    
							    aPlug.mProtocolMode = Integer.parseInt(cmds[8]);
							    aPlug.mIsOnline  	= true;
							    aPlug.mDeviceStatus = Integer.parseInt(cmds[9]);
							    aPlug.mIPAddress = peerIP;
							    aPlug.mFlashMode = Integer.parseInt(cmds[10]);
							    aPlug.mColor_R   = Integer.parseInt(cmds[11]);
							    aPlug.mColor_G   = Integer.parseInt(cmds[12]);
							    aPlug.mColor_B   = Integer.parseInt(cmds[13]);
							    plugs.add(aPlug);
							    
							    int timerCount = Integer.parseInt(cmds[14]);
								int baseIdx = 15;
							    for (j = 0; j < timerCount; j++) {
							    	TimerDefine ti   = new TimerDefine();
							    	ti.mPlugId       = aPlug.mPlugId;
							    	ti.mTimerId      = Integer.parseInt(cmds[baseIdx + j * 6 + 0]);
							    	ti.mType         = Integer.parseInt(cmds[baseIdx + j * 6 + 1]);
							    	ti.mPeriod       = cmds[baseIdx + j * 6 + 2];
							    	ti.mPowerOnTime  = cmds[baseIdx + j * 6 + 3];
							    	ti.mPowerOffTime = cmds[baseIdx + j * 6 + 4];
							    	ti.mEnable       = cmds[baseIdx + j * 6 + 5].equals("1") ? true : false;
							    	timerList.add(ti);
							    }
					        }
					        
					        String user_cookie 	= cmds[0];
					        String module_id 	= cmds[3];
					        String dev_version	= cmds[6];
					        String module_type	= cmds[7];
					        String module_name	= cmds[4];
					        String module_mac	= cmds[5];
					        Integer protocol_mode= Integer.parseInt(cmds[8]);
					        int pwr_status	= Integer.parseInt(cmds[9]);
					        Integer flash_mode	= Integer.parseInt(cmds[10]);
					        Integer color_red	= Integer.parseInt(cmds[11]);
					        Integer color_green	= Integer.parseInt(cmds[12]);
					        Integer color_blue	= Integer.parseInt(cmds[13]);
					        Integer timer_count	= Integer.parseInt(cmds[14]);
					        
					        Map<String, Object> map = new HashMap<String, Object>();
					        // 放入对应MAP映射List中；
					        map.put("cookie", user_cookie);
					        map.put("udp_moduleid", module_id);
					        map.put("udp_ip", peerIP);				
					        map.put("udp_port", peerPort);			
					        map.put("cb_ipAddress", (Boolean)true);
					        
					        // 新增显示内容 lishimin
					        map.put("plug_img_online", 	(Boolean)true);
					        map.put("plug_img_timer",  	timer_count);
					        map.put("plug_img_power", 	(Boolean)(((pwr_status & 0x0001) == 0) ? false : true));
					        map.put("plug_img_light", 	(Boolean)(((pwr_status & 0x0002) == 0) ? false : true));
					        map.put("plug_img_usb", 	(Boolean)(((pwr_status & 0x0004) == 0) ? false : true));
					        map.put("plug_txt_connect", "");
					        map.put("plug_txt_name", 	module_name);
					        map.put("plug_txt_mac", 	module_mac);		// 不再需要从内存中获取了，getMacFromModuleID(module_id));
					        
					        // 循环处理m_tjData, 若在m_tjData中已经存在了，就不再处理；
					        j = 0;
					        for (; j < m_tjData.size(); j++) {
					        	String t_dup_moduleid = m_tjData.get(j).get("udp_moduleid").toString();
					        	if (t_dup_moduleid.equals(cmds[3])) {
							        // 收到设备发过来的信息后，需要给设备反馈一个LOGIN信息；例如： "0,SUCCESS,20160203121223#"
							        String feedback_cmd = "0" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL + "SUCCESS" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL + PubFunc.getTimeString() + StringUtils.PACKAGE_END_SYMBOL;
							        // 同步方式
							        sendCommandRequestWithUDPSocket(peerIP, 50030, UDP_DEST_PORT, UDP_TIME_OUT, 1, feedback_cmd, false, false);				        		
					        		//break;
					        	}
					        }
					        
					        //if (j >= m_tjData.size())
					        {
					        	m_tjData.add(map);	
					        	
						        // 收到设备发过来的信息后，需要给设备反馈一个LOGIN信息；例如： "0,SUCCESS,20160203121223#"
						        String feedback_cmd = "0" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL + "SUCCESS" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL + PubFunc.getTimeString() + StringUtils.PACKAGE_END_SYMBOL;
						        
						        // 同步方式
						        sendCommandRequestWithUDPSocket(peerIP, 50030, UDP_DEST_PORT, UDP_TIME_OUT, 1, feedback_cmd, false, false);
						        
						        // 异步方式
	//					        new send_NetWorkData_Async(peerIP, feedback_cmd).execute();
						        
						        receivedResponse = true;
						        
						        // 同步在ListView显示；
								mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW_SCANPORT);
								try {
									Thread.sleep(THREAD_SLEEP_TIME);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					        }
				      }
			
				    	// Receive Catch
				      } catch (InterruptedIOException e) { // 当receive不到信息或者receive时间超过3秒时，就向服务器重发请求
				    	  Log.v("ScanPort", "Receive Timed out, " + " have received...");
				    	  tries_recv += 1;
	
	//			    	  // 测试使用，真正发布时，必须注释掉
	//			    	  Message msg = new Message();
	//			    	  msg.what = MSG_UPDATE_SHOW_INFO;
	//			    	  msg.obj = "[广播] 接收次数：" + String.valueOf(i);
	//			    	  mHandler.sendMessage(msg);
				      }
				    // Send Catch
		    	 } catch (InterruptedIOException e) { // 当receive不到信息或者receive时间超过3秒时，就向服务器重发请求
		    		 
				        Log.v("ScanPort", "Send Timed out, " + (UDP_MAX_RETRIES_SEND - tries_send) + " more tries...");
				 }
		    	tries_send += 1;
		    } while ((tries_send < UDP_MAX_RETRIES_SEND) && (tries_recv < UDP_MAX_RETRIES_RECV));
		
		    socket.close();
		    socket = null;
		    
		    // 对备份数据再次尝试连接，若存在也增加进来；
		    for (k = 0; k < m_tjData_bak.size(); k++) {
		    	// 如果k在m_tjData中已经存在，则不发送；
		    	String t_bak_moduleid = m_tjData_bak.get(k).get("udp_moduleid").toString();
		    	for (j = 0; j < m_tjData.size(); j++) {
		        	String t_dup_moduleid = m_tjData.get(j).get("udp_moduleid").toString();
		        	if (t_dup_moduleid.equals(t_bak_moduleid)) {
		        		break;
		        	}
		    	}
		    	if (j < m_tjData.size()) {
		    		continue;
		    	}
		    	
		    	String r_ipAddress = m_tjData_bak.get(k).get("udp_ip").toString();
		    	remoteInetAddress = InetAddress.getByName(r_ipAddress); // 服务器地址
		    	
		    	socket = new DatagramSocket(this.localPort);
			    socket.setSoTimeout(this.timeout); 								// 设置阻塞时间
			
			    sendPacket = new DatagramPacket(bytesToSend, 	// 相当于将发送的信息打包
			        bytesToSend.length, remoteInetAddress, this.remotePort);
			
		    	// Send Function
		    	try {
		    		socket.send(sendPacket);          						// 发送信息
		    	
		    		// Receive Function
				    try {
				    	Map<String, Object> map = null;
				        socket.receive(receivePacket); 						// 接收信息
				        
				        String peerIP = receivePacket.getAddress().getHostAddress();
				        String peerPort = "0";
				        String receiveData = new String(receivePacket.getData());
				        receiveData = receiveData.substring(0, receiveData.indexOf("#"));
				        String[] cmds = receiveData.split(",");
				        
				        // 检查返回的命令字格式是否正确；
				        if(cmds == null || cmds.length < 15){
							continue;
						}
	
				        String cmd_name 	= cmds[1];
				        if (!cmd_name.equals(SmartPlugMessage.CMD_SP_LOGIN_MODULE)) {
				        	continue;
				        }
				        
				        // 写入APP数据库
				        {
							SmartPlugDefine aPlug = new SmartPlugDefine();
						    aPlug.mUserName  = "ShakeShake";
						    aPlug.mPlugId    = cmds[3];
						    aPlug.mPlugName  = cmds[4];
						    aPlug.mMAC 		 = cmds[5];
						    aPlug.mVersion   = cmds[6];
						    aPlug.mModuleType= cmds[7];
						    
						    aPlug.mProtocolMode = Integer.parseInt(cmds[8]);
						    aPlug.mIsOnline  	= true;
						    aPlug.mDeviceStatus = Integer.parseInt(cmds[9]);
						    aPlug.mIPAddress = peerIP;
						    aPlug.mFlashMode = Integer.parseInt(cmds[10]);
						    aPlug.mColor_R   = Integer.parseInt(cmds[11]);
						    aPlug.mColor_G   = Integer.parseInt(cmds[12]);
						    aPlug.mColor_B   = Integer.parseInt(cmds[13]);
						    plugs.add(aPlug);
						    
						    int timerCount = Integer.parseInt(cmds[14]);
							int baseIdx = 15;
						    for (j = 0; j < timerCount; j++) {
						    	TimerDefine ti   = new TimerDefine();
						    	ti.mPlugId       = aPlug.mPlugId;
						    	ti.mTimerId      = Integer.parseInt(cmds[baseIdx + j * 6 + 0]);
						    	ti.mType         = Integer.parseInt(cmds[baseIdx + j * 6 + 1]);
						    	ti.mPeriod       = cmds[baseIdx + j * 6 + 2];
						    	ti.mPowerOnTime  = cmds[baseIdx + j * 6 + 3];
						    	ti.mPowerOffTime = cmds[baseIdx + j * 6 + 4];
						    	ti.mEnable       = cmds[baseIdx + j * 6 + 5].equals("1") ? true : false;
						    	timerList.add(ti);
						    }
				        }
				        
				        String user_cookie 	= cmds[0];
				        String module_id 	= cmds[3];
				        String dev_version	= cmds[6];
				        String module_type	= cmds[7];
				        String module_name	= cmds[4];
				        String module_mac	= cmds[5];
				        Integer protocol_mode= Integer.parseInt(cmds[8]);
				        int pwr_status	= Integer.parseInt(cmds[9]);
				        Integer flash_mode	= Integer.parseInt(cmds[10]);
				        Integer color_red	= Integer.parseInt(cmds[11]);
				        Integer color_green	= Integer.parseInt(cmds[12]);
				        Integer color_blue	= Integer.parseInt(cmds[13]);
				        Integer timer_count	= Integer.parseInt(cmds[14]);
	
				        // 放入对应MAP映射List中；
				        map = new HashMap<String, Object>();
				        map.put("cookie", user_cookie);
				        map.put("udp_moduleid", module_id);
				        map.put("udp_ip", peerIP);				
				        map.put("udp_port", peerPort);			
				        map.put("cb_ipAddress", (Boolean)true);
				        
				        // 新增显示内容 lishimin
				        map.put("plug_img_online", 	(Boolean)true);
				        map.put("plug_img_timer",  	timer_count);
				        map.put("plug_img_power", 	(Boolean)(((pwr_status & 0x0001) == 0) ? false : true));
				        map.put("plug_img_light", 	(Boolean)(((pwr_status & 0x0002) == 0) ? false : true));
				        map.put("plug_img_usb", 	(Boolean)(((pwr_status & 0x0004) == 0) ? false : true));
				        map.put("plug_txt_connect", "");
				        map.put("plug_txt_name", 	module_name);
				        map.put("plug_txt_mac", 	module_mac);		// 不再需要从内存中获取了，getMacFromModuleID(module_id));
				        
				        // 循环处理m_tjData, 若在m_tjData中已经存在了，就不再处理；
				        j = 0;
				        for (j = 0; j < m_tjData.size(); j++) {
				        	String t_dup_moduleid = m_tjData.get(j).get("udp_moduleid").toString();
				        	if (t_dup_moduleid.equals(cmds[3])) {
				        		break;
				        	}
				        }
				        if (j >= m_tjData.size()) {
				        	m_tjData.add(map);	
				        	
					        // 收到设备发过来的信息后，需要给设备反馈一个LOGIN信息；例如： "0,SUCCESS,20160203121223#"
					        String feedback_cmd = "0" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL + "SUCCESS" + StringUtils.PACKAGE_RET_SPLIT_SYMBOL + PubFunc.getTimeString() + StringUtils.PACKAGE_END_SYMBOL;
					        
					        // 同步方式
					        sendCommandRequestWithUDPSocket(peerIP, 5010, UDP_DEST_PORT, UDP_TIME_OUT, 1, feedback_cmd, false, false);
					        
					        // 异步方式
	//					        new send_NetWorkData_Async(peerIP, feedback_cmd).execute();
					        
					        receivedResponse = true;
					        
					        // 同步在ListView显示；
							mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW_SCANPORT);
							try {
								Thread.sleep(THREAD_SLEEP_TIME);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				        }
			
				      } catch (InterruptedIOException e) { // 当receive不到信息或者receive时间超过3秒时，就向服务器重发请求
				    	  Log.v("ScanPort", "Receive Timed out, " + " have received...");
				      }
		    	 } catch (InterruptedIOException e) { // 当receive不到信息或者receive时间超过3秒时，就向服务器重发请求
				        Log.v("ScanPort", "Send Timed out, " + (UDP_MAX_RETRIES_SEND - tries_send) + " more tries...");
				 }
		    	
		    	socket.close();
		    }
		    
		    for (j = 0; j < m_tjData.size(); j++) {
	        	String t_dup_moduleid = m_tjData.get(j).get("udp_moduleid").toString();
	        	k = 0;
	        	for (k = 0; k < m_tjData_bak.size(); k++) {
	        		String t_bak_moduleid = m_tjData_bak.get(k).get("udp_moduleid").toString();
	        		if (t_dup_moduleid.equals(t_bak_moduleid)) {
	            		break;
	            	}	
	    	    }
	        	if (k >= m_tjData_bak.size()) {
	        		Map<String, Object> map = m_tjData.get(j);
	        		m_tjData_bak.add(map);
	        		
	        	}
	        }
		    
		    if (receivedResponse) {
		    	b_findedIP = true;
		    }
		    
		}
	}


//------------------------------------------------------

	public final class ViewHolder_listitem {
		public ThingzdoCheckBox  	cb_ipAddress;
		public RelativeLayout       plug_layout;
		public TextView				plug_txt_name;
		public ImageView			plug_img_online;
		public ImageView			plug_img_timer;
		public ImageView			plug_img_power;
		public ImageView			plug_img_light;
		public ImageView			plug_img_usb;
		public TextView				plug_txt_connect;
		public TextView				plug_txt_mac;
		public TextView 			tv_ipAddress;
		
		public void ViewData(Map<String, Object> data, final int position){
			
			cb_ipAddress.setChecked((Boolean)data.get("cb_ipAddress"));
	//		tv_ipAddress.setText((String)data.get("udp_ip"));
	//		tv_ipAddress.setText((String)data.get("udp_ip") + ":" + (String)data.get("udp_moduleid"));
			
			// lishimin 新增显示信息
			plug_txt_name.setText((String)data.get("plug_txt_name") + "(" + (String)data.get("udp_moduleid") + ")");
			plug_img_online.setImageResource((Boolean)data.get("plug_img_online") == true ? R.drawable.smp_online : R.drawable.smp_offline);
			if ((Integer)data.get("plug_img_timer") == 0) {
				plug_img_timer.setVisibility(View.GONE);
			} else {
				plug_img_timer.setVisibility(View.VISIBLE);
				plug_img_timer.setImageResource(R.drawable.smp_timer_enable);
			}
			plug_img_power.setImageResource((Boolean)data.get("plug_img_power") == true ? R.drawable.smp_power_on_small : R.drawable.smp_power_off_small);
			plug_img_light.setImageResource((Boolean)data.get("plug_img_light") == true ? R.drawable.smp_light_on_small : R.drawable.smp_light_off_small);
			plug_img_usb.setImageResource((Boolean)data.get("plug_img_usb") == true ? R.drawable.smp_usb_on_small : R.drawable.smp_usb_off_small);
			
			plug_txt_mac.setText("MAC:" + ((String)data.get("plug_txt_mac")).toUpperCase(Locale.getDefault()));
			tv_ipAddress.setText("IP:" + (String)data.get("udp_ip"));
			
			plug_layout.setOnClickListener(selectPlug);
			plug_layout.setTag((String)data.get("udp_ip"));
			plug_layout.setContentDescription((String)data.get("udp_moduleid"));
			
			
			// Lishimin
	//		cb_ipAddress.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	//
	//			@Override
	//			public void onCheckedChanged(CompoundButton buttonView,
	//					boolean isChecked) {
	//				
	//				m_tjData.get(position).put("cb_ipAddress", isChecked);
	//			}
	//			
	//		});
			
			cb_ipAddress.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					m_tjData.get(position).put("cb_ipAddress", cb_ipAddress.isChecked());
	
				}
			});
			
			
		}
	}

	private String mSelectedPlugId = "";
	private String mSelectedPlugIP = "";
	private OnClickListener selectPlug = new OnClickListener() {
		@Override
		public void onClick(View view) {
			mSelectedPlugId = view.getContentDescription().toString();
			mSelectedPlugIP = view.getTag().toString();
			
			Intent intent = new Intent();
			intent.setClass(ShakeShake.this, PlugDetailActivity.class);
			String plugId = mSelectedPlugId; 
			intent.putExtra("PLUGID", plugId);
			intent.putExtra("PLUGIP", mSelectedPlugIP);
			startActivity(intent);		
		}
	};

/*private Handler shakeConnectHandler = new Handler() {
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case 0:
			PubFunc.thinzdoToast(ShakeShake.this, (String)msg.obj);
			Intent intent = new Intent(PubDefine.PLUG_SHAKE_FAIL_ACTION);
			ShakeShake.this.sendBroadcast(intent);
			break;
		case 1:
		default:
			PubFunc.thinzdoToast(ShakeShake.this, ShakeShake.this.getString(R.string.oper_error));
			break;
		}
	};
};*/


	public class ListItemAdapter extends BaseAdapter{
	
		private LayoutInflater mInflater;
		
		public ListItemAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return m_tjData.size();
		}
	
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {			
			ViewHolder_listitem holder = null;
			if (convertView == null) {				
				holder = new ViewHolder_listitem();
	
				convertView = mInflater.inflate(R.layout.list_item_shake_ipaddress, null);
				holder.plug_layout = (RelativeLayout)convertView.findViewById(R.id.layout_plug_item);
				holder.cb_ipAddress = (ThingzdoCheckBox)convertView.findViewById(R.id.cb_ipAddress);
				holder.plug_txt_name = (TextView) convertView.findViewById(R.id.plug_txt_name);
				holder.plug_img_online = (ImageView) convertView.findViewById(R.id.plug_img_online);
				holder.plug_img_timer = (ImageView) convertView.findViewById(R.id.plug_img_timer);
				holder.plug_img_power = (ImageView) convertView.findViewById(R.id.plug_img_power);
				holder.plug_img_light = (ImageView) convertView.findViewById(R.id.plug_img_light);
				holder.plug_img_usb = (ImageView) convertView.findViewById(R.id.plug_img_usb);
				holder.plug_txt_connect = (TextView) convertView.findViewById(R.id.plug_txt_connect);
				holder.plug_txt_mac = (TextView) convertView.findViewById(R.id.plug_txt_mac);
				holder.tv_ipAddress = (TextView) convertView.findViewById(R.id.tv_ipAddress);
				
				convertView.setTag(holder);
				
			} else {				
				holder = (ViewHolder_listitem)convertView.getTag();
			}
			
			if (holder != null && m_tjData != null && position < m_tjData.size()) {
				Map<String, Object> data = m_tjData.get(position);
		    	convertView.setBackgroundColor(Color.TRANSPARENT);
	      	  	holder.ViewData(data, position);
		    }
	
			return convertView;
		}		
	}

	private void saveScanIPAddressData() {	//lishimin NEED MODIFY
	
		String scanport_ip_nums	= "scanport_ip_nums";
		String cookie 			= "cookie";
		String udp_moduleid 	= "udp_moduleid";
		String udp_ip 			= "udp_ip";
		String udp_port 		= "udp_port";
		String cb_ipAddress 	= "cb_ipAddress";
		
		String plug_img_online	= "plug_img_online";
		String plug_img_timer	= "plug_img_timer";
		String plug_img_power	= "plug_img_power";
		String plug_img_light	= "plug_img_light";
		String plug_img_usb		= "plug_img_usb";
		String plug_txt_connect	= "plug_txt_connect";
		String plug_txt_name	= "plug_txt_name";
		String plug_txt_mac		= "plug_txt_mac";
		
		
		editor = mSharedPreferences.edit();
		editor.putInt(scanport_ip_nums, m_tjData.size());
		
		for (int i = 0; i < m_tjData.size(); i++) {
			
			editor.putString(i + "_" + cookie, 			(String) m_tjData.get(i).get(cookie));
			editor.putString(i + "_" + udp_moduleid, 	(String) m_tjData.get(i).get(udp_moduleid));
			editor.putString(i + "_" + udp_ip, 			(String) m_tjData.get(i).get(udp_ip));
			editor.putString(i + "_" + udp_port, 		(String) m_tjData.get(i).get(udp_port));
			editor.putBoolean(i + "_" + cb_ipAddress, 	(Boolean) m_tjData.get(i).get(cb_ipAddress));
			
			editor.putBoolean(i + "_" + plug_img_online, 	(Boolean) m_tjData.get(i).get(plug_img_online));
			editor.putInt(i + "_" + plug_img_timer, 	(Integer) m_tjData.get(i).get(plug_img_timer));
			editor.putBoolean(i + "_" + plug_img_power, 	(Boolean) m_tjData.get(i).get(plug_img_power));
			editor.putBoolean(i + "_" + plug_img_light, 	(Boolean) m_tjData.get(i).get(plug_img_light));
			editor.putBoolean(i + "_" + plug_img_usb, 		(Boolean) m_tjData.get(i).get(plug_img_usb));
			editor.putString(i + "_" + plug_txt_connect, 	(String) m_tjData.get(i).get(plug_txt_connect));
			editor.putString(i + "_" + plug_txt_name, 		(String) m_tjData.get(i).get(plug_txt_name));
			editor.putString(i + "_" + plug_txt_mac, 		(String) m_tjData.get(i).get(plug_txt_mac));
		}
		
		editor.commit();
	    
	}

	private void loadScanIPAddressData() {
	
		String scanport_ip_nums	= "scanport_ip_nums";
		String cookie 			= "cookie";
		String udp_moduleid 	= "udp_moduleid";
		String udp_ip 			= "udp_ip";
		String udp_port 		= "udp_port";
		String cb_ipAddress 	= "cb_ipAddress";
		
		String plug_img_online	= "plug_img_online";
		String plug_img_timer	= "plug_img_timer";
		String plug_img_power	= "plug_img_power";
		String plug_img_light	= "plug_img_light";
		String plug_img_usb		= "plug_img_usb";
		String plug_txt_connect	= "plug_txt_connect";
		String plug_txt_name	= "plug_txt_name";
		String plug_txt_mac		= "plug_txt_mac";
		
		m_tjData.clear();
		m_tjData_bak.clear();
		
		int t_scanport_ip_nums = mSharedPreferences.getInt(scanport_ip_nums, 0);
				
		Map<String, Object> map = null;
		for (int i = 0; i < t_scanport_ip_nums; i++) {
			map = new HashMap<String, Object>();
			
	        map.put(cookie, 		mSharedPreferences.getString(i + "_" + cookie, ""));
	        map.put(udp_moduleid, 	mSharedPreferences.getString(i + "_" + udp_moduleid, ""));
	        map.put(udp_ip, 		mSharedPreferences.getString(i + "_" + udp_ip, ""));
	        map.put(udp_port, 		mSharedPreferences.getString(i + "_" + udp_port, ""));
	        map.put(cb_ipAddress, 	(Boolean)mSharedPreferences.getBoolean(i + "_" + cb_ipAddress, (Boolean)true));
	        
	        map.put(plug_img_online, 	(Boolean)mSharedPreferences.getBoolean(i + "_" + plug_img_online, (Boolean)true));
	        map.put(plug_img_timer, 	(Integer)mSharedPreferences.getInt(i + "_" + plug_img_timer, 0));
	        map.put(plug_img_power, 	(Boolean)mSharedPreferences.getBoolean(i + "_" + plug_img_power, (Boolean)true));
	        map.put(plug_img_light, 	(Boolean)mSharedPreferences.getBoolean(i + "_" + plug_img_light, (Boolean)true));
	        map.put(plug_img_usb, 	(Boolean)mSharedPreferences.getBoolean(i + "_" + plug_img_usb, (Boolean)true));
	        map.put(plug_txt_connect, 		mSharedPreferences.getString(i + "_" + plug_txt_connect, ""));
	        map.put(plug_txt_name, 		mSharedPreferences.getString(i + "_" + plug_txt_name, ""));
	        map.put(plug_txt_mac, 		mSharedPreferences.getString(i + "_" + plug_txt_mac, ""));
			
	        m_tjData.add(map);
	        m_tjData_bak.add(map);
		}
		
	}

	private InetAddress getBroadcastAddress(Context context, String maskIP) throws UnknownHostException {
	    WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	    DhcpInfo dhcp = wifi.getDhcpInfo();
	    if(dhcp == null) {
	        return InetAddress.getByName(maskIP);
	    }
	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	        quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads);
	}

	// 根据 不同的语音内容 发送不同的命令
	@SuppressLint("NewApi") 
	protected void parseCommand(String text) {
		String cmd_text = "";
		
		Map<String, Object> m_mapCmd = null;
		m_mapCmd = new HashMap<String, Object>();
		
		String[] key_light_on 	= {"开", "灯"};
		String[] key_light_off 	= {"关", "灯"};
		String[] key_power_on 	= {"开", "电"};
		String[] key_power_off 	= {"关", "电"};
		String[] key_usb_on 	= {"开", "USB"};
		String[] key_usb_off 	= {"关", "USB"};
		String[] key_rgb_red 	= {"红"};
		String[] key_rgb_green 	= {"绿"};
		String[] key_rgb_blue 	= {"蓝"};
		String[] key_rgb_purple	= {"紫"};
		String[] key_rgb_white	= {"白"};
		String[] key_rgb_black	= {"黑"};
		String[] key_rgb_blue_flash	= {"闪烁"};
		
		m_mapCmd.put("20150101121212,POWER,test,12345678,1#", key_power_on);
		m_mapCmd.put("20150101121212,POWER,test,12345678,0#", key_power_off);
		m_mapCmd.put("20150101121212,LIGHT,test,12345678,1#", key_light_on);
		m_mapCmd.put("20150101121212,LIGHT,test,12345678,0#", key_light_off);
		m_mapCmd.put("20150101121212,USB,test,12345678,1#", key_usb_on);
		m_mapCmd.put("20150101121212,USB,test,12345678,0#", key_usb_off);
		m_mapCmd.put("20150101121212,RGB,test,12345678,0,255,1,1#", key_rgb_red);
		m_mapCmd.put("20150101121212,RGB,test,12345678,0,1,255,1#", key_rgb_green);
		m_mapCmd.put("20150101121212,RGB,test,12345678,0,1,1,255#", key_rgb_blue);
		m_mapCmd.put("20150101121212,RGB,test,12345678,0,255,1,255#", key_rgb_purple);
		m_mapCmd.put("20150101121212,RGB,test,12345678,0,255,255,255#", key_rgb_white);
		m_mapCmd.put("20150101121212,RGB,test,12345678,0,1,1,1#", key_rgb_black);
		m_mapCmd.put("20150101121212,RGB,test,12345678,1,1,1,255#", key_rgb_blue_flash);
		
		for (String key : m_mapCmd.keySet()) {
			String[] strValues = (String[]) m_mapCmd.get(key);
			boolean result = true;
			for (int i = 0; i < strValues.length; i++) {
				if (text.indexOf(strValues[i]) == -1) {
					result = false;
					break;
				}
			}
			if (result == true) {
				cmd_text = key;
				break;
			}
		}
	
		if (!cmd_text.isEmpty()) {

			for (int i = 0; i < m_tjData.size(); i++) {
				if ((Boolean)m_tjData.get(i).get("cb_ipAddress") == true) {
					String ipAddress = (String) m_tjData.get(i).get("udp_ip");
					new send_NetWorkData_Async(ipAddress, cmd_text).execute();
				}
			}
			b_switch = !b_switch;
		}
	}

	private String getMacFromModuleID(String ModuleID) {
		SharedPreferences router_sp = getSharedPreferences("Router", Activity.MODE_PRIVATE);
	
		String mac_str = router_sp.getString(ModuleID + "_MAC", "00:00:00:00:00:00");
		
		return mac_str;
	}

	private void modifyListViewData(String moduleID, String key, Boolean value) {
		
		for (int j = 0; j < m_tjData.size(); j++) {
	    	String t_dup_moduleid = m_tjData.get(j).get("udp_moduleid").toString();
	    	if (t_dup_moduleid.equals(moduleID)) {
	    		m_tjData.get(j).put(key, value);
	    		break;
	    	}
	    }
		
		return;
	}

	private void add2DB(ArrayList<SmartPlugDefine> plugs, ArrayList<TimerDefine> timers) {
		SmartPlugHelper mPlugHelper = new SmartPlugHelper(SmartPlugApplication.getContext());
		SmartPlugTimerHelper mTimerHelper = new SmartPlugTimerHelper(SmartPlugApplication.getContext()); 
		mPlugHelper.clearSmartPlug();
	
		for (int i = 0; i < plugs.size(); i++) {
			long id = mPlugHelper.addSmartPlug(plugs.get(i));
//			PubFunc.log("addSmartPlug:" + id);	
			
			if (id  > 0) {
				for (int j = 0; j < timers.size(); j++) {
					TimerDefine time = timers.get(j);
					if (time.mPlugId == plugs.get(i).mPlugId) {
						mTimerHelper.addTimer(time);
					}
				}
			}
			
		}
		
		mPlugHelper = null;
		mTimerHelper = null;
	}
	
	 
		public class Add2DBUpdateTableTask extends AsyncTask<Void,Void,Void> {
		ArrayList<SmartPlugDefine> plugs_;
		ArrayList<TimerDefine> timerList_;
	
		public Add2DBUpdateTableTask(ArrayList<SmartPlugDefine> plugs,
				ArrayList<TimerDefine> timerList) {
			plugs_ 		= plugs;
			timerList_	= timerList;
		}
	
		@Override
		protected Void doInBackground(Void... arg0) {
	
			add2DB(plugs_, timerList_);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
	
		}
	};	

	private void modifyPlugInfo(ArrayList<SmartPlugDefine> plugs) {
		SmartPlugHelper mPlugHelper = new SmartPlugHelper(SmartPlugApplication.getContext());
		
		for (int i = 0; i < plugs.size(); i++) {
			mPlugHelper.modifySmartPlug(plugs.get(i));
		}
		
		mPlugHelper = null;
	}


	public class ModifyUpdateTableTask extends AsyncTask<Void,Void,Void> {
		ArrayList<SmartPlugDefine> plugs_;
	
		public ModifyUpdateTableTask(ArrayList<SmartPlugDefine> plugs) {
			plugs_	= plugs;
		}
	
		@Override
		protected Void doInBackground(Void... arg0) {
			modifyPlugInfo(plugs_);
				
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
	
		}
	};
}