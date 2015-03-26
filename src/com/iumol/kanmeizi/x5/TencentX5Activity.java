package com.iumol.kanmeizi.x5;

import java.net.URL;

import com.iumol.kanmeizi.R;
import com.iumol.kanmeizi.activities.AppManager;
import com.iumol.kanmeizi.activities.BaseActivity;
import com.iumol.kanmeizi.activities.MainActivity;
import com.iumol.kanmeizi.util.StringUtils;
import com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient.CustomViewCallback;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class TencentX5Activity extends BaseActivity {

	private TencentX5WebView mWebView;
	private ViewGroup mViewParent;
	private ImageButton mBack;
	private ImageButton mForward;
	private ImageButton mRefresh;
	private ImageButton mExit;
	private ImageButton mHome;

	private static final String TAG = "TencentX5";
	public static String homepage = "http://www.iumol.com/";
	private ValueCallback<Uri> uploadFile;

	private URL mIntentUrl;

	Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFormat(PixelFormat.RGBA_8888);

		Log.e("MainActivity",
				"QQBrowserSDK core version is "
						+ WebView.getQQBrowserCoreVersion(this));

		// if (WebView.getQQBrowserCoreVersion(this) == 0)
		// QbSdk.forceSysWebView();
		Intent intent = getIntent();
		if (intent != null) {
			if (null != intent.getStringExtra("url"))
				homepage = intent.getStringExtra("url");
			else if (intent.getData() != null)
				homepage = intent.getDataString();
		}
		// 在条件满足时开启硬件加速
		try {
			if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
				getWindow()
						.setFlags(
								android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
								android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		setContentView(R.layout.activity_x5);
		mViewParent = (ViewGroup) findViewById(R.id.webview);
		init();
		initBtnListenser();

		LoadUrl(homepage);
	}

	private void LoadUrl(String url) {

		if (!StringUtils.isBlank(url))
			mWebView.loadUrl(url);
	}

	private View mCustomView;
	private CustomViewCallback mCustomViewCallback;
	protected FrameLayout mFullscreenContainer;

	private void init() {
		// ========================================================
		// 创建WebView
		mWebView = new TencentX5WebView(this);
		mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT));

		// 设置Client
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view,
					String url) {
				// TODO Auto-generated method stub

				if (url.contains("cpro.baidustatic.com")) {

					WebResourceResponse response = new WebResourceResponse(
							"text/xml", "utf-8", null);
					return response;

				}
				return super.shouldInterceptRequest(view, url);
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				Log.d(TAG, "title: " + title);
			}

			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				if (mCustomView != null) {
					callback.onCustomViewHidden();
					return;
				}
				mCustomViewCallback = callback;
				FrameLayout decor = (FrameLayout) TencentX5Activity.this
						.getWindow().getDecorView();
				mFullscreenContainer = new FullscreenHolder(
						TencentX5Activity.this);
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						FrameLayout.LayoutParams.MATCH_PARENT);
				mFullscreenContainer.addView(view, lp);
				decor.addView(mFullscreenContainer, lp);
				mCustomView = view;
				// FullScreenManager.getInstance().request(null,
				// FullScreenManager.VIDEO_FULLSCREEN_REQUEST);
				TencentX5Activity.this
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				TencentX5Activity.this.getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
				Intent intent = getIntent();
				intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
						| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
				TencentX5Activity.this.getApplicationContext().startActivity(
						intent);
			}

			@Override
			public void onHideCustomView() {
				if (mCustomView == null) {
					return;
				}

				FrameLayout decor = (FrameLayout) TencentX5Activity.this
						.getWindow().getDecorView();
				decor.removeView(mFullscreenContainer);
				mFullscreenContainer.removeAllViews();
				mFullscreenContainer = null;
				TencentX5Activity.this
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				mCustomView = null;
				TencentX5Activity.this.getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
				mCustomViewCallback.onCustomViewHidden();
			}
		});

		mWebView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String arg0, String arg1, String arg2,
					String arg3, long arg4) {
				Log.d(TAG, "url: " + arg0);
				new AlertDialog.Builder(TencentX5Activity.this)
						.setTitle("是否下载")
						.setPositiveButton("yes",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Toast.makeText(
												TencentX5Activity.this,
												"fake message: i'll download...",
												1000).show();
									}
								})
						.setNegativeButton("no",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										Toast.makeText(
												TencentX5Activity.this,
												"fake message: refuse download...",
												1000).show();
									}
								})
						.setOnCancelListener(
								new DialogInterface.OnCancelListener() {

									@Override
									public void onCancel(DialogInterface dialog) {
										// TODO Auto-generated method stub
										Toast.makeText(
												TencentX5Activity.this,
												"fake message: refuse download...",
												1000).show();
									}
								}).show();
			}
		});
		// 各种设置
		if (mWebView.getX5WebViewExtension() != null) {
			Log.e("robins", "CoreVersion_FromSDK::"
					+ mWebView.getX5WebViewExtension().getQQBrowserVersion());
			mWebView.getX5WebViewExtension().setWebViewClientExtension(
					new ProxyWebViewClientExtension() {
						@Override
						public Object onMiscCallBack(String method,
								Bundle bundle) {
							if (method == "onSecurityLevelGot") {
								Toast.makeText(
										TencentX5Activity.this,
										"Security Level Check: \nit's level is "
												+ bundle.getInt("level"), 1000)
										.show();
							}
							return null;
						}
					});
		} else {
			Log.e("robins", "CoreVersion");
		}
		WebSettings webSetting = mWebView.getSettings();
		webSetting.setJavaScriptEnabled(true);
		webSetting.setAllowFileAccess(true);
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webSetting.setSupportZoom(true);
		webSetting.setBuiltInZoomControls(true);
		webSetting.setUseWideViewPort(true);
		webSetting.setSupportMultipleWindows(false);
		webSetting.setLoadWithOverviewMode(true);
		webSetting.setAppCacheEnabled(true);
		webSetting.setDatabaseEnabled(true);
		webSetting.setDomStorageEnabled(true);
		webSetting.setGeolocationEnabled(true);
		webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
		webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
		webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
		webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
				.getPath());
		// webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
		webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
		webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
		// webSetting.setPreFectch(true);
		long time = System.currentTimeMillis();
		if (mIntentUrl == null) {

		}
		Log.d("time-cost", "cost time: " + (System.currentTimeMillis() - time));
		CookieSyncManager.createInstance(this);
		CookieSyncManager.getInstance().sync();
	}

	private void initBtnListenser() {
		mBack = (ImageButton) findViewById(R.id.btnBack);
		mForward = (ImageButton) findViewById(R.id.btnForward);
		mRefresh = (ImageButton) findViewById(R.id.btnRefresh);
		mExit = (ImageButton) findViewById(R.id.btnExit);
		mHome = (ImageButton) findViewById(R.id.btnHome);
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mWebView != null && mWebView.canGoBack())
					mWebView.goBack();
			}
		});

		mForward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mWebView != null && mWebView.canGoForward())
					mWebView.goForward();
			}
		});

		mRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mWebView != null)
					mWebView.reload();
			}
		});

		mExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mWebView.getX5WebViewExtension() != null)
					mWebView.getX5WebViewExtension().onAppExit();
				// android.os.Process.killProcess(android.os.Process.myPid());

				finishTask();
			}
		});
		mHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!StringUtils.isBlank(mWebView.getUrl())
						&& !homepage.equals(mWebView.getUrl()))
					LoadUrl(homepage);
			}
		});

	}

	public void finishTask() {
		if (AppManager.getAppManager().isOneActivity())
			startActivity(new Intent(this, MainActivity.class));
		this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView != null && mWebView.canGoBack())
				mWebView.goBack();
			else
				return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult, requestCode:" + requestCode
				+ ",resultCode:" + resultCode);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				if (null != uploadFile) {
					Uri result = data == null || resultCode != RESULT_OK ? null
							: data.getData();
					uploadFile.onReceiveValue(result);
					uploadFile = null;
				}
				break;
			default:
				break;
			}
		} else if (resultCode == RESULT_CANCELED) {
			if (null != uploadFile) {
				uploadFile.onReceiveValue(null);
				uploadFile = null;
			}

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent");
		if (intent == null || mWebView == null || intent.getData() == null)
			return;
		mWebView.loadUrl(intent.getData().toString());
	}

	@Override
	protected void onDestroy() {
		if (mWebView != null)
			mWebView.destroy();
		super.onDestroy();
	}

	private class FullscreenHolder extends FrameLayout {

		public FullscreenHolder(Context context) {
			super(context);
			setBackgroundColor(context.getResources().getColor(
					android.R.color.holo_red_light));
		}

	}

}
