package me.aiqi.A7weibo;

import java.util.ArrayList;
import java.util.List;

import me.aiqi.A7weibo.downloader.WeiboDownloader;
import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.WeiboItem;
import me.aiqi.A7weibo.entity.WeiboUser;
import me.aiqi.A7weibo.network.ImageDownloader;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WeiboListAdapter extends BaseAdapter {
	private static final String TAG = "WeiboListAdapter";
	private Context mContext;
	private List<WeiboItem> mWeiboItems = new ArrayList<WeiboItem>();
	private WeiboDownloader mDownloader = new WeiboDownloader(this);

	public WeiboListAdapter(Context context) {
		mContext = context;
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

	private static class ViewHolder {
		public final ImageView iv_avatar;
		public final TextView tv_nickname;
		public final TextView tv_source;
		public final TextView tv_weibo_content;
		public final Button btn_comment;
		public final Button btn_forawrd;
		public final Button btn_like;

		public ViewHolder(ImageView iv_avatar, TextView tv_nickname, TextView tv_source, TextView tv_weibo_content, Button btn_comment,
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
			convertView.setTag(new ViewHolder(iv_avatar, tv_nickname, tv_source, tv_weibo_content, btn_comment, btn_forawrd, btn_like));
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
		WeiboUser user = weiboItem.getUser();
		if (user != null) {
			tv_nickname.setText(user.getScreen_name());
			ImageDownloader.Params params = new ImageDownloader.Params(user.getProfile_image_url(), iv_avatar);
			new ImageDownloader().execute(params);
		}

		tv_source.setText(Html.fromHtml("来自" + weiboItem.getSource() + " " + weiboItem.getCreated_at()));
		tv_weibo_content.setText(weiboItem.getText());
		btn_comment.setText("评论(" + weiboItem.getComments_count() + ")");
		btn_forawrd.setText("转发(" + weiboItem.getReposts_count() + ")");
		btn_like.setText("赞(" + weiboItem.getAttitudes_count() + ")");
		Log.i(TAG, "getView called");
		return convertView;
	}

	public void updateWeibolist(List<WeiboItem> weiboItems) {
		mWeiboItems = weiboItems;
		notifyDataSetChanged();
	}

	public void getWeiboItems(WeiboDownloader.Params params) {
		if (mDownloader.isRunning()) {
			Log.w(TAG, "another weibo download task running");
			return;
		}
		mDownloader.execute(params);
	}
}
