/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.weibo.WeiboNew.java
 * created at: Oct 7, 2013 10:11:56 AM
 * @author starfish
 */

package me.aiqi.A7weibo.weibo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.aiqi.A7weibo.MyApplication;
import me.aiqi.A7weibo.R;
import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.Consts;
import me.aiqi.A7weibo.entity.WeiboItem;
import me.aiqi.A7weibo.entity.WeiboNewResponse;
import me.aiqi.A7weibo.network.NetworkCondition;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WeiboNewActivity extends Activity {

	private static final String TAG = WeiboNewActivity.class.getSimpleName();

	public static final int SUCCEED = 0x100;
	public static final int FAILED_NETWORK = 0x101;
	public static final int FAILED_NOT_CONNECTED = 0x102;
	public static final int FAILED_EMPTY = 0x103;
	public static final int FAILED_TOO_LONG = 0x104;
	public static final int FAILED_EXPIRED = 0x105;

	private Button btn_cancel;
	private Button btn_send;
	private EditText et_weibo_content;
	private TextView tv_character_number;

	private boolean hasSend = false;
	private MyHandler handler = new MyHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_write_weibo);

		btn_cancel = (Button) findViewById(R.id.btn_cancel_new_weibo);
		btn_send = (Button) findViewById(R.id.btn_send_new_weibo);
		et_weibo_content = (EditText) findViewById(R.id.et_new_weibo_content);
		tv_character_number = (TextView) findViewById(R.id.tv_new_weibo_character_number);

		et_weibo_content.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
				}
			}
		});

		et_weibo_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int remains = Consts.Weibo.WEIBO_CONTENT_LENGTH - s.length();
				if (remains >= 0) {
					tv_character_number.setText(Html.fromHtml("还可输入 <b><font size=\"5\" color=\"navy\">" + remains
							+ "</font></b> 个字"));
				} else {
					tv_character_number.setText(Html.fromHtml("超过了 <b><font size=\"5\" color=\"red\">" + (-remains)
							+ "</font></b> 个字"));
				}
			}
		});
		// trigger afterTextChanged
		et_weibo_content.setText("");

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cancel();
			}
		});

		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				send();
			}
		});
	}

	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FAILED_NETWORK:
				Toast.makeText(WeiboNewActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
				btn_send.setEnabled(true);
				break;

			case FAILED_EMPTY:
				Toast.makeText(WeiboNewActivity.this, "微博内容不能为空", Toast.LENGTH_SHORT).show();
				btn_send.setEnabled(true);
				break;

			case FAILED_TOO_LONG:
				Toast.makeText(WeiboNewActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
				btn_send.setEnabled(true);
				break;

			case FAILED_EXPIRED:
				Toast.makeText(WeiboNewActivity.this, "授权已过期，请重新授权", Toast.LENGTH_SHORT).show();
				btn_send.setEnabled(true);
				finish();
				break;

			case SUCCEED:
				Toast.makeText(WeiboNewActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
				btn_send.setEnabled(true);
				hasSend = true;
				finish();
				break;

			case FAILED_NOT_CONNECTED:
				Toast.makeText(WeiboNewActivity.this, "未连接网络", Toast.LENGTH_SHORT).show();
				btn_send.setEnabled(true);
				break;
			}
			super.handleMessage(msg);
		}
	}

	private void cancel() {
		finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!hasSend && !TextUtils.isEmpty(et_weibo_content.getText().toString().trim())) {
			saveDraft();
		}
	}

	private void saveDraft() {
		Log.v(TAG, "save drafts not implemented");
	}

	private void send() {
		btn_send.setEnabled(false);
		new Thread() {
			@Override
			public void run() {
				super.run();

				if (!NetworkCondition.isOnline()) {
					Message msg = Message.obtain();
					msg.what = FAILED_NOT_CONNECTED;
					handler.sendMessage(msg);
					return;
				}

				String content = et_weibo_content.getText().toString().trim();
				if (TextUtils.isEmpty(content)) {
					Message msg = Message.obtain();
					msg.what = FAILED_EMPTY;
					handler.sendMessage(msg);
					return;
				} else if (content.length() > Consts.Weibo.WEIBO_CONTENT_LENGTH) {
					Message msg = Message.obtain();
					msg.what = FAILED_TOO_LONG;
					msg.obj = "评论过长，超了" + (content.length() - Consts.Weibo.WEIBO_CONTENT_LENGTH) + "个字";
					handler.sendMessage(msg);
					return;
				}
				Log.v(TAG, content);

				AccessToken accessToken = MyApplication.getAccessToken();
				if (accessToken.isExpired()) {
					Message msg = Message.obtain();
					msg.what = FAILED_EXPIRED;
					handler.sendMessage(msg);
					return;
				}

				// Create a new HttpClient and Post Header
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("https://api.weibo.com/2/statuses/update.json");

				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					nameValuePairs.add(new BasicNameValuePair("status", content));
					nameValuePairs.add(new BasicNameValuePair("access_token", accessToken.getAccessTokenString()));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

					// Execute HTTP Post Request	
					HttpResponse response = httpclient.execute(httppost);
					int statusCode = response.getStatusLine().getStatusCode();
					if (statusCode == HttpStatus.SC_OK) {
						String json = EntityUtils.toString(response.getEntity());
						WeiboItem weiboItem = WeiboNewResponse.parseJson(json);
						if (weiboItem != null && weiboItem.getId() > 0) {
							Log.v(TAG, "update weibo status succeed");
							Message msg = Message.obtain();
							msg.what = SUCCEED;
							handler.sendMessage(msg);
						} else {
							Log.v(TAG, "update weibo status failed");
							Message msg = Message.obtain();
							msg.what = FAILED_NETWORK;
							msg.obj = "评论失败";
							handler.sendMessage(msg);
							return;
						}
					} else {
						String msgString = "发送微博失败, http_code:" + statusCode;
						Message msg = Message.obtain();
						msg.what = FAILED_NETWORK;
						msg.obj = msgString;
						handler.sendMessage(msg);
						Log.v(TAG, msgString);
						return;
					}
				} catch (IOException e) {
					Message msg = Message.obtain();
					msg.what = FAILED_NETWORK;
					msg.obj = "网络异常,评论失败";
					handler.sendMessage(msg);
					return;
				}
			}
		}.start();
	}
}
