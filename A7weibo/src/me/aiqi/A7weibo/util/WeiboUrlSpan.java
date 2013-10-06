/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.weibo.WeiboUrlSpan.java
 * created at: Oct 4, 2013 10:31:29 PM
 * @author starfish
 */

package me.aiqi.A7weibo.util;

import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

public class WeiboUrlSpan extends ClickableSpan {

	private String urlString;

	public WeiboUrlSpan(String url) {
		urlString = url;
	}

	@Override
	public void onClick(View widget) {
		Toast.makeText(widget.getContext(), urlString, Toast.LENGTH_SHORT).show();
	}
}
