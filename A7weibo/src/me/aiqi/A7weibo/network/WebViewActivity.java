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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
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
		final Activity activity = getActivity();
		mWebView = new WebView(getActivity());
		mWebView.getSettings().setJavaScriptEnabled(true);

		mWebView.setWebChromeClient(new WebChromeClient());
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

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return mWebView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final AppRegInfo appRegInfo = getAppRegInfo();
		Log.v(TAG, appRegInfo.toString());

		StringBuilder urlBuilder = new StringBuilder("https://open.weibo.cn/oauth2/authorize?");
		urlBuilder.append("client_id=").append(appRegInfo.getAppKey())
				.append("&redirect_uri=").append(appRegInfo.getAppUrl())
				.append("&display=mobile")
				.append("&response_ty");
		mWebView.loadUrl(urlBuilder.toString());
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

interface AuthenticationListener {
	public void onAuthException();

	public void onError(String errorMessage);

	public void onAuthComplete(Bundle values);

	public void onAuthCancel();

}