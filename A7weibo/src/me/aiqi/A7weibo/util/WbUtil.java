package me.aiqi.A7weibo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.weibo.sdk.android.Oauth2AccessToken;

/**
 * Utility class used in A7weibo for Android project
 * 
 * @author starfish
 * 
 */
public class WbUtil {

	/**
	 * Format Oauth2AccessToken expire time to human readable string (yyyy-MM-dd
	 * hh:mm:ss) for easy debug
	 * 
	 * @param accessToken
	 * @return Formated date string
	 */
	public static String getExpireDateString(Oauth2AccessToken accessToken) {
		if (accessToken == null) {
			return "";
		}
		return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US).format(new Date(1000 * accessToken.getExpiresTime()));
	}
}
