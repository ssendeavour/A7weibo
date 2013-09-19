package me.aiqi.A7weibo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import me.aiqi.A7weibo.auth.AccessTokenKeeper;
import me.aiqi.A7weibo.downloader.WeiboDownloader;
import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.AppRegInfo;
import me.aiqi.A7weibo.entity.Consts;
import me.aiqi.A7weibo.entity.WeiboItem;
import me.aiqi.A7weibo.network.SslClient;
import me.aiqi.A7weibo.util.AppRegInfoHelper;

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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

	public static final String TAG = "MainActivity";

	public static final int TAB_ITEM_NUMBER = 3;
	public static final int TAB_COMMENT_AT = 0;
	public static final int TAB_WEIBO = 1;
	public static final int TAB_ME = 2;

	protected static final int BEGIN_GET_ACCESS_TOKEN_FROM_CODE = 0x100;
	protected static final int FINISH_GET_ACCESS_TOKEN_FAILED = 0x101;
	protected static final int FINISH_GET_ACCESS_TOKEN_SUCCEEDED = 0x102;

	private AppRegInfo mAppRegInfo = AppRegInfoHelper.getAppRegInfo();
	private SsoHandler mSsoHandler;
	private AccessToken mAccessToken;

	private Handler mHandler;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private WeiboViewFragment mWeiboFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUI();
		mViewPager.setCurrentItem(TAB_WEIBO);

		// Access Token, Re-authentication if necessary
		login();
		Log.i(TAG, "OAuth finished");
		mWeiboFragment = (WeiboViewFragment) mSectionsPagerAdapter.getItem(TAB_WEIBO);

		mHandler = new MyHandler();
	}

	protected class MyHandler extends Handler {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BEGIN_GET_ACCESS_TOKEN_FROM_CODE:
				break;

			case FINISH_GET_ACCESS_TOKEN_SUCCEEDED:
				Toast.makeText(MainActivity.this, "授权成功!", Toast.LENGTH_SHORT).show();

				if (mWeiboFragment == null) {
					Log.w(TAG, "mWeiboFragment is null");
				} else {
					WeiboListAdapter adapter = (WeiboListAdapter) mWeiboFragment.getListAdapter();
					if (adapter == null) {
						Log.w(TAG, "WeiboListAdapter is null, set a new one");
						adapter = new WeiboListAdapter(MainActivity.this);
						mWeiboFragment.setListAdapter(adapter);
					}
					WeiboDownloader.Params params = new WeiboDownloader.Params();
					params.put(WeiboDownloader.Params.ACCESS_TOKEN, mAccessToken.getAccessToken());
					adapter.getWeiboItems(params);
				}

				break;
			case FINISH_GET_ACCESS_TOKEN_FAILED:
				Toast.makeText(MainActivity.this, "授权失败：" + (msg == null ? "" : (String) msg.obj), Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	}

	@Override
	protected void onStart() {
		if (mAccessToken != null && !mAccessToken.isExpired()) {
			if (mWeiboFragment == null) {
				Log.w(TAG, "mWeiboFragment is null");
			} else {
				WeiboDownloader.Params params = new WeiboDownloader.Params();
				params.put(WeiboDownloader.Params.ACCESS_TOKEN, mAccessToken.getAccessToken());
				WeiboListAdapter adapter = (WeiboListAdapter) mWeiboFragment.getListAdapter();
				if (adapter != null) {
					adapter.getWeiboItems(params);
				} else {
					Log.w(TAG, "WeiboListAdapter is null");
				}
			}
		}
		super.onStart();
	}

	private void initUI() {
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}

	private void login() {
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		if (mAccessToken == null || mAccessToken.isExpired()) {
			Log.i(TAG, "Authentication expired, expire time:" + mAccessToken.getExpireTimeString());
			auth();
		} else {
			Log.i(TAG, "Authentication is valid. Expire time: " + mAccessToken.getExpireTimeString());
		}
		// store new access token to application-wide range
		((GlobalVariable) getApplicationContext()).setAccessToken(mAccessToken);
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			if (position == TAB_WEIBO) {
				fragment = new WeiboViewFragment();
			} else {
				fragment = new DummySectionFragment();
				Bundle args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return TAB_ITEM_NUMBER;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case TAB_WEIBO:
				return getString(R.string.title_section_weibo).toUpperCase(l);
			case TAB_COMMENT_AT:
				return getString(R.string.title_section_comment_at).toUpperCase(l);
			case TAB_ME:
				return getString(R.string.title_section_me).toUpperCase(l);
			}
			return null;
		}
	}

	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
			View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(sectionNumber));
			return rootView;
		}
	}

	@Override
	public void onTabReselected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
	}

	@Override
	public void onTabSelected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
		mViewPager.setCurrentItem(arg0.getPosition());
		// getSupportActionBar().setSelectedNavigationItem(arg0.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
	}

	/**
	 * perform authentication
	 */
	public void auth() {
		if (!mAccessToken.isExpired()) {
			String msg = "access_token 仍在有效期内,无需再次授权" + mAccessToken.toString();
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
			Log.i(TAG, msg);
			return;
		}
		AppRegInfo appInfo = AppRegInfoHelper.getAppRegInfo();
		if (appInfo == null) {
			Toast.makeText(this, "no app key found", Toast.LENGTH_SHORT).show();
			Log.e(TAG, "no app key found");
			return;
		}
		Log.i(TAG, appInfo.toString());
		String SCOPE = "direct_messages_read,direct_messages_write," + "statuses_to_me_read," + "follow_app_official_microblog";
		Weibo weibo = Weibo.getInstance(appInfo.getAppKey(), appInfo.getAppUrl(), SCOPE);
		mSsoHandler = new SsoHandler(this, weibo);
		mSsoHandler.authorize(new WeiboAuthListener() {

			@Override
			public void onWeiboException(WeiboException arg0) {
				Toast.makeText(MainActivity.this, "微博异常：" + arg0, Toast.LENGTH_SHORT).show();
				Log.w(TAG, arg0.toString());
			}

			@Override
			public void onError(WeiboDialogError arg0) {
				String msg = "授权失败:" + arg0.getMessage();
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
				Log.e(TAG, msg);
			}

			@Override
			public void onComplete(Bundle values) {
				Log.i(TAG, values.toString());
				String code = values.getString("code");
				if (code != null) {
					Log.i(TAG, "取得认证code: " + code);
					Toast.makeText(MainActivity.this, "认证code成功", Toast.LENGTH_SHORT).show();
					getAccessTokenFromCode(code);
				} else {
					Log.i(TAG, "未取得认证code, " + values.toString());
					Toast.makeText(MainActivity.this, "授权失败：无法获得Oauth2.0 code", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onCancel() {
				String msg = "授权被取消";
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
				Log.i(TAG, msg);
			}
		}, null);
	}

	/**
	 * required by Weibo OAuth2.0
	 */
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(arg0, arg1, arg2);
		}
	}

	public void getAccessTokenFromCode(final String code) {
		new Thread() {
			public void run() {
				boolean succeed = true;
				mHandler.sendMessage(mHandler.obtainMessage(BEGIN_GET_ACCESS_TOKEN_FROM_CODE));
				String url = "https://api.weibo.com/oauth2/access_token";
				String result = null;
				HttpPost httpRequest = new HttpPost(url);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("client_id", mAppRegInfo.getAppKey()));
				params.add(new BasicNameValuePair("client_secret", mAppRegInfo.getAppSecret()));
				params.add(new BasicNameValuePair("grant_type", "authorization_code"));
				params.add(new BasicNameValuePair("redirect_uri", mAppRegInfo.getAppUrl()));
				params.add(new BasicNameValuePair("code", code));
				try {
					httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					HttpClient httpClient = SslClient.getSslClient(new DefaultHttpClient());
					HttpResponse httpResponse = httpClient.execute(httpRequest);
					int statusCode = httpResponse.getStatusLine().getStatusCode();
					if (statusCode == HttpStatus.SC_OK) {
						httpResponse.getEntity();
						result = EntityUtils.toString(httpResponse.getEntity());
					} else {
						String msg = "Fail to get accesstoken, status code" + statusCode;
						Log.w(TAG, msg);
						Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
						succeed = false;
					}
				} catch (Exception e) {
					succeed = false;
					e.printStackTrace();
					mHandler.sendMessage(mHandler.obtainMessage(FINISH_GET_ACCESS_TOKEN_FAILED, e.getMessage()));
					Log.e(TAG, e.getMessage());
					return;
				}
				Log.i(TAG, result);
				try {
					JSONObject jsonObject = new JSONObject(result);
					mAppRegInfo.setUid(jsonObject.getLong(Consts.UID));
					mAccessToken.setAccessToken(jsonObject.getString(Consts.ACCESS_TOKEN));
					mAccessToken.setExpireTimeFromExpiresIn(jsonObject.getLong(Consts.EXPIRES_IN));

					AccessTokenKeeper.keepAccessToken(MainActivity.this, mAccessToken);
					Log.i(TAG, mAccessToken.toString());
				} catch (JSONException e) {
					Log.w(TAG, "Error parsing authentication json");
					e.printStackTrace();
					succeed = false;
				}
				mHandler.sendMessage(mHandler.obtainMessage(succeed ? FINISH_GET_ACCESS_TOKEN_SUCCEEDED : FINISH_GET_ACCESS_TOKEN_FAILED));
			};
		}.start();
	}
}
