package com.thingzdo.ui.common;

import java.io.File;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.dataprovider.ActionHelper;
import com.thingzdo.ui.ActionDefine;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PubFunc {
	
	public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
        mContext.getSystemService(Context.ACTIVITY_SERVICE); 
        List<ActivityManager.RunningServiceInfo> serviceList 
            = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
	
	public static void addToTable(Context context, ActionDefine action) {
        ActionHelper helper  = new ActionHelper(context);
		helper.addAction(action);		  
    }
	
    public static void setDialogCanClose(DialogInterface dialog, boolean canClose) {
    	try {  
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");   
            field.setAccessible(true);   
            field.set(dialog, canClose); 
              
        } catch (Exception e) {  
            e.printStackTrace();  
        }   	
    }
    
    public static boolean isPhoneNumberValid(String phoneNumber) {
		
    	//return PhoneNumberUtils.isGlobalPhoneNumber(phone);
    	
    	/*Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
                          
        Matcher m = p.matcher(phone);
        return m.matches(); */  
    	
    	boolean isValid = false;
    	  /*
    	   * �ɽ��ܵĵ绰��ʽ�У�
    	   */
    	 String expression1 = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
    	 String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";

    	 CharSequence inputStr = phoneNumber;
    	 Pattern pattern1 = Pattern.compile(expression1);
    	 Matcher matcher = pattern1.matcher(inputStr);
    	  
    	 Pattern pattern2 = Pattern.compile(expression2);
    	 Matcher matcher2 = pattern2.matcher(inputStr);
    	 if (matcher.matches() || matcher2.matches()) {
    		 isValid = true;
    	 }
    	 return isValid;    	
    }
    
    public static String isPasswordValid(Context context, String pwd) {
    	String reason = null;
    	if (pwd.length() < 6) {
    		reason = context.getString(R.string.register_info_lessthansixchar);
    		return reason;
    	}
    	if (pwd.length() > 20) {
    		reason = context.getString(R.string.register_info_morethantwentychar);
    		return reason;
    	}
    	
    	Pattern pattern = Pattern.compile("[0-9a-zA-Z]+",Pattern.CASE_INSENSITIVE);    
    	Matcher matcher = pattern.matcher(pwd);
    	if (!matcher.matches()) {
    		reason = context.getString(R.string.register_info_pwdinvalid);
    		return reason;    		
    	}
    	
    	return reason;
    }
    
    public static boolean isEmailValid(String email) {
    	boolean isValid = false;
    	Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$", Pattern.CASE_INSENSITIVE);    
    	Matcher matcher = pattern.matcher(email); 
    	if (matcher.matches()) {
    		isValid = true;	
    	}
    	return isValid;
    }
    
    public static boolean isRouterNameValid(String routerName) {
		for (int i = 0; i < routerName.length(); i++) {
            String bb = routerName.substring(i, i + 1);
            boolean cc = java.util.regex.Pattern.matches("[,\u4E00-\u9FA5]", bb);
            if (cc == true) {
                return false;
            }
        }
		return true;
    }
    
    public static void thinzdoToast(Context context, String msg) {
    	if (null == msg || msg.isEmpty()) {
    		return;
    	}
        Toast toast =  Toast.makeText(context,  msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }   
    
    public static void thinzdoToastCurTime(Context context, String msgTitle) {
    	if (PubDefine.RELEASE_VERSION == false && false) {
    		String msg = msgTitle + ":" + getTimeHMSString();
            Toast toast =  Toast.makeText(context,  msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
    	}
    }
    
    public static SmartPlugProgressDlg createProgressDialog(Context context, String message, boolean closeVisible) {
    	SmartPlugProgressDlg progressDialog = SmartPlugProgressDlg.createDialog(context, closeVisible);
    	progressDialog.setCancelable(false);
    	progressDialog.setCanceledOnTouchOutside(false);
    	progressDialog.setMessage(message);
    	progressDialog.setClose();
    	return progressDialog;
    }
    
    
	public static AlertDialog showProgress(Context context, String msg) {
		LinearLayout layout = (LinearLayout)LayoutInflater.from(context)
	            .inflate(R.layout.activity_sendmsg, null);	
		TextView text = (TextView)layout.findViewById(R.id.sendmsg_info);
		if (null != text) {
			text.setText(msg);
		}
		
		return new AlertDialog.Builder(context)
        					  .setView(layout)
        					  .setCancelable(false).show();		
	}  
	
	public static String unicodeToString(String str) {
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");   
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
		    ch = (char) Integer.parseInt(matcher.group(2), 16);
		    str = str.replace(matcher.group(1), ch + "");   
		}
		return str;
	}
	
	public static String Convert2Unicode(final String sms) {
		if (sms.length() < 4) {
			return "";
		}
		
		String sOldText = sms;
		int length = 4;
		StringBuilder str = new StringBuilder();
		while (sOldText.length() >= length) {
	        str.append("\\u" + sOldText.substring(0, length));
	        sOldText = sOldText.substring(length);
		}
		//str.append("\\u" + sOldText);
		
		return str.toString();
		
	}
	
	/**  
	 * String���ַ�ת����unicode��String  
	 * @param String strText ȫ���ַ�  
	 * @return String ÿ��unicode֮���޷ָ���  
	 * @throws Exception  
	 */    
	public static String strToUnicode(String strText)    
	    throws Exception    
	{    
	    char c;    
	    StringBuilder str = new StringBuilder();    
	    int intAsc;    
	    String strHex;    
	    for (int i = 0; i < strText.length(); i++)    
	    {    
	        c = strText.charAt(i);    
	        intAsc = (int) c;    
	        strHex = Integer.toHexString(intAsc);    
	        if (intAsc > 128)    
	            str.append("\\u" + strHex);    
	        else // ��λ��ǰ�油00    
	            str.append("\\u00" + strHex);    
	    }    
	    return str.toString();    
	}  
	
	public static boolean isAppInstalled(Context context, String packageName) {
		PackageInfo packageInfo;

        try {
            packageInfo = context.getPackageManager().getPackageInfo(
            		packageName, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
	}
	
	public static boolean isAppRunning(Context context, String packageName) {
		boolean isStarted = false;
		try {
		    ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		    List<RunningAppProcessInfo> run = mActivityManager.getRunningAppProcesses(); 
		    for (ActivityManager.RunningAppProcessInfo pro : run){
		        if (pro.processName.equals(packageName)){
		        	isStarted = true;
		        	break;
		    }
		  }
        } catch (SecurityException e) {
		   e.printStackTrace();
		}
		return isStarted;
	}
	
	public static boolean isScreenLight(Context context) {
		PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		return pm.isScreenOn();
	}
	
	public static void turnScreenOn(Context context) {
		PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		WakeLock mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | 
				                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, 
				                            "SimpleTimer");
		mWakelock.acquire();
	} 
	
	public static boolean isTopApp(Context context, String packageName){  
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo>  tasksInfo = mActivityManager.getRunningTasks(1);  
        if (tasksInfo.size() > 0){  
            if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {  
                return true;  
            }  
        }  
        return false;  
    }
	
	public static boolean isTopActivity(Context context, String activityName){  
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo>  tasksInfo = mActivityManager.getRunningTasks(1);  
        if (tasksInfo.size() > 0){  
            if (activityName.equals(tasksInfo.get(0).topActivity.getClassName())) {  
                return true;  
            }  
        }  
        return false;  
    }
	

	/**
     * ����View��˸Ч��
     * 
     * */
/*	public static void startFlick(View view){
        if (null == view) {
            return;
        }
        Animation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(500);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(alphaAnimation);
    }*/

    /**
     * ȡ��View��˸Ч��
     * 
     * */
/*	public static void stopFlick(View view){
        if (null == view){
            return;
        }
        view.clearAnimation();
    }*/
	
	public static void playSound(final Context context, final int resId) {
		//final Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);
		final MediaPlayer player = MediaPlayer.create(context, resId); //new MediaPlayer();
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				player.stop();
				player.release();
			}
		});
		player.start();
	}
	
	public static void playVibrate(final Context context) {
		int milliseconds = 100;
		Vibrator mVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        mVibrator.vibrate(milliseconds);
        SystemClock.sleep(milliseconds + 100);
        mVibrator.vibrate(milliseconds);		
	}
	
	public static boolean isNetworkAvailable(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        if (cm != null) {   
           NetworkInfo[] info = cm.getAllNetworkInfo();   
           if (info != null) {   
               for (int i = 0; i < info.length; i++) {   
                   if (info[i].getState() == NetworkInfo.State.CONNECTED) {   
                       return true;   
                   }   
               }   
           }   
        }   
        return false;   
    } 	

	public static ArrayList<String> stringSplit(String string, String divisionChar)  
    {  
        if (null == string || string.isEmpty()) {
        	return null;
        }
		
		int i = 0;  
        StringTokenizer tokenizer = new StringTokenizer(string, divisionChar);  
          
        String[] str = new String[tokenizer.countTokens()];  
          
        while (tokenizer.hasMoreTokens())  
        {  
            str[i] = new String();  
            str[i] = tokenizer.nextToken();  
            i++;  
        }
        
        ArrayList<String> arrayStr = new ArrayList<String>();
        for (int j = 0; j < str.length; j++) {
        	arrayStr.add(str[j]);	
        }
          
        return arrayStr;  
    } 
	
	//����ն���Ļ���
	public static int getScreenWidth() {
		WindowManager wm = (WindowManager) SmartPlugApplication.getContext()
                .getSystemService(Context.WINDOW_SERVICE);		
		int width = wm.getDefaultDisplay().getWidth();
		
		return width;
	}
	
	
	//根据域名获得IP地址ַ
	public static String getIPAddress() {
		String ip = "";
		InetAddress ReturnStr = null;  
	    try {  
	        ReturnStr = java.net.InetAddress.getByName(PubDefine.THINGZDO_HOST_NAME);  
	        ip = ReturnStr.getHostAddress();
	        return ip;
	    } catch (UnknownHostException e) {  
	        e.printStackTrace();  
	        return "";  
	    }  
	}
	
	static public boolean isFileExists(final String file) {
	    File f = new File(file);
	    return f.exists();
	}
	
	static public void setImageViewSource(ImageView img, String imgFile) {
		if (null == imgFile || imgFile.isEmpty()) {
			img.setImageResource(R.drawable.smp_plug_big);
			img.setContentDescription("");
		} else {
			if (false == PubFunc.isFileExists(imgFile)) {
				img.setImageResource(R.drawable.smp_plug_big);	
				img.setContentDescription("");
			} else {
				Bitmap bmpPic = null;
				bmpPic = BitmapFactory.decodeFile(imgFile, null);				
				img.setImageBitmap(bmpPic);
				img.setContentDescription(imgFile);
			}
		}		
	}
	
    public static int hexStringToAlgorism(String hex) {  
        hex = hex.toUpperCase().trim();  
        int max = hex.length();  
        int result = 0;  
        for (int i = max; i > 0; i--) {  
            char c = hex.charAt(i - 1);  
            int algorism = 0;  
            if (c >= '0' && c <= '9') {  
                algorism = c - '0';  
            } else {  
                algorism = c - 55;  
            }  
            result += Math.pow(16, max - i) * algorism;  
        }  
        return result;  
    }
    
    public static String getTimerTypeLabel(Context context, int type) {
    	switch (type) {
    		case 0:
    			return SmartPlugApplication.getContext().getString(R.string.power);
    		case 1:
    			return SmartPlugApplication.getContext().getString(R.string.nightlight);
    		case 2:
    			return SmartPlugApplication.getContext().getString(R.string.bell);
    		case 3:
    			return SmartPlugApplication.getContext().getString(R.string.usb);    	
    		case 4:
    			return SmartPlugApplication.getContext().getString(R.string.openpc);    
    		case 5:
    			return SmartPlugApplication.getContext().getString(R.string.closepc);    			
    		default:
    			return SmartPlugApplication.getContext().getString(R.string.power);
    	}
    }
    
    public static String getAppVersion() {
    	PackageManager manager;

    	PackageInfo info = null;

    	manager = SmartPlugApplication.getContext().getPackageManager();

    	try {
    		info = manager.getPackageInfo(SmartPlugApplication.getContext().getPackageName(), 0);
    		return info.versionName;

    	} catch (NameNotFoundException e) {
    		e.printStackTrace();
    		return "1.0.0";
    	}    	
    }
    
    public static PowerOption getPowerStatus(final int value) {
    	PowerOption map = null;
    	map = new PowerOption();
    	int power_flag  = 1; //0001
    	int light_flag  = 2; //0010
    	int ubs_flag    = 4; //0100
    	int parent_flag = 8; //1000
    	if (1 == (power_flag & value)) {
    		map.mIsPwrOn = true;	
    	}
    	if (2 == (light_flag & value)) {
    		map.mIsLightOn = true;
    	}
    	if (4 == (ubs_flag & value)) {
    		map.mIsUsbOn = true;
    	}
    	if (8 == (parent_flag & value)) {
    		map.mIsParentOn = true;
    	}    	
    	return map;
    }

    public static String getTimeString() {
    	SimpleDateFormat  formatter = new SimpleDateFormat("yyyyMMddHHmmss");     
		Date  curDate   = new   Date(System.currentTimeMillis());
		String   str   =   formatter.format(curDate); 
		return str;
    }
    public static String getTimeHMSString() {
    	SimpleDateFormat  formatter = new  SimpleDateFormat("HH:mm:ss");     
		Date  curDate   = new   Date(System.currentTimeMillis());
		String   str   =   formatter.format(curDate); 
		return str;
    }
    public static String getTimeString(int days) {
    	Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, days);
        Date anotherday = c.getTime();        
        
    	SimpleDateFormat  formatter = new SimpleDateFormat("yyyyMMddHHmmss");     
		String   str   =   formatter.format(anotherday);
		return str;
    }
    
    public static Date stringToDate(String strTime) {
    	String formatType = "yyyyMMddHH:mm:ss";
    	String cur_date = getTimeString();
    	cur_date = (String) cur_date.subSequence(0, 8);
    	cur_date = cur_date + strTime;
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
		try {
			date = formatter.parse(cur_date);
		} catch (ParseException e) {
			e.printStackTrace();
			date = null;
		}
        return date;
    }
    
    public static Date stringToDate(String strTime, int days) {
    	String formatType = "yyyyMMddHH:mm:ss";
    	String cur_date = getTimeString(days);
    	cur_date = (String) cur_date.subSequence(0, 8);
    	cur_date = cur_date + strTime;
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
		try {
			date = formatter.parse(cur_date);
		} catch (ParseException e) {
			e.printStackTrace();
			date = null;
		}
        return date;
    }
    
    public static void log(String msg) {
    	if (false == PubDefine.RELEASE_VERSION) {
    		Log.i("---Thingzdo---", msg);
    	}
    }
    
    public static void log(String label, String msg) {
    	if (false == PubDefine.RELEASE_VERSION) {
    		Log.i(label, msg);
    	}
    }
    
    public static String getDeviceName(String plugType) {
    	if (plugType.equals("0_1") == true) {
    		return "未知类型";
    	} else if (plugType.equals("1_1") == true) {
    		return "智能插座";
    	} else if (plugType.equals("1_2") == true) {
    		return "智能插座(新)";
    	} else if (plugType.equals("3_1") == true) {
    		return "智能窗帘";
    	} else if (plugType.equals("4_1") == true) {
    		return "智能窗口";
    	} else if (plugType.equals("5_1") == true) {
    		return "智能开关";
    	} else if (plugType.equals("6_1") == true) {
    		return "智能水壶";
    	} else if (plugType.equals("7_1") == true) {
    		return "电脑模拟软件";
    	} else if (plugType.equals("8_1") == true) {
    		return "智能电池";
    	} else if (plugType.equals("9_1") == true) {
    		return "智能红外学习";
    	} else if (plugType.equals("10_1") == true) {
    		return "智能电子秤";
    	} else if (plugType.equals("11_1") == true) {
    		return "智能小车";
    	} else {
    		return "不可能的类型";
    	}
    }
    
    public static String getDeviceType(String plugType) {
    	if (plugType.equals("未知类型") == true) {
    		return "0_1";
    	} else if (plugType.equals("智能插座") == true) {
    		return "1_1";
    	} else if (plugType.equals("智能插座(新)") == true) {
    		return "1_2";
    	} else if (plugType.equals("智能窗帘") == true) {
    		return "3_1";
    	} else if (plugType.equals("智能窗口") == true) {
    		return "4_1";
    	} else if (plugType.equals("智能开关") == true) {
    		return "5_1";
    	} else if (plugType.equals("智能水壶") == true) {
    		return "6_1";
    	} else if (plugType.equals("电脑模拟软件") == true) {
    		return "7_1";
    	} else if (plugType.equals("智能电池") == true) {
    		return "8_1";
    	} else if (plugType.equals("智能红外学习") == true) {
    		return "9_1";
    	} else if (plugType.equals("智能电子秤") == true) {
    		return "10_1";
    	} else if (plugType.equals("智能小车") == true) {
    		return "11_1";
    	} else {
    		return "不可能的类型";
    	}
    }

    public static String getDeviceNameFromModule(String plugType) {
    	if (plugType.equals(PubDefine.DEVICE_UNKNOWN) == true) {
    		return "未知类型";
    	} else if (plugType.equals(PubDefine.DEVICE_SMART_PLUG) == true) {
    		return "智能插座";
    	} else if (plugType.equals(PubDefine.DEVICE_SMART_PLUG_BOARD) == true) {
    		return "智能插座(新)";
    	} else if (plugType.equals(PubDefine.DEVICE_SMART_CURTAIN) == true) {
    		return "智能窗帘";
    	} else if (plugType.equals(PubDefine.DEVICE_SMART_WINDOW) == true) {
    		return "智能窗口";
    	} else if (plugType.equals(PubDefine.DEVICE_SMART_SWITCH) == true) {
    		return "智能开关";
    	} else if (plugType.equals(PubDefine.DEVICE_SMART_KETTLE) == true) {
    		return "智能水壶";
    	} else if (plugType.equals(PubDefine.DEVICE_SMART_SIMULATION_PC) == true) {
    		return "电脑模拟软件";
    	} else if (plugType.equals(PubDefine.DEVICE_SMART_BATTERY) == true) {
    		return "智能电池";
    	} else if (plugType.equals(PubDefine.DEVICE_SMART_AIRCONSTUDY) == true) {
    		return "智能红外学习";
    	} else if (plugType.equals(PubDefine.DEVICE_SMART_STEELYARD) == true) {
    		return "智能电子秤";
    	} else if (plugType.equals(PubDefine.DEVICE_SMART_CAR) == true) {
    		return "智能小车";
    	} else {
    		return "不可能的类型";
    	}
    }
    private static Map<String, String> getDeviceMap() {
    	Map<String, String> m_mapTypes = new HashMap<String, String>();
    	m_mapTypes.put("0", PubDefine.DEVICE_UNKNOWN);
    	m_mapTypes.put("1", PubDefine.DEVICE_SMART_PLUG);
    	m_mapTypes.put("2", PubDefine.DEVICE_SMART_PLUG_BOARD);
    	m_mapTypes.put("3", PubDefine.DEVICE_SMART_CURTAIN);
    	m_mapTypes.put("4", PubDefine.DEVICE_SMART_WINDOW);
    	m_mapTypes.put("5", PubDefine.DEVICE_SMART_SWITCH);
    	m_mapTypes.put("6", PubDefine.DEVICE_SMART_KETTLE);
    	m_mapTypes.put("7", PubDefine.DEVICE_SMART_SIMULATION_PC);
    	m_mapTypes.put("8", PubDefine.DEVICE_SMART_BATTERY);
    	m_mapTypes.put("9", PubDefine.DEVICE_SMART_AIRCONSTUDY);
		
    	return m_mapTypes;
    }
    
    private static Map<String, String> getProductMap() {
    	Map<String, String> m_mapTypes = new HashMap<String, String>();
    	m_mapTypes.put("0", PubDefine.PRODUCT_UNKNOWN);
    	m_mapTypes.put("1", PubDefine.PRODUCT_PLUG);
    	m_mapTypes.put("2", PubDefine.PRODUCT_SMART_PLUG_AIRCON);
    	m_mapTypes.put("3", PubDefine.PRODUCT_SMART_PLUG_ENERGE);
    	m_mapTypes.put("4", PubDefine.PRODUCT_SMART_PLUG_TIME);
		
    	return m_mapTypes;
    }
    
    // 从ModuleType
    public static String getDeviceTypeFromModuleType(String moduleType) {
    	String type = "0";
    	String[] types = moduleType.split("_");
    	if (types.length >= 1) {
    		type = types[0];
    	}
    	
    	Map<String, String> m_mapTypes = getDeviceMap();
		
		String value = m_mapTypes.get(type);
		if (value == null || value.isEmpty() == true) {
			value = PubDefine.DEVICE_UNKNOWN;
		}
		
    	return value;
    }
    
    public static String getProductTypeFromModuleType(String moduleType) {
    	String type = "0";
    	String[] types = moduleType.split("_");
    	if (types.length >= 2) {
    		type = types[1];
    	}

    	Map<String, String> m_mapTypes = getProductMap();
    	
		String value = m_mapTypes.get(type);
		if (value == null || value.isEmpty() == true) {
			value = PubDefine.DEVICE_UNKNOWN;
		}
		
    	return value;
    }

    public static String getDeviceTypeFromSSID(String ssid) {
    	String type = "0";
    	String[] types = ssid.split("_");
    	if (types.length >= 3) {
    		type = types[2];
    	}

    	Map<String, String> m_mapTypes = getDeviceMap();
		
		String value = m_mapTypes.get(type);
		if (value == null || value.isEmpty() == true) {
			value = PubDefine.DEVICE_UNKNOWN;
		}
		
    	return value;
    }

    public static String getProductTypeFromSSID(String ssid) {
    	String type = "0";
    	String[] types = ssid.split("_");
    	if (types.length >= 4) {
    		type = types[3];
    	}

    	Map<String, String> m_mapTypes = getProductMap();
		
		String value = m_mapTypes.get(type);
		if (value == null || value.isEmpty() == true) {
			value = PubDefine.DEVICE_UNKNOWN;
		}
		
    	return value;
    }


	// 从网络中获取当前的天气
	public static void getWeatherWithHTTP(String city, Handler mHandler) {
		String str_output = "";
		try {
			if (city.isEmpty()) {
				str_output = "未设置所属的城市，请设置。";
			} else {
				String t_city = URLEncoder.encode(city, "utf-8");
				String url = "http://www.sojson.com/open/api/weather/json.shtml?city=" + t_city;
				
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {  	// 访问服务器成功
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
		msg.what = 12;		// MSG_READ_CITY_WEATHER
		msg.obj = str_output;
		
		mHandler.sendMessage(msg);
	}
	
	private static String parseJSONWithGSON(String jsonData) {
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
//			String o_item = String.valueOf(data.get(i));
//			JSONObject item = JSONObject.parseObject(o_item);
//			String fengxiang = item.getString("fengxiang");
//			String fengli = item.getString("fengli");
//			String high = item.getString("high");
//			String type = item.getString("type");
//			String low = item.getString("low");
//			String date = item.getString("date");
		}
		
		return String.valueOf(city + "温度" + wendu + ",湿度" + shidu + ",空气质量" + quality + ",PM二点五为" + pm25 + ",总结" + ganmao);
	}	

	public static String getWeatherCity(String cmd) {
		ArrayList<String> WEATHER_CITY = new ArrayList<String>(Arrays.asList("北京", "上海", "深圳", "武汉", "济南", "青岛", "厦门"));
		
		for (int i = 0; i < WEATHER_CITY.size(); i++) {
			if (cmd.contains(WEATHER_CITY.get(i))) {
				return WEATHER_CITY.get(i);
			}
		}
		
		return "";
	}
}

