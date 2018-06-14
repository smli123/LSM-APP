package com.thingzdo.dataprovider;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class SmartPlugProvider extends ContentProvider {
	private static final String TAG= "SmartPlugContentProvider"; 

	private DBHelper dbHelper;
	private SQLiteDatabase gotaDB;
	private Context mContenxt; 
	
	public static final int PLUG_ACTIONS         = 1;
	public static final int PLUG_ACTION          = 2;
	
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;	
	static{
		uriMatcher.addURI(SmartPlugContentDefine.SmartPlugContent.AUTHORITY, SmartPlugContentDefine.SmartPlugContent.ALL_RECORD, PLUG_ACTIONS);
		uriMatcher.addURI(SmartPlugContentDefine.SmartPlugContent.AUTHORITY, SmartPlugContentDefine.SmartPlugContent.ONE_RECORD + "/#", PLUG_ACTION);
	}	
	
	
	
	@Override
	public int delete(Uri uri, String where, String[] selectionArgs) {
		int count = 0;
		gotaDB = DBHelper.getInstance(getContext()).getWritableDatabase();
		if (null == gotaDB) {
			return 0;
		}		
		
		String carID;
		switch (uriMatcher.match(uri)) {
			case PLUG_ACTIONS:
				count = gotaDB.delete(SmartPlugContentDefine.SmartPlugContent.TABLE_NAME,  where, selectionArgs);
				break;
			case PLUG_ACTION:
				carID = uri.getPathSegments().get(1);
				count = gotaDB.delete(SmartPlugContentDefine.SmartPlugContent.TABLE_NAME, 
						SmartPlugContentDefine.SmartPlugContent._ID + "=" + carID
						+ (!TextUtils.isEmpty(where) ? " AND ("+ where + ")" : ""),
						selectionArgs);
				break;
			default: throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		mContenxt.getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
	    case PLUG_ACTIONS:
	        return SmartPlugDbDefine.CONTENT_TYPE;
	    case PLUG_ACTION:
	    	return SmartPlugDbDefine.CONTENT_ITEM_TYPE;
	    default:
	        throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (uriMatcher.match(uri) != PLUG_ACTIONS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		gotaDB = DBHelper.getInstance(getContext()).getWritableDatabase();
		if (null == gotaDB) {
			return null;
		}		
		
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		
		String tableName = "";
		switch (uriMatcher.match(uri)) {
		    case PLUG_ACTIONS:
		    	tableName = SmartPlugContentDefine.SmartPlugContent.TABLE_NAME; 
			    break;
		    default: 
			    break;		
		}
		try{
		    long rowId = gotaDB.insert(tableName, SmartPlugContentDefine.SmartPlugContent._ID, values);
		    if (rowId > 0) {
			    Uri carInfoUri = ContentUris.withAppendedId(uri, rowId);
			    ContentResolver resovler = mContenxt.getContentResolver();
			    resovler.notifyChange(carInfoUri, null);
			    return carInfoUri;
		    } 
		    return null;
		} catch(Exception e) {
			Log.e( TAG + "insert", e.getMessage());	
			return null;
		}
		
	}

	@Override
	public boolean onCreate() {
		mContenxt = getContext();
		
		dbHelper = DBHelper.getInstance(getContext());
		gotaDB = dbHelper.getWritableDatabase();
		return (gotaDB == null)? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String orderBy = null;
		Cursor cursor = null;
		String groupBy = null;
		gotaDB = DBHelper.getInstance(getContext()).getReadableDatabase();
		if (null == gotaDB) {
			return null;
		}
		
		long id;
		String where;
		switch (uriMatcher.match(uri)) {
			case PLUG_ACTIONS:
				cursor = gotaDB.query(true, 
						              SmartPlugContentDefine.SmartPlugContent.TABLE_NAME.toLowerCase(), 
						              projection, 
						              selection, 
						              selectionArgs, 
						              groupBy,
						              null, 
						              sortOrder, 
						              null);
				break;
			case PLUG_ACTION:
				id = ContentUris.parseId(uri);
				where = SmartPlugContentDefine.SmartPlugContent._ID + "=" + id;
				if (null != selection && !"".equals(selection)){
					where = where + " and " + selection;
				}
							
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = SmartPlugContentDefine.SmartPlugContent._ID;
				} else {
					orderBy = sortOrder;
				}
				
				cursor = gotaDB.query(SmartPlugContentDefine.SmartPlugContent.TABLE_NAME.toLowerCase(), 
						              projection, 
						              where, 
						              selectionArgs, 
						              null, 
						              null, 
						              orderBy);
				
				break;
			default: 
				break;
		}
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] selectionArgs) {
		int count;
		gotaDB = DBHelper.getInstance(getContext()).getWritableDatabase();
		if (null == gotaDB) {
			return 0;
		}		
		
		String carID;
		switch (uriMatcher.match(uri)) {
			case PLUG_ACTIONS:
				count = gotaDB.update(SmartPlugContentDefine.SmartPlugContent.TABLE_NAME, values, where, selectionArgs);
				break;
			case PLUG_ACTION:
				carID = uri.getPathSegments().get(1);
				count = gotaDB.update(SmartPlugContentDefine.SmartPlugContent.TABLE_NAME, values,
						SmartPlugContentDefine.SmartPlugContent._ID + "=" + carID
						+ (!TextUtils.isEmpty(where) ? " AND ("+ where + ")" : ""), 
						selectionArgs);
				break;
			default: 
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		mContenxt.getContentResolver().notifyChange(uri, null);
		return count;
	}

}
