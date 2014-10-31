package com.iumol.kanmeizi.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.LinkedList;
import java.util.List;

import com.iumol.kanmeizi.R;
import com.iumol.kanmeizi.dao.ImageClass;
import com.iumol.kanmeizi.entity.MzituUrl;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements
		AbsListView.OnItemClickListener {

	private ListView mListView;
	private ClassAdapter mAdapter;
	private List<MzituUrl> mData;

	// 左滑菜单
	private View mMenuBtn;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		initMzituList();
		mAdapter = new ClassAdapter(this, ImageClass.imageDrawable);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		initListView();

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		if ("图片导航".equals(ImageClass.MenuImageTitle[position]))
			openWebView();
		else {
			MzituUrl mzt = mData.get(position);
			openClassActivity(mzt);
		}

	}

	public void openClassActivity(MzituUrl mzt) {

		Intent mIntent = new Intent(this, ImageListActivity.class);
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("mzt", mzt);
		mIntent.putExtras(mBundle);
		startActivity(mIntent);

	}

	private void initListView() {
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
				// drawer
				if ("关于我们".equals(ImageClass.MenuImageTitle[position]))
					openAboutView();

				else {
					MzituUrl mzt = new MzituUrl(
							ImageClass.MenuImageTitle[position],
							ImageClass.MenuImageUrl[position], "");
					openClassActivity(mzt);
				}
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		});
	}

	public void openAboutView() {

		Intent mIntent = new Intent(this, WelcomeActivity.class);
		mIntent.setFlags(1);
		startActivity(mIntent);
	}

	public void openWebView() {

		Intent it = new Intent(this, WebViewActivity.class);
		startActivity(it);
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

	public void initMzituList() {
		mData = new LinkedList<MzituUrl>();

		for (String[] MzituData : ImageClass.MztImageInfo) {
			MzituUrl mzt = new MzituUrl(MzituData[0], MzituData[1], "");
			mData.add(mzt);
		}
	}

	public class ClassAdapter extends BaseAdapter {

		private static final String TAG = "SampleAdapter";
		int[] imageDrawable;
		private final LayoutInflater mLayoutInflater;
		private Context mContext = null;

		final class ViewHolder {
			ImageView imageview;
			TextView textview;
		}

		public ClassAdapter(final Context context, int[] imageDrawable) {

			mLayoutInflater = LayoutInflater.from(context);
			mContext = context;
			this.imageDrawable = imageDrawable;
		}

		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent) {

			ViewHolder vh;
			if (convertView == null) {
				ImageView image_view = null;
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

			/*
			 * Bitmap bmp = BitmapDecoder.decodeSampledBitmapFromResource(
			 * mContext.getResources(), imageDrawable[position],
			 * SystemUtils.getScreenWidth(mContext), 0);
			 * vh.imageview.setImageBitmap(bmp);
			 */
			vh.imageview.setImageDrawable(mContext.getResources().getDrawable(
					imageDrawable[position]));
			vh.textview.setText(mData.get(position).getTitle());
			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imageDrawable.length;
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
}
