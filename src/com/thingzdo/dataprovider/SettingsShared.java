package com.thingzdo.dataprovider;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsShared {
    private SharedPreferences mData = null;
    private String mTimelessField = "Timeless";
    private String mGPRSField = "GPRS";
    private String mSharkField = "SharkEnable";
    private String mMaintainField = "Maintain";
    
    private String mGPSReport = "GPSReport";
    
    final private int default_StartTimeless = 1; //1:点火不限时        0：点火限时
    final private int default_GPRSKey = 1;    //0:GPRS关闭 1：GPRS打开
    final private int default_ShakeEnable = 0;
    final private int default_Maintain = 0;
    final private int default_gpsReport = 1;//

    public SharedPreferences getSlavePhoneShared() {
    	return mData; 
    }
    
    public SettingsShared(Context context){
    	mData = context.getSharedPreferences("Settings_Command", 0);
    }
   
    public boolean isTimeless(){
    	String timeless = readTimelessData(mTimelessField);
    	try {
    		return Integer.parseInt(timeless) == 1 ? true : false;
    	} catch (Exception exception) {
    		return true;
    	}
    }
    
    public boolean isGPRSOpen(){
    	String key = readGPRSData(mGPRSField);
    	try {
    		return Integer.parseInt(key) == 1 ? true : false;
    	} catch (Exception exception) {
    		return false;
    	}
    }
    
    public boolean isShakeEnable(){
    	String key = readShakeEnableData(mSharkField);
    	try {
    		return Integer.parseInt(key) == 1 ? true : false;
    	} catch (Exception exception) {
    		return false;
    	}
    }    
    
    public boolean isGPSReported(){
    	String key = readData(this.mGPSReport,this.default_gpsReport);
    	try {
    		return Integer.parseInt(key) == 1 ? true : false;
    	} catch (Exception exception) {
    		return false;
    	}	
    }
    
    public void setTimeless(boolean timeless){
    	int value = (timeless ? 1 : 0);
    	writeData(mTimelessField, String.valueOf(value));
    }    
    
    public void setGPRS(boolean key){
    	int value = (key ? 1 : 0);
    	writeData(mGPRSField, String.valueOf(value));
    }  
    
    public void setShakeEnable(boolean key){
    	int value = (key ? 1 : 0);
    	writeData(mSharkField, String.valueOf(value));
    }   
    public void setGPSReport(boolean key){
    	int value = (key ? 1 : 0);
    	writeData(this.mGPSReport, String.valueOf(value));
    }
    
    private void writeData(String name, String value) {
    	if (mData != null) {
    	    mData.edit().putString(name, value).commit();
    	}
    }
    private String readData(String name,int defaultValue){
    	
    	if (mData != null) {
    	    return mData.getString(name, String.valueOf(defaultValue));
    	}    	
		return String.valueOf(defaultValue);
    }
    
    private String readTimelessData(String name) {
    	if (mData != null) {
    	    return mData.getString(name, String.valueOf(default_StartTimeless));
    	}    	
		return String.valueOf(default_StartTimeless);
    }
    
    private String readGPRSData(String name) {
    	if (mData != null) {
    	    return mData.getString(name, String.valueOf(default_GPRSKey));
    	}    	
		return String.valueOf(default_GPRSKey);
    } 
    
    private String readShakeEnableData(String name) {
    	if (mData != null) {
    	    return mData.getString(name, String.valueOf(default_ShakeEnable));
    	}    	
		return String.valueOf(default_ShakeEnable);
    }
    
    private String readMaintainData(String name) {
    	if (mData != null) {
    	    return mData.getString(name, String.valueOf(default_Maintain));
    	}    	
		return String.valueOf(default_Maintain);
    }    

	public boolean isMaintain() {
    	String key = readMaintainData(mMaintainField);
    	try {
    		return Integer.parseInt(key) == 1 ? true : false;
    	} catch (Exception exception) {
    		return false;
    	}
	}

	public void setMaintain(boolean maintain) {
    	int value = (maintain ? 1 : 0);
    	writeData(mMaintainField, String.valueOf(value));
	}    
}
