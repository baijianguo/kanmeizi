package com.iumol.kanmeizi.runnables;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iumol.kanmeizi.entity.ImageReg;
import com.iumol.kanmeizi.util.HttpUtils;
import com.iumol.kanmeizi.util.StringUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Background DownloadRunnable.
 */
public class DownloadDataRunnable implements Runnable {

	public String mUrl = null;
	public Handler mHandler = null;
	private String preImageUrl = "";
	private ImageReg imageReg;

	public DownloadDataRunnable(Handler handler, ImageReg imageReg, String url) {
		mHandler = handler;
		this.imageReg = imageReg;
		mUrl = imageReg.getDetailsUrlPre() + url;
	}

	private String httpGetString(String url) {

		if (mHandler != null)
			if (imageReg.isAgentPc())
				return HttpUtils.httpGetStringPcAgent(url);
			else
				return HttpUtils.httpGetString(url);
		else
			return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		if (StringUtils.isBlank(mUrl) || mHandler == null)
			return;

		String images = "";
		int loopCount = imageReg.getDetailsLoopCount();
		String loopParam = imageReg.getDetailsLoopParam();
		if (loopCount > 0) {

			for (int i = 1; i <= loopCount; i++) {
				String url = mUrl
						+ loopParam.replace("{page_index}", String.valueOf(i));
				String image = RegexImageUrl(url, httpGetString(url));
				if (StringUtils.isBlank(image))
					break;

				if (StringUtils.isBlank(images))
					images = image;
				else {
					images += ",";
					images += image;
				}

			}
		} else
			images = RegexImageUrl(mUrl, httpGetString(mUrl));

		if (!StringUtils.isBlank(images)) {
			pushData(images, mUrl);
		}

		/*
		 * if (mUrl.indexOf("faceks.com/") > 0) { RegexFaceksImageUrl(mUrl,
		 * HttpUtils.httpGetStringPcAgent(mUrl));
		 * 
		 * } else if (mUrl.indexOf("weibo.com/") > 0) {
		 * 
		 * RegexWBImageUrl(mUrl, httpGetString(mUrl));
		 * 
		 * } else if (mUrl.indexOf("dbmeinv.com/") > 0) {
		 * 
		 * RegexDBImageUrl(mUrl, httpGetString(mUrl));
		 * 
		 * } else if (mUrl.indexOf("weimei58.com/") > 0) { mUrl =
		 * mUrl.replace("&amp;", "&"); String html =
		 * HttpUtils.httpGetStringPcAgent(mUrl); Regex58ImageUrl(mUrl, html);
		 * 
		 * } else if (mUrl.indexOf("iumol.com/") > 0) { // mUrl =
		 * mUrl.replace("&amp;", "&"); String html = httpGetString(mUrl);
		 * RegexTaobaoImageUrl(mUrl, html);
		 * 
		 * } else { for (int i = 1; i <= 20; i++) { String url = mUrl + "/" + i;
		 * if (!RegexImageUrl(url, httpGetString(url))) break; } }
		 */

	}

	public void pushData(String images, String url) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("details_url", url);
		map.put("images", images);
		String result = HttpUtils.httpPostString(
				"http://iumol.com/kmz/push.php", map);
		Log.i("DownloadDataRunnable", result);
	}

	// 从分类获取图片List
	public String RegexImageUrl(String url, String str) {

		String image = "";
		str = str.replace("\n", "");
		Pattern p = Pattern.compile(imageReg.getImgsReg());
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);

			while (m.find()) {
				String image_url = m.group(1);
				// 重复抓取
				String json;
				if (preImageUrl.equals(image_url))
					return null;
				preImageUrl = image_url;
				json = "{\"url\":\"" + url + "\",\"image_url\":\"" + image_url
						+ "\"}";
				AddAdapterData(json);
				if (StringUtils.isBlank(image))
					image = image_url;
				else {
					image += ",";
					image += image_url;
				}
			}
		}

		return image;
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

	private void AddAdapterData(String json) {

		Bundle data = new Bundle();
		data.putString("json_str", json);
		Message msg = new Message();
		msg.setData(data);
		mHandler.sendMessage(msg);
	}
}
