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

package com.iumol.kanmeizi.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;

/**
 * Utilities for I/O reading and writing.
 */

public class IOUtils {

	private static String WEBCACHE_FOLDER = "webcache";
	private static String IMAGELOADERCACHE_FOLDER = "imageloadercache";

	public static File getWebCacheFolder(Context context) {
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? getExternalCacheDir(context)
				.getPath() : context.getCacheDir().getPath();

		return new File(cachePath + File.separator + WEBCACHE_FOLDER);
	}

	public static File getExternalCacheDir(Context context) {
		final String cacheDir = "/Android/data/" + context.getPackageName();
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
	}

	public static File getImageLoadCacheFolder(Context context) {
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? getExternalCacheDir(context)
				.getPath() : context.getCacheDir().getPath();

		return new File(cachePath + File.separator + IMAGELOADERCACHE_FOLDER);
	}
}
