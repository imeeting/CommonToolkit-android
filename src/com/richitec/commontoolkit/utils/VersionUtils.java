package com.richitec.commontoolkit.utils;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.richitec.commontoolkit.CTApplication;

public class VersionUtils {

	private static final String LOG_TAG = VersionUtils.class.getCanonicalName();

	// get current version code
	public static Integer versionCode() {
		// define current version code
		Integer _currentVersionCode = -1;

		// get application context
		Context _appContext = CTApplication.getContext();

		try {
			_currentVersionCode = _appContext.getPackageManager()
					.getPackageInfo(_appContext.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG,
					"Get application version code error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		}

		return _currentVersionCode;
	}

	// get current version name
	public static String versionName() {
		// define current version name
		String _currentVersionName = "";

		// get application context
		Context _appContext = CTApplication.getContext();

		try {
			_currentVersionName = _appContext.getPackageManager()
					.getPackageInfo(_appContext.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG,
					"Get application version name error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		}

		return _currentVersionName;
	}

	public static boolean checkVersion = true;
	public static String localVersion = "";

	public static String serverVerion;
	public static String updateURL;

	/**
	 * compare server version and local version
	 * 
	 * @param verFromServer
	 * @param verLocal
	 * @return 1: server version is newer than local, 0: server version equals
	 *         to local, -1: server version is older than local
	 */
	public static int compareVersion(String verFromServer, String verLocal) {
		Log.d("commontoolkit", "version from server: " + verFromServer + " local version: " + verLocal);
		if (verFromServer == null || verLocal == null) {
			return 0;
		}

		if (verFromServer.equals(verLocal)) {
			return 0;
		}

		int result = 0;
		String[] serverSubVers = verFromServer.split("\\.");
		String[] localSubVers = verLocal.split("\\.");

		if (localSubVers == null || localSubVers.length == 0) {
			return 1;
		}

		if (serverSubVers == null || serverSubVers.length == 0) {
			return 0;
		}

//		if (serverSubVers.length != localSubVers.length) {
//			return 1;
//		}
		int i = 0;
		int len = Math.min(serverSubVers.length, localSubVers.length);
		for (i = 0; i < len; i++) {
			int sV = Integer.parseInt(serverSubVers[i]);
			int lV = Integer.parseInt(localSubVers[i]);
			int c = sV - lV;
			if (c > 0) {
				result = 1;
				break;
			} else if (c < 0) {
				result = -1;
				break;
			}
		}
		
		if (result == 0) {
			if (serverSubVers.length > localSubVers.length) {
				result = 1;
			} else if (serverSubVers.length < localSubVers.length) {
				result = -1;
			}
		}
		
//		if (i >= serverSubVers.length - 1) {
//			String tagServer = serverSubVers[serverSubVers.length - 1];
//			String tagLocal = localSubVers[localSubVers.length - 1];
//			if (tagServer.equals("a")) {
//				if (!tagLocal.equals("m")) {
//					result = 1;
//				}
//			} else if (tagServer.equals("b")) {
//				result = -1;
//			} else if (tagServer.equals("m")) {
//				result = 1;
//			}
//		}
		return result;
	}
	
	// compare version name
	public static int compareVersionName(String lhs, String rhs)
			throws VersionCompareException {
		// define return result
		int _ret = 0;

		// version name split word
		final String VERSIONNAME_SPLITWORD = ".";

		// check left and right handle side version name
		if (null == lhs || null == rhs
				|| ("".equalsIgnoreCase(lhs) && "".equalsIgnoreCase(rhs))) {
			Log.e(LOG_TAG,
					"Compare version name unnecessary, left version name = "
							+ lhs + " and right version name = " + rhs);

			throw new VersionCompareException(
					"unnecessary to compare, left handside = " + lhs
							+ " and right handside = " + rhs);
		}

		// get left and right handle side version name string list
		@SuppressWarnings("unchecked")
		List<String> _lhsIntList = (List<String>) CommonUtils
				.array2List(StringUtils.split(lhs, VERSIONNAME_SPLITWORD));
		@SuppressWarnings("unchecked")
		List<String> _rhsIntList = (List<String>) CommonUtils
				.array2List(StringUtils.split(rhs, VERSIONNAME_SPLITWORD));

		// get left and right handle side version name string list max count
		// and update each
		Integer _versionNameIntListMaxCount = Math.max(_lhsIntList.size(),
				_rhsIntList.size());
		if (_lhsIntList.size() < _versionNameIntListMaxCount) {
			for (int i = 0; i < _versionNameIntListMaxCount
					- _lhsIntList.size(); i++) {
				_lhsIntList.add("0");
			}
		} else if (_rhsIntList.size() < _versionNameIntListMaxCount) {
			for (int i = 0; i < _versionNameIntListMaxCount
					- _rhsIntList.size(); i++) {
				_rhsIntList.add("0");
			}
		}

		// compare version name
		for (int i = 0; i < _versionNameIntListMaxCount; i++) {
			// check sub version name
			if ((_ret = _lhsIntList.get(i).compareTo(_rhsIntList.get(i))) != 0) {
				// break immediately
				break;
			}
		}

		return _ret;
	}

	// inner class
	// version name compare exception
	public static class VersionCompareException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6622844578793344270L;

		public VersionCompareException(String reason) {
			super("Version compare error, the reason is " + reason);
		}

	}

}
