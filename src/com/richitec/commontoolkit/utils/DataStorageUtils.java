package com.richitec.commontoolkit.utils;

import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.richitec.commontoolkit.CommonToolkitApplication;

public class DataStorageUtils {

	private static final String LOG_TAG = "DataStorageUtils";

	// shared preferences xml file default name
	private static final String SHARED_PREFERENCES_FILEDEFNAME = "sharedPreferencesDataStorage";

	// put object with storage type and file name
	// Object: String, Integer, Long, Float and Boolean
	public static void putObject(String key, Object value,
			StorageType storageType, String fileName) {
		// check storage type
		switch (storageType) {
		case FILE:
			Log.d(LOG_TAG, "Can't implement file storage for object");
			break;

		case SQLITE:
			Log.d(LOG_TAG, "Can't implement SQLite storage for object");
			break;

		case SHARED_PREFERENCES:
		default:
			// put object to shared preferences editor
			putObject2SharedPreferencesStorage(
					CommonToolkitApplication
							.getContext()
							.getSharedPreferences(
									null == fileName ? SHARED_PREFERENCES_FILEDEFNAME
											: fileName, Activity.MODE_PRIVATE)
							.edit(), key, value).commit();
			break;
		}
	}

	public static void putObject(String key, Object value) {
		DataStorageUtils.putObject(key, value, StorageType.SHARED_PREFERENCES,
				null);
	}

	// put object map with storage type and file name
	public static void putMap(Map<String, Object> objectMap,
			StorageType storageType, String fileName) {
		// check storage type
		switch (storageType) {
		case FILE:
			Log.d(LOG_TAG, "Can't implement file storage for map");
			break;

		case SQLITE:
			Log.d(LOG_TAG, "Can't implement SQLite storage for map");
			break;

		case SHARED_PREFERENCES:
		default: {
			// get shared preferences editor
			SharedPreferences.Editor _sharedPreferencesEditor = CommonToolkitApplication
					.getContext()
					.getSharedPreferences(
							null == fileName ? SHARED_PREFERENCES_FILEDEFNAME
									: fileName, Activity.MODE_PRIVATE).edit();

			// put object to shared preferences editor
			for (String objectMapKey : objectMap.keySet()) {
				putObject2SharedPreferencesStorage(_sharedPreferencesEditor,
						objectMapKey, objectMap.get(objectMapKey));
			}

			// commit to store in shared preferences storage
			_sharedPreferencesEditor.commit();
		}
			break;
		}
	}

	public static void putMap(Map<String, Object> objectMap) {
		DataStorageUtils
				.putMap(objectMap, StorageType.SHARED_PREFERENCES, null);
	}

	// get string from file with storage type
	public static String getString(String key, StorageType storageType,
			String fileName) {
		return (String) getObject(key, String.class, storageType, fileName);
	}

	public static String getString(String key) {
		return DataStorageUtils.getString(key, StorageType.SHARED_PREFERENCES,
				null);
	}

	// get integer from file with storage type
	public static Integer getInteger(String key, StorageType storageType,
			String fileName) {
		return (Integer) getObject(key, Integer.class, storageType, fileName);
	}

	public static Integer getInteger(String key) {
		return DataStorageUtils.getInteger(key, StorageType.SHARED_PREFERENCES,
				null);
	}

	// get long from file with storage type
	public static Long getLong(String key, StorageType storageType,
			String fileName) {
		return (Long) getObject(key, Long.class, storageType, fileName);
	}

	public static Long getLong(String key) {
		return DataStorageUtils.getLong(key, StorageType.SHARED_PREFERENCES,
				null);
	}

	// get float from file with storage type
	public static Float getFloat(String key, StorageType storageType,
			String fileName) {
		return (Float) getObject(key, Float.class, storageType, fileName);
	}

	public static Float getFloat(String key) {
		return DataStorageUtils.getFloat(key, StorageType.SHARED_PREFERENCES,
				null);
	}

	// get boolean from file with storage type
	public static Boolean getBoolean(String key, StorageType storageType,
			String fileName) {
		return (Boolean) getObject(key, Boolean.class, storageType, fileName);
	}

