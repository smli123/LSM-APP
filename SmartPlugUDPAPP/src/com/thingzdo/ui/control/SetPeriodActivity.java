package com.thingzdo.ui.control;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.util.ThingzdoCheckBox;

public class SetPeriodActivity extends TitledActivity 
	implements OnClickListener {
	
	private Context mContext = null;
	private String[] mDays = null;
	private String[] mSelDays = null;
	private ListView mDayList  = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_set_period, false);
		//SmartPlugApplication.getInstance().addActivity(this);
		SmartPlugApplication.resetTask();
		
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		setTitle(R.string.timer_task_timeset_err);
		mContext = this;
		mDayList  = (ListView)findViewById(R.id.week_list);
		
		Intent intent = getIntent();
		mSelDays = intent.getStringArrayExtra("selected_days");
		
		initView();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:
			updateSelecteDays(getPeriod());
			Intent intent = new Intent();
			intent.putExtra("selected_days", mSelDays);
			setResult(0, intent);
			finish();
			break;
		}
	}
	
	private void updateSelecteDays(String days) {
		for (int i = 0; i < 7; i++) {
			mSelDays[i] = days.substring(i, i+1);			
		}		
	}
	
	private String getPeriod() {
		StringBuffer sb = new StringBuffer();
		int count = mDayList.getCount();
		for (int i = 0; i < count; i++){  
			View view = mDayList.getChildAt(i);
			if (null == view) {
				sb.append("0");
				continue;
			}
		    ThingzdoCheckBox cb = (ThingzdoCheckBox)view.findViewById(R.id.img_days); 
		    sb.append(cb.isChecked() ? "1" : "0");
		}
		
		return sb.toString();
	}	
	
	private void initView() {
		mDays = getResources().getStringArray(R.array.current_week);
		DaysAdapter adapter = new DaysAdapter(this, mDays, mSelDays);
		mDayList.setAdapter(adapter);		
	}
	
}
