package com.richitec.commontoolkit.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * An animation that rotates the view on the Y axis between two specified
 * angles. This animation also adds a translation on the Z axis (depth) to
 * improve the effect.
 */
public class CTRotate3DAnimation extends Animation {

	private static final String LOG_TAG = CTRotate3DAnimation.class
			.getCanonicalName();

	// rotate degree ranges
	private final float _mFromDegrees;
	private final float _mToDegrees;

	// rotate point coordinate
	private final float _mCenterX;
	private final float _mCenterY;
	private final float _mDepthZ;

	// reverse flag
	private final boolean _mReverse;

	// camera used to compute 3D transformations and generate a matrix that can
	// be applied
	private Camera _mCamera;

	/**
	 * Creates a new 3D rotation on the Y axis. The rotation is defined by its
	 * start angle and its end angle. Both angles are in degrees. The rotation
	 * is performed around a center point on the 2D space, definied by a pair of
	 * X and Y coordinates, called centerX and centerY. When the animation
	 * starts, a translation on the Z axis (depth) is performed. The length of
	 * the translation can be specified, as well as whether the translation
	 * should be reversed in time.
	 * 
	 * @param fromDegrees
	 *            the start angle of the 3D rotation
	 * @param toDegrees
	 *            the end angle of the 3D rotation
	 * @param centerX
	 *            the X center of the 3D rotation
	 * @param centerY
	 *            the Y center of the 3D rotation
	 * @param reverse
	 *            true if the translation should be reversed, false otherwise
	 */
	public CTRotate3DAnimation(float fromDegrees, float toDegrees,
			float centerX, float centerY, float depthZ, boolean reverse) {
		// init degrees, rotate point coordinate and reverse flag variables
		_mFromDegrees = fromDegrees;
		_mToDegrees = toDegrees;

		_mCenterX = centerX;
		_mCenterY = centerY;
		_mDepthZ = depthZ;

		_mReverse = reverse;
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		// new a camera, with empty transformations
		_mCamera = new Camera();

		super.initialize(width, height, parentWidth, parentHeight);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		// get rotate degrees
		float _rotateDegrees = _mFromDegrees
				+ ((_mToDegrees - _mFromDegrees) * interpolatedTime);

		// get transformation matrix
		final Matrix _matrix = t.getMatrix();

		// save camera state first
		_mCamera.save();

		// check reverse flag
		if (_mReverse) {
			_mCamera.translate(0.0f, 0.0f, _mDepthZ * interpolatedTime);
		} else {
			_mCamera.translate(0.0f, 0.0f, _mDepthZ * (1.0f - interpolatedTime));
		}

		// rotation transform around the Y axis
		_mCamera.rotateY(_rotateDegrees);

		// computes the matrix corresponding to the current transformation and
		// reset transformation matrix
		_mCamera.getMatrix(_matrix);

		// restore camera state
		_mCamera.restore();

		// preconcats and postconcats the matrix with the specified translation
		_matrix.preTranslate(-_mCenterX, -_mCenterY);
		_matrix.postTranslate(_mCenterX, _mCenterY);
	}

	// static 3D rotate for view with direction and center point
	public static void static3DRotate4View(View view,
			ThreeDimensionalRotateDirection rotateDirection, Point centerPoint) {
		// check view and its center point
		if (null != view && null != centerPoint) {
			// define 3D rotation animation
			CTRotate3DAnimation _rotate3DAnimation;

			// check 3D rotate direction and init static rotate 3D animation
			switch (rotateDirection) {
			case HORIZONTAL_RIGHT:
				_rotate3DAnimation = new CTRotate3DAnimation(0.0f, 180.0f,
						centerPoint.x, centerPoint.y, 0, true);
				break;

			case VERTICAL_UP:
				Log.d(LOG_TAG, "");

				_rotate3DAnimation = null;
				break;

			case VERTICAL_DOWN:
				Log.d(LOG_TAG, "");

				_rotate3DAnimation = null;
				break;

			case HORIZONTAL_LEFT:
			default:
				_rotate3DAnimation = new CTRotate3DAnimation(0.0f, -180.0f,
						centerPoint.x, centerPoint.y, 0, true);
				break;
			}

			// set the static rotate 3D animation duration and fill after state
			_rotate3DAnimation.setDuration(0);
			_rotate3DAnimation.setFillAfter(true);

			// start the static 3D rotation animation of the view
			view.startAnimation(_rotate3DAnimation);
		}
	}

	// inner class
	// 3D rotate direction
	public enum ThreeDimensionalRotateDirection {
		HORIZONTAL_LEFT, HORIZONTAL_RIGHT, VERTICAL_UP, VERTICAL_DOWN
	}

}
