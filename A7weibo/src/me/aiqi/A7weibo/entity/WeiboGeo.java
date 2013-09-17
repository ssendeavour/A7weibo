package me.aiqi.A7weibo.entity;

/**
 * 微博地理位置信息
 * {url: http://open.weibo.com/wiki/%E5%B8%B8%E8%A7%81%E8%BF%94%E5%9B%9E%E5%AF%B9%E8%B1%A1%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84#.E5.9C.B0.E7.90.86.E4.BF.A1.E6.81.AF.EF.BC.88geo.EF.BC.89}
 * @author starfish
 * 
 */
public class WeiboGeo {

	private String longitude;// 经度坐标
	private String latitude;// 维度坐标
	private String city;// 所在城市的城市代码
	private String province;// 所在省份的省份代码
	private String city_name;// 所在城市的城市名称
	private String province_name;// 所在省份的省份名称
	private String address;// 所在的实际地址，可以为空
	private String pinyin;// 地址的汉语拼音，不是所有情况都会返回该字段
	private String more;// 更多信息，不是所有情况都会返回该字段

}
