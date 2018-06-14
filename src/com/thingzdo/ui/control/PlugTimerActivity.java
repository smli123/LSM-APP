/**
 * 
 */
package com.thingzdo.ui.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.dataprovider.SmartPlugTimerHelper;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.TimerDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.common.PubDefine.SmartPlug_Connect_Mode;
import com.thingzdo.ui.control.DetailPCCtrlActivity.Question;
import com.thingzdo.ui.control.DetailPCCtrlActivity.QuestionListAdapter;
import com.thingzdo.ui.shakeshake.ShakeSocketMgr;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.ui.wheelutils.ActionSheetDialog;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.OnSheetItemClickListener;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.SheetItemColor;
import com.thingzdo.ui.wheelutils.MyAlertDialog;
import com.thingzdo.ui.wheelutils.ScreenInfo;
import com.thingzdo.ui.wheelutils.WheelMain;

/**
 * @author xiaohui
 *
 */
public class PlugTimerActivity extends TitledActivity implements OnClickListener {
	private String[] mDays = null;
	private String mPlugId = null;
	private SmartPlugDefine mPlug = null;
	private String[] mSelDays = {"0","0","0","0","0","0","0"};
	private String mSelMinute = "0";
	private boolean mIsActive = true;
	private SmartPlugTimerHelper  mTimerHelper = null;
	private SmartPlugHelper mPlugHelper = null;
	private boolean mIsCreateTimer = false; 
	private int mTimerId = 0; 
	private TextView mPwrOnTime  = null;
	private TextView mPwrOffTime = null;
	private WheelMain wheelMain;
	private RelativeLayout mRlPwron = null;
	private RelativeLayout mRlPwroff = null;
	//private ThingzdoCheckBox mCkPwron = null;
	//private ThingzdoCheckBox mCkPwroff = null;
	private Context mContext = null;
	
	private TextView mOnText = null;
	private TextView mOffText = null;
	
	private String mPoweronTime = "";
	private String mPoweroffTime = "";
	private String mPeriod = "";
	
	private ListView lv_macs;
	
	private int mTimerType = 0;
	private TimerDefine mTimertask = null;
	private int mMaxTimerId = 0;
	
	private RelativeLayout layout_poweron_header = null;
	private RelativeLayout layout_poweroff_header = null;
	private RelativeLayout layout_mac = null;
	private RelativeLayout layout_period = null;
	private RelativeLayout layout_period_minute = null;
	private RelativeLayout mRlPeriod = null;
	private RelativeLayout 	ll_mac_low = null;
	private TextView mTxtPeriod = null;
	
	private RelativeLayout mRlPeriod_minute = null;
	private EditText mTxtPeriod_minute = null;
	private EditText et_mac = null;
	private ImageView image_mac = null;
	
	ArrayList<String> macs = null;
	ArrayList<String> macs_show = null;
	
	private ArrayList<String> pcIDs = null;
	
	private QuestionListAdapter questioni_dapter = null;
	private ArrayList<Question> m_QuestionsList = new ArrayList<Question>();
	
