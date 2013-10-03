/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.weibo.WeiboEmoticon.java
 * created at: Oct 3, 2013 12:33:52 AM
 * @author starfish
 */

package me.aiqi.A7weibo.weibo;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.aiqi.A7weibo.R;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;

public class WeiboRichText {
	public static final HashMap<String, Integer> sDefaultEmoticonMap;
	public static final Pattern sEmoticonPattern = Pattern.compile("(?<=\\[)(.+?)(?=\\])");
	public static final Pattern sTopicPattern = Pattern.compile("(#.+?#)");
	// all punctuation are not allowed in user name except underscore and hyphen, \\p{Punct} only cover ASCII punctuation. Space and end of line are also a terminator of user name
	public static final Pattern sAtPeoplePattern = Pattern.compile("(@.*?)((?=[ :，。：？；（）])|$|(?=[\\p{Punct}&&[^_-]]))");
	public static final String TAG = "WeiboEmoticon";

	static {
		sDefaultEmoticonMap = new HashMap<String, Integer>();
		sDefaultEmoticonMap.put("草泥马", R.drawable.emot_default_shenshou_org);
		sDefaultEmoticonMap.put("神马", R.drawable.emot_default_horse2_org);
		sDefaultEmoticonMap.put("浮云", R.drawable.emot_default_fuyun_org);
		sDefaultEmoticonMap.put("给力", R.drawable.emot_default_geili_org);
		sDefaultEmoticonMap.put("围观", R.drawable.emot_default_wg_org);
		sDefaultEmoticonMap.put("威武", R.drawable.emot_default_vw_org);
		sDefaultEmoticonMap.put("熊猫", R.drawable.emot_default_panda_org);
		sDefaultEmoticonMap.put("兔子", R.drawable.emot_default_rabbit_org);
		sDefaultEmoticonMap.put("奥特曼", R.drawable.emot_default_otm_org);
		sDefaultEmoticonMap.put("囧", R.drawable.emot_default_j_org);
		sDefaultEmoticonMap.put("互粉", R.drawable.emot_default_hufen_org);
		sDefaultEmoticonMap.put("礼物", R.drawable.emot_default_liwu_org);
		sDefaultEmoticonMap.put("呵呵", R.drawable.emot_default_smilea_org);
		sDefaultEmoticonMap.put("嘻嘻", R.drawable.emot_default_tootha_org);
		sDefaultEmoticonMap.put("哈哈", R.drawable.emot_default_laugh);
		sDefaultEmoticonMap.put("可爱", R.drawable.emot_default_tza_org);
		sDefaultEmoticonMap.put("可怜", R.drawable.emot_default_kl_org);
		sDefaultEmoticonMap.put("挖鼻屎", R.drawable.emot_default_kbsa_org);
		sDefaultEmoticonMap.put("吃惊", R.drawable.emot_default_cj_org);
		sDefaultEmoticonMap.put("害羞", R.drawable.emot_default_shamea_org);
		sDefaultEmoticonMap.put("挤眼", R.drawable.emot_default_zy_org);
		sDefaultEmoticonMap.put("闭嘴", R.drawable.emot_default_bz_org);
		sDefaultEmoticonMap.put("鄙视", R.drawable.emot_default_bs2_org);
		sDefaultEmoticonMap.put("爱你", R.drawable.emot_default_lovea_org);
		sDefaultEmoticonMap.put("泪", R.drawable.emot_default_sada_org);
		sDefaultEmoticonMap.put("偷笑", R.drawable.emot_default_heia_org);
		sDefaultEmoticonMap.put("亲亲", R.drawable.emot_default_qq_org);
		sDefaultEmoticonMap.put("生病", R.drawable.emot_default_sb_org);
		sDefaultEmoticonMap.put("太开心", R.drawable.emot_default_mb_org);
		sDefaultEmoticonMap.put("懒得理你", R.drawable.emot_default_ldln_org);
		sDefaultEmoticonMap.put("右哼哼", R.drawable.emot_default_yhh_org);
		sDefaultEmoticonMap.put("左哼哼", R.drawable.emot_default_zhh_org);
		sDefaultEmoticonMap.put("嘘", R.drawable.emot_default_x_org);
		sDefaultEmoticonMap.put("衰", R.drawable.emot_default_cry);
		sDefaultEmoticonMap.put("委屈", R.drawable.emot_default_wq_org);
		sDefaultEmoticonMap.put("吐", R.drawable.emot_default_t_org);
		sDefaultEmoticonMap.put("打哈欠", R.drawable.emot_default_k_org);
		sDefaultEmoticonMap.put("抱抱", R.drawable.emot_default_bba_org);
		sDefaultEmoticonMap.put("怒", R.drawable.emot_default_angrya_org);
		sDefaultEmoticonMap.put("疑问", R.drawable.emot_default_yw_org);
		sDefaultEmoticonMap.put("馋嘴", R.drawable.emot_default_cza_org);
		sDefaultEmoticonMap.put("拜拜", R.drawable.emot_default_88_org);
		sDefaultEmoticonMap.put("思考", R.drawable.emot_default_sk_org);
		sDefaultEmoticonMap.put("汗", R.drawable.emot_default_sweata_org);
		sDefaultEmoticonMap.put("困", R.drawable.emot_default_sleepya_org);
		sDefaultEmoticonMap.put("睡觉", R.drawable.emot_default_sleepa_org);
		sDefaultEmoticonMap.put("钱", R.drawable.emot_default_money_org);
		sDefaultEmoticonMap.put("失望", R.drawable.emot_default_sw_org);
		sDefaultEmoticonMap.put("酷", R.drawable.emot_default_cool_org);
		sDefaultEmoticonMap.put("花心", R.drawable.emot_default_hsa_org);
		sDefaultEmoticonMap.put("哼", R.drawable.emot_default_hatea_org);
		sDefaultEmoticonMap.put("鼓掌", R.drawable.emot_default_gza_org);
		sDefaultEmoticonMap.put("晕", R.drawable.emot_default_dizzya_org);
		sDefaultEmoticonMap.put("悲伤", R.drawable.emot_default_bs_org);
		sDefaultEmoticonMap.put("抓狂", R.drawable.emot_default_crazya_org);
		sDefaultEmoticonMap.put("黑线", R.drawable.emot_default_h_org);
		sDefaultEmoticonMap.put("阴险", R.drawable.emot_default_yx_org);
		sDefaultEmoticonMap.put("怒骂", R.drawable.emot_default_nm_org);
		sDefaultEmoticonMap.put("心", R.drawable.emot_default_hearta_org);
		sDefaultEmoticonMap.put("伤心", R.drawable.emot_default_unheart);
		sDefaultEmoticonMap.put("猪头", R.drawable.emot_default_pig);
		sDefaultEmoticonMap.put("ok", R.drawable.emot_default_ok_org);
		sDefaultEmoticonMap.put("耶", R.drawable.emot_default_ye_org);
		sDefaultEmoticonMap.put("good", R.drawable.emot_default_good_org);
		sDefaultEmoticonMap.put("不要", R.drawable.emot_default_no_org);
		sDefaultEmoticonMap.put("赞", R.drawable.emot_default_z2_org);
		sDefaultEmoticonMap.put("来", R.drawable.emot_default_come_org);
		sDefaultEmoticonMap.put("弱", R.drawable.emot_default_sad_org);
		sDefaultEmoticonMap.put("蜡烛", R.drawable.emot_default_lazu_org);
		sDefaultEmoticonMap.put("钟", R.drawable.emot_default_clock_org);
		sDefaultEmoticonMap.put("话筒", R.drawable.emot_default_m_org);
		sDefaultEmoticonMap.put("蛋糕", R.drawable.emot_default_cake);
		sDefaultEmoticonMap.put("带着微博去旅行", R.drawable.emot_default_weitripballoon_org);
		sDefaultEmoticonMap.put("玩去啦", R.drawable.emot_default_weitrip_org);
		sDefaultEmoticonMap.put("放假啦", R.drawable.emot_default_lxhfangjiale_org);
		sDefaultEmoticonMap.put("笑哈哈", R.drawable.emot_default_lxhwahaha_org);
		sDefaultEmoticonMap.put("转发", R.drawable.emot_default_lxhzhuanfa_org);
		sDefaultEmoticonMap.put("得意地笑", R.drawable.emot_default_lxhdeyidixiao_org);
		sDefaultEmoticonMap.put("moc转发", R.drawable.emot_default_moczhuanfa_org);
		sDefaultEmoticonMap.put("bm可爱", R.drawable.emot_default_bmkeai_org);
		sDefaultEmoticonMap.put("lt切克闹", R.drawable.emot_default_ltqiekenao_org);
		sDefaultEmoticonMap.put("xkl转圈", R.drawable.emot_default_xklzhuanquan_org);
		sDefaultEmoticonMap.put("ppb鼓掌", R.drawable.emot_default_ppbguzhang_org);
		sDefaultEmoticonMap.put("din推撞", R.drawable.emot_default_dintuizhuang_org);
		sDefaultEmoticonMap.put("xb压力", R.drawable.emot_default_xbyali_org);
		sDefaultEmoticonMap.put("ali哇", R.drawable.emot_default_aliwanew_org);
		sDefaultEmoticonMap.put("酷库熊顽皮", R.drawable.emot_default_kxwanpi_org);
		sDefaultEmoticonMap.put("BOBO爱你", R.drawable.emot_default_boaini_org);
		sDefaultEmoticonMap.put("偷乐", R.drawable.emot_default_lxhtouxiao_org);
		sDefaultEmoticonMap.put("泪流满面", R.drawable.emot_default_lxhtongku_org);
		sDefaultEmoticonMap.put("江南style", R.drawable.emot_default_gangnamstyle_org);
	}

