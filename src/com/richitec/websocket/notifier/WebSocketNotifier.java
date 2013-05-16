package com.richitec.websocket.notifier;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.clwillingham.socket.io.IOSocket;
import com.clwillingham.socket.io.MessageCallback;

public class WebSocketNotifier {
	private IOSocket sk;
	private String serverAddress;

	private String topic = "";
	private String subscriberID = "";
	private NotifierCallbackListener actionListener;

	private boolean needConnect = true;

	public WebSocketNotifier() {
	}

	private void initialize() throws URLNotFoundException {
		if (serverAddress != null && !serverAddress.equals("")) {
			System.setProperty("java.net.preferIPv6Addresses", "false");
			sk = new IOSocket(serverAddress, new MessageCallback() {

				@Override
				public void onMessage(JSONObject json) {
					System.out.println("onMessage : " + json);
				}

				@Override
				public void onMessage(String message) {
					System.out.println("onMessage : " + message);
				}

				@Override
				public void onDisconnect() {
					System.out.println("onDisconnect");
					if (needConnect) {
						// try {
						connect();
						// } catch (IOException e) {
						// e.printStackTrace();
						// }
					}
				}

				@Override
				public void onConnectFailure() {
					System.out.println("onConnectFailure");
				}

				@Override
				public void onConnect() {
					// do subscription
					System.out.println("onConnect");
					JSONObject message = new JSONObject();
					try {
						message.put("topic", topic);
						message.put("subscriber_id", subscriberID);
						sk.emit("subscribe", message);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void on(String event, JSONObject... data) {
					for (JSONObject obj : data) {
						System.out.println("on " + event + " : " + obj);
						if (actionListener != null) {
							actionListener.doAction(event, obj);
						}
					}

				}
			});
		} else {
			throw new URLNotFoundException();
		}
	}

	public void setServerAddress(String addressUrl) {
		this.serverAddress = addressUrl;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void setSubscriberID(String id) {
		this.subscriberID = id;
	}

	public void setNotifierActionListener(NotifierCallbackListener lis) {
		this.actionListener = lis;
	}

	public void connect() {
		needConnect = true;
		try {
			if (sk == null) {
				initialize();
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						sk.connect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (URLNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		needConnect = false;
		if (sk != null) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					sk.disconnect();
					sk = null;
				}
			}).start();
		}

	}

	public void reconnect() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				needConnect = false;
				if (sk != null) {
					sk.disconnect();
				}
				sk = null;

				connect();
			}
		}).start();
	}

}
