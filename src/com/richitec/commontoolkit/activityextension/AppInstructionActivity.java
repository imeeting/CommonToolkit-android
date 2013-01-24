package com.richitec.commontoolkit.activityextension;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.R;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.commontoolkit.utils.VersionUtils;

public class AppInstructionActivity extends Activity {

	private static final String LOG_TAG = AppInstructionActivity.class
			.getCanonicalName();

	// key for saving application version name
	public static final String NEED2LAUNCH_APPINSTRUCTION = "application_version_name";

	// application instruction activity onCreate param key
	public static final String APPINSTRUCTION_ACTIVITY_TARGETINTENT_PARAM_KEY = "application_target_intent";
	public static final String APPINSTRUCTION_ACTIVITY_IMGRESIDS_PARAM_KEY = "application_instruction_content_image_resource&layout_id_list";

	// application target intent
	private Intent _mAppTargetIntent;

	// instruction viewFlipper
	private ViewFlipper _mInstructionViewFlipper;

	// gesture detector
	private final GestureDetector GESTUREDETECTOR = new GestureDetector(
			new InstructionActivityOnFlingGestureListener());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.app_instruction_activity_layout);

		// get the intent parameter data
		Bundle _data = getIntent().getExtras();

		// check the data bundle
		if (null != _data
				&& null != _data
						.getIntegerArrayList(APPINSTRUCTION_ACTIVITY_IMGRESIDS_PARAM_KEY)
				&& null != _data
						.getParcelable(APPINSTRUCTION_ACTIVITY_TARGETINTENT_PARAM_KEY)) {
			// get application instruction image resource and layout ids
			List<Integer> _appInstructionImgIds = _data
					.getIntegerArrayList(APPINSTRUCTION_ACTIVITY_IMGRESIDS_PARAM_KEY);

			// get instruction viewFlipper
			_mInstructionViewFlipper = ((ViewFlipper) findViewById(R.id.instructionViewFlipper));

			// init application instruction activity UI
			initInstructionUI(
					_appInstructionImgIds.subList(0,
							_appInstructionImgIds.size() - 1),
					_appInstructionImgIds.get(_appInstructionImgIds.size() - 1));

			// save application target intent
			_mAppTargetIntent = _data
					.getParcelable(APPINSTRUCTION_ACTIVITY_TARGETINTENT_PARAM_KEY);
		}
	}

	// init application instruction activity UI
	private void initInstructionUI(List<Integer> instructionImgResIds,
			Integer instructionActiveImgLayoutId) {
		// define valid instruction images count
		int _validInstructionImagesCount = 0;

		// get instruction images
		for (int i = 0; i < instructionImgResIds.size(); i++) {
			// check valid instruction images count and instruction viewFlipper
			// child count
			if (_validInstructionImagesCount < _mInstructionViewFlipper
					.getChildCount() - 1) {
				// get each instruction image
				Drawable _instructionImage = CTApplication.getContext()
						.getResources()
						.getDrawable(instructionImgResIds.get(i));

				// check each instruction image
				if (null != _instructionImage) {
					// set instruction content imageView image
					((ImageView) _mInstructionViewFlipper.getChildAt(
							_validInstructionImagesCount++).findViewById(
							R.id.instructionContent_imageView))
							.setImageDrawable(_instructionImage);
				}
			} else {
				Log.w(LOG_TAG, "Application instruction can't show more images");

				// return immediately
				break;
			}
		}

		// get instruction active image parent view
		ViewGroup _instructionActiveImgParentView = (ViewGroup) ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(instructionActiveImgLayoutId, null);

		// get active content imageView and button
		ImageView _activeContentImageView = (ImageView) _instructionActiveImgParentView
				.findViewById(R.id.activeContent_imageView);
		Button _activeContentButton = (Button) _instructionActiveImgParentView
				.findViewById(R.id.activeContent_button);

		// check active content imageView and button
		if (null != _activeContentButton) {
			// get instruction viewFlipper child relativeLayout
			RelativeLayout _viewFlipperChildRelativeLayout = (RelativeLayout) _mInstructionViewFlipper
					.getChildAt(_validInstructionImagesCount++);

			// check active content imageView and set instruction content
			// imageView image
			if (null != _activeContentImageView) {
				((ImageView) _viewFlipperChildRelativeLayout
						.findViewById(R.id.instructionContent_imageView))
						.setImageDrawable(_activeContentImageView.getDrawable());
			} else {
				Log.w(LOG_TAG,
						"Active content parent view child imageView is null");
			}

			// remove active button from active content parent view first
			_instructionActiveImgParentView.removeView(_activeContentButton);

			// set active content button on click listener
			_activeContentButton
					.setOnClickListener(new GotoTargetIntentBtnOnClickListener());

			// append go to target intent button
			_viewFlipperChildRelativeLayout.addView(_activeContentButton);
		} else {
			Log.e(LOG_TAG,
					"Application instruction active button lost in active content parent view");

			// finish application instruction activity
			finish();
		}

		// remove invisible instruction content imageViews
		while (_mInstructionViewFlipper.getChildCount() > _validInstructionImagesCount) {
			_mInstructionViewFlipper.removeViewAt(_mInstructionViewFlipper
					.getChildCount() - 1);
		}

		// init instruction next navigation and cursors
		if (1 < _validInstructionImagesCount) {
			// show next instruction image navigation imageView
			((ImageView) findViewById(R.id.instructionNav_NextImageView))
					.setVisibility(View.VISIBLE);
		}
		for (int i = 0; i < _validInstructionImagesCount; i++) {
			// get instruction cursor imageView, set its drawable and show it
			ImageView _instructionCursorImageView = ((ImageView) ((LinearLayout) findViewById(R.id.instructionCursorLinearLayout))
					.getChildAt(i));
			_instructionCursorImageView
					.setImageResource(0 == i ? R.drawable.img_instructioncursor_selected
							: R.drawable.img_instructioncursor_unselected);
			_instructionCursorImageView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		// nothing to do
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// use gesture detector to implement on fling gesture listener
		return GESTUREDETECTOR.onTouchEvent(event);
	}

	// update instruction navigation visibility and cursor imageView image
	private void updateInstructionNavVisibility7CursorImageViewImg(
			int viewFlipperDisplayChildIndex, FlingDirection flingDirection) {
		// get previous and next navigation imageView
		ImageView _previousNavImageView = (ImageView) findViewById(R.id.instructionNav_PreviousImageView);
		ImageView _nextNavImageView = (ImageView) findViewById(R.id.instructionNav_NextImageView);

		// check fling direction
		if (FlingDirection.LEFT == flingDirection) {
			// show previous navigate imageView
			_previousNavImageView.setVisibility(View.VISIBLE);

			// hide next navigation prognosis
			if (_mInstructionViewFlipper.getChildCount() - 1 == viewFlipperDisplayChildIndex + 1) {
				// hide next navigate imageView
				_nextNavImageView.setVisibility(View.GONE);
			}
		} else if (FlingDirection.RIGHT == flingDirection) {
			// show next navigate imageView
			_nextNavImageView.setVisibility(View.VISIBLE);

			// hide previous navigation prognosis
			if (1 == viewFlipperDisplayChildIndex) {
				// hide previous navigate imageView
				_previousNavImageView.setVisibility(View.GONE);
			}
		}

		// get instruction cursor imageView parent linearLayout
		LinearLayout _instructionCursorImageViewParentLinearLayout = (LinearLayout) findViewById(R.id.instructionCursorLinearLayout);

		// update cursor imageView image
		((ImageView) _instructionCursorImageViewParentLinearLayout
				.getChildAt(viewFlipperDisplayChildIndex))
				.setImageResource(R.drawable.img_instructioncursor_unselected);
		((ImageView) _instructionCursorImageViewParentLinearLayout
				.getChildAt(viewFlipperDisplayChildIndex
						+ (FlingDirection.LEFT == flingDirection ? 1 : -1)))
				.setImageResource(R.drawable.img_instructioncursor_selected);
	}

	// inner class
	// fling direction
	enum FlingDirection {
		LEFT, RIGHT
	}

	// instruction activity on fling gesture listener
	class InstructionActivityOnFlingGestureListener extends
			SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// define consumed result
			boolean _consumed = true;

			// define fling minimum distance and velocity
			final int FLING_MIN_DISTANCE = 50, FLING_MIN_VELOCITY = 100;

			// compare motion event x position
			if (e1.getX() == e2.getX()) {
				_consumed = false;
			} else {
				// get display child index
				int _displayedChildIndex = _mInstructionViewFlipper
						.getDisplayedChild();

				// check fling direction and strength
				if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
						&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
					// check bound
					if (_mInstructionViewFlipper.getChildCount() - 1 != _displayedChildIndex) {
						// update navigation visibility and cursor imageView
						// image
						updateInstructionNavVisibility7CursorImageViewImg(
								_displayedChildIndex, FlingDirection.LEFT);

						// fling left with animation
						_mInstructionViewFlipper.setInAnimation(
								AppInstructionActivity.this, R.anim.push_in);
						_mInstructionViewFlipper.setOutAnimation(
								AppInstructionActivity.this, R.anim.push_out);
						_mInstructionViewFlipper.showNext();
					}
				} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
						&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
					// check bound
					if (0 != _displayedChildIndex) {
						// update navigation visibility and cursor imageView
						// image
						updateInstructionNavVisibility7CursorImageViewImg(
								_displayedChildIndex, FlingDirection.RIGHT);

						// fling right with animation
						_mInstructionViewFlipper.setInAnimation(
								AppInstructionActivity.this, R.anim.pull_in);
						_mInstructionViewFlipper.setOutAnimation(
								AppInstructionActivity.this, R.anim.pull_out);
						_mInstructionViewFlipper.showPrevious();
					}
				}
			}

			return _consumed;
		}

	}

	// go to target intent button on click listener
	class GotoTargetIntentBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// save current application version
			DataStorageUtils.putObject(NEED2LAUNCH_APPINSTRUCTION,
					VersionUtils.versionName());

			// go to intent activity
			startActivity(_mAppTargetIntent);

			// finish application instruction activity
			finish();
		}

	}

}
