package com.richitec.commontoolkit.customadapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class CommonListAdapter extends BaseAdapter {

	// alphabet key
	public static final String ALPHABET_INDEX = "alphabet_index_key";

	// layout inflater
	protected LayoutInflater _mLayoutInflater;
	// data
	protected List<Map<String, ?>> _mData;
	// items layout resource id
	protected int _mItemsLayoutResId;
	// data keys
	protected String[] _mDataKeys;
	// items component resource identities
	protected int[] _mItemsComponentResIds;

	public CommonListAdapter(Context context, List<Map<String, ?>> data,
			int itemsLayoutResId, String[] dataKeys, int[] itemsComponentResIds) {
		// save layout inflater, data, items layout resource id, data keys and
		// items component resource identities
		_mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_mData = null == data ? new ArrayList<Map<String, ?>>() : data;
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

	// set data
	public CommonListAdapter setData(List<Map<String, ?>> data) {
		// set data
		_mData = data;

		// notify data set changed
		notifyDataSetChanged();

		return this;
	}

	// append data
	public CommonListAdapter appendData(List<Map<String, ?>> data) {
		// append data
		_mData.addAll(data);

		// notify data set changed
		notifyDataSetChanged();

		return this;
	}

	public CommonListAdapter appendData(Map<String, ?> data) {
		// append data
		_mData.add(data);

		// notify data set changed
		notifyDataSetChanged();

		return this;
	}

	// remove data
	public CommonListAdapter removeData(Map<String, ?> data) {
		// append data
		_mData.remove(data);

		// notify data set changed
		notifyDataSetChanged();

		return this;
	}

	public CommonListAdapter removeData(int data) {
		// append data
		_mData.remove(data);

		// notify data set changed
		notifyDataSetChanged();

		return this;
	}

	// get quick alphabet bar alphabet set
	public Set<String> getAlphabet() {
		Set<String> _alphabet = new HashSet<String>();

		// init alphabet
		for (Map<String, ?> _data : _mData) {
			// check if existed alphabet
			if (_data.keySet().contains(ALPHABET_INDEX)) {
				_alphabet.add((String) _data.get(ALPHABET_INDEX));
			}
		}

		return _alphabet;
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
