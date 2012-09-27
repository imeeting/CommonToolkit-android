package com.richitec.commontoolkit.customcomponent;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.activityextension.R;
import com.richitec.commontoolkit.customadapter.CommonListAdapter;

public class ListViewQuickAlphabetBar {

	private static final String LOG_TAG = "ListViewQuickAlphabetBar";

	// alphabet
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";

	// alphabet touched letter toast
	private AlphabetTouchedLetterToast _mAlphabetTouchedLetterToast;

	// alphabet relativeLayout
	private RelativeLayout _mAlphabetRelativeLayout;

	// dependent listView
	private ListView _mDependentListView;

	// listView quick alphabet bar touch listener
	private OnTouchListener _mOnTouchListener;

	public ListViewQuickAlphabetBar(ListView dependentListView) {
		// get quickAlphabetBar frameLayout
		FrameLayout _quickAlphabetBarFrameLayout = (FrameLayout) ((LayoutInflater) AppLaunchActivity
				.getAppContext().getSystemService(
						Activity.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.listview_quickalphabetbar_layout, null);

		// save alphabet relativeLayout
		_mAlphabetRelativeLayout = (RelativeLayout) _quickAlphabetBarFrameLayout
				.findViewById(R.id.alphabet_relativeLayout);
		_quickAlphabetBarFrameLayout.removeView(_mAlphabetRelativeLayout);

		// set alphabet relativeLayout on touch listener
		_mAlphabetRelativeLayout
				.setOnTouchListener(new OnAlphabetRelativeLayoutTouchListener());

		if (null != dependentListView) {
			// init alphabet touched letter toast
			_mAlphabetTouchedLetterToast = new AlphabetTouchedLetterToast(
					dependentListView);

			// save dependent listView
			_mDependentListView = dependentListView;

			// bind listView and alphabet
			bindListViewAlphabet(dependentListView);

			// ??, test by ares
			updateAlphabet(_mDependentListView);
		}
	}

	// bind listView and alphabet
	private void bindListViewAlphabet(ListView dependentListView) {
		// check dependent listView
		if (null != dependentListView && null != dependentListView.getParent()
				&& dependentListView.getParent() instanceof FrameLayout) {
			// hide scroll bar
			dependentListView.setScrollBarStyle(View.INVISIBLE);

			// add alphabet relativeLayout to dependent listView
			((FrameLayout) dependentListView.getParent())
					.addView(_mAlphabetRelativeLayout);
		} else {
			Log.e(LOG_TAG, "Dependent listView = " + dependentListView
					+ " and its parent view = " + dependentListView.getParent());
		}
	}

	// update alphabet
	private void updateAlphabet(ListView dependentListView) {
		// check dependent listView
		if (null != dependentListView
				&& dependentListView.getAdapter() instanceof CommonListAdapter) {
			// clear alphabet relativeLayout
			// hide head letter textView
			TextView _headLetterTextView = (TextView) _mAlphabetRelativeLayout
					.findViewById(R.id.headLetter_textView);
			_headLetterTextView.setVisibility(View.GONE);

			// hide other letters linearLayout and child letter textView
			LinearLayout _otherLettersLinearLayout = (LinearLayout) _mAlphabetRelativeLayout
					.findViewById(R.id.otherLetters_linearLayout);
			_otherLettersLinearLayout.setVisibility(View.GONE);
			for (int i = 0; i < _otherLettersLinearLayout.getChildCount(); i++) {
				((TextView) _otherLettersLinearLayout.getChildAt(i))
						.setVisibility(View.GONE);
			}

			// ??, test by ares
			List<Character> _presentAlphabet = new ArrayList<Character>();
			_presentAlphabet.add('A');
			_presentAlphabet.add('C');
			_presentAlphabet.add('H');
			_presentAlphabet.add('J');
			_presentAlphabet.add('M');
			_presentAlphabet.add('W');
			_presentAlphabet.add('X');
			_presentAlphabet.add('Z');
			_presentAlphabet.add('#');

			// set alphabet
			for (int i = 0; i < _presentAlphabet.size(); i++) {
				// get letter
				String _letter = String.valueOf(_presentAlphabet.get(i));

				// head letter
				if (0 == i) {
					// set head letter textView text and show it
					_headLetterTextView.setText(_letter);
					_headLetterTextView.setVisibility(View.VISIBLE);
				} else {
					// show other letters linearLayout if it is not visible
					if (!_otherLettersLinearLayout.isShown()) {
						_otherLettersLinearLayout.setVisibility(View.VISIBLE);
					}

					// set other letter textView text and show it
					TextView _otherLetterTextView = (TextView) _otherLettersLinearLayout
							.getChildAt(i);
					_otherLetterTextView.setText(_letter);
					_otherLetterTextView.setVisibility(View.VISIBLE);
				}
			}
		} else {
			Log.e(LOG_TAG, "Dependent listView = " + dependentListView
					+ " and its adapter = " + dependentListView.getAdapter());
		}
	}

	// set listView quickAlphabetBar on touch listener
	public void setOnTouchListener(OnTouchListener onTouchListener) {
		_mOnTouchListener = onTouchListener;
	}

	// get touched letter
	private Character getTouchedLetter(MotionEvent event,
			Point headLetterEndPoint, Point otherLettersEndPoint) {
		// define return touched letter
		Character _touchedLetter = null;

		if (MotionEvent.ACTION_DOWN == event.getAction()) {
			// define alphabet relativeLayout original point
			final Point _alphabetRelativeLayoutOrigPoint = new Point(-1, -1);

			// location object
			final int[] _location = new int[2];

			// update alphabet relativeLayout original point
			_mAlphabetRelativeLayout.getLocationOnScreen(_location);
			_alphabetRelativeLayoutOrigPoint.set(_location[0], _location[1]);

			// update head letter textView original point and end point
			TextView _headLetterTextView = (TextView) _mAlphabetRelativeLayout
					.findViewById(R.id.headLetter_textView);
			_headLetterTextView.getLocationOnScreen(_location);
			headLetterEndPoint.set(
					_location[0] - _alphabetRelativeLayoutOrigPoint.x
							+ _headLetterTextView.getWidth(), _location[1]
							- _alphabetRelativeLayoutOrigPoint.y
							+ _headLetterTextView.getHeight());

			// update other letters linearLayout original and end point
			LinearLayout _otherLettersLinearLayout = (LinearLayout) _mAlphabetRelativeLayout
					.findViewById(R.id.otherLetters_linearLayout);
			_otherLettersLinearLayout.getLocationOnScreen(_location);
			otherLettersEndPoint.set(_location[0]
					- _alphabetRelativeLayoutOrigPoint.x
					+ _otherLettersLinearLayout.getWidth(), _location[1]
					- _alphabetRelativeLayoutOrigPoint.y
					+ _otherLettersLinearLayout.getHeight());
		}

		// check touch event location bounds
		float _touchedLocationY = event.getY();
		// at least on letter
		if (0 != headLetterEndPoint.y) {
			// only one letter
			if (0 == otherLettersEndPoint.y) {
				// head letter
				Log.d(LOG_TAG, "head letter");
				_mAlphabetTouchedLetterToast.setText("head letter").show();

				// ??
			} else {
				// get other letter textView average height
				float _otherLetterTextViewAverageHeight = (float) ((otherLettersEndPoint.y - headLetterEndPoint.y) / 8.0);

				// up
				if (_touchedLocationY < headLetterEndPoint.y) {
					// head letter
					Log.d(LOG_TAG, "head letter");
					_mAlphabetTouchedLetterToast.setText("head letter").show();

					// ??
				}
				// down
				else if (_touchedLocationY >= otherLettersEndPoint.y) {
					// other letters last letter
					Log.d(LOG_TAG, "last other letter");
					_mAlphabetTouchedLetterToast.setText("last other letter")
							.show();

					// ??
				} else {
					// letter of other letters
					int _index = (int) ((_touchedLocationY - headLetterEndPoint.y) / _otherLetterTextViewAverageHeight) + 1;
					Log.d(LOG_TAG, _index + " letter of other letters");
					_mAlphabetTouchedLetterToast.setText(
							_index + " letter of other letters").show();

					// ??
				}
			}
		}
		// no letter
		else {
			Log.w(LOG_TAG, "Alphabet has no letter");
		}

		return _touchedLetter;
	}

	// inner class
	// alphabet touched letter toast
	class AlphabetTouchedLetterToast {

		// alphabet touched letter toast
		Toast _mAlphabetTouchedLetterToast;

		public AlphabetTouchedLetterToast(ListView dependentListView) {
			_mAlphabetTouchedLetterToast = Toast.makeText(
					dependentListView.getContext(), "", Toast.LENGTH_SHORT);

			// get dependent listView original point
			final int[] _location = new int[2];
			dependentListView.getLocationOnScreen(_location);

			// set gravity
			_mAlphabetTouchedLetterToast.setGravity(Gravity.CENTER_VERTICAL
					| Gravity.LEFT, _location[0] + dependentListView.getWidth()
					/ 2, 0);
		}

		// set text
		public Toast setText(CharSequence text) {
			_mAlphabetTouchedLetterToast.setText(text);

			return _mAlphabetTouchedLetterToast;
		}

		public Toast setText(int text) {
			_mAlphabetTouchedLetterToast.setText(text);

			return _mAlphabetTouchedLetterToast;
		}

	}

	// alphabet relativeLayout on touch listener
	class OnAlphabetRelativeLayoutTouchListener implements
			android.view.View.OnTouchListener {

		// define head letter textView and other letters linearLayout end point
		final Point _headLetterTextViewEndPoint = new Point(-1, -1);
		final Point _otherLettersLinearLayoutEndPoint = new Point(-1, -1);

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// check event action
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				if (MotionEvent.ACTION_DOWN == event.getAction()) {
					// update alphabet relativeLayout background resource
					_mAlphabetRelativeLayout
							.setBackgroundResource(R.drawable.listview_alphabetrelativelayout_bg);

					// check dependent listView and on touch listener
					if (null == _mDependentListView) {
						Log.e(LOG_TAG, "Dependent listView is null");
					} else if (null == _mOnTouchListener) {
						Log.w(LOG_TAG,
								"ListView quickAlphabetBar not be stted on touch listener");
					}
				}

				// get touched letter
				Character _touchedLetter = getTouchedLetter(event,
						_headLetterTextViewEndPoint,
						_otherLettersLinearLayoutEndPoint);

				// check touch listener
				if (null != _mOnTouchListener && null != _mDependentListView) {
					_mOnTouchListener.onTouch(_mAlphabetRelativeLayout,
							_mDependentListView, event, _touchedLetter);
				}
				break;

			case MotionEvent.ACTION_UP:
			default:
				// update alphabet relativeLayout background color
				_mAlphabetRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
				break;
			}

			return true;
		}

	}

	// listView quick alphabet bar touch listener
	public static abstract class OnTouchListener {

		// listView quick alphabet bar on touch
		protected abstract boolean onTouch(
				RelativeLayout alphabetRelativeLayout,
				ListView dependentListView, MotionEvent event,
				Character alphabeticalCharacter);

	}

}
