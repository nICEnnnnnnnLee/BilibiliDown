package nicelee.util;

import java.util.HashMap;

public class HttpHeaders {
	HashMap<String, String> headerMap = new HashMap<String, String>();
	public void setHeader(String key, String value) {
		headerMap.put(key, value);
	}
	
	public String getHeader(String key) {
		return headerMap.get(key);
	}
	
	public HashMap<String, String> getHeaders(){
		return headerMap;
	}
	
	/**
	 * 该Header配置用于用户信息/登录状态获取
	 */
	public HashMap<String, String> getBiliUserInfoHeaders() {
		headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		headerMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", "account.bilibili.com");
		headerMap.put("X-Requested-With", "XMLHttpRequest");
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}
	/**
	 * 该Header配置用于登录AuthKey获取
	 */
	public HashMap<String, String> getBiliLoginAuthHeaders() {
		headerMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
		headerMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", "passport.bilibili.com");
		headerMap.put("Referer", "https://passport.bilibili.com/login");
		headerMap.put("X-Requested-With", "XMLHttpRequest");
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}
	
	/**
	 * 该Header配置用于登录AuthKey验证
	 */
	public HashMap<String, String> getBiliLoginAuthVaHeaders() {
		headerMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
		headerMap.put("Accept-Encoding", "gzip, deflate, br");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", "passport.bilibili.com");
		headerMap.put("Origin", "https://passport.bilibili.com");
		headerMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		headerMap.put("Referer", "https://passport.bilibili.com/login");
		headerMap.put("X-Requested-With", "XMLHttpRequest");
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}
	
	/**
	 * 该Header配置用于视频下载
	 */
	public HashMap<String, String> getBiliWwwHeaders(String avId) {
		headerMap.put("X-Requested-With", "ShockwaveFlash/28.0.0.137");
		headerMap.put("Referer", "https://www.bilibili.com/video/" + avId);//need addavId
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
		headerMap.put("Referer", "https://www.bilibili.com/video/" + avId);//need addavId
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		headerMap.put("X-Requested-With", "ShockwaveFlash/28.0.0.137");
		return headerMap;
	}
	
	/**
	 * 该Header配置用于移动端视频下载/网页访问
	 */
	public HashMap<String, String> getBiliMHeaders() {
		headerMap.put("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		headerMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Cache-Control", "max-age=0");
		headerMap.put("Host", "m.bilibili.com");
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Mobile Safari/537.36");
				return headerMap;
	}
	
}
