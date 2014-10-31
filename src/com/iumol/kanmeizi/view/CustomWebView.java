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

package com.iumol.kanmeizi.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.iumol.kanmeizi.R;
import com.iumol.kanmeizi.activities.WebViewActivity;
import com.iumol.kanmeizi.constant.AndroidConstants;
import com.iumol.kanmeizi.util.IOUtils;
import com.iumol.kanmeizi.util.PreferencesUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebSettings.ZoomDensity;

/**
 * A convenient extension of WebView.
 */
@SuppressLint({ "ClickableViewAccessibility", "SetJavaScriptEnabled" })
public class CustomWebView extends WebView {

	private Context mContext;

	private boolean mIsLoading = false;

	private int mProgress = WebViewActivity.MaxWebProgress;
	private int mVitualProgress = 0;

	private String mLoadedUrl;
	private static boolean mBoMethodsLoaded = false;

	private static Method mOnPauseMethod = null;
	private static Method mOnResumeMethod = null;
	private static String mAdSweepString = null;

	@SuppressWarnings("deprecation")
	private static final TextSize[] text_size = { WebSettings.TextSize.SMALLER,
			WebSettings.TextSize.NORMAL, WebSettings.TextSize.LARGER,
			WebSettings.TextSize.LARGEST };

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The current context.
	 */
	public CustomWebView(Context context) {
		super(context);

		mContext = context;

		initializeOptions();
		loadMethods();
	}

	public int getVitualProgress() {
		return mVitualProgress;
	}

	public void setVitualProgress(int mVitualProgress) {
		this.mVitualProgress = mVitualProgress;
	}

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The current context.
	 * @param attrs
	 *            The attribute set.
	 */
	public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;

