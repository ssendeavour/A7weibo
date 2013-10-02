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
import android.text.style.ImageSpan;
import android.util.Log;

public class WeiboEmoticon {
	public static final HashMap<String, Integer> sEmoticonMap;
	public static final Pattern sEmoticonPattern = Pattern.compile("(?<=\\[)(.+)(?=\\])");
	public static final String TAG = "WeiboEmoticon";

	static {
		sEmoticonMap = new HashMap<String, Integer>();
		sEmoticonMap.put("草泥马", R.drawable.emot_default_shenshou_org);
		sEmoticonMap.put("神马", R.drawable.emot_default_horse2_org);
		sEmoticonMap.put("浮云", R.drawable.emot_default_fuyun_org);
		sEmoticonMap.put("给力", R.drawable.emot_default_geili_org);
		sEmoticonMap.put("围观", R.drawable.emot_default_wg_org);
		sEmoticonMap.put("威武", R.drawable.emot_default_vw_org);
		sEmoticonMap.put("熊猫", R.drawable.emot_default_panda_org);
		sEmoticonMap.put("兔子", R.drawable.emot_default_rabbit_org);
		sEmoticonMap.put("奥特曼", R.drawable.emot_default_otm_org);
		sEmoticonMap.put("囧", R.drawable.emot_default_j_org);
		sEmoticonMap.put("互粉", R.drawable.emot_default_hufen_org);
		sEmoticonMap.put("礼物", R.drawable.emot_default_liwu_org);
		sEmoticonMap.put("呵呵", R.drawable.emot_default_smilea_org);
		sEmoticonMap.put("嘻嘻", R.drawable.emot_default_tootha_org);
		sEmoticonMap.put("哈哈", R.drawable.emot_default_laugh);
		sEmoticonMap.put("可爱", R.drawable.emot_default_tza_org);
		sEmoticonMap.put("可怜", R.drawable.emot_default_kl_org);
		sEmoticonMap.put("挖鼻屎", R.drawable.emot_default_kbsa_org);
		sEmoticonMap.put("吃惊", R.drawable.emot_default_cj_org);
		sEmoticonMap.put("害羞", R.drawable.emot_default_shamea_org);
		sEmoticonMap.put("挤眼", R.drawable.emot_default_zy_org);
		sEmoticonMap.put("闭嘴", R.drawable.emot_default_bz_org);
		sEmoticonMap.put("鄙视", R.drawable.emot_default_bs2_org);
		sEmoticonMap.put("爱你", R.drawable.emot_default_lovea_org);
		sEmoticonMap.put("泪", R.drawable.emot_default_sada_org);
		sEmoticonMap.put("偷笑", R.drawable.emot_default_heia_org);
		sEmoticonMap.put("亲亲", R.drawable.emot_default_qq_org);
		sEmoticonMap.put("生病", R.drawable.emot_default_sb_org);
		sEmoticonMap.put("太开心", R.drawable.emot_default_mb_org);
		sEmoticonMap.put("懒得理你", R.drawable.emot_default_ldln_org);
		sEmoticonMap.put("右哼哼", R.drawable.emot_default_yhh_org);
		sEmoticonMap.put("左哼哼", R.drawable.emot_default_zhh_org);
		sEmoticonMap.put("嘘", R.drawable.emot_default_x_org);
		sEmoticonMap.put("衰", R.drawable.emot_default_cry);
		sEmoticonMap.put("委屈", R.drawable.emot_default_wq_org);
		sEmoticonMap.put("吐", R.drawable.emot_default_t_org);
		sEmoticonMap.put("打哈欠", R.drawable.emot_default_k_org);
		sEmoticonMap.put("抱抱", R.drawable.emot_default_bba_org);
		sEmoticonMap.put("怒", R.drawable.emot_default_angrya_org);
		sEmoticonMap.put("疑问", R.drawable.emot_default_yw_org);
		sEmoticonMap.put("馋嘴", R.drawable.emot_default_cza_org);
		sEmoticonMap.put("拜拜", R.drawable.emot_default_88_org);
		sEmoticonMap.put("思考", R.drawable.emot_default_sk_org);
		sEmoticonMap.put("汗", R.drawable.emot_default_sweata_org);
		sEmoticonMap.put("困", R.drawable.emot_default_sleepya_org);
		sEmoticonMap.put("睡觉", R.drawable.emot_default_sleepa_org);
		sEmoticonMap.put("钱", R.drawable.emot_default_money_org);
		sEmoticonMap.put("失望", R.drawable.emot_default_sw_org);
		sEmoticonMap.put("酷", R.drawable.emot_default_cool_org);
		sEmoticonMap.put("花心", R.drawable.emot_default_hsa_org);
		sEmoticonMap.put("哼", R.drawable.emot_default_hatea_org);
		sEmoticonMap.put("鼓掌", R.drawable.emot_default_gza_org);
		sEmoticonMap.put("晕", R.drawable.emot_default_dizzya_org);
		sEmoticonMap.put("悲伤", R.drawable.emot_default_bs_org);
		sEmoticonMap.put("抓狂", R.drawable.emot_default_crazya_org);
		sEmoticonMap.put("黑线", R.drawable.emot_default_h_org);
		sEmoticonMap.put("阴险", R.drawable.emot_default_yx_org);
		sEmoticonMap.put("怒骂", R.drawable.emot_default_nm_org);
		sEmoticonMap.put("心", R.drawable.emot_default_hearta_org);
		sEmoticonMap.put("伤心", R.drawable.emot_default_unheart);
		sEmoticonMap.put("猪头", R.drawable.emot_default_pig);
		sEmoticonMap.put("ok", R.drawable.emot_default_ok_org);
		sEmoticonMap.put("耶", R.drawable.emot_default_ye_org);
		sEmoticonMap.put("good", R.drawable.emot_default_good_org);
		sEmoticonMap.put("不要", R.drawable.emot_default_no_org);
		sEmoticonMap.put("赞", R.drawable.emot_default_z2_org);
		sEmoticonMap.put("来", R.drawable.emot_default_come_org);
		sEmoticonMap.put("弱", R.drawable.emot_default_sad_org);
		sEmoticonMap.put("蜡烛", R.drawable.emot_default_lazu_org);
		sEmoticonMap.put("钟", R.drawable.emot_default_clock_org);
		sEmoticonMap.put("话筒", R.drawable.emot_default_m_org);
		sEmoticonMap.put("蛋糕", R.drawable.emot_default_cake);
		sEmoticonMap.put("带着微博去旅行", R.drawable.emot_default_weitripballoon_org);
		sEmoticonMap.put("玩去啦", R.drawable.emot_default_weitrip_org);
		sEmoticonMap.put("放假啦", R.drawable.emot_default_lxhfangjiale_org);
		sEmoticonMap.put("笑哈哈", R.drawable.emot_default_lxhwahaha_org);
		sEmoticonMap.put("转发", R.drawable.emot_default_lxhzhuanfa_org);
		sEmoticonMap.put("得意地笑", R.drawable.emot_default_lxhdeyidixiao_org);
		sEmoticonMap.put("moc转发", R.drawable.emot_default_moczhuanfa_org);
		sEmoticonMap.put("bm可爱", R.drawable.emot_default_bmkeai_org);
		sEmoticonMap.put("lt切克闹", R.drawable.emot_default_ltqiekenao_org);
		sEmoticonMap.put("xkl转圈", R.drawable.emot_default_xklzhuanquan_org);
		sEmoticonMap.put("ppb鼓掌", R.drawable.emot_default_ppbguzhang_org);
		sEmoticonMap.put("din推撞", R.drawable.emot_default_dintuizhuang_org);
		sEmoticonMap.put("xb压力", R.drawable.emot_default_xbyali_org);
		sEmoticonMap.put("ali哇", R.drawable.emot_default_aliwanew_org);
		sEmoticonMap.put("酷库熊顽皮", R.drawable.emot_default_kxwanpi_org);
		sEmoticonMap.put("BOBO爱你", R.drawable.emot_default_boaini_org);
		sEmoticonMap.put("偷乐", R.drawable.emot_default_lxhtouxiao_org);
		sEmoticonMap.put("泪流满面", R.drawable.emot_default_lxhtongku_org);
		sEmoticonMap.put("江南style", R.drawable.emot_default_gangnamstyle_org);
	}

	public static Spannable getRichWeiboText(Context context, String weiboText) {
		SpannableStringBuilder builder = new SpannableStringBuilder(weiboText);
		Matcher matcher = sEmoticonPattern.matcher(weiboText);
		Log.v(TAG, weiboText);
		while (matcher.find()) {
			String key = matcher.group(1);
			Log.v(TAG, "key: " + key);
			if (sEmoticonMap.containsKey(key)) {
				ImageSpan imageSpan = new ImageSpan(context, sEmoticonMap.get(key), ImageSpan.ALIGN_BASELINE);
				int start = weiboText.indexOf("[" + key + "]");
				builder.setSpan(imageSpan, start, start + key.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return builder;
	}
}
