package com.thingzdo.ui.control;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.util.ThingzdoCheckBox;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DaysAdapter extends BaseAdapter {

	private LayoutInflater mInflater = null;
	private Context mContext = null;
	private String[] mDays = null;
	private String[] mSelDays = null;
	
	public DaysAdapter(Context context, String[] days, String[] seldays) {
		mContext = context;
		mDays = days;
		mInflater = LayoutInflater.from(mContext);
		mSelDays = seldays;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDays.length;
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return mDays[pos];
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    ViewHolder holder = null;
	    if (convertView == null) {
	    	holder = new ViewHolder();
	    	convertView = mInflater.inflate(R.layout.view_timerdays_item, null);
            holder.txtDay = (TextView)convertView.findViewById(R.id.txt_week_day);
            holder.imgChose =  (ThingzdoCheckBox)convertView.findViewById(R.id.img_days);
            holder.rlDay = (RelativeLayout) convertView.findViewById(R.id.rlTimer);
            convertView.setTag(holder);
	    } else {
      	    holder = (ViewHolder) convertView.getTag();
	    }
	    
	    if (holder != null){
	    	convertView.setBackgroundColor(Color.TRANSPARENT);
      	  	holder.ViewData(mDays, position);
	    }	    
	    return convertView;
	}
	private class ViewHolder
	{
		public TextView    txtDay; 
		public ThingzdoCheckBox   imgChose;
		public RelativeLayout rlDay;
      
		public void ViewData(String[] days, int pos){
			txtDay.setSingleLine(true);
			txtDay.setText(mContext.getString(R.string.week) + days[pos]);
			if (null != mSelDays) {
				imgChose.setChecked(mSelDays[pos].equals("1") ? true : false);
			} else {
				imgChose.setChecked(true);
			}
			imgChose.setContentDescription(String.valueOf(pos));
			rlDay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					imgChose.setChecked(!imgChose.isChecked());
					
				}
			});
		}
	}

}
