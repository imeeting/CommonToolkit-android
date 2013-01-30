package com.richitec.commontoolkit.utils;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import com.richitec.commontoolkit.CTApplication;

public class DisplayScreenUtils {

	private static final String LOG_TAG = DisplayScreenUtils.class
			.getCanonicalName();

	// get screen width
	public static int screenWidth() {
		return ((WindowManager) CTApplication.getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
	}

	// get screen height
	public static int screenHeight() {
		return ((WindowManager) CTApplication.getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
	}

	// get screen density
	public static int screenDensity() {
		return CTApplication.getContext().getResources().getDisplayMetrics().densityDpi;
	}

	// get status bar height
	public static int statusBarHeight() {
		// define default status height
		int _statusBarHeight = 0;

		// define dimen class
		Class<?> _dimenClass;

		// get dimen class and set status bar height
		try {
			_dimenClass = Class.forName("com.android.internal.R$dimen");

			_statusBarHeight = CTApplication
					.getContext()
					.getResources()
					.getDimensionPixelSize(
							Integer.parseInt(_dimenClass
									.getField("status_bar_height")
									.get(_dimenClass.newInstance()).toString()));
		} catch (ClassNotFoundException e) {
			Log.e(LOG_TAG,
					"Get dimen class error, which is inner class of internal, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		} catch (InstantiationException e) {
			Log.e(LOG_TAG,
					"Create dimen instantiation error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.e(LOG_TAG,
					"Create dimen instantiation error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		} catch (SecurityException e) {
			Log.e(LOG_TAG,
					"Get status bar height firld of dimen error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			Log.e(LOG_TAG,
					"Get status bar height firld of dimen error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		}

		return _statusBarHeight;
	}

	// convert device-independent pixels to real pixels
	public static int dp2pix(float dpValue) {
		float _scaledDensity = CTApplication.getContext().getResources()
				.getDisplayMetrics().density;

		return (int) (dpValue * _scaledDensity);
	}

	// convert pixels to device-independent pixels
	public static int pix2dp(float pixValue) {
		float _scaledDensity = CTApplication.getContext().getResources()
				.getDisplayMetrics().density;

		return (int) (pixValue / _scaledDensity);
	}

	// convert scaled pixels to real pixels
	public static int sp2pix(float dpValue) {
		float _scaledDensity = CTApplication.getContext().getResources()
				.getDisplayMetrics().scaledDensity;

		return (int) (dpValue * _scaledDensity);
	}

	// convert pixels to scaled pixels
	public static int pix2sp(float pixValue) {
		float _scaledDensity = CTApplication.getContext().getResources()
				.getDisplayMetrics().scaledDensity;

		return (int) (pixValue / _scaledDensity);
	}

}
