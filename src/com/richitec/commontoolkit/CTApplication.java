package com.richitec.commontoolkit;

import android.app.Application;
import android.content.Context;
import android.util.Log;


public class CTApplication extends Application {

	// singleton instance
	private static volatile CTApplication _singletonInstance;

	public CTApplication() {
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
