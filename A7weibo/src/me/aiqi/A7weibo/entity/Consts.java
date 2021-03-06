package me.aiqi.A7weibo.entity;

import me.aiqi.A7weibo.MyApplication;
import me.aiqi.A7weibo.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Consts {

	public static final class ImageLoader {
		public static final int ANIM_AVATAR_FADE_IN_MILLIS = 1000;

		public static final boolean PAUSE_ON_SCROLL = false;
		public static final boolean PAUSE_ON_FLING = true;

		public static final boolean MEMORY_CACHE = true;
		public static final int MEMORY_CACHE_SIZE = 3 * 1024 * 1024; // 3MB
		public static final boolean DISK_CACHE = true;
		public static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB, use Unlimited policy by default

	}

	public static final class WeiboDownloader {
		public static final int COUNT_PER_PAGE = 20; // weibo items to return per request, max:100
	}

	/** set this small image to release memory if ImageView */
	public static final Bitmap PLACE_HOLDER_IMAGE_1x1 = BitmapFactory.decodeResource(MyApplication.getContext()
			.getResources(), R.drawable.place_holder_1x1);

	public static final class Weibo {
		public static final int WEIBO_CONTENT_LENGTH = 140; // 140 characters at most one weibo
	}

	public static final class ApiUrl {
		public static final String FRIENDS_TIMELINE = "https://api.weibo.com/2/statuses/friends_timeline.json";
		public static final String USER_TIMELINE = "https://api.weibo.com/2/statuses/user_timeline.json";
	}
}
