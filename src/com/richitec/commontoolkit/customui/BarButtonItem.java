package com.richitec.commontoolkit.customui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.Button;

import com.richitec.commontoolkit.activityextension.R;

public class BarButtonItem extends Button {

	// normal background drawable
	private Drawable _mNormalBackgroundDrawable;
	// pressed background drawable
	private Drawable _mPressedBackgroundDrawable;

	public BarButtonItem(Context context) {
		super(context);
	}

	//
	public BarButtonItem(Context context, int resId) {
		super(context);

		//
	}

	// init with button title, normal background drawable, pressed background
	// drawable and button click listener
	public BarButtonItem(Context context, CharSequence title,
			Drawable normalBackgroundDrawable,
			Drawable pressedBackgroundDrawable, OnClickListener btnClickListener) {
		super(context);

		// set title and title color
		setText(null == title ? "" : title);
		setTextColor(Color.WHITE);

		// set normal and pressed background drawable
		_mNormalBackgroundDrawable = normalBackgroundDrawable;
		_mPressedBackgroundDrawable = pressedBackgroundDrawable;

		// set on click listener
		setOnClickListener(btnClickListener);
	}

	public BarButtonItem(Context context, CharSequence title,
			OnClickListener btnClickListener) {
		this(context, title, null, null, btnClickListener);
	}

	public BarButtonItem(Context context, BarButtonItemStyle barBtnItemStyle,
			CharSequence title, OnClickListener btnClickListener) {
		this(
				context,
				title,
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

	public BarButtonItem(Context context, int titleId,
			int normalBackgroundResId, int pressedBackgroundResId,
			OnClickListener btnClickListener) {
		super(context);

		// set title and title color
		setText(titleId);
		setTextColor(Color.WHITE);

		// set normal and pressed background drawable
		_mNormalBackgroundDrawable = getResources().getDrawable(
				normalBackgroundResId);
		_mPressedBackgroundDrawable = getResources().getDrawable(
				pressedBackgroundResId);

		// set on click listener
		setOnClickListener(btnClickListener);
	}

	public BarButtonItem(Context context, int titleId,
			OnClickListener btnClickListener) {
		this(context, context.getResources().getString(titleId),
				btnClickListener);
	}

	public BarButtonItem(Context context, BarButtonItemStyle barBtnItemStyle,
			int titleId, OnClickListener btnClickListener) {
		this(context, barBtnItemStyle, context.getResources()
				.getString(titleId), btnClickListener);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// set the button background image based on whether the button in its
		// pressed state
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

	public void setNormalBackgroundDrawable(Drawable normalBackgroundDrawable) {
		_mNormalBackgroundDrawable = normalBackgroundDrawable;
	}

	public void setPressedBackgroundDrawable(Drawable pressedBackgroundDrawable) {
		_mPressedBackgroundDrawable = pressedBackgroundDrawable;
	}

	// inner class
	// bar button item style
	public static enum BarButtonItemStyle {
		LEFT_BACK, RIGHT_GO
	}

}
