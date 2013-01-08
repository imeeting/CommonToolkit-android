package com.richitec.commontoolkit;

import android.app.Application;
import android.content.Context;

public class CommonToolkitApplication extends Application {

	// singleton instance
	private static volatile CommonToolkitApplication _singletonInstance;

	// private constructor
	public CommonToolkitApplication() {
		super();

		// init singleton instance
		_singletonInstance = this;
	}

	// retrieve application's context
	public static Context getContext() {
		return _singletonInstance;
	}

}
