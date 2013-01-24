package com.richitec.commontoolkit.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.richitec.commontoolkit.CTApplication;

public class CommonUtils {

	private static final String LOG_TAG = CommonUtils.class.getCanonicalName();

	// convert array to list
	public static List<?> array2List(Object[] array) {
		List<Object> _ret = new ArrayList<Object>();

		if (null != array) {
			for (int i = 0; i < array.length; i++) {
				_ret.add(array[i]);
			}
		}

		return _ret;
	}

	// check if intent available or not
	public static boolean isIntentAvailable(Intent intent) {
		// define return result
		boolean _ret = true;

		// get application context
		Context _appContext = CTApplication.getContext();

		// get and check intent resolve info list
		List<ResolveInfo> _resolveInfoList = _appContext.getPackageManager()
				.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);

		// check
		if (_resolveInfoList.size() <= 0) {
			// update return result
			_ret = false;

			Log.e(LOG_TAG, "Intent = " + intent + " is not available");

			// // show intent is not available message
			// Toast.makeText(_appContext, "??", Toast.LENGTH_LONG).show();
		}

		return _ret;
	}

}
