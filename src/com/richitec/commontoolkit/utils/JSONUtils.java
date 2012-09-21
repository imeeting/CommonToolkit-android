package com.richitec.commontoolkit.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONUtils {

	private static final String LOG_TAG = "JSONUtils";

	// get json object value with key and value class name
	private static Object getJSONObjectValue(JSONObject jsonObject, String key,
			Class<? extends Object> valueClass) {
		Object _value = null;

		// check json object
		if (null != jsonObject) {
			// get value
			try {
				if (Integer.class == valueClass) {
					_value = jsonObject.getInt(key);
				} else if (Long.class == valueClass) {
					_value = jsonObject.getLong(key);
				} else if (Double.class == valueClass) {
					_value = jsonObject.getDouble(key);
				} else if (Boolean.class == valueClass) {
					_value = jsonObject.getBoolean(key);
				} else if (String.class == valueClass) {
					_value = jsonObject.getString(key);
				} else if (JSONObject.class == valueClass) {
					_value = jsonObject.getJSONObject(key);
				} else if (JSONArray.class == valueClass) {
					_value = jsonObject.getJSONArray(key);
				} else {
					_value = jsonObject.get(key);
				}
			} catch (JSONException e) {
				e.printStackTrace();

				Log.e(LOG_TAG,
						"Get json = " + jsonObject + " value with key = " + key
								+ " and value class = " + valueClass.getName()
								+ " exception: " + e.getMessage());
			}
		} else {
			Log.e(LOG_TAG, "Get json value with key = " + key
					+ " and value class = " + valueClass.getName()
					+ ", json object is null");
		}

		return _value;
	}

	// get integer from json object with key
	public static Integer getIntegerFromJSONObject(JSONObject jsonObject,
			String key) {
		return (Integer) getJSONObjectValue(jsonObject, key, Integer.class);
	}

	// get long from json object with key
	public static Long getLongFromJSONObject(JSONObject jsonObject, String key) {
		return (Long) getJSONObjectValue(jsonObject, key, Long.class);
	}

	// get double from json object with key
	public static Double getDoubleFromJSONObject(JSONObject jsonObject,
			String key) {
		return (Double) getJSONObjectValue(jsonObject, key, Double.class);
	}

	// get boolean from json object with key
	public static Boolean getBooleanFromJSONObject(JSONObject jsonObject,
			String key) {
		return (Boolean) getJSONObjectValue(jsonObject, key, Boolean.class);
	}

	// get string from json object with key
	public static String getStringFromJSONObject(JSONObject jsonObject,
			String key) {
		return (String) getJSONObjectValue(jsonObject, key, String.class);
	}

	// get json object from json object with key
	public static JSONObject getJSONObjectFromJSONObject(JSONObject jsonObject,
			String key) {
		return (JSONObject) getJSONObjectValue(jsonObject, key,
				JSONObject.class);
	}

	// get json array from json object with key
	public static JSONArray getJSONArrayFromJSONObject(JSONObject jsonObject,
			String key) {
		return (JSONArray) getJSONObjectValue(jsonObject, key, JSONArray.class);
	}

	// get object from json object with key
	public static Object getObjectFromJSONObject(JSONObject jsonObject,
			String key) {
		return getJSONObjectValue(jsonObject, key, Object.class);
	}

	// get json array object at index with object class name
	private static Object getJSONArrayObject(JSONArray jsonArray, int index,
			Class<? extends Object> objectClass) {
		Object _object = null;

		// check json array
		if (null != jsonArray) {
			// check json array bounds
			if (index < jsonArray.length()) {
				try {
					if (Integer.class == objectClass) {
						_object = jsonArray.getInt(index);
					} else if (Long.class == objectClass) {
						_object = jsonArray.getLong(index);
					} else if (Double.class == objectClass) {
						_object = jsonArray.getDouble(index);
					} else if (Boolean.class == objectClass) {
						_object = jsonArray.getBoolean(index);
					} else if (String.class == objectClass) {
						_object = jsonArray.getString(index);
					} else if (JSONObject.class == objectClass) {
						_object = jsonArray.getJSONObject(index);
					} else if (JSONArray.class == objectClass) {
						_object = jsonArray.getJSONArray(index);
					} else {
						_object = jsonArray.get(index);
					}
				} catch (JSONException e) {
					e.printStackTrace();

					Log.e(LOG_TAG, "Get json array = " + jsonArray
							+ " object at index = " + index
							+ " and object class = " + objectClass.getName()
							+ " exception: " + e.getMessage());
				}
			} else {
				Log.e(LOG_TAG, "Get json array object at index = " + index
						+ " and object class = " + objectClass.getName()
						+ ", index out of json array bounds");
			}
		} else {
			Log.e(LOG_TAG, "Get json array object at index = " + index
					+ " and object class = " + objectClass.getName()
					+ ", json array is null");
		}

		return _object;
	}

	// get integer from json array at index
	public static Integer getIntegerFromJSONArray(JSONArray jsonArray, int index) {
		return (Integer) getJSONArrayObject(jsonArray, index, Integer.class);
	}

	// get long from json array at index
	public static Long getLongFromJSONArray(JSONArray jsonArray, int index) {
		return (Long) getJSONArrayObject(jsonArray, index, Long.class);
	}

	// get double from json array at index
	public static Double getDoubleFromJSONArray(JSONArray jsonArray, int index) {
		return (Double) getJSONArrayObject(jsonArray, index, Double.class);
	}

	// get boolean from json array at index
	public static Boolean getBooleanFromJSONArray(JSONArray jsonArray, int index) {
		return (Boolean) getJSONArrayObject(jsonArray, index, Boolean.class);
	}

	// get string from json array at index
	public static String getStringFromJSONArray(JSONArray jsonArray, int index) {
		return (String) getJSONArrayObject(jsonArray, index, String.class);
	}

	// get json object from json array at index
	public static JSONObject getJSONObjectFromJSONArray(JSONArray jsonArray,
			int index) {
		return (JSONObject) getJSONArrayObject(jsonArray, index,
				JSONObject.class);
	}

	// get json array from json array at index
	public static JSONArray getJSONArrayFromJSONArray(JSONArray jsonArray,
			int index) {
		return (JSONArray) getJSONArrayObject(jsonArray, index, JSONArray.class);
	}

	// get object from json array at index
	public static Object getObjectFromJSONArray(JSONArray jsonArray, int index) {
		return getJSONArrayObject(jsonArray, index, Object.class);
	}

}
