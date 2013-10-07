package me.aiqi.A7weibo.downloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.aiqi.A7weibo.MyApplication;
import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.Consts;
import me.aiqi.A7weibo.entity.WeiboError;
import me.aiqi.A7weibo.entity.WeiboItem;
import me.aiqi.A7weibo.network.SslClient;
import me.aiqi.A7weibo.weibo.WeiboListAdapter;
import me.aiqi.A7weibo.weibo.WeiboListCallback;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class WeiboDownloader extends AsyncTask<WeiboDownloader.Params, Void, ArrayList<WeiboItem>> {

	public static final String TAG = WeiboDownloader.class.getSimpleName();
	private WeiboListAdapter mAdapter;
	private Context mContext;
	private int mRefreshMode;

	public WeiboDownloader(WeiboListAdapter adapter, Context context) {
		if (!(context instanceof WeiboListCallback)) {
			Log.e(TAG, "context must implements WeiboListCallback interface");
		}
		mAdapter = adapter;
		mContext = context;
		// in fact, refresh mode is set through download params every time call execute() 
		mRefreshMode = WeiboListAdapter.UPDATE_MODE_LOAD_MORE;
	}

	public static class Params {
		public static final String ACCESS_TOKEN = "access_token"; // OAuth2.0方式授权的必选，其余为选填
		public static final String SINCE_ID = "since_id"; // long,返回ID比since_id大的微博，默认为0。
		public static final String MAX_ID = "max_id"; // long，回ID小于或等于max_id的微博，默认为0。
		public static final String COUNT = "count"; // 单页返回的记录条数，最大不超过100，默认为20。
		public static final String PAGE = "page"; // 返回结果的页码，默认为1。
		public static final String BASE_APP = "base_app"; // 是否只获取当前应用的数据。0为否（所有数据），1为是（仅当前应用），默认为0。
		public static final String FEATURE = "feature"; // 过滤类型ID，0：全部、1：原创、2：图片、3：视频、4：音乐，默认为0。
		public static final String TRIM_USER = "trim_user"; // int,
															// 返回值中user字段开关，0：返回完整user字段、1：user字段仅返回user_id，默认为0。
		/** uid 和 screen_name 只能选一个 */
		public static final String UID = "uid"; // 	需要查询的用户ID。
		public static final String SCREEN_NAME = "screen_name"; //需要查询的用户昵称。

		/** 这个参数是程序定义的，Weibo API没有，参数值在{@link WeiboListAdapter}中定义 */
		public static final String REFRESH_MODE = "refresh_mode";

		public static final int FEATURE_ALL = 0; // 全部
		public static final int FEATURE_ORIGNAL = 1; // 原创
		public static final int FEATURE_PICTURE = 2; // 图片
		public static final int FEATURE_VIDEO = 3; // 视频
		public static final int FEATURE_MUSIC = 4; // 音乐

		public static final int TRIM_USER_YES = 1; // 返回结果中user字段仅返回user_id
		public static final int TRIM_USER_NO = 0; // 返回完整user字段
		public static final int BASE_APP_YES = 1; // 仅返回当前应用的数据
		public static final int BASE_APP_NO = 0; // 获得所有数据

		private Map<String, String> mMap;

		public Params() {
			mMap = new HashMap<String, String>();
		}

		public Params(HashMap<String, String> map) {
			mMap = map;
		}

		/**
		 * return complete encoded query url
		 * 
		 * @return
		 */
		public String buildURL() {
			Uri.Builder builder;
			if (mMap.containsKey(UID) && mMap.get(UID).length() > 0 || mMap.containsKey(SCREEN_NAME)
					&& mMap.get(SCREEN_NAME).length() > 0) {
				builder = Uri.parse(Consts.ApiUrl.USER_TIMELINE).buildUpon();
			} else {
				builder = Uri.parse(Consts.ApiUrl.FRIENDS_TIMELINE).buildUpon();
			}

			for (Map.Entry<String, String> pair : mMap.entrySet()) {
				builder.appendQueryParameter(pair.getKey(), pair.getValue());
			}
			return builder.build().toString();
		}

		/**
		 * @return the value of any previous mapping with the specified key or
		 *         null if there was no mapping.
		 */
		public String put(String key, String value) {
			return mMap.put(key, value);
		}

		/**
		 * @param value
		 *            : long value will be converted to its string
		 *            representation
		 * @return the value of any previous mapping with the specified key or
		 *         null if there was no mapping.
		 */
		public String put(String key, long value) {
			return mMap.put(key, String.valueOf(value));
		}

		public String get(String key) {
			return mMap.get(key);
		}

		public Map<String, String> getAll() {
			return mMap;
		}

		public void clear() {
			mMap.clear();
		}

		public String remove(String key) {
			return mMap.remove(key);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected ArrayList<WeiboItem> doInBackground(Params... params) {
		AccessToken accessToken = MyApplication.getAccessToken();
		if (accessToken == null || accessToken.isExpired()) {
			return null;
		}
		if (params[0].get(Params.REFRESH_MODE) == null) {
			throw new IllegalArgumentException("miss parameter: refresh_mode");
		} else {
			mRefreshMode = Integer.parseInt(params[0].remove(Params.REFRESH_MODE));
		}
		try {
			String url = params[0].buildURL();
			HttpGet httpGet = new HttpGet(url);
			Log.v(TAG, "Request weibo:" + url);
			HttpResponse response = SslClient.getSslClient(new DefaultHttpClient()).execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				String json = EntityUtils.toString(response.getEntity());
				ArrayList<WeiboItem> weiboItems = WeiboItem.parseJson(json);
				if (weiboItems == null) {
					// try parse json as an error 
					WeiboError error = WeiboError.parseError(json);
					if (error != null && error.getError_code() != 0) {
						final String errorString = "Error code:" + error.getError_code() + ", " + error.getError();
						Toast.makeText(mContext, errorString, Toast.LENGTH_SHORT).show();
						Log.v(TAG, error.toString());
					} else {
						Toast.makeText(mContext, "未知错误", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "未知错误");
						Log.d(TAG, json);
					}
					return null;
				} else {
					return weiboItems;
				}
			} else {
				Log.i(TAG, "Failed download weibo items:" + url + ", status code: " + statusCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * notify view to update UI
	 */
	@Override
	protected void onPostExecute(ArrayList<WeiboItem> result) {
		super.onPostExecute(result);
		if (result != null) {
			Log.v(TAG, new StringBuilder("got ").append(result.size()).append(" new weibo, update adapter now")
					.toString());
			mAdapter.updateWeibolist(result, mRefreshMode);
		} else {
			Log.w(TAG, "got nothing");
		}
		// Notify PullTORefreshattacher refresh has completed
		((WeiboListCallback) mContext).getPullToRefreshAttacher().setRefreshComplete();
	}

}
