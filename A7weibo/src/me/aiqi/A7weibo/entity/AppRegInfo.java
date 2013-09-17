package me.aiqi.A7weibo.entity;

public class AppRegInfo {
	private String appKey;
	private String appSecret;
	private String appUrl;
	private long uid;

	public AppRegInfo(String appKey, String appSecret, String appUrl, long uid) {
		super();
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.appUrl = appUrl;
		this.uid = uid;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "AppRegInfo [appKey=" + appKey + ", appSecret=" + appSecret + ", appUrl=" + appUrl + ", uid=" + uid + "]";
	}
}
