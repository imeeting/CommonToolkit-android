package com.richitec.commontoolkit.utils;

import java.util.ArrayList;
import java.util.List;

public class CommonUtils {

	// convert array to list
	public static List<?> array2List(Object[] array) {
		List<Object> _ret = new ArrayList<Object>();

		if (null != array) {
			for (int i = 0; i < array.length; i++) {
				_ret.add(array[i]);
			}
		}

		return _ret;
	}

}
