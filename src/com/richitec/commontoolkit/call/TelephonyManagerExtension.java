package com.richitec.commontoolkit.call;

import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.richitec.commontoolkit.CTApplication;

public class TelephonyManagerExtension {

	private static final String LOG_TAG = TelephonyManagerExtension.class
			.getCanonicalName();

	// telephony manager
	private static volatile TelephonyManager _telephonyManager;

	// get telephony manager
	private static TelephonyManager getTelephonyManager() {
		// check telephony manager instance
		if (null == _telephonyManager) {
			synchronized (TelephonyManagerExtension.class) {
				if (null == _telephonyManager) {
					// init telephony manager instance
					_telephonyManager = (TelephonyManager) CTApplication
							.getContext().getSystemService(
									Context.TELEPHONY_SERVICE);
				}
			}
		}

		return _telephonyManager;
	}

	// reject incoming call
	public static void rejectIncomingCall() {
		try {
			// get get telephony interface method
			Method _getITelephonyMethod = TelephonyManager.class
					.getDeclaredMethod("getITelephony", (Class[]) null);

			// set get telephony interface method access permission
			_getITelephonyMethod.setAccessible(true);

			// get telephony interface object
			Object _ITelephonyObject = _getITelephonyMethod.invoke(
					getTelephonyManager(), (Object[]) null);

			// get end call method
			Method _endCallMethod = _ITelephonyObject.getClass()
					.getDeclaredMethod("endCall", (Class[]) null);

			// set end call method access permission
			_endCallMethod.setAccessible(true);

			// reject the incoming call
			_endCallMethod.invoke(_ITelephonyObject, (Object[]) null);
		} catch (SecurityException e) {
			Log.e(LOG_TAG,
					"Handle telephony manager with security error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Log.e(LOG_TAG,
					"Get method of 'getITelephony' or 'endCall' error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		} catch (Exception e) {
			Log.e(LOG_TAG,
					"Invoke method of 'getITelephony' or 'endCall' error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		}
	}

}
