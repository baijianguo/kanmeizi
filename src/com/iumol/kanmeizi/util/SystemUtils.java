package com.iumol.kanmeizi.util;

import android.R.integer;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * SystemUtils
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-15
 */
public class SystemUtils {

	/**
	 * recommend default thread pool size according to system available
	 * processors, {@link #getDefaultThreadPoolSize()}
	 **/
	public static final int DEFAULT_THREAD_POOL_SIZE = getDefaultThreadPoolSize();

	/**
	 * get recommend default thread pool size
	 * 
	 * @return if 2 * availableProcessors + 1 less than 8, return it, else
	 *         return 8;
	 * @see {@link #getDefaultThreadPoolSize(int)} max is 8
	 */
	public static int getDefaultThreadPoolSize() {
		return getDefaultThreadPoolSize(8);
	}

	/**
	 * get recommend default thread pool size
	 * 
	 * @param max
	 * @return if 2 * availableProcessors + 1 less than max, return it, else
	 *         return max;
	 */
	public static int getDefaultThreadPoolSize(int max) {
		int availableProcessors = 2 * Runtime.getRuntime()
				.availableProcessors() + 1;
		return availableProcessors > max ? max : availableProcessors;
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics metric = new DisplayMetrics();
		metric = context.getResources().getDisplayMetrics();
		return metric.widthPixels; // ��Ļ��ȣ����أ�
	}

	public static String getVersionName(Context context) {
		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String version = packInfo.versionName;
		return version;
	}

	public static boolean isNetWorkConnect(Context context) {
		// ��ȡ�ֻ��������ӹ�����󣨰�����wi-fi,net�����ӵĹ���
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// ��ȡ�������ӹ���Ķ���
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// �жϵ�ǰ�����Ƿ��Ѿ�����
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("error", e.toString());
		}
		return false;
	}
}
