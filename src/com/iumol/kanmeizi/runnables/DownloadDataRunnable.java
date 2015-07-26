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

	private String httpGetString(String url) {
		if (mHandler != null)
			return HttpUtils.httpGetString(url);
		else
			return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		if (StringUtils.isBlank(mUrl) || mHandler == null)
			return;

		if (mUrl.indexOf("faceks.com/") > 0) {
			RegexFaceksImageUrl(mUrl, HttpUtils.httpGetStringPcAgent(mUrl));

		} else if (mUrl.indexOf("weibo.com/") > 0) {

			RegexWBImageUrl(mUrl, httpGetString(mUrl));

		} else if (mUrl.indexOf("dbmeinv.com/") > 0) {

			RegexDBImageUrl(mUrl, httpGetString(mUrl));

		} else if (mUrl.indexOf("weimei58.com/") > 0) {
			mUrl = mUrl.replace("&amp;", "&");
			String html = HttpUtils.httpGetStringPcAgent(mUrl);
			Regex58ImageUrl(mUrl, html);

		} else if (mUrl.indexOf("iumol.com/") > 0) {
			// mUrl = mUrl.replace("&amp;", "&");
			String html = httpGetString(mUrl);
			RegexTaobaoImageUrl(mUrl, html);

		} else {
			for (int i = 1; i <= 20; i++) {
				String url = mUrl + "/" + i;
				if (!RegexImageUrl(url, httpGetString(url)))
					break;
			}
		}

	}

	// 从分类获取图片List
	public boolean RegexFaceksImageUrl(String url, String str) {

		String reg = "<img src=\"(.*?)\"/>";
		Pattern p = Pattern.compile(reg);
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);
			while (m.find()) {
				String image_url = m.group(1);
				// 重复抓取
				String json;
				json = "{\"url\":\"" + url + "\",\"image_url\":\"" + image_url
						+ "\"}";
				AddAdapterData(json);
			}
		}

		return true;
	}

	public void RegexWBImageUrl(String url, String str) {

		String reg = "action-data=\"imgsrc=(.*?)\"";
		Pattern p = Pattern.compile(reg);
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);
			while (m.find()) {
				String image_url = m.group(1);
				String json;
				json = "{\"url\":\"" + url + "\",\"image_url\":\"" + image_url
						+ "\"}";
				AddAdapterData(json);
			}
		}

	}

	public void RegexDBImageUrl(String url, String str) {

		String reg = "<div class=\"topic-figure cc\">.*?<img src=\"(.*?)\"";
		str = str.replace("\n", "");
		Pattern p = Pattern.compile(reg);
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);
			while (m.find()) {
				String image_url = m.group(1);
				String json;
				json = "{\"url\":\"" + url + "\",\"image_url\":\"" + image_url
						+ "\"}";
				AddAdapterData(json);
			}
		}

	}

	public void RegexTaobaoImageUrl(String url, String str) {

		String reg = "image\":\"(.*?)\"";
		Pattern p = Pattern.compile(reg);
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);
			while (m.find()) {
				String image_url = m.group(1);
				String json;
				json = "{\"url\":\"" + url + "\",\"image_url\":\"" + image_url
						+ "\"}";
				AddAdapterData(json);
			}
		}

	}

	public void Regex58ImageUrl(String url, String str) {

		String reg = "zoomfile=\"(.*?)\"";
		Pattern p = Pattern.compile(reg);
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);
			while (m.find()) {
				String image_url = m.group(1);
				String json;
				json = "{\"url\":\"" + url
						+ "\",\"image_url\":\"http://www.weimei58.com/"
						+ image_url + "\"}";
				AddAdapterData(json);
			}
		}

	}

	// 从分类获取图片List
	public boolean RegexImageUrl(String url, String str) {

		String reg = "<p><a href=\".*?\" ><img src=\"(.*?)\" alt=\".*?\" />";
		Pattern p = Pattern.compile(reg);
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);
			while (m.find()) {
				String image_url = m.group(1);
				// 重复抓取
				String json;
				if (preImageUrl.equals(image_url))
					return false;
				preImageUrl = image_url;
				json = "{\"url\":\"" + url + "\",\"image_url\":\"" + image_url
						+ "\"}";
				AddAdapterData(json);
			}
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
