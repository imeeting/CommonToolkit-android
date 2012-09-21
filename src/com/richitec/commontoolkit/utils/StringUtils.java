package com.richitec.commontoolkit.utils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class StringUtils {

	private static final String LOG_TAG = "StringUtils";

	// split the string with given split word
	public static String[] split(String string, String splitWord) {
		// define return string array
		String[] _ret = null;

		// define sentences vector
		Vector<String> _sentences = new Vector<String>();

		// append split word to end for splitting if not ended with it
		if (!string.endsWith(splitWord)) {
			string += splitWord;
		}

		// process not null or empty string
		if (null != string && !string.equals("")) {
			for (int i = 0, j = 0;;) {
				i = string.indexOf(splitWord, j);

				if (i >= 0 && i > j) {
					_sentences.addElement(string.substring(j, i));
				}

				if (i < 0 || i == (string.length() - splitWord.length())) {
					break;
				}

				j = i + splitWord.length();
			}

		}

		// reset return result
		if (_sentences.size() > 0) {
			_ret = new String[_sentences.size()];
			_sentences.copyInto(_ret);
		}

		return _ret;

	}

	// split the string with the two given split word
	public static String[] split(String string, String splitWord1,
			String splitWord2) {
		// define return string array
		String[] _ret = null;

		// define sentences vector
		Vector<String> _sentences = new Vector<String>();

		int i = 0;
		int j = 0;

		do {
			// get the first matched word
			i = string.indexOf(splitWord1, j);

			// get the following matched word
			if ((i + splitWord1.length()) < string.length()) {
				j = string.indexOf(splitWord2, i + splitWord1.length());
			}

			// if the i & j are not out of bound length of text
			if (j > i && j < string.length() && i < string.length()) {
				_sentences.addElement(string.substring(i + splitWord1.length(),
						j));
			}

			j = j + splitWord2.length();
		} while (string.indexOf(splitWord1, j) > 0
				&& string.indexOf(splitWord2, j) > 0);

		// reset return result
		if (_sentences.size() > 0) {
			_ret = new String[_sentences.size()];
			_sentences.copyInto(_ret);
		}

		return _ret;
	}

	// string md5
	public static String md5(String string) {
		// hex digit array
		char _hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };

		try {
			// get message digest instance and update message
			MessageDigest _MessageDigestInstance = MessageDigest
					.getInstance("MD5");
			_MessageDigestInstance.update(string.getBytes("UTF-8"));

			// get digest array and length
			byte[] _digests = _MessageDigestInstance.digest();
			int _digestsLength = _digests.length;

			// define return result
			char _ret[] = new char[_digestsLength * 2];

			// process string byte array
			for (int i = 0, k = 0; i < _digestsLength; i++) {
				byte _byte = _digests[i];
				_ret[k++] = _hexDigits[_byte >>> 4 & 0xf];
				_ret[k++] = _hexDigits[_byte & 0xf];
			}

			return new String(_ret);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	// perfect http request url
	public static String perfectHttpRequestUrl(String string) {
		String _ret = string;

		// check url is nil and has prefix "http://" or "https://"
		if (null != string && !string.equalsIgnoreCase("")
				&& !string.startsWith("http://")
				&& !string.startsWith("https://")) {
			StringBuilder _tmpStringBuilder = new StringBuilder("http://");
			_tmpStringBuilder.append(_ret);

			_ret = _tmpStringBuilder.toString();
		}

		return _ret;
	}

	// convert string to json object
	public static JSONObject toJSONObject(String string) {
		JSONObject _stringJsonObject = null;

		// check string
		if (null != string) {
			// convert
			try {
				_stringJsonObject = new JSONObject(string);
			} catch (JSONException e) {
				e.printStackTrace();

				Log.e(LOG_TAG, "Convert string = " + string
						+ " to json object exception: " + e.getMessage());
			}
		} else {
			Log.e(LOG_TAG, "Convert to json object string is null");
		}

		return _stringJsonObject;
	}

	// convert string to json array
	public static JSONArray toJSONArray(String string) {
		JSONArray _stringJsonArray = null;

		// check string
		if (null != string) {
			// convert
			try {
				_stringJsonArray = new JSONArray(string);
			} catch (JSONException e) {
				e.printStackTrace();

				Log.e(LOG_TAG, "Convert string = " + string
						+ " to json array exception: " + e.getMessage());
			}
		} else {
			Log.e(LOG_TAG, "Convert to json array string is null");
		}

		return _stringJsonArray;
	}

	// convert string to string array, according to Chinese character
	public static List<String> toStringList(String string) {
		List<String> _stringList = new ArrayList<String>();

		// define not Chinese character string builder
		StringBuilder _notCCSB = new StringBuilder();

		// get each char to process
		for (int i = 0; i < string.length(); i++) {
			// get char index string
			String _charIndexString = String.valueOf(string.charAt(i));

			// check the each char index string
			if (_charIndexString.matches("[\u4e00-\u9fa5]")) {
				// add not Chinese character string builder to string list and
				// clear it
				if (0 != _notCCSB.length()) {
					_stringList.add(_notCCSB.toString());
					_notCCSB.setLength(0);
				}

				// add character to string list
				_stringList.add(_charIndexString);
			} else {
				// append to not Chinese character string builder
				_notCCSB.append(_charIndexString);
			}

			// add not Chinese character string builder to string list in the
			// end
			if (string.length() - 1 == i && 0 != _notCCSB.length()) {
				_stringList.add(_notCCSB.toString());
			}
		}

		return _stringList;
	}

	// get sub string positions list in string
	public static List<Integer> subStringPositions(String string,
			String subString) {
		List<Integer> _positions = new ArrayList<Integer>();

		while (string.contains(subString)) {
			// get the first appearance position
			int _position = string.indexOf(subString);

			// add position to return position list
			_positions.add(0 == _positions.size() ? _position : _position
					+ _positions.get(_positions.size() - 1) + 1);

			// reset string
			string = string.substring(_position + 1);
		}

		return _positions;
	}

	// phone number from string
	public static String phoneNumberFromString(String string) {
		StringBuffer _ret = new StringBuffer();

		// process not null string
		if (null != string) {
			for (int i = 0; i < string.length(); i++) {
				// get each character
				char _char = string.charAt(i);

				// check character, and append phone number like character
				if ((_char <= '9' && _char >= '0') || _char == '+') {
					_ret.append(_char);
				}
			}
		}

		return _ret.toString();
	}

	// number from string
	public static String numberFromString(String string) {
		StringBuffer _ret = new StringBuffer();

		// process not null string
		if (null != string) {
			for (int i = 0; i < string.length(); i++) {
				// get each character
				char _char = string.charAt(i);

				// check character, and append numeric character
				if (_char <= '9' && _char >= '0') {
					_ret.append(_char);
				}
			}
		}

		return _ret.toString();
	}

	// replace string
	public static String replace(String string, String original,
			String replacement) {
		// find the original string position of the string
		int _pos = string.indexOf(original);

		// found it
		if (-1 != _pos) {

			StringBuffer _tmp = new StringBuffer();

			// define the last matched position
			int _lastPos = 0;

			do {
				_tmp.append(string.substring(_lastPos, _pos)).append(
						replacement);

				// save last matched and reset matched position
				_lastPos = _pos + original.length();
				_pos = string.indexOf(original, _lastPos);
			} while (-1 != _pos);

			// append last
			_tmp.append(string.substring(_lastPos));

			// reset the string
			string = _tmp.toString();
		}

		return string;
	}

}
