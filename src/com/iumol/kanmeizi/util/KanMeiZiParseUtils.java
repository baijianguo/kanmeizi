package com.iumol.kanmeizi.util;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iumol.kanmeizi.entity.MzituUrl;

public class KanMeiZiParseUtils {

	public static LinkedList<MzituUrl> ParseJsonToList(String json_str) {

		if (StringUtils.isBlank(json_str))
			return null;

		LinkedList<MzituUrl> listItems = new LinkedList<MzituUrl>();
		JSONObject jsonObject = null;
		int result_count = 0;

		// 转成json格式
		try {
			jsonObject = new JSONObject(json_str.trim());
			result_count = jsonObject.getInt("count");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (result_count == 0)
			return listItems;

		// 转成json数组
		JSONArray jsons = JSONUtils.getJSONArray(jsonObject, "item", null);

		int len = 0;

		if (null != jsons) {
			len = jsons.length();
			for (int i = 0; i < len; i++) {
				JSONObject json;
				try {
					json = jsons.getJSONObject(i);
					String title = "", url = "", image_url = "";
					if (json.has("title"))
						title = json.getString("title");
					if (json.has("url"))
						url = json.getString("url");
					if (json.has("image_url"))
						image_url = json.getString("image_url");
					MzituUrl mzt = new MzituUrl(title, url, image_url);
					listItems.add(mzt);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		return listItems;
	}

	public static String ParseListToJson(LinkedList<String> list) {
		String json = "{\"count\":10,\"item\":[";
		for (String str : list) {
			json += "{\"url\":\"" + str + "\"},";
		}
		json = json.substring(0, json.length() - 1);
		json += "]}";
		return json;
	}

	public static MzituUrl ParseJson(String json_str) {

		if (StringUtils.isBlank(json_str))
			return null;

		JSONObject jsonObject = null;

		// 转成json格式
		try {
			jsonObject = new JSONObject(json_str.trim());
			String url = jsonObject.getString("url");
			String image_url = jsonObject.getString("image_url");
			MzituUrl mzt = new MzituUrl(url, image_url);

			return mzt;
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;

	}

}
