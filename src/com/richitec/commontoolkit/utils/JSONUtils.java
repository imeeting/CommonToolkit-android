package com.richitec.commontoolkit.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONUtils {

	private static final String LOG_TAG = "JSONUtils";

	// get json value with key
	public static String getJsonString(JSONObject jsonObject, String key) {
		String _strinValue = null;

		// check json object
		if (null != jsonObject) {
			// get string value
			try {
				_strinValue = jsonObject.getString(key);
			} catch (JSONException e) {
				e.printStackTrace();

				Log.e(LOG_TAG,
						"Get json = " + jsonObject + " string with key = "
								+ key + " exception: " + e.getMessage());
			}
		} else {
			Log.e(LOG_TAG, "Get json string with key = " + key
					+ ", json object is null");
		}

		return _strinValue;
	}

}
