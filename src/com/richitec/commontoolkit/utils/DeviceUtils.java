package com.richitec.commontoolkit.utils;

import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.richitec.commontoolkit.CTApplication;

public class DeviceUtils {

	private static final String LOG_TAG = DeviceUtils.class.getCanonicalName();

	// telephony manager
	private static volatile TelephonyManager _telephonyManager;

	// get telephony manager
	private static TelephonyManager getTelephonyManager() {
		// check telephony manager
		if (null == _telephonyManager) {
			synchronized (DeviceUtils.class) {
				if (null == _telephonyManager) {
					// init telephony manager object
					_telephonyManager = (TelephonyManager) CTApplication
							.getContext().getSystemService(
									Context.TELEPHONY_SERVICE);
				}
			}
		}

		return _telephonyManager;
	}

	// get system current setting language. zh_CN, zh_TW etc.
	public static Locale systemSettingLanguage() {
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
	
	// get device id. IMEI(15bits) for GSM or WCDMA, MEID(14bits) for CDMA
	public static String deviceId() {
		// return device id
		return getTelephonyManager().getDeviceId();
	}

	// get sim serial number, ICCID(20bits)
	public static String simSerialNumber() {
		// return sim serial number
		return getTelephonyManager().getSimSerialNumber();
	}

	// get android id
	public static String androidId() {
		// return android id
		return Secure.getString(
				CTApplication.getContext().getContentResolver(),
				Secure.ANDROID_ID);
	}

	// get wlan mac address
	public static String wlanMacAddress() {
		String _ret = null;

		// get wifi manager
		WifiManager _wifiManager = (WifiManager) CTApplication.getContext()
				.getSystemService(Context.WIFI_SERVICE);

		// check wifi manager and return waln mac address
		if (null != _wifiManager) {
			// check the wifi state
			if (!_wifiManager.isWifiEnabled()) {
				// open the wlan
				_wifiManager.setWifiEnabled(true);

				// get wlan mac address(has 3-5 seconds delay)
				_ret = _wifiManager.getConnectionInfo().getMacAddress();

				// close the wlan
				_wifiManager.setWifiEnabled(false);
			} else {
				// get wlan mac address
				_ret = _wifiManager.getConnectionInfo().getMacAddress();
			}
		}

		return _ret;
	}

	// get bluetooth mac address
	public static String bluetoothMacAddress() {
		String _ret = null;

		// get a handle to the default local Bluetooth adapter
		BluetoothAdapter _localBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		// check default local bluetooth adapter and return bluetooth mac
		// address
		if (null != _localBluetoothAdapter) {
			// check the bluetooth state
			if (!_localBluetoothAdapter.isEnabled()) {
				// open the bluetooth
				_localBluetoothAdapter.enable();

				// get default local bluetooth mac address(has 1-2 seconds
				// delay)
				_ret = _localBluetoothAdapter.getAddress();

				// close the bluetooth
				_localBluetoothAdapter.disable();
			} else {
				// get default local bluetooth mac address
				_ret = _localBluetoothAdapter.getAddress();
			}
		}

		return _ret;
	}

	// get combined unique id(had been md5)
	public static String combinedUniqueId() {
		// generate combined unique id using device id, pseudo unique id and
		// android id
		String _combinedUniqueId = deviceId() + pseudoUniqueId() + androidId();

		Log.d(LOG_TAG, "The combined unique id = " + _combinedUniqueId);

		return StringUtils.md5(_combinedUniqueId);
	}

	// generate pseudo unique id(13bits)
	private static String pseudoUniqueId() {
		// decimal
		final Integer DECIMAL = 10;

		// return the android device some common info(board, brand, CPU type +
		// ABI convention, device, display, host, id, manufacturer, model,
		// product, tags, type and user) combined string
		return new StringBuilder().append(Build.BOARD.length() % DECIMAL)
				.append(Build.BRAND.length() % DECIMAL)
				.append(Build.CPU_ABI.length() % DECIMAL)
				.append(Build.DEVICE.length() % DECIMAL)
				.append(Build.DISPLAY.length() % DECIMAL)
				.append(Build.HOST.length() % DECIMAL)
				.append(Build.ID.length() % DECIMAL)
				.append(Build.MANUFACTURER.length() % DECIMAL)
				.append(Build.MODEL.length() % DECIMAL)
				.append(Build.PRODUCT.length() % DECIMAL)
				.append(Build.TAGS.length() % DECIMAL)
				.append(Build.TYPE.length() % DECIMAL)
				.append(Build.USER.length() % DECIMAL).toString();
	}

}
