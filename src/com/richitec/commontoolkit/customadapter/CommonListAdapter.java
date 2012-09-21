package com.richitec.commontoolkit.customadapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class CommonListAdapter extends BaseAdapter {

	// layout inflater
	protected LayoutInflater _mLayoutInflater;
	// data
	protected List<? extends Map<String, ?>> _mData;
	// items layout resource id
	protected int _mItemsLayoutResId;
	// data keys
	protected String[] _mDataKeys;
	// items component resource identities
	protected int[] _mItemsComponentResIds;

	public CommonListAdapter(Context context,
			List<? extends Map<String, ?>> data, int itemsLayoutResId,
			String[] dataKeys, int[] itemsComponentResIds) {
		// save layout inflater, data, items layout resource id, data keys and
		// items component resource identities
		_mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_mData = data;
		_mItemsLayoutResId = itemsLayoutResId;
		_mDataKeys = dataKeys;
		_mItemsComponentResIds = itemsComponentResIds;
	}

	public void setData(List<? extends Map<String, ?>> data) {
		this._mData = data;
	}
	
	public List<? extends Map<String, ?>> getData() {
		return _mData;
	}
	
	@Override
	public int getCount() {
		return _mData.size();
	}

	@Override
	public Object getItem(int position) {
		return _mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// define view holer object
		ViewHolder _viewHolder;

		// check convert view
		if (null == convertView) {
			convertView = _mLayoutInflater.inflate(_mItemsLayoutResId, null);

			// init view holder and set its views for holding
			_viewHolder = new ViewHolder();
			// set item component view subViews
			for (int i = 0; i < _mItemsComponentResIds.length; i++) {
				_viewHolder.views4Holding.append(_mItemsComponentResIds[i],
						convertView.findViewById(_mItemsComponentResIds[i]));
			}

			// set tag
			convertView.setTag(_viewHolder);
		} else {
			// get view holder
			_viewHolder = (ViewHolder) convertView.getTag();
		}

		// set item component view subViews
		for (int i = 0; i < _mItemsComponentResIds.length; i++) {
			// bind item component view data
			// bindView(convertView.findViewById(_mItemsComponentResIds[i]),
			// _mData.get(position), _mDataKeys[i]);
			bindView(_viewHolder.views4Holding.get(_mItemsComponentResIds[i]),
					_mData.get(position), _mDataKeys[i]);
		}

		return convertView;
	}

	// bind view and data
	protected abstract void bindView(View view, Map<String, ?> dataMap,
			String dataKey);

	// inner class
	class ViewHolder {
		// views for holding
		SparseArray<View> views4Holding;

		public ViewHolder() {
			super();

			// init views sparse array for holding
			views4Holding = new SparseArray<View>();
		}

	}

}
