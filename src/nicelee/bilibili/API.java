package nicelee.bilibili;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONObject;

import nicelee.bilibili.util.ConfigUtil;
import nicelee.bilibili.util.Encrypt;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.bilibili.util.convert.ConvertUtil;
import nicelee.bilibili.util.custom.System;
import nicelee.ui.Global;

public class API {

	public static void main(String[] args) {
//		List<HttpCookie> cookies = HttpCookies
//				.convertCookies("[DedeUserID=xxx; DedeUserID__ckMd5=xxx; SESSDATA=xxx; bili_jct=xxx; bfe_id=xxx...]");
//		HttpCookies.setGlobalCookies(cookies);
		ConfigUtil.initConfigs();
//		API.genNewFingerprint();
//		API.getFingerprint();
		API.uploadFingerprint();
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

	public static String encodeURL(String rawUrl) {
		String url = rawUrl;
		if (!url.contains("%")) {
			try {
				url = URLEncoder.encode(url, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return url.replace("+", "%20");
	}

	/**
	 * @param url	url含有空格会报错，建议先URLEncode处理
	 * @return
	 */
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
					String value = keyValue.length >= 2 ? encodeURL(keyValue[1]) : "";
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

	final static long SECONDS_OF_ONE_YEAR = 60 * 60 * 24 * 365;

	public static synchronized String getFingerprint() {
		String cookie = null;
		File fingerprint = ResourcesUtil.sourceOf("./config/fingerprint.config");
		if (fingerprint.exists()) {
			cookie = ResourcesUtil.readAll(fingerprint);
			Pattern p = Pattern.compile("b_nut=([^;]+)");
			Matcher m = p.matcher(cookie);
			m.find();
			long b_nut = Long.parseLong(m.group(1));
			long currentTime = System.currentTimeMillis() / 1000L;
			// 由于历史原因，老版本的文件需要删除
			if (!cookie.contains("i-wanna-go-back") || currentTime - b_nut > SECONDS_OF_ONE_YEAR) {
				fingerprint.delete();
				cookie = null;
			} else {
				// 看情况更新 b_lsid
				p = Pattern.compile("b_lsid=([^;]+)");
				m = p.matcher(cookie);
				m.find();
				String timeHex = m.group(1).split("_")[1];
				long time = Long.parseLong(timeHex, 16);
				if (time + 5400000L < currentTime) {
					String b_lsid = ResourcesUtil.randomHex(8) + "_" + Long.toHexString(currentTime).toUpperCase();
					cookie = m.replaceFirst("b_lsid=" + b_lsid);
					ResourcesUtil.write(fingerprint, cookie);
				}
			}
		}
		if (cookie == null) {
			cookie = genNewFingerprint();
			ResourcesUtil.write(fingerprint, cookie);
		}
		Logger.println(cookie);
		return cookie;
	}

	public static void uploadFingerprint() {
		long currentTime = System.currentTimeMillis();
		HttpRequestUtil util = new HttpRequestUtil();
		HttpHeaders headers = new HttpHeaders();
		// 看情况更新 b_lsid
		String timeHex = HttpCookies.get("b_lsid").split("_")[1];
		long time = Long.parseLong(timeHex, 16);
		if (time + 5400000L < currentTime) {
			String b_lsid = ResourcesUtil.randomHex(8) + "_" + Long.toHexString(currentTime).toUpperCase();
			HttpCookies.set("b_lsid", b_lsid);
		}
		// 设置 browser_resolution
		Matcher mResolution = pResolution.matcher(Global.userAgentPayload);
		if(mResolution.find()) {
			String resolution = String.format("%s-%s", mResolution.group(1), mResolution.group(2));
			HttpCookies.set("browser_resolution", resolution);
		}
		// TODO payload
		String payload = Global.userAgentPayload;
		payload = payload.replaceFirst("\"5062\":\"[^\"]+\"", "\"5062\":\"" + currentTime + "\"");
		payload = payload.replaceFirst("\"b8ce\":\"[^\"]+\"", "\"b8ce\":\"" + Global.userAgent + "\"");
		payload = payload.replaceFirst("\"df35\":\"[^\"]+\"", "\"df35\":\"" + HttpCookies.get("_uuid") + "\"");

		String url = "https://api.bilibili.com/x/internal/gaia-gateway/ExClimbWuzhi";
		JSONObject p = new JSONObject();
		p.put("payload", payload);
		String param = p.toString();
		Logger.println(payload);
		Logger.println(param);
		HashMap<String, String> h = headers.getCommonHeaders();
		h.put("Content-type", "application/json;charset=UTF-8");
		String result = util.postContent(url, h, param, HttpCookies.globalCookiesWithFingerprint());
		Logger.println(result); // {"code":0,"message":"0","ttl":1,"data":{}}
								// {"code":130212,"message":"130212","ttl":1,"data":null}
	}

	public static String genNewFingerprint() {
		try {
			Map<String, String> kvMap = new HashMap<>();
			long currentTime = System.currentTimeMillis();
			// 获取 buvid3 b_nut i-wanna-go-back b_ut 有效期一年
			HttpURLConnection conn = (HttpURLConnection) new URL("https://www.bilibili.com/").openConnection();
			conn.setRequestProperty("User-Agent", Global.userAgent);
			conn.connect();
			List<String> setCookie = conn.getHeaderFields().get("Set-Cookie");
			setCookie = setCookie != null ? setCookie : conn.getHeaderFields().get("set-cookie");
			for (String c : setCookie) {
				String[] kv = c.split(";", 2)[0].split("=", 2);
				kvMap.put(kv[0].trim(), kv[1].trim());
			}
			kvMap.put("i-wanna-go-back", "-1");
			// 生成 b_lsid
			String b_lsid = ResourcesUtil.randomHex(8) + "_" + Long.toHexString(currentTime).toUpperCase();
			kvMap.put("b_lsid", b_lsid);
			// 生成 _uuid
			String _uuid = new StringBuilder().append(ResourcesUtil.randomHex(8)).append("-")
					.append(ResourcesUtil.randomHex(4)).append("-").append(ResourcesUtil.randomHex(4)).append("-")
					.append(ResourcesUtil.randomHex(4)).append("-").append(ResourcesUtil.randomHex(18)).append("infoc")
					.toString();
			kvMap.put("_uuid", _uuid);
			// 获取 buvid4
			HttpRequestUtil util = new HttpRequestUtil();
			HttpHeaders headers = new HttpHeaders();
			String tempCookie = HttpCookies.map2CookieStr(kvMap);
			Logger.println(tempCookie);
			String r = util.getContent("https://api.bilibili.com/x/frontend/finger/spi", headers.getCommonHeaders(),
					HttpCookies.convertCookies(tempCookie));
			Logger.print(r);
			String buvid4 = new JSONObject(r).getJSONObject("data").getString("b_4");
			kvMap.put("buvid4", buvid4);
			// TODO 获取 buvid_fp (浏览器指纹)
			String buvid_fp = Global.userAgentFingerprint;
			kvMap.put("buvid_fp", buvid_fp);
			kvMap.put("fingerprint", buvid_fp);
			// 获取 browser_resolution
			Matcher mResolution = pResolution.matcher(Global.userAgentPayload);
			mResolution.find();
			String resolution = String.format("%s-%s", mResolution.group(1), mResolution.group(2));
			kvMap.put("browser_resolution", resolution);
			return HttpCookies.map2CookieStr(kvMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	static String b64Sub2(String data) {
		try {
			String result = new String(Base64.getEncoder().encode(data.getBytes("UTF-8")), "UTF-8");
			result = result.substring(0, result.length() - 2);
			return result;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	static Random random = new Random();

	static String f114(int a, int b) {
		int t = random.nextInt(114);
		return String.format("[%d,%d,%d]", 2 * a + 2 * b + 3 * t, 4 * a - b + t, t);
	}

	static String f514(int a, int b) {
		int t = random.nextInt(514);
		return String.format("[%d,%d,%d]", 3 * a + 2 * b + t, 4 * a - 4 * b + 2 * t, t);
	}

	static Pattern pWebglVersion = Pattern.compile("\"webgl version:([^\"]+)\"");
	static Pattern pResolution = Pattern.compile("\"6e7c\":\"(\\d+)x(\\d+)\"");
	static Pattern pWebglUnRenderer = Pattern.compile("\"webgl unmasked renderer:([^\"]+)\"");
	static Pattern pWebglUnVendor = Pattern.compile("\"webgl unmasked vendor:([^\"]+)\"");

	public static String genDmImgParams() {
		// TODO dm_img_list 浏览器加载完毕后，如果没什么动作，请求始终为[]
		String dm_img_list = "[]";
		// dm_img_str
		Matcher mWebglVersion = pWebglVersion.matcher(Global.userAgentPayload);
		mWebglVersion.find();
		String dm_img_str = b64Sub2(mWebglVersion.group(1).trim());
		// TODO dm_img_inter 浏览器加载完毕后第一个请求始终为
		// {"ds":[],"wh":f114(width, height),"of":f514(0,0)}
		Matcher mResolution = pResolution.matcher(Global.userAgentPayload);
		mResolution.find();
		String _wh = f114(Integer.parseInt(mResolution.group(1)), Integer.parseInt(mResolution.group(2)));
		String _of = f514(0, 0);
		String dm_img_inter = String.format("{\"ds\":[],\"wh\":%s,\"of\":%s}", _wh, _of);
		// dm_cover_img_str
		Matcher mWebglUnRenderer = pWebglUnRenderer.matcher(Global.userAgentPayload);
		mWebglUnRenderer.find();
		Matcher mWebglUnVendor = pWebglUnVendor.matcher(Global.userAgentPayload);
		mWebglUnVendor.find();
		String dm_cover_img_str = mWebglUnRenderer.group(1).trim() + mWebglUnVendor.group(1).trim();
		dm_cover_img_str = b64Sub2(dm_cover_img_str);
		return String.format("&dm_img_list=%s&dm_img_str=%s&dm_img_inter=%s&dm_cover_img_str=%s", dm_img_list,
				dm_img_str, dm_img_inter, dm_cover_img_str);
	}
}
