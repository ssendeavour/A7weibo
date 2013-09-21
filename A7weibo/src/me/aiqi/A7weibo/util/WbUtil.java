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
	 * Format Oauth2AccessToken expire time to human readable string (yyyy-MM-dd
	 * hh:mm:ss) for easy debug
	 * 
	 * @param accessToken
	 * @return Formated date string
	 */
	public static String getExpireDateString(Oauth2AccessToken accessToken) {
		if (accessToken == null) {
			return "";
		}
		return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US).format(new Date(1000 * accessToken.getExpiresTime()));
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
		java.text.DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
		Calendar cal = Calendar.getInstance();
		cal.setTime(df.parse(dateString));
		Log.d(TAG, cal.getTime().toString());
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
			Log.d(TAG, "yesterday: " + yesterDay.getTime().toString());
			Log.d(TAG, "now: " + now.getTime().toString());
			if (date.get(Calendar.YEAR) == now.get(Calendar.YEAR) && date.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
				// today
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
				result = sdf.format(date.getTime());
			} else if (date.get(Calendar.YEAR) == yesterDay.get(Calendar.YEAR)
					&& date.get(Calendar.DAY_OF_YEAR) == yesterDay.get(Calendar.DAY_OF_YEAR)) {
				// yesterday
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
				result = "昨天" + sdf.format(date.getTime());
			} else {
				// otherwise
				SimpleDateFormat sdf = new SimpleDateFormat("M月d号 HH:mm", Locale.ENGLISH);
				result = sdf.format(date.getTime());
			}
		}
		return result;
	}

	/**
	 * get user friendly data string, a combination of getCalenderFromDateString
	 * and getUserFriendlyTime
	 * 
	 * @param dateString
	 * @return
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
