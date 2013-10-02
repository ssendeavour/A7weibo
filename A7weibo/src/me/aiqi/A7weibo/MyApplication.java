package me.aiqi.A7weibo;

import java.io.File;

import cn.trinea.android.common.service.impl.ImageCache;
import cn.trinea.android.common.service.impl.ImageCache.OnImageCallbackListener;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

import me.aiqi.A7weibo.entity.AccessToken;
import me.aiqi.A7weibo.entity.Consts;
import android.app.Application;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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

	@Override
	public void onCreate() {
		super.onCreate();

		// global configuration here are for avatar images
		File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc(true)
				.cacheInMemory(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.displayer(new FadeInBitmapDisplayer(Consts.ImageLoader.ANIM_AVATAR_FADE_IN_MILLIS))
				.showStubImage(R.drawable.ic_launcher)
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.memoryCache(new LruMemoryCache(Consts.ImageLoader.MEMORY_CACHE_SIZE))
				.discCache(new UnlimitedDiscCache(cacheDir))
				.defaultDisplayImageOptions(defaultOptions)
				.build();
		ImageLoader.getInstance().init(config);
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
