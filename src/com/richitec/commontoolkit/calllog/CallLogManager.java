package com.richitec.commontoolkit.calllog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;

import com.richitec.commontoolkit.CommonToolkitApplication;
import com.richitec.commontoolkit.calllog.CallLogBean.CallType;
import com.richitec.commontoolkit.utils.DeviceUtils;

public class CallLogManager {

	private static final String LOG_TAG = "CallLogManager";

	// call log sqlite query content resolver
	private static volatile ContentResolver _contentResolver;

	// get content resolver
	private static ContentResolver getContentResolver() {
		// check content resolver instance
		if (null == _contentResolver) {
			synchronized (CallLogManager.class) {
				if (null == _contentResolver) {
					// init content resolver object
					_contentResolver = CommonToolkitApplication.getContext()
							.getContentResolver();
				}
			}
		}

		return _contentResolver;
	}

	// get all call logs
	public static List<CallLogBean> getAllCallLogs() {
		// define return list
		List<CallLogBean> _ret = new ArrayList<CallLogBean>();

		// define constant
		final String[] _projection = new String[] { CallLog.Calls._ID,
				CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER,
				CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE };

		// use contentResolver to query calls table
		Cursor _callLogCursor = getContentResolver().query(
				CallLog.Calls.CONTENT_URI, _projection, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);

		// check call log cursor and traverse result
		if (null != _callLogCursor) {
			while (_callLogCursor.moveToNext()) {
				// // get call log id, callee name, callee phone, call date,
				// call
				// // duration and call type
				// Long _callLogId = _callLogCursor.getLong(_callLogCursor
				// .getColumnIndex(CallLog.Calls._ID));
				// String _calleeName = _callLogCursor.getString(_callLogCursor
				// .getColumnIndex(CallLog.Calls.CACHED_NAME));
				// String _calleePhone = _callLogCursor.getString(_callLogCursor
				// .getColumnIndex(CallLog.Calls.NUMBER));
				// Long _callDate = _callLogCursor.getLong(_callLogCursor
				// .getColumnIndex(CallLog.Calls.DATE));
				// Long _callDuration = _callLogCursor.getLong(_callLogCursor
				// .getColumnIndex(CallLog.Calls.DURATION));
				// Integer _callType = _callLogCursor.getInt(_callLogCursor
				// .getColumnIndex(CallLog.Calls.TYPE));
				//
				// // Log.d(LOG_TAG, "getAllCallLogs - callLogId: " + _callLogId
				// // + ", callee name: " + _calleeName + ", callee number: "
				// // + _calleePhone + ", call date: " + _callDate
				// // + ", call duration: " + _callDuration
				// // + " and call type: " + _callType);
				//
				// // new call log bean
				// CallLogBean _callLog = new CallLogBean();
				//
				// // get system current setting language
				// Locale _systemCurrentSettingLanguage = DeviceUtils
				// .getSystemCurrentSettingLanguage();
				//
				// // check callee phone, then update callee phone and name
				// // callee phone unknown
				// if (null == _calleePhone || _calleePhone.startsWith("-")) {
				// _calleePhone = "";
				//
				// // check callee name and update callee name
				// if (null == _calleeName
				// || _calleeName.trim().equalsIgnoreCase("")) {
				// _calleeName = Locale.SIMPLIFIED_CHINESE
				// .equals(_systemCurrentSettingLanguage) ? "未知号码"
				// : Locale.TRADITIONAL_CHINESE
				// .equals(_systemCurrentSettingLanguage) ? "未知號碼"
				// : "Unknown Phone";
				// }
				// } else {
				// // check callee name and update callee name
				// if (null == _calleeName
				// || _calleeName.trim().equalsIgnoreCase("")) {
				// _calleeName = Locale.SIMPLIFIED_CHINESE
				// .equals(_systemCurrentSettingLanguage) ? "未知联系人"
				// : Locale.TRADITIONAL_CHINESE
				// .equals(_systemCurrentSettingLanguage) ? "未知聯繫人"
				// : "Unknown";
				// }
				// }
				//
				// // set call log id, callee name, callee phone, call date,
				// call
				// // duration and call type
				// _callLog.setCallLogId(_callLogId);
				// _callLog.setCalleeName(_calleeName);
				// _callLog.setCalleePhone(_calleePhone);
				// _callLog.setCallDate(_callDate);
				// _callLog.setCallDuration(_callDuration);
				// _callLog.setCallType(getCallType(_callType));
				//
				// // add call log to return result
				// _ret.add(_callLog);

				_ret.add(getCallLogFromCursor(_callLogCursor));
			}

			// close call log cursor
			_callLogCursor.close();
		}

		return _ret;
	}

	// insert call log and return new added call log id
	public static Long insertCallLog(String calleeName, String calleePhone) {
		// define new added call log id
		Long _ret;

		// new call log values
		ContentValues _callLogValues = new ContentValues();

		// set call log callee phone, call date, call duration, call
		// type(outgoing call), call new flag, callee name and call phone type
		_callLogValues.put(CallLog.Calls.NUMBER, calleePhone);
		_callLogValues.put(CallLog.Calls.DATE, System.currentTimeMillis());
		_callLogValues.put(CallLog.Calls.DURATION, 0);
		_callLogValues.put(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);
		_callLogValues.put(CallLog.Calls.NEW, 1);
		_callLogValues.put(CallLog.Calls.CACHED_NAME, calleeName);
		_callLogValues.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);

