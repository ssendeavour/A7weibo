package me.aiqi.A7weibo.entity;

import org.json.JSONObject;

import android.util.Log;

/**
 * 代表一个用户信息，详见：http://open.weibo.com/wiki/%E5%B8%B8%E8%A7%81%E8%BF%94%E5%9B%
 * 9E%E5%AF%B9%E8%B1%A1%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84#.E7.94.A8.E6.88.B7.EF.BC.88user.EF.BC
 * . 8 9
 * 
 * @author starfish
 * 
 */
public class WeiboUser {
	private static final String TAG = "WeiboUser";
	private long id = 0;// 用户UID
	private String idstr = "";// 字符串型的用户UID
	private String screen_name = "";// 用户昵称
	private String name = "";// 友好显示名称
	private int province = 0;// 用户所在省级ID
	private int city = 0;// 用户所在城市ID
	private String location = "";// 用户所在地
	private String description = "";// 用户个人描述
	private String url = "";// 用户博客地址
	private String profile_image_url = "";// 用户头像地址，50×50像素
	private String profile_url = "";// 用户的微博统一URL地址
	private String domain = "";// 用户的个性化域名
	private String weihao = "";// 用户的微号
	private String gender = "";// 性别，m：男、f：女、n：未知
	private int followers_count = 0;// 粉丝数
	private int friends_count = 0;// 关注数
	private int statuses_count = 0;// 微博数
	private int favourites_count = 0;// 收藏数
	private String created_at = "";// 用户创建（注册）时间
	private boolean following = false;// 暂未支持
	private boolean allow_all_act_msg = false;// 是否允许所有人给我发私信，true：是，false：否
	private boolean geo_enabled = false;// 是否允许标识用户的地理位置，true：是，false：否
	private boolean verified = false;// 是否是微博认证用户，即加V用户，true：是，false：否
	private int verified_type = 0;// 暂未支持
	private String remark = "";// 用户备注信息，只有在查询用户关系时才返回此字段
	private WeiboItem status = null;// 用户的最近一条微博信息字段
	private boolean allow_all_comment = false;// 是否允许所有人对我的微博进行评论，true：是，false：否
	private String avatar_large = "";// 用户大头像地址
	private String verified_reason = "";// 认证原因
	private boolean follow_me = false;// 该用户是否关注当前登录用户，true：是，false：否
	private int online_status = 0;// 用户的在线状态，0：不在线、1：在线
	private int bi_followers_count = 0;// 用户的互粉数
	private String lang = "";// 用户当前的语言版本，zh-cn：简体中文，zh-tw：繁体中文，en：英语

	public WeiboUser() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdstr() {
		return idstr;
	}

	public void setIdstr(String idstr) {
		this.idstr = idstr;
	}

