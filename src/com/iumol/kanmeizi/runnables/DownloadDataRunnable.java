package com.iumol.kanmeizi.runnables;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iumol.kanmeizi.util.HttpUtils;
import com.iumol.kanmeizi.util.StringUtils;

import android.R.integer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Background DownloadRunnable.
 */
public class DownloadDataRunnable implements Runnable {

	public String mUrl = null;
	public Handler mHandler = null;
	private String preImageUrl = "";

	public DownloadDataRunnable(Handler handler, String url) {
		mHandler = handler;
		mUrl = url;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		if (StringUtils.isBlank(mUrl) || mHandler == null)
			return;

		if (mUrl.indexOf("mnchao.com/") > 0) {
			for (int i = 1; i <= 10; i++) {
				String url = String.format(mUrl, i);
				if (!RegexMNCImageUrl(url, HttpUtils.httpGetString(url)))
					break;
			}
		} else {
			for (int i = 1; i <= 20; i++) {
				String url = mUrl + "/" + i;
				if (!RegexImageUrl(url, HttpUtils.httpGetString(url)))
					break;
			}
		}

	}

	// �ӷ����ȡͼƬList
	public boolean RegexMNCImageUrl(String url, String str) {

		String reg = "src=\".*?upaiyun.com/(.*?)\".*?alt=";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		while (m.find()) {
			String image_url = m.group(1);
			try {
				image_url = java.net.URLEncoder.encode(image_url, "utf-8");
			} catch (UnsupportedEncodingException e) { // TODO Auto-generated
														// catch block
				e.printStackTrace();
			}
			// �ظ�ץȡ
			String json;
			if (preImageUrl.equals(image_url))
				return false;
			preImageUrl = image_url;
			json = "{\"url\":\"" + url + "\",\"image_url\":\"http://meinvchao.b0.upaiyun.com/" + image_url
					+ "\"}";
			AddAdapterData(json);
		}

		return true;
	}

	// �ӷ����ȡͼƬList
	public boolean RegexImageUrl(String url, String str) {

		String reg = "single-post-content.*?src=\"(.*?)\".*?alt";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		while (m.find()) {
			String image_url = m.group(1);
			// �ظ�ץȡ
			String json;
			if (preImageUrl.equals(image_url))
				return false;
			preImageUrl = image_url;
			json = "{\"url\":\"" + url + "\",\"image_url\":\"" + image_url
					+ "\"}";
			AddAdapterData(json);
		}

		return true;
	}

	private void AddAdapterData(String json) {

		Bundle data = new Bundle();
		data.putString("json_str", json);
		Message msg = new Message();
		msg.setData(data);
		mHandler.sendMessage(msg);
	}
}
