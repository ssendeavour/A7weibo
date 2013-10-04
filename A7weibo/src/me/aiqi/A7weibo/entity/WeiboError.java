/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.entity.WeiboError.java
 * created at: Oct 4, 2013 7:01:01 PM
 * @author starfish
 */

package me.aiqi.A7weibo.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class WeiboError {
	private String error;
	private long error_code;
	private String request;

	public WeiboError() {
	}

	public WeiboError(String error, long error_code, String request) {
		super();
		this.error = error;
		this.error_code = error_code;
		this.request = request;
	}

	@Override
	public String toString() {
		return "WeiboError [error_code=" + error_code + ", error=" + error + ", request=" + request + "]";
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public long getError_code() {
		return error_code;
	}

	public void setError_code(long error_code) {
		this.error_code = error_code;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public static WeiboError parseErrorJson(JSONObject jsonObject) {

		WeiboError weiboError = new WeiboError();

		weiboError.setError_code(jsonObject.optLong("error_code"));
		weiboError.setError(jsonObject.optString("error"));
		weiboError.setRequest(jsonObject.optString("request"));

		return weiboError;
	}

	/** return null if error happens */
	public static WeiboError parseError(String json) {
		try {
			return parseErrorJson(new JSONObject(json));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
