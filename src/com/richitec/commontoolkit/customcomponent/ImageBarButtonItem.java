package com.richitec.commontoolkit.customcomponent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;

import com.richitec.commontoolkit.R;
import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;

public class ImageBarButtonItem extends ImageButton {

	// normal background drawable
	private Drawable _mNormalBackgroundDrawable;
	// pressed background drawable
	private Drawable _mPressedBackgroundDrawable;

	public ImageBarButtonItem(Context context) {
		super(context);
	}

	// private constructor using bar button item style and normal background
	// drawable
	private ImageBarButtonItem(Context context,
			BarButtonItemStyle barBtnItemStyle,
			Drawable normalBackgroundDrawable) {
		this(context);

		// check bar button item style
		switch (barBtnItemStyle) {
		case LEFT_BACK:
			// set normal background drawable
			setBackgroundDrawable(null != normalBackgroundDrawable ? normalBackgroundDrawable
					: leftBarBtnItemNormalDrawable());
			break;

		case RIGHT_GO:
		default:
			// set normal background drawable
			setBackgroundDrawable(null != normalBackgroundDrawable ? normalBackgroundDrawable
					: rightBarBtnItemNormalDrawable());
			break;
		}
	}

	// init with image button resource id
	public ImageBarButtonItem(Context context, int resId) {
		this(context);

		//
	}

	// init with image button src drawable, bar button item style, normal
	// background drawable, pressed background drawable and button click
	// listener
	public ImageBarButtonItem(Context context, Drawable srcDrawable,
			BarButtonItemStyle barBtnItemStyle,
			Drawable normalBackgroundDrawable,
			Drawable pressedBackgroundDrawable, OnClickListener btnClickListener) {
		this(context, barBtnItemStyle, normalBackgroundDrawable);

		// set src scale type and drawable
		setScaleType(ScaleType.CENTER_INSIDE);
		setImageDrawable(srcDrawable);

		// set normal and pressed background drawable
		_mNormalBackgroundDrawable = normalBackgroundDrawable;
		_mPressedBackgroundDrawable = pressedBackgroundDrawable;

		// set on click listener
		setOnClickListener(btnClickListener);
	}

	public ImageBarButtonItem(Context context, Drawable srcDrawable,
			OnClickListener btnClickListener) {
		this(context, srcDrawable, BarButtonItemStyle.RIGHT_GO, null, null,
				btnClickListener);
	}

	public ImageBarButtonItem(Context context, Drawable srcDrawable,
			BarButtonItemStyle barBtnItemStyle, OnClickListener btnClickListener) {
		this(
				context,
				srcDrawable,
				barBtnItemStyle,
				BarButtonItemStyle.LEFT_BACK == barBtnItemStyle ? context
						.getResources().getDrawable(
								R.drawable.img_leftbarbtnitem_normal_bg)
						: (BarButtonItemStyle.RIGHT_GO == barBtnItemStyle ? context
								.getResources()
								.getDrawable(
										R.drawable.img_rightbarbtnitem_normal_bg)
								: null),
				BarButtonItemStyle.LEFT_BACK == barBtnItemStyle ? context
						.getResources().getDrawable(
								R.drawable.img_leftbarbtnitem_touchdown_bg)
						: (BarButtonItemStyle.RIGHT_GO == barBtnItemStyle ? context
								.getResources()
								.getDrawable(
										R.drawable.img_rightbarbtnitem_touchdown_bg)
								: null), btnClickListener);
	}

	public ImageBarButtonItem(Context context, int srcId,
			int normalBackgroundResId, int pressedBackgroundResId,
			OnClickListener btnClickListener) {
		this(context, BarButtonItemStyle.RIGHT_GO, context.getResources()
				.getDrawable(normalBackgroundResId));

		// set src scale type and drawable
		setScaleType(ScaleType.CENTER_INSIDE);
		setImageResource(srcId);

		// set normal and pressed background drawable
		_mNormalBackgroundDrawable = getResources().getDrawable(
				normalBackgroundResId);
		_mPressedBackgroundDrawable = getResources().getDrawable(
				pressedBackgroundResId);

		// set on click listener
		setOnClickListener(btnClickListener);
	}

	public ImageBarButtonItem(Context context, int srcId,
			OnClickListener btnClickListener) {
		this(context, context.getResources().getDrawable(srcId),
				btnClickListener);
	}

	public ImageBarButtonItem(Context context, int srcId,
			BarButtonItemStyle barBtnItemStyle, OnClickListener btnClickListener) {
		this(context, context.getResources().getDrawable(srcId),
				barBtnItemStyle, btnClickListener);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// set the image button background image based on whether the button in
		// its pressed state
		if (isPressed()) {
			if (null != _mPressedBackgroundDrawable) {
				setBackgroundDrawable(_mPressedBackgroundDrawable);
			}
		} else {
			if (null != _mNormalBackgroundDrawable) {
				setBackgroundDrawable(_mNormalBackgroundDrawable);
			}
		}

		super.onDraw(canvas);
	}

	// left bar button item normal drawable
	protected Drawable leftBarBtnItemNormalDrawable() {
		return getResources().getDrawable(
				R.drawable.img_leftbarbtnitem_normal_bg);
	}

	// right bar button item normal drawable
	protected Drawable rightBarBtnItemNormalDrawable() {
		return getResources().getDrawable(
				R.drawable.img_rightbarbtnitem_normal_bg);
	}

}
