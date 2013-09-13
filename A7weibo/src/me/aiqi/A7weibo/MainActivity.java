package me.aiqi.A7weibo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.aiqi.A7weibo.authkeep.AccessTokenKeeper;
import me.aiqi.A7weibo.entity.AppRegInfo;
import me.aiqi.A7weibo.util.AppRegInfoHelper;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

	protected static final String TAG = "MainActivity";

	SectionsPagerAdapter mSectionsPagerAdapter;
	Oauth2AccessToken accessToken;

	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUI();

		accessToken = AccessTokenKeeper.readAccessToken(this);
		String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US).format(new Date(accessToken.getExpiresTime()));
		if (accessToken == null || isAuthExpired()) {
			Log.i(TAG, "Authentication expired, reauth, expire time:" + date);
			auth();
		} else {
			Log.i(TAG, "Authentication is valid. Expire time: " + date);
		}
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

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
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

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
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
			View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	@Override
	public void onTabReselected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabUnselected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	public Boolean isAuthExpired() {
		if (accessToken != null) {
			return !accessToken.isSessionValid();
		}
		return true;
	}

	/**
	 * perform authentication
	 */
	public void auth() {
		if (!isAuthExpired()) {
			String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US).format(new Date(accessToken.getExpiresTime()));
			String msg = "access_token 仍在有效期内,无需再次登录: \naccess_token:" + accessToken.getToken() + "\n有效期：" + date;
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
		Weibo weibo = Weibo.getInstance(appInfo.getAppKey(), appInfo.getAppUrl(), null);
		new SsoHandler(this, weibo).authorize(new WeiboAuthListener() {

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
				}
				String token = values.getString("access_token");
				String expires_in = values.getString("expires_in");
				if (TextUtils.isEmpty(token) || TextUtils.isEmpty(expires_in)) {
					String msg = "授权失败：token or expires_in empty!";
					Log.e(TAG, msg);
					Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
					if (token != null) {
						token = "";
					}
					Log.i(TAG, "token = " + token);

					if (expires_in != null) {
						expires_in = "";
					}
					Log.i(TAG, "expires_in = " + expires_in);
					return;
				}
				accessToken = new Oauth2AccessToken(token, expires_in);
				if (accessToken.isSessionValid()) {
					String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US).format(new Date(accessToken.getExpiresTime()));
					String msg = "认证成功: access_token: " + token + ", " + "expires_in: " + expires_in + "有效期：" + date;
					Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
					Log.i(TAG, msg);
					if (AccessTokenKeeper.keepAccessToken(MainActivity.this, accessToken)) {
						Log.i(TAG, "new access token saved to pref");
					} else {
						Log.i(TAG, "Fail to save new access token to pref");
					}
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
}
