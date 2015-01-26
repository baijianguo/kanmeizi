package com.iumol.kanmeizi.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.igexin.sdk.PushConsts;
import com.iumol.kanmeizi.activities.WebViewActivity;
import com.iumol.kanmeizi.util.StringUtils;

public class PushMeiziReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
		case PushConsts.GET_MSG_DATA:
			// 获取透传（payload）数据
			byte[] payload = bundle.getByteArray("payload");
			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");
			if (payload != null) {
				String data = new String(payload);
				Log.d("GetuiSdkDemo", "Got Payload:" + data);
				OpenWebview(context, data);
			}
			break;
		// 添加其他case
		// .........
		default:
			break;
		}
	}

	public void OpenWebview(Context context, String data) {
		if (StringUtils.isBlank(data))
			return;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(data);
			if (jsonObject != null && jsonObject.has("url")) {
				String url = jsonObject.getString("url");
				Intent it = new Intent(context, WebViewActivity.class);
				it.setData(Uri.parse(url));
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(it);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}