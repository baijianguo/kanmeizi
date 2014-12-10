package com.iumol.kanmeizi.activities;

import com.iumol.kanmeizi.R;
import com.iumol.kanmeizi.runnables.WebLoadingProgressRunnable;
import com.iumol.kanmeizi.util.StringUtils;
import com.iumol.kanmeizi.view.CustomWebView;
import com.iumol.kanmeizi.view.CustomWebViewClient;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WebViewActivity extends Activity implements OnClickListener,
		OnTouchListener {

	private CustomWebView mWebView = null;
	private ImageView btn_back = null;
	private ImageView btn_forward = null;
	private ImageView btn_fresh = null;
	private ImageView btn_home = null;
	private View bottomView = null;

	private ProgressBar mProgressBar = null;
	public static String homepage = "http://www.iumol.com/";
	public static final int MaxWebProgress = 1000;

	protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);

	// webview 相关
	private static final int OPEN_FILE_CHOOSER_ACTIVITY = 0;

	private FrameLayout mFullscreenContainer;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
	private View mCustomView;

	private WebLoadingProgressRunnable mWebLoadingProgressRunnable = null;

	private Bitmap mDefaultVideoPoster = null;
	private View mVideoProgressView = null;
	public static String TAG = "WebViewActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.web_view);

		mWebView = (CustomWebView) findViewById(R.id.webview);
		initializeWebView();

		Intent intent = getIntent();
		if (null != intent.getStringExtra("url"))
			homepage = intent.getStringExtra("url");

		mProgressBar = (ProgressBar) findViewById(R.id.WebViewProgress);
		mProgressBar.setMax(MaxWebProgress);

		bottomView = findViewById(R.id.bottom_button);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_forward = (ImageView) findViewById(R.id.btn_forward);
		btn_forward.setOnClickListener(this);
		btn_fresh = (ImageView) findViewById(R.id.btn_fresh);
		btn_fresh.setOnClickListener(this);
		btn_home = (ImageView) findViewById(R.id.btn_home);
		btn_home.setOnClickListener(this);

		LoadUrl(homepage);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mWebView.doOnResume();
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mWebView.doOnPause();
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onDestroy() {

		if (null != mWebView) {
			mWebView.loadUrl("about:blank");
			mWebView.clearCache(true);
			mWebView.removeAllViews();
			mWebView.destroy();
		}

		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public void setHttpAuthUsernamePassword(String host, String realm,
			String username, String password) {
		mWebView.setHttpAuthUsernamePassword(host, realm, username, password);
	}

	/**
	 * InitializeWebView.
	 */
	private void initializeWebView() {

		mWebView.setOnTouchListener(this);

		mWebView.setWebViewClient(new CustomWebViewClient(this));
		mWebView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				// TODO Auto-generated method stub
				UserBrowserOpenUrl(url);
			}
		});

		final Activity activity = this;
		mWebView.setWebChromeClient(new WebChromeClient() {

			@SuppressWarnings("unused")
			// This is an undocumented method, it _is_ used, whatever Eclipse
			// may think :)
			// Used to show a file chooser dialog.
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				// mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				WebViewActivity.this.startActivityForResult(Intent
						.createChooser(i, WebViewActivity.this
								.getString(R.string.Main_FileChooserPrompt)),
						OPEN_FILE_CHOOSER_ACTIVITY);
			}

			// 配置权限（同样在WebChromeClient中实现）
			public void onGeolocationPermissionsShowPrompt(String origin,
					GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}

			@SuppressWarnings("unused")
			// This is an undocumented method, it _is_ used, whatever Eclipse
			// may think :)
			// Used to show a file chooser dialog.
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType) {
				// mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				WebViewActivity.this.startActivityForResult(Intent
						.createChooser(i, WebViewActivity.this
								.getString(R.string.Main_FileChooserPrompt)),
						OPEN_FILE_CHOOSER_ACTIVITY);
			}

			@Override
			public Bitmap getDefaultVideoPoster() {
				if (mDefaultVideoPoster == null) {
					mDefaultVideoPoster = BitmapFactory.decodeResource(
							WebViewActivity.this.getResources(),
							R.drawable.default_video_poster);
				}

				return mDefaultVideoPoster;
			}

			@Override
			public View getVideoLoadingProgressView() {
				if (mVideoProgressView == null) {
					LayoutInflater inflater = LayoutInflater
							.from(WebViewActivity.this);
					mVideoProgressView = inflater.inflate(
							R.layout.video_loading_progress, null);
				}

				return mVideoProgressView;
			}

			public void onShowCustomView(View view,
					WebChromeClient.CustomViewCallback callback) {

				showCustomView(view, callback);
			}

			@Override
			public void onHideCustomView() {

				hideCustomView();
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				newProgress = 10 * newProgress;
				((CustomWebView) view).setProgress(newProgress);

				setWebProgress(newProgress);
			}

			@Override
			public void onReceivedIcon(WebView view, Bitmap icon) {

				super.onReceivedIcon(view, icon);
			}

			@Override
			public boolean onCreateWindow(WebView view, final boolean dialog,
					final boolean userGesture, final Message resultMsg) {

				return true;
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {

				super.onReceivedTitle(view, title);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final JsResult result) {
				new AlertDialog.Builder(activity)
						.setTitle(R.string.Commons_JavaScriptDialog)
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new AlertDialog.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								}).setCancelable(false).create().show();

				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {
				new AlertDialog.Builder(WebViewActivity.this)
						.setTitle(R.string.Commons_JavaScriptDialog)
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.cancel();
									}
								}).create().show();

				return true;
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, final JsPromptResult result) {

				final LayoutInflater factory = LayoutInflater
						.from(WebViewActivity.this);
				final View v = factory.inflate(
						R.layout.javascript_prompt_dialog, null);
				((TextView) v.findViewById(R.id.JavaScriptPromptMessage))
						.setText(message);
				((EditText) v.findViewById(R.id.JavaScriptPromptInput))
						.setText(defaultValue);

				new AlertDialog.Builder(WebViewActivity.this)
						.setTitle(R.string.Commons_JavaScriptDialog)
						.setView(v)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										String value = ((EditText) v
												.findViewById(R.id.JavaScriptPromptInput))
												.getText().toString();
										result.confirm(value);
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										result.cancel();
									}
								})
						.setOnCancelListener(
								new DialogInterface.OnCancelListener() {
									public void onCancel(DialogInterface dialog) {
										result.cancel();
									}
								}).show();

				return true;

			}

		});

	}

	static class FullscreenHolder extends FrameLayout {

		public FullscreenHolder(Context ctx) {
			super(ctx);
			setBackgroundColor(ctx.getResources().getColor(
					android.R.color.black));
		}

		@Override
		public boolean onTouchEvent(MotionEvent evt) {
			return true;
		}

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (mCustomView != null) {
				hideCustomView();
			} else {
				this.finishTask();
			}
		}
		return false;
	}

	public void finishTask() {

		this.finish();

	}

	private void LoadUrl(String url) {

		if (!StringUtils.isBlank(url))
			mWebView.loadUrl(url);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btn_back:
			if (mWebView.canGoBack())
				mWebView.goBack();
			else
				this.finishTask();
			break;
		case R.id.btn_forward:
			if (mWebView.canGoForward())
				mWebView.goForward();
			break;
		case R.id.btn_fresh:
			mWebView.reload();
			break;
		case R.id.btn_home:
			if (!StringUtils.isBlank(mWebView.getUrl())
					&& !homepage.equals(mWebView.getUrl()))
				LoadUrl(homepage);
			break;
		default:
			break;
		}
	}

	public void UserBrowserOpenUrl(String url) {

		Uri uri = Uri.parse(url);// id为包名
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(it);
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

	private void showCustomView(View view,
			WebChromeClient.CustomViewCallback callback) {
		// if a view already exists then immediately terminate the new one
		if (mCustomView != null) {
			callback.onCustomViewHidden();
			return;
		}
		WebViewActivity.this.getWindow().getDecorView();
		FrameLayout decor = (FrameLayout) getWindow().getDecorView();
		mFullscreenContainer = new FullscreenHolder(WebViewActivity.this);
		mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
		decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
		mCustomView = view;
		bottomView.setVisibility(View.GONE);
		mCustomViewCallback = callback;

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	private void hideCustomView() {
		if (mCustomView == null)
			return;
		FrameLayout decor = (FrameLayout) getWindow().getDecorView();
		decor.removeView(mFullscreenContainer);
		bottomView.setVisibility(View.VISIBLE);
		mFullscreenContainer = null;
		mCustomView = null;
		mCustomViewCallback.onCustomViewHidden();

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		default:
			break;
		}
		hideKeyboard();
		return super.onTouchEvent(event);
	}

	public void hideKeyboard() {

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(bottomView.getWindowToken(), 0);

	}

	public void onPageStarted(String url) {

		// btn_back.setEnabled(false);
		btn_forward.setEnabled(false);

		if (mWebLoadingProgressRunnable == null)
			mWebLoadingProgressRunnable = new WebLoadingProgressRunnable(this);
		new Thread(mWebLoadingProgressRunnable).start();

		updateUI();
	}

	public void onPageFinished(String url) {

		mWebView.loadAdSweep();
		updateUI();
	}

	public Boolean CurrentWebLoading() {
		return mWebView.isLoading();
	}

	private void updateUI() {

		btn_forward.setEnabled(mWebView.canGoForward());
		setWebProgress(mWebView.getProgress());

		if (mWebView.isLoading()) {
			btn_forward.setEnabled(true);
			btn_forward.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_btn_stop));

		} else {
			btn_forward.setEnabled(mWebView.canGoForward());
			btn_forward.setImageDrawable(getResources().getDrawable(
					R.drawable.btn_forward));
		}
	}

	/*
	 * 处理WEB页加载进度条
	 */
	public void setWebProgress(int progress) {

		if (progress == 0) {

			int tmpProgress = mWebView.getProgress();
			if (tmpProgress >= MaxWebProgress)
				return;

			if (tmpProgress > mWebView.getVitualProgress())
				mWebView.setVitualProgress(mWebView.getProgress() + 1);

			else
				mWebView.setVitualProgress(mWebView.getVitualProgress() + 1);

			if (mWebView.getVitualProgress() < (MaxWebProgress - MaxWebProgress / 10))
				mProgressBar.setProgress(mWebView.getVitualProgress());
			// 如果线程运行，则停止线程
			else if (mWebLoadingProgressRunnable != null
					&& new Thread(mWebLoadingProgressRunnable).isAlive())
				new Thread(mWebLoadingProgressRunnable).stop();

			return;
		}

		if (mWebView.getProgress() < MaxWebProgress) {

			if (progress > mWebView.getVitualProgress()) {
				mProgressBar.setVisibility(View.VISIBLE);
				mProgressBar.setProgress(progress);
			}

		} else {

			mProgressBar.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */

	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}
}
