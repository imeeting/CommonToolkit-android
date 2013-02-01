package com.richitec.commontoolkit.customcomponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.richitec.commontoolkit.R;
import com.richitec.commontoolkit.customadapter.CTListAdapter;

public class ListViewQuickAlphabetBar extends DataSetObserver {

	private static final String LOG_TAG = ListViewQuickAlphabetBar.class
			.getCanonicalName();

	// alphabet
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";

	// alphabet touched letter toast
	private AlphabetTouchedLetterToast _mAlphabetTouchedLetterToast;

	// alphabet touch frameLayout
	private FrameLayout _mAlphabetTouchFrameLayout;

	// alphabet present relativeLayout
	private RelativeLayout _mAlphabetPresentRelativeLayout;

	// dependent listView
	private ListView _mDependentListView;

	// dependent listView data previous count
	private Integer _mDepedentListViewDataPreviousCount;

	// alphabet
	private List<Character> _mAlphabet;

	// previous touched letter
	private Character _mAlphabetRelativeLayoutPreviousTouchedLetter;

	// listView quick alphabet bar touch listener
	private OnTouchListener _mOnTouchListener;

	public ListViewQuickAlphabetBar(ListView dependentListView,
			CTToast touchedAlphabetLetterDisplayToast) {
		// check dependent lisView and its adapter
		if (null != dependentListView && null != dependentListView.getAdapter()) {
			// get quickAlphabetBar frameLayout
			FrameLayout _quickAlphabetBarFrameLayout = (FrameLayout) ((LayoutInflater) dependentListView
					.getContext().getSystemService(
							Activity.LAYOUT_INFLATER_SERVICE)).inflate(
					R.layout.listview_quickalphabetbar_layout, null);

			// save alphabet touch and present layout
			_mAlphabetTouchFrameLayout = (FrameLayout) _quickAlphabetBarFrameLayout
					.findViewById(R.id.alphabet_touch_frameLayout);
			_mAlphabetPresentRelativeLayout = (RelativeLayout) _mAlphabetTouchFrameLayout
					.findViewById(R.id.alphabet_present_relativeLayout);
			_quickAlphabetBarFrameLayout.removeView(_mAlphabetTouchFrameLayout);

			// set alphabet touch frameLayout on touch listener
			_mAlphabetTouchFrameLayout
					.setOnTouchListener(new OnAlphabetTouchFrameLayoutTouchListener());

			// init alphabet touched letter toast
			_mAlphabetTouchedLetterToast = new AlphabetTouchedLetterToast(
					dependentListView, touchedAlphabetLetterDisplayToast);

			// save dependent listView
			_mDependentListView = dependentListView;

			// bind listView and alphabet
			bindListViewAlphabet(dependentListView);
//			enlargeAlphabetIndexerTouchRegion();
		} else {
			Log.e(LOG_TAG,
					null == dependentListView ? "Dependent listView is null"
							: "Dependent listView = " + dependentListView
									+ " and its adapter is null");
		}
	}

	public ListViewQuickAlphabetBar(ListView dependentListView) {
		this(dependentListView, null);
	}

	@Override
	public void onChanged() {
		super.onChanged();

		// check dependent listView data count
		if (_mDepedentListViewDataPreviousCount != _mDependentListView
				.getAdapter().getCount()) {
			// update alphabet
			updateAlphabet(_mDependentListView.getAdapter());
		}
	}

	// bind listView and alphabet
	private void bindListViewAlphabet(ListView dependentListView) {
		// check dependent listView
		if (null != dependentListView && null != dependentListView.getParent()
				&& dependentListView.getParent() instanceof FrameLayout) {
			// hide vertical scroll bar
			dependentListView.setVerticalScrollBarEnabled(false);

			// add alphabet touch frameLayout to dependent listView
			((FrameLayout) dependentListView.getParent())
					.addView(_mAlphabetTouchFrameLayout);

			// register data set changed observer
			dependentListView.getAdapter().registerDataSetObserver(this);

			// init alphabet
			updateAlphabet(dependentListView.getAdapter());
		} else {
			Log.e(LOG_TAG, "Dependent listView = " + dependentListView
					+ " and its parent view = " + dependentListView.getParent());
		}
	}

