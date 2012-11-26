package com.richitec.commontoolkit.utils;

import java.util.Locale;

public class DeviceUtils {

	// get system current setting language. zh_CN, zh_TW etc.
	public static Locale getSystemCurrentSettingLanguage() {
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

}
