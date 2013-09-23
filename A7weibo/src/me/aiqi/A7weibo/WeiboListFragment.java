package me.aiqi.A7weibo;

import me.aiqi.A7weibo.downloader.WeiboDownloader;
import me.aiqi.A7weibo.entity.AccessToken;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WeiboListFragment extends ListFragment {
	private static final String TAG = "WeiboViewFragment";
	private WeiboListAdapter mWeiboListdapter;

	// empty constructor is required as per Fragment docs
	public WeiboListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mWeiboListdapter = new WeiboListAdapter(getActivity().getApplicationContext());
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_weibo_list, container, false);
		setListAdapter(mWeiboListdapter);
		return view;
	}

	/**
	 * automatic load new weibo items if access token is valid and no weibo item
	 * yet
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		AccessToken accessToken = ((GlobalVariable) getActivity().getApplicationContext()).getAccessToken();

		if (!accessToken.isExpired() && mWeiboListdapter.getCount() == 0) {
			Log.v(TAG, "WeiboListAdapter is empty, try load new weibo items");
			WeiboDownloader.Params params = new WeiboDownloader.Params();
			params.put(WeiboDownloader.Params.ACCESS_TOKEN, accessToken.getAccessToken());
			mWeiboListdapter.getWeiboItems(params);
		} else {
			Log.v(TAG, "WeiboListAdapter items count: " + mWeiboListdapter.getCount());
		}
	}
}
