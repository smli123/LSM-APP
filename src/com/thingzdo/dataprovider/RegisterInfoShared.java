package com.thingzdo.dataprovider;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.thingzdo.ui.RegisterInfo;

public class RegisterInfoShared extends Activity{
    private SharedPreferences mData;
    private String mPwd = null;
    
    public RegisterInfoShared(Context context){
    	mData = context.getSharedPreferences("Register_Info", 0);	
    }
    
    public void setRegisterInfo(RegisterInfo info) {
    	mPwd = info.mPassword; 
    	if (mData != null) {
    		writeData("UserName", info.mUserName);
    		writeData("UserPwd", mPwd);//MD5Util.string2MD5(mPwd));
    		writeData("EMail", info.mEmail);
    		writeData("State", String.valueOf(info.mState));
    		writeData("KeepPwd", String.valueOf(info.mKeepPwd));
    		writeData("WifiDirect", String.valueOf(info.mWifiDirect));
    	}
    }
    
	public RegisterInfo getRegisterInfo(){
    	RegisterInfo info = new RegisterInfo();
    	info.mUserName = readData("UserName");
    	info.mPassword = readData("UserPwd");//MD5Util.convertMD5(MD5Util.convertMD5(readData("UserPwd")));
    	info.mEmail = readData("EMail");
    	String state = readData("State");
    	if (null == state || state.isEmpty()) {
    		info.mState = 1;	
    	} else {
    		info.mState = Integer.parseInt(state);
    	}
    	
    	String keeppwd = readData("KeepPwd");
    	if (null == keeppwd || keeppwd.isEmpty()) {
    		info.mKeepPwd = 0;	
    	} else {
    		info.mKeepPwd = Integer.parseInt(keeppwd);
    	}

    	String wifidirect = readData("WifiDirect");
    	if (null == wifidirect || wifidirect.isEmpty()) {
    		info.mWifiDirect = 0;	
    	} else {
    		info.mWifiDirect = Integer.parseInt(wifidirect);
    	}
    	return info;
    }
    
    public void updateRegisterInfo(RegisterInfo info){
    	setRegisterInfo(info);	
    }
    
    private void writeData(String name, String value) {
    	if (mData != null) {
    	    mData.edit().putString(name, value).commit();
    	}
    }
    
    private String readData(String name) {
    	if (mData != null) {
    	    return mData.getString(name, "");
    	}    	
		return "";
    }
}
 