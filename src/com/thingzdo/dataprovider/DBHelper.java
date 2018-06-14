package com.thingzdo.dataprovider;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper  extends SQLiteOpenHelper{
	private static DBHelper mInstance = null;
	
	public static DBHelper getInstance(Context context){
		if (null == mInstance){
			mInstance = new DBHelper(context);	
		}
		return 	mInstance;
	}
	
	ArrayList<String> mTableList = new ArrayList<String>();
	//�������ݿ�
	private DBHelper(Context context) {
		super(context, SmartPlugDbDefine.g_CarGuardDbName, null, SmartPlugDbDefine.DATABASE_VERSION);
		//��Ҫ�����SQL�ű������б�
		mTableList.add(SmartPlugContentDefine.ControlAction.TABLE_NAME);
		mTableList.add(SmartPlugContentDefine.SmartPlugContent.TABLE_NAME);
		
//		mTableList.add(SmartPlugContentDefine.User.TABLE_NAME);
//		mTableList.add(SmartPlugContentDefine.SmartPlugTimer.TABLE_NAME);
//		mTableList.add(SmartPlugContentDefine.SmartPlugIRScene.TABLE_NAME);
//		mTableList.add(SmartPlugContentDefine.SmartPlugContent.TABLE_NAME);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (null == db) {
			return;
		}
		
		db.execSQL(SmartPlugContentDefine.ControlAction.TABLE_CREATE);
		
		db.execSQL(SmartPlugContentDefine.SmartPlugContent.TABLE_CREATE);	
		
		db.execSQL(SmartPlugContentDefine.User.TABLE_CREATE);
		
		db.execSQL(SmartPlugContentDefine.SmartPlugTimer.TABLE_CREATE);
		
		db.execSQL(SmartPlugContentDefine.SmartPlugIRScene.TABLE_CREATE);
		
		db.execSQL(SmartPlugContentDefine.User.SQL_TRIGER_USER_DELETE);
		db.execSQL(SmartPlugContentDefine.SmartPlugContent.SQL_TRIGER_TIMER_DELETE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (null == db) {
			return;
		}
		
		for (int i = 0; i < mTableList.size(); i++){
    		db.execSQL("DROP TABLE IF EXISTS " + mTableList.get(i));
		}	
		onCreate(db);
	}
}