		// inert new added call log values to calls table
		Uri _newAddedCallLogUri = getContentResolver().insert(
				CallLog.Calls.CONTENT_URI, _callLogValues);

		// reset return new added call log id
		_ret = ContentUris.parseId(_newAddedCallLogUri);

		Log.d(LOG_TAG, "Insert new call log, call log id: " + _ret
				+ ", callee number: " + calleePhone + ", call date: "
				+ _callLogValues.getAsLong(CallLog.Calls.DATE)
				+ ", callee name: " + calleeName);

		return _ret;
	}

	// update call log call duration with call log id
	public static void updateCallLog(Long callLogId,
			Map<String, String> updateValues) {
		// define constant
		final String _where = CallLog.Calls._ID + "=?";
		final String[] _selectionArgs = new String[] { Long.toString(callLogId) };

		// check call log id and update values map
		if (null != callLogId && callLogId >= 1 && null != updateValues) {
			// generate new call log values
			ContentValues _callLogValues = new ContentValues();

			// parse update values map
			try {
				// get update call duration
				Long _updateCallLogCallDuration = Long.parseLong(updateValues
						.get(CallLog.Calls.DURATION));

				// update call log call duration
				_callLogValues.put(CallLog.Calls.DURATION,
						_updateCallLogCallDuration);
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Parse call duration string to integer error, duration = "
								+ updateValues.get(CallLog.Calls.DURATION)
								+ " and exception = " + e.getMessage());
			}

			Log.d(LOG_TAG,
					"Update call log, call log id: " + callLogId
							+ " and update duration: "
							+ _callLogValues.getAsLong(CallLog.Calls.DURATION));

			// update calls table record with index
			getContentResolver().update(CallLog.Calls.CONTENT_URI,
					_callLogValues, _where, _selectionArgs);
		}
	}

	// get all call log query cursor
	public static Cursor getAllCallLogQueryCursor() {
		// define constant
		final String[] _projection = new String[] { CallLog.Calls._ID,
				CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER,
				CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE };

		return getContentResolver().query(CallLog.Calls.CONTENT_URI,
				_projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
	}

	// get call log object from query cursor
	public static CallLogBean getCallLogFromCursor(Cursor queryCursor) {
		// new call log bean
		CallLogBean _callLog = new CallLogBean();

		// get call log id, callee name, callee phone, call date, call
		// duration and call type
		Long _callLogId = queryCursor.getLong(queryCursor
				.getColumnIndex(CallLog.Calls._ID));
		String _calleeName = queryCursor.getString(queryCursor
				.getColumnIndex(CallLog.Calls.CACHED_NAME));
		String _calleePhone = queryCursor.getString(queryCursor
				.getColumnIndex(CallLog.Calls.NUMBER));
		Long _callDate = queryCursor.getLong(queryCursor
				.getColumnIndex(CallLog.Calls.DATE));
		Long _callDuration = queryCursor.getLong(queryCursor
				.getColumnIndex(CallLog.Calls.DURATION));
		Integer _callType = queryCursor.getInt(queryCursor
				.getColumnIndex(CallLog.Calls.TYPE));

		// Log.d(LOG_TAG, "get call log from cursor - callLogId: " + _callLogId
		// + ", callee name: " + _calleeName + ", callee number: "
		// + _calleePhone + ", call date: " + _callDate
		// + ", call duration: " + _callDuration + " and call type: "
		// + _callType);

		// get system current setting language
		Locale _systemCurrentSettingLanguage = DeviceUtils
				.getSystemCurrentSettingLanguage();

		// check callee phone, then update callee phone and name
		// callee phone unknown
		if (null == _calleePhone || _calleePhone.startsWith("-")) {
			_calleePhone = "";

			// check callee name and update callee name
			if (null == _calleeName || _calleeName.trim().equalsIgnoreCase("")) {
				_calleeName = Locale.SIMPLIFIED_CHINESE
						.equals(_systemCurrentSettingLanguage) ? "未知号码"
						: Locale.TRADITIONAL_CHINESE
								.equals(_systemCurrentSettingLanguage) ? "未知號碼"
								: "Unknown Phone";
			}
		} else {
			// check callee name and update callee name
			if (null == _calleeName || _calleeName.trim().equalsIgnoreCase("")) {
				_calleeName = Locale.SIMPLIFIED_CHINESE
						.equals(_systemCurrentSettingLanguage) ? "未知联系人"
						: Locale.TRADITIONAL_CHINESE
								.equals(_systemCurrentSettingLanguage) ? "未知聯繫人"
								: "Unknown";
			}
		}

		// set call log id, callee name, callee phone, call date, call
		// duration and call type
		_callLog.setCallLogId(_callLogId);
		_callLog.setCalleeName(_calleeName);
		_callLog.setCalleePhone(_calleePhone);
		_callLog.setCallDate(_callDate);
		_callLog.setCallDuration(_callDuration);
		_callLog.setCallType(getCallType(_callType));

		return _callLog;
	}

	// convert call type from integer to CallType
	private static CallType getCallType(Integer callType) {
		// define return value
		CallType _ret;

		// check call type
		switch (callType) {
		case CallLog.Calls.INCOMING_TYPE:
			_ret = CallType.INCOMING;
			break;

		case CallLog.Calls.OUTGOING_TYPE:
			_ret = CallType.OUTGOING;
			break;

		case CallLog.Calls.MISSED_TYPE:
		default:
			_ret = CallType.MISSED;
			break;
		}

		return _ret;
	}

}
