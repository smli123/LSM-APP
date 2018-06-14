package com.thingzdo.smartplug.ui.share;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.TitledActivity;

public class SelectSharePlugWizardActivity extends TitledActivity {
	private SmartPlugHelper mPlugHelper = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String user = intent.getStringExtra("USER");
		
		ArrayList<SmartPlugDefine>  plugs = mPlugHelper.getAllSmartPlug(user);
	}
	
	
	private class UserlistAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
}
