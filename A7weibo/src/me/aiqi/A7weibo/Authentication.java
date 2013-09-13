package me.aiqi.A7weibo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.aiqi.A7weibo.authkeep.AccessTokenKeeper;
import me.aiqi.A7weibo.entity.AppRegInfo;
import me.aiqi.A7weibo.util.AppRegInfoHelper;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;

public class Authentication extends Activity {

	private static final String TAG = "Authentication";
	private Oauth2AccessToken accessToken;
	private Context mContext;

	public Authentication() {
	}

	public Authentication(Context context) {
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.authentication);
		if (mContext == null) {
			Log.w(TAG, "mContext is null");
		}
		accessToken = AccessTokenKeeper.readAccessToken(mContext);
	}

	/**
	 * is access_token expired
	 * 
	 * @return
	 */
	public Boolean isAuthExpired() {
		if (accessToken != null) {
			return accessToken.isSessionValid();
		}
		return true;
	}

	/**
	 * Button click listener
	 * 
	 * @param view
	 */
	public void auth_click(View view) {
		auth();
	}

	/**
	 * perform authentication
	 */
	public void auth() {
		if (!isAuthExpired()) {
			String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US).format(new Date(accessToken.getExpiresTime()));
			String msg = "access_token 仍在有效期内,无需再次登录: \naccess_token:" + accessToken.getToken() + "\n有效期：" + date;
			Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
			Log.i(TAG, msg);
			return;
		}
		AppRegInfo appInfo = AppRegInfoHelper.getAppRegInfo();
		if (appInfo == null) {
			Toast.makeText(mContext, "can't get app key", Toast.LENGTH_SHORT).show();
			Log.e(TAG, "can't get app key");
			return;
		}
		Weibo weibo = Weibo.getInstance(appInfo.getAppKey(), appInfo.getAppUrl(), null);
		new SsoHandler(this, weibo).authorize(new WeiboAuthListener() {

			@Override
			public void onWeiboException(WeiboException arg0) {
				Toast.makeText(mContext, "微博异常：" + arg0, Toast.LENGTH_SHORT).show();
				Log.w(TAG, arg0.toString());
			}

			@Override
			public void onError(WeiboDialogError arg0) {
				String msg = "授权失败:" + arg0.getMessage();
				Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				Log.e(TAG, msg);
			}

			@Override
			public void onComplete(Bundle values) {
				String code = values.getString("code");
				if (code != null) {
					Log.i(TAG, "取得认证code: " + code);
					Toast.makeText(mContext, "认证code成功", Toast.LENGTH_SHORT).show();
					return;
				}
				String token = values.getString("access_token");
				String expires_in = values.getString("expires_in");
				accessToken = new Oauth2AccessToken(token, expires_in);
				if (accessToken.isSessionValid()) {
					String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US).format(new Date(accessToken.getExpiresTime()));
					String msg = "认证成功: access_token: " + token + ", " + "expires_in: " + expires_in + "有效期：" + date;
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
					Log.i(TAG, msg);
					AccessTokenKeeper.keepAccessToken(Authentication.this, accessToken);
				}
			}

			@Override
			public void onCancel() {
				String msg = "授权被取消";
				Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
				Log.i(TAG, msg);
			}
		}, null);
	}
}
