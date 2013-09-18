package me.aiqi.A7weibo.downloader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.X509TrustManager;

import me.aiqi.A7weibo.WeiboListAdapter;
import me.aiqi.A7weibo.entity.WeiboGeo;
import me.aiqi.A7weibo.entity.WeiboItem;
import me.aiqi.A7weibo.entity.WeiboUser;
import me.aiqi.A7weibo.entity.WeiboVisiblity;
import me.aiqi.A7weibo.network.SslClient;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;

public class WeiboDownloader extends AsyncTask<WeiboDownloader.Params, Void, ArrayList<WeiboItem>> {

	private static final String TAG = "WeiboDownloader";
	private WeiboListAdapter mAdapter;
	private boolean isRunning = false;

	public WeiboDownloader(WeiboListAdapter adapter) {
		mAdapter = adapter;
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

		public String put(String key, String value) {
			return mMap.put(key, value);
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
		isRunning = true;
		super.onPreExecute();
	}

	@Override
	protected ArrayList<WeiboItem> doInBackground(Params... params) {
		try {
			String url = params[0].buildURL();
			HttpGet httpGet = new HttpGet(url);
			Log.i(TAG, "Request weibo:" + url);
			HttpResponse response = SslClient.getSslClient(new DefaultHttpClient()).execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return parseJson(EntityUtils.toString(response.getEntity()));
			} else {
				Log.e(TAG, "Failed download weibo items:" + url + ", status code: " + statusCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
		return null;
	}

	/**
	 * if there is already a task running;
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * notify view to update UI
	 */
	@Override
	protected void onPostExecute(ArrayList<WeiboItem> result) {
		if (result != null) {
			Log.i(TAG, "got " + result.size() + " new weibo, update adapter now");
			mAdapter.updateWeibolist(result);
		} else {
			Log.i(TAG, "got nothing");
		}
		isRunning = false;

		super.onPostExecute(result);
	}

	/**
	 * parse json string and encapsulate Weibo items into ArrayList<WeiboItem>
	 * 
	 * @param json
	 * @return ArrayList<WeiboItem>
	 */
	protected ArrayList<WeiboItem> parseJson(String json) {
		if (TextUtils.isEmpty(json)) {
			Log.w(TAG, "json is empty or null");
			return null;
		}
		Log.i(TAG, "json: length: " + json.length());
		ArrayList<WeiboItem> list = new ArrayList<WeiboItem>();
		try {
			JSONObject object = new JSONObject(json);
			JSONArray array = null;
			if (object != null) {
				array = object.optJSONArray("statuses");
			}
			if (array == null) {
				Log.w(TAG, "Error parsing weiboitem json");
				return null;
			}
			Log.i(TAG, "begin build ArrayList");
			for (int i = 0; i < array.length(); i++) {
				object = (JSONObject) array.get(i);
				WeiboItem weiboItem = new WeiboItem();
				weiboItem.setAttitudes_count(object.optInt("attitudes_count")); // fallback:0
				weiboItem.setComments_count(object.optInt("comments_count")); // fallback:0
				weiboItem.setCreated_at(object.optString("created_at")); // fallback:""
				weiboItem.setFavorited(object.optBoolean("favorited")); // fallback:false
				// weiboItem.setGeo(); // fallback:null
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
				// //TODO: 暂时不实现
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
				Log.i(TAG, weiboItem.toString());
				list.add(weiboItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.w(TAG, "parsing weiboitem json error:" + e.toString());
		}
		return list;
	}
}
