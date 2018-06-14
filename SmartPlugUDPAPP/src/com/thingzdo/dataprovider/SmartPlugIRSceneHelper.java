package com.thingzdo.dataprovider;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.thingzdo.ui.IRSceneDefine;

public class SmartPlugIRSceneHelper {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "UserHelper";
	
	public SmartPlugIRSceneHelper(Context context){
		mContext = context;
		if (mContext == null) {
			Log.e(TAG, "Context is empty");
		} else {
			mContentResolver = mContext.getContentResolver();
		}
	}
	
    public ArrayList<IRSceneDefine> getAllIRScene(String plugId){
		ArrayList< IRSceneDefine > timers = new ArrayList< IRSceneDefine >();
		if (null == mContentResolver) {
			return null; 
		}
		String where = SmartPlugContentDefine.SmartPlugIRScene.PLUG_ID + " = '" + plugId + "'";
		String order = SmartPlugContentDefine.SmartPlugIRScene._ID  + " asc";
    	Cursor cur = mContentResolver.query(SmartPlugContentDefine.SmartPlugIRScene.ALL_CONTENT_URI, 
    			                            null, 
				                            where, 
				                            null, 
				                            order);
    	
    	if (null != cur) {
    		while (cur.moveToNext()){
    			IRSceneDefine timer = new IRSceneDefine();
		        timer.mId = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.ID_COLUMN);
        		timer.mIRSceneId = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ID_COLUMN);
				timer.mPlugId = cur.getString(SmartPlugContentDefine.SmartPlugIRScene.PLUG_ID_COLUMN);
        		timer.mPower = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_POWER_COLUMN);
        		timer.mMode = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_MODE_COLUMN);
        		timer.mDirection = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_DIRECTION_COLUMN);
        		timer.mScale = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_SCALE_COLUMN);
        		timer.mTemperature = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_TEMPERATURE_COLUMN);
        		timer.mTime = cur.getString(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_TIME_COLUMN);   			
				timer.mPeriod = cur.getString(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_PERIOD_COLUMN);
				timer.mIRName = cur.getString(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_IRNAME_COLUMN);
				timer.mEnable = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ENABLE_COLUMN);
        		timers.add(timer);
    		}
    		cur.close();
    	}
		return timers;		
    }
    
    //������ж�ʱ����
    public ArrayList<IRSceneDefine> getAllIRScene(){
		ArrayList< IRSceneDefine > timers = new ArrayList< IRSceneDefine >();
		if (null == mContentResolver) {
			return null; 
		}
		String order = SmartPlugContentDefine.SmartPlugIRScene._ID  + " asc";
    	Cursor cur = mContentResolver.query(SmartPlugContentDefine.SmartPlugIRScene.ALL_CONTENT_URI, 
    			                            null, 
				                            null, 
				                            null, 
				                            order);
    	
    	
    	if (null != cur) {
    		while (cur.moveToNext()){
    			IRSceneDefine timer = new IRSceneDefine();
		        timer.mId = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.ID_COLUMN);
        		timer.mIRSceneId = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ID_COLUMN);
				timer.mPlugId = cur.getString(SmartPlugContentDefine.SmartPlugIRScene.PLUG_ID_COLUMN);
        		timer.mPower = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_POWER_COLUMN);
        		timer.mMode = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_MODE_COLUMN);
        		timer.mDirection = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_DIRECTION_COLUMN);
        		timer.mScale = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_SCALE_COLUMN);
        		timer.mTemperature = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_TEMPERATURE_COLUMN);
        		timer.mTime = cur.getString(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_TIME_COLUMN);   			
				timer.mPeriod = cur.getString(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_PERIOD_COLUMN);
				timer.mIRName = cur.getString(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_IRNAME_COLUMN);
				timer.mEnable = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ENABLE_COLUMN);
        		timers.add(timer);
    		}
    		cur.close();
    	}
		return timers;		
    }    
    
    //��ѯһ��timer
    public IRSceneDefine getIRScene(String plugId, int timerId){
    	IRSceneDefine timer = new IRSceneDefine();
		if (null == mContentResolver) {
			return null; 
		}    	
    	
		String where = SmartPlugContentDefine.SmartPlugIRScene.PLUG_ID + "=" + plugId 
				     + " AND " + SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ID + "='" + timerId + "'";
    	    	
    	Cursor cur = mContentResolver.query(SmartPlugContentDefine.SmartPlugIRScene.ALL_CONTENT_URI, 
                                            null, 
                                            where, 
                                            null, 
                                            null);
    	if (null != cur) {
        	while (cur.moveToNext()){
		        timer.mId = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.ID_COLUMN);
        		timer.mIRSceneId = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ID_COLUMN);
				timer.mPlugId = cur.getString(SmartPlugContentDefine.SmartPlugIRScene.PLUG_ID_COLUMN);
        		timer.mPower = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_POWER_COLUMN);
        		timer.mMode = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_MODE_COLUMN);
        		timer.mDirection = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_DIRECTION_COLUMN);
        		timer.mScale = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_SCALE_COLUMN);
        		timer.mTemperature = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_TEMPERATURE_COLUMN);
        		timer.mTime = cur.getString(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_TIME_COLUMN);   			
				timer.mPeriod = cur.getString(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_PERIOD_COLUMN);
				timer.mIRName = cur.getString(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_IRNAME_COLUMN);
				timer.mEnable = cur.getInt(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ENABLE_COLUMN);
        	}
        	cur.close();
        	return timer;         	
    	} else {
        	return null;     		
    	}
    }    
    //����һ����ʱ����
    public long addIRScene(final IRSceneDefine timer) {
		if (null == mContentResolver) {
			return 0; 
		} 
		if (null == timer) {
			return 0;
		}
			
    	ContentValues values = new ContentValues();
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ID, timer.mIRSceneId);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.PLUG_ID, timer.mPlugId);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_POWER, timer.mPower);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_MODE, timer.mMode);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_DIRECTION, timer.mDirection);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_SCALE, timer.mScale);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_TEMPERATURE, timer.mTemperature);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_TIME, timer.mTime);   			
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_PERIOD, timer.mPeriod);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_IRNAME, timer.mIRName);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ENABLE, timer.mEnable);
    	
    	Uri uri = mContentResolver.insert(SmartPlugContentDefine.SmartPlugIRScene.ALL_CONTENT_URI, values);
    	
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
    
    //ɾ��һ����ʱ����
    public boolean deleteIRScene(int timerId){
		if (null == mContentResolver) {
			return false; 
		}
    	String where = SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ID + "='" + timerId + "'"; 
    	int count = mContentResolver.delete(SmartPlugContentDefine.SmartPlugIRScene.ALL_CONTENT_URI, where, null);
    	return count > 0 ? true : false;
    } 
    
    //ɾ��һ�����������ж�ʱ����
    public boolean clearIRScene(String lpugId){
		if (null == mContentResolver) {
			return false; 
		}
    	String where = SmartPlugContentDefine.SmartPlugIRScene.PLUG_ID + "='" + lpugId + "'"; 
    	int count = mContentResolver.delete(SmartPlugContentDefine.SmartPlugIRScene.ALL_CONTENT_URI, where, null);
    	return count > 0 ? true : false;
    } 
        
    
    //�޸�һ����ʱ����
    public int modifyIRScene(IRSceneDefine timer){
		if (null == mContentResolver) {
			return -1; 
		} 
		if (null == timer) {
			return -1;
		}
			
    	ContentValues values = new ContentValues();
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ID, timer.mIRSceneId);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.PLUG_ID, timer.mPlugId);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_POWER, timer.mPower);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_MODE, timer.mMode);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_DIRECTION, timer.mDirection);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_SCALE, timer.mScale);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_TEMPERATURE, timer.mTemperature);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_TIME, timer.mTime);   			
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_PERIOD, timer.mPeriod);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_IRNAME, timer.mIRName);
    	values.put(SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ENABLE, timer.mEnable);
    	
    	String where = SmartPlugContentDefine.SmartPlugIRScene.PLUG_ID + "='" + timer.mPlugId + "' AND " 
    	               + SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ID + "='" + timer.mIRSceneId + "'";
    	int index = mContentResolver.update(SmartPlugContentDefine.SmartPlugIRScene.ALL_CONTENT_URI, 
    			                         values, 
    			                         where, 
    			                         null);
        return index;
    } 
    
    public int getMaxSceneId(String plugid) {
		if (null == mContentResolver) {
			return 0; 
		}
		int maxid = 0;
		String[] fields = {"max(" + SmartPlugContentDefine.SmartPlugIRScene.IRSCENE_ID+ ") as maxid"};
		String sWhere = SmartPlugContentDefine.SmartPlugIRScene.PLUG_ID + "='" + plugid + "'";
    	Cursor cur = mContentResolver.query(SmartPlugContentDefine.SmartPlugIRScene.ALL_CONTENT_URI, 
    										fields, 
    										sWhere, 
				                            null, 
				                            null);
    	
    	
    	if (null != cur) {
    		while (cur.moveToNext()){
    			maxid = cur.getInt(0);
    		}
    		cur.close();
    	}
    	maxid = (maxid < 100) ? 100 : maxid;
		return maxid + 1;
    }
	
	 	
}