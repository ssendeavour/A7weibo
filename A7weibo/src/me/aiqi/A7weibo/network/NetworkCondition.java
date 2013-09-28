/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.network.NetworkCondition.java
 * created at: Sep 28, 2013 3:49:36 PM
 * @author starfish
 */

package me.aiqi.A7weibo.network;

import me.aiqi.A7weibo.MyApplication;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkCondition {

	public static boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) MyApplication.getContext().getSystemService(
				Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
}
