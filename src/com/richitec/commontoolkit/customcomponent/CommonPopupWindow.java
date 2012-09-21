package com.richitec.commontoolkit.customcomponent;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.richitec.commontoolkit.activityextension.AppLaunchActivity;

public abstract class CommonPopupWindow extends PopupWindow {

	public CommonPopupWindow() {
		super();

		// bind popup window content view and its present child view listener
		bindPopupWindowContentView7PresentChildViewListener();

		// bind popup window components listener
		bindPopupWindowComponentsListener();
	}

	public CommonPopupWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// bind popup window content view and its present child view listener
		bindPopupWindowContentView7PresentChildViewListener();

		// bind popup window components listener
		bindPopupWindowComponentsListener();
	}

	public CommonPopupWindow(Context context, AttributeSet attrs) {
		super(context, attrs);

		// bind popup window content view and its present child view listener
		bindPopupWindowContentView7PresentChildViewListener();

		// bind popup window components listener
		bindPopupWindowComponentsListener();
	}

	public CommonPopupWindow(Context context) {
		super(context);

		// bind popup window content view and its present child view listener
		bindPopupWindowContentView7PresentChildViewListener();

		// bind popup window components listener
		bindPopupWindowComponentsListener();
	}

	public CommonPopupWindow(int width, int height) {
		super(width, height);

		// bind popup window content view and its present child view listener
		bindPopupWindowContentView7PresentChildViewListener();

		// bind popup window components listener
		bindPopupWindowComponentsListener();
	}

	public CommonPopupWindow(View contentView, int width, int height,
			boolean focusable) {
		super(contentView, width, height, focusable);

		// bind popup window content view and its present child view listener
		bindPopupWindowContentView7PresentChildViewListener();

		// bind popup window components listener
		bindPopupWindowComponentsListener();
	}

	public CommonPopupWindow(View contentView, int width, int height) {
		super(contentView, width, height);

		// bind popup window content view and its present child view listener
		bindPopupWindowContentView7PresentChildViewListener();

		// bind popup window components listener
		bindPopupWindowComponentsListener();
	}

	public CommonPopupWindow(View contentView) {
		super(contentView);

		// bind popup window content view and its present child view listener
		bindPopupWindowContentView7PresentChildViewListener();

		// bind popup window components listener
		bindPopupWindowComponentsListener();
	}

	// constructor with popup window layout resource id
	public CommonPopupWindow(int resource, int width, int height,
			boolean focusable, boolean isBindDefListener) {
		super(((LayoutInflater) AppLaunchActivity.getAppContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE)).inflate(
				resource, null), width, height, focusable);

		// check bind default content view and its present child view listener
		if (isBindDefListener) {
			bindPopupWindowContentView7PresentChildViewListener();
		}

		// bind popup window components listener
		bindPopupWindowComponentsListener();
	}

	public CommonPopupWindow(int resource, int width, int height) {
		this(resource, width, height, true, true);
	}

	@Override
	public void dismiss() {
		super.dismiss();

		// reset popup window using an new handle
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				resetPopupWindow();
			}
		}, 0);
	}

	// bind popup window content view and its present child view listener
	private void bindPopupWindowContentView7PresentChildViewListener() {
		// bind popup window content view and its present child view on touch
		// listener
		getContentView().setOnTouchListener(
				new ContentView6PresentChildViewOnTouchListener());
		for (int i = 0; i < ((FrameLayout) getContentView()).getChildCount(); i++) {
			((FrameLayout) getContentView()).getChildAt(i).setOnTouchListener(
					new ContentView6PresentChildViewOnTouchListener());
		}

		// bind popup window content view on key listener
		getContentView().setOnKeyListener(new ContentViewOnKeyListener());
	}

	// bind popup window components listener
	protected abstract void bindPopupWindowComponentsListener();

	// reset popup window
	protected abstract void resetPopupWindow();

	// inner class
	// popup window content view and or its present child view on touch listener
	class ContentView6PresentChildViewOnTouchListener implements
			OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// check on touch view class
			if (getContentView().equals(v)) {
				// dismiss popup window
				dismiss();
			}

			return true;
		}

	}

	// popup window content view on key listener
	class ContentViewOnKeyListener implements OnKeyListener {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			Log.d("CommonPopupWindow", "view = " + v + ", key code = "
					+ keyCode + " and key event = " + event);

			// listen back button pressed
			if (KeyEvent.KEYCODE_BACK == keyCode
					&& KeyEvent.ACTION_DOWN == event.getAction()) {
				// dismiss popup window
				dismiss();
			}

			return false;
		}

	}

}
