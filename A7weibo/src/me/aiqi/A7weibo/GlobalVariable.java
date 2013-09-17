package me.aiqi.A7weibo;

import me.aiqi.A7weibo.entity.AccessToken;
import android.app.Application;

public class GlobalVariable extends Application {
	private AccessToken accessToken;

	public GlobalVariable() {
		super();
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}
}
