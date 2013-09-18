package me.aiqi.A7weibo;

import me.aiqi.A7weibo.downloader.WeiboDownloader;
import me.aiqi.A7weibo.entity.AccessToken;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class WeiboViewFragment extends ListFragment {
	private static final String TAG = "WeiboViewFragment";
	private WeiboListAdapter adapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new WeiboListAdapter(getActivity().getApplicationContext());
		Log.i(TAG, "WeiboListAdapter created in WeiboViewFragment");
		setListAdapter(adapter);
		Log.i(TAG, "WeiboListAdapter set in WeiboViewFragment");
		
		AccessToken accessToken = ((GlobalVariable) getActivity().getApplicationContext()).getAccessToken();
		if (!accessToken.isExpired() && adapter.getCount() == 0) {
			WeiboDownloader.Params params = new WeiboDownloader.Params();
			params.put(WeiboDownloader.Params.ACCESS_TOKEN, accessToken.getAccessToken());
			adapter.getWeiboItems(params);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_weibo_list, container, false);

		Log.i(TAG, "onCreateView");
		return view;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}

}
