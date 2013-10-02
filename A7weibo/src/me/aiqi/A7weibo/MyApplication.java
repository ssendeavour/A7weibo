package me.aiqi.A7weibo;

import me.aiqi.A7weibo.entity.AccessToken;
import android.app.Application;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import cn.trinea.android.common.service.impl.ImageCache;
import cn.trinea.android.common.service.impl.ImageCache.OnImageCallbackListener;

public class MyApplication extends Application {
	private static final String TAG = "MyApplication";
	private static AccessToken accessToken;
	private static MyApplication mApplicationContext;

	// Image cacher and loader from AndroidCommon
	public static final ImageCache AVATAR_CACHE = new ImageCache();

	static {
		OnImageCallbackListener imageCallbackListener = new OnImageCallbackListener() {

			private static final long serialVersionUID = 1L;

			//  callback function after image get success, run on ui thread
			@Override
			public void onImageLoaded(String imageUrl, Drawable imageDrawable, View view, boolean isInCache) {
				// can be another view child, like textView and so on
				if (view != null && imageDrawable != null) {
					ImageView imageView = (ImageView) view;
					imageView.setImageDrawable(imageDrawable);
				}
			}
		};
		AVATAR_CACHE.setOnImageCallbackListener(imageCallbackListener);
	}

	public MyApplication() {
		super();
		accessToken = new AccessToken();
		mApplicationContext = this;
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public static void setAccessToken(AccessToken token) {
		accessToken = token;
		Log.v(TAG, token.toString());
	}

	public static MyApplication getContext() {
		return mApplicationContext;
	}
}
