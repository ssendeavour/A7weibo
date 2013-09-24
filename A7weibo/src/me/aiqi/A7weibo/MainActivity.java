package me.aiqi.A7weibo;

import java.util.Locale;

import me.aiqi.A7weibo.auth.Authentication;
import me.aiqi.A7weibo.downloader.WeiboDownloader;
import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.AppRegInfo;
import me.aiqi.A7weibo.util.AppRegInfoHelper;
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

import com.weibo.sdk.android.sso.SsoHandler;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

	public static final String TAG = "MainActivity";

	public static final int TAB_ITEM_NUMBER = 3;
	public static final int TAB_COMMENT_AT = 1;
	public static final int TAB_WEIBO = 0;
	public static final int TAB_ME = 2;

	private AppRegInfo mAppRegInfo = AppRegInfoHelper.getAppRegInfo();
	private SsoHandler mSsoHandler;
	private AccessToken mAccessToken;

	public Handler mHandler;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private WeiboListFragment mWeiboFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUI();

		// Access Token, Re-authentication if necessary
		Authentication.login(this, mHandler);

		mViewPager.setCurrentItem(TAB_WEIBO);
		Log.i(TAG, "OAuth finished");
		mWeiboFragment = (WeiboListFragment) mSectionsPagerAdapter.getItem(TAB_WEIBO);

		mHandler = new MyHandler();
	}

	public class MyHandler extends Handler {
		public static final int BEGIN_GET_ACCESS_TOKEN_FROM_CODE = 0x100;
		public static final int FINISH_GET_ACCESS_TOKEN_FAILED = 0x101;
		public static final int FINISH_GET_ACCESS_TOKEN_SUCCEEDED = 0x102;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BEGIN_GET_ACCESS_TOKEN_FROM_CODE:
				break;

			case FINISH_GET_ACCESS_TOKEN_SUCCEEDED:
				Toast.makeText(MainActivity.this, "授权成功!", Toast.LENGTH_SHORT).show();
				mAccessToken = ((MyApplication) getApplicationContext()).getAccessToken();

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
		} else {
			Authentication.login(this, mHandler);
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
	}

	@Override
	public void onTabUnselected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
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
}
