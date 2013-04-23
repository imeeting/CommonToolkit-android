package com.richitec.commontoolkit.customcomponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.R;
import com.richitec.commontoolkit.customadapter.CTListAdapter;
import com.richitec.commontoolkit.utils.DisplayScreenUtils;

public class CTMenu extends PopupWindow {

	private static final String LOG_TAG = CTMenu.class.getCanonicalName();

	// menu item tag key
	private final Integer MENU_ITEM_TAG = -333;

	// commonToolkit menu item adapter date keys
	private final String MENUITEM_ID = "menu_item_id";
	private final String MENUITEM_ICON = "menu_item_icon";
	private final String MENUITEM_TITLE = "menu_item_title";

	// menu item listView
	private ListView _mMenuItemListView;

	// menu item adapter data list
	private final List<Map<String, ?>> _mMenuItemAdpaterDataList = new ArrayList<Map<String, ?>>();

	// menu on item selected listener
	private CTMenuOnItemSelectedListener _mMenuOnItemSelectedListener;

	// menu style, default is text menu item
	private CTMenuStyle _mMenuStyle = CTMenuStyle.TEXT_ITEM;

	public CTMenu(Context context) {
		this(R.layout.commontoolkit_menu_layout, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	// constructor with popup menu layout resource id
	public CTMenu(int resource, int width, int height) {
		super(((LayoutInflater) CTApplication.getContext().getSystemService(
				Activity.LAYOUT_INFLATER_SERVICE)).inflate(resource, null),
				width, height, false);

		// update content view padding
		getContentView().setPadding(
				getContentView().getPaddingLeft(),
				getContentView().getPaddingTop()
						+ DisplayScreenUtils.dp2pix(14.0f),
				getContentView().getPaddingRight(),
				getContentView().getPaddingBottom());

		// get menu item listView and init
		_mMenuItemListView = (ListView) getContentView().findViewById(
				R.id.commonToolkit_menu_listView);

		// set background drawable and outside touchable, important the order of
		// the the following called methods
		setBackgroundDrawable(new BitmapDrawable());
		setOutsideTouchable(true);
		setFocusable(true);
		for (int i = 0; i < ((ViewGroup) getContentView()).getChildCount(); i++) {
			((ViewGroup) getContentView()).getChildAt(i)
					.setFocusableInTouchMode(true);
		}

		// set menu item listView adapter
		_mMenuItemListView.setAdapter(new CTMenuItemAdapter(CTApplication
				.getContext(), _mMenuItemAdpaterDataList,
				R.layout.commontoolkit_menuitem_layout, new String[] {
						MENUITEM_ID, MENUITEM_ICON, MENUITEM_TITLE },
				new int[] { R.id.commonToolkit_menuItem_relativeLayout,
						R.id.commonToolkit_menuItem_iconImageView,
						R.id.commonToolkit_menuItem_textView }));

		// set menu item on item click listener
		_mMenuItemListView
				.setOnItemClickListener(new CTMenuListViewOnItemClickListener());
	}

	// set commonToolkit menu style
	public void setMenuStyle(CTMenuStyle menuStyle) {
		_mMenuStyle = menuStyle;
	}

	// set commonToolkit menu on item selected listener
	public void setMenuOnItemSelectedListener(
			CTMenuOnItemSelectedListener menuOnItemSelectedListener) {
		_mMenuOnItemSelectedListener = menuOnItemSelectedListener;
	}

	// add menu item
	public void add(int itemId, CharSequence title, Drawable icon) {
		Map<String, Object> _dataMap = new HashMap<String, Object>();

		// set data
		_dataMap.put(MENUITEM_ID, itemId);
		_dataMap.put(MENUITEM_ICON, icon);
		_dataMap.put(MENUITEM_TITLE, title);

		_mMenuItemAdpaterDataList.add(_dataMap);

		// notify menu item data set changed
		((CTMenuItemAdapter) _mMenuItemListView.getAdapter())
				.notifyDataSetChanged();
	}

	public void add(int itemId, CharSequence title) {
		add(itemId, title, null);
	}

	public void add(int itemId, int titleRes, int iconRes) {
		add(itemId,
				CTApplication.getContext().getResources().getString(titleRes),
				CTApplication.getContext().getResources().getDrawable(iconRes));
	}

	public void add(int itemId, int titleRes) {
		add(itemId,
				CTApplication.getContext().getResources().getString(titleRes),
				null);
	}

	// inner class
	// commonToolkit menu style
	public enum CTMenuStyle {
		TEXT_ITEM, ITEM_WITHICON
	}

	// commonToolkit menu item adapter
	class CTMenuItemAdapter extends CTListAdapter {

		public CTMenuItemAdapter(Context context, List<Map<String, ?>> data,
				int itemsLayoutResId, String[] dataKeys,
				int[] itemsComponentResIds) {
			super(context, data, itemsLayoutResId, dataKeys,
					itemsComponentResIds);
		}

		@Override
		protected void bindView(View view, Map<String, ?> dataMap,
				String dataKey) {
			// get item date object
			Object _itemData = dataMap.get(dataKey);

			// check view type
			// textView
			if (view instanceof TextView) {
				// set view text
				((TextView) view)
						.setText(null == _itemData ? ""
								: _itemData instanceof SpannableString ? (SpannableString) _itemData
										: _itemData.toString());
			} else if (view instanceof ImageView) {
				try {
					// define item data drawable and convert item data to
					// drawable
					Drawable _itemDataDrawable = (Drawable) _itemData;

					// check commonToolkit menu style
					switch (_mMenuStyle) {
					case ITEM_WITHICON:
						// chech and set view image drawable
						if (null != _itemDataDrawable) {
							((ImageView) view)
									.setImageDrawable(_itemDataDrawable);
						}

						// show menu icon
						if (View.VISIBLE != view.getVisibility()) {
							view.setVisibility(View.VISIBLE);
						}
						break;

					case TEXT_ITEM:
					default:
						// hide the view
						view.setVisibility(View.GONE);
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();

					Log.e(LOG_TAG,
							"Convert item date to drawable error, item data = "
									+ _itemData);
				}
			} else if (view instanceof RelativeLayout) {
				try {
					// define item data integer and convert item data to integer
					Integer _itemDataInteger = (Integer) _itemData;

					// set view tag
					view.setTag(MENU_ITEM_TAG, _itemDataInteger);
				} catch (Exception e) {
					e.printStackTrace();

					Log.e(LOG_TAG,
							"Convert item date to integer error, item data = "
									+ _itemData);
				}
			}
		}

	}

	// commonToolkit menu listView on item click listener
	class CTMenuListViewOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// check menu on item selected listener
			if (null != _mMenuOnItemSelectedListener) {
				// get menu item view tag
				Object _menuItemViewTag = view.getTag(MENU_ITEM_TAG);

				// check menu item view tag
				if (_menuItemViewTag instanceof Integer) {
					_mMenuOnItemSelectedListener.onMenuItemSelected(
							CTMenu.this, (Integer) _menuItemViewTag);
				} else {
					Log.w(LOG_TAG,
							"Get menu item view tag error, menu item view tag = "
									+ _menuItemViewTag);
				}
			} else {
				Log.w(LOG_TAG,
						"CommonToolkit menu item on item selected listener is null, menu list = "
								+ parent + " item = " + view
								+ " and click item position = " + position);
			}
		}

	}

	// commonToolkit menu item on item selected
	public static interface CTMenuOnItemSelectedListener {

		// on menu item selected
		public boolean onMenuItemSelected(CTMenu menu, int menuItemId);

	}

}
