package com.richitec.commontoolkit.customadapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public abstract class CommonListCursorAdapter extends CursorAdapter {

	private static final String LOG_TAG = "CommonListCursorAdapter";

	// context
	protected Context _mContext;
	// layout inflater
	protected LayoutInflater _mLayoutInflater;
	// items layout resource id
	protected int _mItemsLayoutResId;
	// data keys
	protected String[] _mDataKeys;
	// items component resource identities
	protected int[] _mItemsComponentResIds;

	// cursor data list
	protected final List<Object> _data = new ArrayList<Object>();

	public CommonListCursorAdapter(Context context, int itemsLayoutResId,
			Cursor c, String[] dataKeys, int[] itemsComponentResIds) {
		super(context, c);

		// save context, layout inflater, items layout resource id, data keys
		// and items component resource identities
		_mContext = context;
		_mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_mItemsLayoutResId = itemsLayoutResId;
		_mDataKeys = dataKeys;
		_mItemsComponentResIds = itemsComponentResIds;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// check cursor position and append cursor data
		if (cursor.getPosition() >= _data.size()) {
			appendCursorData(_data, cursor);
		} else {
			Log.d(LOG_TAG, "Rollback, mustn't append cursor data");
		}

		// set item component view subViews
		for (int i = 0; i < _mItemsComponentResIds.length; i++) {
			// recombination data and bind item component view data
			bindView(
					view.findViewById(_mItemsComponentResIds[i]),
					recombinationData(_mDataKeys[i],
							_data.get(cursor.getPosition())), _mDataKeys[i]);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return _mLayoutInflater.inflate(_mItemsLayoutResId, parent, false);
	}

	public List<Object> getDataList() {
		return _data;
	}

	// append cursor data
	protected abstract void appendCursorData(List<Object> data, Cursor cursor);

	// recombination data
	protected abstract Map<String, ?> recombinationData(String dataKey,
			Object dataObject);

	// bind view and data
	protected abstract void bindView(View view, Map<String, ?> dataMap,
			String dataKey);

}
