package me.aiqi.A7weibo.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AccessToken {
	private String accessToken;
	private long expireTime;

	public AccessToken(String accessToken, long expireTime) {
		super();
		this.accessToken = accessToken;
		this.expireTime = expireTime;
	}

	public AccessToken() {
		super();
		this.accessToken = "";
		this.expireTime = 0;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public void setExpireTimeFromExpiresIn(long expiresIn) {
		this.expireTime = System.currentTimeMillis() / 1000 + expiresIn;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() / 1000 > this.expireTime;
	}

	public String getExpireTimeString() {
		return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US).format(new Date(1000 * this.expireTime));
	}

	@Override
	public String toString() {
		return "AccessToken [accessToken=" + accessToken + ", expireTime=" + expireTime + "(" + this.getExpireTimeString() + ")]";
	}
}
