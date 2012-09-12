package com.richitec.commontoolkit.activityextension;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.richitec.commontoolkit.customcomponent.BarButtonItem;
import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.commontoolkit.customcomponent.IBackBarButtonItemDrawable;

public class NavigationActivity extends Activity implements
		IBackBarButtonItemDrawable {

	private static final String LOG_TAG = "NavigationActivity";

	// commonToolkit navigation activity onCreate param key
	private static final String NAV_ACTIVITY_PARAM_KEY = "nav_back_btn_default_title";

	// nav bar back button item
	private BarButtonItem _mBackBarBtnItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get the intent parameter data
		Bundle _data = getIntent().getExtras();

		// check the data bundle
		if (null != _data && null != _data.getString(NAV_ACTIVITY_PARAM_KEY)) {
			// init default nav bar back button item
			_mBackBarBtnItem = new BarButtonItem(this,
					_data.getString(NAV_ACTIVITY_PARAM_KEY),
					BarButtonItemStyle.LEFT_BACK,
					backBarBtnItemNormalDrawable(),
					backBarBtnItemPressedDrawable(), new OnClickListener() {

						@Override
						public void onClick(View v) {
							// finish self activity
							finish();

						}
					});
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		// set content view
		super.setContentView(R.layout.activity_navigation);

		LinearLayout body = (LinearLayout) findViewById(R.id.navContent_relativeLayout);
		body.removeAllViews();
		// set parameter view to navigation content linearLayout
		getLayoutInflater().inflate(layoutResID, body);

		// set nav bar back button item, if not null
		if (null != _mBackBarBtnItem) {
			setLeftBarButtonItem(_mBackBarBtnItem);
		}
	}

	public View getBody() {
		View body = findViewById(R.id.navContent_relativeLayout);
		return body;
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		Log.d(LOG_TAG, "navigation activity set content view, view: " + view
				+ " and layout params: " + params);

		// set content view
		super.setContentView(view, params);
	}

	@Override
	public void setContentView(View view) {
		// set content view
		super.setContentView(view);

		LinearLayout body = (LinearLayout) findViewById(R.id.navContent_relativeLayout);
		body.removeAllViews();
		// set parameter view to navigation content linearLayout
		body.addView(view);

		// set nav bar back button item, if not null
		if (null != _mBackBarBtnItem) {
			setLeftBarButtonItem(_mBackBarBtnItem);
		}
	}

	// set navBar background color
	public void setNavBarBackgroundColor(int navBarBackgroundColor) {
		((RelativeLayout) findViewById(R.id.navBar_relativeLayout))
				.setBackgroundColor(navBarBackgroundColor);
	}

	// set navBar background resource
	public void setNavBarBackgroundResource(int navBarBackgroundResId) {
		((RelativeLayout) findViewById(R.id.navBar_relativeLayout))
				.setBackgroundResource(navBarBackgroundResId);
	}

	// set navBar background drawable
	public void setNavBarBackgroundDrawable(Drawable navBarBackgroundDrawable) {
		((RelativeLayout) findViewById(R.id.navBar_relativeLayout))
				.setBackgroundDrawable(navBarBackgroundDrawable);
	}

	// set left bar button item
	public void setLeftBarButtonItem(BarButtonItem barButtonItem) {
		// remove default navigation bar left button item and add the new one
		FrameLayout _leftBtnLayout = (FrameLayout) findViewById(R.id.left_btn_frameLayout);
		_leftBtnLayout.removeAllViews();
		_leftBtnLayout.addView(barButtonItem);
	}

	// set right bar button item
	public void setRightBarButtonItem(BarButtonItem barButtonItem) {
		// remove default navigation bar left button item and add the new one
		FrameLayout _rightBtnLayout = (FrameLayout) findViewById(R.id.right_btn_frameLayout);
		_rightBtnLayout.removeAllViews();
		_rightBtnLayout.addView(barButtonItem);
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);

		// set title textView text
		((TextView) findViewById(R.id.title_textView)).setText(title);
	}

	@Override
	public void setTitle(int titleId) {
		super.setTitle(titleId);

		// set title textView text
		((TextView) findViewById(R.id.title_textView)).setText(titleId);
	}

	@Override
	public void setTitleColor(int textColor) {
		super.setTitleColor(textColor);

		// set title textView text color
		((TextView) findViewById(R.id.title_textView)).setTextColor(textColor);
	}

	// set title font
	public void setTitleSize(int textSize) {
		// set title textView text font size
		((TextView) findViewById(R.id.title_textView)).setTextSize(textSize);
	}

	// push activity with extra data to navigation activity stack
	public void pushActivity(Class<? extends Activity> activityClass,
			Map<String, ?> extraData) {
		// define the intent
		Intent _intent = new Intent(this, activityClass);

		// set intent extra parameter data
		_intent.putExtra(NAV_ACTIVITY_PARAM_KEY, (String) this.getTitle());

		if (null != extraData) {
			for (String extraDataKey : extraData.keySet()) {
				// String
				if (extraData.get(extraDataKey) instanceof String) {
					_intent.putExtra(extraDataKey,
							(String) extraData.get(extraDataKey));
				}
				// others, not implementation
				else {
					Log.d(LOG_TAG, "Type except of String not implementation");
				}
			}
		}

		// go to the activity
		startActivity(_intent);
	}

	// push activity to navigation activity stack
	public void pushActivity(Class<? extends Activity> activityClass) {
		this.pushActivity(activityClass, null);
	}

	// pop this activity from activity stack
	public void popActivity() {
		// finish self activity
		finish();
	}

	@Override
	public Drawable backBarBtnItemNormalDrawable() {
		return getResources().getDrawable(
				R.drawable.img_leftbarbtnitem_normal_bg);
	}

	@Override
	public Drawable backBarBtnItemPressedDrawable() {
		return getResources().getDrawable(
				R.drawable.img_leftbarbtnitem_touchdown_bg);
	}

}
