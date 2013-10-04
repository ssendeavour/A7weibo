package me.aiqi.A7weibo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.AppRegInfo;
import me.aiqi.A7weibo.network.SslClient;
import me.aiqi.A7weibo.util.AccessTokenKeeper;
import me.aiqi.A7weibo.util.AppRegInfoHelper;
import me.aiqi.A7weibo.weibo.WeiboListAdapter;
import me.aiqi.A7weibo.weibo.WeiboListCallback;
import me.aiqi.A7weibo.weibo.WeiboListFragment;

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

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import android.annotation.SuppressLint;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

@SuppressLint("HandlerLeak")
public class MainActivity extends ActionBarActivity implements WeiboListCallback {
	
	public static final String TAG = MainActivity.class.getSimpleName();

	public static final int GET_ACCESS_TOKEN_FROM_CODE_START = 0x100;

	/**
	 * msg.obj should set to responding HTTP status code ({@code int}).<br />
	 * see also {@link GET_ACCESS_TOKEN_FROM_CODE_EXCEPTION}
	 */
	public static final int GET_ACCESS_TOKEN_FROM_CODE_FAILED = 0x101;

	/** msg.obj is not used in handleMessage */
	public static final int GET_ACCESS_TOKEN_FROM_CODE_SUCCEED = 0x102;

	/**
	 * msg.obj should set to a text description. <br />
	 * see also {@link GET_ACCESS_TOKEN_FROM_CODE_FAILED}
	 */
	public static final int GET_ACCESS_TOKEN_FROM_CODE_EXCEPTION = 0x103;

	/** msg.obj is not used in handleMessage */
	public static final int GET_ACCESS_TOKEN_FROM_CODE_PARSE_JSON_EXCEPTION = 0x104;

	public static final int OAUTH_WEIBO_EXCEPTION = 0x110;
	public static final int OAUTH_APP_KEY_NOT_FOUND = 0x111;
	public static final int OAUTH_CANCELED = 0x112;
	public static final int OAUTH_SUCCEED = 0x113;
	public static final int OAUTH_ERROR = 0x114;
	public static final int OAUTH_GOT_ACCESS_CODE = 0x115;
	public static final int OAUTH_FAILED = 0x116;

	public static final int TAB_ITEM_NUMBER = 3;
	public static final int TAB_COMMENT_AT = 1;
	public static final int TAB_WEIBO = 0;
	public static final int TAB_ME = 2;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private WeiboListFragment mWeiboFragment;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	private AccessToken mAccessToken;
	private Handler mHandler;
	protected static AtomicBoolean isLoggingOn = new AtomicBoolean(false);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUI();

		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case GET_ACCESS_TOKEN_FROM_CODE_START:
					break;

				case GET_ACCESS_TOKEN_FROM_CODE_SUCCEED:
					Toast.makeText(MainActivity.this, "授权成功!", Toast.LENGTH_SHORT).show();
					mAccessToken = ((MyApplication) getApplicationContext()).getAccessToken();
					if (mWeiboFragment == null) {
						Log.d(TAG, "mWeiboFragment is null");
					} else {
						WeiboListAdapter adapter = (WeiboListAdapter) mWeiboFragment.getListAdapter();
						if (adapter == null) {
							Log.v(TAG, "got access token, but WeiboListAdapter is null, create one");
							adapter = new WeiboListAdapter(MainActivity.this);
							mWeiboFragment.setListAdapter(adapter);
						}
						adapter.refresh(mAccessToken);
					}
					break;

				case GET_ACCESS_TOKEN_FROM_CODE_FAILED:
					String msgString = "授权失败：http code: " + (java.lang.Integer) msg.obj;
					Toast.makeText(MainActivity.this, msgString, Toast.LENGTH_SHORT).show();
					Log.w(TAG, msgString);
					break;

				case GET_ACCESS_TOKEN_FROM_CODE_PARSE_JSON_EXCEPTION:
					String msgString1 = "授权失败:解析json出错";
					Toast.makeText(MainActivity.this, msgString1, Toast.LENGTH_SHORT).show();
					Log.w(TAG, msgString1);

