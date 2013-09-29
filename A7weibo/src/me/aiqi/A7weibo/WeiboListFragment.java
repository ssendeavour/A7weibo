package me.aiqi.A7weibo;

import me.aiqi.A7weibo.entity.AccessToken;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

public class WeiboListFragment extends ListFragment {
	private static final String TAG = "WeiboViewFragment";
	private WeiboListAdapter mWeiboListdapter;

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
		// set footer and header before set Adapter
		View footer = LayoutInflater.from(getActivity()).inflate(R.layout.frag_weibo_list_footer, null);
		footer.findViewById(R.id.btn_load_more).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadMoreWeibo();
			}
		});

		getListView().addFooterView(footer);
		getListView().setFooterDividersEnabled(true);

		getListView().setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount >= totalItemCount - 1) {
					loadMoreWeibo();
				}
				Log.v(TAG, "firstVisibleItem:" + firstVisibleItem + ", visibleItemCount:" + visibleItemCount
						+ ", totalItemCount:" + totalItemCount);
			}
		});

		mWeiboListdapter = new WeiboListAdapter(getActivity());
		setListAdapter(mWeiboListdapter);
		refreshWeiboList();
		getListView().setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
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
		}
	}
}
