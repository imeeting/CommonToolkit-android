package com.richitec.websocket.notifier;

import org.json.JSONObject;

public interface NotifierCallbackListener {
	public void doAction(String event, JSONObject data);
}
