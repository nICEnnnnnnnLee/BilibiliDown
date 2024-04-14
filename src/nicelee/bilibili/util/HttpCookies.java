package nicelee.bilibili.util;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nicelee.bilibili.API;

public class HttpCookies {
	static List<HttpCookie> globalCookies;
	static List<HttpCookie> globalCookiesWithFingerprint;
	static String csrf;
	static String refreshToken;

	public static List<HttpCookie> convertCookies(String cookie) {
		String lines[] = cookie.split("\n");
		if (lines.length >= 2) {
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

	/**
	 * @deprecated	推荐使用HttpCookies.globalCookiesWithFingerprint()
	 * @return	不带指纹的全局cookie
	 */
	public static List<HttpCookie> getGlobalCookies() {
		return globalCookies;
	}

	public static List<HttpCookie> globalCookiesWithFingerprint() {
		if (globalCookiesWithFingerprint == null) {
			String fingerprint = API.getFingerprint();
			globalCookiesWithFingerprint = new ArrayList<HttpCookie>();
			globalCookiesWithFingerprint.addAll(convertCookies(fingerprint));
			if (globalCookies != null)
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
		if (csrf == null && globalCookies != null) {
			for (HttpCookie cookie : globalCookies) {
				if ("bili_jct".equals(cookie.getName())) {
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

	public static String map2CookieStr(Map<String, String> kvMap) {
		if (kvMap.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : kvMap.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
		}
		return sb.substring(0, sb.length() - 2);
	}

	public static String get(String key) {
		for (HttpCookie cookie : globalCookiesWithFingerprint()) {
			if (key.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}

	public static boolean set(String key, String value) {
		for (HttpCookie cookie : globalCookiesWithFingerprint()) {
			if (key.equals(cookie.getName())) {
				cookie.setValue(value);
				return true;
			}
		}
		HttpCookie cCookie = new HttpCookie(key, value);
		globalCookiesWithFingerprint().add(cCookie);
		return false;
	}
}
