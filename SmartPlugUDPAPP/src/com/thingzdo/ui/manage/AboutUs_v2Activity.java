package com.thingzdo.ui.manage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class AboutUs_v2Activity extends TitledActivity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_about_v2, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		
		TextView verText = (TextView)findViewById(R.id.tv_smartplug_version);
		verText.setText(PubFunc.getAppVersion());
		RelativeLayout rl_company_web = (RelativeLayout)findViewById(R.id.rl_company_web);
		rl_company_web.setOnClickListener(this);
		RelativeLayout rl_service_phone = (RelativeLayout)findViewById(R.id.rl_service_phone);
		rl_service_phone.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:
			finish();
			break;
		case R.id.rl_company_web: 		// web address
			String url = SmartPlugApplication.getContext().getString(R.string.smartplug_company_web_address);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
			break;
		case R.id.rl_service_phone:		//直接播出电话
			Uri uri = Uri.parse("tel:" + SmartPlugApplication.getContext().getString(R.string.smartplug_service_phone_number));
			Intent call = new Intent(Intent.ACTION_CALL, uri);
			startActivity(call);
			break;
		}
	}
}
