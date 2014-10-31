package com.iumol.kanmeizi.runnables;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.iumol.kanmeizi.util.HttpUtils;
import com.iumol.kanmeizi.util.StringUtils;

public class AndroidGetImageUrlRunnable implements Runnable {

	public static String[] mzituUrls = {
			"http://www.mzitu.com/mm/page/",
			"http://www.mzitu.com/tag/rosi/page/",
			"http://www.mzitu.com/tag/%E6%9C%89%E6%B2%9F%E5%BF%85%E7%81%AB/page/",
			"http://www.mzitu.com/tag/%E7%A7%80%E4%BA%BA%E6%A8%A1%E7%89%B9/page/",
			"http://www.mzitu.com/japan/page/",
			"http://www.mzitu.com/model/page/",
			"http://www.mzitu.com/taiwan/page/",
			"http://www.mzitu.com/tag/tuigirl/page/" };
	static String dbmzUrl = "http://www.dbmeizi.com/?p=";

	public int page = 0;
	public Handler mHandler = null;
	public String json = "";
	public String mUrl = "";

	public AndroidGetImageUrlRunnable(Handler handler, String url, int page) {
		this.page = page;
		this.mHandler = handler;
		this.mUrl = url;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		// String url = dbmzUrl + page;
		String url;
		if (!StringUtils.isBlank(mUrl)) {
			json = "{\"count\":\"6\",\"item\":[";
			if (mUrl.indexOf("dbmeizi.com/") > 0) {
				url = String.format(mUrl, page);
				String strHtml = HttpGet(url);
				RegexDBImageUrl(strHtml);
			} else if (mUrl.indexOf("mnchao.com/") > 0) {
				url = String.format(mUrl, page);
				String strHtml = HttpGet(url);
				RegexMNCIndex(strHtml);
			} else {
				url = mUrl + page;
				String strHtml = HttpGet(url);
				RegexUrlIndex(strHtml);
			}

			json = json.substring(0, json.length() - 1);
			json += "]}";
		}
		Bundle data = new Bundle();
		data.putString("json_str", json);
		Message msg = new Message();
		msg.setData(data);
		mHandler.sendMessage(msg);
	}

	// �ӷ����ȡͼƬList
	public void RegexUrlIndex(String str) {

		String reg = "post-title-link.*?href=\"(.*?)\".*?>(.*?)</a></h2></div>.*?post-content.*?src=\"(.*?)\".*?/>";
		String re = "\\[([^\\]]+)\\]";
		// String str = "[����]��abcdefg��[abc]";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		while (m.find()) {
			String url = m.group(1);
			String title = m.group(2);
			String image_url = m.group(3);
			// String html = HttpGet(url);
			// RegexImageUrl(url, html);
			json += "{\"title\":\"" + title + "\",\"url\":\"" + url
					+ "\",\"image_url\":\"" + image_url + "\"},";
		}
	}

	// �ӷ����ȡͼƬList
	public void RegexImageUrl(String url, String str) {

		String reg = "single-post-content.*src=\"(.*?)\".*?/></p></div>";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		while (m.find()) {
			String image_url = m.group(1);
			json += "{\"url\":\"" + url + "\",\"image_url\":\"" + image_url
					+ "\"},";
		}
	}

	public String HttpGet(String url) {
		return HttpUtils.httpGetString(url);
	}

	// �ӷ����ȡͼƬList
	public void RegexDBImageUrl(String str) {

		String reg = "data-bigimg=\"(.*?)\".*?data-title=\"(.*?)\".*?data-url";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		while (m.find()) {
			String image_url = m.group(1);
			String title = m.group(2);
			json += "{\"title\":\"" + title + "\",\"image_url\":\"" + image_url
					+ "\"},";
		}
	}

	public void RegexMNCIndex(String strHtml) {
		String reg = "<li>.*?href=\"(.*?)\\.html\".*?title=\"(.*?)\".*?src=\".*?upaiyun.com/(.*?)!smallimg\".*?<span>";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(strHtml);
		while (m.find()) {
			String url = m.group(1);
			String title = m.group(2);
			String image_url = m.group(3);

			try {
				image_url = java.net.URLEncoder.encode(image_url, "utf-8");
			} catch (UnsupportedEncodingException e) { // TODO Auto-generated
														// catch block
				e.printStackTrace();
			}

			json += "{\"title\":\"" + title
					+ "\",\"url\":\"http://www.mnchao.com/" + url
					+ "_%1$s.html\",\"image_url\":\"http://meinvchao.b0.upaiyun.com/"
					+ image_url + "!new\"},";
		}
	}
}