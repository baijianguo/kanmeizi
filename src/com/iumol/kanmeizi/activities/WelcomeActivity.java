package com.iumol.kanmeizi.activities;

import com.iumol.kanmeizi.R;
import com.iumol.kanmeizi.util.SystemUtils;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WelcomeActivity extends BaseActivity implements Runnable {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		String ver = SystemUtils.getVersionName(this);
		String auth = String.format(
				getResources().getString(R.string.Commons_Auth), ver);
		TextView auth_view = (TextView) findViewById(R.id.auth);
		auth_view.setText(auth);

		if (1 != getIntent().getFlags())
			auth_view.postDelayed(new Thread(this), 1200);
	}

	public void run() {
		toMainActivity();
	}

	public void toMainActivity() {

		Intent mIntent = new Intent(this, MainActivity.class);
		startActivity(mIntent);
		finish();

	}
}