	// update alphabet
	private void updateAlphabet(ListAdapter dependentListViewAdapter) {
		// save dependent listView adapter data count
		_mDepedentListViewDataPreviousCount = dependentListViewAdapter
				.getCount();

		if (dependentListViewAdapter instanceof CTListAdapter) {
			// clear alphabet present relativeLayout
			// hide head letter textView
			TextView _headLetterTextView = (TextView) _mAlphabetPresentRelativeLayout
					.findViewById(R.id.headLetter_textView);
			_headLetterTextView.setVisibility(View.GONE);

			// hide other letters linearLayout and child letter textView
			LinearLayout _otherLettersLinearLayout = (LinearLayout) _mAlphabetPresentRelativeLayout
					.findViewById(R.id.otherLetters_linearLayout);
			_otherLettersLinearLayout.setVisibility(View.GONE);
			for (int i = 0; i < _otherLettersLinearLayout.getChildCount(); i++) {
				((TextView) _otherLettersLinearLayout.getChildAt(i))
						.setVisibility(View.GONE);
			}

			// check dependent listView adapter alphabet set
			Set<Character> _dependentListViewAdapterAlphabetSet = ((CTListAdapter) dependentListViewAdapter)
					.getAlphabet();
			if (0 != _dependentListViewAdapterAlphabetSet.size()) {
				// init present alphabet
				_mAlphabet = new ArrayList<Character>();
				for (int i = 0; i < ALPHABET.length(); i++) {
					for (Character _alphabetIndex : _dependentListViewAdapterAlphabetSet) {
						// compare
						if (String.valueOf(ALPHABET.charAt(i))
								.equalsIgnoreCase(
										String.valueOf(_alphabetIndex))) {
							// add to alphabet and remove from dependent
							// listView adapter alphabet set
							_mAlphabet.add(ALPHABET.charAt(i));

							_dependentListViewAdapterAlphabetSet
									.remove(_alphabetIndex);

							break;
						}
					}
				}

				// init quick alphabet bar
				for (int i = 0; i < _mAlphabet.size(); i++) {
					// get letter
					String _letter = String.valueOf(_mAlphabet.get(i));

					// head letter
					if (0 == i) {
						// set head letter textView text and show it
						_headLetterTextView.setText(_letter);
						_headLetterTextView.setVisibility(View.VISIBLE);
					} else {
						// show other letters linearLayout if it is not visible
						if (!_otherLettersLinearLayout.isShown()) {
							_otherLettersLinearLayout
									.setVisibility(View.VISIBLE);
						}

						// set other letter textView text and show it
						TextView _otherLetterTextView = (TextView) _otherLettersLinearLayout
								.getChildAt(i);
						_otherLetterTextView.setText(_letter);
						_otherLetterTextView.setVisibility(View.VISIBLE);
					}
				}
			} else {
				Log.w(LOG_TAG, "Dependent listView adapter alphabet is empty");
			}
		} else {
			Log.w(LOG_TAG, "Dependent listView adapter = "
					+ dependentListViewAdapter + " and class name = "
					+ dependentListViewAdapter.getClass().getName());
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
			// define alphabet present relativeLayout original point
			final Point _alphabetRelativeLayoutOrigPoint = new Point(-1, -1);

			// location object
			final int[] _location = new int[2];

			// update alphabet present relativeLayout original point
			_mAlphabetPresentRelativeLayout.getLocationOnScreen(_location);
			_alphabetRelativeLayoutOrigPoint.set(_location[0], _location[1]);

			// update head letter textView original point and end point
			TextView _headLetterTextView = (TextView) _mAlphabetPresentRelativeLayout
					.findViewById(R.id.headLetter_textView);
			_headLetterTextView.getLocationOnScreen(_location);
			headLetterEndPoint.set(
					_location[0] - _alphabetRelativeLayoutOrigPoint.x
							+ _headLetterTextView.getWidth(), _location[1]
							- _alphabetRelativeLayoutOrigPoint.y
							+ _headLetterTextView.getHeight());

			// update other letters linearLayout original and end point
			LinearLayout _otherLettersLinearLayout = (LinearLayout) _mAlphabetPresentRelativeLayout
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
		// at least one letter
		if (0 != _mAlphabet.size()/* 0 != headLetterEndPoint.y */) {
			// only one letter
			if (1 == _mAlphabet.size()/* 0 == otherLettersEndPoint.y */) {
				// init touched letter(head letter) and show alphabet touched
				// letter toast
				_touchedLetter = _mAlphabet.get(0);

				_mAlphabetTouchedLetterToast.setText(
						String.valueOf(_touchedLetter)).show();
			} else {
				// get other letter textView average height
				float _otherLetterTextViewAverageHeight = ((float) (otherLettersEndPoint.y - headLetterEndPoint.y) / (_mAlphabet
						.size() - 1));

				// up
				if (_touchedLocationY < headLetterEndPoint.y) {
					// init touched letter(head letter) and show alphabet
					// touched letter toast
					_touchedLetter = _mAlphabet.get(0);

					_mAlphabetTouchedLetterToast.setText(
							String.valueOf(_touchedLetter)).show();
				}
				// down
				else if (_touchedLocationY >= otherLettersEndPoint.y) {
					// init touched letter(other letters last letter) and show
					// alphabet touched letter toast
					_touchedLetter = _mAlphabet.get(_mAlphabet.size() - 1);

					_mAlphabetTouchedLetterToast.setText(
							String.valueOf(_touchedLetter)).show();
				} else {
					// init touched letter(letter of other letters, remember
					// process last letter textView bottom bounds) and show
					// alphabet touched letter toast
					int _index = (int) ((_touchedLocationY - headLetterEndPoint.y) / _otherLetterTextViewAverageHeight) + 1;
					_touchedLetter = _mAlphabet
							.get(_mAlphabet.size() == _index ? _mAlphabet
									.size() - 1 : _index);

					_mAlphabetTouchedLetterToast.setText(
							String.valueOf(_touchedLetter)).show();
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

		// toast for displaying touched alphabet letter
		CTToast _mDisplayToast;

		public AlphabetTouchedLetterToast(ListView dependentListView,
				CTToast touchedAlphabetLetterDisplayToast) {
			// check touched alphabet letter display toast and set toast for
			// displaying touched alphabet letter
			if (null == touchedAlphabetLetterDisplayToast) {
				// init toast for displaying touched alphabet letter
				_mDisplayToast = CTToast.makeText(
						dependentListView.getContext(), "",
						CTToast.LENGTH_TRANSIENT);

				// get dependent listView original point
				final int[] _location = new int[2];
				dependentListView.getLocationOnScreen(_location);

				// set gravity
				_mDisplayToast.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.LEFT,
						(_location[0] + dependentListView.getWidth()) / 2, 0);
			} else {
				_mDisplayToast = touchedAlphabetLetterDisplayToast;
			}
		}

		// set text
		public CTToast setText(CharSequence text) {
			_mDisplayToast.setText(text);

			return _mDisplayToast;
		}

		public CTToast setText(int text) {
			_mDisplayToast.setText(text);

			return _mDisplayToast;
		}

	}

	// alphabet touch frameLayout on touch listener
	class OnAlphabetTouchFrameLayoutTouchListener implements
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
					// update alphabet present relativeLayout background
					// resource
					_mAlphabetPresentRelativeLayout
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

				// check touch listener and touched letter
				if (null != _mOnTouchListener
						&& null != _mDependentListView
						&& null != _touchedLetter
						&& !_touchedLetter
								.equals(_mAlphabetRelativeLayoutPreviousTouchedLetter)) {
					// save touched letter
					_mAlphabetRelativeLayoutPreviousTouchedLetter = _touchedLetter;

					_mOnTouchListener.onTouch(_mAlphabetPresentRelativeLayout,
							_mDependentListView, event, _touchedLetter);
				}
				break;

			case MotionEvent.ACTION_UP:
			default:
				// update alphabet present relativeLayout background color
				_mAlphabetPresentRelativeLayout
						.setBackgroundColor(Color.TRANSPARENT);
				break;
			}

			return true;
		}

	}

	// listView quick alphabet bar touch listener
	public static abstract class OnTouchListener {

		// listView quick alphabet bar on touch
		protected abstract boolean onTouch(
				RelativeLayout alphabetPresentRelativeLayout,
				ListView dependentListView, MotionEvent event,
				Character alphabeticalCharacter);

	}

}
