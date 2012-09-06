package com.richitec.commontoolkit.user;

import com.richitec.commontoolkit.utils.StringUtils;

public class UserManager {

	// singleton instance
	private static volatile UserManager _singletonInstance;

	// user bean
	private UserBean userBean;

	private UserManager() {
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

	// get user
	public UserBean getUser() {
		return _mUserBean;
	}

	// set user with user name and password
	public void setUser(String pName, String pPassword) {
		// set user bean
		userBean.setName(pName);
		userBean.setPassword(StringUtils.md5(pPassword));
	}

	public void setUserKey(String userKey) {
		userBean.setUserkey(userKey);
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
