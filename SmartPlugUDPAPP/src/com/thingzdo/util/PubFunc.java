package com.thingzdo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thingzdo.ui.common.PubDefine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class PubFunc {
	private static PubFunc mInstance = null;
	
	public final static int  MSG_NOTIFY_INIT					= 0;
	public final static int  MSG_NOTIFY_SUCCESS					= 1;
	public final static int  MSG_NOTIFY_FAIL					= 2;
	public final static int  MSG_NOTIFY_NETWORK_FAIL			= 3;
	public final static int  MSG_UPDATE_LISTVIEW_SCANPORT		= 4;
	public final static int  MSG_UPDATE_LISTVIEW_SCANPORT_END	= 5;
	public final static int  MSG_UPDATE_SCANBTN_ENABLE			= 6;
	public final static int  MSG_UPDATE_SCANBTN_DISABLE			= 7;
	public final static int  MSG_UPDATE_SHOW_INFO				= 8;
	public final static int  MSG_SHOW_MESSAGE 					= 9;
	public final static int  MSG_UPDATE_TJDATA 					= 10;
	public final static int  MSG_UPDATE_TJDATA_BAK 				= 11;
	private final static int MSG_READ_CITY_WEATHER				= 12;
	
	// UDP Scan Port Mode
	public static final int UDP_MAX_RETRIES_SEND		= 1; 	    				// 最大重发次数5次
	public static final int UDP_MAX_RETRIES_RECV		= 3; 	    				// 最大接收次数5次
	
	public final static int UDP_MAX_RECEIVE_IP_NUM  	= 256;						// 最大接收数据的次数
	public final static int UDP_RECEIVE_BUFFER_SIZE 	= 1024;						// 接收数据的缓冲器大小
	public final static int UDP_TIME_OUT				= 5000;						// UDP 网络访问超时时间 默认：5秒
	public final static int UDP_SRC_PORT 				= 50022;						// 本地UDP端口号
	public final static int UDP_DEST_PORT 				= 5000;						// 扫描UDP端口号：即：智能插座的UDP端口号 
	
	public final static String UDP_SCANPORT_COMMAND		= "0,REPORTIP,test,null#";	// 扫描UDP端口号用的命令行语句
	
	// TCP Scan Port Mode
	public final static int TCP_TIME_OUT				= 5000;
	public final static int TCP_PORT_ID					= 6002;
	
	// Thread Sleep Time for ListView Show Data
	public final static int THREAD_SLEEP_TIME			= 500;
	
	public static PubFunc getInstance() {
		if (null == mInstance) {
			mInstance = new PubFunc();
		}
		return mInstance;
	}
	
		// 根据 不同的语音内容 发送不同的命令
		@SuppressLint("NewApi") 
		public void parseCommand(String text, String moduleID, Handler mHandler, boolean b_speech, TextToSpeech tSpeech) {
//			showTip(mHandler, "命令：" + text);
			
			String cmd_text = "";			
			Map<String, Object> m_mapCmd = null;
			m_mapCmd = new HashMap<String, Object>();
			
			// 摇一摇灯的命令
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
			// 小车平台的命令
			String[] key_action_forward		= {"向前"};
			String[] key_action_backward	= {"向后"};
			String[] key_action_left		= {"左转"};
			String[] key_action_left2		= {"左传"};
			String[] key_action_right		= {"右转"};
			String[] key_action_speedup		= {"增加速度"};
			String[] key_action_speeddown	= {"降低速度"};
			String[] key_action_stop		= {"停止"};
			// 红外遥控的命令--空调
			String[] key_action_ir_air_poweron				= {"打开空调"};
			String[] key_action_ir_air_poweroff				= {"关闭空调"};
			String[] key_action_ir_air_adjust_temp_18		= {"空调调成18"};
			String[] key_action_ir_air_adjust_temp_19		= {"空调调成19"};
			String[] key_action_ir_air_adjust_temp_20		= {"空调调成20"};
			String[] key_action_ir_air_adjust_temp_21		= {"空调调成21"};
			String[] key_action_ir_air_adjust_temp_22		= {"空调调成22"};
			String[] key_action_ir_air_adjust_temp_23		= {"空调调成23"};
			String[] key_action_ir_air_adjust_temp_24		= {"空调调成24"};
			String[] key_action_ir_air_adjust_temp_25		= {"空调调成25"};
			String[] key_action_ir_air_adjust_temp_26		= {"空调调成26"};
			String[] key_action_ir_air_adjust_temp_27		= {"空调调成27"};
			String[] key_action_ir_air_adjust_temp_28		= {"空调调成28"};
			String[] key_action_ir_air_adjust_temp_29		= {"空调调成29"};
			String[] key_action_ir_air_adjust_temp_30		= {"空调调成30"};
			
			// 智能窗帘
			String[] key_action_curtain_open		= {"打开窗帘"};
			String[] key_action_curtain_close		= {"关闭窗帘"};
			String[] key_action_curtain_pause		= {"暂停窗帘"};
			
			
					
			// 摇一摇灯的命令字
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
			
			// 智能窗帘的命令字
			String cmd = "";
			m_mapCmd.put("20150101121212,APPCURTAIN_ACTION,test,12345678,1#", key_action_curtain_open);
			m_mapCmd.put("20150101121212,APPCURTAIN_ACTION,test,12345678,2#", key_action_curtain_close);
			m_mapCmd.put("20150101121212,APPCURTAIN_ACTION,test,12345678,0#", key_action_curtain_pause);
			
			// 小车平台的命令字
//			0,ACTION,TEST,12345678,MODE=FORWARD/BACKWARD/LEFT/RIGHT/SPEEDUP/SPEEDDOWN/STOP,VALUE1,VALUE2#
			m_mapCmd.put("0,ACTION,test,12345678,FORWARD,100,100#", key_action_forward);
			m_mapCmd.put("0,ACTION,test,12345678,BACKWARD,100,100#", key_action_backward);
			m_mapCmd.put("0,ACTION,test,12345678,LEFT,100,0#", key_action_left);
			m_mapCmd.put("0,ACTION,test,10000002,LEFT,100,0#", key_action_left2);
			m_mapCmd.put("0,ACTION,test,12345678,RIGHT,0,100#", key_action_right);
			m_mapCmd.put("0,ACTION,test,12345678,SPEEDUP,30,30#", key_action_speedup);
			m_mapCmd.put("0,ACTION,test,12345678,SPEEDDOWN,30,30#", key_action_speeddown);
			m_mapCmd.put("0,ACTION,test,12345678,STOP,0,0#", key_action_stop);
			
			// 红外遥控的命令字--空调
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3000,3000,3050,4200,600,1700,550,550,550,1700,550,700,550,550,550,1700,550,550,550,1850,550,550,600,1650,550,600,550,700,550,550,550,600,550,550,550,1800,550,600,550,550,600,1650,550,700,550,600,550,1650,550,550,600,1800,550,600,500,600,550,550,600,1800,550,1700,550,550,550,1700,550,1800,550,550,550,600,550,550,550,750,550,1650,550,1700,550,600,500,750,500,600,550,1700,550,550,550,700,550,600,550,550,550,600,550,700,550,550,550,1700,550,550,550,750,500,600,550,550,600,550,550,750,500,600,500,600,550,600,500,750,500,600,550,600,500,600,550,750,550,1700,500,600,550,1700,500,1900,500,600,550,600,500,1700,550,600,550,52614#", key_action_ir_air_poweron);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3000,3000,3000,4250,600,1650,550,600,550,1650,550,700,550,600,550,1700,500,600,550,1800,550,600,550,1700,550,550,550,750,500,600,500,600,550,600,500,750,500,600,550,600,550,1650,550,700,550,600,550,1650,550,600,550,1800,550,600,550,550,550,600,550,1800,550,1650,600,550,550,1700,550,1800,550,600,550,550,550,600,500,750,550,1650,550,1700,550,600,550,700,550,550,550,1700,550,550,550,750,500,600,550,600,500,600,550,750,500,550,550,1700,550,600,500,750,550,550,550,600,550,550,550,750,500,600,550,550,550,600,500,750,550,550,550,600,500,600,550,750,550,1650,550,600,550,1700,500,1850,550,600,550,550,550,600,550,1650,600,1836#", key_action_ir_air_poweroff);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3100,3000,3000,4200,650,1650,550,550,550,1650,600,700,550,550,600,1650,550,550,600,1750,600,550,600,550,550,1700,550,700,550,550,550,1700,550,1700,550,1750,600,550,550,550,600,1700,550,650,600,550,550,1700,550,550,550,1800,600,550,550,550,600,550,600,1750,600,1650,550,1700,550,1700,550,650,600,550,550,600,550,550,550,700,600,1650,550,1650,600,550,550,700,550,550,600,1650,550,550,600,650,600,550,600,550,550,550,550,700,600,550,550,1650,600,550,550,700,550,600,550,550,600,550,550,700,550,550,550,600,550,550,550,700,550,600,550,550,550,550,600,700,600,1650,550,550,600,550,550,1850,550,1650,550,600,550,1650,600,1650,600,59664#", key_action_ir_air_adjust_temp_18);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3050,3000,3000,4250,600,1650,550,550,600,1650,550,700,550,550,600,1650,600,550,550,1800,550,550,600,550,550,1700,550,1800,550,550,600,1650,550,1700,550,650,600,550,600,550,600,1650,550,700,550,550,600,1650,550,600,550,1800,550,550,550,550,600,550,600,1800,550,1700,550,1650,550,1700,550,700,550,550,550,600,550,550,550,700,600,1650,550,1700,550,550,550,700,550,600,550,1700,550,550,550,700,550,600,550,550,550,600,550,700,550,550,550,1700,550,550,550,700,550,600,550,550,600,550,550,700,550,550,550,600,550,550,550,700,550,600,550,550,550,600,550,750,500,1700,550,600,550,1650,550,750,550,1700,550,550,550,1700,550,550,600,27334#", key_action_ir_air_adjust_temp_19);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3050,3000,3000,4250,600,1650,550,550,600,1700,550,650,600,550,550,1650,600,550,550,1800,550,600,550,1700,550,550,550,700,550,600,550,1650,550,1700,550,650,600,600,550,550,600,1650,550,700,550,600,550,1650,550,550,600,1800,550,550,550,600,550,550,600,1750,600,1650,600,1650,550,1700,550,1800,550,600,550,550,550,600,550,700,550,1650,600,1650,550,550,600,650,600,550,550,1650,600,550,550,700,550,550,600,550,550,600,550,700,550,550,550,1700,550,550,600,650,600,550,550,550,600,550,550,700,550,600,550,550,550,600,550,650,600,550,550,600,500,600,550,750,550,1650,550,600,550,1700,550,1800,550,1700,550,550,550,1700,550,1700,550,62036#", key_action_ir_air_adjust_temp_20);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3000,3000,3050,4200,600,1700,550,550,550,1700,550,650,600,550,550,1700,550,550,550,1800,600,550,600,1600,600,600,550,1750,600,550,550,1700,550,1650,600,650,600,550,550,600,550,1650,600,700,550,550,550,1700,550,550,550,1800,550,600,550,550,550,600,550,1800,550,1700,550,1700,550,1650,550,1800,600,550,550,600,550,550,550,700,600,1650,550,1700,550,550,550,700,550,550,600,1650,550,600,550,700,550,550,600,550,550,550,550,700,550,600,550,1700,550,550,550,700,550,550,600,550,600,550,550,700,550,550,550,600,550,550,550,700,550,600,550,550,550,550,600,700,550,1700,550,1700,550,550,550,750,550,1650,550,600,550,1650,550,1700,600,8558#", key_action_ir_air_adjust_temp_21);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3050,3000,3050,4200,600,1700,550,550,550,1700,550,700,550,600,500,1700,550,550,600,1750,600,550,600,1650,550,1700,550,700,550,550,550,1700,550,1700,550,650,600,550,550,600,550,1700,550,650,600,550,550,1700,550,550,550,1800,600,550,550,600,550,550,550,1800,600,1650,550,1700,550,1700,550,1800,550,550,550,550,600,550,550,700,600,1650,550,1700,550,550,550,700,550,600,550,1700,550,550,550,700,550,550,600,550,550,600,550,650,600,550,550,1700,550,550,550,700,550,600,550,550,600,550,550,700,550,550,550,600,550,550,550,700,550,600,550,550,550,600,550,700,550,1700,550,1700,550,550,550,1850,550,1700,550,550,550,1700,550,1700,550,16552#", key_action_ir_air_adjust_temp_22);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3050,3000,3000,4250,600,1650,550,550,600,1650,550,700,550,600,550,1650,600,550,550,1800,550,600,550,1650,600,1650,550,1800,600,550,550,1650,600,1700,550,700,550,550,550,600,550,1650,550,750,500,600,550,1650,600,550,550,1800,550,600,550,550,550,1700,550,700,550,600,550,550,550,600,550,700,550,550,550,600,550,550,550,700,600,1650,550,1700,550,550,550,700,550,600,550,1650,550,600,550,700,550,550,600,550,550,550,550,700,550,600,550,1700,550,550,550,700,550,600,500,600,550,600,550,700,550,550,550,600,550,550,550,700,550,600,550,550,550,550,600,700,550,1700,550,1700,550,1650,550,750,550,1650,550,1700,550,600,550,550,600,27208#", key_action_ir_air_adjust_temp_23);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3050,3000,3000,4250,600,1650,550,550,600,1650,550,700,550,600,550,1650,550,600,550,1800,550,1650,600,550,550,600,550,700,550,600,500,1700,550,1650,600,700,550,550,550,600,550,1700,550,700,550,550,550,1700,550,550,550,1850,550,550,550,600,500,1700,600,650,550,600,550,550,550,600,550,1800,550,600,550,550,550,550,550,750,550,1650,550,1700,550,550,600,700,550,550,550,1700,550,550,550,700,550,600,550,550,550,600,550,700,550,550,550,1700,550,550,550,750,550,550,550,550,600,550,550,700,550,600,550,550,550,600,500,750,550,550,550,550,550,600,550,750,550,1650,550,1700,550,1700,550,1850,500,1700,550,1700,550,550,550,1700,600,16506#", key_action_ir_air_adjust_temp_24);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3050,2950,3050,4250,600,1650,550,600,550,1650,550,700,550,600,550,1650,550,600,550,1800,550,1650,600,550,600,550,550,1800,550,600,550,1650,600,1650,550,700,550,600,550,550,600,1650,550,700,550,600,550,1650,550,600,550,1800,550,550,550,600,550,1650,600,700,550,550,550,600,550,550,550,1850,500,600,550,550,550,600,550,700,550,1700,550,1650,550,600,550,700,550,600,500,1700,550,600,550,700,550,550,600,550,550,550,550,700,550,600,550,1650,550,600,550,700,550,550,550,600,550,600,500,750,500,600,550,550,550,600,550,700,550,550,550,600,550,550,600,700,550,600,500,600,550,550,550,750,550,1700,500,1700,550,600,550,1650,600,7222#", key_action_ir_air_adjust_temp_25);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3100,2950,3050,4250,600,1600,600,550,550,1700,550,700,550,550,600,1650,550,600,550,1800,550,1650,600,550,550,1700,550,700,550,550,600,1650,550,1700,550,700,550,550,550,600,550,1700,550,700,550,550,550,1650,600,550,550,1850,550,550,550,600,550,1650,600,700,550,550,550,550,600,550,550,1800,550,600,550,550,550,600,550,700,550,1650,550,1700,550,600,550,700,550,550,550,1700,550,550,550,700,550,600,550,550,600,550,550,700,550,550,550,1700,550,550,600,700,550,550,550,550,600,550,550,700,550,600,550,550,550,600,550,700,550,550,550,550,600,550,550,750,550,550,550,600,550,550,550,1850,550,1650,550,1700,550,600,550,1650,600,32714#", key_action_ir_air_adjust_temp_26);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3100,2950,3050,4250,600,1650,550,550,600,1650,550,700,550,600,550,1650,550,600,550,1750,600,1650,600,550,600,1650,550,1800,550,600,550,1650,600,1650,550,700,550,600,550,550,600,1650,550,700,550,550,600,1650,550,600,550,1750,600,550,550,600,550,1650,600,700,550,550,550,600,550,550,550,1800,550,600,550,550,550,600,550,700,550,1650,600,1650,550,600,550,700,550,550,550,1700,550,550,550,750,500,600,550,600,550,550,550,700,550,600,550,1650,550,600,550,700,550,550,550,600,550,550,550,750,500,600,550,550,550,600,550,700,550,550,550,600,550,550,600,700,550,550,550,600,550,1650,550,750,550,1700,550,1650,550,600,550,1650,600,41388#", key_action_ir_air_adjust_temp_27);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3100,3000,3000,4200,650,1650,550,550,600,1650,550,700,550,600,550,1650,600,550,550,1800,550,1700,550,1700,550,550,550,700,550,550,600,1650,600,1650,550,700,550,600,550,550,600,1650,550,700,550,600,550,1650,550,550,600,1800,550,550,550,600,550,1650,600,650,600,550,550,550,600,1650,550,700,600,550,550,550,550,600,550,700,550,1650,600,1650,600,550,550,700,550,550,550,1700,550,600,550,700,550,550,600,500,600,550,550,700,550,600,550,1650,550,600,550,700,550,550,550,600,550,600,550,700,550,550,550,550,550,600,550,700,550,550,550,600,550,550,600,700,550,600,550,550,550,1700,550,1800,550,1700,550,1700,550,1650,550,600,550,25644#", key_action_ir_air_adjust_temp_28);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3050,3000,3000,4250,600,1650,550,600,550,1650,550,700,550,600,550,1650,550,600,550,1750,600,1650,600,1650,600,550,550,1800,600,550,550,1700,550,1650,550,750,500,600,550,550,600,1650,550,700,550,600,550,1650,550,600,550,1800,550,600,550,550,550,1650,600,700,550,600,550,550,550,1650,600,650,600,550,550,600,550,550,550,700,600,1650,550,1700,550,550,550,700,550,600,550,1650,550,600,550,700,550,550,600,550,550,600,500,750,500,600,550,1700,550,550,550,700,550,600,550,550,600,550,550,700,550,550,550,600,550,550,550,700,550,600,550,550,550,600,550,700,550,600,550,1650,550,600,550,700,550,1700,550,1700,550,1650,550,600,550,29694#", key_action_ir_air_adjust_temp_29);
			m_mapCmd.put("0,AIRCON,test,12345678,0,0,150,3050,3000,3050,4200,600,1700,550,550,550,1650,600,650,600,550,550,1700,550,550,550,1800,600,1650,600,1650,550,1700,550,700,550,600,500,1700,550,1700,550,700,550,550,550,600,550,1650,600,650,600,550,550,1650,600,550,550,1800,600,550,550,600,550,1650,600,650,600,550,550,550,550,1700,550,700,550,600,550,550,550,600,500,750,550,1700,500,1700,550,600,550,650,600,550,550,1700,550,550,550,700,550,600,550,600,500,600,550,700,550,550,550,1700,550,600,550,650,600,550,550,600,550,550,550,700,550,600,550,550,550,600,550,700,550,550,550,600,500,600,550,750,550,550,550,1700,550,550,550,1850,550,1700,550,1650,550,1700,550,600,550,44688#", key_action_ir_air_adjust_temp_30);
					
			
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
				if (b_speech == true && tSpeech != null) {
					String speech_text = "正在为您" + text;
					tSpeech.speak(speech_text,  TextToSpeech.QUEUE_FLUSH, null);
				}
				
				new send_NetWorkData_Async(moduleID, cmd_text, mHandler).execute();
			}
		}
		

		private byte[] changeCommand(String cmd_text) {
			byte[] byte_info;
			
			String revMsg = cmd_text.substring(0, cmd_text.indexOf("#"));
			String arrays[] = revMsg.split(",");
			String cmd = arrays[1];
			int i = 0;
			int j = 0;
			
			// 红外命令单独处理
			if (arrays[1].equals("AIRCON") == true) {
				byte_info = new byte[512];
				
				// 临时Debug，要删除
				int count = 0;
				int length = Integer.parseInt(arrays[6]);
				for (j = 0; j < length; j++) {
					int value = Integer.parseInt(arrays[7 + j]);
					char a = (char)((value >> 8) & 0xFF);
					char b = (char)(value & 0xFF);
					count = count + a + b;
				}
				
				String strHeader = arrays[0] + "," + arrays[1] + "," + arrays[2] + "," + arrays[3] + "," + arrays[4] + "," + arrays[5] + "," +  arrays[6] + "," + count + ",";
				byte[] temp = strHeader.getBytes();
				for (i = 0; i < temp.length; i++) {
					byte_info[i] = temp[i];
				}
				
				length = Integer.parseInt(arrays[6]);
				for (j = 0; j < length; j++) {
					int value = Integer.parseInt(arrays[7 + j]);
					
					byte_info[i++] = (byte)((value >> 8) & 0xFF);
					byte_info[i++] = (byte)(value & 0xFF);
				}
				
				byte_info[i++] = '#';
				for (; i < 512; i++) {
					byte_info[i] = 0;
				}
				
				String print_str = "";
				for (i = 0; i < byte_info.length; i++) {
					int v = byte_info[i] & 0xFF;
			        String hv = Integer.toHexString(v);
			        
					print_str = print_str + hv + ",";
				}
				
				Log.v("IR_Send", "IR_Send command:" + new String(byte_info));
				Log.v("IR_Send", "IR_Send command:" + print_str);
				
			} else {
				 byte_info = cmd_text.getBytes();
				 Log.v("IR_Send", "Normal command:" + new String(byte_info));
			}
			
			return byte_info;
			
		}

		private void showTip(final Handler mHandler, final String str)
		{
			new Thread(new Runnable(){
				public void run(){
					Message msg = new Message();
					msg.what = MSG_SHOW_MESSAGE;
					msg.obj = str;
					mHandler.sendMessage(msg);
				}
			}).start();
		}

		class send_NetWorkData_Async extends AsyncTask<Void, Void, Void>  {
			private String ipAddress;
			private String moduleID;
			private String str_Text;
			private byte[] str_Text_byte = new byte[512];
			private Socket socket = null;
			private String buffer = "";
			private int timeout = 500;
			Handler mHandler = null;
			
			int msg_notify = MSG_NOTIFY_SUCCESS;

			public send_NetWorkData_Async(String moduleID, String str_Text, Handler handler) {
				this.ipAddress = PubDefine.THINGZDO_HOST_NAME;
				this.moduleID = moduleID;
				this.str_Text = str_Text;
				mHandler = handler;
			}
			
			public send_NetWorkData_Async(String ipAddress, byte[] str_Text) {
				this.ipAddress = ipAddress;
				for (int i = 0; i < str_Text.length; i++) {
					this.str_Text_byte[i] = str_Text[i];
				}			
			}

			@Override
			protected Void doInBackground(Void... params) {
				// UDP Mode
				msg_notify = sendCommandRequestWithUDPSocket(ipAddress, UDP_SRC_PORT, UDP_DEST_PORT, timeout, UDP_MAX_RETRIES_SEND, str_Text);
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
					mHandler.sendEmptyMessage(msg_notify);
			}

		}
		

		public static int sendCommandRequestWithUDPSocket(String ipAddress, int localPort, int remotePort, int timeout, int max_retries, String str_Text) {
			int msg_notify = 0;
			try {
				InetAddress remoteInetAddress = InetAddress.getByName(ipAddress); // 服务器地址
			    // Convert the argument String to bytes using the default encoding
			    //发送的信息
			    byte[] bytesToSend = str_Text.getBytes();
		
			    DatagramSocket socket = new DatagramSocket(localPort);
			    socket.setSoTimeout(timeout); 									// 设置阻塞时间
			
			    DatagramPacket sendPacket = new DatagramPacket(bytesToSend, 	// 相当于将发送的信息打包
			        bytesToSend.length, remoteInetAddress, remotePort);
			
			    int tries = 0;      											// Packets may be lost, so we have to keep trying
			    do {
			    	// Send Function
			    	try {
			    		socket.send(sendPacket);          	// 发送信息
			    	 } catch (InterruptedIOException e) { // 当receive不到信息或者receive时间超过3秒时，就向服务器重发请求
					        tries += 1;
					        Log.v("Send UDP Message", "Send Timed out, " + (max_retries - tries) + " more tries...");
					 }
					    
			    } while (tries >= max_retries);
			
			    socket.close();
					
			    if (tries < max_retries) { 	// 发送成功
					msg_notify = MSG_NOTIFY_SUCCESS;
				} else {					// 发送失败
					msg_notify = MSG_NOTIFY_NETWORK_FAIL;
				}
			} catch (Exception e) {
				e.printStackTrace();
				msg_notify = MSG_NOTIFY_NETWORK_FAIL;
			}
			return msg_notify;
		}

		public static int sendCommandRequestWithUDPSocket(String ipAddress, int localPort, int remotePort, int timeout, int max_retries, byte[] str_Text) {
			int msg_notify = 0;
			try {
				InetAddress remoteInetAddress = InetAddress.getByName(ipAddress); // 服务器地址
			    // Convert the argument String to bytes using the default encoding
			    //发送的信息
			    byte[] bytesToSend = str_Text;
		
			    DatagramSocket socket = new DatagramSocket(localPort);
			    socket.setSoTimeout(timeout); 									// 设置阻塞时间
			
			    DatagramPacket sendPacket = new DatagramPacket(bytesToSend, 	// 相当于将发送的信息打包
			        bytesToSend.length, remoteInetAddress, remotePort);
			
			    int tries = 0;      											// Packets may be lost, so we have to keep trying
			    do {
			    	// Send Function
			    	try {
			    		socket.send(sendPacket);          	// 发送信息
			    	 } catch (InterruptedIOException e) { // 当receive不到信息或者receive时间超过3秒时，就向服务器重发请求
					        tries += 1;
					        Log.v("Send UDP Message", "Send Timed out, " + (max_retries - tries) + " more tries...");
					 }
					    
			    } while (tries >= max_retries);
			
			    socket.close();
					
			    if (tries < max_retries) { 	// 发送成功
					msg_notify = MSG_NOTIFY_SUCCESS;
				} else {					// 发送失败
					msg_notify = MSG_NOTIFY_NETWORK_FAIL;
				}
			} catch (Exception e) {
				e.printStackTrace();
				msg_notify = MSG_NOTIFY_NETWORK_FAIL;
			}
			
			return msg_notify;
		}

}
