package nicelee.bilibili;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;

import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.convert.ConvertUtil;

public class API {

	public static void main(String[] args) {
		List<HttpCookie> cookies = HttpCookies.convertCookies("[DedeUserID=xxx; DedeUserID__ckMd5=xxx; SESSDATA=xxx; bili_jct=xxx; bfe_id=xxx...]");
		HttpCookies.setGlobalCookies(cookies);
		boolean result = API.like(666);
		System.out.println(result);
		//API.logout();
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
		if(result_query.startsWith("{\"code\":0,\"message\":\"0\",\"ttl\":1,\"data\":0}")) {
			String url = "https://api.bilibili.com/x/web-interface/archive/like";
			// like 1 点赞 2 取消
			String param = String.format("aid=%d&like=1&csrf=%s", avIdNum, HttpCookies.getCsrf());
			String result = util.postContent(url, headers, param, HttpCookies.getGlobalCookies());
			//{"code":-101,"message":"账号未登录","ttl":1}
			//{"code":65006,"message":"已赞过","ttl":1}
			//{"code":0,"message":"0","ttl":1}
			Logger.println(result);
			if(result.startsWith("{\"code\":0"))
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
		String result = util.postContent(url, new HttpHeaders().getLogoutHeaders(), param, HttpCookies.getGlobalCookies());
		Logger.println(result);
	}
}
