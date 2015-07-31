package com.iumol.kanmeizi.adapters;

import com.iumol.kanmeizi.R;
import com.iumol.kanmeizi.bitmap.core.FinalBitmap;
import com.iumol.kanmeizi.entity.MzituUrl;
import com.iumol.kanmeizi.util.StringUtils;
import com.iumol.kanmeizi.util.SystemUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/***
 * ADAPTER
 */

@SuppressLint({ "ViewHolder", "NewApi" })
public class SampleAdapter extends ArrayAdapter<MzituUrl> {

	private final LayoutInflater mLayoutInflater;
	private Context mContext;

	static class ViewHolder {
		ImageView imageview;
		TextView titleview;
	}

	public SampleAdapter(final Context context) {
		super(context, 0);
		mLayoutInflater = LayoutInflater.from(context);
		mContext = context;
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {

		ViewHolder vh;
		if (convertView == null) {

			convertView = mLayoutInflater.inflate(R.layout.list_item_sample,
					parent, false);
			vh = new ViewHolder();
			vh.imageview = (ImageView) convertView.findViewById(R.id.image);
			vh.titleview = (TextView) convertView
					.findViewById(R.id.image_title);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		MzituUrl mzt = getItem(position);
		String url = mzt.getImageUrl();
		String title = mzt.getTitle();

		if (!url.isEmpty() && null != FinalBitmap.mImageCache) {
			Bitmap bm = FinalBitmap.mImageCache.getBitmapFromMemCache(url);
			if (bm == null) {
				bm = FinalBitmap.mImageCache.getBitmapFromDiskCache(url);
				Log.d("SampleAdapter", "get bitmap from DiskCache " + url);
			}
			if (bm != null) {
				float width = SystemUtils.getScreenWidth(mContext);
				float column_count = mContext.getResources().getInteger(
						R.integer.column_count);
				// 屏幕宽度的
				float request_width = (width / column_count) / 1.2f;
				float bw = bm.getWidth();
				float scale = request_width / bw;

				if (Math.abs(request_width - bw) > 20) {
					Matrix matrix = new Matrix();
					matrix.postScale(scale, scale); // 长和宽放大缩小的比例
					bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
							bm.getHeight(), matrix, true);
				}
				vh.imageview.setImageBitmap(bm);
			}
		}
		if (!StringUtils.isBlank(title))
			vh.titleview.setText(title);

		return convertView;
	}
	/*
	 * private Bitmap getItemBitmap(int position) {
	 * 
	 * DisplayMetrics dm = new DisplayMetrics(); dm =
	 * mContext.getResources().getDisplayMetrics(); int sw = dm.widthPixels;
	 * 
	 * Bitmap bm = getItem(position); int w, h; w = bm.getWidth(); h =
	 * bm.getHeight();
	 * 
	 * int gap = Math.abs(w - sw); if (gap < 50) return bm;
	 * 
	 * Matrix matrix = new Matrix(); float ratio = (float) sw / (float) w;
	 * matrix.postScale(ratio, ratio); // 长和宽放大缩小的比例 bm =
	 * Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true); // w =
	 * resizeBmp.getWidth(); // h = resizeBmp.getHeight();
	 * 
	 * return bm; }
	 */
}