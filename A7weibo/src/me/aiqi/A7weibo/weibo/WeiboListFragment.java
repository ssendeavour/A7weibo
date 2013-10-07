package me.aiqi.A7weibo.weibo;

import me.aiqi.A7weibo.MainActivity;
import me.aiqi.A7weibo.MyApplication;
import me.aiqi.A7weibo.R;
import me.aiqi.A7weibo.R.id;
import me.aiqi.A7weibo.R.layout;
import me.aiqi.A7weibo.R.menu;
import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.WeiboItem;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

public class WeiboListFragment extends ListFragment implements PullToRefreshAttacher.OnRefreshListener {

	public static final int VIEW_WEIBO_DETAIL = 0x100;
	public static final int COMMENT_WEIBO = 0x101;
	public static final int REPOST_WEIBO = 0x102;
	//	public static final int VIEW_WEIBO = 0x103;
	//	public static final int VIEW_WEIBO = 0x104;
	//	public static final int VIEW_WEIBO = 0x105;

	private static final String TAG = WeiboListFragment.class.getSimpleName();
	private WeiboListAdapter mWeiboListdapter;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private MyHandler mHandler;

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
		mPullToRefreshAttacher.addRefreshableView(getView(), this);

		mHandler = new MyHandler();
		mWeiboListdapter = new WeiboListAdapter(getActivity(), mHandler);
		setListAdapter(mWeiboListdapter);
		refreshWeiboList();

		// load more automatically when scroll to the end of list
		getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
			private int currentFirstVisibleItem = 0;
			private int currentVisibleItemCount = 0;
			private int currentTotalItemCount = 0;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_IDLE
						&& currentFirstVisibleItem + currentVisibleItemCount >= currentTotalItemCount - 2) {
					loadMoreWeibo();
					Log.v(TAG, "firstVisibleItem:" + currentFirstVisibleItem + ", visibleItemCount:"
							+ currentVisibleItemCount + ", totalItemCount:" + currentTotalItemCount);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				currentFirstVisibleItem = firstVisibleItem;
				currentVisibleItemCount = visibleItemCount;
				currentTotalItemCount = totalItemCount;
			}
		});

		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.v(TAG, ((WeiboItem) getListView().getItemAtPosition(position)).getUser().getName());
				Log.v(TAG, "list item clicked");
			}
		});
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int position;
			WeiboItem weiboItem;
			WeiboItem item;
			Intent intent;

			switch (msg.what) {
			case VIEW_WEIBO_DETAIL:

				break;

			case COMMENT_WEIBO:
				position = (Integer) msg.obj;
				weiboItem = (WeiboItem) getListAdapter().getItem(position);
				intent = new Intent(getActivity(), WeiboAddCommentActivity.class);
				intent.putExtra(WeiboAddCommentActivity.WEIBO_ID, weiboItem.getId());
				item = weiboItem.getRetweeted_status();
				if (item != null) {
					intent.putExtra(WeiboAddCommentActivity.ORIGINAL_WEIBO_ID, item.getId());
				}
				startActivityForResult(intent, COMMENT_WEIBO);
				break;

			case REPOST_WEIBO:
				position = (Integer) msg.obj;
				weiboItem = (WeiboItem) getListAdapter().getItem(position);
				intent = new Intent(getActivity(), WeiboRepostActivity.class);

				item = weiboItem.getRetweeted_status();
				if (item != null) {
					// id of original weibo
					intent.putExtra(WeiboRepostActivity.WEIBO_ID, item.getId());
					intent.putExtra(WeiboRepostActivity.USER_NAME, item.getUser().getName());
					intent.putExtra(WeiboRepostActivity.WEIBO_CONTENT, item.getText());
					intent.putExtra(WeiboRepostActivity.OTHERS_COMMENT, weiboItem.getText());
					intent.putExtra(WeiboRepostActivity.COMMENTERS_NAME, weiboItem.getUser().getName());
				} else {
					intent.putExtra(WeiboRepostActivity.WEIBO_ID, weiboItem.getId());
					intent.putExtra(WeiboRepostActivity.USER_NAME, weiboItem.getUser().getName());
					intent.putExtra(WeiboRepostActivity.WEIBO_CONTENT, weiboItem.getText());
				}

				startActivityForResult(intent, COMMENT_WEIBO);
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case COMMENT_WEIBO:
			if (resultCode == Activity.RESULT_OK) {

			}
			break;

		default:
			break;
		}
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

		case R.id.action_new_weibo:
			Log.v(TAG, "new text weibo");
			Intent intent = new Intent(getActivity(), WeiboNewActivity.class);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public void refreshWeiboList() {
		final AccessToken accessToken = MyApplication.getAccessToken();
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
		final AccessToken accessToken = MyApplication.getAccessToken();
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
		Log.v(TAG, "PullToRefresh: refresh starting");
		refreshWeiboList();
	}
}
