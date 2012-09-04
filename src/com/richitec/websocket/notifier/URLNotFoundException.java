package com.richitec.websocket.notifier;

/**
 * URLNotFoundException
 * @author sk
 *
 */
public class URLNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6319465154294876713L;

	public URLNotFoundException() {
		super("URL not found!");
	}
}
