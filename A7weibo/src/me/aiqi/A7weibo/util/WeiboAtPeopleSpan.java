/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.weibo.WeiboAtPeopleSpan.java
 * created at: Oct 3, 2013 5:14:52 AM
 * @author starfish
 */

package me.aiqi.A7weibo.util;

import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

public class WeiboAtPeopleSpan extends ClickableSpan {
	private String mUsername;
	
	public WeiboAtPeopleSpan(String username){
		mUsername = username;
	}

	@Override
	public void onClick(View widget) {
		Toast.makeText(widget.getContext(), "user:" + mUsername, Toast.LENGTH_SHORT).show();
	}
}
