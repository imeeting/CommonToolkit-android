package com.richitec.commontoolkit.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.richitec.commontoolkit.CTApplication;

public class NetworkInfoUtils {

	private static final String LOG_TAG = NetworkInfoUtils.class
			.getCanonicalName();

	// connectivity manager
	private static volatile ConnectivityManager _connectivityManager;

	// get connectivity manager
	private static ConnectivityManager getConnectivityManager() {
		// check connectivity manager instance
		if (null == _connectivityManager) {
			synchronized (NetworkInfoUtils.class) {
				if (null == _connectivityManager) {
					// init connectivity manager instance
					_connectivityManager = (ConnectivityManager) CTApplication
							.getContext().getSystemService(
									Context.CONNECTIVITY_SERVICE);
				}
			}
		}

		return _connectivityManager;
	}

	// is current active network available
	public static boolean isCurrentActiveNetworkAvailable() {
		// define network available, default is no
		boolean _networkAvailable = false;

		// get the currently active data network info
		NetworkInfo _currentActiveNetworkInfo = getConnectivityManager()
				.getActiveNetworkInfo();

		// check the currently active data network info
		if (null != _currentActiveNetworkInfo) {
			// reset network available
			_networkAvailable = _currentActiveNetworkInfo.isAvailable();
		}

		return _networkAvailable;
	}

	// get network type
	public static int getNetworkType() throws NoActiveNetworkException {
		// define network type, default is mobile
		Integer _networkType = null;

		// define and get current active network is or not available
		boolean _currentActiveNetworkAvailable = isCurrentActiveNetworkAvailable();

		// check current active network available
		if (_currentActiveNetworkAvailable) {
			// get the currently active data network type
			_networkType = getConnectivityManager().getActiveNetworkInfo()
					.getType();
		} else {
			Log.e(LOG_TAG,
					"Get current active network type unnecessary, current active network available = "
							+ _currentActiveNetworkAvailable);

			throw new NoActiveNetworkException(
					"Get current active network type unnecessary");
		}

		return _networkType;
	}

	// inner class
	// no active network exception
	public static class NoActiveNetworkException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 565416286766300952L;

		public NoActiveNetworkException(String causeOf) {
			super(causeOf + " , there is no active network currently");
		}

	}

}
