package com.thingzdo.dataprovider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.ActionDefine;
import com.thingzdo.ui.common.PubFunc;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class ActionHelper {
	private Context mContext;
	private ContentResolver mActionResolver;
	private String TAG = "CallLogHelper";
	
	//private String DatetimeFormat = "yyyy-M-d H:mm:ss";
	
	public ActionHelper(Context context){
		mContext = context;
		if (mContext == null) {
			Log.e(TAG, "Context is empty");
		} else {
			mActionResolver = mContext.getContentResolver();
		}
	}
	
	private ArrayList<ActionDefine> getData(Cursor cur) {
		ArrayList< ActionDefine > actions = new ArrayList< ActionDefine >();
		ActionDefine action = null;
    	while (cur.moveToNext()){
    		action = new ActionDefine();
    		action.mID = cur.getInt(SmartPlugContentDefine.ControlAction.ID_COLUMN);
    		action.mActionCode =  cur.getInt(SmartPlugContentDefine.ControlAction.ACTION_CODE_COLUMN);
    		action.mActionString =  cur.getString(SmartPlugContentDefine.ControlAction.ACTION_STRING_COLUMN);
    		action.mReceiveType = cur.getInt(SmartPlugContentDefine.ControlAction.RECEIVE_TYPE_COLUMN);
    		action.mReceiveValue = cur.getString(SmartPlugContentDefine.ControlAction.RECEIVE_VALUE_COLUMN);
    		
    		SimpleDateFormat df = new SimpleDateFormat(mContext.getString(R.string.DateTime_db_format));
    		try {
				String timeString = cur.getString(SmartPlugContentDefine.ControlAction.ACTION_TIME_COLUMN); 
    			Date date = (Date) df.parse(timeString);
    			action.mActionTime = date;
			} catch (ParseException e) {
				e.printStackTrace();
			}
    		actions.add(action);
    	}
    	action = null;
		return actions;	
	}
	
	//���ݵ绰���ͣ�������е�ͨ����¼�����غ����б�
	//ͬһ����������ж���ͨ����¼������ֻ��ȡͬһ�������һ����¼��
	public ArrayList<ActionDefine> getAllActions(){
		ArrayList< ActionDefine > actions = new ArrayList< ActionDefine >();
		if (null == mActionResolver) {
			return actions; 
		}
		String where = "";
		String order = SmartPlugContentDefine.ControlAction.ACTION_TIME  + " desc";
    	Cursor cur = mActionResolver.query(SmartPlugContentDefine.ControlAction.ALL_CONTENT_URI, 
    			                            null, 
				                            where, 
				                            null, 
				                            order);
    	
    	actions = getData(cur);
    	cur.close();
    	return actions;
    }	
    
	//���ݵ绰���롢�������ͺͺ������ͣ����ͨ����¼��
    public ArrayList<ActionDefine> getAction(int id){
    	ArrayList<ActionDefine> actions = new ArrayList<ActionDefine>();
		if (null == mActionResolver) {
			return actions; 
		}    	
    	
		String where = SmartPlugContentDefine.ControlAction._ID + "='" + id + "'";
    	    	
    	Cursor cur = mActionResolver.query(SmartPlugContentDefine.ControlAction.ALL_CONTENT_URI, 
                                            null, 
                                            where, 
                                            null, 
                                            SmartPlugContentDefine.ControlAction.ACTION_TIME + " desc");
    	actions = getData(cur);
    	cur.close();
    	
    	return actions;   	   	
    }
    
    //����һ����¼
    public long addAction(ActionDefine action){
		if (null == mActionResolver) {
			return 0; 
		} 
		if (null == action) {
			return 0;
		}
			
    	ContentValues values = new ContentValues();
    	values.put(SmartPlugContentDefine.ControlAction.DIRECTION, action.mDirection);
    	values.put(SmartPlugContentDefine.ControlAction.ACTION_CODE, action.mActionCode);;
    	values.put(SmartPlugContentDefine.ControlAction.ACTION_STRING, action.mActionString);
    	SimpleDateFormat df = new SimpleDateFormat(mContext.getString(R.string.DateTime_db_format));
    	values.put(SmartPlugContentDefine.ControlAction.ACTION_TIME, df.format(action.mActionTime));
    	values.put(SmartPlugContentDefine.ControlAction.RECEIVE_TYPE, action.mReceiveType);
    	values.put(SmartPlugContentDefine.ControlAction.RECEIVE_VALUE, action.mReceiveValue);
    	
    	Uri uri = mActionResolver.insert(SmartPlugContentDefine.ControlAction.ALL_CONTENT_URI, values);
    	PubFunc.log("ActionHelper", "Insert a record");
    	
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
    
    //ɾ��ָ�����������ͨ����¼
    public boolean deleteAction(int id){
		if (null == mActionResolver) {
			return false; 
		}
    	String where = SmartPlugContentDefine.ControlAction._ID + "='" + id + "'"; 
    	int count = mActionResolver.delete(SmartPlugContentDefine.ControlAction.ALL_CONTENT_URI, where, null);
    	return count > 0 ? true : false;
    }
    
    //�������ͨ����¼
    public void clearActions(){
		if (null != mActionResolver) {
			PubFunc.log("ActionHelper", "Clear record");
			mActionResolver.delete(SmartPlugContentDefine.ControlAction.ALL_CONTENT_URI, null, null); 
		}
    }
}