package com.thingzdo.dataprovider;

import android.content.Context;
import android.content.SharedPreferences;

public class MasterPhoneShared {
	private SharedPreferences mData = null;
	private int PWD_CHANGE_FLAG = 0;
	private final String DEFAULT_MASTER_PWD = "0000"; 
	private final String MASTER_SHARED_FILE = "Master_Share";
	private final String MASTER_PWD_FIELD = "Master_PWD";
	private final String MASTER_Phone_FIELD = "Master_Phone";
	private final String MASTER_PWD_CHANGE_FLAG = "Pwd_Change_Flag";

    public MasterPhoneShared(Context context){
    	mData = context.getSharedPreferences(MASTER_SHARED_FILE, 0);
    }
    
	public String getPwd() {
		String pwd = readData(MASTER_PWD_FIELD); 
		if (pwd.isEmpty()) {
			return DEFAULT_MASTER_PWD; 
		}
		return pwd;
	}
	
	public boolean setPwd(final String newPwd) {
		if (true == isPwdOk(newPwd)) {
			writeData(MASTER_PWD_FIELD, newPwd);
			return true;
		}
		return false;
	}
	
	private boolean isPwdOk(final String newPwd) {
		if (newPwd.equals(DEFAULT_MASTER_PWD)) {
		    return false;
		}
		return true;
	}
	
	public boolean isPwdSet() {
		String sFlag = readData(MASTER_PWD_CHANGE_FLAG);
		if (sFlag.isEmpty()) {
			return false;
		}
		int flag = Integer.parseInt(sFlag);
		if (flag == PWD_CHANGE_FLAG) {
			return false;
		}
		return true;
	}
	
	public void setPwdChanged() {
		PWD_CHANGE_FLAG = 1;
		writeData(MASTER_PWD_CHANGE_FLAG, String.valueOf(PWD_CHANGE_FLAG));
	}
	
	public String getMasterPhone() {
		return readData(MASTER_Phone_FIELD);
	}
	
	public boolean setMasterPhone(final String newPhone) {
		writeData(MASTER_Phone_FIELD, newPhone);
		return true;
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