	public String getScreen_name() {
		return screen_name;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getProvince() {
		return province;
	}

	public void setProvince(int province) {
		this.province = province;
	}

	public int getCity() {
		return city;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProfile_image_url() {
		return profile_image_url;
	}

	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}

	public String getProfile_url() {
		return profile_url;
	}

	public void setProfile_url(String profile_url) {
		this.profile_url = profile_url;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getWeihao() {
		return weihao;
	}

	public void setWeihao(String weihao) {
		this.weihao = weihao;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getFollowers_count() {
		return followers_count;
	}

	public void setFollowers_count(int followers_count) {
		this.followers_count = followers_count;
	}

	public int getFriends_count() {
		return friends_count;
	}

	public void setFriends_count(int friends_count) {
		this.friends_count = friends_count;
	}

	public int getStatuses_count() {
		return statuses_count;
	}

	public void setStatuses_count(int statuses_count) {
		this.statuses_count = statuses_count;
	}

	public int getFavourites_count() {
		return favourites_count;
	}

	public void setFavourites_count(int favourites_count) {
		this.favourites_count = favourites_count;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public boolean isAllow_all_act_msg() {
		return allow_all_act_msg;
	}

	public void setAllow_all_act_msg(boolean allow_all_act_msg) {
		this.allow_all_act_msg = allow_all_act_msg;
	}

	public boolean isGeo_enabled() {
		return geo_enabled;
	}

	public void setGeo_enabled(boolean geo_enabled) {
		this.geo_enabled = geo_enabled;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public WeiboItem getStatus() {
		return status;
	}

	public void setStatus(WeiboItem status) {
		this.status = status;
	}

	public boolean isAllow_all_comment() {
		return allow_all_comment;
	}

	public void setAllow_all_comment(boolean allow_all_comment) {
		this.allow_all_comment = allow_all_comment;
	}

	public String getAvatar_large() {
		return avatar_large;
	}

	public void setAvatar_large(String avatar_large) {
		this.avatar_large = avatar_large;
	}

	public String getVerified_reason() {
		return verified_reason;
	}

	public void setVerified_reason(String verified_reason) {
		this.verified_reason = verified_reason;
	}

	public boolean isFollow_me() {
		return follow_me;
	}

	public void setFollow_me(boolean follow_me) {
		this.follow_me = follow_me;
	}

	public int getOnline_status() {
		return online_status;
	}

	public void setOnline_status(int online_status) {
		this.online_status = online_status;
	}

	public int getBi_followers_count() {
		return bi_followers_count;
	}

	public void setBi_followers_count(int bi_followers_count) {
		this.bi_followers_count = bi_followers_count;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	@Override
	public String toString() {
		return "WeiboUser [id=" + id + ", idstr=" + idstr + ", screen_name=" + screen_name + ", name=" + name + ", province=" + province + ", city="
				+ city + ", location=" + location + ", description=" + description + ", url=" + url + ", profile_image_url=" + profile_image_url
				+ ", profile_url=" + profile_url + ", domain=" + domain + ", weihao=" + weihao + ", gender=" + gender + ", followers_count="
				+ followers_count + ", friends_count=" + friends_count + ", statuses_count=" + statuses_count + ", favourites_count="
				+ favourites_count + ", created_at=" + created_at + ", allow_all_act_msg=" + allow_all_act_msg + ", geo_enabled=" + geo_enabled
				+ ", verified=" + verified + ", remark=" + remark + ", allow_all_comment=" + allow_all_comment + ", avatar_large=" + avatar_large
				+ ", verified_reason=" + verified_reason + ", follow_me=" + follow_me + ", online_status=" + online_status + ", bi_followers_count="
				+ bi_followers_count + ", lang=" + lang + "]";
	}

	public static WeiboUser parseUserFromJsonObject(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}
		WeiboUser user = new WeiboUser();
		user.setAvatar_large(jsonObject.optString("avatar_large"));
		user.setBi_followers_count(jsonObject.optInt("bi_followers_count"));
		user.setCity(jsonObject.optInt("city"));
		user.setCreated_at(jsonObject.optString("created_at"));
		user.setDescription(jsonObject.optString("description"));
		user.setDomain(jsonObject.optString("domain"));
		user.setFavourites_count(jsonObject.optInt("favourites_count"));
		user.setFollow_me(jsonObject.optBoolean("follow_me"));
		user.setFollowers_count(jsonObject.optInt("followers_count"));
		user.setFriends_count(jsonObject.optInt("friends_count"));
		user.setGender(jsonObject.optString("gender"));
		user.setGeo_enabled(jsonObject.optBoolean("geo_enabled"));
		user.setId(jsonObject.optLong("id"));
		user.setIdstr(jsonObject.optString("idstr"));
		user.setLang(jsonObject.optString("lang"));
		user.setLocation(jsonObject.optString("location"));
		user.setName(jsonObject.optString("name"));
		user.setOnline_status(jsonObject.optInt("online_status"));
		user.setProfile_image_url(jsonObject.optString("profile_image_url"));
		user.setProfile_url(jsonObject.optString("profile_url"));
		user.setProvince(jsonObject.optInt("province"));
		user.setRemark(jsonObject.optString("remark"));
		user.setScreen_name(jsonObject.optString("screen_name"));
		// user.setStatus(jsonObject.optJSONObject("status")); //TODO: don't
		// care for now
		user.setStatuses_count(jsonObject.optInt("statuses_count"));
		user.setUrl(jsonObject.optString("url"));
		user.setVerified(jsonObject.optBoolean("verified"));
		user.setVerified_reason(jsonObject.optString("verified_reason"));
		user.setWeihao(jsonObject.optString("weihao"));
//		Log.d(TAG, user.toString());
		return user;
	}
}
