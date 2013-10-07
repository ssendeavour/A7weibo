/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.weibo.WeiboRepostActivity.java
 * created at: Oct 6, 2013 2:43:24 PM
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
import me.aiqi.A7weibo.util.WbUtil;
import me.aiqi.A7weibo.util.WeiboRichText;

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

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
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

public class WeiboRepostActivity extends Activity {

	private static final String TAG = WeiboRepostActivity.class.getSimpleName();

	public static final int REPOST_SUCCEED = 0x100;
	public static final int REPOST_FAILED_NETWORK = 0x101;
	public static final int REPOST_FAILED_NOT_CONNECTED = 0x102;
	public static final int REPOST_FAILED_TOO_LONG = 0x104;
	public static final int REPOST_FAILED_EXPIRED = 0x105;

	/** weibo to be reposted */
	public static final String WEIBO_ID = "weibo_id";
	public static final String USER_NAME = "user_name";
	/**
	 * weibo text to be reposted, it is the comment to the original weibo if the
	 * weibo itself is a reposted weibo
	 */
	public static final String WEIBO_CONTENT = "weibo_content";

	/** original weibo if weibo to be reposted is a reposted weibo */
	public static final String OTHERS_COMMENT = "others_comment";
	public static final String COMMENTERS_NAME = "commenters_name";

	private Button btn_cancel;
	private Button btn_send;
	/** logic in Checkbox and EditText, TextView are twisted ... */
	private CheckBox cb_cc_to_reposted;
	private CheckBox cb_cc_to_original;
	private EditText et_repost_content;
	private TextView tv_original_text;
	private TextView tv_character_number;

	private boolean hasSend = false;

	private long weibo_id = 0;

