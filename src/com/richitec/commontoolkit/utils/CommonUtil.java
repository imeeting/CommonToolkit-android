package com.richitec.commontoolkit.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonUtil {

	public static String md5(String text) {
		String digestText = "";
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			byte[] digest = digester.digest(text.getBytes("UTF-8"));
			digestText = HexUtils.convert(digest);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return digestText;
	}

	
	
}
