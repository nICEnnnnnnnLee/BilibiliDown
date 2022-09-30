package nicelee.bilibili.util;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

import nicelee.bilibili.INeedLogin;

public class HttpCookies {
	static List<HttpCookie> globalCookies;
	static List<HttpCookie> globalCookiesWithFingerprint;
	static String csrf;
	static String refreshToken;
	
	public static List<HttpCookie> convertCookies(String cookie) {
		String lines[] = cookie.split("\n");
		if(lines.length >= 2) {
			refreshToken = lines[1].trim();
		}
		List<HttpCookie> iCookies = new ArrayList<HttpCookie>();
		String[] cookieStrs = lines[0].replaceAll("\\||\r|\n| |\\[|\\]|\"", "").split(",|;|&");
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
	
	public static List<HttpCookie> globalCookiesWithFingerprint() {
		if(globalCookiesWithFingerprint == null) {
			// String fingerprint = Files.readString(Paths.get("config/fingerprint.config"));
			String fingerprint = new INeedLogin().genLoginHeader().get("Cookie");
			globalCookiesWithFingerprint = new ArrayList<HttpCookie>();
			globalCookiesWithFingerprint.addAll(convertCookies(fingerprint));
			if(globalCookies != null)
				globalCookiesWithFingerprint.addAll(globalCookies);
		}
		return globalCookiesWithFingerprint;
	}

	public static void setGlobalCookies(List<HttpCookie> globalCookies) {
		HttpCookies.globalCookies = globalCookies;
		csrf = null;
		globalCookiesWithFingerprint = null;
	}
	
	public static String getCsrf() {
		if(csrf == null && globalCookies != null) {
			for(HttpCookie cookie: globalCookies) {
				if("bili_jct".equals(cookie.getName())){
					csrf = cookie.getValue();
				}
			}
		}
		return csrf;
	}

	public static String getRefreshToken() {
		return refreshToken;
	}

	public static void setRefreshToken(String refreshToken) {
		HttpCookies.refreshToken = refreshToken;
	}

}
