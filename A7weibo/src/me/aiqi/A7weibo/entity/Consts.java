package me.aiqi.A7weibo.entity;

public class Consts {

	public static class ImageLoader {
		public static final int ANIM_AVATAR_FADE_IN_MILLIS = 1000;

		public static final boolean PAUSE_ON_SCROLL = false;
		public static final boolean PAUSE_ON_FLING = true;

		public static final boolean MEMORY_CACHE = true;
		public static final int MEMORY_CACHE_SIZE = 3 * 1024 * 1024; // 3MB
		public static final boolean DISK_CACHE = true;
		public static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB, use Unlimited policy by default
	}
}
