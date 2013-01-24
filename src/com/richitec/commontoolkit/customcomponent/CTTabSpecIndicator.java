package com.richitec.commontoolkit.customcomponent;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.richitec.commontoolkit.R;

public class CTTabSpecIndicator extends RelativeLayout {

	public CTTabSpecIndicator(Context context) {
		super(context);
	}

	// constructor with tab spec background, label text color, label and icon
	public CTTabSpecIndicator(Context context, int tabSpecBackground,
			int tabSpecLabel, int tabSpecIcon) {
		super(context);

		// get tab widget item relativeLayout
		RelativeLayout _tabWidgetItemRelativeLayout = (RelativeLayout) LayoutInflater
				.from(context).inflate(R.layout.tab_widget_item_layout, null)
				.findViewById(R.id.tabWidget_item_relativeLayout);

		// set common tab sepc indicator layout params
		setPadding(_tabWidgetItemRelativeLayout.getPaddingLeft(),
				_tabWidgetItemRelativeLayout.getPaddingTop(),
				_tabWidgetItemRelativeLayout.getPaddingRight(),
				_tabWidgetItemRelativeLayout.getPaddingBottom());

		// set common tab sepc indicator background
		setBackgroundResource(tabSpecBackground);

		// set common tab sepc indicator label and icon
		// get tab spec label textView
		TextView _tabSpecLabelTextView = (TextView) _tabWidgetItemRelativeLayout
				.findViewById(R.id.tabWidget_item_labelTextView);

		// set text
		_tabSpecLabelTextView.setText(tabSpecLabel);

		// get tab spec icon imageView
		ImageView _tabSpecIconImageView = (ImageView) _tabWidgetItemRelativeLayout
				.findViewById(R.id.tabWidget_item_iconImgView);

		// set image
		_tabSpecIconImageView.setImageResource(tabSpecIcon);

		// add tab spec label textView and icon imageView to common tab spec
		// indicator
		_tabWidgetItemRelativeLayout.removeAllViews();
		addView(_tabSpecLabelTextView);
		addView(_tabSpecIconImageView);
	}

	// constructor with tab spec label and icon
	public CTTabSpecIndicator(Context context, int tabSpecLabel, int tabSpecIcon) {
		this(context, R.drawable.tab_widget_item_bg, tabSpecLabel, tabSpecIcon);
	}

}