				case OAUTH_APP_KEY_NOT_FOUND:
					Toast.makeText(MainActivity.this, "no app key found", Toast.LENGTH_SHORT).show();
					Log.i(TAG, "no app key found");
					break;

				case OAUTH_WEIBO_EXCEPTION:
					Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
					Log.i(TAG, (String) msg.obj);
					break;

				case OAUTH_ERROR:
					Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
					Log.w(TAG, (String) msg.obj);

				case OAUTH_GOT_ACCESS_CODE:
					Log.v(TAG, "Access code:" + (String) msg.obj);
					break;

				case OAUTH_FAILED:
					Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
					Log.w(TAG, (String) msg.obj);
					break;

				case OAUTH_CANCELED:
					Toast.makeText(MainActivity.this, "授权已取消", Toast.LENGTH_SHORT).show();
					Log.w(TAG, "授权已取消");
					break;

				default:
					break;
				}
			};
		};

		mViewPager.setCurrentItem(TAB_WEIBO);
		mWeiboFragment = (WeiboListFragment) mSectionsPagerAdapter.getItem(TAB_WEIBO);
	}

	@Override
	protected void onStart() {
		super.onStart();

		new Authentication().login();

		if (mAccessToken != null && !mAccessToken.isExpired() && mWeiboFragment != null) {
			WeiboListAdapter adapter = (WeiboListAdapter) mWeiboFragment.getListAdapter();
			// if weibo list is empty, refresh it automatically
			if (adapter != null && adapter.getCount() == 0) {
				Log.v(TAG, "refreshing weibo list");
				adapter.refresh(mAccessToken);
			}
		} else {
			Log.v(TAG, "not refresh weibo items on startup(true if OAuth2 is not performed)");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	public class Authentication {

		public final String TAG = Authentication.class.getSimpleName();
		private final String url = "https://api.weibo.com/oauth2/access_token";

		/*
		 * perform OAuth or check for validity of access token, re-auth if
		 * necessary
		 */
		public void login() {
			AccessToken accessToken = AccessTokenKeeper.readAccessToken(MainActivity.this);

			if (accessToken == null || accessToken.isExpired()) {
				Log.v(TAG, "access_token expired");
				auth();
			} else {
				Log.v(TAG, "access_token is valid. Expire time: " + accessToken.getExpireTimeString());
				isLoggingOn.set(false);
				// store new access token in application-wide variable
				MyApplication.setAccessToken(accessToken);
			}
		}

		/**
		 * perform SSO or OAuth 2.0 authentication
		 */
		public synchronized void auth() {
			// another authentication is in process
			if (isLoggingOn.get()) {
				return;
			}
			isLoggingOn.set(true);
			final AppRegInfo appInfo = AppRegInfoHelper.getAppRegInfo();
			if (appInfo == null) {
				mHandler.sendMessage(mHandler.obtainMessage(OAUTH_APP_KEY_NOT_FOUND));
				isLoggingOn.set(false);
				return;
			}
			Log.v(TAG, appInfo.toString());

			// String SCOPE = "direct_messages_read,direct_messages_write," +
			// "statuses_to_me_read," + "follow_app_official_microblog";
			Weibo weibo = Weibo.getInstance(appInfo.getAppKey(), appInfo.getAppUrl(), null);
			weibo.anthorize(MainActivity.this, new WeiboAuthListener() {
				@Override
				public void onWeiboException(WeiboException arg0) {
					mHandler.sendMessage(mHandler.obtainMessage(OAUTH_WEIBO_EXCEPTION, "微博异常：" + arg0));
					isLoggingOn.set(false);
				}

				@Override
				public void onError(WeiboDialogError arg0) {
					String msgString = "授权出错:" + arg0.getMessage();
					mHandler.sendMessage(mHandler.obtainMessage(OAUTH_ERROR, msgString));
					isLoggingOn.set(false);
				}

				@Override
				public void onComplete(Bundle values) {
					Log.v(TAG, values.toString());
					String code = values.getString("code");
					if (code != null) {
						mHandler.sendMessage(mHandler.obtainMessage(OAUTH_GOT_ACCESS_CODE, code));
						getAccessTokenFromCode(code, appInfo);
					} else {
						mHandler.sendMessage(mHandler.obtainMessage(OAUTH_FAILED, "授权失败：无法获得Oauth code"));
						isLoggingOn.set(false);
					}
				}

				@Override
				public void onCancel() {
					mHandler.sendMessage(mHandler.obtainMessage(OAUTH_CANCELED));
					isLoggingOn.set(false);
				}
			});
		}

		/**
		 * get access token, and save it in global application space
		 * 
		 * @param context
		 * @param handler
		 * @param code
		 * @param appRegInfo
		 */
		public void getAccessTokenFromCode(final String code, final AppRegInfo appRegInfo) {
			new Thread() {
				public void run() {
					boolean succeed = true;
					mHandler.sendMessage(mHandler.obtainMessage(GET_ACCESS_TOKEN_FROM_CODE_START));
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
						Log.v(TAG, "Post url: " + httpRequest.getURI());
						HttpClient httpClient = SslClient.getSslClient(new DefaultHttpClient());
						HttpResponse httpResponse = httpClient.execute(httpRequest);
						int statusCode = httpResponse.getStatusLine().getStatusCode();
						if (statusCode == HttpStatus.SC_OK) {
							httpResponse.getEntity();
							result = EntityUtils.toString(httpResponse.getEntity());
						} else {
							mHandler.sendMessage(mHandler.obtainMessage(GET_ACCESS_TOKEN_FROM_CODE_FAILED, statusCode));
							succeed = false;
						}
					} catch (Exception e) {
						succeed = false;
						e.printStackTrace();
						mHandler.sendMessage(mHandler.obtainMessage(GET_ACCESS_TOKEN_FROM_CODE_EXCEPTION,
								e.getMessage()));
						Log.e(TAG, e.getMessage());
						return;
					}
					Log.v(TAG, result);
					try {
						AccessToken accessToken = new AccessToken();
						JSONObject jsonObject = new JSONObject(result);
						appRegInfo.setUid(jsonObject.getLong(AccessToken.UID));
						accessToken.setAccessTokenString(jsonObject.getString(AccessToken.ACCESS_TOKEN));
						accessToken.setExpireTimeFromExpiresIn(jsonObject.getLong(AccessToken.EXPIRES_IN));

						// save access token to disk (SharedPreference)
						AccessTokenKeeper.keepAccessToken(MainActivity.this, accessToken);
						MyApplication.setAccessToken(accessToken);
					} catch (JSONException e) {
						mHandler.sendMessage(mHandler.obtainMessage(GET_ACCESS_TOKEN_FROM_CODE_PARSE_JSON_EXCEPTION));
						e.printStackTrace();
						succeed = false;
					}
					if (succeed) {
						mHandler.sendMessage(mHandler.obtainMessage(GET_ACCESS_TOKEN_FROM_CODE_SUCCEED));
					}
				};
			}.start();
		}
	}

	public void reAuthentication() {
		new Authentication().auth();
	}

	private void initUI() {
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

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

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabReselected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
			}

			@Override
			public void onTabSelected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
				mViewPager.setCurrentItem(arg0.getPosition());
			}

			@Override
			public void onTabUnselected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
			}
		};

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(tabListener));
		}
		//PullToRefreshAttacher should be created in Activity:onCreate and then pulled in from Fragments as necessary.
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
	}

	public PullToRefreshAttacher getPullToRefreshAttacher() {
		return mPullToRefreshAttacher;
	}

	/**
	 * Pager Adapter providing weibo tab, comment and @ tab and me tab
	 * 
	 * @author starfish
	 * 
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			if (position == TAB_WEIBO) {
				fragment = new WeiboListFragment();
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

	/**
	 * place holder class, will be removed when develop functions for the other
	 * two tabs
	 */
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
}
