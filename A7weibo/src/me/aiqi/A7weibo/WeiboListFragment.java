package me.aiqi.A7weibo;

import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.Consts;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

public class WeiboListFragment extends ListFragment implements PullToRefreshAttacher.OnRefreshListener {
	private static final String TAG = WeiboListFragment.class.getSimpleName();
	private WeiboListAdapter mWeiboListdapter;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	// empty constructor is required as per Fragment docs
	public WeiboListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_content, container, false);
		return view;
	}

	/**
	 * automatic load new weibo items if access token is valid and no weibo item
	 * yet
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (!(getActivity() instanceof WeiboListCallback)) {
			Log.e(TAG, "Activity must implements WeiboListCallback interface");
		}

		mPullToRefreshAttacher = ((WeiboListCallback) getActivity()).getPullToRefreshAttacher();
		mPullToRefreshAttacher.addRefreshableView(getListView(), this);

		mWeiboListdapter = new WeiboListAdapter(getActivity());
		setListAdapter(mWeiboListdapter);
		refreshWeiboList();

		class MyOnScrollListener extends PauseOnScrollListener {
			private int currentFirstVisibleItem = 0;
			private int currentVisibleItemCount = 0;
			private int currentTotalItemCount = 0;

			public MyOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
				super(imageLoader, pauseOnScroll, pauseOnFling);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				currentFirstVisibleItem = firstVisibleItem;
				currentVisibleItemCount = visibleItemCount;
				currentTotalItemCount = totalItemCount;

				super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}

			/**
			 * automatically load more Weibo when scroll near the end of the
			 * list
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 
				if (scrollState == SCROLL_STATE_IDLE
						&& currentFirstVisibleItem + currentVisibleItemCount >= currentTotalItemCount - 2) {
					loadMoreWeibo();
					Log.v(TAG, "firstVisibleItem:" + currentFirstVisibleItem + ", visibleItemCount:"
							+ currentVisibleItemCount + ", totalItemCount:" + currentTotalItemCount);
				}
				super.onScrollStateChanged(view, scrollState);
			}
		}
		getListView().setOnScrollListener(
				new MyOnScrollListener(ImageLoader.getInstance(), Consts.ImageLoader.PAUSE_ON_SCROLL,
						Consts.ImageLoader.PAUSE_ON_FLING));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main_activity_actions, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			Log.v(TAG, "Refresh menu clicked");
			refreshWeiboList();
			return true;

		case R.id.action_load_more:
			Log.v(TAG, "Load more menu clicked");
			loadMoreWeibo();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public void refreshWeiboList() {
		final AccessToken accessToken = MyApplication.getContext().getAccessToken();
		if (accessToken != null && !accessToken.isExpired()) {
			mWeiboListdapter = (WeiboListAdapter) getListAdapter();
			if (mWeiboListdapter != null) {
				mWeiboListdapter.refresh(accessToken);
			} else {
				Log.v(TAG, "mWeiboListdapter is null");
			}
		} else {
			Log.v(TAG, "access token expired? :" + accessToken.isExpired());
			((MainActivity) getActivity()).reAuthentication();
		}
	}

	public void loadMoreWeibo() {
		final AccessToken accessToken = MyApplication.getContext().getAccessToken();
		if (accessToken != null && !accessToken.isExpired()) {
			mWeiboListdapter = (WeiboListAdapter) getListAdapter();
			if (mWeiboListdapter != null) {
				mWeiboListdapter.loadMore(accessToken);
			} else {
				Log.v(TAG, "mWeiboListdapter is null");
			}
		} else {
			Log.v(TAG, "access token expired? :" + accessToken.isExpired());
			((MainActivity) getActivity()).reAuthentication();
		}
	}

	@Override
	public void onRefreshStarted(View view) {
		refreshWeiboList();
	}
}
