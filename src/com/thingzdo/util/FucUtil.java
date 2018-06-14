package com.thingzdo.util;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 功能性函数扩展类
 */
public class FucUtil {
	/**
	 * 读取asset目录下文件�??
	 * @return content
	 */
	public static String readFile(Context mContext,String file,String code)
	{
		int len = 0;
		byte []buf = null;
		String result = "";
		try {
			InputStream in = mContext.getAssets().open(file);			
			len  = in.available();
			buf = new byte[len];
			in.read(buf, 0, len);
			
			result = new String(buf,code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 向子网掩码maskIP 和 端口号portID 发送 字符串sendText；
	public static void getBroadcastData(String maskIP, int portID, String sendText) {
		try {
			DatagramSocket socket = new DatagramSocket(8000);
			socket.setBroadcast(true);
			InetAddress addr = InetAddress.getByName(maskIP);
			byte[] buffer = sendText.getBytes();
			DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
			packet.setAddress(addr);
			packet.setPort(8086);
		
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static InetAddress getBroadcastAddress(Context context, String maskIP) throws UnknownHostException {
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
	
	public static Boolean isWifiApEnabled(Context context) {
	    try {
	        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);  
	        Method method = manager.getClass().getMethod("isWifiApEnabled");
	        return (Boolean)method.invoke(manager);
	    }
	    catch (NoSuchMethodException e) {
	        e.printStackTrace();
	    }
	    catch (Exception e)  {
	        e.printStackTrace();
	}
	    return (Boolean)false;
	}
	
	public static String getWeek() {
		Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
        case 1:
            return "星期日";
        case 2:
            return "星期一";
        case 3:
            return "星期二";
        case 4:
            return "星期三";
        case 5:
            return "星期四";
        case 6:
            return "星期五";
        case 7:
            return "星期六";
        default:
            return "";
        }
	}

	public static String getTimeString() {
		SimpleDateFormat  formatter = new  SimpleDateFormat("yyyyMMddHHmmss");     
		Date  curDate   = new   Date(System.currentTimeMillis());//获取当前时间     
		String   str   =   formatter.format(curDate); 
		return str;
	}

	public static String getLocalIP(final Context context) {
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		if (ipAddress == 0)
			return null;
		return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."+(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
	}

	/**
	 * 从字符串文本中获得数字
	 * @param text
	 * @return
	 */
	public static List<Long> getDigit(String text) {
		List<Long> digitList = new ArrayList<Long>();
		Pattern p = Pattern.compile("(\\d+)");
		Matcher m = p.matcher(text);
		while (m.find()) {
			String find = m.group(1).toString();
			digitList.add(Long.valueOf(find));
		}
		return digitList;
	}
	
	/**
	* 日期转换成字符串
	* @param date 
	* @return str
	*/
	public static String DateToStr(Date date) {
	  
	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   String str = format.format(date);
	   return str;
	} 

	/**
	* 字符串转换成日期
	* @param str
	* @return date
	*/
	public static Date StrToDate(String str) {
	  
	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   Date date = null;
	   try {
		   date = format.parse(str);
	   } catch (ParseException e) {
	    e.printStackTrace();
	   }
	   return date;
	}

}
