package com.richitec.commontoolkit.utils;

import com.richitec.commontoolkit.CommonToolkitApplication;

public class DpPixUtils {

	public static int dp2pix(float dpValue) {
		float _scaledDensity = CommonToolkitApplication.getContext()
				.getResources().getDisplayMetrics().scaledDensity;

		return (int) (dpValue * _scaledDensity);
	}

	public static int pix2dp(float pixValue) {
		float _scaledDensity = CommonToolkitApplication.getContext()
				.getResources().getDisplayMetrics().scaledDensity;

		return (int) (pixValue / _scaledDensity);
	}

}
