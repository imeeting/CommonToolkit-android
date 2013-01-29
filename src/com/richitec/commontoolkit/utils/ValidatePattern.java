package com.richitec.commontoolkit.utils;

import java.util.regex.Pattern;

public class ValidatePattern {
	
	private static final String emailPattern = "^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$"; 
	private static final String namePattern = "[0-9A-Za-z_]*";
	private static final String phonePattern = "(^[0]\\d{2,3}\\-\\d{7,8})|(^[1-9]\\d{6,7})|(^[0]\\d{10,11})";
	private static final String phoneAndMobilePattern = "(^[0]\\d{2,3}\\-\\d{7,8})|(^[1-9]\\d{6,7})|(^[0]\\d{10,11})|(^[1][\\d]{10})";
	private static final String mobilePhonePattern = "^[1][\\d]{10}";
	private static final String numberPattern = "[0-9]+";
	private static final String moneyPattern = "([0-9]+)|([0-9]+\\.[0-9]{1,2})";
	private static final String faxPattern="(^[0]\\d{2,3}\\-\\d{7,8})|(^[1-9]\\d{6,7})|(^[0]\\d{10,11})";
	private static final String postCodePattern = "^[1-9]\\d{5}";
	private static final String datePattern = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-"
			+ "(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
	/**
	 * 验证Email格式
	 * @param email
	 * @return
	 */
	public static boolean isValidEmail(String email){ 
	     return Pattern.matches(emailPattern, email); 
	} 
	
	/**
	 * 只允许数字和字母
	 * @param name
	 * @return
	 */
	public static boolean isValidName(String name){
		return Pattern.matches(namePattern, name);
	}
	
	/**
	 * 电话格式
	 * @param name
	 * @return
	 */
	public static boolean isValidPhone(String phone){
		return Pattern.matches(phonePattern, phone);
	}
	
	/**
	 * 手机格式
	 * @param name
	 * @return
	 */
	public static boolean isValidMobilePhone (String mobilePhone){
		return Pattern.matches(mobilePhonePattern, mobilePhone);
	}
	
	/**
	 * 电话和手机格�?
	 * @param phone
	 * @return
	 */
	public static boolean isValidPhoneAndMobile(String phone){
		return Pattern.matches(phoneAndMobilePattern, phone);
	}
	
	/**
	 * 传真格式
	 * @param name
	 * @return
	 */
	public static boolean isValidFax (String fax){
		return Pattern.matches(faxPattern, fax);
	}
	
	/**
	 * 邮政格式
	 * @param name
	 * @return
	 */
	public static boolean isValidPostcode (String postcode){
		return Pattern.matches(postCodePattern, postcode);
	}
	
	/**
	 * 时间格式yyyy-MM-dd
	 * @param name
	 * @return
	 */
	public static boolean isValidDate (String date){
		return Pattern.matches(datePattern, date);
	}
	
	public static boolean isNumber(String number) {
		return Pattern.matches(numberPattern, number);
	}
	
	public static boolean isValidMoney(String money) {
		return Pattern.matches(moneyPattern, money);
	}
}
