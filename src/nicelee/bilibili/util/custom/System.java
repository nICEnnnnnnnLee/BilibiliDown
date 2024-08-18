package nicelee.bilibili.util.custom;

import static java.lang.System.err;

import java.io.PrintStream;

import org.json.JSONObject;

import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;

public class System {

	private static long deltaRemoteSubLocal;
	private static boolean initiated;

	public final static PrintStream out = java.lang.System.out;

	public static void init(boolean isToInitiate) {
		if (!isToInitiate)
			return;
		try {
			HttpRequestUtil util = new HttpRequestUtil();
			// String url = "https://api.bilibili.com/x/report/click/now";
			String url = "https://api.bilibili.com/x/click-interface/click/now";
			long currentTime0 = java.lang.System.currentTimeMillis();
			String timeResult = util.getContent(url, new HttpHeaders().getCommonHeaders());
			long currentTime1 = java.lang.System.currentTimeMillis();
			Logger.println(timeResult);
			long remoteTime = new JSONObject(timeResult).getJSONObject("data").getLong("now") * 1000;
			deltaRemoteSubLocal = remoteTime - (currentTime0 + currentTime1) / 2;
			Logger.println(deltaRemoteSubLocal);
			initiated = true;
		} catch (Exception e) {
			e.printStackTrace();
			err.println("本地时间校准失败");
		}
	}

	public static long currentTimeMillis() {
		if (initiated)
			return java.lang.System.currentTimeMillis() + deltaRemoteSubLocal;
		else
			return java.lang.System.currentTimeMillis();
	}

	public static void exit(int status) {
		java.lang.System.exit(status);
	}

	public static String getProperty(String key) {
		return java.lang.System.getProperty(key);
	}
}
