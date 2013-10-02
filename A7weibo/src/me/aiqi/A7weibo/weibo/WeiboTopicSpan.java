/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.weibo.WeiboTopicSpan.java
 * created at: Oct 3, 2013 5:05:25 AM
 * @author starfish
 */

package me.aiqi.A7weibo.weibo;

import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

public class WeiboTopicSpan extends ClickableSpan {
	private String mTopic;

	public WeiboTopicSpan(String topic) {
		mTopic = topic;
	}

	@Override
	public void onClick(View widget) {
		Toast.makeText(widget.getContext(), "topic:" + mTopic, Toast.LENGTH_SHORT).show();
	}
}
