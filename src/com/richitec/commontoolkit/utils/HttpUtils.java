package com.richitec.commontoolkit.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.os.AsyncTask;
import android.util.Log;

import com.richitec.commontoolkit.user.UserManager;

public class HttpUtils {

	private static final String LOG_TAG = "HttpUtils";

	// singleton instance
	private static volatile HttpUtils _singletonInstance;

	// apache default http client
	private HttpClient _mDefaultHttpClient;

	// connection and socket timeout
	private int _mTimeoutConnection = 5000;
	private int _mTimeoutSocket = 10000;

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

		// init http client
		_mDefaultHttpClient = new DefaultHttpClient(_httpParameters);
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

	// send get request
	public static void getRequest(String pUrl, Map<String, String> pParam,
			Map<String, ?> pUserInfo, HttpRequestType pRequestType,
			OnHttpRequestListener httpRequestListener) {
		// perfect http request url
		StringBuilder _httpRequestUrl = new StringBuilder(
				StringUtils.perfectHttpRequestUrl(pUrl));
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
			try {
				HttpResponse _response = getHttpClient().execute(
						_getHttpRequest);

				// check http request listener and bind request response
				// callback function
				if (null != httpRequestListener) {
					httpRequestListener.bindReqRespCallBackFunction(
							_getHttpRequest, _response);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Send synchronous get request excetion: "
								+ e.getMessage());
				e.printStackTrace();

				// check http request listener
				if (ConnectTimeoutException.class == e.getClass()
						&& null != httpRequestListener) {
					httpRequestListener.onTimeout(_getHttpRequest);
				}
			}
			break;

		case ASYNCHRONOUS:
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
		HttpPost _postHttpRequest = new HttpPost(
				StringUtils.perfectHttpRequestUrl(pUrl));

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
						"post request post body unsupported encoding exception: "
								+ e.getMessage());
				e.printStackTrace();
			}

		}

		// check http request type
		switch (pRequestType) {
		case SYNCHRONOUS:
			// send synchronous post request
			try {
				HttpResponse _response = getHttpClient().execute(
						_postHttpRequest);

				// check http request listener and bind request response
				// callback function
				if (null != httpRequestListener) {
					httpRequestListener.bindReqRespCallBackFunction(
							_postHttpRequest, _response);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Send synchronous post request excetion: "
								+ e.getMessage());
				e.printStackTrace();

				// check http request listener
				if (ConnectTimeoutException.class == e.getClass()
						&& null != httpRequestListener) {
					httpRequestListener.onTimeout(_postHttpRequest);
				}
			}
			break;

		case ASYNCHRONOUS:
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

	// inner class
	// http request type
	public static enum HttpRequestType {
		SYNCHRONOUS, ASYNCHRONOUS
	}

	// post request format
	public static enum PostRequestFormat {
		URLENCODED, MULTIPARTFORMDATA
	}

	// http request listener
	public static abstract class OnHttpRequestListener {

		// bind request response callback function
		private void bindReqRespCallBackFunction(HttpRequest request,
				HttpResponse response) {
			// check response status code
			switch (response.getStatusLine().getStatusCode()) {
			case HttpStatus.SC_OK:
				onFinished(request, response);
				break;

			case HttpStatus.SC_BAD_REQUEST:
				onBadRequest(request, response);
				break;

			case HttpStatus.SC_FORBIDDEN:
				onForbidden(request, response);
				break;

			case HttpStatus.SC_NOT_FOUND:
				onNotFound(request, response);
				break;

			case HttpStatus.SC_INTERNAL_SERVER_ERROR:
				onInternalServerError(request, response);
				break;

			default:
				onFailed(request, response);
				break;
			}
		}

		// onFinished
		public abstract void onFinished(HttpRequest request,
				HttpResponse response);

		// bad request
		public void onBadRequest(HttpRequest request, HttpResponse response) {
			// call onFailed callback function
			onFailed(request, response);
		}

		// forbidden
		public void onForbidden(HttpRequest request, HttpResponse response) {
			// call onFailed callback function
			onFailed(request, response);
		}

		// not found
		public void onNotFound(HttpRequest request, HttpResponse response) {
			// call onFailed callback function
			onFailed(request, response);
		}

		// internal server error
		public void onInternalServerError(HttpRequest request,
				HttpResponse response) {
			// call onFailed callback function
			onFailed(request, response);
		}

		// onFailed
		public abstract void onFailed(HttpRequest request, HttpResponse response);

		// on timeout
		public void onTimeout(HttpRequest request) {
			// call onFailed callback function
			onFailed(request, null);
		}

	}

	// request execute result
	enum RequestExecuteResult {
		NORMAL, TIMEOUT
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

		@Override
		protected RequestExecuteResult doInBackground(Object... params) {
			// init return result
			RequestExecuteResult _ret = RequestExecuteResult.NORMAL;

			// set http request and request listener
			_mHttpRequest = (HttpUriRequest) getSuitableParam(
					HttpUriRequest.class, params);
			_mHttpRequestListener = (OnHttpRequestListener) getSuitableParam(
					OnHttpRequestListener.class, params);

			try {
				_mHttpResponse = getHttpClient().execute(_mHttpRequest);
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Send asynchronous http request excetion: "
								+ e.getMessage());
				e.printStackTrace();

				// check http request listener
				if (ConnectTimeoutException.class == e.getClass()
						&& null != getSuitableParam(
								OnHttpRequestListener.class, params)) {
					// update request execute result
					_ret = RequestExecuteResult.TIMEOUT;
				}
			}

			return _ret;
		}

		@Override
		protected void onPostExecute(RequestExecuteResult result) {
			super.onPostExecute(result);

			// check result
			switch (result) {
			case TIMEOUT:
				_mHttpRequestListener.onTimeout(_mHttpRequest);
				break;

			default:
				_mHttpRequestListener.bindReqRespCallBackFunction(
						_mHttpRequest, _mHttpResponse);
				break;
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
