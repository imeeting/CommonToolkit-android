package com.richitec.commontoolkit.utils;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.richitec.commontoolkit.user.UserManager;

public class HttpUtils {

	private static final String LOG_TAG = HttpUtils.class.getCanonicalName();

	// singleton instance
	private static volatile HttpUtils _singletonInstance;

	// apache default http client
	private HttpClient _mDefaultHttpClient;

	// connection and socket timeout
	private int _mTimeoutConnection = 60000;
	private int _mTimeoutSocket = 60000;

	// user name parameter key
	private static final String USERNAME_PARAMETER_KEY = "username";
	// signature parameter key
	private static final String SIGNATURE_PARAMETER_KEY = "sig";

	private HttpUtils() {
		// init http param
		HttpParams _httpParameters = new BasicHttpParams();
		// set timeout
		HttpConnectionParams.setConnectionTimeout(_httpParameters,
				_mTimeoutConnection);
		HttpConnectionParams.setSoTimeout(_httpParameters, _mTimeoutSocket);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(
				_httpParameters, registry);

		// init http client
		_mDefaultHttpClient = new DefaultHttpClient(cm, _httpParameters);
	}

	private HttpClient getDefaultHttpClient() {
		return _mDefaultHttpClient;
	}

	// get apache http client
	private static HttpClient getHttpClient() {
		if (null == _singletonInstance) {
			synchronized (HttpUtils.class) {
				if (null == _singletonInstance) {
					_singletonInstance = new HttpUtils();
				}
			}
		}

		return _singletonInstance.getDefaultHttpClient();
	}

	// perfect http request url with prefix
	public static String perfectHttpRequestUrl(String url,
			HttpUrlPrefix httpUrlPrefix) {
		String _ret = url;

		// check url is nil and has prefix "http://" or "https://"
		if (null != url && null != httpUrlPrefix && !url.equalsIgnoreCase("")) {
			// check need to perfect http request url
			if (!url.startsWith(httpUrlPrefix.prefix())) {
				// define url string builder
				StringBuilder _urlStringBuilder = new StringBuilder();

				// need to split with prefix
				if (url.startsWith(httpUrlPrefix.anotherHttpUrlPrefix()
						.prefix())) {
					_ret = _urlStringBuilder
							.append(httpUrlPrefix.prefix())
							.append(url.substring(httpUrlPrefix
									.anotherHttpUrlPrefix().prefix().length()))
							.toString();
				} else {
					_ret = _urlStringBuilder.append(httpUrlPrefix.prefix())
							.append(url).toString();
				}
			}
		} else {
			Log.e(LOG_TAG, "Perfect http request url error, url = " + url
					+ " and prefix = " + httpUrlPrefix);
		}

		return _ret;
	}

