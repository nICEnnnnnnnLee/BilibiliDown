package nicelee.server.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.bilibili.annotations.Controller;
import nicelee.bilibili.annotations.Value;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;
import nicelee.server.core.PathDealer;
import nicelee.server.util.ResponseUtil;
import nicelee.ui.thread.CookieRefreshThread;

@Controller(path = "/cookieRefresh", note = "刷新Cookie")
public class ControllerCookieRefresh {

	@Controller(path = "/index.html", matchAll = true, note = "html主页")
	public String index(BufferedWriter out, OutputStream outRaw, @Value(key = "pathData") String path) {
		String realPath = path.substring(14);
		makeFileResponse(out, outRaw, realPath, "text/html; charset=UTF-8");
		return null;
	}

	@Controller(path = "/refresh_csrf", matchAll = true, note = "根据传入的参数发起Cookie刷新请求")
	public String refresh(BufferedWriter out, OutputStream outRaw, @Value(key = "postData") String param)
			throws IOException {
		try {
			HttpRequestUtil util = new HttpRequestUtil();
			HttpHeaders header = new HttpHeaders();
			// 获取 refresh_csrf
			String url = "https://www.bilibili.com/correspond/1/" + PathDealer.getValue(param, "sufix");
			String html = util.getContent(url, header.getCommonHeaders(), HttpCookies.getGlobalCookies());
			// Logger.println(html);
			Pattern p = Pattern.compile("<div +id=\"1-name\">(.+?)</div>");
			Matcher m = p.matcher(html);
			m.find();
			String refreshCsrf = m.group(1).trim();
			Logger.println(refreshCsrf);
			CookieRefreshThread thread = CookieRefreshThread.currentInstance();
			thread.setRefreshCsrf(refreshCsrf);
			thread.interrupt();
			ResponseUtil.response200OK(out);
			ResponseUtil.responseHeader(out, "Content-Type", "application/json;charset=UTF-8");
			ResponseUtil.endResponseHeader(out);
			out.write("{\"code\":0, \"message\": \"ok\"}");
			ResponseUtil.endResponse(out);
		} catch (Exception e) {
			// e.printStackTrace();
			ResponseUtil.response200OK(out);
			ResponseUtil.responseHeader(out, "Content-Type", "application/json;charset=UTF-8");
			ResponseUtil.endResponseHeader(out);
			out.write("{\"code\":404, \"message\": \"" + e.getMessage() + "\"}");
			ResponseUtil.endResponse(out);
		}
		return null;
	}

	public void makeFileResponse(BufferedWriter out, OutputStream outRaw, String path, String contentType) {
		try {
			// System.out.println("请求的path: " + path);
			URL url = this.getClass().getResource("/resources/cookieRefresh" + path);
			if (url != null) {
				InputStream in = this.getClass().getResourceAsStream("/resources/cookieRefresh" + path);
				ResponseUtil.response200OK(out);
				ResponseUtil.responseHeader(out, "Content-Type", contentType);
				ResponseUtil.endResponseHeader(out);
				byte[] buffer = new byte[1024];
				int len = in.read(buffer);
				while (len > 0) {
					outRaw.write(buffer, 0, len);
					len = in.read(buffer);
				}
				in.close();
			} else {
				ResponseUtil.response404NotFound(out);
				ResponseUtil.responseHeader(out, "Content-Length", "0");
				ResponseUtil.endResponseHeader(out);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
