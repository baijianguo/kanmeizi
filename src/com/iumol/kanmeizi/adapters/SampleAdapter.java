package com.iumol.kanmeizi.adapters;

import com.iumol.kanmeizi.R;
import com.iumol.kanmeizi.bitmap.core.FinalBitmap;
import com.iumol.kanmeizi.entity.MzituUrl;
import com.iumol.kanmeizi.util.StringUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
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

	static class ViewHolder {
		ImageView imageview;
		TextView titleview;
	}

	public SampleAdapter(final Context context) {
		super(context, 0);
		mLayoutInflater = LayoutInflater.from(context);

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
			if (null == bm) {
				bm = FinalBitmap.mImageCache.getBitmapFromDiskCache(url);
				Log.d("SampleAdapter", "get bitmap from DiskCache " + url);
			}
			vh.imageview.setImageBitmap(bm);
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
	 * matrix.postScale(ratio, ratio); // ���Ϳ��Ŵ���С�ı��� bm =
	 * Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true); // w =
	 * resizeBmp.getWidth(); // h = resizeBmp.getHeight();
	 * 
	 * return bm; }
	 */
}