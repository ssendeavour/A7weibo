/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.network.Authentication.java
 * created at: Oct 2, 2013 1:36:15 AM
 * @author starfish
 */

package me.aiqi.A7weibo.network;

import me.aiqi.A7weibo.entity.AppRegInfo;
import me.aiqi.A7weibo.util.AppRegInfoHelper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends DialogFragment {
	private static final String TAG = WebViewActivity.class.getSimpleName();
	private WebView mWebView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWebView = new WebView(getActivity());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//get code
				if (url.contains("code=")) {
					processCode(url);
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		});

		final AppRegInfo appRegInfo = getAppRegInfo();
		Log.v(TAG, appRegInfo.toString());

	}

	private AppRegInfo getAppRegInfo() {
		final AppRegInfo appInfo = AppRegInfoHelper.getAppRegInfo();
		if (appInfo == null) {
			final String msg = "授权失败：无法获取AppRegInfo";
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			Log.e(TAG, msg);
		}
		return appInfo;
	}

	protected void processCode(String url) {
		Log.v(TAG, url);
	}

}
