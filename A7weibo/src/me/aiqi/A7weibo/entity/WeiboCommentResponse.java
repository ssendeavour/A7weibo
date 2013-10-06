/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.entity.WeiboCommentResponse.java
 * created at: Oct 6, 2013 1:28:45 PM
 * @author starfish
 */

package me.aiqi.A7weibo.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class WeiboCommentResponse {

	private WeiboItem commentedWeibo = null;
	private long comment_id = 0;

	public WeiboItem getCommentedWeibo() {
		return commentedWeibo;
	}

	public void setCommentedWeibo(WeiboItem commentedWeibo) {
		this.commentedWeibo = commentedWeibo;
	}

	public long getComment_id() {
		return comment_id;
	}

	public void setComment_id(long comment_id) {
		this.comment_id = comment_id;
	}

	/** return WeiboCommentResponse object or null if parsing json error */
	public static WeiboCommentResponse parseJson(String json){
		WeiboCommentResponse response = new WeiboCommentResponse();
		try {
			JSONObject object = new JSONObject(json);
			response.setComment_id(object.optLong("id"));
			response.setCommentedWeibo(WeiboItem.parseSingleWeiboItem(object.optJSONObject("status")));
		} catch (JSONException e) {
			e.printStackTrace();
			response = null;
		}
		
		return response;
	}
}
