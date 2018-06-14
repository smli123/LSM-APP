package com.thingzdo.dataprovider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.thingzdo.ui.UserDefine;
import com.thingzdo.ui.common.PubFunc;

public class UserHelper {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "UserHelper";
	
	public UserHelper(Context context){
		mContext = context;
		if (mContext == null) {
			Log.e(TAG, "Context is empty");
		} else {
			mContentResolver = mContext.getContentResolver();
		}
	}
	
	private UserDefine getData(Cursor cur) {
		UserDefine user = new UserDefine();
    	while (cur.moveToNext()){
    		user.mID = cur.getInt(SmartPlugContentDefine.User.ID_COLUMN);
    		user.mName = cur.getString(SmartPlugContentDefine.User.USER_NAME_COLUMN);
    		user.mPwd = cur.getString(SmartPlugContentDefine.User.USER_PWD_COLUMN);
    		user.mEMail = cur.getString(SmartPlugContentDefine.User.PWD_FIND_EMAIL_COLUMN);
    		user.mKeepPwd = cur.getInt(SmartPlugContentDefine.User.PWD_KEEP_COLUMN) == 1 ? true : false;
    		user.mLoginMode = cur.getInt(SmartPlugContentDefine.User.LOGIN_MODE_COLUMN) ;
    	}		
		return user;	
	}
	

	public UserDefine getUser(){
		if (null == mContentResolver) {
			return null; 
		}
		UserDefine user = null;
		String where = "";
		String order = SmartPlugContentDefine.User._ID  + " asc";
    	Cursor cur = mContentResolver.query(SmartPlugContentDefine.User.ALL_CONTENT_URI, 
    			                            null, 
				                            where, 
				                            null, 
				                            order);
    	
    	
    	if (null != cur) {
    		user = getData(cur);
    		cur.close();
    	}
		return user;
    }	
    

    public UserDefine getUser(String userName){
    	UserDefine user = new UserDefine();
		if (null == mContentResolver) {
			return null; 
		}    	
    	
		String where = SmartPlugContentDefine.User.USER_NAME + "='" + userName + "'";
    	    	
    	Cursor cur = mContentResolver.query(SmartPlugContentDefine.User.ALL_CONTENT_URI, 
                                            null, 
                                            where, 
                                            null, 
                                            null);
    	if (null != cur) {
    		if (cur.moveToFirst() == false) {
            	cur.close();
            	return null;    			
    		}
       		user.mID = cur.getInt(SmartPlugContentDefine.User.ID_COLUMN);
       		user.mName = cur.getString(SmartPlugContentDefine.User.USER_NAME_COLUMN);
       		user.mPwd = cur.getString(SmartPlugContentDefine.User.USER_PWD_COLUMN);
       		user.mEMail = cur.getString(SmartPlugContentDefine.User.PWD_FIND_EMAIL_COLUMN);
       		user.mKeepPwd = cur.getInt(SmartPlugContentDefine.User.PWD_KEEP_COLUMN) == 1 ? true : false;
       		user.mLoginMode = cur.getInt(SmartPlugContentDefine.User.LOGIN_MODE_COLUMN);;
        	cur.close();
        	return user;         	
    	} else {
        	return null;     		
    	}
    }
    

    public long addUser(UserDefine user){
		if (null == mContentResolver) {
			return 0; 
		} 
		if (null == user) {
			return 0;
		}
		
		deleteUser(user.mName);
			
    	ContentValues values = new ContentValues();
    	values.put(SmartPlugContentDefine.User.USER_NAME, user.mName);
    	values.put(SmartPlugContentDefine.User.USER_PWD, user.mPwd);
    	values.put(SmartPlugContentDefine.User.PWD_FIND_EMAIL, user.mEMail);
    	values.put(SmartPlugContentDefine.User.PWD_KEEP, user.mKeepPwd);
    	values.put(SmartPlugContentDefine.User.LOGIN_MODE, user.mLoginMode);
    	
    	Uri uri = mContentResolver.insert(SmartPlugContentDefine.User.ALL_CONTENT_URI, values);
    	PubFunc.log(TAG, "Insert a record");
    	
    	if (null == uri) {
    		return 0;
    	}
        try {
    	    long id = ContentUris.parseId(uri);
    	    return id;
        } catch(Exception e) {
        	Log.e(TAG, e.getMessage());
        	return 0;
        }
    }
    
    public boolean deleteUser(String userName){
		if (null == mContentResolver) {
			return false; 
		}
    	String where = SmartPlugContentDefine.User.USER_NAME + "='" + userName + "'"; 
    	int count = mContentResolver.delete(SmartPlugContentDefine.User.ALL_CONTENT_URI, where, null);
    	return count > 0 ? true : false;
    }
       

    public void clearUser(){
		if (null != mContentResolver) {
			PubFunc.log(TAG, "Clear users");
			mContentResolver.delete(SmartPlugContentDefine.User.ALL_CONTENT_URI, null, null); 
		}
    }
    
    public int modifyUser(UserDefine user){
		if (null == mContentResolver) {
			return -1; 
		} 
		if (null == user) {
			return -1;
		}
			
    	ContentValues values = new ContentValues();
    	values.put(SmartPlugContentDefine.User.USER_PWD, user.mPwd);
    	values.put(SmartPlugContentDefine.User.PWD_FIND_EMAIL, user.mEMail);
    	values.put(SmartPlugContentDefine.User.PWD_KEEP, user.mKeepPwd);
    	values.put(SmartPlugContentDefine.User.LOGIN_MODE, user.mLoginMode);
    	
    	String where = null;
    	if (!user.mName.isEmpty()) {
    		where = SmartPlugContentDefine.User.USER_NAME + "='" + user.mName + "'";
    	}
    	int index = mContentResolver.update(SmartPlugContentDefine.User.ALL_CONTENT_URI, 
    			                         values, 
    			                         where, 
    			                         null);
    	PubFunc.log(TAG, "Update a record");
        return index;
    } 
}