	public static Spannable getRichWeiboText(Context context, String weiboText) {
		SpannableStringBuilder builder = new SpannableStringBuilder(weiboText);
		int start = 0;
		Log.v(TAG, "Length: " + weiboText.length());

		Matcher emoticonMatcher = sEmoticonPattern.matcher(weiboText);
		while (emoticonMatcher.find()) {
			String key = emoticonMatcher.group(1);
			Log.v(TAG, "key: " + key);
			if (sDefaultEmoticonMap.containsKey(key)) {
				ImageSpan imageSpan = new ImageSpan(context, sDefaultEmoticonMap.get(key), ImageSpan.ALIGN_BASELINE);
				start = weiboText.indexOf("[" + key + "]", start);
				builder.setSpan(imageSpan, start, start + key.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				//				Log.v(TAG, builder.subSequence(start, start + key.length() + 2).toString());
			}
			start += key.length() + 2;
		}

		start = 0;
		Matcher topicMatcher = sTopicPattern.matcher(weiboText);
		while (topicMatcher.find()) {
			String topic = topicMatcher.group(1);
			Log.v(TAG, "topic: " + topic);
			WeiboTopicSpan topicSpan = new WeiboTopicSpan(topic);
			start = weiboText.indexOf(topic, start);
			builder.setSpan(topicSpan, start, start + topic.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			//			Log.v(TAG, builder.subSequence(start, start + topic.length()).toString());
			start += topic.length();
		}

		start = 0;
		Matcher userMatcher = sAtPeoplePattern.matcher(weiboText);
		while (userMatcher.find()) {
			String username = userMatcher.group(1);
			if (username.length() <= 1) {
				// skip single @ character
				continue;
			}
			Log.v(TAG, "user: " + username);
			WeiboAtPeopleSpan atSpan = new WeiboAtPeopleSpan(username);
			start = weiboText.indexOf(username, start);
			builder.setSpan(atSpan, start, start + username.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			//			Log.v(TAG, builder.subSequence(start, start + username.length()).toString());
			start += username.length();
		}

		return builder;
	}
}