	public static Boolean getBoolean(String key) {
		return DataStorageUtils.getBoolean(key, StorageType.SHARED_PREFERENCES,
				null);
	}

	// get object map from file with storage type
	public static Map<String, ?> getAll(StorageType storageType, String fileName) {
		// init return map
		Map<String, ?> _ret = null;

		// check storage type
		switch (storageType) {
		case FILE:
			Log.d(LOG_TAG, "Can't implement get map all from file storage");
			break;

		case SQLITE:
			Log.d(LOG_TAG, "Can't implement get map all from SQLite storage");
			break;

		case SHARED_PREFERENCES:
		default: {
			// get shared preferences
			SharedPreferences _sharedPreferences = CommonToolkitApplication
					.getContext().getSharedPreferences(
							null == fileName ? SHARED_PREFERENCES_FILEDEFNAME
									: fileName, Activity.MODE_PRIVATE);

			// get all
			_ret = _sharedPreferences.getAll();
		}
			break;
		}

		return _ret;
	}

	public static Map<String, ?> getAll() {
		return DataStorageUtils.getAll(StorageType.SHARED_PREFERENCES, null);
	}

	// put object to shared preferences editor
	private static SharedPreferences.Editor putObject2SharedPreferencesStorage(
			SharedPreferences.Editor sharedPreferencesEditor, String key,
			Object value) {
		// check object type
		if (value instanceof String) {
			sharedPreferencesEditor.putString(key, (String) value);
		} else if (value instanceof Integer) {
			sharedPreferencesEditor.putInt(key, (Integer) value);
		} else if (value instanceof Long) {
			sharedPreferencesEditor.putLong(key, (Long) value);
		} else if (value instanceof Float) {
			sharedPreferencesEditor.putFloat(key, (Float) value);
		} else if (value instanceof Boolean) {
			sharedPreferencesEditor.putBoolean(key, (Boolean) value);
		} else {
			Log.e(LOG_TAG, "the object = " + value.toString()
					+ " can't put to shared preferences editor");
		}

		return sharedPreferencesEditor;
	}

	// get object
	private static Object getObject(String key,
			Class<? extends Object> keyClass, StorageType storageType,
			String fileName) {
		// init return object
		Object _ret;

		// check key class
		if (keyClass.equals(String.class)) {
			_ret = null;
		} else if (keyClass.equals(Integer.class)
				|| keyClass.equals(Long.class) || keyClass.equals(Float.class)) {
			_ret = 0;
		} else if (keyClass.equals(Boolean.class)) {
			_ret = false;
		} else {
			Log.e(LOG_TAG, "Get object key class = " + keyClass.getName()
					+ " can't recognized");

			_ret = null;
		}

		// check storage type
		switch (storageType) {
		case FILE:
			Log.d(LOG_TAG, "Can't implement get " + keyClass.getName()
					+ " from file storage");
			break;

		case SQLITE:
			Log.d(LOG_TAG, "Can't implement get " + keyClass.getName()
					+ " from SQLite storage");
			break;

		case SHARED_PREFERENCES:
		default: {
			// get shared preferences
			SharedPreferences _sharedPreferences = CommonToolkitApplication
					.getContext().getSharedPreferences(
							null == fileName ? SHARED_PREFERENCES_FILEDEFNAME
									: fileName, Activity.MODE_PRIVATE);

			// check key
			if (_sharedPreferences.contains(key)) {
				// check key class
				if (keyClass.equals(String.class)) {
					_ret = _sharedPreferences.getString(key, null);
				} else if (keyClass.equals(Integer.class)) {
					_ret = _sharedPreferences.getInt(key, 0);
				} else if (keyClass.equals(Long.class)) {
					_ret = _sharedPreferences.getLong(key, 0);
				} else if (keyClass.equals(Float.class)) {
					_ret = _sharedPreferences.getFloat(key, 0);
				} else if (keyClass.equals(Boolean.class)) {
					_ret = _sharedPreferences.getBoolean(key, false);
				}
			}
		}
			break;
		}

		return _ret;
	}

	// inner class
	public static enum StorageType {
		SHARED_PREFERENCES, FILE, SQLITE
	}

}
