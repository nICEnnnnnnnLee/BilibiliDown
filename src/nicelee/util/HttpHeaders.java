package nicelee.util;

import java.util.HashMap;

public class HttpHeaders {
	HashMap<String, String> headerMap = new HashMap<String, String>();
	static HashMap<String, String> userInfoHeaderMap = null;
	static HashMap<String, String> loginAuthHeaderMap = null;
	static HashMap<String, String> loginAuthVaHeaderMap = null;
	static HashMap<String, String> mobileHeaderMap = null;

	public void setHeader(String key, String value) {
		headerMap.put(key, value);
	}

	public String getHeader(String key) {
		return headerMap.get(key);
	}

	public HashMap<String, String> getHeaders() {
		return headerMap;
	}

	/**
	 * 该Header配置用于用户信息/登录状态获取
	 */
	public HashMap<String, String> getBiliUserInfoHeaders() {
		if (userInfoHeaderMap == null) {
			userInfoHeaderMap = new HashMap<String, String>();
			userInfoHeaderMap.put("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			userInfoHeaderMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
			userInfoHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
			userInfoHeaderMap.put("Connection", "keep-alive");
			userInfoHeaderMap.put("Host", "account.bilibili.com");
			userInfoHeaderMap.put("X-Requested-With", "XMLHttpRequest");
			userInfoHeaderMap.put("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		}
		return userInfoHeaderMap;
	}

	/**
	 * 该Header配置用于登录AuthKey获取
	 */
	public HashMap<String, String> getBiliLoginAuthHeaders() {
		if (loginAuthHeaderMap == null) {
			loginAuthHeaderMap = new HashMap<String, String>();
			loginAuthHeaderMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
			loginAuthHeaderMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
			loginAuthHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
			loginAuthHeaderMap.put("Connection", "keep-alive");
			loginAuthHeaderMap.put("Host", "passport.bilibili.com");
			loginAuthHeaderMap.put("Referer", "https://passport.bilibili.com/login");
			loginAuthHeaderMap.put("X-Requested-With", "XMLHttpRequest");
			loginAuthHeaderMap.put("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		}
		return loginAuthHeaderMap;
	}

	/**
	 * 该Header配置用于登录AuthKey验证
	 */
	public HashMap<String, String> getBiliLoginAuthVaHeaders() {
		if (loginAuthVaHeaderMap == null) {
			loginAuthVaHeaderMap = new HashMap<String, String>();
			loginAuthVaHeaderMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
			loginAuthVaHeaderMap.put("Accept-Encoding", "gzip, deflate, br");
			loginAuthVaHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
			loginAuthVaHeaderMap.put("Connection", "keep-alive");
			loginAuthVaHeaderMap.put("Host", "passport.bilibili.com");
			loginAuthVaHeaderMap.put("Origin", "https://passport.bilibili.com");
			loginAuthVaHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			loginAuthVaHeaderMap.put("Referer", "https://passport.bilibili.com/login");
			loginAuthVaHeaderMap.put("X-Requested-With", "XMLHttpRequest");
			loginAuthVaHeaderMap.put("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		}
		return loginAuthVaHeaderMap;
	}

	/**
	 * 该Header配置用于视频下载
	 */
	public HashMap<String, String> getBiliWwwHeaders(String avId) {
		headerMap.put("X-Requested-With", "ShockwaveFlash/28.0.0.137");
		headerMap.put("Referer", "https://www.bilibili.com/video/" + avId);// need addavId
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}

	/**
	 * 该Header配置用于api 信息查询
	 */
	public HashMap<String, String> getBiliJsonAPIHeaders(String avId) {
		headerMap.put("Accept", "*/*");
		headerMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", "api.bilibili.com");
		headerMap.put("Referer", "https://www.bilibili.com/video/" + avId);// need addavId
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		headerMap.put("X-Requested-With", "ShockwaveFlash/28.0.0.137");
		return headerMap;
	}

	/**
	 * 该Header配置用于移动端视频下载/网页访问
	 */
	public HashMap<String, String> getBiliMHeaders() {
		if (mobileHeaderMap == null) {
			mobileHeaderMap = new HashMap<String, String>();
			mobileHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			mobileHeaderMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
			mobileHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
			mobileHeaderMap.put("Connection", "keep-alive");
			mobileHeaderMap.put("Cache-Control", "max-age=0");
			mobileHeaderMap.put("Host", "m.bilibili.com");
			mobileHeaderMap.put("User-Agent",
					"Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Mobile Safari/537.36");
		}
		return mobileHeaderMap;
	}

}
