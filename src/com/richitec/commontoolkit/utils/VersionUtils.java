package com.richitec.commontoolkit.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class VersionUtils {

	// get current version code
	public static Integer currentVersionCode(Context context) {
		// define current version code
		Integer _currentVersionCode = -1;

		try {
			_currentVersionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return _currentVersionCode;
	}

	// get current version name
	public static String currentVersionName(Context context) {
		// define current version name
		String _currentVersionName = "";

		try {
			_currentVersionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
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
	
}