	private BroadcastReceiver mTimerTaskReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(PubDefine.PLUG_ADD_TIMERTASK)) {
				if (null != mProgress) {
					mProgress.dismiss();
				}
				timeoutHandler.removeCallbacks(timeoutProcess);					
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
				case 0:  //success
			    	timeoutHandler.removeCallbacks(timeoutProcess);
					//addTimer();
			    	updateTableHandler.sendEmptyMessage(0);
			    	finish();

					break;
				default:  //fail
					PubFunc.thinzdoToast(SmartPlugApplication.getContext(), message);
					finish();
					break;
				}
			}
			
			if (intent.getAction().equals(PubDefine.PLUG_MOD_TIMERTASK)) {
				if (null != mProgress) {
					mProgress.dismiss();
				}
				timeoutHandler.removeCallbacks(timeoutProcess);					
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
				case 0:  //success
			    	timeoutHandler.removeCallbacks(timeoutProcess);		    	
					//modifyTimer();
			    	updateTableHandler.sendEmptyMessage(1);
					
					finish();

					break;
				default:  //fail
					PubFunc.thinzdoToast(SmartPlugApplication.getContext(), message);
					finish();
					break;
				}
			}	
			
			if (intent.getAction().equals(PubDefine.PLUG_SHAKE_FAIL_ACTION)) {
				if (null != mProgress) {
					mProgress.dismiss();
				}				
			}			
		}
	};
	
	private Handler updateTableHandler = new Handler() {
		public void handleMessage(Message msg) {
		    if (0 == msg.what) {			// ADD Timer
		    	addTimer();
		    } else if (1 == msg.what) {		// MOD Timer
		    	modifyTimer();
		    } else if (2 == msg.what) {		// Show Mac Address
		    	et_mac.setText(String.valueOf(msg.obj));
		    } else if (3 == msg.what){
		    	questioni_dapter.notifyDataSetChanged();
		    } else if (4 == msg.what){
		    	int position = msg.arg1;
		    	et_mac.setText(macs.get(position));
		    } else {
		    	
		    }
		};
	};
	
	private void addTimer() {
		mTimertask = new TimerDefine();
		mTimertask.mEnable = true;
		mTimertask.mPeriod = mPeriod;
    	
		String tempModuleID = mPlugId;
    	if (mTimerType == PubDefine.TIMER_TYPE_CLOSEPC && mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG) {		// 1_2
    		tempModuleID = mPlugHelper.getPlugIDFromMac(et_mac.getText().toString());
    	}
    	
		mTimertask.mPlugId = tempModuleID;
		mTimertask.mPowerOnTime  = mPoweronTime;
		mTimertask.mPowerOffTime = (mTimerType == PubDefine.TIMER_TYPE_OPENPC) ? et_mac.getText().toString() : mPoweroffTime;
		mTimertask.mTimerId = mMaxTimerId;
		mTimertask.mType = mTimerType;
		mTimerHelper.addTimer(mTimertask);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState, int layoutResID,
			boolean backToExit) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState, layoutResID, backToExit);
		SmartPlugApplication.resetTask();
	}
	
	private void modifyTimer() {
		mTimertask = mTimerHelper.getTimer(mPlugId, mTimerId);
		mTimertask.mPeriod = mPeriod;
		
		String tempModuleID = mPlugId;
    	if (mTimerType == PubDefine.TIMER_TYPE_CLOSEPC && mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG) {			// 1_2) {
    		tempModuleID = mPlugHelper.getPlugIDFromMac(et_mac.getText().toString());
    	}
    	
		mTimertask.mPlugId = tempModuleID;
		mTimertask.mPowerOnTime  = mPoweronTime;
		mTimertask.mPowerOffTime = mPoweroffTime;
		mTimertask.mTimerId = mTimerId;
		mTimertask.mType = mTimerType;
		mTimerHelper.modifyTimer(mTimertask);
	}
	
	@SuppressLint("CutPasteId") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_plug_timer, false);
		SmartPlugApplication.getInstance().addActivity(this);
		SmartPlugApplication.resetTask();
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		setTitleRightButton(R.string.smartplug_ok, R.drawable.title_btn_selector, this);
		mContext = this;
		String plugIp = getIntent().getStringExtra("PLUGIP");
		//ShakeSocketMgr.createUDPSocket(new ShakeHandler(), plugIp);
		UDPClient.getInstance().setIPAddress(plugIp);
		
		mTimerHelper = new SmartPlugTimerHelper(this);
		mPlugHelper = new SmartPlugHelper(this);
		
		layout_poweron_header = (RelativeLayout) findViewById(R.id.layout_poweron_header);
		layout_poweroff_header = (RelativeLayout) findViewById(R.id.layout_poweroff_header);
		layout_mac = (RelativeLayout) findViewById(R.id.layout_mac);
		layout_period = (RelativeLayout) findViewById(R.id.layout_period);
		layout_period_minute = (RelativeLayout) findViewById(R.id.layout_period_minute);
		ll_mac_low = (RelativeLayout) findViewById(R.id.ll_mac_low);
		ll_mac_low.setVisibility(View.GONE);
		layout_mac.setVisibility(View.GONE);
		
		mRlPeriod = (RelativeLayout) findViewById(R.id.layout_period_header);
		mRlPeriod.setOnClickListener(this);
		mTxtPeriod = (TextView) findViewById(R.id.tv_select_days);		

		// 新增 分钟周期循环的功能；
		mRlPeriod_minute = (RelativeLayout) findViewById(R.id.layout_period_minute);
		if (PubDefine.RELEASE_VERSION == true) {
			mRlPeriod_minute.setVisibility(View.INVISIBLE);
		} else {
			mRlPeriod_minute.setVisibility(View.VISIBLE);
		}
		mTxtPeriod_minute = (EditText) findViewById(R.id.tv_select_minute);
		et_mac = (EditText) findViewById(R.id.et_mac);	
		image_mac = (ImageView) findViewById(R.id.image_mac);
		image_mac.setOnClickListener(this);
		
		layout_mac.setOnClickListener(this);
		
