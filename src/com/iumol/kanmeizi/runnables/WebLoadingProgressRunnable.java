/*
 * HuoSu Browser for Android
 * 
 * Copyright (C) 2010 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.iumol.kanmeizi.runnables;

import com.iumol.kanmeizi.activities.WebViewActivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * A runnable to hide tool bars after the given delay.
 */
public class WebLoadingProgressRunnable implements Runnable {

	private static final String TAG = "WebLoadingProgressRunnable";

	private final int mDelay = 20;
	private WebViewActivity mActivity;

	public WebLoadingProgressRunnable(WebViewActivity activity) {

		mActivity = activity;
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			mActivity.setWebProgress(0);
		}
	};

	@Override
	public void run() {
		try {

			while (mActivity.CurrentWebLoading()) {
				Thread.sleep(mDelay);
				mHandler.sendEmptyMessage(0);
			}

		} catch (InterruptedException e) {
			Log.w(TAG, "Exception in thread: " + e.getMessage());
		}
	}

}