	private MyHandler handler = new MyHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repost);

		btn_cancel = (Button) findViewById(R.id.btn_cancel_repost);
		btn_send = (Button) findViewById(R.id.btn_send_repost);
		cb_cc_to_reposted = (CheckBox) findViewById(R.id.cb_also_comment_to_current_weibo);
		cb_cc_to_original = (CheckBox) findViewById(R.id.cb_also_comment_to_original_weibo);
		et_repost_content = (EditText) findViewById(R.id.et_repost_weibo_content);
		tv_original_text = (TextView) findViewById(R.id.tv_repost_original_weibo);
		tv_character_number = (TextView) findViewById(R.id.tv_repost_character_number);

		et_repost_content.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
				}
			}
		});

		et_repost_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

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

		weibo_id = getIntent().getLongExtra(WEIBO_ID, 0);

		String userName = getIntent().getStringExtra(USER_NAME);
		// other's comments to the original weibo, maybe null
		String weibo = getIntent().getStringExtra(OTHERS_COMMENT);
		if (!TextUtils.isEmpty(weibo)) {
			cb_cc_to_original.setVisibility(View.VISIBLE);
			String commenter = getIntent().getStringExtra(COMMENTERS_NAME);
			cb_cc_to_original.setText("评论给原作者 " + userName);
			cb_cc_to_reposted.setText("评论给 " + commenter);
			et_repost_content.setText("//@" + commenter + ":" + getIntent().getStringExtra(OTHERS_COMMENT));
			// move cursor before the first character
			et_repost_content.setSelection(0);
		} else {
			et_repost_content.setText("");
			cb_cc_to_reposted.setText("评论给 " + getIntent().getStringExtra(USER_NAME));
		}

		// weibo to be reposted, user can't modify this weibo
		weibo = getIntent().getStringExtra(WEIBO_CONTENT);
		if (TextUtils.isEmpty(weibo)) {
			Log.w(TAG, "weibo to be reposted is empty");
		}
		tv_original_text.setText(WeiboRichText.getRichWeiboText(getApplicationContext(), "@" + userName + ":" + weibo));

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
			case REPOST_FAILED_NETWORK:
				btn_send.setEnabled(true);
				Toast.makeText(WeiboRepostActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
				break;

			case REPOST_FAILED_TOO_LONG:
				btn_send.setEnabled(true);
				Toast.makeText(WeiboRepostActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
				break;

			case REPOST_FAILED_EXPIRED:
				btn_send.setEnabled(true);
				Toast.makeText(WeiboRepostActivity.this, "授权已过期，请重新授权", Toast.LENGTH_SHORT).show();
				finish();
				break;

			case REPOST_SUCCEED:
				btn_send.setEnabled(true);
				Toast.makeText(WeiboRepostActivity.this, "转发成功", Toast.LENGTH_SHORT).show();
				hasSend = true;
				finish();
				break;

			case REPOST_FAILED_NOT_CONNECTED:
				btn_send.setEnabled(true);
				Toast.makeText(WeiboRepostActivity.this, "未连接网络", Toast.LENGTH_SHORT).show();
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
		if (!hasSend && !TextUtils.isEmpty(et_repost_content.getText())) {
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
					msg.what = REPOST_FAILED_NOT_CONNECTED;
					handler.sendMessage(msg);
					return;
				}

				AccessToken accessToken = MyApplication.getAccessToken();
				if (accessToken.isExpired()) {
					Message msg = Message.obtain();
					msg.what = REPOST_FAILED_EXPIRED;
					handler.sendMessage(msg);
					return;
				}

				String weibo = et_repost_content.getText().toString().trim();
				// if content is empty, weibo will provide a default string (转发微博)
				if (weibo.length() > Consts.Weibo.WEIBO_CONTENT_LENGTH) {
					Message msg = Message.obtain();
					msg.what = REPOST_FAILED_TOO_LONG;
					msg.obj = "微博过长，超了" + (weibo.length() - Consts.Weibo.WEIBO_CONTENT_LENGTH) + "个字";
					handler.sendMessage(msg);
					return;
				}
				Log.v(TAG, weibo);

				// also comment when repost
				int is_comment;
				if (cb_cc_to_original.getVisibility() == View.VISIBLE) {
					is_comment = (cb_cc_to_original.isChecked() ? 2 : 0) + (cb_cc_to_reposted.isChecked() ? 1 : 0);
				} else {
					is_comment = cb_cc_to_reposted.isChecked() ? 1 : 0;
				}

				// Create a new HttpClient and Post Header
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("https://api.weibo.com/2/statuses/repost.json");

				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
					nameValuePairs.add(new BasicNameValuePair("status", weibo));
					nameValuePairs.add(new BasicNameValuePair("access_token", accessToken.getAccessTokenString()));
					nameValuePairs.add(new BasicNameValuePair("id", "" + weibo_id));
					nameValuePairs.add(new BasicNameValuePair("is_comment", "" + is_comment));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

					Log.v(TAG, httppost.getRequestLine().getUri());

					// Execute HTTP Post Request	
					HttpResponse response = httpclient.execute(httppost);
					int statusCode = response.getStatusLine().getStatusCode();
					if (statusCode == HttpStatus.SC_OK) {
						String json = EntityUtils.toString(response.getEntity());
						WeiboRepostResponse repostResponse = WeiboRepostResponse.parseJson(json);
						if (repostResponse != null
								&& repostResponse.getRepostedWeibo().getId() == weibo_id) {
							Log.v(TAG, "repost succeed");
							Message msg = Message.obtain();
							msg.what = REPOST_SUCCEED;
							handler.sendMessage(msg);
						} else {
							Log.v(TAG, "repost failed");
							Message msg = Message.obtain();
							msg.what = REPOST_FAILED_NETWORK;
							msg.obj = "转发失败";
							handler.sendMessage(msg);
							return;
						}
					} else {
						String msgString = "转发失败, http_code:" + statusCode;
						Message msg = Message.obtain();
						msg.what = REPOST_FAILED_NETWORK;
						msg.obj = msgString;
						handler.sendMessage(msg);
						Log.v(TAG, msgString);
						return;
					}
				} catch (IOException e) {
					Message msg = Message.obtain();
					msg.what = REPOST_FAILED_NETWORK;
					msg.obj = "网络异常,转发失败";
					handler.sendMessage(msg);
					return;
				}
			}
		}.start();
	}
}
