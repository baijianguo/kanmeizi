package com.iumol.kanmeizi.runnables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.iumol.kanmeizi.util.FileUtils;
import com.iumol.kanmeizi.util.StringUtils;

import android.graphics.Bitmap;
import android.os.Environment;

/**
 * Background DownloadRunnable.
 */

public class SaveImageRunnable implements Runnable {

	public String url = "";
	public String name = "";
	String oldpath;

	public SaveImageRunnable(String url, String path) {
		this.url = url;
		name = getFileNameFromUrl();
		oldpath = path;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		if (FileUtils.isFileExist(oldpath)) {
			FileUtils.copyFile(oldpath, getNewFile().getPath());
		}

	}

	private String getFileNameFromUrl() {

		String fileName = url.substring(url.lastIndexOf("/") + 1);

		int queryParamStart = fileName.indexOf("?");
		if (queryParamStart > 0) {
			fileName = fileName.substring(0, queryParamStart);
		}

		return fileName;
	}

	public void saveMyBitmap() {

	}

	public static File getImageFolder() {
		File root = Environment.getExternalStorageDirectory();
		if (root.canWrite()) {

			File folder = new File(root, FileUtils.APPLICATION_FOLDER);

			if (!folder.exists()) {
				folder.mkdir();
			}

			return folder;

		} else {
			return null;
		}
	}
	private File getNewFile() {

		File imageFolder = getImageFolder();

		if (imageFolder != null) {

			return new File(imageFolder, name);
		}
		return null;
	}

}
