package com.richitec.commontoolkit.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtil.HeadResponseListener;
import com.richitec.commontoolkit.utils.HttpUtil.ResponseListener;

@Deprecated
public class HttpUtil {

	private DefaultHttpClient httpClient;
	int timeoutConnection = 10000;
	int timeoutSocket = 20000;

	public void initHttpClient() {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		httpClient = new DefaultHttpClient(httpParameters);
	}

	static public abstract class ResponseListener {
		public abstract void onComplete(int status, String responseText);
	};

	static public interface HeadResponseListener {
		public void onComplete(int status, HttpEntity entity);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doHttpHead(String uri,
			final HeadResponseListener responseListener) {
		DefaultHttpClient _httpClient = null;

		CookieStore _cookieStore = httpClient.getCookieStore();

		if (_cookieStore.getCookies() == null
				|| _cookieStore.getCookies().size() <= 0)
			_httpClient = httpClient;
		else {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			_httpClient = new DefaultHttpClient(httpParameters);
			_httpClient.setCookieStore(_cookieStore);
		}

		HttpHead head = new HttpHead(uri);

		final ResponseHandler _responseHandler = new ResponseHandler() {

			@Override
			public Object handleResponse(HttpResponse response) {
				try {
					if (responseListener != null)
						responseListener.onComplete(response.getStatusLine()
								.getStatusCode(), response.getEntity());

				} catch (Exception e) {
					e.printStackTrace();
					if (responseListener != null)
						responseListener.onComplete(-1, null);
				}
				return null;
			}
			// 使用ResponseHandler来创建一个异步的Http调用

		};
		// 发送请求并等待响应
		try {
			_httpClient.execute(head, _responseHandler);
		} catch (Exception ex) {
			// 网络连接失败,error code 为-1
			if (responseListener != null)
				responseListener.onComplete(-1, null);
		}
	}

