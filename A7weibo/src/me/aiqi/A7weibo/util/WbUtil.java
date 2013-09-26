package me.aiqi.A7weibo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import com.weibo.sdk.android.Oauth2AccessToken;

/**
 * Utility class used in A7weibo for Android project
 * 
 * @author starfish
 * 
 */
public class WbUtil {

	private static final String TAG = "WbUtil";

	/**
	 * H: 00-23, h: 0-11(am/pm) Z: timezone (+8000) see full doc <a href=
	 * "http://docs.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html"
	 * >docs.oracle.com</a>
	 */

	private static java.text.DateFormat weiboCreateDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy",
			Locale.ENGLISH);
	private static java.text.DateFormat yyyyMMddhhmmsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
			Locale.ENGLISH);
	private static java.text.DateFormat hhmmDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
	private static java.text.DateFormat mMddHHmmDateFormat = new SimpleDateFormat("M月d号 HH:mm", Locale.ENGLISH);

	/**
	 * Format Oauth2AccessToken expire time to human readable string (yyyy-MM-dd
	 * HH:mm:ss) for easy debug
	 * 
	 * @param accessToken
	 * @return Formated date string
	 */
	public static String getExpireDateString(Oauth2AccessToken accessToken) {
		if (accessToken == null) {
			return "";
		}
		return yyyyMMddhhmmsFormat.format(new Date(1000 * accessToken
				.getExpiresTime()));
	}

	/**
	 * parse Date string like: "Sat Sep 21 23:39:40 +0800 2013" to
	 * java.util.Date object
	 * 
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static Calendar getCalenderFromDateString(String dateString) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(weiboCreateDateFormat.parse(dateString));
		Log.v(TAG, cal.getTime().toString());
		return cal;
	}

	/**
	 * format Calendar object in user friendly way, like: just now, 5 minutes
	 * ago, yesterday 13:21, 5/9 23:05
	 * 
	 * @param date
	 * @return
	 */
	public static String getUserFriendlyTime(Calendar date) {
		if (date == null) {
			return null;
		}
		Calendar now = Calendar.getInstance();
		long interval = (now.getTimeInMillis() - date.getTimeInMillis()) / 1000;
		String result = null;
		if (interval <= 60 * 60 && interval >= 0) {
			if (interval < 6) {
				// 0 - 5 seconds
				result = "刚刚";
			} else if (interval < 60) {
				// 7 seconds - 1 minutes
				result = String.valueOf(interval) + "秒前";
			} else {
				// 1 minutes - 1 hour
				result = String.valueOf(interval / 60) + "分钟前";
			}
		} else {
			// older than one hour
			Calendar yesterDay = Calendar.getInstance();
			yesterDay.setTime(now.getTime());
			yesterDay.add(Calendar.DAY_OF_YEAR, -1); // also work for date like
														// January 1st.
			if (date.get(Calendar.YEAR) == now.get(Calendar.YEAR)
					&& date.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
				// today
				result = hhmmDateFormat.format(date.getTime());
			} else if (date.get(Calendar.YEAR) == yesterDay.get(Calendar.YEAR)
					&& date.get(Calendar.DAY_OF_YEAR) == yesterDay.get(Calendar.DAY_OF_YEAR)) {
				// yesterday
				result = "昨天" + hhmmDateFormat.format(date.getTime());
			} else {
				// otherwise
				result = mMddHHmmDateFormat.format(date.getTime());
			}
		}
		return result;
	}

	/**
	 * get user friendly data string, a combination of getCalenderFromDateString
	 * and getUserFriendlyTime
	 * 
	 * @param dateString
	 * @return converted user-friendly data string, null if parse fail
	 */
	public static String getTimeString(String dateString) {
		String result = null;
		try {
			result = getUserFriendlyTime(getCalenderFromDateString(dateString));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
}
