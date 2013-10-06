/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.entity.WeiboCommentResponse.java
 * created at: Oct 6, 2013 1:28:45 PM
 * @author starfish
 */

package me.aiqi.A7weibo.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WeiboRepostResponse {

	private static final String TAG = WeiboRepostResponse.class.getSimpleName();

	private WeiboItem repostedWeibo = null;

	public WeiboItem getRepostedWeibo() {
		return repostedWeibo;
	}

	public void setRepostedWeibo(WeiboItem repostedWeibo) {
		this.repostedWeibo = repostedWeibo;
	}

	/** return WeiboRepostResponse object or null if parsing json error */
	public static WeiboRepostResponse parseJson(String json) {
		WeiboRepostResponse response = new WeiboRepostResponse();
		try {
			JSONObject reposted = new JSONObject(json).optJSONObject("retweeted_status");
			WeiboItem repostedWeibo = WeiboItem.parseSingleWeiboItem(reposted);
			if (repostedWeibo == null) {
				response = null;
			} else {
				response.setRepostedWeibo(repostedWeibo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			response = null;
			WeiboError error = WeiboError.parseError(json);
			if (error != null) {
				Log.v(TAG, error.toString());
			}
		}

		return response;
	}
}
