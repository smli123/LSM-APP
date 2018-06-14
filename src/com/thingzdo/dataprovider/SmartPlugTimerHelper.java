package com.thingzdo.dataprovider;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.thingzdo.ui.TimerDefine;

public class SmartPlugTimerHelper {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "UserHelper";
	
	public SmartPlugTimerHelper(Context context){
		mContext = context;
		if (mContext == null) {
			Log.e(TAG, "Context is empty");
		} else {
			mContentResolver = mContext.getContentResolver();
		}
	}
	
    //���ָ�����������ж�ʱ����
    public ArrayList<TimerDefine> getAllTimer(String plugId){
		ArrayList< TimerDefine > timers = new ArrayList< TimerDefine >();
		if (null == mContentResolver) {
			return null; 
		}
		String where = SmartPlugContentDefine.SmartPlugTimer.PLUG_ID + " = '" + plugId + "'";
		String order = SmartPlugContentDefine.SmartPlugTimer._ID  + " asc";
    	Cursor cur = mContentResolver.query(SmartPlugContentDefine.SmartPlugTimer.ALL_CONTENT_URI, 
    			                            null, 
				                            where, 
				                            null, 
				                            order);
    	
    	
    	if (null != cur) {
    		while (cur.moveToNext()){
    			TimerDefine timer = new TimerDefine();
        		timer.mId = cur.getInt(SmartPlugContentDefine.SmartPlugTimer.ID_COLUMN);
        		timer.mTimerId = cur.getInt(SmartPlugContentDefine.SmartPlugTimer.TIMER_ID_COLUMN);
        		timer.mPlugId = cur.getString(SmartPlugContentDefine.SmartPlugTimer.PLUG_ID_COLUMN);
        		timer.mType = cur.getInt(SmartPlugContentDefine.SmartPlugTimer.TIMER_TYPE_COLUMN);
        		timer.mEnable = cur.getInt(SmartPlugContentDefine.SmartPlugTimer.PLUG_TIMER_ENABLE_COLUMN) == 1 ? true : false;
        		timer.mPeriod = cur.getString(SmartPlugContentDefine.SmartPlugTimer.PLUG_PERIOD_COLUMN);
        		timer.mPowerOnTime = cur.getString(SmartPlugContentDefine.SmartPlugTimer.PLUG_BEGINTIME_COLUMN);
        		timer.mPowerOffTime = cur.getString(SmartPlugContentDefine.SmartPlugTimer.PLUG_ENDTIME_COLUMN);    			
        		timers.add(timer);
    		}
    		cur.close();
    	}
		return timers;		
    }
    
    //������ж�ʱ����
    public ArrayList<TimerDefine> getAllTimer(){
		ArrayList< TimerDefine > timers = new ArrayList< TimerDefine >();
		if (null == mContentResolver) {
			return null; 
		}
		String order = SmartPlugContentDefine.SmartPlugTimer._ID  + " asc";
    	Cursor cur = mContentResolver.query(SmartPlugContentDefine.SmartPlugTimer.ALL_CONTENT_URI, 
    			                            null, 
				                            null, 
				                            null, 
				                            order);
    	
    	
    	if (null != cur) {
    		while (cur.moveToNext()){
    			TimerDefine timer = new TimerDefine();
        		timer.mId = cur.getInt(SmartPlugContentDefine.SmartPlugTimer.ID_COLUMN);
        		timer.mTimerId = cur.getInt(SmartPlugContentDefine.SmartPlugTimer.TIMER_ID_COLUMN);
        		timer.mPlugId = cur.getString(SmartPlugContentDefine.SmartPlugTimer.PLUG_ID_COLUMN);
        		timer.mType = cur.getInt(SmartPlugContentDefine.SmartPlugTimer.TIMER_TYPE_COLUMN);
        		timer.mEnable = cur.getInt(SmartPlugContentDefine.SmartPlugTimer.PLUG_TIMER_ENABLE_COLUMN) == 1 ? true : false;
        		timer.mPeriod = cur.getString(SmartPlugContentDefine.SmartPlugTimer.PLUG_PERIOD_COLUMN);
        		timer.mPowerOnTime = cur.getString(SmartPlugContentDefine.SmartPlugTimer.PLUG_BEGINTIME_COLUMN);
        		timer.mPowerOffTime = cur.getString(SmartPlugContentDefine.SmartPlugTimer.PLUG_ENDTIME_COLUMN);    			
        		timers.add(timer);
    		}
    		cur.close();
    	}
		return timers;		
    }    
    
