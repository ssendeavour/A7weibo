package me.aiqi.A7weibo.downloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.aiqi.A7weibo.MyApplication;
import me.aiqi.A7weibo.WeiboListAdapter;
import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.WeiboItem;
import me.aiqi.A7weibo.entity.WeiboUser;
import me.aiqi.A7weibo.entity.WeiboVisiblity;
import me.aiqi.A7weibo.network.SslClient;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public class WeiboDownloader extends AsyncTask<WeiboDownloader.Params, Void, ArrayList<WeiboItem>> {

	private static final String TAG = "WeiboDownloader";
	private WeiboListAdapter mAdapter;
	private Context mContext;
	private int mRefreshMode;

	public WeiboDownloader(WeiboListAdapter adapter, Context context) {
		mAdapter = adapter;
		mContext = context;
		// in fact, refresh mode is set through download params every time call execute() 
		mRefreshMode = WeiboListAdapter.UPDATE_MODE_LOAD_MORE;
	}

	public static class Params {
		public static final String mUrl = "https://api.weibo.com/2/statuses/friends_timeline.json";

		public static final String ACCESS_TOKEN = "access_token"; // OAuth2.0方式授权的必选，其余为选填
		public static final String SINCE_ID = "since_id"; // long,返回ID比since_id大的微博，默认为0。
		public static final String MAX_ID = "max_id"; // long，回ID小于或等于max_id的微博，默认为0。
		public static final String COUNT = "count"; // 单页返回的记录条数，最大不超过100，默认为20。
		public static final String PAGE = "page"; // 返回结果的页码，默认为1。
		public static final String BASE_APP = "base_app"; // 是否只获取当前应用的数据。0为否（所有数据），1为是（仅当前应用），默认为0。
		public static final String FEATURE = "feature"; // 过滤类型ID，0：全部、1：原创、2：图片、3：视频、4：音乐，默认为0。
		public static final String TRIM_USER = "trim_user"; // int,
															// 返回值中user字段开关，0：返回完整user字段、1：user字段仅返回user_id，默认为0。

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
			Uri.Builder builder = Uri.parse(mUrl).buildUpon();
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
		AccessToken accessToken = ((MyApplication) mContext.getApplicationContext()).getAccessToken();
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
				return parseJson(EntityUtils.toString(response.getEntity()));
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
			Log.d(TAG, new StringBuilder("got ").append(result.size()).append(" new weibo, update adapter now")
					.toString());
			mAdapter.updateWeibolist(result, mRefreshMode);
		} else {
			Log.d(TAG, "got nothing");
		}
	}

	/**
	 * parse json string and encapsulate Weibo items into ArrayList<WeiboItem>
	 * 
	 * @param json
	 * @return ArrayList<WeiboItem>
	 */
	protected ArrayList<WeiboItem> parseJson(String json) {
		if (TextUtils.isEmpty(json)) {
			Log.d(TAG, "json is empty or null");
			return null;
		}
		Log.v(TAG, "json: length: " + json.length());
		ArrayList<WeiboItem> list = new ArrayList<WeiboItem>();
		try {
			JSONObject object = new JSONObject(json);
			JSONArray array = null;
			if (object != null) {
				array = object.optJSONArray("statuses");
			}
			if (array == null) {
				Log.d(TAG, "Error parsing weiboitem json");
				return null;
			}
			Log.v(TAG, "begin build ArrayList");
			for (int i = 0; i < array.length(); i++) {
				object = (JSONObject) array.get(i);
				WeiboItem weiboItem = new WeiboItem();
				weiboItem.setAttitudes_count(object.optInt("attitudes_count")); // fallback:0
				weiboItem.setComments_count(object.optInt("comments_count")); // fallback:0
				weiboItem.setCreated_at(object.optString("created_at")); // fallback:""
				weiboItem.setFavorited(object.optBoolean("favorited")); // fallback:false
				// weiboItem.setGeo(); // fallback:null TODO: 暂时不实现
				weiboItem.setId(object.optLong("id")); // fallback:0
				weiboItem.setIdstr(object.optString("idstr")); // fallback:""
				weiboItem.setOriginal_pic(object.optString("original_pic")); // fallback:""
				JSONArray pic_urlsArray = object.optJSONArray("pic_urls"); // fallback:null
				if (pic_urlsArray != null) {
					ArrayList<String> pic_urls = new ArrayList<String>();
					for (int j = 0; j < pic_urlsArray.length(); j++) {
						pic_urls.add(array.getString(j));
					}
					weiboItem.setPic_urls(pic_urls);
				}
				weiboItem.setReposts_count(object.optInt("reposts_count"));
				// weiboItem.setRetweeted_status(retweeted_status)
				// TODO: 暂时不实现
				weiboItem.setSource(object.optString("source"));
				weiboItem.setText(object.optString("text"));
				weiboItem.setThumbnail_pic(object.optString("thumbnail_pic"));
				weiboItem.setTruncated(object.optBoolean("truncated"));
				weiboItem.setUser(WeiboUser.parseUserFromJsonObject(object.optJSONObject("user")));
				WeiboVisiblity visiblity = new WeiboVisiblity(object.optInt("visible"));
				if (visiblity.getType() == WeiboVisiblity.SELECTED_GROUP) {
					visiblity.setList_id(object.optInt("list_id"));
				}
				weiboItem.setVisible(visiblity);
				//				Log.v(TAG, weiboItem.toString());
				list.add(weiboItem);
			}
		} catch (Exception e) {
			Log.w(TAG, "parsing weiboitem json error");
			e.printStackTrace();
		}
		return list;
	}
}
