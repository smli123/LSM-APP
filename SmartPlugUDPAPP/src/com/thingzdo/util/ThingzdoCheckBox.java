package com.thingzdo.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubFunc;

public class ThingzdoCheckBox extends ImageView {
	
	private ThingzdoClickListener mClickListener = new ThingzdoClickListener();
	
    private boolean mChecked = false;
	public ThingzdoCheckBox(Context context) {
		super(context);
		setupClickListener();
	}
	
	public ThingzdoCheckBox(Context context, AttributeSet attrs, 
            int defStyle) { 
        super(context, attrs, defStyle); 
        setupClickListener();
    } 
 
    public ThingzdoCheckBox(Context context, AttributeSet attrs) { 
        super(context, attrs); 
        setupClickListener();
    } 
    
	private void setupClickListener(){
		setOnClickListener(mClickListener, true);
		updateButtonBackground();
	}
	
	private void updateButtonBackground(){
		int backgroundResId = R.drawable.smp_uncheck;
		if (true == mChecked) {
			backgroundResId = R.drawable.smp_checked;
		} else {
			backgroundResId = R.drawable.smp_uncheck;
		}	
		setImageResource(backgroundResId);
	}	
	
	private void setOnClickListener(OnClickListener l, boolean self){
		super.setOnClickListener(l);
	}    
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		mClickListener.setWrapperClickListener(l);
	}
	
	public void setChecked(boolean checked) {
		mChecked = checked;
		updateButtonBackground();		
	}
	
	public boolean isChecked() {
		return mChecked;
	}
	
	private class ThingzdoClickListener implements OnClickListener{
		private OnClickListener mWrapperClickListener = null;
		public void setWrapperClickListener(OnClickListener l){
			mWrapperClickListener = l;
		}
		@Override
		public void onClick(View v) {
			PubFunc.log("ThingzdoClickListener Click="+ mChecked);
			mChecked = !mChecked;
			updateButtonBackground();
			if (mWrapperClickListener != null){
				mWrapperClickListener.onClick(v);
			}
		}
	}	

}
