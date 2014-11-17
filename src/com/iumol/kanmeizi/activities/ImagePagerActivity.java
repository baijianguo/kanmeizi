package com.iumol.kanmeizi.activities;

import java.io.File;
import java.util.LinkedList;
import com.iumol.kanmeizi.R;
import com.iumol.kanmeizi.adapters.UrlPagerAdapter;
import com.iumol.kanmeizi.entity.MzituUrl;
import com.iumol.kanmeizi.photoview.PhotoView;
import com.iumol.kanmeizi.runnables.DownloadDataRunnable;
import com.iumol.kanmeizi.runnables.SaveImageRunnable;
import com.iumol.kanmeizi.util.KanMeiZiParseUtils;
import com.iumol.kanmeizi.util.StringUtils;
import com.iumol.kanmeizi.util.SystemUtils;
import com.iumol.kanmeizi.util.ToastUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import com.umeng.analytics.MobclickAgent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ImagePagerActivity extends BaseActivity implements
		OnPageChangeListener {

	private ViewPager mViewPager = null;
	private SamplePagerAdapter pagerAdapter = null;
	DisplayImageOptions options;
	private Handler mHandler = null;
	private boolean isloading = false;
	private LinkedList<MzituUrl> list_url;
	private MzituUrl mzt = null;

	String iniUrl = "";
	int current_index = 0;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pager_image);

		int mCurrentItem = 0;

		if (null != getIntent().getSerializableExtra("mzt")) {
			mzt = (MzituUrl) getIntent().getSerializableExtra("mzt");
		} else {
			this.finish();
		}

		if (!StringUtils.isBlank(mzt.getTitle())) {
			TextView titleView = (TextView) findViewById(R.id.app_title);
			titleView.setText(mzt.getTitle());
		}

		ImageView btnMenu = (ImageView) findViewById(R.id.menu_btn);
		btnMenu.setImageDrawable(getResources().getDrawable(
				R.drawable.titlebar_back));
		btnMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		list_url = new LinkedList<MzituUrl>();
		mViewPager = (ViewPager) findViewById(R.id.pager);
		pagerAdapter = new SamplePagerAdapter();
		mViewPager.setAdapter(pagerAdapter);

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		initHandler();
		iniUrl = mzt.getUrl();
		if (StringUtils.isBlank(iniUrl)) {
			list_url.add(mzt);
			pagerAdapter.notifyDataSetChanged();
			mViewPager.setVisibility(View.VISIBLE);
		} else {
			loadingData();
		}
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setCurrentItem(mCurrentItem);

	}

	class SamplePagerAdapter extends PagerAdapter {

		String[] drawables = UrlPagerAdapter.images;

		private LayoutInflater inflater;

		SamplePagerAdapter() {

			inflater = getLayoutInflater();
		}

		@Override
		public int getCount() {

			// return drawables.length;
			return list_url.size();
		}

		public String getPostionUrl(int location) {
			return list_url.get(location).getImageUrl();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {

			View imageLayout = inflater.inflate(R.layout.item_pager_image,
					container, false);
			assert imageLayout != null;
			PhotoView imageView = (PhotoView) imageLayout
					.findViewById(R.id.image);

			final ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.loading);
			if (imageLoader.isInited())
				imageLoader.displayImage(list_url.get(position).getImageUrl(),
						imageView, options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								spinner.setVisibility(View.VISIBLE);
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								// String message = null;
								spinner.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								spinner.setVisibility(View.GONE);
							}
						});

			// imageView.setImageResource(sDrawables[position]);
			if (list_url.size() > 0 && list_url.size() % 20 == 0) {
				imageLoader.clearMemoryCache();
				imageLoader.clearDiskCache();
			}
			container.addView(imageLayout, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

			return imageLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

	}

	@Override
	public void onBackPressed() {
		imageLoader.stop();
		super.onBackPressed();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int index) {
		// TODO Auto-generated method stub
		// 加载数据
		current_index = index;
	}

	private void loadingData() {
		// TODO 下拉获取数据

		if (SystemUtils.isNetWorkConnect(this)) {
			if (Isloading())
				return;
			else
				Setloading(true);
			DownloadDataRunnable getlistitem = new DownloadDataRunnable(
					mHandler, iniUrl);
			new Thread(getlistitem).start();
		} else {
			ToastUtils.show(this, "网络连接错误！");
		}
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				String val = data.getString("json_str");

				// TODO 简析json数据
				MzituUrl mzt = KanMeiZiParseUtils.ParseJson(val);
				if (null != mzt) {
					list_url.add(mzt);
					pagerAdapter.notifyDataSetChanged();
					Setloading(false);
				}

				if (View.GONE == mViewPager.getVisibility())
					mViewPager.setVisibility(View.VISIBLE);
			}
		};
	}

	public boolean Isloading() {
		return isloading;
	}

	public void Setloading(boolean isloading) {
		this.isloading = isloading;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		MobclickAgent.onPause(this);
		super.onPause();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_clear_memory_cache:
			imageLoader.clearMemoryCache();
			return true;
		case R.id.item_clear_disc_cache:
			imageLoader.clearDiscCache();
			return true;
		case R.id.item_save_to_disc:
			SaveBitmap();
			return true;
		default:
			return false;
		}
	}

	private void SaveBitmap() {
		// TODO Auto-generated method stub

		String url = pagerAdapter.getPostionUrl(current_index);
		File file = (imageLoader.getDiskCache()).get(url);

		if (null != file) {
			SaveImageRunnable sbr = new SaveImageRunnable(url, file.getPath());
			new Thread(sbr).start();
			ToastUtils.show(this, "已保存到 kanmeizi/images/");
		}

	}
}
