package com.thingzdo.ui.wheelutils;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

import android.view.View;

public class WheelMain {

	private View view;
	private WheelView wv_hours;
	private WheelView wv_mins;
	private WheelView wv_secs;
	public int screenheight;
	private boolean hasSelectTime;
	private static int START_YEAR = 1990, END_YEAR = 2100;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public static int getSTART_YEAR() {
		return START_YEAR;
	}

	public static void setSTART_YEAR(int sTART_YEAR) {
		START_YEAR = sTART_YEAR;
	}

	public static int getEND_YEAR() {
		return END_YEAR;
	}

	public static void setEND_YEAR(int eND_YEAR) {
		END_YEAR = eND_YEAR;
	}

	public WheelMain(View view) {
		super();
		this.view = view;
		hasSelectTime = false;
		setView(view);
	}

	public WheelMain(View view, boolean hasSelectTime) {
		super();
		this.view = view;
		this.hasSelectTime = hasSelectTime;
		setView(view);
	}

	/**
	 * @Description: TODO 弹出时间选择器
	 */
	public void initDateTimePicker(int h, int m, int s) {
		wv_hours = (WheelView) view.findViewById(R.id.hour);
		wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
		wv_hours.setCyclic(true);
		wv_hours.setLabel("");
		wv_hours.setCurrentItem(h);
		
		wv_mins = (WheelView) view.findViewById(R.id.min);
		wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
		wv_mins.setCyclic(true);
		wv_mins.setLabel("");
		wv_mins.setCurrentItem(m);		

		wv_secs = (WheelView) view.findViewById(R.id.sec);
		wv_secs.setAdapter(new NumericWheelAdapter(0, 59));
		wv_secs.setCyclic(true);
		wv_secs.setLabel("");
		wv_secs.setCurrentItem(s);		

		wv_hours = (WheelView) view.findViewById(R.id.hour);
		wv_mins = (WheelView) view.findViewById(R.id.min);
		wv_secs = (WheelView) view.findViewById(R.id.sec);
		wv_hours.setVisibility(View.VISIBLE);
		wv_mins.setVisibility(View.VISIBLE);
		wv_secs.setVisibility(View.VISIBLE);

		wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
		wv_hours.setCyclic(true);// 可循环滚动
		wv_hours.setLabel(SmartPlugApplication.getContext().getString(R.string.timer_task_hour));// 添加文字
		wv_hours.setCurrentItem(h);

		wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
		wv_mins.setCyclic(true);// 可循环滚动
		wv_mins.setLabel(SmartPlugApplication.getContext().getString(R.string.timer_task_minute));// 添加文字
		wv_mins.setCurrentItem(m);
		
		wv_secs.setAdapter(new NumericWheelAdapter(0, 59));
		wv_secs.setCyclic(true);// 可循环滚动
		wv_secs.setLabel(SmartPlugApplication.getContext().getString(R.string.timer_task_second));// 添加文字
		wv_secs.setCurrentItem(s);

		// 添加"小时"监听
		OnWheelChangedListener wheelListener_hour = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
			}
		};
		// 添加"分钟"监听
		OnWheelChangedListener wheelListener_minute = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
			}
		};
		// 添加"秒"监听
		OnWheelChangedListener wheelListener_second = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wv_secs.setAdapter(new NumericWheelAdapter(0, 59));
			}
		};
		wv_hours.addChangingListener(wheelListener_hour);
		wv_mins.addChangingListener(wheelListener_minute);
		wv_mins.addChangingListener(wheelListener_second);

		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		int textSize = 0;
		if (hasSelectTime)
			textSize = (screenheight / 100) * 3;
		else
			textSize = (screenheight / 100) * 4;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;
		wv_secs.TEXT_SIZE = textSize;

	}

	public String getTime() {
		StringBuffer sb = new StringBuffer();
		if (!hasSelectTime)
			sb.append(wv_hours.getCurrentItem()).append(":")
				.append(wv_mins.getCurrentItem()).append(":")
				.append(wv_secs.getCurrentItem());
		else
			sb.append(wv_hours.getCurrentItem()).append(":")
				.append(wv_mins.getCurrentItem()).append(":")
				.append(wv_secs.getCurrentItem());
		return sb.toString();
	}
}
