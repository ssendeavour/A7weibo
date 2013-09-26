package me.aiqi.A7weibo.util;

import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.AppRegInfo;

import com.weibo.sdk.android.Oauth2AccessToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * 该类用于保存Oauth2AccessToken到sharepreference，并提供读取功能
 * 
 * @author xiaowei6@staff.sina.com.cn
 * 
 */
public class AccessTokenKeeper {
	public static final String TAG = "AccessTokenKeeper";

	private static final String PREFERENCES_NAME = "me.aiqi.A7weibo";

	/**
	 * 保存accesstoken到SharedPreferences
	 * 
	 * @param context
	 *            Activity 上下文环境
	 * @param token
	 *            Oauth2AccessToken
	 */
	public static boolean keepAccessToken(Context context, AccessToken token) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString("token", token.getAccessTokenString());
		editor.putLong("expiresTime", token.getExpireTime());
		return editor.commit();
	}

	/**
	 * 清空sharepreference
	 * 
	 * @param context
	 */
	public static void clear(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 从SharedPreferences读取accessstoken
	 * 
	 * @param context
	 * @return Oauth2AccessToken
	 */
	public static AccessToken readAccessToken(Context context) {
		AccessToken token = new AccessToken();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		if (pref == null) {
			return null;
		}
		token.setAccessTokenString(pref.getString("token", ""));
		token.setExpireTime(pref.getLong("expiresTime", 0));
		return token;
	}
}
