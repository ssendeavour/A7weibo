package me.aiqi.A7weibo;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import me.aiqi.A7weibo.downloader.WeiboDownloader;
import me.aiqi.A7weibo.entity.AccessToken;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;

public class WeiboListFragment extends ListFragment {
	private static final String TAG = "WeiboViewFragment";
	private WeiboListAdapter mWeiboListdapter;

	// empty constructor is required as per Fragment docs
	public WeiboListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_weibo_list, container, false);
		return view;
	}

	/**
	 * automatic load new weibo items if access token is valid and no weibo item
	 * yet
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mWeiboListdapter = new WeiboListAdapter(getActivity());
		setListAdapter(mWeiboListdapter);
		refreshWeiboList();
		getListView().setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
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
