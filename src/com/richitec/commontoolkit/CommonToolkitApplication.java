package com.richitec.commontoolkit;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class CommonToolkitApplication extends Application {
	
	// singleton instance
	private static volatile CommonToolkitApplication _singletonInstance;

	// private constructor
	public CommonToolkitApplication() {
		super();
		// init singleton instance
		_singletonInstance = this;
		
	}
	
	@Override
	public void onCreate () {
		super.onCreate();
		Log.d("commontoolkit", "CommonToolkitApplication - onCreate");
	}

	// retrieve application's context
	public static Context getContext() {
		return _singletonInstance;
	}

}
