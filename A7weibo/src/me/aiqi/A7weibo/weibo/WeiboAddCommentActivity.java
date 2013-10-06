/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.weibo.WeiboCommentActivity.java
 * created at: Oct 6, 2013 11:27:36 AM
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
import me.aiqi.A7weibo.entity.WeiboCommentResponse;
import me.aiqi.A7weibo.entity.WeiboRepostResponse;
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

import android.annotation.SuppressLint;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WeiboAddCommentActivity extends Activity {

	private static final String TAG = WeiboAddCommentActivity.class.getSimpleName();

	public static final int COMMENT_SUCCEED = 0x100;
	public static final int COMMENT_FAILED_NETWORK = 0x101;
	public static final int COMMENT_FAILED_NOT_CONNECTED = 0x102;
	public static final int COMMENT_FAILED_EMPTY = 0x103;
	public static final int COMMENT_FAILED_TOO_LONG = 0x104;
	public static final int COMMENT_FAILED_EXPIRED = 0x105;

	public static final int REPOST_SUCCEED = 0x110;
	public static final int REPOST_FAILED_NETWORK = 0x111;
	public static final int REPOST_FAILED_NOT_CONNECTED = 0x112;
	public static final int REPOST_FAILED_TOO_LONG = 0x113;
	public static final int REPOST_FAILED_EXPIRED = 0x114;

	public static final String WEIBO_ID = "weibo_id";
	public static final String ORIGINAL_WEIBO_ID = "orig_weibo_id";

	private Button btn_cancel;
	private Button btn_send;
	private CheckBox cb_cc_to_me;
	private EditText et_comment_content;
	private TextView tv_character_number;

	private boolean hasSend = false;
	private long commentted_weibo_id; //id of weibo to be commented 
	private long original_weibo_id = 0; // original weibo id, maybe 0 if don't have
	private MyHandler handler = new MyHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_comment);

		btn_cancel = (Button) findViewById(R.id.btn_cancel_comment);
		btn_send = (Button) findViewById(R.id.btn_send_comment);
		cb_cc_to_me = (CheckBox) findViewById(R.id.cb_also_forward_to_my_weibo);
		et_comment_content = (EditText) findViewById(R.id.et_weibo_comment_content);
		tv_character_number = (TextView) findViewById(R.id.tv_comment_character_number);

		et_comment_content.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
				}
			}
		});
		et_comment_content.addTextChangedListener(new TextWatcher() {

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
		et_comment_content.setText("");

		commentted_weibo_id = getIntent().getLongExtra(WEIBO_ID, 0);
		original_weibo_id = getIntent().getLongExtra(ORIGINAL_WEIBO_ID, 0);

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
			case COMMENT_FAILED_NETWORK:
			case REPOST_FAILED_NETWORK:
				Toast.makeText(WeiboAddCommentActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
				btn_send.setEnabled(true);
				break;

			case COMMENT_FAILED_EMPTY:
				Toast.makeText(WeiboAddCommentActivity.this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
				btn_send.setEnabled(true);
				break;

			case COMMENT_FAILED_TOO_LONG:
				Toast.makeText(WeiboAddCommentActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
				btn_send.setEnabled(true);
				break;

			case COMMENT_FAILED_EXPIRED:
			case REPOST_FAILED_EXPIRED:
				Toast.makeText(WeiboAddCommentActivity.this, "授权已过期，请重新授权", Toast.LENGTH_SHORT).show();
				btn_send.setEnabled(true);
				finish();
				break;

			case COMMENT_SUCCEED:
				Toast.makeText(WeiboAddCommentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
				btn_send.setEnabled(true);
				finish();
				break;

			case COMMENT_FAILED_NOT_CONNECTED:
			case REPOST_FAILED_NOT_CONNECTED:
				Toast.makeText(WeiboAddCommentActivity.this, "未连接网络", Toast.LENGTH_SHORT).show();
				btn_send.setEnabled(true);
				break;

			case REPOST_SUCCEED:
				btn_send.setEnabled(true);
				Toast.makeText(WeiboAddCommentActivity.this, "评论和转发成功", Toast.LENGTH_SHORT).show();
				finish();
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
		if (!hasSend && !TextUtils.isEmpty(et_comment_content.getText().toString().trim())) {
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
					msg.what = COMMENT_FAILED_NOT_CONNECTED;
					handler.sendMessage(msg);
					return;
				}

				String content = et_comment_content.getText().toString().trim();
				if (TextUtils.isEmpty(content)) {
					Message msg = Message.obtain();
					msg.what = COMMENT_FAILED_EMPTY;
					handler.sendMessage(msg);
					return;
				} else if (content.length() > Consts.Weibo.WEIBO_CONTENT_LENGTH) {
					Message msg = Message.obtain();
					msg.what = COMMENT_FAILED_TOO_LONG;
					msg.obj = "评论过长，超了" + (content.length() - Consts.Weibo.WEIBO_CONTENT_LENGTH) + "个字";
					handler.sendMessage(msg);
					return;
				}
				Log.v(TAG, content);

				AccessToken accessToken = MyApplication.getAccessToken();
				if (accessToken.isExpired()) {
					Message msg = Message.obtain();
					msg.what = COMMENT_FAILED_EXPIRED;
					handler.sendMessage(msg);
					return;
				}

				// repost to myself
				if (cb_cc_to_me.isChecked()) {
					// Create a new HttpClient and Post Header
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost("https://api.weibo.com/2/statuses/repost.json");

					try {
						// Add your data
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
						nameValuePairs.add(new BasicNameValuePair("status", content));
						nameValuePairs.add(new BasicNameValuePair("access_token", accessToken.getAccessTokenString()));
						nameValuePairs.add(new BasicNameValuePair("id", "" + commentted_weibo_id));
						nameValuePairs.add(new BasicNameValuePair("is_comment", "" + 1)); // 0: no, 1: yes
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

						Log.v(TAG, httppost.getRequestLine().getUri());

						// Execute HTTP Post Request	
						HttpResponse response = httpclient.execute(httppost);
						int statusCode = response.getStatusLine().getStatusCode();
						if (statusCode == HttpStatus.SC_OK) {
							String json = EntityUtils.toString(response.getEntity());
							WeiboRepostResponse repostResponse = WeiboRepostResponse.parseJson(json);
							if (repostResponse != null
									&& repostResponse.getRepostedWeibo().getId() == (original_weibo_id == 0 ? commentted_weibo_id
											: original_weibo_id)) {
								Log.v(TAG, "repost succeed");
								Message msg = Message.obtain();
								msg.what = REPOST_SUCCEED;
								handler.sendMessage(msg);
							} else {
								Log.v(TAG, "repost failed");
								Message msg = Message.obtain();
								msg.what = COMMENT_FAILED_NETWORK;
								msg.obj = "转发与评论失败";
								handler.sendMessage(msg);
								return;
							}
						} else {
							String msgString = "转发与评论失败, http_code:" + statusCode;
							Message msg = Message.obtain();
							msg.what = COMMENT_FAILED_NETWORK;
							msg.obj = msgString;
							handler.sendMessage(msg);
							Log.v(TAG, msgString);
							return;
						}
					} catch (IOException e) {
						Message msg = Message.obtain();
						msg.what = COMMENT_FAILED_NETWORK;
						msg.obj = "网络异常,转发失败";
						handler.sendMessage(msg);
						return;
					}
				} else {
					// Create a new HttpClient and Post Header
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost("https://api.weibo.com/2/comments/create.json");

					try {
						// Add your data
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("comment", content));
						nameValuePairs.add(new BasicNameValuePair("access_token", accessToken.getAccessTokenString()));
						nameValuePairs.add(new BasicNameValuePair("id", "" + commentted_weibo_id));
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

						Log.v(TAG, httppost.getRequestLine().getUri());

						// Execute HTTP Post Request	
						HttpResponse response = httpclient.execute(httppost);
						int statusCode = response.getStatusLine().getStatusCode();
						if (statusCode == HttpStatus.SC_OK) {
							String json = EntityUtils.toString(response.getEntity());
							WeiboCommentResponse commentResponse = WeiboCommentResponse.parseJson(json);
							if (commentResponse != null
									&& commentResponse.getCommentedWeibo().getId() == commentted_weibo_id) {
								Log.v(TAG, "comment succeed");
								Message msg = Message.obtain();
								msg.what = COMMENT_SUCCEED;
								handler.sendMessage(msg);
							} else {
								Log.v(TAG, "comment failed");
								Message msg = Message.obtain();
								msg.what = COMMENT_FAILED_NETWORK;
								msg.obj = "评论失败";
								handler.sendMessage(msg);
								return;
							}
						} else {
							String msgString = "发送评论失败, http_code:" + statusCode;
							Message msg = Message.obtain();
							msg.what = COMMENT_FAILED_NETWORK;
							msg.obj = msgString;
							handler.sendMessage(msg);
							Log.v(TAG, msgString);
							return;
						}
					} catch (IOException e) {
						Message msg = Message.obtain();
						msg.what = COMMENT_FAILED_NETWORK;
						msg.obj = "网络异常,评论失败";
						handler.sendMessage(msg);
						return;
					}
				}
			}
		}.start();
	}
}
