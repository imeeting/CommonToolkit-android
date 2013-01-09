package com.richitec.commontoolkit.utils;

import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

public class DeviceUtils {

	// get system current setting language. zh_CN, zh_TW etc.
	public static Locale getSystemCurrentSettingLanguage() {
		// define return result
		Locale _ret = Locale.ENGLISH;

		// get default locale
		Locale _defaultLocale = Locale.getDefault();

		// check language and country
		if (Locale.CHINESE.toString().equalsIgnoreCase(
				_defaultLocale.getLanguage())) {
			if ("CN".equalsIgnoreCase(_defaultLocale.getCountry())) {
				_ret = Locale.SIMPLIFIED_CHINESE;
			} else {
				_ret = Locale.TRADITIONAL_CHINESE;
			}
		}

		return _ret;
	}

	public static boolean isServiceRunning(Context context, Class clazz) {
		Log.d("commontoolkit", clazz.getCanonicalName());
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(
					clazz.getCanonicalName()) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
