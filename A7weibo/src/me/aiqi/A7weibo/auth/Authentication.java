package me.aiqi.A7weibo.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;

import me.aiqi.A7weibo.GlobalVariable;
import me.aiqi.A7weibo.MainActivity;
import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.AppRegInfo;
import me.aiqi.A7weibo.entity.Consts;
import me.aiqi.A7weibo.network.SslClient;
import me.aiqi.A7weibo.util.AppRegInfoHelper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class Authentication {

	private static final String TAG = "OAuth2";
	private static boolean isRunning = false;

	public static void login(Context context, Handler handler) {
		// another authentication is in process
		if (isRunning) {
			return;
		}
		AccessToken accessToken = AccessTokenKeeper.readAccessToken(context);
		if (accessToken == null || accessToken.isExpired()) {
			Log.i(TAG, "access_token expired, expire time:" + accessToken.getExpireTimeString());
			auth(context, handler);
		} else {
			Log.i(TAG, "access_token is valid. Expire time: " + accessToken.getExpireTimeString());
			isRunning = false;
			// store new access token to application-wide range
			((GlobalVariable) context.getApplicationContext()).setAccessToken(accessToken);
		}
	}

	/**
	 * perform SSO or OAuth 2.0 authentication
	 */
	public static void auth(final Context context, final Handler handler) {
		final AppRegInfo appInfo = AppRegInfoHelper.getAppRegInfo();
		if (appInfo == null) {
			Toast.makeText(context, "no app key found", Toast.LENGTH_SHORT).show();
			Log.e(TAG, "no app key found");
			isRunning = false;
			return;
		}
		Log.i(TAG, appInfo.toString());

		// String SCOPE = "direct_messages_read,direct_messages_write," +
		// "statuses_to_me_read," + "follow_app_official_microblog";
		Weibo weibo = Weibo.getInstance(appInfo.getAppKey(), appInfo.getAppUrl(), null);
		new SsoHandler((Activity) context, weibo).authorize(new WeiboAuthListener() {

			@Override
			public void onWeiboException(WeiboException arg0) {
				Toast.makeText(context, "微博异常：" + arg0, Toast.LENGTH_SHORT).show();
				Log.w(TAG, arg0.toString());
				isRunning = false;
			}

			@Override
			public void onError(WeiboDialogError arg0) {
				String msg = "授权出错:" + arg0.getMessage();
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				Log.e(TAG, msg);
				isRunning = false;
			}

			@Override
			public void onComplete(Bundle values) {
				Log.i(TAG, values.toString());
				String code = values.getString("code");
				if (code != null) {
					Log.i(TAG, "取得认证code: " + code);
					getAccessTokenFromCode(context, handler, code, appInfo);
				} else {
					Log.i(TAG, "未取得认证code, " + values.toString());
					Toast.makeText(context, "授权失败：无法获得Oauth code", Toast.LENGTH_SHORT).show();
					isRunning = false;
				}
			}

			@Override
			public void onCancel() {
				String msg = "授权取消";
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				Log.i(TAG, msg);
				isRunning = false;
			}
		}, null);
	}
	
	/**
	 * get access token, and save it in global application space
	 * 
	 * @param context
	 * @param handler
	 * @param code
	 * @param appRegInfo
	 */
	public static void getAccessTokenFromCode(final Context context, final Handler handler, final String code, final AppRegInfo appRegInfo) {
		final String url = "https://api.weibo.com/oauth2/access_token";
		new Thread() {
			public void run() {
				boolean succeed = true;
				handler.sendMessage(handler.obtainMessage(MainActivity.MyHandler.BEGIN_GET_ACCESS_TOKEN_FROM_CODE));
				String result = null;
				HttpPost httpRequest = new HttpPost(url);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("client_id", appRegInfo.getAppKey()));
				params.add(new BasicNameValuePair("client_secret", appRegInfo.getAppSecret()));
				params.add(new BasicNameValuePair("grant_type", "authorization_code"));
				params.add(new BasicNameValuePair("redirect_uri", appRegInfo.getAppUrl()));
				params.add(new BasicNameValuePair("code", code));
				try {
					httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					Log.d(TAG, "Post url: " + httpRequest.getURI());
					HttpClient httpClient = SslClient.getSslClient(new DefaultHttpClient());
					HttpResponse httpResponse = httpClient.execute(httpRequest);
					int statusCode = httpResponse.getStatusLine().getStatusCode();
					if (statusCode == HttpStatus.SC_OK) {
						httpResponse.getEntity();
						result = EntityUtils.toString(httpResponse.getEntity());
					} else {
						String msg = "Fail to get accesstoken, status code" + statusCode;
						Log.w(TAG, msg);
						Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
						succeed = false;
					}
				} catch (Exception e) {
					succeed = false;
					e.printStackTrace();
					handler.sendMessage(handler.obtainMessage(MainActivity.MyHandler.FINISH_GET_ACCESS_TOKEN_FAILED, e.getMessage()));
					Log.e(TAG, e.getMessage());
					return;
				}
				Log.i(TAG, result);
				try {
					AccessToken accessToken = new AccessToken();
					JSONObject jsonObject = new JSONObject(result);
					appRegInfo.setUid(jsonObject.getLong(Consts.UID));
					accessToken.setAccessToken(jsonObject.getString(Consts.ACCESS_TOKEN));
					accessToken.setExpireTimeFromExpiresIn(jsonObject.getLong(Consts.EXPIRES_IN));

					AccessTokenKeeper.keepAccessToken(context, accessToken);
					((GlobalVariable) context.getApplicationContext()).setAccessToken(accessToken);
					Log.i(TAG, accessToken.toString());
				} catch (JSONException e) {
					Log.w(TAG, "Error parsing authentication json");
					e.printStackTrace();
					succeed = false;
				}
				handler.sendMessage(handler.obtainMessage(succeed ? MainActivity.MyHandler.FINISH_GET_ACCESS_TOKEN_SUCCEEDED
						: MainActivity.MyHandler.FINISH_GET_ACCESS_TOKEN_FAILED));
			};
		}.start();
	}
}
