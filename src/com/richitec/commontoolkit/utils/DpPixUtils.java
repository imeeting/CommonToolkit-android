package com.richitec.commontoolkit.utils;

import com.richitec.commontoolkit.CTApplication;

public class DpPixUtils {

	public static int dp2pix(float dpValue) {
		float _scaledDensity = CTApplication.getContext()
				.getResources().getDisplayMetrics().scaledDensity;

		return (int) (dpValue * _scaledDensity);
	}

	public static int pix2dp(float pixValue) {
		float _scaledDensity = CTApplication.getContext()
				.getResources().getDisplayMetrics().scaledDensity;

		return (int) (pixValue / _scaledDensity);
	}

}
