package me.aiqi.A7weibo.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class AccessToken {
	public static String ACCESS_TOKEN = "access_token";
	public static String EXPIRES_IN = "expires_in";
	public static String UID = "uid";
	public static String CODE = "code";

	private String mAccessTokenString;
	/** expire time in seconds since epoch */
	private long mExpireTime;

	public AccessToken(String accessTokenString, long expireTime) {
		super();
		mAccessTokenString = accessTokenString;
		mExpireTime = expireTime;
	}

	public AccessToken() {
		super();
		mAccessTokenString = "";
		mExpireTime = 0;
	}

	public String getAccessTokenString() {
		return mAccessTokenString;
	}

	public void setAccessTokenString(String accessToken) {
		mAccessTokenString = accessToken;
	}

	public long getExpireTime() {
		return mExpireTime;
	}

	public void setExpireTime(long expireTime) {
		mExpireTime = expireTime;
	}

	public void setExpireTimeFromExpiresIn(long expiresIn) {
		mExpireTime = System.currentTimeMillis() / 1000 + expiresIn;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() / 1000 > mExpireTime;
	}

	public String getExpireTimeString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date(1000 * mExpireTime));
	}

	@Override
	public String toString() {
		return "AccessToken [accessToken=" + mAccessTokenString + ", expireTime=" + mExpireTime + "("
				+ getExpireTimeString() + ")]";
	}
}
