/*
 * Copyright 2014 trinea.cn All right reserved. This software is the confidential and proprietary information of
 * trinea.cn ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with trinea.cn.
 */
package com.iumol.kanmeizi.adapters;

import java.util.List;

import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iumol.kanmeizi.R;
import com.jakewharton.salvage.RecyclingPagerAdapter;

/**
 * ImagePagerAdapter
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-23
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter {

	private Context context;
	private int[] list;
	private final LayoutInflater mLayoutInflater;
	private int size;
	private boolean isInfiniteLoop;

	public ImagePagerAdapter(Context context, int[] list) {

		this.context = context;
		this.list = list;
		this.size = list.length;
		isInfiniteLoop = false;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// Infinite loop
		return isInfiniteLoop ? Integer.MAX_VALUE : list.length;
	}

	/**
	 * get really position
	 * 
	 * @param position
	 * @return
	 */
	private int getPosition(int position) {
		return isInfiniteLoop ? position % size : position;
	}

	@Override
	public View getView(int position, View view, ViewGroup container) {
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();

			view = holder.imageView = (ImageView) mLayoutInflater.inflate(
					R.layout.banner_imageview, container, false);
			holder.pos = position;
			view.setTag(holder);

		} else {

			holder = (ViewHolder) view.getTag();
		}

		holder.imageView.setImageDrawable(context.getResources().getDrawable(
				list[position]));

		return view;
	}

	private static class ViewHolder {
		int pos = 0;
		ImageView imageView;
	}

	/**
	 * @return the isInfiniteLoop
	 */
	public boolean isInfiniteLoop() {
		return isInfiniteLoop;
	}

	/**
	 * @param isInfiniteLoop
	 *            the isInfiniteLoop to set
	 */
	public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
		this.isInfiniteLoop = isInfiniteLoop;
		return this;
	}

}
