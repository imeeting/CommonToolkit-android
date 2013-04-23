package com.richitec.commontoolkit.customcomponent;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.R;

public abstract class CTPopupWindow extends PopupWindow {

	private static final String LOG_TAG = CTPopupWindow.class
			.getCanonicalName();

	// commonToolkit popup window dismiss and next show animation switch
	// duration(milliseconds)
	public final Integer DISMISS7NEXTSHOWNANIMATION_SWITCHDURATION = 400;

	// android popup window animation style
	private final int ANDROID_POPUPWINDOW_ANIMATIONSTYLE = getAnimationStyle();

	// constructor with popup window layout resource id
	public CTPopupWindow(int resource, int width, int height,
			boolean focusable, boolean isBindDefListener) {
		super(((LayoutInflater) CTApplication.getContext().getSystemService(
				Activity.LAYOUT_INFLATER_SERVICE)).inflate(resource, null),
				width, height, focusable);

		// check bind default content view and its present child view listener
		if (isBindDefListener) {
			bindPopupWindowContentView7PresentChildViewListener();
		}

		// bind popup window components listener
		bindPopupWindowComponentsListener();
	}

	public CTPopupWindow(int resource, int width, int height) {
		this(resource, width, height, true, true);
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		// reset animation style
		setAnimationStyle(ANDROID_POPUPWINDOW_ANIMATIONSTYLE);

		// show at location
		super.showAtLocation(parent, gravity, x, y);
	}

	@Override
	public void dismiss() {
		// reset animation style
		setAnimationStyle(ANDROID_POPUPWINDOW_ANIMATIONSTYLE);

		// dismiss and reset popup window
		dismiss7ResetPopupWindow();
	}

	// show at location with default animation
	public void showAtLocationWithAnimation(View parent, int gravity, int x,
			int y) {
		// set default enter and exit animation
		setAnimationStyle(R.style.CommonToolkitPopupWindowAnimationPopup);

		// set animation for all child views of content vie for showing
		for (int i = 0; i < ((ViewGroup) getContentView()).getChildCount(); i++) {
			((ViewGroup) getContentView()).getChildAt(i).startAnimation(
					AnimationUtils.loadAnimation(CTApplication.getContext(),
							R.anim.bottom_popup_in));
		}

		// show at location
		super.showAtLocation(parent, gravity, x, y);
	}

	// show at location in parent view with gravity, offset x, y and animation
	public void showAtLocation(View parent, int gravity, int x, int y,
			int animationResId) {
		// set default enter and exit animation
		setAnimationStyle(R.style.CommonToolkitPopupWindowAnimationPopup);

		// set animation for all child views of content vie for showing
		for (int i = 0; i < ((ViewGroup) getContentView()).getChildCount(); i++) {
			((ViewGroup) getContentView()).getChildAt(i).startAnimation(
					AnimationUtils.loadAnimation(CTApplication.getContext(),
							animationResId));
		}

		// show at location
		super.showAtLocation(parent, gravity, x, y);
	}

	// dismiss with default animation
	public void dismissWithAnimation() {
		// set default enter and exit animation
		setAnimationStyle(R.style.CommonToolkitPopupWindowAnimationPopup);

		// get commonToolkit popup window bottom popup out dismiss animation
		Animation _dismissAnimation = AnimationUtils.loadAnimation(
				CTApplication.getContext(), R.anim.bottom_popup_out);

		// set animation for all child views of content vie for hiding
		for (int i = 0; i < ((ViewGroup) getContentView()).getChildCount(); i++) {
			((ViewGroup) getContentView()).getChildAt(i).startAnimation(
					_dismissAnimation);
		}

		// dismiss and reset popup window using animation duration
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				dismiss7ResetPopupWindow();
			}
		}, _dismissAnimation.getDuration());
	}

	// dismiss with animation
	public void dismiss(int animationResId) {
		// set default enter and exit animation
		setAnimationStyle(R.style.CommonToolkitPopupWindowAnimationPopup);

		// get commonToolkit popup window dismiss animation
		Animation _dismissAnimation = AnimationUtils.loadAnimation(
				CTApplication.getContext(), animationResId);

		// set animation for all child views of content vie for hiding
		for (int i = 0; i < ((ViewGroup) getContentView()).getChildCount(); i++) {
			((ViewGroup) getContentView()).getChildAt(i).startAnimation(
					_dismissAnimation);
		}

		// dismiss and reset popup window
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				dismiss7ResetPopupWindow();
			}
		}, _dismissAnimation.getDuration());
	}

	// bind popup window content view and its present child view listener
	private void bindPopupWindowContentView7PresentChildViewListener() {
		// bind popup window content view and its present child view on touch
		// listener
		getContentView().setOnTouchListener(
				new ContentView6PresentChildViewOnTouchListener());
		for (int i = 0; i < ((ViewGroup) getContentView()).getChildCount(); i++) {
			((ViewGroup) getContentView()).getChildAt(i).setOnTouchListener(
					new ContentView6PresentChildViewOnTouchListener());
		}

		// bind popup window content view on key listener
		getContentView().setOnKeyListener(new ContentViewOnKeyListener());
	}

	// dismiss commonToolkit popup window and reset it
	private void dismiss7ResetPopupWindow() {
		// dismiss it
		super.dismiss();

		// reset popup window using an new handle
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				resetPopupWindow();
			}
		}, 0);
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
			Log.d(LOG_TAG, "Content view = " + v + ", key code = " + keyCode
					+ " and key event = " + event);

			boolean _ret = false;

			// listen back button pressed if popup window has no children
			if (KeyEvent.KEYCODE_BACK == keyCode
					&& KeyEvent.ACTION_DOWN == event.getAction()) {
				// dismiss popup window
				dismiss();

				// reset return result
				_ret = true;
			}

			return _ret;
		}

	}

}
