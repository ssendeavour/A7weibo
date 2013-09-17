package me.aiqi.A7weibo.entity;

/**
 * 代表一个用户信息，详见：http://open.weibo.com/wiki/%E5%B8%B8%E8%A7%81%E8%BF%94%E5%9B%
 * 9E%E5%AF%B9%E8%B1%A1%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84#.E7.94.A8.E6.88.B7.EF.BC.88user.EF.BC.
 * 8 9
 * 
 * @author starfish
 * 
 */
public class WeiboUser {
	private long id;// 用户UID
	private String idstr;// 字符串型的用户UID
	private String screen_name;// 用户昵称
	private String name;// 友好显示名称
	private int province;// 用户所在省级ID
	private int city;// 用户所在城市ID
	private String location;// 用户所在地
	private String description;// 用户个人描述
	private String url;// 用户博客地址
	private String profile_image_url;// 用户头像地址，50×50像素
	private String profile_url;// 用户的微博统一URL地址
	private String domain;// 用户的个性化域名
	private String weihao;// 用户的微号
	private String gender;// 性别，m：男、f：女、n：未知
	private int followers_count;// 粉丝数
	private int friends_count;// 关注数
	private int statuses_count;// 微博数
	private int favourites_count;// 收藏数
	private String created_at;// 用户创建（注册）时间
	private boolean following;// 暂未支持
	private boolean allow_all_act_msg;// 是否允许所有人给我发私信，true：是，false：否
	private boolean geo_enabled;// 是否允许标识用户的地理位置，true：是，false：否
	private boolean verified;// 是否是微博认证用户，即加V用户，true：是，false：否
	private int verified_type;// 暂未支持
	private String remark;// 用户备注信息，只有在查询用户关系时才返回此字段
	private WeiboItem status;// 用户的最近一条微博信息字段
	private boolean allow_all_comment;// 是否允许所有人对我的微博进行评论，true：是，false：否
	private String avatar_large;// 用户大头像地址
	private String verified_reason;// 认证原因
	private boolean follow_me;// 该用户是否关注当前登录用户，true：是，false：否
	private int online_status;// 用户的在线状态，0：不在线、1：在线
	private int bi_followers_count;// 用户的互粉数
	private String lang;// 用户当前的语言版本，zh-cn：简体中文，zh-tw：繁体中文，en：英语

}
