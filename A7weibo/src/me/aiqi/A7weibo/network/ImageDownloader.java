/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.network.ImageDownloader.java
 * created at: Sep 19, 2013 1:38:21 AM
 * @author starfish
 */

package me.aiqi.A7weibo.network;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageDownloader extends AsyncTask<ImageDownloader.Params, Void, Bitmap> {
	private static final String TAG = "ImageDownloader";
	private ImageView imageView;

	public static class Params {

		public Params() {
		}

		public Params(String url, ImageView imageView) {
			super();
			this.url = url;
			this.imageView = imageView;
		}

		public String url;
		public ImageView imageView;
	}

	@Override
	protected Bitmap doInBackground(Params... params) {
		if (params == null) {
			return null;
		}
		String url = params[0].url;
		imageView = params[0].imageView;

		Bitmap bitmap = null;
		try {
			URLConnection conn = new URL(url).openConnection();
			conn.setConnectTimeout(10);
			conn.setReadTimeout(30000);
			bitmap = BitmapFactory.decodeStream((InputStream) conn.getContent());
		} catch (Exception e) {
			Log.w(TAG, "Failed downloading image: " + url);
			e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (imageView != null && result != null) {
			imageView.setImageBitmap(result);
		}
		super.onPostExecute(result);
	}
}
