package com.thingzdo.ui.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class DetailBatteryActivity extends TitledActivity implements OnClickListener {
	
	private SmartPlugHelper  mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";
	private boolean mOnline = false;
	
	private String mErrorMsg =  "";
	
	private TextView tv_charge;
	private RadioButton cb_type_day;
	private RadioButton cb_type_week;
	private RadioButton cb_type_month;
	
	private TextView tv_map_trail;
	
	private RevCmdFromSocketThread mTcpSocketThread = null;
	private List<MAP_POINT> list_points = new ArrayList<MAP_POINT>();
	
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
	
	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(DetailBatteryActivity.this, intent)) {
					
				}
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_BATTERY_QUERYTRAIL_ACTION)) {
				timeoutHandler.removeCallbacks(timeoutProcess);				
				if (null != mProgress) {
					mProgress.dismiss();
				}
				
				String plugid = intent.getStringExtra("PLUGID");
				int result = intent.getIntExtra("RESULT", 0);
				int status = intent.getIntExtra("STATUS", 0);
				String message = intent.getStringExtra("MESSAGE");
				if (result == 0) {
					update_map(message);
				} else {
					PubFunc.thinzdoToast(DetailBatteryActivity.this, message);
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_BATTERY_NOTIFYENERGE_ACTION)) {
				timeoutHandler.removeCallbacks(timeoutProcess);				
				if (null != mProgress) {
					mProgress.dismiss();
				}
				
				int status = intent.getIntExtra("STATUS", 0);
				String energe = intent.getStringExtra("ENERGE");
				if (status == 0) {
					update_energe(energe);
				} else {
					// ...
				}
			}
			if (intent.getAction().equals(PubDefine.PLUG_BATTERY_QUERYENERGE_ACTION)) {
				timeoutHandler.removeCallbacks(timeoutProcess);				
				if (null != mProgress) {
					mProgress.dismiss();
				}
				
				int status = intent.getIntExtra("STATUS", 0);
				String oper_date = intent.getStringExtra("OPER_DATE");
				String energe = intent.getStringExtra("ENERGE");
				if (status == 0) {
					update_energe(energe);
				} else {
					// ...
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateBaiduMap(savedInstanceState, R.layout.activity_detail_battery, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);		
		setTitleRightButton(R.string.smartplug_title_plug_detail, R.drawable.title_btn_selector, this);
		
		// 初始化百度地图
        mMapView = (MapView) findViewById(R.id.map_view);
     	mBaiduMap = mMapView.getMap();
		
		mPlugHelper = new SmartPlugHelper(this);
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";	
		} 
		mPlugIp = intent.getStringExtra("PLUGIP");
		mOnline = intent.getBooleanExtra("ONLINE", false);
		
		init();
				
		if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
			setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		} else {
			setTitleLeftButton(R.string.smartplug_esc, R.drawable.title_btn_selector, this);
		}		
		
		if (PubDefine.SmartPlug_Connect_Mode.WiFi == PubDefine.g_Connect_Mode) {
			setTitleRightButton(R.string.smartplug_mgr_reselect, R.drawable.title_btn_selector, this);
		}
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_BATTERY_QUERYTRAIL_ACTION);
		filter.addAction(PubDefine.PLUG_BATTERY_NOTIFYENERGE_ACTION);
		filter.addAction(PubDefine.PLUG_BATTERY_QUERYENERGE_ACTION);
		registerReceiver(mDetailRev, filter);

		UDPClient.getInstance().setIPAddress(mPlugIp);

		tv_charge  = (TextView)findViewById(R.id.tv_charge);
		tv_map_trail = (TextView)findViewById(R.id.tv_map_trail);
		cb_type_day = (RadioButton)findViewById(R.id.cb_type_day);
		cb_type_week = (RadioButton)findViewById(R.id.cb_type_week);
		cb_type_month = (RadioButton)findViewById(R.id.cb_type_month);
        