	public void doHttpRequest(String _uriAPI, Map<String, String> _argsMap,
			final ResponseListener _responseListener) {

		Object[] _requestParamNameArray = null;
		Object[] _requestParamValueArray = null;

		if (_argsMap != null) {
			// NameValuePair实现请求参数的封装
			_requestParamNameArray = _argsMap.keySet().toArray();
			_requestParamValueArray = _argsMap.values().toArray();

		}

		doHttpRequest(_uriAPI, _requestParamNameArray, _requestParamValueArray,
				_responseListener);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doHttpRequest(String _uriAPI, Object[] _requestParamNameArray,
			Object[] _requestParamValueArray,
			final ResponseListener _responseListener) {

		DefaultHttpClient _httpClient = null;

		CookieStore _cookieStore = httpClient.getCookieStore();

		if (_cookieStore.getCookies() == null
				|| _cookieStore.getCookies().size() <= 0)
			_httpClient = httpClient;
		else {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			_httpClient = new DefaultHttpClient(httpParameters);
			_httpClient.setCookieStore(_cookieStore);
		}

		// 建立HTTPost对象
		HttpPost _httpRequest = new HttpPost(_uriAPI);

		// NameValuePair实现请求参数的封装
		List<NameValuePair> _params = new ArrayList<NameValuePair>();

		for (int i = 0; _requestParamNameArray != null
				&& _requestParamValueArray != null
				&& i < _requestParamNameArray.length; i++) {

			_params.add(new BasicNameValuePair(
					(String) _requestParamNameArray[i],
					(String) _requestParamValueArray[i]));
		}
		try {
			// 添加请求参数到请求对象
			_httpRequest
					.setEntity(new UrlEncodedFormEntity(_params, HTTP.UTF_8));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		final ResponseHandler _responseHandler = new ResponseHandler() {
			// 使用ResponseHandler来创建一个异步的Http调用
			public String handleResponse(HttpResponse _httpResponse) {
				String _reponseText = null;
				try {
					_reponseText = EntityUtils.toString(
							_httpResponse.getEntity(), HTTP.UTF_8);
					Log.d("CommonToolkit", "HttpUtils reponseText =" + _reponseText);
					if (_responseListener != null)
						_responseListener.onComplete(_httpResponse
								.getStatusLine().getStatusCode(), _reponseText);

				} catch (Exception e) {
					e.printStackTrace();
					if (_responseListener != null)
						_responseListener.onComplete(-1, e.getMessage());
				}
				return _reponseText;
			}
		};
		// 发送请求并等待响应
		try {
			_httpClient.execute(_httpRequest, _responseHandler);
		} catch (Exception ex) {
			// 网络连接失败,error code 为-1
			if (_responseListener != null)
				_responseListener.onComplete(-1, ex.getMessage());
		}

	}

	public DefaultHttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(DefaultHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Deprecated
	public static HttpUtil startHttpHead(String url,
			HeadResponseListener listener, HttpUtil httpUtil) {
		HttpHeadRunThread httpThread = new HttpHeadRunThread();
		httpThread.url = url;
		httpThread.responseListener = listener;
		if (httpUtil == null) {
			httpUtil = new HttpUtil();
			httpUtil.initHttpClient();
		}
		httpThread.httpUtils = httpUtil;
		httpThread.start();
		return httpUtil;
	}

	public static HttpUtil startHttpPostRequest(String url,
			HashMap<String, String> params, ResponseListener listener,
			HttpUtil httpUtil) {
		HttpRunThread httpThread = new HttpRunThread();
		httpThread.url = url;
		httpThread.paramMap = params;
		httpThread.responseListener = listener;
		if (httpUtil == null) {
			httpUtil = new HttpUtil();
			httpUtil.initHttpClient();
		}
		httpThread.httpUtils = httpUtil;
		httpThread.start();
		return httpUtil;
	}

	private static HashMap<String, String> parseParamsInTheURL(String url) {
		HashMap<String, String> params = new HashMap<String, String>();
		if (url != null && url.indexOf('?') > 0 && !url.endsWith("?")) {
			String keyValueStr = url.substring(url.indexOf('?') + 1);
			String[] keyValues = StringUtils.split(keyValueStr, "&");
			if (keyValues != null) {
				for (String keyValue : keyValues) {
					String[] kvs = StringUtils.split(keyValue, "=");
					if (kvs != null && kvs.length == 2) {
						params.put(kvs[0], kvs[1]);
					}
				}
			}
		}

		return params;
	}

	public static HttpUtil startHttpPostRequestWithSignature(String url,
			HashMap<String, String> params, ResponseListener listener,
			HttpUtil httpUtil) {
		Log.d("CommonToolkit", "startHttpPostRequestWithSignature - url: " + url);
		if (params == null) {
			params = new HashMap<String, String>();
		}
		params.putAll(parseParamsInTheURL(url));
		params.put(User.username.name(), UserManager.getInstance().getUser().getName());

		Set<String> keys = params.keySet();
		ArrayList<String> paramList = new ArrayList<String>();
		if (keys != null) {
			for (String key : keys) {
				String value = params.get(key);
				StringBuffer sb = new StringBuffer();
				sb.append(key).append("=").append(value);
				paramList.add(sb.toString());
			}
		}
		Collections.sort(paramList);
		// printList(paramList);

		StringBuffer sb2 = new StringBuffer();
		for (int i = 0; i < paramList.size(); i++) {
			sb2.append(paramList.get(i));
		}
		sb2.append(UserManager.getInstance().getUser().getUserKey());

		String signature = CommonUtil.md5(sb2.toString());
		params.put("sig", signature);

		return startHttpPostRequest(url, params, listener, httpUtil);
	}

	private static void printList(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			Log.d("walkwork", list.get(i));
		}
	}

}

class HttpRunThread extends Thread {
	public String url;
	public HashMap<String, String> paramMap;
	public ResponseListener responseListener;
	public HttpUtil httpUtils;

	public void run() {
		if (httpUtils != null)
			httpUtils.doHttpRequest(this.url, paramMap, responseListener);
	}
}

@Deprecated
class HttpHeadRunThread extends Thread {
	public String url;
	public HeadResponseListener responseListener;
	public HttpUtil httpUtils;

	public void run() {
		if (httpUtils != null)
			httpUtils.doHttpHead(this.url, responseListener);
	}
}
