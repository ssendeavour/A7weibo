package me.aiqi.A7weibo;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import me.aiqi.A7weibo.downloader.WeiboDownloader;
import me.aiqi.A7weibo.entity.AccessToken;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
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

		mWeiboListdapter.refresh(MyApplication.getContext().getAccessToken());
		getListView().setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));

		new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.w(TAG, "Loading more");
				mWeiboListdapter.loadMore(MyApplication.getContext().getAccessToken());
			}
		};
	}

}