	// send get request
	public static void getRequest(String pUrl, Map<String, String> pParam,
			Map<String, ?> pUserInfo, HttpRequestType pRequestType,
			OnHttpRequestListener httpRequestListener) {
		// perfect http request url
		StringBuilder _httpRequestUrl = new StringBuilder(
				perfectHttpRequestUrl(pUrl, HttpUrlPrefix.HTTP));
		// append char '?' first and param pairs, if param not null
		if (null != pParam && !pParam.isEmpty()) {
			_httpRequestUrl.append('?');

			// append param pairs
			for (String _paramKey : pParam.keySet()) {
				_httpRequestUrl.append(_paramKey + "=" + pParam.get(_paramKey)
						+ '&');
			}

			// trim last char '&' in http request url
			_httpRequestUrl.deleteCharAt(_httpRequestUrl.length() - 1);
		}

		// new httpGet object
		HttpGet _getHttpRequest = new HttpGet(_httpRequestUrl.toString());

		// check http request type
		switch (pRequestType) {
		case SYNCHRONOUS:
			// send synchronous get request
			HttpResponseResult responseResult = new HttpResponseResult();
			try {
				HttpResponse _response = getHttpClient().execute(
						_getHttpRequest);
				responseResult.setStatusCode(_response.getStatusLine().getStatusCode());
				responseResult.setResponseText(getHttpResponseEntityString(_response));
				if (_response != null) {
					_response.getEntity().consumeContent();
				}
				// check http request listener and bind request response
				// callback function
				if (null != httpRequestListener) {
					httpRequestListener.bindReqRespCallBackFunction(
							responseResult);
				}


			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Send synchronous get request excetion message = "
								+ e.getMessage());

				e.printStackTrace();

				// process needed exception and check http request listener
				if (ConnectTimeoutException.class == e.getClass()
						&& null != httpRequestListener) {
					httpRequestListener.onTimeout(responseResult);
				} else if (UnknownHostException.class == e.getClass()
						&& null != httpRequestListener) {
					httpRequestListener.onUnknownHost(responseResult);
				}
				_getHttpRequest.abort();
			}
			break;

		case ASYNCHRONOUS:
			// new asynchronous http request task to do get request in
			// background
			new AsyncHttpRequestTask().execute(_getHttpRequest,
					httpRequestListener);
			break;
		}
	}

	// send post request
	public static void postRequest(String pUrl, PostRequestFormat pPostFormat,
			Map<String, String> pParam, Map<String, ?> pUserInfo,
			HttpRequestType pRequestType,
			OnHttpRequestListener httpRequestListener) {
		// new httpPost object
		HttpPost _postHttpRequest = new HttpPost(perfectHttpRequestUrl(pUrl,
				HttpUrlPrefix.HTTP));

		// check param and set post request param
		if (null != pParam && !pParam.isEmpty()) {
			try {
				switch (pPostFormat) {
				case URLENCODED: {
					// define urlEncodedForm post request param
					List<NameValuePair> _urlEncodedFormPostReqParam = new ArrayList<NameValuePair>();

					// update urlEncodedForm post request param
					for (String _paramKey : pParam.keySet()) {
						_urlEncodedFormPostReqParam.add(new BasicNameValuePair(
								_paramKey, pParam.get(_paramKey)));
					}

					// set entity
					_postHttpRequest.setEntity(new UrlEncodedFormEntity(
							_urlEncodedFormPostReqParam, HTTP.UTF_8));
				}

					break;

				case MULTIPARTFORMDATA: {
					// init multipart entity
					MultipartEntity _multipartEntity = new MultipartEntity();

					// update multipart entity
					for (String _paramKey : pParam.keySet()) {
						_multipartEntity.addPart(
								_paramKey,
								new StringBody(pParam.get(_paramKey), Charset
										.forName(HTTP.UTF_8)));
					}

					// set entity
					_postHttpRequest.setEntity(_multipartEntity);
				}
					break;
				}
			} catch (UnsupportedEncodingException e) {
				Log.e(LOG_TAG,
						"Post request post body unsupported encoding exceptio message = "
								+ e.getMessage());

				e.printStackTrace();
			}

		}

		// check http request type
		switch (pRequestType) {
		case SYNCHRONOUS:
			// send synchronous post request
			HttpResponseResult responseResult = new HttpResponseResult();
			try {
				HttpResponse _response = getHttpClient().execute(
						_postHttpRequest);
				responseResult.setStatusCode(_response.getStatusLine().getStatusCode());
				responseResult.setResponseText(getHttpResponseEntityString(_response));
				if (_response != null) {
					_response.getEntity().consumeContent();
				}
				// check http request listener and bind request response
				// callback function
				if (null != httpRequestListener) {
					httpRequestListener.bindReqRespCallBackFunction(
							responseResult);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Send synchronous post request excetion message = "
								+ e.getMessage());

				e.printStackTrace();

				// process needed exception and check http request listener
				if (ConnectTimeoutException.class == e.getClass()
						&& null != httpRequestListener) {
					httpRequestListener.onTimeout(responseResult);
				} else if (UnknownHostException.class == e.getClass()
						&& null != httpRequestListener) {
					httpRequestListener.onUnknownHost(responseResult);
				}

				_postHttpRequest.abort();
			}
			break;

		case ASYNCHRONOUS:
			// new asynchronous http request task to do post request in
			// background
			new AsyncHttpRequestTask().execute(_postHttpRequest,
					httpRequestListener);
			break;
		}
	}

	// generate signature with param
	private static String generateSignature(Map<String, String> pParam) {
		// update param
		pParam = null == pParam ? new HashMap<String, String>() : pParam;

		// put user name
		pParam.put(USERNAME_PARAMETER_KEY, UserManager.getInstance().getUser()
				.getName());

		// get param string list
		ArrayList<String> _paramStringList = new ArrayList<String>();
		for (String _paramKey : pParam.keySet()) {
			_paramStringList.add(new StringBuilder(_paramKey).append('=')
					.append(pParam.get(_paramKey)).toString());
		}
		// sorted
		Collections.sort(_paramStringList);

		// get param string
		StringBuilder _paramString = new StringBuilder();
		for (String string : _paramStringList) {
			_paramString.append(string);
		}

		// append userKey
		_paramString.append(UserManager.getInstance().getUser().getUserKey());

		return StringUtils.md5(_paramString.toString());
	}

	// send signature get request
	public static void getSignatureRequest(String pUrl,
			Map<String, String> pParam, Map<String, ?> pUserInfo,
			HttpRequestType pRequestType,
			OnHttpRequestListener httpRequestListener) {
		// init signature get request param
		HashMap<String, String> _signatureGetRequestParam = null == pParam ? new HashMap<String, String>()
				: new HashMap<String, String>(pParam);

		// append user name and signature
		_signatureGetRequestParam.put(USERNAME_PARAMETER_KEY, UserManager
				.getInstance().getUser().getName());
		_signatureGetRequestParam.put(SIGNATURE_PARAMETER_KEY,
				generateSignature(pParam));

		// send signature get request
		getRequest(pUrl, _signatureGetRequestParam, pUserInfo, pRequestType,
				httpRequestListener);
	}

	// send signature post request
	public static void postSignatureRequest(String pUrl,
			PostRequestFormat pPostFormat, Map<String, String> pParam,
			Map<String, ?> pUserInfo, HttpRequestType pRequestType,
			OnHttpRequestListener httpRequestListener) {
		// init signature post request param
		HashMap<String, String> _signaturePostRequestParam = null == pParam ? new HashMap<String, String>()
				: new HashMap<String, String>(pParam);

		// append user name and signature
		_signaturePostRequestParam.put(USERNAME_PARAMETER_KEY, UserManager
				.getInstance().getUser().getName());
		_signaturePostRequestParam.put(SIGNATURE_PARAMETER_KEY,
				generateSignature(pParam));

		// send signature post request
		postRequest(pUrl, pPostFormat, _signaturePostRequestParam, pUserInfo,
				pRequestType, httpRequestListener);
	}

	// get http response entity string
	public static String getHttpResponseEntityString(HttpResponse response) {
		String _respEntityString = "";

		// check response
		if (null != response) {
			try {
				_respEntityString = EntityUtils.toString(response.getEntity(),
						HTTP.UTF_8);
			} catch (Exception e) {
				Log.e(LOG_TAG, "Get http response entity excetion message = "
						+ e.getMessage());

				e.printStackTrace();
			}
		} else {
			Log.e(LOG_TAG, "Get http response entity, response is null");
		}

		return _respEntityString;
	}

	// inner class
	// http url prefix
	public enum HttpUrlPrefix {
		HTTP, HTTPS;

		// http url prefix string
		public String prefix() {
			// define return result
			String _ret = this.name();

			// set return result
			switch (this) {
			case HTTPS:
				_ret = "https://";
				break;

			case HTTP:
				_ret = "http://";
			default:
				break;
			}

			return _ret;
		}

		// another http url prefix
		public HttpUrlPrefix anotherHttpUrlPrefix() {
			HttpUrlPrefix _ret = this;

			// set return result
			switch (this) {
			case HTTPS:
				_ret = HTTP;
				break;

			case HTTP:
			default:
				_ret = HTTPS;
				break;
			}

			return _ret;
		}

		@Override
		public String toString() {
			return prefix();
		}

	}

	// http request type
	public enum HttpRequestType {
		SYNCHRONOUS, ASYNCHRONOUS
	}

	// post request format
	public enum PostRequestFormat {
		URLENCODED, MULTIPARTFORMDATA
	}

	/**
	 * http response result set
	 * 
	 * @author star
	 * 
	 */
	public static class HttpResponseResult {
		private int statusCode;
		private String responseText;

		public HttpResponseResult() {
			statusCode = -1;
			responseText = "";
		}

		public int getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}

		public String getResponseText() {
			return responseText;
		}

		public void setResponseText(String responseText) {
			this.responseText = responseText;
		}

	}

	// http request listener
	public static abstract class OnHttpRequestListener {

		// bind request response callback function
		private void bindReqRespCallBackFunction(
				HttpResponseResult responseResult) {
			// check response status code
			if (responseResult != null) {
				switch (responseResult.getStatusCode()) {
				case HttpStatus.SC_ACCEPTED:
				case HttpStatus.SC_CREATED:
				case HttpStatus.SC_OK:
					onFinished(responseResult);
					break;

				case HttpStatus.SC_BAD_REQUEST:
					onBadRequest(responseResult);
					break;

				case HttpStatus.SC_FORBIDDEN:
					onForbidden(responseResult);
					break;

				case HttpStatus.SC_NOT_FOUND:
					onNotFound(responseResult);
					break;

				case HttpStatus.SC_INTERNAL_SERVER_ERROR:
					onInternalServerError(responseResult);
					break;

				default:
					onFailed(responseResult);
					break;
				}
			} else {
				onFailed(responseResult);
			}
		}

		// onFinished
		public abstract void onFinished(HttpResponseResult responseResult);

		// bad request
		public void onBadRequest(HttpResponseResult responseResult) {
			// call onFailed callback function
			onFailed(responseResult);
		}

		// forbidden
		public void onForbidden(HttpResponseResult responseResult) {
			// call onFailed callback function
			onFailed(responseResult);
		}

		// not found
		public void onNotFound(HttpResponseResult responseResult) {
			// call onFailed callback function
			onFailed(responseResult);
		}

		// internal server error
		public void onInternalServerError(HttpResponseResult responseResult) {
			// call onFailed callback function
			onFailed(responseResult);
		}

		// onFailed
		public abstract void onFailed(HttpResponseResult responseResult);

		// on timeout
		public void onTimeout(HttpResponseResult responseResult) {
			// call onFailed callback function
			onFailed(responseResult);
		}

		// on unknown host
		public void onUnknownHost(HttpResponseResult responseResult) {
			// call onFailed callback function
			onFailed(responseResult);
		}

	}

	// request execute result
	enum RequestExecuteResult {
		NORMAL, TIMEOUT, UNKNOWN_HOST
	}

	// asynchronous http request task
	// Objects: HttpUriRequest, OnHttpRequestListener
	static class AsyncHttpRequestTask extends
			AsyncTask<Object, Integer, RequestExecuteResult> {

		// http request
		private HttpUriRequest _mHttpRequest;
		// http response
		private HttpResponse _mHttpResponse;
		// http request listener
		private OnHttpRequestListener _mHttpRequestListener;

		private HttpResponseResult _mResponseResult;

		@Override
		protected RequestExecuteResult doInBackground(Object... params) {
			// init return result
			RequestExecuteResult _ret = RequestExecuteResult.NORMAL;

			// save http request and request listener
			_mHttpRequest = (HttpUriRequest) getSuitableParam(
					HttpUriRequest.class, params);
			_mHttpRequestListener = (OnHttpRequestListener) getSuitableParam(
					OnHttpRequestListener.class, params);

			// save http response
			_mResponseResult = new HttpResponseResult();
			try {
				_mHttpResponse = getHttpClient().execute(_mHttpRequest);
				_mResponseResult.setStatusCode(_mHttpResponse.getStatusLine()
						.getStatusCode());
				_mResponseResult.setResponseText(HttpUtils
						.getHttpResponseEntityString(_mHttpResponse));

				_mHttpResponse.getEntity().consumeContent();
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Send asynchronous http request excetion message = "
								+ e.getMessage());

				e.printStackTrace();

				// process needed exception and check http request listener
				if (ConnectTimeoutException.class == e.getClass()
						&& null != _mHttpRequestListener) {
					// update request execute result
					_ret = RequestExecuteResult.TIMEOUT;
				} else if (UnknownHostException.class == e.getClass()
						&& null != _mHttpRequestListener) {
					// update request execute result
					_ret = RequestExecuteResult.UNKNOWN_HOST;
				}
				_mHttpRequest.abort();
			}

			return _ret;
		}

		@Override
		protected void onPostExecute(RequestExecuteResult result) {
			super.onPostExecute(result);

			// check http request listener and bind request response
			// callback function
			if (null != _mHttpRequestListener) {
				// check result
				switch (result) {
				case TIMEOUT:
					_mHttpRequestListener.onTimeout(_mResponseResult);
					break;

				case UNKNOWN_HOST:
					_mHttpRequestListener.onUnknownHost(_mResponseResult);
					break;

				case NORMAL:
				default:
					_mHttpRequestListener
							.bindReqRespCallBackFunction(_mResponseResult);
					break;
				}
			}

		}

		// get suitable param from params with class name
		private Object getSuitableParam(Class<?> className, Object... params) {
			Object _ret = null;

			// process params
			for (int i = 0; i < params.length; i++) {
				if (className.isInstance(params[i])) {
					_ret = params[i];

					break;
				}
			}

			return _ret;
		}

	}

}
