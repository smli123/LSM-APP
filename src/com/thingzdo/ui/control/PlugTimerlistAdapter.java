package com.thingzdo.ui.control;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.TimerDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class PlugTimerlistAdapter extends BaseAdapter {

	private ArrayList<TimerDefine> mTasklist = null;
	private LayoutInflater mInflater = null;
	private Context mContext = null;
	private String mPlugId = null;
	private String mPlugIp = null;
	private String[] mDays;
	// private boolean mIsEdit = false;
	private Handler mHandler;
	private boolean mIsOnline = false;

	public PlugTimerlistAdapter(Context context, String plugId, String plugIp,
			ArrayList<TimerDefine> taskList, Handler handler, boolean isOnline) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mTasklist = taskList;
		mPlugId = plugId;
		mPlugIp = plugIp;
		mDays = mContext.getResources().getStringArray(R.array.current_week);
		mHandler = handler;
		mIsOnline = isOnline;
	}

	/*
	 * public void setEditable(boolean edit) { mIsEdit = edit; }
	 */

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mTasklist.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return mTasklist.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	private class ViewHolder {
		public TextView txtDay; // ��ʾÿ�ܼ�����
		public ImageView imgTaskType;
		public TextView txtTaskBegin;
		public TextView txtTaskEnd;
		public TextView img_timer_start;
		public TextView img_timer_stop;
		public ImageView imgMore;
		public ImageView imgEnable;
		public TextView txtTimerPCMac;
		// public ImageView imgDelete;

		public RelativeLayout layout_Whole;
		public RelativeLayout layout_Left;

		public void ViewData(TimerDefine task, int position) {
			if (task != null) {
				txtDay.setSingleLine(true);
				txtDay.setText(getDay(task.mPeriod));

				img_timer_stop.setText(SmartPlugApplication.getInstance()
						.getString(R.string.timer_task_timer_stop));

				switch (task.mType) {
					case PubDefine.TIMER_TYPE_POWER :
						txtTaskEnd.setVisibility(View.VISIBLE);
						imgTaskType
								.setImageResource(R.drawable.smp_power_on_small);
						break;
					case PubDefine.TIMER_TYPE_LIGHT :
						txtTaskEnd.setVisibility(View.VISIBLE);
						imgTaskType
								.setImageResource(R.drawable.smp_light_on_small);
						break;
					case PubDefine.TIMER_TYPE_USB :
						txtTaskEnd.setVisibility(View.VISIBLE);
						imgTaskType
								.setImageResource(R.drawable.smp_usb_on_small);
						break;
					case PubDefine.TIMER_TYPE_BELL :
						txtTaskEnd.setVisibility(View.VISIBLE);
						imgTaskType
								.setImageResource(R.drawable.smp_parentctrl_active_small);
						break;
					case PubDefine.TIMER_TYPE_OPENPC :
						txtTaskBegin.setVisibility(View.VISIBLE);
						imgTaskType.setImageResource(R.drawable.smp_openpc_mac);
						img_timer_stop.setText(SmartPlugApplication
								.getInstance().getString(
										R.string.timer_task_timer_mac));
						break;
					case PubDefine.TIMER_TYPE_CLOSEPC :
						img_timer_start.setVisibility(View.GONE);
						txtTaskBegin.setVisibility(View.GONE);
						img_timer_stop.setVisibility(View.VISIBLE);
						txtTaskEnd.setVisibility(View.VISIBLE);
						imgTaskType.setImageResource(R.drawable.smp_closepc);
						// lishimin
						txtTimerPCMac.setText(task.mPCName);
						break;
					default :
						txtTaskEnd.setVisibility(View.VISIBLE);
						imgTaskType.setImageResource(R.drawable.smp_plug_small);
						break;
				}

				txtDay.setContentDescription(String.valueOf(task.mPlugId));
				imgTaskType.setContentDescription(String.valueOf(task.mType));
				StringBuilder sb = new StringBuilder();
				sb.append(task.mPowerOnTime);
				txtTaskBegin.setText(sb.toString());
				sb.delete(0, sb.length());// ���
				sb.append(task.mPowerOffTime);
				txtTaskEnd.setText(sb);
				if (task.mEnable) {
					imgEnable.setImageResource(R.drawable.smp_switcher_open);
					imgEnable.setContentDescription("1");
				} else {
					imgEnable.setImageResource(R.drawable.smp_switcher_close);
					imgEnable.setContentDescription("0");
				}

				layout_Whole.setContentDescription(String
						.valueOf(task.mTimerId));
				// layout_Whole.setOnClickListener(selectTaskClick);
				imgMore.setVisibility(View.GONE);
				imgEnable.setVisibility(View.VISIBLE);
				// imgEnable.setContentDescription(String.valueOf(task.mTimerId));
				imgEnable.setContentDescription(String.valueOf(task.mPlugId)
						+ " " + String.valueOf(task.mTimerId));
				if (true == mIsOnline) {
					imgEnable.setOnClickListener(enableTaskClick);
				} else {
					imgEnable.setOnClickListener(null);
				}
				layout_Left.setVisibility(View.VISIBLE);
			}
		}
	}

	private String getDay(String days) {
		StringBuffer sb = new StringBuffer();
		String[] selDays = {"0", "0", "0", "0", "0", "0", "0"};
		for (int i = 0; i < 7; i++) {
			selDays[i] = days.substring(i, i + 1);
		}

		for (int i = 0; i < 7; i++) {
			if (selDays[i].equals("1")) {
				if (i < 6) {
					sb.append(mDays[i]).append(" ");
				} else {
					sb.append(mDays[i]);
				}
			}
		}

		// 新增 重复周期功能：例如，1111111_30, 其中 30代表每隔30分钟就循环一次；当天内有效；
		if (PubDefine.RELEASE_VERSION == false) {
			String[] str_Period = days.split("_");
			if (str_Period.length >= 2) {
				if (!str_Period[1].equals("0") && !str_Period[1].equals("")) {
					sb.append("   " + str_Period[1]);
				}
			}
		}

		return sb.toString();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if ((mTasklist == null) || (mTasklist.size() == 0)) {
			return convertView;
		}
		if ((position < 0) || (position > mTasklist.size())) {
			return convertView;
		}
		if (mInflater == null) {
			return convertView;
		}

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_plug_task, null);
			holder.txtDay = (TextView) convertView
					.findViewById(R.id.txtTimerPeriod);
			holder.imgTaskType = (ImageView) convertView
					.findViewById(R.id.plug_img_type);
			holder.txtTaskBegin = (TextView) convertView
					.findViewById(R.id.txtTaskTimerBegin);
			holder.txtTaskEnd = (TextView) convertView
					.findViewById(R.id.txtTaskTimerEnd);
			holder.img_timer_start = (TextView) convertView
					.findViewById(R.id.img_timer_start);
			holder.img_timer_stop = (TextView) convertView
					.findViewById(R.id.img_timer_stop);
			holder.imgMore = (ImageView) convertView
					.findViewById(R.id.plug_img_more);
			holder.imgEnable = (ImageView) convertView
					.findViewById(R.id.plug_img_enabled);
			holder.layout_Whole = (RelativeLayout) convertView
					.findViewById(R.id.lay_plug_whole);
			holder.layout_Left = (RelativeLayout) convertView
					.findViewById(R.id.plug_lay_delete);
			holder.txtTimerPCMac = (TextView) convertView
					.findViewById(R.id.txtTimerPCMac);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (holder != null && mTasklist != null && position < mTasklist.size()) {
			TimerDefine timer = mTasklist.get(position);
			convertView.setBackgroundColor(Color.TRANSPARENT);
			holder.ViewData(timer, position);
		}
		return convertView;
	}

	View.OnClickListener selectTaskClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, PlugTimerActivity.class);
			intent.putExtra("PLUGID", mPlugId);
			intent.putExtra("PLUGIP", mPlugIp);
			ImageView imgType = (ImageView) v.findViewById(R.id.plug_img_type);
			intent.putExtra("TIMER_TYPE", Integer.parseInt(imgType
					.getContentDescription().toString()));
			intent.putExtra("TIMERID", v.getContentDescription().toString());
			mContext.startActivity(intent);
		}
	};

	View.OnClickListener deleteTaskClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Message msg = new Message();
			msg.what = 2;
			msg.obj = v.getContentDescription().toString();
			// msg.arg1 =
			// Integer.parseInt(v.getContentDescription().toString());
			mHandler.sendMessage(msg);
		}
	};

	View.OnClickListener enableTaskClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Message msg = new Message();
			msg.what = 1;
			msg.obj = v.getContentDescription().toString();
			// msg.arg1 =
			// Integer.parseInt(v.getContentDescription().toString());
			mHandler.sendMessage(msg);
		}
	};
}
