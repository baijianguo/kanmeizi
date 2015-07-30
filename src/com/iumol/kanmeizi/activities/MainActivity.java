package com.iumol.kanmeizi.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.igexin.sdk.PushManager;
import com.iumol.kanmeizi.R;
import com.iumol.kanmeizi.dao.ImageClass;
import com.iumol.kanmeizi.entity.ImageReg;
import com.iumol.kanmeizi.util.HttpUtils;
import com.iumol.kanmeizi.util.ImageCacheManager;
import com.iumol.kanmeizi.util.StringUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity implements
		AbsListView.OnItemClickListener {

	private ListView mListView;
	private ClassAdapter mAdapter;
	private List<ImageReg> imagelist;
	// 左滑菜单
	private View mMenuBtn;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushManager.getInstance().initialize(this.getApplicationContext());
		UmengUpdateAgent.update(this);

		setContentView(R.layout.activity_main);

		setTitle(getResources().getString(R.string.app_name));

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mMenuBtn = findViewById(R.id.menu_btn);

		mMenuBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}
		});

		mListView = (ListView) findViewById(R.id.list_view);
		mListView.setEmptyView(findViewById(android.R.id.empty));

		// do we have saved data?
		initData();

		mAdapter = new ClassAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		initMenuListView();

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {

		MobclickAgent.onEvent(MainActivity.this, "list_" + position);
		ImageReg imageReg = imagelist.get(position);
		if (StringUtils.isBlank(imageReg.getLogo()))
			openWebView(imageReg.getUrl());
		else {
			openClassActivity(imageReg);
		}

	}

	public void openClassActivity(ImageReg imageReg) {

		Intent mIntent = new Intent(this, ImageListActivity.class);
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("ImageReg", imageReg);
		mIntent.putExtras(mBundle);
		startActivity(mIntent);

	}

	public void openAboutView() {

		Intent mIntent = new Intent(this, WelcomeActivity.class);
		mIntent.setFlags(1);
		startActivity(mIntent);
	}

	public void openWebView(String url) {

		Intent it = new Intent(this, WebViewActivity.class);
		if (!StringUtils.isBlank(url))
			it.putExtra("url", url);
		startActivity(it);
	}

	public void initData() {
		imagelist = new ArrayList<ImageReg>();
		ImageReg webapp = new ImageReg(1000, "http://www.iumol.com", "妹子导航");
		imagelist.add(webapp);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String httpUrl = "http://iumol.com/kmz/reglist.php";
				String result = HttpUtils.httpGetString(httpUrl);
				List<ImageReg> imageRegs = getImageRegs(result);
				if (imageRegs != null && imageRegs.size() > 0) {
					Message msg = Message.obtain();
					if (handler != null && msg != null) {
						msg.obj = imageRegs;
						msg.what = imageRegs.size();
						handler.sendMessage(msg);
					}
				} else {
					if (handler != null)
						handler.sendEmptyMessage(-1);
				}
			}
		}).start();

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				break;

			default:
				if (msg.obj != null) {
					imagelist.addAll((List<ImageReg>) msg.obj);
					mAdapter.notifyDataSetChanged();
				}
				break;
			}
		}

	};

	public List<ImageReg> getImageRegs(String str) {
		List<ImageReg> imageRegs = null;
		if (StringUtils.isBlank(str))
			return null;
		try {
			JSONObject json = new JSONObject(str);
			if (json != null && json.has("item")) {
				JSONArray imageRegArray = json.getJSONArray("item");
				imageRegs = new ArrayList<ImageReg>();
				if (imageRegArray != null) {
					for (int i = 0; i < imageRegArray.length(); i++) {
						JSONObject jsonImageReg = imageRegArray
								.getJSONObject(i);
						ImageReg imageReg = new ImageReg();
						if (jsonImageReg.has("id")) {
							imageReg.setId(jsonImageReg.getInt("id"));
						}
						if (jsonImageReg.has("logo")) {
							imageReg.setLogo(jsonImageReg.getString("logo"));
						}
						if (jsonImageReg.has("title")) {
							imageReg.setTitle(jsonImageReg.getString("title"));
						}
						if (jsonImageReg.has("url")) {
							imageReg.setUrl(jsonImageReg.getString("url"));
						}
						if (jsonImageReg.has("thum_reg")) {
							imageReg.setThumReg(jsonImageReg
									.getString("thum_reg"));
						}
						if (jsonImageReg.has("imgs_reg")) {
							imageReg.setImgsReg(jsonImageReg
									.getString("imgs_reg"));
						}
						if (jsonImageReg.has("attri_order")) {
							imageReg.setAttriOrder(jsonImageReg
									.getString("attri_order"));
						}
						if (jsonImageReg.has("details_loop_param")) {
							imageReg.setDetailsLoopParam(jsonImageReg
									.getString("details_loop_param"));
						}
						if (jsonImageReg.has("details_loop_count")) {
							imageReg.setDetailsLoopCount(jsonImageReg
									.getInt("details_loop_count"));
						}
						if (jsonImageReg.has("details_url_pre")) {
							imageReg.setDetailsUrlPre(jsonImageReg
									.getString("details_url_pre"));
						}
						if (jsonImageReg.has("agent_pc")) {
							imageReg.setAgentPc(jsonImageReg.getInt("agent_pc") == 0 ? false
									: true);
						}
						imageRegs.add(imageReg);
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageRegs;
	}

	public class ClassAdapter extends BaseAdapter {

		private final LayoutInflater mLayoutInflater;

		final class ViewHolder {
			ImageView imageview;
			TextView textview;
		}

		public ClassAdapter() {

			mLayoutInflater = LayoutInflater.from(MainActivity.this);

		}

		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent) {

			ViewHolder vh;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.list_item_class,
						parent, false);
				vh = new ViewHolder();
				vh.imageview = (ImageView) convertView.findViewById(R.id.image);
				vh.textview = (TextView) convertView
						.findViewById(R.id.class_text);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			ImageReg image = imagelist.get(position);
			if (StringUtils.isBlank(image.getLogo())) {
				vh.imageview.setImageDrawable(getResources().getDrawable(
						R.drawable.gengduo));
			} else {
				ImageCacheManager.getImageCache().get(image.getLogo(),
						vh.imageview);
			}

			vh.textview.setText(image.getTitle());
			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imagelist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		MobclickAgent.onPause(this);
		super.onPause();

	}

	private void initMenuListView() {
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// mPlanetTitles = getResources().getStringArray(R.array.planets_array);

		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.list_item, ImageClass.MenuImageTitle));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Highlight the selected item, update the title, and close the
				MobclickAgent.onEvent(MainActivity.this, "menu_" + position);
				// drawer
				if ("关于我们".equals(ImageClass.MenuImageTitle[position]))
					openAboutView();

				else {
					openWebView(ImageClass.MenuImageUrl[position]);
				}
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		});
	}

}
