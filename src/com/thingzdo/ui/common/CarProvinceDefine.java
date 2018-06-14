package com.thingzdo.ui.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarProvinceDefine {
	
	private static Map<String, String> g_Province = new HashMap<String, String>();
	static
	{
		g_Province.put("00", "粤");	
		g_Province.put("01", "京");
		g_Province.put("02", "津");
		g_Province.put("03", "冀");
		g_Province.put("04", "晋");
		g_Province.put("05", "蒙");
		g_Province.put("06", "辽");
		g_Province.put("07", "吉");
		g_Province.put("08", "黑");
		g_Province.put("09", "沪");
		g_Province.put("10", "苏");
		g_Province.put("11", "浙");
		g_Province.put("12", "皖");
		g_Province.put("13", "闽");
		g_Province.put("14", "赣");
		g_Province.put("15", "鲁");
		g_Province.put("16", "豫");
		g_Province.put("17", "鄂");
		g_Province.put("18", "湘");
		g_Province.put("19", "桂");
		g_Province.put("20", "琼");
		g_Province.put("21", "渝");
		g_Province.put("22", "川");
		g_Province.put("23", "贵");
		g_Province.put("24", "云");
		g_Province.put("25", "藏");
		g_Province.put("26", "陕");
		g_Province.put("27", "甘");
		g_Province.put("28", "青");
		g_Province.put("29", "宁");
		g_Province.put("30", "新");
		g_Province.put("31", "港");
		g_Province.put("32", "澳");
		g_Province.put("33", "台");
	};
	
	public static String getProviceName(String code) {
		for (String key : g_Province.keySet()) {
			if (code.equals(key)) {
				return g_Province.get(key);
			}			
		}
		
		return "";
	}
	
	public static String getProviceCode(String name) {
		for (String key : g_Province.keySet()) {
			if (name.equals(g_Province.get(key))) {
				return key;
			}			
		}
		
		return "00";
	}	
	
	public static List<String> getAllProvince() {
		List<String> list = new ArrayList<String>();
		for (String key : g_Province.keySet()) {
			list.add(g_Province.get(key));	
		}
		return list;
	}
	
	public static String getCarRealName(String name) {
		//车牌号格式：00B41Q49 -->粤B41Q49
		String provice = getProviceName(name.substring(0, 2));
		String carName = provice + name.substring(2, name.length());	
		return carName;
	}
}
