package com.thingzdo.dataprovider;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;

public class SmartPlugHelper {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "SmartPlugContentHelper";

	public SmartPlugHelper(Context context) {
		mContext = context;
		if (mContext == null) {
			Log.e(TAG, "Context is empty");
		} else {
			mContentResolver = mContext.getContentResolver();
		}
	}

	// 获得数据
	private ArrayList<SmartPlugDefine> getData(Cursor cur) {
		ArrayList<SmartPlugDefine> plugs = new ArrayList<SmartPlugDefine>();
		while (cur.moveToNext()) {
			SmartPlugDefine plug = new SmartPlugDefine();
			plug.mID = cur
					.getInt(SmartPlugContentDefine.SmartPlugContent.ID_COLUMN);
			plug.mPlugId = cur
					.getString(SmartPlugContentDefine.SmartPlugContent.PLUG_ID_COLUMN);
			plug.mUserName = cur
					.getString(SmartPlugContentDefine.SmartPlugContent.USER_NAME_COLUMN);
			plug.mPlugName = cur
					.getString(SmartPlugContentDefine.SmartPlugContent.PLUG_NAME_COLUMN);
			plug.mIPAddress = cur
					.getString(SmartPlugContentDefine.SmartPlugContent.PLUG_IP_COLUMN);
			plug.mIsOnline = cur
					.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_ONLINE_COLUMN) == 1
					? true
					: false;
			plug.mDeviceStatus = cur
					.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_STATUS_COLUMN);
			plug.mFlashMode = cur
					.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_FLASHMODE_COLUMN);
			plug.mColor_R = cur
					.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_R_COLUMN);
			plug.mColor_G = cur
					.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_G_COLUMN);
			plug.mColor_B = cur
					.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_B_COLUMN);
			plug.mProtocolMode = cur
					.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_PROTOCOLMODE_COLUMN);
			plug.mVersion = cur
					.getString(SmartPlugContentDefine.SmartPlugContent.PLUG_VERSION_COLUMN);
			plug.mMAC = cur
					.getString(SmartPlugContentDefine.SmartPlugContent.PLUG_MAC_COLUMN);
			plug.mModuleType = cur
					.getString(SmartPlugContentDefine.SmartPlugContent.PLUG_TYPE_COLUMN);
			plug.mPosition = cur
					.getInt(SmartPlugContentDefine.SmartPlugContent.CURTAIN_POSITION_COLUMN);

			plug.mSubDeviceType = PubFunc
					.getDeviceTypeFromModuleType(plug.mModuleType);
			plug.mSubProductType = PubFunc
					.getProductTypeFromModuleType(plug.mModuleType);

			plugs.add(plug);
		}
		// plug = null;
		return plugs;
	}

	// 获得指定用户名下所有插座
	public ArrayList<SmartPlugDefine> getAllSmartPlug(String user) {
		ArrayList<SmartPlugDefine> plugs = new ArrayList<SmartPlugDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String where = SmartPlugContentDefine.SmartPlugContent.USER_NAME
				+ " = '" + user + "'";
		String order = SmartPlugContentDefine.SmartPlugContent._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI, null,
				where, null, order);

		if (null != cur) {
			plugs = getData(cur);
			cur.close();
		}

		// return plugs;

		// 获取所有的智能插座，不包括 模拟PC
		ArrayList<SmartPlugDefine> newPlugs = new ArrayList<SmartPlugDefine>();
		for (SmartPlugDefine plug : plugs) {
			if (plug.mSubDeviceType
					.equals(PubDefine.DEVICE_SMART_SIMULATION_PC) == false) {
				newPlugs.add(plug);
			}
		}

		return newPlugs;
	}

	// 获得所有插座
	public String getSmartPlugMacShowName(String plugID) {
		String str_no = "";
		int location = plugID.indexOf("A");
		if (location != -1) {
			str_no = plugID.substring(location);
		}

		return str_no;
	}

	// 获得所有插座
	public ArrayList<SmartPlugDefine> getAllSmartPlug() {
		ArrayList<SmartPlugDefine> plugs = new ArrayList<SmartPlugDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String order = SmartPlugContentDefine.SmartPlugContent._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI, null,
				null, null, order);

		if (null != cur) {
			plugs = getData(cur);
			cur.close();
		}
		// SmartPlugDefine smartplug = new SmartPlugDefine();
		// smartplug.mPlugName = "";
		// smartplug.mPlugId = "";
		// plugs.add(smartplug);

		return plugs;
	}

	// 获得所有插座
	public ArrayList<String> getAllSmartPlugMacID(String plugID) {
		ArrayList<SmartPlugDefine> plugs = new ArrayList<SmartPlugDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String order = SmartPlugContentDefine.SmartPlugContent._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI, null,
				null, null, order);

		if (null != cur) {
			plugs = getData(cur);
			cur.close();
		}

		ArrayList<String> macs = new ArrayList<String>();
		for (SmartPlugDefine plug : plugs) {
			if (plug.mPlugId.equals(plugID)) {
				continue;
			}
			if (plug.mPlugId.contains(plugID)) {
				macs.add(plug.mPlugId);
			}
		}
		return macs;
	}

	// 获得所有插座
	public ArrayList<SmartPlugDefine> getAllSmartPlugPCs(String plugID) {
		ArrayList<SmartPlugDefine> plugs = new ArrayList<SmartPlugDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String order = SmartPlugContentDefine.SmartPlugContent._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI, null,
				null, null, order);

		if (null != cur) {
			plugs = getData(cur);
			cur.close();
		}

		ArrayList<SmartPlugDefine> macs = new ArrayList<SmartPlugDefine>();
		for (SmartPlugDefine plug : plugs) {
			if (plug.mPlugId.equals(plugID)) {
				continue;
			}
			if (plug.mPlugId.contains(plugID)) {
				macs.add(plug);
			}
		}
		return macs;
	}

	// 获得所有插座
	public ArrayList<String> getAllSmartPlugMac(String plugID) {
		ArrayList<SmartPlugDefine> plugs = new ArrayList<SmartPlugDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String order = SmartPlugContentDefine.SmartPlugContent._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI, null,
				null, null, order);

		if (null != cur) {
			plugs = getData(cur);
			cur.close();
		}

		ArrayList<String> macs = new ArrayList<String>();
		for (SmartPlugDefine plug : plugs) {
			if (plug.mPlugId.equals(plugID)) {
				continue;
			}
			if (plug.mPlugId.contains(plugID)) {
				macs.add(plug.mMAC);
			}
		}
		return macs;
	}

	// 获得所有插座
	public ArrayList<String> getAllSmartPlugMacShow(String plugID) {
		ArrayList<SmartPlugDefine> plugs = new ArrayList<SmartPlugDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String order = SmartPlugContentDefine.SmartPlugContent._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI, null,
				null, null, order);

		if (null != cur) {
			plugs = getData(cur);
			cur.close();
		}

		ArrayList<String> macs = new ArrayList<String>();
		for (SmartPlugDefine plug : plugs) {
			if (plug.mPlugId.equals(plugID)) {
				continue;
			}
			if (plug.mPlugId.contains(plugID)) {
				String str_no = "";
				int location = plug.mPlugId.indexOf("A");
				if (location != -1) {
					str_no = plug.mPlugId.substring(location) + " ";
				}
				macs.add(str_no + plug.mMAC);
			}
		}
		return macs;
	}

	// 获得所有插座
	public ArrayList<String> getAllSmartPlugPCID(String plugID) {
		ArrayList<SmartPlugDefine> plugs = new ArrayList<SmartPlugDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String order = SmartPlugContentDefine.SmartPlugContent._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI, null,
				null, null, order);

		if (null != cur) {
			plugs = getData(cur);
			cur.close();
		}

		ArrayList<String> pcIDs = new ArrayList<String>();
		for (SmartPlugDefine plug : plugs) {
			if (plug.mPlugId.equals(plugID)) {
				continue;
			}
			if (plug.mPlugId.contains(plugID)) {
				pcIDs.add(plug.mPlugId);
			}
		}
		return pcIDs;
	}

	// 根据Mac地址找SmartPlug ID
	public String getPlugIDFromMac(String mac) {
		ArrayList<SmartPlugDefine> plugs = new ArrayList<SmartPlugDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String order = SmartPlugContentDefine.SmartPlugContent._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI, null,
				null, null, order);

		if (null != cur) {
			plugs = getData(cur);
			cur.close();
		}

		for (SmartPlugDefine plug : plugs) {
			if (plug.mMAC.equals(mac)) {
				return plug.mPlugId;
			}
		}
		return null;
	}

	// 获得指定插座信息
	public SmartPlugDefine getSmartPlug(String id) {
		SmartPlugDefine plug = null;
		if (null == mContentResolver) {
			return null;
		}

		String where = SmartPlugContentDefine.SmartPlugContent.PLUG_ID + "='"
				+ id + "'";

		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI, null,
				where, null, null);
		if (null != cur) {
			while (cur.moveToNext()) {
				plug = new SmartPlugDefine();
				plug.mID = cur
						.getInt(SmartPlugContentDefine.SmartPlugContent.ID_COLUMN);
				plug.mPlugId = cur
						.getString(SmartPlugContentDefine.SmartPlugContent.PLUG_ID_COLUMN);
				plug.mUserName = cur
						.getString(SmartPlugContentDefine.SmartPlugContent.USER_NAME_COLUMN);
				plug.mPlugName = cur
						.getString(SmartPlugContentDefine.SmartPlugContent.PLUG_NAME_COLUMN);
				plug.mIPAddress = cur
						.getString(SmartPlugContentDefine.SmartPlugContent.PLUG_IP_COLUMN);
				plug.mIsOnline = cur
						.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_ONLINE_COLUMN) == 1
						? true
						: false;
				plug.mDeviceStatus = cur
						.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_STATUS_COLUMN);
				plug.mFlashMode = cur
						.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_FLASHMODE_COLUMN);
				plug.mColor_R = cur
						.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_R_COLUMN);
				plug.mColor_G = cur
						.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_G_COLUMN);
				plug.mColor_B = cur
						.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_B_COLUMN);
				plug.mProtocolMode = cur
						.getInt(SmartPlugContentDefine.SmartPlugContent.PLUG_PROTOCOLMODE_COLUMN);
				plug.mVersion = cur
						.getString(SmartPlugContentDefine.SmartPlugContent.PLUG_VERSION_COLUMN);
				plug.mMAC = cur
						.getString(SmartPlugContentDefine.SmartPlugContent.PLUG_MAC_COLUMN);
				plug.mModuleType = cur
						.getString(SmartPlugContentDefine.SmartPlugContent.PLUG_TYPE_COLUMN);
				plug.mPosition = cur
						.getInt(SmartPlugContentDefine.SmartPlugContent.CURTAIN_POSITION_COLUMN);

				plug.mSubDeviceType = PubFunc
						.getDeviceTypeFromModuleType(plug.mModuleType);
				plug.mSubProductType = PubFunc
						.getProductTypeFromModuleType(plug.mModuleType);

				break;
			}
			cur.close();
			return plug;
		} else {
			return null;
		}
	}

	// 增加新插座
	public long addSmartPlug(SmartPlugDefine plug) {
		if (null == mContentResolver) {
			return 0;
		}
		if (null == plug) {
			return 0;
		}

		ContentValues values = new ContentValues();
		values.put(SmartPlugContentDefine.SmartPlugContent.USER_NAME,
				plug.mUserName);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_ID,
				plug.mPlugId);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_NAME,
				plug.mPlugName);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_IP,
				plug.mIPAddress);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_ONLINE,
				plug.mIsOnline);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_STATUS,
				plug.mDeviceStatus);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_FLASHMODE,
				plug.mFlashMode);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_R,
				plug.mColor_R);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_G,
				plug.mColor_G);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_B,
				plug.mColor_B);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_PROTOCOLMODE,
				plug.mProtocolMode);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_VERSION,
				plug.mVersion);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_MAC, plug.mMAC);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_TYPE,
				plug.mModuleType);
		values.put(SmartPlugContentDefine.SmartPlugContent.CURTAIN_POSITION,
				plug.mPosition);

		Uri uri = mContentResolver
				.insert(SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI,
						values);
		PubFunc.log(TAG, "Insert a record");

		if (null == uri) {
			return 0;
		}
		try {
			long id = ContentUris.parseId(uri);
			return id;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return 0;
		}
	}

	// 删除指定的插座
	public boolean deleteSmartPlug(String id) {
		if (null == mContentResolver) {
			return false;
		}
		String where = SmartPlugContentDefine.SmartPlugContent.PLUG_ID + "='"
				+ id + "'";
		int count = mContentResolver.delete(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI, where,
				null);
		return count > 0 ? true : false;
	}

	// 删除所有插座
	public void clearSmartPlug() {
		if (null != mContentResolver) {
			PubFunc.log(TAG, "Clear call plug");
			mContentResolver.delete(
					SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI,
					null, null);
		}
	}

	// 修改插座
	public int modifySmartPlug(SmartPlugDefine plug) {
		if (null == mContentResolver) {
			return -1;
		}
		if (null == plug) {
			return -1;
		}

		ContentValues values = new ContentValues();
		values.put(SmartPlugContentDefine.SmartPlugContent.USER_NAME,
				plug.mUserName);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_ID,
				plug.mPlugId);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_NAME,
				plug.mPlugName);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_IP,
				plug.mIPAddress);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_ONLINE,
				plug.mIsOnline);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_STATUS,
				plug.mDeviceStatus);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_FLASHMODE,
				plug.mFlashMode);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_R,
				plug.mColor_R);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_G,
				plug.mColor_G);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_B,
				plug.mColor_B);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_PROTOCOLMODE,
				plug.mProtocolMode);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_VERSION,
				plug.mVersion);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_MAC, plug.mMAC);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_TYPE,
				plug.mModuleType);
		values.put(SmartPlugContentDefine.SmartPlugContent.CURTAIN_POSITION,
				plug.mPosition);

		String where = SmartPlugContentDefine.SmartPlugContent.PLUG_ID + "='"
				+ plug.mPlugId + "'";
		int index = mContentResolver.update(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI,
				values, where, null);
		PubFunc.log(TAG, "Update a plug");
		return index;
	}

	// 判断用户名下的指定ID的插座是否存在
	public boolean isPlugExists(final String username, final String plugname) {
		if (null == mContentResolver) {
			Log.e(TAG, "mContentResolver=null");
			return true;
		}

		String where = SmartPlugContentDefine.SmartPlugContent.USER_NAME + "='"
				+ username + "' AND "
				+ SmartPlugContentDefine.SmartPlugContent.PLUG_NAME + "='"
				+ plugname + "'";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI, null,
				where, null, null);
		if (null == cur || 0 == cur.getCount()) {
			cur.close();
			return false;
		}

		cur.close();
		return true;
	}

	public void setAllPlugsOffline() {
		if (null == mContentResolver) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_ONLINE, false);
		// values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_POWERON,
		// false);
		// values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_LIGHTON,
		// false);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_STATUS, 0);
		values.put(SmartPlugContentDefine.SmartPlugContent.CURTAIN_POSITION, 0);

		mContentResolver.update(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI,
				values, null, null);
		PubFunc.log(TAG, "Update setAllPlugsOffline");
	}

	public int modifySmartPlugColor(String id, int flashmode, int red,
			int green, int blue) {
		if (null == mContentResolver) {
			return -1;
		}

		ContentValues values = new ContentValues();
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_FLASHMODE,
				flashmode);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_R, red);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_G, green);
		values.put(SmartPlugContentDefine.SmartPlugContent.PLUG_COLOR_B, blue);

		String where = SmartPlugContentDefine.SmartPlugContent.PLUG_ID + "='"
				+ id + "'";
		int index = mContentResolver.update(
				SmartPlugContentDefine.SmartPlugContent.ALL_CONTENT_URI,
				values, where, null);
		PubFunc.log(TAG, "Update a plug color");
		return index;
	}
}