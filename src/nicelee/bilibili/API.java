package nicelee.bilibili;

import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import nicelee.bilibili.util.Encrypt;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.convert.ConvertUtil;

public class API {

	public static void main(String[] args) {
		List<HttpCookie> cookies = HttpCookies
				.convertCookies("[DedeUserID=xxx; DedeUserID__ckMd5=xxx; SESSDATA=xxx; bili_jct=xxx; bfe_id=xxx...]");
		HttpCookies.setGlobalCookies(cookies);
		boolean result = API.like(666);
		System.out.println(result);
		// API.logout();
	}

	/**
	 * 给视频点赞(前提是已经登录)
	 */
	public static boolean like(String BVid) {
		long avIdNum = ConvertUtil.Bv2Av(BVid);
		return like(avIdNum);
	}

	/**
	 * 给视频点赞(前提是已经登录)
	 */
	public static boolean like(long avIdNum) {
		/**
		 * 是否已经点赞
		 * https://api.bilibili.com/x/web-interface/archive/has/like?aid=666
		 * {"code":0,"message":"0","ttl":1,"data":1} 已经点赞
		 * {"code":0,"message":"0","ttl":1,"data":0} 没有点赞
		 */
		HttpRequestUtil util = new HttpRequestUtil();
		String url_query = "https://api.bilibili.com/x/web-interface/archive/has/like?aid=" + avIdNum;
		HashMap<String, String> headers = new HttpHeaders().getActionHeaders("av" + avIdNum);
		String result_query = util.getContent(url_query, headers, HttpCookies.getGlobalCookies());
		Logger.println(result_query);
		if (result_query.startsWith("{\"code\":0,\"message\":\"0\",\"ttl\":1,\"data\":0}")) {
			String url = "https://api.bilibili.com/x/web-interface/archive/like";
			// like 1 点赞 2 取消
			String param = String.format("aid=%d&like=1&csrf=%s", avIdNum, HttpCookies.getCsrf());
			String result = util.postContent(url, headers, param, HttpCookies.getGlobalCookies());
			// {"code":-101,"message":"账号未登录","ttl":1}
			// {"code":65006,"message":"已赞过","ttl":1}
			// {"code":0,"message":"0","ttl":1}
			Logger.println(result);
			if (result.startsWith("{\"code\":0"))
				return true;
		}
		return false;
	}

	/**
	 * 注销登录状态
	 */
	public static void logout() {
		HttpRequestUtil util = new HttpRequestUtil();
		String url = "https://passport.bilibili.com/login/exit/v2";
		String param = String.format("biliCSRF=%s", HttpCookies.getCsrf());
		String result = util.postContent(url, new HttpHeaders().getLogoutHeaders(), param,
				HttpCookies.getGlobalCookies());
		Logger.println(result);
	}

	// https://s1.hdslb.com/bfs/seed/laputa-header/bili-header.umd.js
	// function getMixinKey(e)
	static int[] MixinArray = { 46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49, 33, 9, 42,
			19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40, 61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54,
			21, 56, 59, 6, 63, 57, 62, 11, 36, 20, 34, 44, 52 };

	static String wbiImg = null;

	private static String getMixinKey(String content) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 32; i++) {
			sb.append(content.charAt(MixinArray[i]));
		}
		return sb.toString();
	}

	private static void getWbiUrl() {
		if (wbiImg == null) {
			synchronized (API.class) {
				if (wbiImg == null) {
					HttpRequestUtil util = new HttpRequestUtil();
					String content = util.getContent("https://api.bilibili.com/x/web-interface/nav",
							new HttpHeaders().getCommonHeaders(), HttpCookies.getGlobalCookies());
					JSONObject obj = new JSONObject(content).getJSONObject("data").getJSONObject("wbi_img");
					String wbiImgUrl = obj.getString("img_url");
					int is = wbiImgUrl.lastIndexOf("/");
					int ie = wbiImgUrl.indexOf(".", is);
					Logger.println(wbiImgUrl);
					String wbiSubUrl = obj.getString("sub_url");
					int ss = wbiSubUrl.lastIndexOf("/");
					int se = wbiSubUrl.indexOf(".", ss);
					Logger.println(wbiSubUrl);
					wbiImg = wbiImgUrl.substring(is + 1, ie) + wbiSubUrl.substring(ss + 1, se);
					Logger.println(wbiImg);
				}
			}
		}
	}

	public static String encWbi(String url) {
		// Logger.println(url);
		// 获取 mixinKey
		getWbiUrl();
		String mixinKey = getMixinKey(wbiImg);
		// url 参数URLEncode并排序
		String paramEncodedSorted, sep, wts = "wts=" + (System.currentTimeMillis() / 1000);
		int questionMarkIdx = url.indexOf("?");
		if (questionMarkIdx >= 0) {
			String paramRaw = (url).substring(questionMarkIdx + 1);
			if (paramRaw.isEmpty()) {
				sep = "";
			} else {
				sep = "&";
			}
			paramRaw += sep + wts;
			String[] params = paramRaw.split("&");
			List<String> paramList = Arrays.stream(params).map((aEqB) -> {
				try {
					// Logger.println(aEqB);
					String[] keyValue = aEqB.split("=", 2);
					String key = URLEncoder.encode(keyValue[0], "UTF-8");
					String value = keyValue.length >= 2 ? URLEncoder.encode(keyValue[1], "UTF-8") : "";
					return key + "=" + value;
				} catch (UnsupportedEncodingException e) {
					return aEqB;
				}
			}).collect(Collectors.toList());
			Collections.sort(paramList);
			paramEncodedSorted = String.join("&", paramList);
			// Logger.println(paramRaw);
			// Logger.println(paramSorted);
		} else {
			sep = "?";
			paramEncodedSorted = wts;
		}
		String md5 = Encrypt.MD5(paramEncodedSorted + mixinKey);
		String encUrl = String.format("%s%sw_rid=%s&%s", url, sep, md5, wts);
		// Logger.println(encUrl);
		return encUrl;
	}
}
