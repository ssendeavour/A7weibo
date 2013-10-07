package me.aiqi.A7weibo;

import me.aiqi.A7weibo.entity.AccessToken;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import cn.trinea.android.common.service.impl.ImageCache;
import cn.trinea.android.common.service.impl.ImageCache.OnImageCallbackListener;
import cn.trinea.android.common.service.impl.ImageSDCardCache;
import cn.trinea.android.common.service.impl.ImageSDCardCache.OnImageSDCallbackListener;

public class MyApplication extends Application {
	private static final String TAG = "MyApplication";
	private static AccessToken sAccessToken;
	private static MyApplication sApplicationContext;

	// Image cacher and loader from AndroidCommon
	public static final ImageCache AVATAR_CACHE = new ImageCache();
	public static final ImageSDCardCache LARGE_IMAGE_CACHE = new ImageSDCardCache();
	public static final AlphaAnimation IMAGE_APPEAR_ANIM;

	static {
		IMAGE_APPEAR_ANIM = new AlphaAnimation(0.0F, 1.0F);
		IMAGE_APPEAR_ANIM.setDuration(800);
	}

	static {

		//		AVATAR_CACHE.setValidTime(61 * 86400 * 1000); // cache for two months
		AVATAR_CACHE.setOnImageCallbackListener(new OnImageCallbackListener() {

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
		});
	}

	static {

		//		LARGE_IMAGE_CACHE.setValidTime(10 * 86400 * 1000); // cache for 10 days
		LARGE_IMAGE_CACHE.setOnImageSDCallbackListener(new OnImageSDCallbackListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onImageLoaded(String imageUrl, String imagePath, View view, boolean isInCache) {
				if (view == null) {
					return;
				}

				Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
				if (bitmap == null) {
					return;
				}

				((ImageView) view).setImageBitmap(bitmap);
				if (!isInCache) {
					view.startAnimation(IMAGE_APPEAR_ANIM);
				}
			}
		});
	}

	public MyApplication() {
		super();
		sAccessToken = new AccessToken();
		sApplicationContext = this;
	}

	public static AccessToken getAccessToken() {
		return sAccessToken;
	}

	public static void setAccessToken(AccessToken token) {
		sAccessToken = token;
		Log.v(TAG, token.toString());
	}

	public static MyApplication getContext() {
		return sApplicationContext;
	}
}
