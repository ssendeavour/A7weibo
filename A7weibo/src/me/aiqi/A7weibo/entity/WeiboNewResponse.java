/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.entity.WeiboNewResponse.java
 * created at: Oct 7, 2013 10:44:59 AM
 * @author starfish
 */

package me.aiqi.A7weibo.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WeiboNewResponse {

	private static final String TAG = WeiboNewResponse.class.getSimpleName();

	public static WeiboItem parseJson(String json) {
		try {
			JSONObject object = new JSONObject(json);
			return WeiboItem.parseSingleWeiboItem(object);
		} catch (JSONException e) {
			WeiboError error = WeiboError.parseError(json);
			if (error != null) {
				Log.v(TAG, error.toString());
			}
			return null;
		}
	}
}
