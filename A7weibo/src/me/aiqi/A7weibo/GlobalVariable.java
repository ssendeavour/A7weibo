package me.aiqi.A7weibo;

import me.aiqi.A7weibo.entity.AccessToken;
import android.app.Application;

public class GlobalVariable extends Application {
	private static AccessToken accessToken;
	private static GlobalVariable mApplicationContext;

	public GlobalVariable() {
		super();
		accessToken = new AccessToken();
		mApplicationContext = this;
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(AccessToken token) {
		accessToken = token;
	}

	public static GlobalVariable getContext() {
		return mApplicationContext;
	}
}
