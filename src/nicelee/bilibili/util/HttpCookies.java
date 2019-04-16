package nicelee.bilibili.util;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class HttpCookies {
	static List<HttpCookie> globalCookies;

	public static List<HttpCookie> convertCookies(String cookie) {
		List<HttpCookie> iCookies = new ArrayList<HttpCookie>();
		String[] cookieStrs = cookie.replaceAll("\\||\r|\n| |\\[|\\]|\"", "").split(",|;|&");
		for (String cookieStr : cookieStrs) {
			String entry[] = cookieStr.split("=");
			HttpCookie cCookie = new HttpCookie(entry[0], entry[1]);
			iCookies.add(cCookie);
		}
		return iCookies;
	}

	public static List<HttpCookie> getGlobalCookies() {
		return globalCookies;
	}

	public static void setGlobalCookies(List<HttpCookie> globalCookies) {
		HttpCookies.globalCookies = globalCookies;
	}

}