    //��ѯһ��timer
    public TimerDefine getTimer(String plugId, int timerId){
    	TimerDefine timer = new TimerDefine();
		if (null == mContentResolver) {
			return null; 
		}    	
		
		String where = SmartPlugContentDefine.SmartPlugTimer.PLUG_ID + " = '" + plugId + "'"
				     + " AND " + SmartPlugContentDefine.SmartPlugTimer.TIMER_ID + " = '" + timerId + "'";
		String order = SmartPlugContentDefine.SmartPlugTimer._ID  + " asc";
    	Cursor cur = mContentResolver.query(SmartPlugContentDefine.SmartPlugTimer.ALL_CONTENT_URI, 
                                            null, 
                                            where, 
                                            null, 
                                            order);
    	if (null != cur) {
        	while (cur.moveToNext()){
        		timer.mId = cur.getInt(SmartPlugContentDefine.SmartPlugTimer.ID_COLUMN);
        		timer.mTimerId = cur.getInt(SmartPlugContentDefine.SmartPlugTimer.TIMER_ID_COLUMN);
        		timer.mPlugId = cur.getString(SmartPlugContentDefine.SmartPlugTimer.PLUG_ID_COLUMN);
        		timer.mType = cur.getInt(SmartPlugContentDefine.SmartPlugTimer.TIMER_TYPE_COLUMN);
        		timer.mEnable = cur.getInt(SmartPlugContentDefine.SmartPlugTimer.PLUG_TIMER_ENABLE_COLUMN) == 1 ? true : false;
        		timer.mPeriod = cur.getString(SmartPlugContentDefine.SmartPlugTimer.PLUG_PERIOD_COLUMN);
        		timer.mPowerOnTime = cur.getString(SmartPlugContentDefine.SmartPlugTimer.PLUG_BEGINTIME_COLUMN);
        		timer.mPowerOffTime = cur.getString(SmartPlugContentDefine.SmartPlugTimer.PLUG_ENDTIME_COLUMN);
        	}
        	cur.close();
        	return timer;         	
    	} else {
        	return null;     		
    	}
    }    
    //����һ����ʱ����
    public long addTimer(final TimerDefine timer) {
		if (null == mContentResolver) {
			return 0; 
		} 
		if (null == timer) {
			return 0;
		}
			
    	ContentValues values = new ContentValues();
    	values.put(SmartPlugContentDefine.SmartPlugTimer.TIMER_ID, timer.mTimerId);
    	values.put(SmartPlugContentDefine.SmartPlugTimer.PLUG_ID, timer.mPlugId);
    	values.put(SmartPlugContentDefine.SmartPlugTimer.PLUG_TIMER_TYPE, timer.mType);
    	values.put(SmartPlugContentDefine.SmartPlugTimer.PLUG_TIMER_ENABLE, String.valueOf(timer.mEnable == true ? 1 : false));
    	values.put(SmartPlugContentDefine.SmartPlugTimer.PLUG_PERIOD, timer.mPeriod);
    	values.put(SmartPlugContentDefine.SmartPlugTimer.PLUG_BEGINTIME, timer.mPowerOnTime);
    	values.put(SmartPlugContentDefine.SmartPlugTimer.PLUG_ENDTIME, timer.mPowerOffTime);
    	
    	Uri uri = mContentResolver.insert(SmartPlugContentDefine.SmartPlugTimer.ALL_CONTENT_URI, values);
    	
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
    public boolean deleteTimer(int timerId){
		if (null == mContentResolver) {
			return false; 
		}
    	String where = SmartPlugContentDefine.SmartPlugTimer._ID + "='" + timerId + "'"; 
    	int count = mContentResolver.delete(SmartPlugContentDefine.SmartPlugTimer.ALL_CONTENT_URI, where, null);
    	return count > 0 ? true : false;
    } 
    
    //ɾ��һ�����������ж�ʱ����
    public boolean clearTimer(String lpugId){
		if (null == mContentResolver) {
			return false; 
		}
    	String where = SmartPlugContentDefine.SmartPlugTimer.PLUG_ID + "='" + lpugId + "'"; 
    	int count = mContentResolver.delete(SmartPlugContentDefine.SmartPlugTimer.ALL_CONTENT_URI, where, null);
    	return count > 0 ? true : false;
    } 
        
    
    //�޸�һ����ʱ����
    public int modifyTimer(TimerDefine timer){
		if (null == mContentResolver) {
			return -1; 
		} 
		if (null == timer) {
			return -1;
		}
			
    	ContentValues values = new ContentValues();
    	values.put(SmartPlugContentDefine.SmartPlugTimer.PLUG_TIMER_TYPE, timer.mType);
    	values.put(SmartPlugContentDefine.SmartPlugTimer.PLUG_TIMER_ENABLE, timer.mEnable);
    	values.put(SmartPlugContentDefine.SmartPlugTimer.PLUG_PERIOD, timer.mPeriod);
    	values.put(SmartPlugContentDefine.SmartPlugTimer.PLUG_BEGINTIME, timer.mPowerOnTime);
    	values.put(SmartPlugContentDefine.SmartPlugTimer.PLUG_ENDTIME, timer.mPowerOffTime);
    	
    	String where = SmartPlugContentDefine.SmartPlugTimer.PLUG_ID + "='" + timer.mPlugId + "' AND " 
    	               + SmartPlugContentDefine.SmartPlugTimer.TIMER_ID + "='" + timer.mTimerId + "'";
    	int index = mContentResolver.update(SmartPlugContentDefine.SmartPlugTimer.ALL_CONTENT_URI, 
    			                         values, 
    			                         where, 
    			                         null);
        return index;
    } 
    
    public int getMaxTimerId(String plugid) {
		if (null == mContentResolver) {
			return 0; 
		}
		int maxid = 0;
		String[] fields = {"max(" + SmartPlugContentDefine.SmartPlugTimer.TIMER_ID + ") as maxid"};
		String sWhere = SmartPlugContentDefine.SmartPlugTimer.PLUG_ID + "='" + plugid + "'";
    	Cursor cur = mContentResolver.query(SmartPlugContentDefine.SmartPlugTimer.ALL_CONTENT_URI, 
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
		return maxid;    	
    }
    
    public int getMaxOpenPCTimerId(String plugid) {
		if (null == mContentResolver) {
			return 0; 
		}
		int maxid = 0;
		String[] fields = {"max(" + SmartPlugContentDefine.SmartPlugTimer.TIMER_ID + ") as maxid"};
		String sWhere = SmartPlugContentDefine.SmartPlugTimer.PLUG_ID + "='" + plugid + "' and TimerId >= 400";
    	Cursor cur = mContentResolver.query(SmartPlugContentDefine.SmartPlugTimer.ALL_CONTENT_URI, 
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
		return maxid;    	
    }
	 	
}