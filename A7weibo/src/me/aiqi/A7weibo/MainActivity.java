package me.aiqi.A7weibo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.aiqi.A7weibo.auth.AccessTokenKeeper;
import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.AppRegInfo;
import me.aiqi.A7weibo.entity.Consts;
import me.aiqi.A7weibo.util.AppRegInfoHelper;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
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

	protected static final String TAG = "MainActivity";
	static final int TAB_ITEM_NUMBER = 3;
	protected static final int BEGIN_GET_ACCESS_TOKEN_FROM_CODE = 0x100;
	protected static final int FINISH_GET_ACCESS_TOKEN_FAILED = 0x101;
	protected static final int FINISH_GET_ACCESS_TOKEN_SUCCEEDED = 0x102;

	private AppRegInfo appRegInfo = AppRegInfoHelper.getAppRegInfo();
	private Handler handler;
	private SsoHandler ssoHandler;

	private AccessToken accessToken;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUI();
		
		accessToken = AccessTokenKeeper.readAccessToken(this);
		if (accessToken == null || accessToken.isExpired()) {
			Log.i(TAG, "Authentication expired, expire time:" + accessToken.getExpireTimeString());
			auth();
		} else {
			Log.i(TAG, "Authentication is valid. Expire time: " + accessToken.getExpireTimeString());
		}
		// at this point, accessToken should be valid if user granted access
		((GlobalVariable) getApplicationContext()).setAccessToken(accessToken);

		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case BEGIN_GET_ACCESS_TOKEN_FROM_CODE:

					break;

				case FINISH_GET_ACCESS_TOKEN_SUCCEEDED:
					String result = (String) msg.obj;
					try {
						JSONObject jsonObject = new JSONObject(result);
						appRegInfo.setUid(jsonObject.getLong(Consts.UID));
						accessToken.setAccessToken(jsonObject.getString(Consts.ACCESS_TOKEN));
						accessToken.setExpireTimeFromExpiresIn(jsonObject.getLong(Consts.EXPIRES_IN));

						AccessTokenKeeper.keepAccessToken(MainActivity.this, accessToken);
						Log.i(TAG, appRegInfo.toString());
						Log.i(TAG, accessToken.toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}

					break;
				case FINISH_GET_ACCESS_TOKEN_FAILED:
					Toast.makeText(MainActivity.this, "授权失败：获取access_token失败", Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
				}
			};
		};

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

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			if (position == 0) {
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
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
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
	}

	@Override
	public void onTabUnselected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
	}

	/**
	 * perform authentication
	 */
	public void auth() {
		if (!accessToken.isExpired()) {
			String msg = "access_token 仍在有效期内,无需再次登录; " + accessToken.toString();
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
			Log.i(TAG, msg);
			return;
		}
		AppRegInfo appInfo = AppRegInfoHelper.getAppRegInfo();
		if (appInfo == null) {
			Toast.makeText(this, "can't get app key", Toast.LENGTH_SHORT).show();
			Log.e(TAG, "can't get app key");
			return;
		}
		Log.i(TAG, appInfo.toString());
		String SCOPE = "direct_messages_read,direct_messages_write," + "statuses_to_me_read," + "follow_app_official_microblog";
		Weibo weibo = Weibo.getInstance(appInfo.getAppKey(), appInfo.getAppUrl(), SCOPE);
		ssoHandler = new SsoHandler(this, weibo);
		ssoHandler.authorize(new WeiboAuthListener() {

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

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(arg0, arg1, arg2);
		}
	}

	public void getAccessTokenFromCode(final String code) {
		new Thread() {
			public void run() {
				handler.sendMessage(handler.obtainMessage(BEGIN_GET_ACCESS_TOKEN_FROM_CODE));
				String url = "https://api.weibo.com/oauth2/access_token";
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
					HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						httpResponse.getEntity();
						result = EntityUtils.toString(httpResponse.getEntity());
					}
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendMessage(handler.obtainMessage(FINISH_GET_ACCESS_TOKEN_FAILED, e.getMessage()));
					Log.e(TAG, e.getMessage());
				}
				Log.i(TAG, "access_token: " + result);
				handler.sendMessage(handler.obtainMessage(FINISH_GET_ACCESS_TOKEN_SUCCEEDED, result));
			};
		}.start();
	}
}
