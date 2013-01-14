package com.richitec.commontoolkit.user;

import android.util.Log;

import com.richitec.commontoolkit.utils.StringUtils;

public class UserManager {

	// singleton instance
	private static UserManager _singletonInstance;

	// user bean
	private UserBean userBean;

	private UserManager() {
		Log.d("commontoolkit", "constrct UserManager");
		// init user bean
		userBean = new UserBean();
	}

	// get userManager singleton instance
	public static UserManager getInstance() {
		if (null == _singletonInstance) {
			synchronized (UserManager.class) {
				if (null == _singletonInstance) {
					_singletonInstance = new UserManager();
				}
			}
		}

		return _singletonInstance;
	}

	// set user with user name and password
	public UserBean setUser(String pName, String pPassword) {
		if (userBean == null) {
			userBean = new UserBean();
		}
		// set user bean
		userBean.setName(pName);
		userBean.setPassword(StringUtils.md5(pPassword));
		return userBean;
	}

	public UserBean setUserKey(String userKey) {
		if (userBean == null) {
			userBean = new UserBean();
		}
		userBean.setUserKey(userKey);
		return userBean;
	}

	/**
	 * set user
	 * 
	 * @param user
	 */
	public void setUser(UserBean user) {
		this.userBean = user;
	}

	/**
	 * get user
	 * 
	 * @return user bean or null
	 */
	public UserBean getUser() {
		return userBean;
	}

	// remove the user
	public void removeUser() {
		userBean = null;
	}

}
