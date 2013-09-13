package me.aiqi.A7weibo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import me.aiqi.A7weibo.entity.AppRegInfo;
import android.os.Environment;

public class AppRegInfoHelper {
	public static AppRegInfo getAppRegInfo() {
		AppRegInfo appinfo = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(Environment.getExternalStorageDirectory(), "appkey.txt")));
			String appKey = br.readLine().trim();
			String appSecret = br.readLine().trim();
			String appUrl = br.readLine().trim();
			br.close();

			if (appKey == null) {
				appKey = "";
			}
			if (appSecret == null) {
				appSecret = "";
			}
			if (appUrl == null) {
				appUrl = "";
			}

			appinfo = new AppRegInfo(appKey, appSecret, appUrl);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return appinfo;
	}
}
