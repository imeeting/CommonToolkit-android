package com.richitec.commontoolkit.utils;

/**
 * HexUtils
 * @author sk
 *
 */
public class HexUtils {
	private static final char[] HEX_DIGITS = new char[] { '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * convert byte array to hex string format
	 * @param bytes
	 * @return
	 */
	public static String convert(byte[] bytes) {
		StringBuffer sb = new StringBuffer();

		if (bytes != null) {
			for (int i = 0; i < bytes.length; i++) {
				short low = (short) (bytes[i] & (byte) 0x0f);
				short high = (short) (((short) (bytes[i]) & 0x00ff) >>> 4);
				sb.append(HEX_DIGITS[high]).append(HEX_DIGITS[low]);
			}
		}

		return sb.toString();
	}

	/**
	 * convert hex string format to byte array
	 * @param hexString
	 * @return
	 */
	public static byte[] convert(String hexString) {
		byte[] bytes = null;
		if (hexString != null) {
			if (hexString.length() % 2 != 0) {
				hexString = '0' + hexString;
			}
			bytes = new byte[hexString.length() / 2];
			for (int i = 0, j = 0; i < hexString.length(); i += 2, j++) {
				char highHex = hexString.charAt(i);
				char lowHex = hexString.charAt(i + 1);
				try {
					byte high = convertHex2Byte(highHex);
					byte low = convertHex2Byte(lowHex);

					byte num = (byte) ((byte) ((high << 4) & 0xf0) | (byte) (low & 0x0f));
					bytes[j] = num;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return bytes;
	}

	private static byte convertHex2Byte(char hex) throws Exception {
		byte num = 0;
		switch (hex) {
		case '0':
			num = 0;
			break;

		case '1':
			num = 1;
			break;

		case '2':
			num = 2;
			break;

		case '3':
			num = 3;
			break;

		case '4':
			num = 4;
			break;

		case '5':
			num = 5;
			break;

		case '6':
			num = 6;
			break;

		case '7':
			num = 7;
			break;

		case '8':
			num = 8;
			break;

		case '9':
			num = 9;
			break;

		case 'a':
		case 'A':
			num = 10;
			break;

		case 'b':
		case 'B':
			num = 11;
			break;

		case 'c':
		case 'C':
			num = 12;
			break;

		case 'd':
		case 'D':
			num = 13;
			break;

		case 'e':
		case 'E':
			num = 14;
			break;

		case 'f':
		case 'F':
			num = 15;
			break;

		default:
			throw new Exception("wrong char");
		}
		return num;
	}

}
