package com.richitec.commontoolkit.user;

import java.util.HashMap;
import java.util.Map;

/**
 * User Bean
 * 
 * @author sk
 * 
 */
public class UserBean {
	private String name = "";
	private String password = "";
	private String userKey = "";
	private boolean rememberPwd;

	private Map<String, Object> store;
	
	private boolean inited;
	
	private void setAsInited() {
		inited = true;
	}
	
	public boolean isInited() {
		return inited;
	}
	
	public UserBean() {
		store = new HashMap<String, Object>();
		inited = false;
	}

	public UserBean(String name, String password, String userKey) {
		this.name = name;
		this.password = password;
		this.userKey = userKey;
		store = new HashMap<String, Object>();
		setAsInited();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setAsInited();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		setAsInited();
	}

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userkey) {
		this.userKey = userkey;
		setAsInited();
	}

	public boolean isRememberPwd() {
		return rememberPwd;
	}

	public void setRememberPwd(boolean rememberPwd) {
		this.rememberPwd = rememberPwd;
		setAsInited();
	}
	
	public void setValue(String key, Object value) {
		store.put(key, value);
		setAsInited();
	}
	
	public Object getValue(String key) {
		return store.get(key);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("username: ").append(name).append('\n');
		sb.append("password: ").append(password).append('\n');
		sb.append("userkey: ").append(userKey).append('\n');
		sb.append("rememberPwd: ").append(rememberPwd).append('\n');
		sb.append("map: ").append(store.toString());
		
		return sb.toString();
	}

}
