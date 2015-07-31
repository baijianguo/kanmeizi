/**
 * Copyright (c) 2012-2013, Michael Yang ��� (www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iumol.kanmeizi.bitmap.core;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.iumol.kanmeizi.bitmap.core.BitmapCache;
import com.iumol.kanmeizi.bitmap.core.BitmapCommonUtils;
import com.iumol.kanmeizi.bitmap.core.BitmapDisplayConfig;
import com.iumol.kanmeizi.bitmap.core.BitmapProcess;
import com.iumol.kanmeizi.bitmap.display.Displayer;
import com.iumol.kanmeizi.bitmap.display.SimpleDisplayer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.iumol.kanmeizi.bitmap.download.Downloader;
import com.iumol.kanmeizi.bitmap.download.SimpleHttpDownloader;
import com.iumol.kanmeizi.entity.MzituUrl;

@SuppressLint("NewApi")
public class FinalBitmap {

	private FinalBitmapConfig mConfig;
	public static BitmapCache mImageCache;

	private boolean mExitTasksEarly = false;
	private boolean mPauseWork = false;
	private final Object mPauseWorkLock = new Object();
	private Context mContext;

	private static ExecutorService bitmapLoadAndDisplayExecutor;

	public interface ImageLoadCompleteListener {
		void onLoadComplete(Bitmap bitmap, MzituUrl _mzt);
	}

	private ImageLoadCompleteListener completeListener;

	public FinalBitmap(Context context) {
		mContext = context;
		mConfig = new FinalBitmapConfig(context);

		configDiskCachePath(BitmapCommonUtils.getDiskCacheDir(context,
				"kanmeizi"));// ���û���·��
		configDisplayer(new SimpleDisplayer());// ������ʾ��
		configDownlader(new SimpleHttpDownloader());// ����������
	}

	/**
	 * ����ͼƬ���ڼ��ص�ʱ����ʾ��ͼƬ
	 * 
	 * @param bitmap
	 */
	public FinalBitmap configLoadingImage(Bitmap bitmap) {
		mConfig.defaultDisplayConfig.setLoadingBitmap(bitmap);
		return this;
	}

	/**
	 * ����ͼƬ���ڼ��ص�ʱ����ʾ��ͼƬ
	 * 
	 * @param bitmap
	 */
	public FinalBitmap configLoadingImage(int resId) {
		mConfig.defaultDisplayConfig.setLoadingBitmap(BitmapFactory
				.decodeResource(mContext.getResources(), resId));
		return this;
	}

	/**
	 * ����ͼƬ����ʧ��ʱ����ʾ��ͼƬ
	 * 
	 * @param bitmap
	 */
	public FinalBitmap configLoadfailImage(Bitmap bitmap) {
		mConfig.defaultDisplayConfig.setLoadfailBitmap(bitmap);
		return this;
	}

	/**
	 * ����ͼƬ����ʧ��ʱ����ʾ��ͼƬ
	 * 
	 * @param resId
	 */
	public FinalBitmap configLoadfailImage(int resId) {
		mConfig.defaultDisplayConfig.setLoadfailBitmap(BitmapFactory
				.decodeResource(mContext.getResources(), resId));
		return this;
	}

	/**
	 * ���ô��̻���·��
	 * 
	 * @param strPath
	 * @return
	 */
	public FinalBitmap configDiskCachePath(String strPath) {
		if (!TextUtils.isEmpty(strPath)) {
			mConfig.cachePath = strPath;
		}
		return this;
	}

	/**
	 * ���ô��̻���·��
	 * 
	 * @param strPath
	 * @return
	 */
	public FinalBitmap configDiskCachePath(File pathFile) {
		if (pathFile != null)
			configDiskCachePath(pathFile.getAbsolutePath());
		return this;
	}

	/**
	 * ����Ĭ��ͼƬ��С�ĸ߶�
	 * 
	 * @param bitmapHeight
	 */
	public FinalBitmap configBitmapMaxHeight(int bitmapHeight) {
		mConfig.defaultDisplayConfig.setBitmapHeight(bitmapHeight);
		return this;
	}

	/**
	 * ����Ĭ��ͼƬ��С�Ŀ��
	 * 
	 * @param bitmapHeight
	 */
	public FinalBitmap configBitmapMaxWidth(int bitmapWidth) {
		mConfig.defaultDisplayConfig.setBitmapWidth(bitmapWidth);
		return this;
	}

	/**
	 * ����������������ͨ��ftp��������Э��ȥ�����ȡͼƬ��ʱ�������������
	 * 
	 * @param downlader
	 * @return
	 */
	public FinalBitmap configDownlader(Downloader downlader) {
		mConfig.downloader = downlader;
		return this;
	}

	/**
	 * ������ʾ������������ʾ�Ĺ�������ʾ������
	 * 
	 * @param displayer
	 * @return
	 */
	public FinalBitmap configDisplayer(Displayer displayer) {
		mConfig.displayer = displayer;
		return this;
	}

	/**
	 * �����ڴ滺���С ����2MB������Ч
	 * 
	 * @param size
	 *            �����С
	 */
	public FinalBitmap configMemoryCacheSize(int size) {
		mConfig.memCacheSize = size;
		return this;
	}

	/**
	 * ����Ӧ�������APK���ڴ�İٷֱȣ����ȼ�����configMemoryCacheSize
	 * 
	 * @param percent
	 *            �ٷֱȣ�ֵ�ķ�Χ���� 0.05 �� 0.8֮��
	 */
	public FinalBitmap configMemoryCachePercent(float percent) {
		mConfig.memCacheSizePercent = percent;
		return this;
	}

	/**
	 * ���ô��̻����С 5MB ������Ч
	 * 
	 * @param size
	 */
	public FinalBitmap configDiskCacheSize(int size) {
		mConfig.diskCacheSize = size;
		return this;
	}

	/**
	 * ����ԭʼͼƬ�����С����ѹ�����棩
	 * 
	 * @param size
	 */
	public FinalBitmap configOriginalDiskCacheSize(int size) {
		mConfig.diskCacheSize = size;
		return this;
	}

	/**
	 * ���ü���ͼƬ���̲߳�������
	 * 
	 * @param size
	 */
	public FinalBitmap configBitmapLoadThreadSize(int size) {
		if (size >= 1)
			mConfig.poolSize = size;
		return this;
	}

	/**
	 * ����������뱻���ú� FinalBitmap ���ò�����Ч
	 * 
	 * @return
	 */
	public FinalBitmap init() {

		mConfig.init();

		BitmapCache.ImageCacheParams imageCacheParams = new BitmapCache.ImageCacheParams(
				mConfig.cachePath);
		if (mConfig.memCacheSizePercent > 0.05
				&& mConfig.memCacheSizePercent < 0.8) {
			imageCacheParams.setMemCacheSizePercent(mContext,
					mConfig.memCacheSizePercent);
		} else {
			if (mConfig.memCacheSize > 1024 * 1024 * 2) {
				imageCacheParams.setMemCacheSize(mConfig.memCacheSize);
			} else {
				// ����Ĭ�ϵ��ڴ滺���С
				imageCacheParams.setMemCacheSizePercent(mContext, 0.3f);
			}
		}
		if (mConfig.diskCacheSize > 1024 * 1024 * 5)
			imageCacheParams.setDiskCacheSize(mConfig.diskCacheSize);
		mImageCache = new BitmapCache(imageCacheParams);

		bitmapLoadAndDisplayExecutor = Executors.newFixedThreadPool(
				mConfig.poolSize, new ThreadFactory() {
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						// �����̵߳����ȼ������߳��Ⱥ�˳��ִ�У�����Խ�ߣ�����cpuִ�е�ʱ��Խ�ࣩ
						t.setPriority(Thread.NORM_PRIORITY - 1);
						return t;
					}
				});

		new CacheExecutecTask()
				.execute(CacheExecutecTask.MESSAGE_INIT_DISK_CACHE);

		return this;
	}

	public void reload(String uri, ImageView view) {
		reloadDisplay(uri, null, view);
	}

	public void display(MzituUrl mzt) {
		doDisplay(mzt, null);
	}

	private void reloadDisplay(String uri, BitmapDisplayConfig displayConfig,
			ImageView view) {
		if (TextUtils.isEmpty(uri)) {
			return;
		}

		if (displayConfig == null)
			displayConfig = mConfig.defaultDisplayConfig;

		Bitmap bitmap = null;

		if (mImageCache != null) {
			bitmap = mImageCache.getBitmapFromMemCache(uri);
		}

		if (bitmap != null) {

			view.setImageBitmap(bitmap);

		} else {

			final BitmapReoadTask task = new BitmapReoadTask(displayConfig,
					view);
			task.executeOnExecutor(bitmapLoadAndDisplayExecutor, uri);
		}
	}

	private void doDisplay(MzituUrl mzt, BitmapDisplayConfig displayConfig) {

		// Log.d("FinalBitmap", "Url " + url + " is reload!");
		if (TextUtils.isEmpty(mzt.getImageUrl())) {
			return;
		}

		if (displayConfig == null)
			displayConfig = mConfig.defaultDisplayConfig;

		Bitmap bitmap = null;

		if (mImageCache != null) {
			bitmap = mImageCache.getBitmapFromMemCache(mzt.getImageUrl());
		}

		if (bitmap != null) {

			getCompleteListener().onLoadComplete(bitmap, mzt);

		} else {

			final BitmapLoadTask task = new BitmapLoadTask(displayConfig, mzt);
			task.executeOnExecutor(bitmapLoadAndDisplayExecutor,
					mzt.getImageUrl());
		}
	}

	private void initDiskCacheInternal() {
		if (mImageCache != null) {
			mImageCache.initDiskCache();
		}
		if (mConfig != null && mConfig.bitmapProcess != null) {
			mConfig.bitmapProcess.initHttpDiskCache();
		}
	}

	private void clearCacheInternal() {
		if (mImageCache != null) {
			mImageCache.clearCache();
		}
		if (mConfig != null && mConfig.bitmapProcess != null) {
			mConfig.bitmapProcess.clearCacheInternal();
		}
	}

	private void flushCacheInternal() {
		if (mImageCache != null) {
			mImageCache.flush();
		}
		if (mConfig != null && mConfig.bitmapProcess != null) {
			mConfig.bitmapProcess.flushCacheInternal();
		}
	}

	private void closeCacheInternal() {
		if (mImageCache != null) {
			mImageCache.close();
			mImageCache = null;
		}
		if (mConfig != null && mConfig.bitmapProcess != null) {
			mConfig.bitmapProcess.clearCacheInternal();
		}
	}

	/**
	 * �������bitmap
	 * 
	 * @param data
	 * @return
	 */
	private Bitmap processBitmap(String uri, BitmapDisplayConfig config) {
		if (mConfig != null && mConfig.bitmapProcess != null) {
			return mConfig.bitmapProcess.processBitmap(uri, config);
		}
		return null;
	}

	public void setExitTasksEarly(boolean exitTasksEarly) {
		mExitTasksEarly = exitTasksEarly;
	}

	/**
	 * activity onResume��ʱ���������������ü���ͼƬ�̼߳���
	 */
	public void onResume() {
		setExitTasksEarly(false);
	}

	/**
	 * activity onPause��ʱ�����������������߳���ͣ
	 */
	public void onPause() {
		setExitTasksEarly(true);
		flushCache();
	}

	/**
	 * activity onDestroy��ʱ���������������ͷŻ���
	 */
	public void onDestroy() {
		closeCache();
	}

	/**
	 * �������
	 */
	public void clearCache() {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLEAR);
	}

	/**
	 * ˢ�»���
	 */
	public void flushCache() {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_FLUSH);
	}

	/**
	 * �رջ���
	 */
	public void closeCache() {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLOSE);
	}

	/**
	 * �˳����ڼ��ص��̣߳������˳���ʱ����ôʷ���
	 * 
	 * @param exitTasksEarly
	 */
	public void exitTasksEarly(boolean exitTasksEarly) {
		mExitTasksEarly = exitTasksEarly;
		if (exitTasksEarly)
			pauseWork(false);// ����ͣ���߳̽���
	}

	/**
	 * ��ͣ���ڼ��ص��̣߳�����listview����gridview���ڻ�����ʱ�����ôʷ���
	 * 
	 * @param pauseWork
	 *            trueֹͣ��ͣ�̣߳�false�����߳�
	 */
	public void pauseWork(boolean pauseWork) {
		synchronized (mPauseWorkLock) {
			mPauseWork = pauseWork;
			if (!mPauseWork) {
				mPauseWorkLock.notifyAll();
			}
		}
	}

	public ImageLoadCompleteListener getCompleteListener() {
		return completeListener;
	}

	public void setCompleteListener(ImageLoadCompleteListener completeListener) {
		this.completeListener = completeListener;
	}

	/**
	 * @title ����������첽����
	 * @description ��������
	 * @company ̽�������繤����(www.tsz.net)
	 * @author michael Young (www.YangFuhai.com)
	 * @version 1.0
	 * @created 2012-10-28
	 */
	private class CacheExecutecTask extends AsyncTask<Object, Void, Void> {
		public static final int MESSAGE_CLEAR = 0;
		public static final int MESSAGE_INIT_DISK_CACHE = 1;
		public static final int MESSAGE_FLUSH = 2;
		public static final int MESSAGE_CLOSE = 3;

		@Override
		protected Void doInBackground(Object... params) {
			switch ((Integer) params[0]) {
			case MESSAGE_CLEAR:
				clearCacheInternal();
				break;
			case MESSAGE_INIT_DISK_CACHE:
				initDiskCacheInternal();
				break;
			case MESSAGE_FLUSH:
				flushCacheInternal();
				break;
			case MESSAGE_CLOSE:
				closeCacheInternal();
				break;
			}
			return null;
		}
	}

	/**
	 * bitmap������ʾ���߳�
	 * 
	 * @author michael yang
	 */
	private class BitmapLoadTask extends AsyncTask<Object, Void, Bitmap> {
		private Object data;
		private final BitmapDisplayConfig displayConfig;
		private MzituUrl mzt;

		public BitmapLoadTask(BitmapDisplayConfig config, MzituUrl _mzt) {
			displayConfig = config;
			mzt = _mzt;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			data = params[0];
			final String dataString = String.valueOf(data);
			Bitmap bitmap = null;

			synchronized (mPauseWorkLock) {
				while (mPauseWork && !isCancelled()) {
					try {
						mPauseWorkLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			flushCache();
			if (mImageCache != null && !isCancelled() && !mExitTasksEarly) {
				bitmap = mImageCache.getBitmapFromDiskCache(dataString);
			}

			if (bitmap == null && !isCancelled() && !mExitTasksEarly) {
				bitmap = processBitmap(dataString, displayConfig);
				// int w, h;
				// w = bitmap.getWidth();
				// h = bitmap.getHeight();

			}

			if (bitmap != null && mImageCache != null) {
				mImageCache.addBitmapToCache(dataString, bitmap);
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			// Log.d("FinalBitmap", "onPostExecute");
			if (isCancelled() || mExitTasksEarly) {
				bitmap = null;
			}
			// �ж��̺߳͵�ǰ��imageview�Ƿ���ƥ��
			if (bitmap != null) {
				getCompleteListener().onLoadComplete(bitmap, mzt);

			} else if (bitmap == null) {

			}
		}

		@Override
		protected void onCancelled(Bitmap bitmap) {
			super.onCancelled(bitmap);
			synchronized (mPauseWorkLock) {
				mPauseWorkLock.notifyAll();
			}
		}

	}

	private class BitmapReoadTask extends AsyncTask<Object, Void, Bitmap> {
		private Object data;
		private final BitmapDisplayConfig displayConfig;
		private ImageView view;

		public BitmapReoadTask(BitmapDisplayConfig config, ImageView _view) {
			displayConfig = config;
			view = _view;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			data = params[0];
			final String dataString = String.valueOf(data);
			Bitmap bitmap = null;

			synchronized (mPauseWorkLock) {
				while (mPauseWork && !isCancelled()) {
					try {
						mPauseWorkLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}

			if (mImageCache != null && !isCancelled() && !mExitTasksEarly) {
				bitmap = mImageCache.getBitmapFromDiskCache(dataString);
			}

			if (bitmap == null && !isCancelled() && !mExitTasksEarly) {
				bitmap = processBitmap(dataString, displayConfig);
			}

			if (bitmap != null && mImageCache != null) {
				mImageCache.addBitmapToCache(dataString, bitmap);
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			// Log.d("FinalBitmap", "onPostExecute");
			if (isCancelled() || mExitTasksEarly) {
				bitmap = null;
			}
			// �ж��̺߳͵�ǰ��imageview�Ƿ���ƥ��
			if (bitmap != null) {
				view.setImageBitmap(bitmap);
			} else if (bitmap == null) {

			}
		}

		@Override
		protected void onCancelled(Bitmap bitmap) {
			super.onCancelled(bitmap);
			synchronized (mPauseWorkLock) {
				mPauseWorkLock.notifyAll();
			}
		}

	}

	/**
	 * @title ������Ϣ
	 * @description FinalBitmap��������Ϣ
	 * @company ̽�������繤����(www.tsz.net)
	 * @author michael Young (www.YangFuhai.com)
	 * @version 1.0
	 * @created 2012-10-28
	 */
	private class FinalBitmapConfig {

		public String cachePath;

		public Displayer displayer;
		public Downloader downloader;
		public BitmapProcess bitmapProcess;
		public BitmapDisplayConfig defaultDisplayConfig;
		public float memCacheSizePercent;// ����ٷֱȣ�androidϵͳ�����ÿ��apk�ڴ�Ĵ�С
		public int memCacheSize;// �ڴ滺��ٷֱ�
		public int diskCacheSize;// ���̰ٷֱ�
		public int poolSize = 3;// Ĭ�ϵ��̳߳��̲߳�������
		public int originalDiskCache = 20 * 1024 * 1024;// 20MB

		public FinalBitmapConfig(Context context) {
			defaultDisplayConfig = new BitmapDisplayConfig();

			defaultDisplayConfig.setAnimation(null);
			defaultDisplayConfig
					.setAnimationType(BitmapDisplayConfig.AnimationType.fadeIn);

			// ����ͼƬ����ʾ���ߴ磨Ϊ��Ļ�Ĵ�С,Ĭ��Ϊ��Ļ��ȵ�1/2��
			DisplayMetrics displayMetrics = context.getResources()
					.getDisplayMetrics();
			int defaultWidth = (int) Math.floor(displayMetrics.widthPixels);
			defaultDisplayConfig.setBitmapWidth(defaultWidth / 2);
			defaultDisplayConfig.setBitmapHeight(0);

		}

		public void init() {
			if (downloader == null)
				downloader = new SimpleHttpDownloader();

			if (displayer == null)
				displayer = new SimpleDisplayer();

			bitmapProcess = new BitmapProcess(downloader, cachePath,
					originalDiskCache);
		}

	}

}