//		et_mac.addTextChangedListener(new TextWatcher() {
//			   @SuppressLint("DefaultLocale") 
//			   @Override
//			   public void onTextChanged(CharSequence s, int start, int before, int count) {
//			   et_mac.removeTextChangedListener(this);
//			    final String s1  = et_mac.getText().toString();
//			    //判断如果是小写的字母的换，就转换
////			    if((s1.charAt(0))-0 >= 97 && (s1.charAt(0))-0 <=122){
//			     new Handler().postDelayed(new Runnable() {
//			      @Override
//			      public void run() {
//			      //小写转大写
//			      et_mac.setText(s1.toUpperCase());
//			      }
//			    }, 300);
////			  }
//			   }
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//			int after) {
//			// TODO Auto-generated method stub
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//			// TODO Auto-generated method stub
//
//			}
//		});
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_ADD_TIMERTASK);
		filter.addAction(PubDefine.PLUG_MOD_TIMERTASK);
		filter.addAction(PubDefine.PLUG_SHAKE_FAIL_ACTION);
		registerReceiver(mTimerTaskReceiver, filter);
		
		mIsActive = true;
		Intent intent = getIntent();
		mPlugId    = intent.getStringExtra("PLUGID");
		//mPlugIp    = intent.getStringExtra("PLUGIp");
		mTimerType = intent.getIntExtra("TIMER_TYPE", 0);
		mIsActive  = intent.getBooleanExtra("ACTIVE", true);
		setTitle(PubFunc.getTimerTypeLabel(mContext, mTimerType) + getString(R.string.str_plug_task));
		String timerid = intent.getStringExtra("TIMERID");
		if (TextUtils.isEmpty(timerid)) {
			mTimerId = 0;	
		} else {
		    mTimerId = Integer.parseInt(timerid);
		}
		
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		
		initView();
		
		pcIDs = mPlugHelper.getAllSmartPlugPCID(mPlugId);
		
		macs = mPlugHelper.getAllSmartPlugMac(mPlugId);
		macs_show = mPlugHelper.getAllSmartPlugMacShow(mPlugId);
		m_QuestionsList.clear();
		for (int i = 0; i < macs_show.size(); i++) {
			Question item = new Question();
			item.setTv_pc_mac(macs_show.get(i));
			m_QuestionsList.add(item);
		}
		
		questioni_dapter = new QuestionListAdapter(this, m_QuestionsList, updateTableHandler);
    	lv_macs.setAdapter(questioni_dapter);
    	
		if (0 == mTimerId) {
			mIsCreateTimer = true;
			initNewTimer();
		} else {
			mIsCreateTimer = false;
			initCurTimer();
		}
		
		if (mIsCreateTimer == false || mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_SIMULATION_PC) {		// 7_1
			layout_mac.setVisibility(View.GONE);				
		}
	}
	

	public void initView() {
		
		mOnText  = (TextView)findViewById(R.id.txtCheckPoweron);
		mOffText = (TextView)findViewById(R.id.txtCheckPoweroff);
		RelativeLayout layTitleOff = (RelativeLayout)findViewById(R.id.layout_poweroff_switcher);
		RelativeLayout layContentOff = (RelativeLayout)findViewById(R.id.lay_poweroff_time);
		
		ImageView imgTypeStart = (ImageView)findViewById(R.id.image_icon_enable);
		ImageView imgTypeStop  = (ImageView)findViewById(R.id.image_icon_disenable);
		
		lv_macs = (ListView)findViewById(R.id.lv_macs);
		
		String strOn = "";
		String strOff = "";
		layout_mac.setVisibility(View.GONE);
		switch (mTimerType) {
		case PubDefine.TIMER_TYPE_POWER:
			imgTypeStart.setImageResource(R.drawable.smp_power_on_small);
			imgTypeStop.setImageResource(R.drawable.smp_power_off_small);
			strOn = getString(R.string.timer_task_poweron);
			strOff = getString(R.string.timer_task_poweroff);
			break;
		case PubDefine.TIMER_TYPE_LIGHT:
			imgTypeStart.setImageResource(R.drawable.smp_light_on_small);
			imgTypeStop.setImageResource(R.drawable.smp_light_off_small);			
			strOn = getString(R.string.timer_task_lighton);
			strOff = getString(R.string.timer_task_lightoff);
			break;	
		case PubDefine.TIMER_TYPE_USB:
			imgTypeStart.setImageResource(R.drawable.smp_usb_on_small);
			imgTypeStop.setImageResource(R.drawable.smp_usb_off_small);			
			strOn = getString(R.string.timer_task_usbon);
			strOff = getString(R.string.timer_task_usboff);
			break;			
		case PubDefine.TIMER_TYPE_BELL:
			imgTypeStart.setImageResource(R.drawable.smp_parentctrl_active_small);
			imgTypeStop.setImageResource(R.drawable.smp_parentctrl_deactive_small);			
			strOn = getString(R.string.timer_task_bell);
			strOff = getString(R.string.timer_task_bell);
			layTitleOff.setVisibility(View.GONE);
			layContentOff.setVisibility(View.GONE);			
			break;		
		case PubDefine.TIMER_TYPE_OPENPC:
			imgTypeStart.setImageResource(R.drawable.smp_parentctrl_active_small);
			imgTypeStop.setImageResource(R.drawable.smp_parentctrl_deactive_small);
			strOn = getString(R.string.timer_task_openpc_time);
			strOff = getString(R.string.timer_task_openpc_time);
			layTitleOff.setVisibility(View.GONE);
			layContentOff.setVisibility(View.GONE);
			
			layout_poweron_header.setVisibility(View.VISIBLE);
			layout_poweroff_header.setVisibility(View.GONE);
			layout_mac.setVisibility(View.VISIBLE);
			layout_period.setVisibility(View.VISIBLE);
			layout_period_minute.setVisibility(View.GONE);
			ll_mac_low.setVisibility(View.VISIBLE);
			
			break;
		case PubDefine.TIMER_TYPE_CLOSEPC:
			imgTypeStart.setImageResource(R.drawable.smp_power_on_small);
			imgTypeStop.setImageResource(R.drawable.smp_power_off_small);
			strOn = getString(R.string.timer_task_poweron);
			strOff = getString(R.string.timer_task_closepc);

			layout_poweron_header.setVisibility(View.GONE);
			layout_poweroff_header.setVisibility(View.VISIBLE);
			layout_mac.setVisibility(View.VISIBLE);
			layout_period.setVisibility(View.VISIBLE);
			layout_period_minute.setVisibility(View.GONE);
			ll_mac_low.setVisibility(View.VISIBLE);			
			et_mac.setEnabled(false);
			break;
		default:
			imgTypeStart.setImageResource(R.drawable.smp_power_on_small);
			imgTypeStop.setImageResource(R.drawable.smp_power_off_small);			
			strOn = getString(R.string.timer_task_poweron);
			strOff = getString(R.string.timer_task_poweroff);
			break;			
		}
		mOnText.setText(strOn);
		mOffText.setText(strOff);
		
		mPwrOnTime = (TextView)findViewById(R.id.txtPoweronTime);
		mPwrOffTime = (TextView)findViewById(R.id.txtPoweroffTime);
		
		mRlPwron = (RelativeLayout)findViewById(R.id.lay_poweron_time);
		mRlPwron.setOnClickListener(this);
		mRlPwroff = (RelativeLayout)findViewById(R.id.lay_poweroff_time);
		mRlPwroff.setOnClickListener(this);
		
		//mCkPwron = (ThingzdoCheckBox)findViewById(R.id.imgCheckPoweron);
		//mCkPwron.setOnClickListener(this);
		//mCkPwroff = (ThingzdoCheckBox)findViewById(R.id.imgCheckPoweroff);
		//mCkPwroff.setOnClickListener(this);
	}
	
	public void initPeriod(String[] selDays){
		mDays = getResources().getStringArray(R.array.current_week);
		mTxtPeriod.setTag(getPeriodValue());
		mTxtPeriod.setText(getPeriodText());
		mTxtPeriod_minute.setText(mSelMinute);
	}
	
	private String getPeriodValue() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mSelDays.length; i++){ 
			sb.append(mSelDays[i]);
		}
		return sb.toString();
	}
	
	private String getPeriodText() {
		StringBuffer sbDay = new StringBuffer();
		for (int i = 0; i < mSelDays.length; i++) {
			if (mSelDays[i].equals("1")) {
				if (i < mSelDays.length - 1) {
					sbDay.append(mDays[i]).append(" ");
				} else {
					sbDay.append(mDays[i]);
				}
			}
		}
		
		return sbDay.toString();
	}	
	
	private String getDelayTime(int iMin) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, iMin);
		
    	SimpleDateFormat formatter = new  SimpleDateFormat("HH:mm:ss");
		Date curDate = new Date(cal.getTimeInMillis());
		String str   = formatter.format(curDate);
		return str;
    }
	
	private void initNewTimer() {

		// 方式一： 00:00
//		mPwrOnTime.setText(mContext.getString(R.string.timer_task_inittime));
//		if (PubDefine.TIMER_TYPE_BELL == mTimerType) {
//			mPwrOffTime.setText(mContext.getString(R.string.timer_task_inittime_addone));
//		} else {
//			mPwrOffTime.setText(mContext.getString(R.string.timer_task_inittime));
//		}
		
		// 方式二： 当前操作时间的下一分钟；
//		String strCurTime = PubFunc.getTimeString();
//		int strHour = Integer.valueOf(strCurTime.substring(8, 10));
//		int strMin = Integer.valueOf(strCurTime.substring(10, 12));
//		int strSec = Integer.valueOf(strCurTime.substring(12, 14));
		mPwrOnTime.setText(getDelayTime(1));
		mPwrOffTime.setText(getDelayTime(2));
		
		//mPwrOnTime.setTextColor(Color.GRAY);
		//mPwrOffTime.setTextColor(Color.GRAY);	
		for (int i = 0; i < 7; i++) {
			mSelDays[i] = "1";			
		}
		initPeriod(mSelDays);
	}
	
	@SuppressLint("DefaultLocale") 
	private void initCurTimer() {
		TimerDefine timer = mTimerHelper.getTimer(mPlugId, mTimerId);
		if (TextUtils.isEmpty(timer.mPowerOnTime)) {
			mPwrOnTime.setText(mContext.getString(R.string.timer_task_inittime));
			//mCkPwron.setChecked(false);
		} else {
			mPwrOnTime.setText(timer.mPowerOnTime);
			//mCkPwron.setChecked(true);
		}
		
		if (TextUtils.isEmpty(timer.mPowerOffTime)) {
			mPwrOffTime.setText(mContext.getString(R.string.timer_task_inittime));
			//mCkPwroff.setChecked(false);
		} else {
			mPwrOffTime.setText(timer.mPowerOffTime);
			et_mac.setText(timer.mPowerOffTime.toUpperCase());
			//mCkPwroff.setChecked(true);
		}		
		
		for (int i = 0; i < 7; i++) {
			mSelDays[i] = timer.mPeriod.substring(i, i + 1);			
		}
		
		// 新增 重复周期功能：例如，1111111_30, 其中 30代表每隔30分钟就循环一次；当天内有效；
		String[] str_Period = timer.mPeriod.split("_");
		if (str_Period.length >= 2) {
			mSelMinute = str_Period[1];
		}
		
		initPeriod(mSelDays);
	}	
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:
			//PubFunc.thinzdoToast(mContext, getPeriod());
			finish();
			break;
		case R.id.titlebar_rightbutton:
			if (false == checkTimeValid()) {
				PubFunc.thinzdoToast(mContext, mContext.getString(R.string.timer_task_timeset_err));
				return;
			}
			
			if (PubDefine.TIMER_TYPE_CLOSEPC == mTimerType) {
				mPwrOnTime.setText(mPwrOffTime.getText().toString());
			}
			// BELL和关机棒不进行检查
			if (PubDefine.TIMER_TYPE_BELL != mTimerType && PubDefine.TIMER_TYPE_CLOSEPC != mTimerType) {
				if (mPwrOnTime.getText().toString().equals(mPwrOffTime.getText().toString())) {
					PubFunc.thinzdoToast(mContext, mContext.getString(R.string.timer_task_sametime));
					return;
				}
			}
			
			et_mac.setText(et_mac.getText().toString().toUpperCase());
			
			mPeriod = (String) mTxtPeriod.getTag();
			String str_temp = mTxtPeriod_minute.getText().toString().trim();
			if (!str_temp.equals("0") && !str_temp.equals("")) {
				mPeriod = mPeriod + "_" + str_temp;
			}
			mPoweronTime = mPwrOnTime.getText().toString();
			if (PubDefine.TIMER_TYPE_BELL != mTimerType) {
				mPoweroffTime = mPwrOffTime.getText().toString();
			} else {
				mPoweroffTime = mPoweronTime;
			}
			
			if (mTimerType == PubDefine.TIMER_TYPE_OPENPC && isMACValid(et_mac.getText().toString()) == false) {
				PubFunc.thinzdoToast(mContext, mContext.getString(R.string.timer_task_mac_fail));
				return;
			}
			
			if (true == mIsCreateTimer) {
				addTimerTask();
			} else {
				modifyTimerTask();
			}
			break;
		case R.id.lay_poweron_time:
			showDialog(mPwrOnTime, mPwrOnTime.getText().toString());
			break;			
		case R.id.lay_poweroff_time:
			showDialog(mPwrOffTime, mPwrOffTime.getText().toString());
			break;
		/*case R.id.imgCheckPoweron:
			mPwrOnTime.setTextColor(mCkPwron.isChecked() ? Color.BLACK : Color.GRAY);
			break;
		case R.id.imgCheckPoweroff:
			mPwrOffTime.setTextColor(mCkPwroff.isChecked() ? Color.BLACK : Color.GRAY);
			break;*/
		case R.id.layout_period_header:
			Intent intent = new Intent();
			intent.setClass(this, SetPeriodActivity.class);
			intent.putExtra("selected_days", mSelDays); 
			startActivityForResult(intent, 0);
			break;
		case R.id.image_mac:
			//showAllMacs();
			break;
		case R.id.layout_mac:
			//showAllMacs();
			break;
		}			
	}
	
	/*
	 * 显示所有温度
	 */
	private void showAllMacs() {
		macs = mPlugHelper.getAllSmartPlugMac(mPlugId);
		macs_show = mPlugHelper.getAllSmartPlugMacShow(mPlugId);
		
		ActionSheetDialog dlg = new ActionSheetDialog(PlugTimerActivity.this)
		.builder()
		.setTitle(PlugTimerActivity.this.getString(R.string.smartplug_oper_selectmac_title))
		.setCancelable(true)
		.setCanceledOnTouchOutside(true);
		
		for (int i = 0; i < macs_show.size(); i++) {
			
			dlg.addSheetItem(macs_show.get(i), SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
			            	Message msg = new Message();
			            	msg.what = 2;
			            	msg.obj = macs.get(which - 1);
			            	updateTableHandler.sendMessage(msg);							
						}
			});
		}
		dlg.show();
	}
	
	private boolean isMACValid(String str_mac) {
		if (str_mac.length() != 17) {
			return false;
		}
		for (int i = 0; i < 17; i++) {
			if (i % 3 == 2) {
				if (str_mac.charAt(i) != ':') {
					return false;
				}
			} else {
				if (isMacCharValid(str_mac.charAt(i)) == false) {
					return false;
				}
			}
		}
				
		return true;
	}
	
	private boolean isMacCharValid(char temp) {
		String str_range = "0123456789ABCDEF";
		String str_temp = String.valueOf(temp);
		if (str_range.indexOf(str_temp) != -1) {
			return true;
		}
		
		return false;
	}
	
	private void showDialog(TextView view, String timer) {
		String[] time_val_t = timer.split(":");
		String[] time_val = {"00", "00", "00"};
		 
		if (time_val_t.length > 0) {
			time_val[0] = time_val_t[0];
		} 
		if (time_val_t.length > 1) {
			time_val[1] = time_val_t[1];
		} 
		if (time_val_t.length > 2) {
			time_val[2] = time_val_t[2];
		}
		final TextView mView = view;
		
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View timepickerview = inflater.inflate(R.layout.timepicker,
				null);
		ScreenInfo screenInfo = new ScreenInfo(PlugTimerActivity.this);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		wheelMain.initDateTimePicker(Integer.parseInt(time_val[0]), Integer.parseInt(time_val[1]), Integer.parseInt(time_val[2]));
		final MyAlertDialog dialog = new MyAlertDialog(mContext)
				.builder()
				.setTitle(mContext.getString(R.string.timer_task_selecttime))
				.setView(timepickerview)
				.setNegativeButton(mContext.getString(R.string.smartplug_cancel) , new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});
		dialog.setPositiveButton(mContext.getString(R.string.smartplug_ok) , new OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] time = wheelMain.getTime().split(":");
				StringBuffer sb = new StringBuffer();
				if (1 == time[0].length()) {
					sb.append("0").append(time[0]).append(":");	
				} else {
					sb.append(time[0]).append(":");
				}
				if (1 == time[1].length()) {
					sb.append("0").append(time[1]).append(":");	
				} else {
					sb.append(time[1]).append(":");
				}	
				if (1 == time[2].length()) {
					sb.append("0").append(time[2]);	
				} else {
					sb.append(time[2]);
				}				
				
				mView.setText(sb);
			}
		});
		dialog.show();		
	}
	
	private void addTimerTask() {
    	mProgress = PubFunc.createProgressDialog(PlugTimerActivity.this, 
    			getString(R.string.smartplug_ctrl_adding), false);
    	mProgress.show();
    	
    	String str_mac = et_mac.getText().toString();
    	String str_temp = (mTimerType == PubDefine.TIMER_TYPE_OPENPC) ? str_mac : mPoweroffTime; 

    	mMaxTimerId = mTimerHelper.getMaxTimerId(mPlugId) + 1;
    	for (int i = 0; i < pcIDs.size(); i++) {
    		int maxid = mTimerHelper.getMaxTimerId(pcIDs.get(i)) + 1;
    		if (mMaxTimerId < maxid) {
    			mMaxTimerId = maxid;
    		}
    	}
    	
    	String tempModuleID = mPlugId;
    	if (mTimerType == PubDefine.TIMER_TYPE_CLOSEPC && mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG) {			// 1_2
    		tempModuleID = mPlugHelper.getPlugIDFromMac(str_mac);
    	}

//    	if (tempModuleID != null) {
	    	StringBuffer sb = new StringBuffer();
	    	sb.append(SmartPlugMessage.CMD_SP_ADDTIMER)
	    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
	    	         .append(PubStatus.getUserName())
	    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
	    	         .append(tempModuleID)
	    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
	    	         .append(mMaxTimerId)
	    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
	    	         .append(mTimerType)
	    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
	    	         .append(mPeriod)
	    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
	    	         .append(mPoweronTime)
	    	         .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
	    	         .append(str_temp);
	    	
	    	/*if (SmartPlug_Connect_Mode.Shake == PubDefine.g_Connect_Mode) {
	    		ShakeSocketMgr.sendCommandRequestWithUDPSocket(sb.toString());
	    		return;
	    	}*/ 
	    	
	    	//RevCmdFromSocketThread.getInstance().setRunning(true);
	    	sendMsg(true, sb.toString(), true);
//    	} else {
//    		PubFunc.thinzdoToast(mContext, "未找到Mac地址对应的电脑模块!");
//    	}
	}
	
	private void modifyTimerTask() {
    	mProgress = PubFunc.createProgressDialog(PlugTimerActivity.this, 
    			getString(R.string.smartplug_ctrl_modifying), false);
    	mProgress.show();		

    	String str_mac = et_mac.getText().toString();
    	String str_temp = (mTimerType == PubDefine.TIMER_TYPE_OPENPC) ? str_mac : mPoweroffTime; 
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append(SmartPlugMessage.CMD_SP_MODTIMER)
    	    .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
    	    .append(PubStatus.getUserName())
    	    .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
        	.append(mPlugId)
        	.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
        	.append(mTimerId)
        	.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
        	.append(mTimerType)
        	.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
        	.append(mPeriod)
        	.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
        	.append(mPoweronTime)
        	.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
        	.append(str_temp);  
    	
    	/*if (SmartPlug_Connect_Mode.Shake == PubDefine.g_Connect_Mode) {
    		ShakeSocketMgr.sendCommandRequestWithUDPSocket(sb.toString());
    		return;
    	} */    	
    	
    	//RevCmdFromSocketThread.getInstance().setRunning(true);
    	sendMsg(true, sb.toString(), true);
	}	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mTimerTaskReceiver);
	}
	
	private boolean checkTimeValid() {
		if (null == mTxtPeriod.getText() || 
				mTxtPeriod.getText().toString().isEmpty()) {
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (null == data) {
			return;
		}
		if (0 == resultCode) {
			mSelDays = data.getStringArrayExtra("selected_days");
		}
		mTxtPeriod.setTag(getPeriodValue());
		mTxtPeriod.setText(getPeriodText());
	}
	
	public class Question {
		private String tv_pc_mac;
	    
		public String getTv_pc_mac() {
			return tv_pc_mac;
		}
		public void setTv_pc_mac(String tv_pc_mac) {
			this.tv_pc_mac = tv_pc_mac;
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
			public RelativeLayout rl_all;
			
			public void ViewData(Question news, int position){
				tv_pc_mac.setText(String.valueOf(news.getTv_pc_mac()));
				rl_all.setContentDescription(String.valueOf(position));
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
		    	convertView        	= mInflater.inflate(R.layout.item_pcctrl_mac_simple_list, null);
	            holder.tv_pc_mac 	= (TextView)convertView.findViewById(R.id.tv_pc_mac);
	            holder.rl_all 	= (RelativeLayout)convertView.findViewById(R.id.rl_all);
	            
	            //给Button添加单击事件 添加Button之后ListView将失去焦点 需要的直接把Button的焦点去掉
	            holder.rl_all.setOnClickListener(new View.OnClickListener() {
	              @Override
	              public void onClick(View v) {
	            	  String temp = v.getContentDescription().toString();
	            	  int t_position = Integer.parseInt(v.getContentDescription().toString());
	            	  Message msg = new Message();
	            	  msg.what = 4;
	            	  msg.arg1 = t_position;
	            	  mHandler.sendMessage(msg);
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
