package com.iumol.kanmeizi.activities;

import com.iumol.kanmeizi.R;
import com.iumol.kanmeizi.util.SystemUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class WelcomeActivity extends Activity implements Runnable {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		String ver = SystemUtils.getVersionName(this);
		String auth = String.format(
				getResources().getString(R.string.Commons_Auth), ver);
		TextView auth_view = (TextView) findViewById(R.id.auth);
		auth_view.setText(auth);

		if (1 != getIntent().getFlags())
			new Thread(this).start();
	}

	public void run() {
		try {

			Thread.sleep(1200);
			startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
			finish();

		} catch (InterruptedException e) {

		}
	}
}
