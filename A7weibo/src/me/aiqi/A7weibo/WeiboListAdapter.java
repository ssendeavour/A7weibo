package me.aiqi.A7weibo;

import java.util.ArrayList;
import java.util.List;

import me.aiqi.A7weibo.downloader.WeiboDownloader;
import me.aiqi.A7weibo.downloader.WeiboDownloader.Params;
import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.WeiboItem;
import me.aiqi.A7weibo.entity.WeiboUser;
import me.aiqi.A7weibo.network.NetworkCondition;
import me.aiqi.A7weibo.util.WbUtil;
import android.content.Context;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class WeiboListAdapter extends BaseAdapter {
	public static final String TAG = "WeiboListAdapter";

	/** append new weibo items to the end of the inner list */
	public static final int UPDATE_MODE_LOAD_MORE = 0;
	/** insert new weibo items to the head of the inner List */
	public static final int UPDATE_MODE_REFRESH = 1;

	private Context mContext;
	private List<WeiboItem> mWeiboItems;
	private WeiboDownloader mDownloader;
	private AsyncTask<Params, Void, ArrayList<WeiboItem>> mTask;

	public WeiboListAdapter(Context context) {
		mContext = context;
		mWeiboItems = readWeiboItemsFromCache();
		mDownloader = new WeiboDownloader(this, context);
		mTask = null;
	}

	/**
	 * TODO: currently return new empty ArrayList, may read from SQlite in the
	 * future
	 * 
	 * @return
	 */
	private ArrayList<WeiboItem> readWeiboItemsFromCache() {
		return new ArrayList<WeiboItem>();
	}

	@Override
	public int getCount() {
		return mWeiboItems.size();
	}

	@Override
	public WeiboItem getItem(int position) {
		return mWeiboItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mWeiboItems.get(position).getId();
	}

	/**
	 * reuse widgets to reduce call to View.findViewById(), speed up ListView
	 * 
	 * @author starfish
	 * 
	 */
	private static class ViewHolder {
		public final ImageView iv_avatar;
		public final TextView tv_nickname;
		public final TextView tv_source;
		public final TextView tv_weibo_content;
		public final Button btn_comment;
		public final Button btn_forawrd;
		public final Button btn_like;

		public ViewHolder(ImageView iv_avatar, TextView tv_nickname, TextView tv_source, TextView tv_weibo_content,
				Button btn_comment,
				Button btn_forawrd, Button btn_like) {
			super();
			this.iv_avatar = iv_avatar;
			this.tv_nickname = tv_nickname;
			this.tv_source = tv_source;
			this.tv_weibo_content = tv_weibo_content;
			this.btn_comment = btn_comment;
			this.btn_forawrd = btn_forawrd;
			this.btn_like = btn_like;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv_avatar;
		TextView tv_nickname;
		TextView tv_source;
		TextView tv_weibo_content;
		Button btn_comment;
		Button btn_forawrd;
		Button btn_like;

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.frag_weibo_list_item, parent, false);
			iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
			tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
			tv_source = (TextView) convertView.findViewById(R.id.tv_source);
			tv_weibo_content = (TextView) convertView.findViewById(R.id.tv_weibo_content);
			btn_comment = (Button) convertView.findViewById(R.id.btn_comment);
			btn_forawrd = (Button) convertView.findViewById(R.id.btn_forawrd);
			btn_like = (Button) convertView.findViewById(R.id.btn_like);
			convertView.setTag(new ViewHolder(iv_avatar, tv_nickname, tv_source, tv_weibo_content, btn_comment,
					btn_forawrd, btn_like));
		} else {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			iv_avatar = viewHolder.iv_avatar;
			tv_nickname = viewHolder.tv_nickname;
			tv_source = viewHolder.tv_source;
			tv_weibo_content = viewHolder.tv_weibo_content;
			btn_comment = viewHolder.btn_comment;
			btn_forawrd = viewHolder.btn_forawrd;
			btn_like = viewHolder.btn_like;
		}

		WeiboItem weiboItem = getItem(position);
		if (weiboItem == null) {
			// make weibo content green to indicate no weibo info
			tv_weibo_content.setText(Html.fromHtml("<font color='#00FF00'> 好像出错了=_=<br />没有微博信息</font>"));
			return convertView;
		}
		WeiboUser user = weiboItem.getUser();
		if (user != null) {
			tv_nickname.setText(user.getScreen_name());
			// start async task to set user's avatar
			// new ImageDownloader().download(user.getProfile_image_url(),
			// iv_avatar);
			ImageLoader.getInstance().displayImage(user.getProfile_image_url(), iv_avatar);

		} else {
			// make username to green to indicate no user info found
			tv_nickname.setText(Html.fromHtml("<font color='#00FF00'>好像出错了=_=<br />没有微博信息</font>"));
			tv_weibo_content.setText("");
			return convertView;
		}

		String createTimeString = WbUtil.getTimeString(weiboItem.getCreated_at());
		if (createTimeString == null) {
			createTimeString = weiboItem.getCreated_at();
		}
		Log.v(TAG, createTimeString);

		String sourceAndTimeHtmlString = new StringBuilder()
				.append("<font color='#FFCC00'>")
				.append(createTimeString)
				.append("</font> 来自")
				.append(weiboItem.getSource()).toString();
		tv_source.setText(Html.fromHtml(sourceAndTimeHtmlString));
		tv_weibo_content.setText(weiboItem.getText());
		btn_comment.setText(weiboItem.getComments_count() == 0 ? "评论" : String.valueOf(weiboItem.getComments_count()));
		btn_forawrd.setText(weiboItem.getReposts_count() == 0 ? "转发" : String.valueOf(weiboItem.getReposts_count()));
		btn_like.setText(weiboItem.getAttitudes_count() == 0 ? "赞" : String.valueOf(weiboItem.getAttitudes_count()));
		return convertView;
	}

	public void updateWeibolist(List<WeiboItem> weiboItems, int mode) {
		// It's fairly rare that weiboItem == mWeiboItem, so we don't check, it don't worth
		switch (mode) {
		case UPDATE_MODE_REFRESH:
			mWeiboItems.addAll(0, weiboItems);
			break;

		case UPDATE_MODE_LOAD_MORE:
			mWeiboItems.addAll(weiboItems);
			break;

		default:
			break;
		}
		Log.d(TAG, "data set changed, refresh UI");
		notifyDataSetChanged();
	}

	/** It's the caller's duty to check the validity of AccessToken */
	public void loadMore(AccessToken accessToken) {
		downloadWeiboItems(accessToken, UPDATE_MODE_LOAD_MORE);
	}

	/** It's the caller's duty to check the validity of AccessToken */
	public void refresh(AccessToken accessToken) {
		downloadWeiboItems(accessToken, UPDATE_MODE_REFRESH);
	}

	/** It's the caller's duty to check the validity of AccessToken */
	public void downloadWeiboItems(AccessToken accessToken, int refreshMode) {
		if (mTask != null) {
			Status status = mTask.getStatus();
			// another download task running or pending
			if (status == Status.RUNNING || status == Status.PENDING) {
				return;
			}
		}
		if (checkDownloadCondition(accessToken)) {
			WeiboDownloader.Params params = new WeiboDownloader.Params();
			params.put(WeiboDownloader.Params.ACCESS_TOKEN, accessToken.getAccessTokenString());
			params.put(WeiboDownloader.Params.REFRESH_MODE, refreshMode);
			switch (refreshMode) {
			case UPDATE_MODE_LOAD_MORE:
				params.put(WeiboDownloader.Params.MAX_ID, getMaxId());
				break;
			case UPDATE_MODE_REFRESH:
				params.put(WeiboDownloader.Params.SINCE_ID, getSinceId());
			}
			mTask = mDownloader.execute(params);
		} else {
			// TODO: notify user of network problem;
		}
	}

	/**
	 * check for network conditions, etc. but do not check for AccessToken
	 * validity, the caller should check it in Activity or Fragment
	 * 
	 * @return true if can perform download task (currently means network is
	 *         connected, {@code false} otherwise
	 */
	private boolean checkDownloadCondition(AccessToken accessToken) {
		return NetworkCondition.isOnline();
	}

	/** @return id of most recent weibo item, 0 otherwise */
	private long getSinceId() {
		long sinceID = 0;
		if (mWeiboItems != null && mWeiboItems.size() > 0) {
			sinceID = mWeiboItems.get(0).getId();
		}
		return sinceID;
	}

	/**
	 * used in load more method to return weibos whose id <= MaxId
	 * 
	 * @return id of weibo item in the end of the WeiboList minus one or 0
	 *         otherwise
	 */
	private long getMaxId() {
		long id = 0;
		if (mWeiboItems != null && mWeiboItems.size() > 0) {
			id = mWeiboItems.get(mWeiboItems.size() - 1).getId() - 1;
		}
		return id;
	}
}
