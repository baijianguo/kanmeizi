/*
 * caimao.browser Browser for Android
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

package com.iumol.kanmeizi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.iumol.kanmeizi.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

/**
 * Application utilities.
 */
public class ApplicationUtils {

	private static String mAdSweepString = null;
	private static int mFaviconSize = -1;
	private static int mImageButtonSize = -1;
	private static int mFaviconSizeForBookmarks = -1;

	public static int getImageButtonSize(Activity activity) {
		if (mImageButtonSize == -1) {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			switch (metrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				mImageButtonSize = 16;
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				mImageButtonSize = 32;
				break;
			case DisplayMetrics.DENSITY_HIGH:
				mImageButtonSize = 48;
				break;
			default:
				mImageButtonSize = 32;
			}
		}

		return mImageButtonSize;
	}

	/**
	 * Get the required size of the favicon, depending on current screen
	 * density.
	 * 
	 * @param activity
	 *            The current activity.
	 * @return The size of the favicon, in pixels.
	 */
	public static int getFaviconSize(Activity activity) {
		if (mFaviconSize == -1) {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			switch (metrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				mFaviconSize = 12;
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				mFaviconSize = 24;
				break;
			case DisplayMetrics.DENSITY_HIGH:
				mFaviconSize = 32;
				break;
			default:
				mFaviconSize = 24;
			}
		}

		return mFaviconSize;
	}

	/**
	 * Get the required size of the favicon, depending on current screen
	 * density.
	 * 
	 * @param activity
	 *            The current activity.
	 * @return The size of the favicon, in pixels.
	 */
	public static int getFaviconSizeForBookmarks(Activity activity) {
		if (mFaviconSizeForBookmarks == -1) {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			switch (metrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				mFaviconSizeForBookmarks = 12;
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				mFaviconSizeForBookmarks = 16;
				break;
			case DisplayMetrics.DENSITY_HIGH:
				mFaviconSizeForBookmarks = 24;
				break;
			default:
				mFaviconSizeForBookmarks = 16;
			}
		}

		return mFaviconSizeForBookmarks;
	}

	/**
	 * Display a standard yes / no dialog.
	 * 
	 * @param context
	 *            The current context.
	 * @param icon
	 *            The dialog icon.
	 * @param title
	 *            The dialog title.
	 * @param message
	 *            The dialog message.
	 * @param onYes
	 *            The dialog listener for the yes button.
	 */
	public static void showYesNoDialog(Context context, int icon, int title,
			int message, DialogInterface.OnClickListener onYes) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		builder.setIcon(icon);
		builder.setTitle(context.getResources().getString(title));
		builder.setMessage(context.getResources().getString(message));

		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton(
				context.getResources().getString(R.string.Commons_Yes), onYes);
		builder.setNegativeButton(
				context.getResources().getString(R.string.Commons_No),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Display a continue / cancel dialog.
	 * 
	 * @param context
	 *            The current context.
	 * @param icon
	 *            The dialog icon.
	 * @param title
	 *            The dialog title.
	 * @param message
	 *            The dialog message.
	 * @param onContinue
	 *            The dialog listener for the continue button.
	 * @param onCancel
	 *            The dialog listener for the cancel button.
	 */
	public static void showContinueCancelDialog(Context context, int icon,
			String title, String message,
			DialogInterface.OnClickListener onContinue,
			DialogInterface.OnClickListener onCancel) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		builder.setIcon(icon);
		builder.setTitle(title);
		builder.setMessage(message);

		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton(
				context.getResources().getString(R.string.Commons_Continue),
				onContinue);
		builder.setNegativeButton(
				context.getResources().getString(R.string.Commons_Cancel),
				onCancel);
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Display a standard Ok dialog.
	 * 
	 * @param context
	 *            The current context.
	 * @param icon
	 *            The dialog icon.
	 * @param title
	 *            The dialog title.
	 * @param message
	 *            The dialog message.
	 */
	public static void showOkDialog(Context context, int icon, String title,
			String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setIcon(icon);
		builder.setTitle(title);
		builder.setMessage(message);

		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton(
				context.getResources().getString(R.string.Commons_Ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Display a standard Ok / Cancel dialog.
	 * 
	 * @param context
	 *            The current context.
	 * @param icon
	 *            The dialog icon.
	 * @param title
	 *            The dialog title.
	 * @param message
	 *            The dialog message.
	 * @param onYes
	 *            The dialog listener for the yes button.
	 */
	public static void showOkCancelDialog(Context context, int icon,
			String title, String message, DialogInterface.OnClickListener onYes) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		builder.setIcon(icon);
		builder.setTitle(title);
		builder.setMessage(message);

		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton(
				context.getResources().getString(R.string.Commons_Ok), onYes);
		builder.setNegativeButton(
				context.getResources().getString(R.string.Commons_Cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Show an error dialog.
	 * 
	 * @param context
	 *            The current context.
	 * @param title
	 *            The title string id.
	 * @param message
	 *            The message string id.
	 */
	public static void showErrorDialog(Context context, int title, int message) {
		new AlertDialog.Builder(context).setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(message)
				.setPositiveButton(R.string.Commons_Ok, null).show();
	}

	public static void showErrorDialog(Context context, int title,
			String message) {
		new AlertDialog.Builder(context).setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(message)
				.setPositiveButton(R.string.Commons_Ok, null).show();
	}

	/**
	 * Load a raw string resource.
	 * 
	 * @param context
	 *            The current context.
	 * @param resourceId
	 *            The resource id.
	 * @return The loaded string.
	 */
	private static String getStringFromRawResource(Context context,
			int resourceId) {
		String result = null;

		InputStream is = context.getResources().openRawResource(resourceId);
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} catch (IOException e) {
				Log.w("ApplicationUtils", String.format(
						"Unable to load resource %s: %s", resourceId,
						e.getMessage()));
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					Log.w("ApplicationUtils", String.format(
							"Unable to load resource %s: %s", resourceId,
							e.getMessage()));
				}
			}
			result = sb.toString();
		} else {
			result = "";
		}

		return result;
	}

	/**
	 * Load the AdSweep script if necessary.
	 * 
	 * @param context
	 *            The current context.
	 * @return The AdSweep script.
	 */
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
	 * Get the application version code.
	 * 
	 * @param context
	 *            The current context.
	 * @return The application version code.
	 */
	public static int getApplicationVersionCode(Context context) {

		int result = -1;

		try {

			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);

			result = info.versionCode;

		} catch (NameNotFoundException e) {
			Log.w("ApplicationUtils",
					"Unable to get application version: " + e.getMessage());
			result = -1;
		}

		return result;
	}

}
