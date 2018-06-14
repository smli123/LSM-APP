package com.thingzdo.ui.manage;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class AboutUsActivity extends TitledActivity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_aboutus, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		setTitleLeftButton(R.string.smartplug_goback, R.drawable.title_btn_selector, this);
		
		TextView verText = (TextView)findViewById(R.id.textVer);
		verText.setText(PubFunc.getAppVersion());
		
		TextView txtCorp = (TextView)findViewById(R.id.textView1);
		txtCorp.setText(getString(R.string.corp_name) + " " + getString(R.string.version_notify));
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:
			finish();
			break;
		}
	}
}
