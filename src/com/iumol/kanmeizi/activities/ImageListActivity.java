package com.iumol.kanmeizi.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import com.iumol.kanmeizi.bitmap.core.FinalBitmap.ImageLoadCompleteListener;

import com.iumol.kanmeizi.R;
import com.iumol.kanmeizi.adapters.SampleAdapter;
import com.iumol.kanmeizi.entity.MzituUrl;
import com.iumol.kanmeizi.grid.StaggeredGridView;
import com.iumol.kanmeizi.runnables.AndroidGetImageUrlRunnable;
import com.iumol.kanmeizi.util.KanMeiZiParseUtils;
import com.iumol.kanmeizi.util.SystemUtils;
import com.iumol.kanmeizi.util.ToastUtils;
import com.iumol.kanmeizi.bitmap.core.FinalBitmap;
import com.umeng.analytics.MobclickAgent;

@SuppressLint({ "NewApi", "CutPasteId" })
public class ImageListActivity extends Activity implements
		AbsListView.OnItemClickListener, AbsListView.OnScrollListener,
		ImageLoadCompleteListener {

	public static final String SAVED_DATA_KEY = "SAVED_DATA";
	private static final int FETCH_DATA_TASK_DURATION = 0;// 2000;

	private StaggeredGridView mGridView;
	private SampleAdapter mAdapter;
	private boolean mHasRequestedMore;
	private List<MzituUrl> mData;
	private int page = 1;
	private Handler mHandler = null;
	private FinalBitmap fb;
	private View loadView;

	private MzituUrl mzt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_image);

		if (null != getIntent().getSerializableExtra("mzt")) {
			mzt = (MzituUrl) getIntent().getSerializableExtra("mzt");
		} else {
			this.finish();
		}

		setTitle(getResources().getString(R.string.app_name));
		TextView titleView = (TextView) findViewById(R.id.app_title);
		titleView.setText(mzt.getTitle());

		ImageView btnMenu = (ImageView) findViewById(R.id.menu_btn);
		btnMenu.setImageDrawable(getResources().getDrawable(
				R.drawable.titlebar_back));
		btnMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishTask();
			}
		});

		mGridView = (StaggeredGridView) findViewById(R.id.grid_view);
		mGridView.setEmptyView(findViewById(android.R.id.empty));

		LayoutInflater layoutInflater = getLayoutInflater();
		loadView = layoutInflater.inflate(R.layout.loading_view, null);
		mGridView.addFooterView(loadView);

		mData = new LinkedList<MzituUrl>();
		// do we have saved data?

		fb = new FinalBitmap(this).init();// 必须调用init初始化FinalBitmap模块
		fb.setCompleteListener(this);

		mAdapter = new SampleAdapter(this);

		mGridView.setAdapter(mAdapter);
		mGridView.setOnScrollListener(this);
		mGridView.setOnItemClickListener(this);
		initHandler();
		fetchData();

	}

	private void fillAdapter() {

		if (SystemUtils.isNetWorkConnect(this)) {
			AndroidGetImageUrlRunnable getlistitem = new AndroidGetImageUrlRunnable(
					mHandler, mzt.getUrl(), page++);
			new Thread(getlistitem).start();
		} else {
			ToastUtils.show(this, "网络连接错误！");

		}

	}

	private void fetchData() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				if (mAdapter.isEmpty())
					SystemClock.sleep(FETCH_DATA_TASK_DURATION);
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				fillAdapter();
			}
		}.execute();
	}

	private void onLoadMoreItems() {

		// stash all the data in our backing store
		fetchData();
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				String val = data.getString("json_str");

				// TODO 简析json数据
				LinkedList<MzituUrl> list = null;
				list = KanMeiZiParseUtils.ParseJsonToList(val);

				if (null == list) {
					mHasRequestedMore = false;
					return;
				} else {
					// mAdapter.addAll(list);

					for (MzituUrl mzt : list) {
						fb.display(mzt);
					}

				}
				mHasRequestedMore = false;
			}

		};
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {

		Intent mIntent = new Intent(this, ImagePagerActivity.class);
		Bundle mBundle = new Bundle();
		MzituUrl mzt = mData.get(position);
		if (null != mzt) {
			mBundle.putSerializable("mzt", mzt);
			mIntent.putExtras(mBundle);
			startActivity(mIntent);
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		// TODO Auto-generated method stub
		if (!mHasRequestedMore) {
			int lastInScreen = firstVisibleItem + visibleItemCount;
			if (visibleItemCount == totalItemCount
					|| (firstVisibleItem != 0 && lastInScreen >= totalItemCount)) {
				mHasRequestedMore = true;
				onLoadMoreItems();
			}
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

	@Override
	public synchronized void onLoadComplete(Bitmap bitmap, MzituUrl mzt) {
		// TODO Auto-generated method stub
		mData.add(mzt);
		mAdapter.add(mzt);
		mAdapter.notifyDataSetChanged();
	}

	public void finishTask() {

		this.finish();
	}
}
