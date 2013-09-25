package me.aiqi.A7weibo;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import me.aiqi.A7weibo.entity.AccessToken;
import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
	private static final String TAG = "MyApplication";
	private static AccessToken accessToken;
	private static MyApplication mApplicationContext;

	public MyApplication() {
		super();
		accessToken = new AccessToken();
		mApplicationContext = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.discCacheSize(50 * 1024 * 1024)
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
