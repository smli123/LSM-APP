package com.thingzdo.dataprovider;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

public class SlavePhoneShared {
    private SharedPreferences mData = null;
    private String[] mField  = {"Phone_No1", 
    		                    "Phone_No2",
    		                    "Phone_No3",
    		                    "Phone_No4"};
    private ArrayList<String> mPhoneList = new ArrayList<String>();
    
    public SharedPreferences getSlavePhoneShared() {
    	return mData; 
    }
    
    public SlavePhoneShared(Context context){
    	mData = context.getSharedPreferences("Slave_phone", 0);
    	//init();
    }
    
    private void init() {
    	mPhoneList.clear();
    	for (int i = 0; i < mField.length; i++) {
    		String phone = readData(mField[i]);
    		if (!phone.isEmpty()) {
    			mPhoneList.add(phone);	
    		}
    	}
    }
    
    private void update() {
    	for (int i = 0; i < mField.length; i++) {
    		mData.edit().remove(mField[i]).commit();	
    	} 
    	
    	for (int i = 0; i < mPhoneList.size(); i++) {
    		String field = "Phone_No" + String.valueOf(i + 1);
    		writeData(field, mPhoneList.get(i));	
    	}
    }
    
    public ArrayList<String> getPhone() {
    	init();
    	return mPhoneList;	
    }
    
    public void addPhone(String phone) {
  	    if (-1 == mPhoneList.indexOf(phone)) {
   		    mPhoneList.add(phone);
   		    update();
    	}
    }
    
    public boolean deletePhone(String phone) {
    	if (-1 != mPhoneList.indexOf(phone)) {
    	    mPhoneList.remove(phone);	
   		    update();
   		    return true;
    	}
    	return false;
    }    
    
    public int getPhoneCount() {
    	return mPhoneList.size();
    }
    
    public void clear() {
    	mPhoneList.clear();
    	for (int i = 0; i < 4; i++) {
    		String field = "Phone_No" + String.valueOf(i + 1);
    		writeData(field, "");	
    	}    	
    }
    
   
    public boolean changePhone(String phoneOld, String phoneNew) {
    	int index = mPhoneList.indexOf(phoneOld);
    	if (-1 == index) {
    		return false;
    	} else {
    		mPhoneList.set(index, phoneNew);
    		update();
    		return true;
    	}
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
