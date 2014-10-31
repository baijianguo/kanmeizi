/*
 * HuoSu Browser for Android
 * 
 * Copyright (C) 2010 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.iumol.kanmeizi.util;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Holder for animation objects.
 */
public final class AnimationManager {

	private static final int ANIMATION_DURATION = 250;

	public Animation getmShowSearchBoxAnimation() {
		return mShowSearchBoxAnimation;
	}

	private Animation mOptionMenuShowAnimation = null;

	private Animation mOptionMenuHideAnimation = null;

	private Animation mOutOptionShowAnimation = null;

	private Animation mOutOptionHideAnimation = null;

	private Animation mSwitchTurnCircleImgAnimation = null;

	private Animation mShowSearchBoxAnimation = null;
	private Animation mHideSearchBoxAnimation = null;

	/**
	 * Holder for singleton implementation.
	 */
	private static class AnimationManagerHolder {
		private static final AnimationManager INSTANCE = new AnimationManager();
	}

	/**
	 * Get the unique instance of the Controller.
	 * 
	 * @return The instance of the Controller
	 */
	public static AnimationManager getInstance() {
		return AnimationManagerHolder.INSTANCE;
	}

	/**
	 * Contructor.
	 */
	private AnimationManager() {

		mOptionMenuShowAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);

		mOptionMenuShowAnimation.setDuration(ANIMATION_DURATION);

		mOptionMenuHideAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 1.0f);

		mOptionMenuHideAnimation.setDuration(ANIMATION_DURATION);

		mOutOptionShowAnimation = new AlphaAnimation(0, 1);
		mOutOptionShowAnimation.setDuration(ANIMATION_DURATION);

		mOutOptionHideAnimation = new AlphaAnimation(1, 0);
		mOutOptionHideAnimation.setDuration(ANIMATION_DURATION);

		mSwitchTurnCircleImgAnimation = new RotateAnimation(0f, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mSwitchTurnCircleImgAnimation.setDuration(ANIMATION_DURATION * 6);

		mShowSearchBoxAnimation = new AlphaAnimation(0, 1);
		mShowSearchBoxAnimation.setDuration(ANIMATION_DURATION * 6);

		mHideSearchBoxAnimation = new AlphaAnimation(1, 0);
		mHideSearchBoxAnimation.setDuration(ANIMATION_DURATION * 6);

	}

	public Animation getmOptionMenuShowAnimation() {
		return mOptionMenuShowAnimation;
	}

	public Animation getmOptionMenuHideAnimation() {
		return mOptionMenuHideAnimation;
	}

	public Animation getmOutOptionShowAnimation() {
		return mOutOptionShowAnimation;
	}

	public Animation getmOutOptionHideAnimation() {
		return mOutOptionHideAnimation;
	}

	public Animation getSwitchTurnCircleImgAnimation() {
		return mSwitchTurnCircleImgAnimation;
	}

	public Animation getHideSearchBoxAnimation() {
		return mHideSearchBoxAnimation;
	}

	public Animation getShowSearchBoxAnimation() {
		return mShowSearchBoxAnimation;
	}
}
