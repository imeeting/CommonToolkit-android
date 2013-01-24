package com.richitec.commontoolkit.customcomponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.richitec.commontoolkit.R;

public class CTToast {

	private static final String LOG_TAG = CTToast.class.getCanonicalName();

	// define some commonToolkit toast duration constant
	public static final Integer LENGTH_TRANSIENT = 500;
	public static final Integer LENGTH_SHORT = 1000;
	public static final Integer LENGTH_NORMAL = 3000;
	public static final Integer LENGTH_LONG = 5000;

	// commonToolkit toast widget context
	private Context _mContext;

	// toast to store attributes
	private Toast _mStoreAttrsToast;

	// commonToolkit toast content view
	private ViewGroup _mContentView;

	// display popup window
	private PopupWindow _mDisplayPopupWindow;

	// popup window display textView default padding list
	private List<Integer> _mPopupWindowDisplayTextViewDefaultPaddings;

	// popup window display timer
	private final Timer POPUPWINDOW_DISPLAY_TIMER = new Timer();

	// popup window display timer task
	private TimerTask _mPopupWindowDisplayTimerTask;

	public CTToast(Context context, Integer contentViewResId) {
		super();

		// save commonToolkit toast widget context
		_mContext = context;

		// check content view resource id and init content view
		if (null != contentViewResId) {
			try {
				_mContentView = (ViewGroup) ((LayoutInflater) context
						.getSystemService(Activity.LAYOUT_INFLATER_SERVICE))
						.inflate(contentViewResId, null);

				// check commonToolkit toast content view
				if (null == _mContentView) {
					throw new Exception(
							"CommonToolkit toast content view is null, init with default style");
				}
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Inflate content view error, exception message = "
								+ e.getMessage());

				e.printStackTrace();

				_mContentView = (ViewGroup) ((LayoutInflater) context
						.getSystemService(Activity.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.commontoolkit_toast_content_layout,
								null);
			}
		} else {
			// init content view with default style
			_mContentView = (ViewGroup) ((LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.commontoolkit_toast_content_layout, null);
		}

		// set content view on touch listener
		_mContentView
				.setOnTouchListener(new CTToastDisplayPopupWindowOnTouchListener());

		// init toast for storing attributes and display popup window
		_mStoreAttrsToast = new Toast(context);
		_mDisplayPopupWindow = new PopupWindow(_mContentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public CTToast(Context context) {
		this(context, null);
	}

	public void setText(CharSequence s) {
		// get commonToolkit toast tip text display textView, check it and set
		// its text and padding
		TextView _displayTextView = (TextView) _mContentView
				.findViewById(R.id.commonToolkit_toast_textView);

		if (null != _displayTextView) {
			_displayTextView.setText(s);
			setPopupWindowDisplaTextViewPaddings(_displayTextView);
		} else {
			Log.e(LOG_TAG, "Can't find textView for displaying text tip");
		}
	}

	public void setText(int resId) {
		// get commonToolkit toast tip text display textView, check it and set
		// its text and padding
		TextView _displayTextView = (TextView) _mContentView
				.findViewById(R.id.commonToolkit_toast_textView);

		if (null != _displayTextView) {
			_displayTextView.setText(resId);
			setPopupWindowDisplaTextViewPaddings(_displayTextView);
		} else {
			Log.e(LOG_TAG, "Can't find textView for displaying text tip");
		}
	}

	public void setDuration(int duration) {
		_mStoreAttrsToast.setDuration(duration);
	}

	public int getDuration() {
		return _mStoreAttrsToast.getDuration();
	}

	public void setGravity(int gravity, int xOffset, int yOffset) {
		_mStoreAttrsToast.setGravity(gravity, xOffset, yOffset);
	}

	public int getGravity() {
		return _mStoreAttrsToast.getGravity();
	}

	public int getXOffset() {
		return _mStoreAttrsToast.getXOffset();
	}

	public int getYOffset() {
		return _mStoreAttrsToast.getYOffset();
	}

	public void setMargin(float horizontalMargin, float verticalMargin) {
		_mStoreAttrsToast.setMargin(horizontalMargin, verticalMargin);
	}

	public float getHorizontalMargin() {
		return _mStoreAttrsToast.getHorizontalMargin();
	}

	public float getVerticalMargin() {
		return _mStoreAttrsToast.getVerticalMargin();
	}

	public void setView(View view) {
		_mStoreAttrsToast.setView(view);

		// remove all sub views of content view and add param view
		_mContentView.removeAllViews();
		_mContentView.addView(view);
	}

	public View getView() {
		return _mStoreAttrsToast.getView();
	}

	public void show() {
		// show display popup window
		if (null != _mDisplayPopupWindow) {
			// check display popup window visibility
			if (_mDisplayPopupWindow.isShowing()) {
				// update display popup window location
				_mDisplayPopupWindow.update(getXOffset(), getYOffset(),
						_mDisplayPopupWindow.getWidth(),
						_mDisplayPopupWindow.getHeight());
			} else {
				// show immediately
				_mDisplayPopupWindow.showAtLocation(((Activity) _mContext)
						.getWindow().getDecorView(), getGravity(),
						getXOffset(), getYOffset());
			}

			// check popup window display timer task and cancel it
			if (null != _mPopupWindowDisplayTimerTask) {
				_mPopupWindowDisplayTimerTask.cancel();
			}

			// dismiss popup window after duration time using timer task
			POPUPWINDOW_DISPLAY_TIMER.schedule(
			// new timer task and reset popup window display timer task
					_mPopupWindowDisplayTimerTask = new TimerTask() {

						@Override
						public void run() {
							// dismiss display popup window
							_mDisplayPopupWindow.dismiss();

							// clear popup window display timer task
							_mPopupWindowDisplayTimerTask = null;
						}
					}, getDuration());
		}
	}

	public void cancel() {
		// dismiss display popup window
		if (null != _mDisplayPopupWindow) {
			_mDisplayPopupWindow.dismiss();

			// check popup window display timer task, cancel and clear it
			if (null != _mPopupWindowDisplayTimerTask) {
				_mPopupWindowDisplayTimerTask.cancel();

				_mPopupWindowDisplayTimerTask = null;
			}
		}
	}

	public static CTToast makeText(Context context, CharSequence text,
			int duration) {
		// define commonToolkit toast
		CTToast _ctToast = new CTToast(context);

		// set text and duration
		_ctToast.setText(text);
		_ctToast.setDuration(duration);

		return _ctToast;
	}

	public static CTToast makeText(Context context, int textResId, int duration) {
		// define commonToolkit toast
		CTToast _ctToast = new CTToast(context);

		// set text and duration
		_ctToast.setText(textResId);
		_ctToast.setDuration(duration);

		return _ctToast;
	}

	// get commonToolkit toast display popup window tip textView
	public TextView getTipTextView() {
		return (TextView) _mContentView
				.findViewById(R.id.commonToolkit_toast_textView);
	}

	// set commonToolkit toast display popup window tip textView paddings
	private void setPopupWindowDisplaTextViewPaddings(TextView displayTextView) {
		// check popup window display textView default padding list
		if (null == _mPopupWindowDisplayTextViewDefaultPaddings) {
			// init popup window display textView default padding list
			_mPopupWindowDisplayTextViewDefaultPaddings = new ArrayList<Integer>(
					4);

			// save popup window display textView default padding
			_mPopupWindowDisplayTextViewDefaultPaddings.add(displayTextView
					.getPaddingLeft());
			_mPopupWindowDisplayTextViewDefaultPaddings.add(displayTextView
					.getPaddingTop());
			_mPopupWindowDisplayTextViewDefaultPaddings.add(displayTextView
					.getPaddingRight());
			_mPopupWindowDisplayTextViewDefaultPaddings.add(displayTextView
					.getPaddingBottom());
		}

		// set display textView padding left, top, right and bottom
		displayTextView.setPadding(
				_mPopupWindowDisplayTextViewDefaultPaddings.get(0)
						+ (int) getHorizontalMargin(),
				_mPopupWindowDisplayTextViewDefaultPaddings.get(1)
						+ (int) getVerticalMargin(),
				_mPopupWindowDisplayTextViewDefaultPaddings.get(2)
						+ (int) getHorizontalMargin(),
				_mPopupWindowDisplayTextViewDefaultPaddings.get(3)
						+ (int) getVerticalMargin());
	}

	// inner class
	// commonToolkit toast display popup window on touch listener
	class CTToastDisplayPopupWindowOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// dismiss commonToolkit toast display popup window
			_mDisplayPopupWindow.dismiss();

			return false;
		}

	}

}
