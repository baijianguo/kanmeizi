/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.iumol.kanmeizi.view;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.iumol.kanmeizi.constant.AndroidConstants;
import com.iumol.kanmeizi.util.ImageCacheManager;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class UILApplication extends Application {

	@SuppressWarnings("unused")
	@Override
	public void onCreate() {

		if (Config.DEVELOPER_MODE
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyDeath().build());
		}
		super.onCreate();
		// ³õÊ¼»¯Í¼Æ¬»º´æ
		ImageCacheManager.getImageCache().initData(this,
				AndroidConstants.IMAGE_CACHE_TAG);
	}

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
}