package com.richitec.commontoolkit.utils;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import com.richitec.commontoolkit.CTApplication;

public class DisplayScreenUtils {

	private static final String LOG_TAG = DisplayScreenUtils.class
			.getCanonicalName();

	// window manager
	private static volatile WindowManager _windowManager;

	// get window manager
	private static WindowManager getWindowManager() {
		// check window manager
		if (null == _windowManager) {
			synchronized (DisplayScreenUtils.class) {
				if (null == _windowManager) {
					// init window manager object
					_windowManager = (WindowManager) CTApplication.getContext()
							.getSystemService(Context.WINDOW_SERVICE);
				}
			}
		}

		return _windowManager;
	}

	// get screen width
	public static int screenWidth() {
		return getWindowManager().getDefaultDisplay().getWidth();
	}

	// get screen height
	public static int screenHeight() {
		return getWindowManager().getDefaultDisplay().getHeight();
	}

	// get screen density, dots-per-inch
	public static int screenDensity() {
		return CTApplication.getContext().getResources().getDisplayMetrics().densityDpi;
	}

	// get the logical density of the display screen
	public static float screenLogicalDensity() {
		return CTApplication.getContext().getResources().getDisplayMetrics().density;
	}

	// get status bar height
	public static int statusBarHeight() {
		// define default status height
		int _statusBarHeight = 0;

		// get dimen class and set status bar height
		try {
			// get dimen class
			Class<?> _dimenClass = Class
					.forName("com.android.internal.R$dimen");

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
		} catch (NoSuchFieldException e) {
			Log.e(LOG_TAG,
					"Get status bar height firld of dimen error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		} catch (SecurityException e) {
			Log.e(LOG_TAG,
					"Handle dimen with security error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		} catch (Exception e) {
			Log.e(LOG_TAG,
					"Create dimen instantiation error, exception message = "
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
