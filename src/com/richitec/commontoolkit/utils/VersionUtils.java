package com.richitec.commontoolkit.utils;

import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.R;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;

public class VersionUtils {

	private static final String LOG_TAG = VersionUtils.class.getCanonicalName();

	// application auto check and upgrade flag
	private static boolean _mAppAutoCheck7UpgradeFlag = true;

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

	// check application current version and upgrade the application with
	// application id, application version center url and upgrade mode
	public static void upgradeApp(Context activityContext, String appId,
			String appVCenterUrl, APPUPGRADEMODE upgradeMode) {
		// check activity context
		if (activityContext instanceof Activity) {
			// check application id
			if (null != appId) {
				// check and reset application version center url, if null use
				// common application version center url instead
				appVCenterUrl = null == appVCenterUrl ? activityContext
						.getResources().getString(
								R.string.application_upgradeVCenter_url)
						: appVCenterUrl;

				// check
				if (APPUPGRADEMODE.MANUAL == upgradeMode
						|| _mAppAutoCheck7UpgradeFlag) {
					// send get the application latest version from its version
					// center get request
					HttpUtils
							.getRequest(
									appVCenterUrl
											+ activityContext
													.getResources()
													.getString(
															R.string.get_appLatestVersion_url)
													.replace("***", appId),
									null,
									null,
									HttpRequestType.ASYNCHRONOUS,
									new GetApplicationLatestVersionHttpRequestListener(
											activityContext,
											upgradeMode,
											appVCenterUrl
													+ activityContext
															.getResources()
															.getString(
																	R.string.application_download_url)
															.replace("***",
																	appId)));
				}
			} else {
				Log.e(LOG_TAG, "Unknown application id, application id is null");

				// show the upgraded application id is null message
				Toast.makeText(activityContext,
						R.string.ct_toast_upgradedAppId_null,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Log.e(LOG_TAG,
					"Unable to upgrade the application, because there is no activity context to builder upgrade alert dialog");
		}
	}

	// check application current version and upgrade the application with
	// application id and upgrade mode
	public static void upgradeApp(Context activityContext, String appId,
			APPUPGRADEMODE upgradeMode) {
		// upgrade application with application id from application version
		// center
		upgradeApp(activityContext, appId, null, upgradeMode);
	}

	// check application current version and upgrade the application with
	// application id
	public static void upgradeApp(Context activityContext, String appId) {
		// auto upgrade application with application id from application version
		// center
		upgradeApp(activityContext, appId, null, APPUPGRADEMODE.AUTO);
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

	// application upgrade mode
	public static enum APPUPGRADEMODE {
		AUTO, MANUAL
	}

	// get the application latest version from its version center http request
	// listener
	static class GetApplicationLatestVersionHttpRequestListener extends
			OnHttpRequestListener {

		// activity context
		private Context _mContext;

		// application upgrade mode and download url
		private APPUPGRADEMODE _mAppUpgradeMode;
		private String _mAppDownloadUrl;

		public GetApplicationLatestVersionHttpRequestListener(Context context,
				APPUPGRADEMODE upgradeMode, String downloadUrl) {
			super();

			// save activity context, upgrade mode and download url
			_mContext = context;
			_mAppUpgradeMode = upgradeMode;
			_mAppDownloadUrl = downloadUrl;
		}

		@Deprecated
		public GetApplicationLatestVersionHttpRequestListener() {
			super();
		}

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			// check application upgrade mode and reset application auto check
			// and upgrade flag
			if (APPUPGRADEMODE.MANUAL != _mAppUpgradeMode) {
				_mAppAutoCheck7UpgradeFlag = false;
			}

			// get http response entity string json object
			JSONObject _respEntityStringJsonObject = JSONUtils
					.toJSONObject(responseResult.getResponseText());

			// get the upgraded application latest version name and upgrade
			// comment
			String _latestVersionName = JSONUtils
					.getStringFromJSONObject(
							_respEntityStringJsonObject,
							_mContext
									.getResources()
									.getString(
											R.string.rbgServer_getUpgradedAppLatestVersionReq_resp_version));
			String _upgradeComment = JSONUtils
					.getStringFromJSONObject(
							_respEntityStringJsonObject,
							_mContext
									.getResources()
									.getString(
											R.string.rbgServer_getUpgradedAppLatestVersionReq_resp_comment));

			try {
				// get the local and latest version compare result
				int _compareResult = compareVersionName(versionName(),
						_latestVersionName);

				// define a alertDialog builder
				final Builder ALERTDIALOG_BUILDER = new AlertDialog.Builder(
						_mContext);

				// check the upgraded application local version if it is the
				// latest or not
				if (0 == _compareResult) {
					// show application is the latest alert dialog
					ALERTDIALOG_BUILDER
							.setTitle(
									R.string.ct_appUpgrade6theLatestApp_alertDialog_title)
							.setMessage(
									_mContext
											.getResources()
											.getString(
													R.string.ct_theLatestApp_alertDialog_message)
											.replace("***", versionName()))
							.setNeutralButton(
									R.string.ct_theLatestApp_alertDialog_neutralButton_title,
									null).show();
				} else if (_compareResult < 0) {
					// show application upgrade alert dialog
					ALERTDIALOG_BUILDER
							.setTitle(
									R.string.ct_appUpgrade6theLatestApp_alertDialog_title)
							.setMessage(
									_mContext
											.getResources()
											.getString(
													R.string.ct_appUpgrade_alertDialog_message)
											.replaceFirst("\\*\\*\\*",
													_latestVersionName)
											.replace("***", _upgradeComment))
							.setPositiveButton(
									R.string.ct_appUpgrade_alertDialog_upgradeButton_title,
									new ApplicationUpgradeButtonOnClickListener())
							.setNegativeButton(
									R.string.ct_appUpgrade_alertDialog_remindLaterButton_title,
									null).show();
				} else {
					Log.e(LOG_TAG,
							"The upgraded application latest version is less than its local version");
				}
			} catch (VersionCompareException e) {
				Log.e(LOG_TAG,
						"Compare application version name error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			Log.e(LOG_TAG,
					"Get the application latest version from its version center failed, send get the application latest version get request failed");

			// show unable to get the upgraded application latest version from
			// its version center message
			Toast.makeText(_mContext,
					R.string.ct_toast_getUpgradedAppLatestVersion_failed,
					Toast.LENGTH_SHORT).show();
		}

		// inner class
		// application upgrade button on click listener
		class ApplicationUpgradeButtonOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// download the latest application from its version center
				_mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse(_mAppDownloadUrl)));
			}

		}

	}

}
