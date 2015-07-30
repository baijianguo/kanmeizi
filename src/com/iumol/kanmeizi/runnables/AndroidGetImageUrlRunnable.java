package com.iumol.kanmeizi.runnables;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iumol.kanmeizi.entity.ImageReg;
import com.iumol.kanmeizi.util.HttpUtils;
import com.iumol.kanmeizi.util.StringUtils;

public class AndroidGetImageUrlRunnable implements Runnable {

	public int page = 0;
	public Handler mHandler = null;
	public String json = "";
	public ImageReg imageReg;

	public AndroidGetImageUrlRunnable(Handler handler, ImageReg imageReg,
			int page) {
		this.page = page;
		this.mHandler = handler;
		this.imageReg = imageReg;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (imageReg != null) {

			json = "{\"count\":\"6\",\"item\":[";
			String strHtml;
			String url = imageReg.getUrl().replace("{page_index}",
					String.valueOf(page));
			if (imageReg.isAgentPc())
				strHtml = HttpUtils.httpGetStringPcAgent(url);
			else
				strHtml = HttpGet(url);

			RegexUrlIndex(strHtml);

			json = json.substring(0, json.length() - 1);
			json += "]}";
		}
		Bundle data = new Bundle();
		data.putString("json_str", json);
		Message msg = new Message();
		msg.setData(data);
		mHandler.sendMessage(msg);
	}

	public void pushData(String thum, String title, String url) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("thum_url", thum);
		map.put("title", title);
		map.put("details_url", imageReg.getDetailsUrlPre() + url);
		map.put("reg_id", String.valueOf(imageReg.getId()));
		map.put("type_name", imageReg.getTitle());
		String result = HttpUtils.httpPostString(
				"http://iumol.com/kmz/push.php", map);
		Log.i("AndroidGetImageUrlRunnable", result);

	}

	// 从分类获取图片List
	public void RegexUrlIndex(String str) {

		str = str.replace("\n", "");
		str = str.replace("&nbsp;", " ");
		str = str.replace("<br />", " ");

		Pattern p = Pattern.compile(imageReg.getThumReg());
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);
			while (m.find()) {
				String url = "", title = "", image_url = "";
				String[] attriOrder = imageReg.getAttriOrder().split(",");
				for (int i = 0; i < attriOrder.length; i++) {
					if ("title".equals(attriOrder[i])) {
						title = m.group(i + 1);
					} else if ("thum".equals(attriOrder[i])) {
						image_url = m.group(i + 1);
					} else {
						url = m.group(i + 1);
					}
				}
				pushData(image_url, title, url);
				json += "{\"title\":\"" + title + "\",\"url\":\"" + url
						+ "\",\"image_url\":\"" + image_url + "\"},";
			}
		}
	}

	// 从分类获取图片List
	public void RegexTaobaoIndex(String str) {
		String reg = "id\":\"(.*?)\",\"title\":\"(.*?)\",\"thum\":\"(.*?)\"";
		Pattern p = Pattern.compile(reg);
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);

			while (m.find()) {

				String id = m.group(1);
				String title = m.group(2);
				String image_url = m.group(3);

				json += "{\"title\":\""
						+ title
						+ "\",\"url\":\"http://www.iumol.com/imagedetial.php?id="
						+ id + "\",\"image_url\":\"" + image_url + "\"},";

			}
		}
	}

	public void Regex58Index(String str) {

		String reg = "c cl.*?\n.*?href=\"(.*?)\".*?title=\"(.*?)\".*?\n.*?src=\"(.*?)\"";
		Pattern p = Pattern.compile(reg);
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);
			while (m.find()) {
				String url = m.group(1);
				String title = m.group(2);
				String image_url = m.group(3);
				json += "{\"title\":\"" + title + "\",\"url\":\"" + url
						+ "\",\"image_url\":\"http://www.weimei58.com/"
						+ image_url + "\"},";
			}

		}
	}

	// 从分类获取图片List
	public void RegexImageUrl(String url, String str) {

		String reg = "single-post-content.*src=\"(.*?)\".*?/></p></div>";
		Pattern p = Pattern.compile(reg);
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);
			while (m.find()) {
				String image_url = m.group(1);
				json += "{\"url\":\"" + url + "\",\"image_url\":\"" + image_url
						+ "\"},";
			}
		}
	}

	public String HttpGet(String url) {
		return HttpUtils.httpGetString(url);
	}

	// 从分类获取图片List
	public void RegexDBImageUrl(String str) {

		String reg = "<div class=\"img_single\">.*?<a href=\"(.*?)\" class=\"link\" target=\"_topic_detail\">.*?<img class=\"height_min\" title=\"(.*?)\".*?src=\"(.*?)\"";
		Pattern p = Pattern.compile(reg);
		str = str.replace("\n", "");
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);
			while (m.find()) {
				String url = m.group(1);
				String title = m.group(2);
				String image_url = m.group(3);
				json += "{\"title\":\"" + title + "\",\"image_url\":\""
						+ image_url + "\",\"url\":\"" + url + "\"},";
			}
		}
	}

	// 从微博获取list
	public void RegexTuxiuIndex(String str) {

		String reg = "long_img.*?src=\"(.*?)\".*?\n.*?\n.*?\n.*?show_link_id.*?href=\"(.*?)\".*?>(.*?)</a>";
		Pattern p = Pattern.compile(reg);
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);
			while (m.find()) {
				String image_url = m.group(1);
				String url = "http://vgirl.weibo.com/5show/";
				url += m.group(2);
				String title = m.group(3);
				json += "{\"title\":\"" + title + "\",\"image_url\":\""
						+ image_url + "\",\"url\":\"" + url + "\"},";
			}
		}
	}

	// 从微博获取list
	public void RegexWeiboIndex(String str) {

		String reg = "<li>.*?\n.*?href=\"(.*?)\".*?\n.*?src=\"(.*?)\".*?title=\"(.*?)\"";
		Pattern p = Pattern.compile(reg);
		if (!StringUtils.isBlank(str)) {
			Matcher m = p.matcher(str);
			while (m.find()) {
				String url = "http://vgirl.weibo.com";
				url += m.group(1);
				String image_url = m.group(2);
				String title = m.group(3);
				json += "{\"title\":\"" + title + "\",\"image_url\":\""
						+ image_url + "\",\"url\":\"" + url + "\"},";
			}
		}
	}

	public void RegexFaceksIndex(String strHtml) {
		String reg = "<div class=\"pic\">.*?<a class=\"img\" href=\"(.*?)\">.*?<img src=\"(.*?)\" />.*?<div class=\"text\"><p>(.*?)</p>";
		Pattern p = Pattern.compile(reg);
		strHtml = strHtml.replace("\n", "");
		strHtml = strHtml.replace("&nbsp;", " ");
		strHtml = strHtml.replace("<br />", " ");
		if (!StringUtils.isBlank(strHtml)) {
			Matcher m = p.matcher(strHtml);
			while (m.find()) {
				String url = m.group(1);
				String image_url = m.group(2);
				String title = m.group(3);

				json += "{\"title\":\"" + title + "\",\"url\":\"" + url
						+ "\",\"image_url\":\"" + image_url + "\"},";
			}
		}
	}
}
