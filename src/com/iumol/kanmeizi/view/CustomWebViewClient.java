/*
 * HuoSu Browser for Android
 * 
 * Copyright (C) 2010 - 2012 J. Devauchelle and contributors.
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

import com.iumol.kanmeizi.R;
import com.iumol.kanmeizi.activities.WebViewActivity;
import com.iumol.kanmeizi.util.ApplicationUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.EditText;

/**
 * Convenient extension of WebViewClient.
 */
public class CustomWebViewClient extends WebViewClient {

	private WebViewActivity mWebViewActivity;

	public CustomWebViewClient(WebViewActivity activity) {
		super();
		mWebViewActivity = activity;
	}

	@Override
	public void onPageFinished(WebView view, String url) {

		((CustomWebView) view).notifyPageFinished();
		mWebViewActivity.onPageFinished(url);
		super.onPageFinished(view, url);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {

		((CustomWebView) view).notifyPageStarted();
		mWebViewActivity.onPageStarted(url);
		super.onPageStarted(view, url, favicon);
	}

	@TargetApi(8)
	@Override
	public void onReceivedSslError(WebView view, final SslErrorHandler handler,
			SslError error) {

		StringBuilder sb = new StringBuilder();

		sb.append(view.getResources().getString(
				R.string.Commons_SslWarningsHeader));
		sb.append("\n\n");

		if (error.hasError(SslError.SSL_UNTRUSTED)) {
			sb.append(" - ");
			sb.append(view.getResources().getString(
					R.string.Commons_SslUntrusted));
			sb.append("\n");
		}

		if (error.hasError(SslError.SSL_IDMISMATCH)) {
			sb.append(" - ");
			sb.append(view.getResources().getString(
					R.string.Commons_SslIDMismatch));
			sb.append("\n");
		}

		if (error.hasError(SslError.SSL_EXPIRED)) {
			sb.append(" - ");
			sb.append(view.getResources()
					.getString(R.string.Commons_SslExpired));
			sb.append("\n");
		}

		if (error.hasError(SslError.SSL_NOTYETVALID)) {
			sb.append(" - ");
			sb.append(view.getResources().getString(
					R.string.Commons_SslNotYetValid));
			sb.append("\n");
		}

		ApplicationUtils.showContinueCancelDialog(view.getContext(),
				android.R.drawable.ic_dialog_info, view.getResources()
						.getString(R.string.Commons_SslWarning), sb.toString(),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						handler.proceed();
					}

				}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						handler.cancel();
					}
				});
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {

		return false;
	}

	@Override
	public void onReceivedHttpAuthRequest(WebView view,
			final HttpAuthHandler handler, final String host, final String realm) {
		String username = null;
		String password = null;

		boolean reuseHttpAuthUsernamePassword = handler
				.useHttpAuthUsernamePassword();

		if (reuseHttpAuthUsernamePassword && view != null) {
			String[] credentials = view
					.getHttpAuthUsernamePassword(host, realm);
			if (credentials != null && credentials.length == 2) {
				username = credentials[0];
				password = credentials[1];
			}
		}

		if (username != null && password != null) {
			handler.proceed(username, password);
		} else {
			LayoutInflater factory = LayoutInflater.from(mWebViewActivity);
			final View v = factory.inflate(R.layout.http_authentication_dialog,
					null);

			if (username != null) {
				((EditText) v.findViewById(R.id.username_edit))
						.setText(username);
			}
			if (password != null) {
				((EditText) v.findViewById(R.id.password_edit))
						.setText(password);
			}

			AlertDialog dialog = new AlertDialog.Builder(mWebViewActivity)
					.setTitle(
							String.format(
									mWebViewActivity
											.getString(R.string.HttpAuthenticationDialog_DialogTitle),
									host, realm))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setView(v)
					.setPositiveButton(R.string.Commons_Proceed,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String nm = ((EditText) v
											.findViewById(R.id.username_edit))
											.getText().toString();
									String pw = ((EditText) v
											.findViewById(R.id.password_edit))
											.getText().toString();
									mWebViewActivity
											.setHttpAuthUsernamePassword(host,
													realm, nm, pw);
									handler.proceed(nm, pw);
								}
							})
					.setNegativeButton(R.string.Commons_Cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									handler.cancel();
								}
							})
					.setOnCancelListener(
							new DialogInterface.OnCancelListener() {
								public void onCancel(DialogInterface dialog) {
									handler.cancel();
								}
							}).create();

			dialog.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			dialog.show();

			v.findViewById(R.id.username_edit).requestFocus();
		}
	}

	@Override
	public void onLoadResource(WebView view, String url) {

		super.onLoadResource(view, url);
	}

	@SuppressLint("NewApi")
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		// TODO Auto-generated method stub

		if (url.contains("cpro.baidustatic.com")) {

			WebResourceResponse response = new WebResourceResponse("text/xml",
					"utf-8", null);
			return response;

		}
		return super.shouldInterceptRequest(view, url);
	}

	private boolean isExternalApplicationUrl(String url) {

		return url.startsWith("vnd.") || url.startsWith("rtsp://")
				|| url.startsWith("itms://") || url.startsWith("itpc://");
	}

}