//        if (mOnline == true) {
//        	cb_type_day.setEnabled(true);
//        	cb_type_week.setEnabled(true);
//        	cb_type_month.setEnabled(true);
//        } else {
//        	cb_type_day.setEnabled(false);
//        	cb_type_week.setEnabled(false);
//        	cb_type_month.setEnabled(false);
//        }
        
        cb_type_day.setOnClickListener(DetailBatteryActivity.this);
        cb_type_week.setOnClickListener(DetailBatteryActivity.this);
        cb_type_month.setOnClickListener(DetailBatteryActivity.this);
        
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}
		
		setTitle(mPlug.mPlugName);
		
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
			@Override
			public void run() {
				sendcommandtoBatteryEnerge();
			}
        };
        timer.schedule(task, 1000);
	}

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();
		init();
		mMapView.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mDetailRev);
		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread.setRunning(false);
		}
		mMapView.onDestroy();
        MapView.setMapCustomEnable(false);
        mMapView = null;
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:  // WiFi模式 退出时，需要close掉 TCP连接
			disconnectSocket();
			finish();
			break;
		// Window Controller
		case R.id.cb_type_day:
			update_radio_button(R.id.cb_type_day);
			SendCommand(2);
			break;
		case R.id.cb_type_week:
			update_radio_button(R.id.cb_type_week);
			SendCommand(3);
			break;
		case R.id.cb_type_month:
			update_radio_button(R.id.cb_type_month);
			SendCommand(4);
			break;
		default:
			break;
		}
	}
	
	private void convertToLL(List<MAP_POINT> list_points, List<LatLng> points) {
		for (int i = 0; i < list_points.size(); i++) {
			LatLng p1 = new LatLng(list_points.get(i).getDimension(), list_points.get(i).getLongitude());
			points.add(p1);
		}
	}
	
	/**
     * 添加点、线、多边形、圆、文字
     */
    public void addCustomElements(List<MAP_POINT> list_points) {
    	// 清除所有图层
        mMapView.getMap().clear();
        
        List<LatLng> points = new ArrayList<LatLng>();
        convertToLL(list_points, points);
        
        // 添加折线
        OverlayOptions ooPolyline = new PolylineOptions().width(10)
                .color(0xAAFF0000).points(points);
        mBaiduMap.addOverlay(ooPolyline);
        
        // 添加文字 
        LatLng llText = new LatLng(39.86923, 116.397428);
        OverlayOptions ooText = new TextOptions().bgColor(0xAAFFFF00)
        		.fontSize(24).fontColor(0xFFFF00FF)
        		.text("百度地图SDK").rotate(-30).position(llText); 
        mBaiduMap.addOverlay(ooText);
        
        /*
         * // 添加弧线 OverlayOptions ooArc = new
         * ArcOptions().color(0xAA00FF00).width(4) .points(p1, p2, p3);
         * mBaiduMap.addOverlay(ooArc); 
         * // 添加圆 LatLng llCircle = new
         * LatLng(39.90923, 116.447428); OverlayOptions ooCircle = new
         * CircleOptions().fillColor(0x000000FF) .center(llCircle).stroke(new
         * Stroke(5, 0xAA000000)) .radius(1400); mBaiduMap.addOverlay(ooCircle);
         *
         * LatLng llDot = new LatLng(39.98923, 116.397428); OverlayOptions ooDot
         * = new DotOptions().center(llDot).radius(6) .color(0xFF0000FF);
         * mBaiduMap.addOverlay(ooDot); 
         * // 添加多边形 LatLng pt1 = new
         * LatLng(39.93923, 116.357428); LatLng pt2 = new LatLng(39.91923,
         * 116.327428); LatLng pt3 = new LatLng(39.89923, 116.347428); LatLng
         * pt4 = new LatLng(39.89923, 116.367428); LatLng pt5 = new
         * LatLng(39.91923, 116.387428); List<LatLng> pts = new
         * ArrayList<LatLng>(); pts.add(pt1); pts.add(pt2); pts.add(pt3);
         * pts.add(pt4); pts.add(pt5); OverlayOptions ooPolygon = new
         * PolygonOptions().points(pts) .stroke(new Stroke(5,
         * 0xAA00FF00)).fillColor(0xAAFFFF00); mBaiduMap.addOverlay(ooPolygon);
         * // 添加文字 LatLng llText = new LatLng(39.86923, 116.397428);
         * OverlayOptions ooText = new TextOptions().bgColor(0xAAFFFF00)
         * .fontSize(24).fontColor(0xFFFF00FF).text("百度地图SDK").rotate(-30)
         * .position(llText); mBaiduMap.addOverlay(ooText);
         */
    }
	
	private void update_radio_button(int type) {
		switch (type) {
		case R.id.cb_type_day:
			cb_type_day.setChecked(true);
			cb_type_week.setChecked(false);
			cb_type_month.setChecked(false);
			break;
		case R.id.cb_type_week:
			cb_type_day.setChecked(false);
			cb_type_week.setChecked(true);
			cb_type_month.setChecked(false);
			
			break;
		case R.id.cb_type_month:
			cb_type_day.setChecked(false);
			cb_type_week.setChecked(false);
			cb_type_month.setChecked(true);
			
			break;
		}
	}
	
	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}
	}

	private void update_energe(String energe) {
		tv_charge.setText(energe);
	}
	
	private void update_map(String trails) {
		list_points.clear();
		if (trails.isEmpty() == false) {
			String[] buffer = trails.split(",");
			
			for (int i = 0; i < buffer.length; ) {
				MAP_POINT point = new MAP_POINT();
				point.setOper_date(buffer[i++]);
				point.setLongitude(Double.parseDouble(buffer[i++]));
				point.setDimension(Double.parseDouble(buffer[i++]));
				point.setMeno(String.format("%d", i % 3));
				
				list_points.add(point);
			}
			
			// 更新地图的路线
			addCustomElements(list_points);
		}
		tv_map_trail.setText(trails);
	}
	
	private void disconnectSocket() {
		// WiFi 直连模式下，退出或者重选时，必须close TCP连接；
		/*if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			SmartPlugWifiMgr.disconnectSocket();	
		}*/
		
		return;
	}
	
	private void SendCommand(int type) {
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailBatteryActivity.this, getString(R.string.str_battery_sendcommand), false);
    	mProgress.show();
    	
    	sendcommandtoBatteryController(type);	
	}
	
	// For Battery Controller
	private void sendcommandtoBatteryController(int type) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_BATTERY_QUERYTRAIL)
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(PubStatus.getUserName())
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(mPlugId)
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(String.format("%d", type));
    	
    	//RevCmdFromSocketThread.getInstance().setRunning(true);
    	sendMsg(true, sb.toString(), true);

	}
	
	// For Battery Controller
	private void sendcommandtoBatteryEnerge() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_BATTERY_QUERYENERGE)
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(PubStatus.getUserName())
    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	         .append(mPlugId);
    	
    	//RevCmdFromSocketThread.getInstance().setRunning(true);
    	sendMsg(true, sb.toString(), true);

	}

	public class MAP_POINT {
		private String oper_date;
		private double longitude;
		private double dimension;
		private String meno;
		
		public String getOper_date() {
			return oper_date;
		}
		public void setOper_date(String oper_date) {
			this.oper_date = oper_date;
		}
		public double getLongitude() {
			return longitude;
		}
		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}
		public double getDimension() {
			return dimension;
		}
		public void setDimension(double dimension) {
			this.dimension = dimension;
		}
		public String getMeno() {
			return meno;
		}
		public void setMeno(String strmeno) {
			this.meno = strmeno;
		}
	}

}
