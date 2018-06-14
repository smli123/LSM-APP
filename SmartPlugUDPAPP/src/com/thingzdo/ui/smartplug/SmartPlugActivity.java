package com.thingzdo.ui.smartplug;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.SmartPlugFragmentPagerAdapter;
import com.thingzdo.ui.control.ControlFragment;
import com.thingzdo.ui.login.LoginActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.manage.ManageFragment;
import com.thingzdo.ui.wheelutils.MyAlertDialog;

public class SmartPlugActivity extends FragmentActivity
		implements
			View.OnClickListener,
			TextToSpeech.OnInitListener {
	private static final String TAG = SmartPlugActivity.class.getName();
	private final int MSG_READ_CITY_WEATHER = 1;

	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private TextView txtTabControl, txtTabManage;
	private ImageView imgTabControl, imgTabManage;
	private LinearLayout layTabControl, layTabManage;
	protected boolean mBack2Exit = false;
	private TextView tvTitle;
	// private ImageView mImgAddPlug = null;
	private Button mBtnAddPlug = null;
	private ImageView mImgFreshPlug = null;

	private ImageView imgControlIcon;
	private Button btnLoginout;

	private int currIndex = 0;
	private int bottomLineWidth;
	private int offset = 0;
	private int position_one;
	private int position_two;
	private Resources resources;
	private Context mContext;
	private SmartPlugHelper mPlugProvider = null;

	private OnlineUpdateReceiver mReceiver = null;

	private TextToSpeech tSpeech;

	private SharedPreferences mSharedPreferences;
	private String cur_city = "";
	private String speek_time = "";

	Timer mTimer = null;
	TimerTask mTimerTask = null;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SmartPlugApplication.getInstance().addActivity(this);
		SmartPlugApplication.resetTask();

		/*
		 * if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
		 * getWindow().addFlags(WindowManager
		 * .LayoutParams.FLAG_TRANSLUCENT_STATUS);
		 * getWindow().addFlags(WindowManager
		 * .LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); }
		 */

		mSharedPreferences = getSharedPreferences("PARAMETERCONFIG"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);

		loadData();

		mContext = this;
		mPlugProvider = new SmartPlugHelper(mContext);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_viewpager);
		resources = getResources();
		InitWidth();
		InitTextView();
		InitViewPager();

		// IntentFilter filter = new IntentFilter();
		// filter.addAction("");
		// mContext.registerReceiver(mReceiver, filter);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.LOGOUT_BROADCAST);
		filter.addAction(PubDefine.CONFIG_MODIFY_SPEEK_TIMER);
		mContext.registerReceiver(mLogoutReveiver, filter);

		// reset_speek_timer ();

		// new Handler().postDelayed(new Runnable(){
		// public void run() {
		// new SendCommand_Async().execute();
		// }
		// }, 3000);
	}

	private void reset_speek_timer() {
		if (mTimerTask != null) {
			mTimerTask.cancel();
		}
		if (mTimer != null) {
			mTimer.cancel();
		}

		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				new SendCommand_Async().execute();
				// 每24小时执行一次
				// mTimer.schedule(mTimerTask, 24*60*60*1000);
			}
		};

		Date cur_date = new Date();
		Date d_Speek_Timer = PubFunc.stringToDate(speek_time);
		if (d_Speek_Timer != null) {
			if (cur_date.getTime() > d_Speek_Timer.getTime()) {
				d_Speek_Timer = PubFunc.stringToDate(speek_time, 1);
			}
			mTimer.schedule(mTimerTask, d_Speek_Timer);
		}
	}

	private void loadData() {
		cur_city = mSharedPreferences.getString("CITY", "");
		speek_time = mSharedPreferences.getString("SPEEK_TIME", "");
	}

	private class OnlineUpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean isOnline = intent.getBooleanExtra(
					"com.thingzdo.carguard.caronline", false);
			PubFunc.thinzdoToast(
					mContext,
					isOnline
							? context
									.getString(R.string.app_response_device_online)
							: context
									.getString(R.string.app_response_device_offline));
		}

	}

	private BroadcastReceiver mLogoutReveiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(PubDefine.LOGOUT_BROADCAST)) {
				int ret = intent.getIntExtra("LOGOUT", 0);
				if ((1 == ret) || (2 == ret)) { // ret 为 1： 表示被其他用户登陆而退出； 2：表示
												// APP用户与服务器断开；

					ControlFragment.delete();
					ManageFragment.delete();
					fragmentsList.clear();

					String tmp_str = "";
					if (ret == 1) {
						tmp_str = SmartPlugApplication.getContext().getString(
								R.string.user_force_logout);
					} else if (ret == 2) {
						tmp_str = SmartPlugApplication.getContext().getString(
								R.string.app_disconnect_with_server);
					}

					PubFunc.thinzdoToast(SmartPlugApplication.getContext(),
							tmp_str);

					Intent mIntent = new Intent();
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mIntent.setClass(SmartPlugApplication.getContext(),
							LoginActivity.class);
					mIntent.putExtra("FORCE_LOGOUT", ret);
					SmartPlugApplication.getContext().startActivity(mIntent);
				}
			} else if (intent.getAction().equals(
					PubDefine.CONFIG_MODIFY_SPEEK_TIMER)) {
				speek_time = intent.getStringExtra("SPEEK_TIME");
				cur_city = intent.getStringExtra("CITY");
				reset_speek_timer();
			}
		}

	};

	@Override
	public void onInit(int status) {
		// if (status == TextToSpeech.SUCCESS) {
		// int result = tSpeech.setLanguage(Locale.CHINA);
		// if (result == TextToSpeech.LANG_MISSING_DATA
		// || result == TextToSpeech.LANG_NOT_SUPPORTED) {
		// Toast.makeText(getApplicationContext(),
		// "Language is not available.", Toast.LENGTH_LONG).show();
		// }
		// } else {
		// Toast.makeText(getApplicationContext(), "init failed",
		// Toast.LENGTH_LONG).show();
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
		// tSpeech = new TextToSpeech(getApplicationContext(), this);
	}

	private void StopSpeech() {
		// if (tSpeech != null) {
		// tSpeech.stop();
		// }
	}

	private void InitTextView() {
		imgTabControl = (ImageView) findViewById(R.id.img_tab_plug);
		imgTabManage = (ImageView) findViewById(R.id.img_tab_manage);
		txtTabControl = (TextView) findViewById(R.id.tv_tab_control);
		txtTabManage = (TextView) findViewById(R.id.tv_tab_manage);

		layTabControl = (LinearLayout) findViewById(R.id.layout_tab_plugs);
		layTabManage = (LinearLayout) findViewById(R.id.layout_tab_settings);

		tvTitle = (TextView) findViewById(R.id.titlebar_caption);
		btnLoginout = (Button) findViewById(R.id.titlebar_leftbutton);

		/*
		 * mImgAddPlug = (ImageView) findViewById(R.id.titlebar_rightimage);
		 * mImgAddPlug.setImageResource(R.drawable.smp_scan_plug);
		 * mImgAddPlug.setVisibility(View.VISIBLE);
		 * mImgAddPlug.setOnClickListener(this);
		 */

		mBtnAddPlug = (Button) findViewById(R.id.titlebar_rightbutton);
		mBtnAddPlug.setText(R.string.add);
		mBtnAddPlug.setBackgroundResource(R.drawable.title_btn_selector);
		mBtnAddPlug.setVisibility(View.VISIBLE);
		mBtnAddPlug.setOnClickListener(this);

		// mImgFreshPlug = (ImageView) findViewById(R.id.titlebar_leftimage);
		// mImgFreshPlug.setImageResource(R.drawable.plug_fresh_pressed);
		// mImgFreshPlug.setVisibility(View.GONE);

		imgControlIcon = (ImageView) findViewById(R.id.img_ctrl_righticon);
		// imgControlIcon.setImageResource(R.drawable.remote_car_control);

		tvTitle.setVisibility(View.VISIBLE);
		btnLoginout.setVisibility(View.INVISIBLE);
		btnLoginout.setText(getString(R.string.smartplug_goback));

		imgTabControl.setOnClickListener(new TabOnClickListener(0));
		imgTabManage.setOnClickListener(new TabOnClickListener(1));
		layTabControl.setOnClickListener(new TabOnClickListener(0));
		layTabManage.setOnClickListener(new TabOnClickListener(1));

		btnLoginout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SmartPlugApplication.setLogined(false);

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClass(SmartPlugApplication.getInstance(),
						LoginActivity.class);
				SmartPlugApplication.getInstance().startActivity(intent);
			}
		});

		tvTitle.setText(getString(R.string.smartplug_title_control));

	}

	private Handler mLogouthandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0 :
					Intent mIntent = new Intent();
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mIntent.setClass(SmartPlugApplication.getContext(),
							LoginActivity.class);
					mIntent.putExtra("FORCE_LOGOUT", 1);
					SmartPlugApplication.getContext().startActivity(mIntent);
					finish();
					break;
				case MSG_READ_CITY_WEATHER :
					String output = (String) msg.obj;
					tSpeech.speak(output, TextToSpeech.QUEUE_FLUSH, null);
					break;
				default :
					break;
			}
		};
	};

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.carguard_tab_pager);
		if (null != fragmentsList) {
			fragmentsList.clear();
			fragmentsList = null;
		}
		fragmentsList = new ArrayList<Fragment>();

		Fragment controlFragment = ControlFragment.newInstance(); // new
																	// ControlFragment();
		Fragment manageFragment = ManageFragment.newInstance();
		((ManageFragment) manageFragment).setHandler(mLogouthandler); // new
																		// ManageFragment(mLogouthandler);

		fragmentsList.add(controlFragment);
		fragmentsList.add(manageFragment);

		mPager.setAdapter(new SmartPlugFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentsList));

		// 是否自动启动语音控制
		if (PubDefine.AUTO_RUN_SMARTPLUG_IATDEMO == false) {
			mPager.setCurrentItem(0);
		} else {
			mPager.setCurrentItem(1);
		}
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		txtTabControl.setTextColor(resources.getColor(R.color.blue));
		imgTabControl.setImageResource(R.drawable.smp_tab_pluglist_pressed);
	}

	private void InitWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (int) ((screenW / 2.0 - bottomLineWidth) / 2);
		// PubFunc.log("MainActivity offset=" + offset);

		position_one = (int) (screenW / 2.0);
		position_two = position_one * 2;
	}

	public class TabOnClickListener implements View.OnClickListener {
		private int index = 0;

		public TabOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int pos) {
			Animation animation = null;
			switch (pos) {
				case 0 :
					if (currIndex == 1) {
						animation = new TranslateAnimation(position_one, 0, 0,
								0);
						txtTabManage.setTextColor(resources
								.getColor(R.color.lightwhite));
						imgTabManage
								.setImageResource(R.drawable.smp_tab_settings_normal);
					}
					txtTabControl
							.setTextColor(resources.getColor(R.color.blue));
					imgTabControl
							.setImageResource(R.drawable.smp_tab_pluglist_pressed);
					tvTitle.setText(getString(R.string.smartplug_title_control));
					// mImgAddPlug.setVisibility(View.VISIBLE);
					mBtnAddPlug.setVisibility(View.VISIBLE);
					break;
				case 1 :
					if (currIndex == 0) {
						animation = new TranslateAnimation(0, position_two, 0,
								0);
						txtTabControl.setTextColor(resources
								.getColor(R.color.lightwhite));
						imgTabControl
								.setImageResource(R.drawable.smp_tab_pluglist_normal);
					}
					txtTabManage.setTextColor(resources.getColor(R.color.blue));
					imgTabManage
							.setImageResource(R.drawable.smp_tab_settings_pressed);
					tvTitle.setText(getString(R.string.smartplug_title_manage));
					// mImgAddPlug.setVisibility(View.INVISIBLE);
					mBtnAddPlug.setVisibility(View.INVISIBLE);
					break;
				default :
					break;
			}

			currIndex = pos;
			animation.setFillAfter(true);
			animation.setDuration(200);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// ���µ������BACK��ͬʱû���ظ�
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			// ����ȷ�Ϸ��͵ĶԻ���
			new MyAlertDialog(mContext)
					.builder()
					.setMsg(this.getString(R.string.smartplug_exit))
					.setPositiveButton(this.getString(R.string.smartplug_ok),
							okListener)
					.setNegativeButton(
							this.getString(R.string.smartplug_cancel),
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {

								}
							}).setCancelable(true).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	final OnClickListener okListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			finish();
			// PubDefine.disconnect();
			SmartPlugApplication.getInstance().exit();
		}
	};

	protected void onDestroy() {
		fragmentsList.clear();
		// mContext.unregisterReceiver(mReceiver);
		mContext.unregisterReceiver(mLogoutReveiver);

		if (tSpeech != null) {
			tSpeech.stop();
			tSpeech.shutdown();
		}

		super.onDestroy();
	};

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		/*
		 * case R.id.titlebar_rightimage: //Intent intent = new
		 * Intent(this,AddSocketActivity2.class); //startActivity(intent);
		 * break;
		 */
			case R.id.titlebar_rightbutton :
				Intent intent = new Intent(this, AddSocketActivity2.class);
				startActivity(intent);
				break;
		}

	}

	private int count = 0;

	void testAddPlug() {
		SmartPlugDefine plug = new SmartPlugDefine();
		plug.mUserName = PubStatus.g_CurUserName;
		plug.mPlugName = "test" + count;
		plug.mPlugId = "PLUG_" + count;
		plug.mIsOnline = true;
		// plug.mIsPoweron = true;
		count += 1;

		mPlugProvider.addSmartPlug(plug);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		String plugId = intent.getStringExtra("new_plug");
		Log.i(TAG, "new plug id is: " + plugId);

		super.onNewIntent(intent);

	}

	// ----------------------------------------------------------
	class SendCommand_Async extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			sendRequestWithHttpClient(cur_city);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

		}
	}

	private void sendRequestWithHttpClient(String city) {
		String str_output = "";
		try {
			if (city.isEmpty()) {
				str_output = "未设置所属的城市，请设置。";
			} else {
				String t_city = URLEncoder.encode(city, "utf-8");
				String url = "http://www.sojson.com/open/api/weather/json.shtml?city="
						+ t_city;

				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				if (httpResponse.getStatusLine().getStatusCode() == 200) { // 访问服务器成功
					HttpEntity entity = httpResponse.getEntity();
					String response = EntityUtils.toString(entity, "utf-8");
					str_output = parseJSONWithGSON(response);
				} else {
					str_output = "获取的网络数据错误";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			str_output = "网络访问失败";
		}

		Message msg = new Message();
		msg.what = MSG_READ_CITY_WEATHER;
		msg.obj = str_output;

		mLogouthandler.sendMessage(msg);
	}

	private String parseJSONWithGSON(String jsonData) {
		JSONObject jsonObject = JSONObject.parseObject(jsonData);

		int status = jsonObject.getInteger("status");
		String date = jsonObject.getString("date");
		int count = jsonObject.getInteger("count");
		String message = jsonObject.getString("message");
		String city = jsonObject.getString("city");
		JSONObject data = jsonObject.getJSONObject("data");

		if (status != 200)
			return "获取天气数据失败";

		String wendu = data.getString("wendu");
		String ganmao = data.getString("ganmao");
		String quality = data.getString("quality");
		String pm25 = data.getString("pm25");
		String pm10 = data.getString("pm10");
		String shidu = data.getString("shidu");

		JSONArray forecast = data.getJSONArray("forecast");
		for (int i = 0; i < forecast.size(); i++) {
			// String o_item = String.valueOf(data.get(i));
			// JSONObject item = JSONObject.parseObject(o_item);
			// String fengxiang = item.getString("fengxiang");
			// String fengli = item.getString("fengli");
			// String high = item.getString("high");
			// String type = item.getString("type");
			// String low = item.getString("low");
			// String date = item.getString("date");
		}

		return String.valueOf(city + "温度" + wendu + ",湿度" + shidu + ",空气质量"
				+ quality + ",PM二点五为" + pm25 + ",总结" + ganmao);
	}

}