		initializeOptions();
		loadMethods();
	}

	/**
	 * Initialize the WebView with the options set by the user through
	 * preferences.
	 */
	@SuppressWarnings("deprecation")
	public void initializeOptions() {

		requestFocus();

		WebSettings settings = getSettings();

		// User settings
		settings.setJavaScriptEnabled(true);

		settings.setLoadsImagesAutomatically(true);
		/*
		 * settings.setLoadsImagesAutomatically(PreferencesUtils.getBoolean(
		 * mContext, AndroidConstants.PREFERENCES_LIGHTAPP_ENABLE_IMAGES,
		 * true));
		 */

		settings.setLoadWithOverviewMode(true);
		settings.setSaveFormData(true);
		// settings.setSavePassword(true);
		settings.setDefaultZoom(ZoomDensity.MEDIUM);
		settings.setUserAgentString("");

		CookieManager.getInstance().setAcceptCookie(true);

		settings.setSupportZoom(true);

		settings.setGeolocationEnabled(true);
		// Technical settings
		settings.setSupportMultipleWindows(false);
		setLongClickable(true);
		setScrollbarFadingEnabled(true);
		setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		setDrawingCacheEnabled(true);

		settings.setAppCacheEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setUseWideViewPort(true);

		File cachefile = IOUtils.getWebCacheFolder(mContext);
		if (null != cachefile) {
			String cacheDirPath = cachefile.getAbsolutePath();
			settings.setDatabasePath(cacheDirPath);
			settings.setAppCachePath(cacheDirPath);
		}
	}

	public void onScrollChanged(int l, int t, int oldl, int oldt) {

	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();

		// Enable / disable zoom support in case of multiple pointer, e.g.
		// enable zoom when we have two down pointers, disable with one pointer
		// or when pointer up.
		// We do this to prevent the display of zoom controls, which are not
		// useful and override over the right bubble.
		if ((action == MotionEvent.ACTION_DOWN)
				|| (action == MotionEvent.ACTION_POINTER_DOWN)
				|| (action == MotionEvent.ACTION_POINTER_1_DOWN)
				|| (action == MotionEvent.ACTION_POINTER_2_DOWN)
				|| (action == MotionEvent.ACTION_POINTER_3_DOWN)) {

			if (ev.getPointerCount() > 1) {
				this.getSettings().setBuiltInZoomControls(true);
				this.getSettings().setSupportZoom(true);
			} else {
				this.getSettings().setBuiltInZoomControls(false);
				this.getSettings().setSupportZoom(false);
			}

		} else if ((action == MotionEvent.ACTION_UP)
				|| (action == MotionEvent.ACTION_POINTER_UP)
				|| (action == MotionEvent.ACTION_POINTER_1_UP)
				|| (action == MotionEvent.ACTION_POINTER_2_UP)
				|| (action == MotionEvent.ACTION_POINTER_3_UP)) {

			this.getSettings().setBuiltInZoomControls(false);
			this.getSettings().setSupportZoom(false);

		} else if (action == MotionEvent.ACTION_MOVE) {

		}

		return super.onTouchEvent(ev);
	}

	@Override
	public void loadUrl(String url) {
		mLoadedUrl = url;
		super.loadUrl(url);
	}

	/**
	 * Inject the AdSweep javascript.
	 */
	public void loadAdSweep() {
		super.loadUrl(getAdSweepString(mContext));
	}

	public static String getAdSweepString(Context context) {
		if (mAdSweepString == null) {
			InputStream is = context.getResources().openRawResource(
					R.raw.adsweep);
			if (is != null) {
				StringBuilder sb = new StringBuilder();
				String line;

				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is, "UTF-8"));
					while ((line = reader.readLine()) != null) {
						if ((line.length() > 0) && (!line.startsWith("//"))) {
							sb.append(line).append("\n");
						}
					}
				} catch (IOException e) {
					Log.w("AdSweep",
							"Unable to load AdSweep: " + e.getMessage());
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						Log.w("AdSweep",
								"Unable to load AdSweep: " + e.getMessage());
					}
				}
				mAdSweepString = sb.toString();
			} else {
				mAdSweepString = "";
			}
		}
		return mAdSweepString;
	}

	/**
	 * Set the current loading progress of this view.
	 * 
	 * @param progress
	 *            The current loading progress.
	 */
	public void setProgress(int progress) {
		mProgress = progress;
	}

	/**
	 * Get the current loading progress of the view.
	 * 
	 * @return The current loading progress of the view.
	 */
	public int getProgress() {
		return mProgress;
	}

	/**
	 * Triggered when a new page loading is requested.
	 */
	public void notifyPageStarted() {
		mIsLoading = true;
	}

	/**
	 * Triggered when the page has finished loading.
	 */
	public void notifyPageFinished() {
		mVitualProgress = 0;
		mProgress = WebViewActivity.MaxWebProgress;
		mIsLoading = false;
	}

	/**
	 * Check if the view is currently loading.
	 * 
	 * @return True if the view is currently loading.
	 */
	public boolean isLoading() {
		return mIsLoading;
	}

	/**
	 * Get the loaded url, e.g. the one asked by the user, without redirections.
	 * 
	 * @return The loaded url.
	 */
	public String getLoadedUrl() {
		return mLoadedUrl;
	}

	/**
	 * Reset the loaded url.
	 */
	public void resetLoadedUrl() {
		mLoadedUrl = null;
	}

	public boolean isSameUrl(String url) {
		if (url != null) {
			return url.equalsIgnoreCase(this.getUrl());
		}

		return false;
	}

	/**
	 * Perform an 'onPause' on this WebView through reflexion.
	 */
	public void doOnPause() {
		if (mOnPauseMethod != null) {
			try {

				mOnPauseMethod.invoke(this);

			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView", "doOnPause(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView", "doOnPause(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView", "doOnPause(): " + e.getMessage());
			}
		}
	}

	/**
	 * Perform an 'onResume' on this WebView through reflexion.
	 */
	public void doOnResume() {
		if (mOnResumeMethod != null) {
			try {

				mOnResumeMethod.invoke(this);

			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView", "doOnResume(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView", "doOnResume(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView", "doOnResume(): " + e.getMessage());
			}
		}
	}

	/**
	 * Load static reflected methods.
	 */
	private void loadMethods() {

		if (!mBoMethodsLoaded) {

			try {

				mOnPauseMethod = WebView.class.getMethod("onPause");
				mOnResumeMethod = WebView.class.getMethod("onResume");

			} catch (SecurityException e) {
				Log.e("CustomWebView", "loadMethods(): " + e.getMessage());
				mOnPauseMethod = null;
				mOnResumeMethod = null;
			} catch (NoSuchMethodException e) {
				Log.e("CustomWebView", "loadMethods(): " + e.getMessage());
				mOnPauseMethod = null;
				mOnResumeMethod = null;
			}

			mBoMethodsLoaded = true;
		}
	}

	@Override
	@ExportedProperty(category = "webview")
	public String getUrl() {
		// TODO Auto-generated method stub
		if (super.getUrl() == null)
			return "";
		return super.getUrl();
	}

}
