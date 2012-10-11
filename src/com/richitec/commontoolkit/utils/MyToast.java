package com.richitec.commontoolkit.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class MyToast {

	public static void show(Context context, String text, int duration) {
		Toast t = Toast.makeText(context, text, duration);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}
	
	public static void show(Context context, int resId, int duration) {
		if (context == null) {
			return;
		}
		show(context, context.getString(resId), duration);
	}
}
