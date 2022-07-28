package nicelee.server.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import nicelee.bilibili.annotations.Controller;
import nicelee.bilibili.annotations.Value;
import nicelee.server.util.ResponseUtil;

@Controller(path = "/static", note = "静态文件")
public class ControllerStaticFiles {

	@Controller(path = "/index.html", matchAll = true, note = "html主页")
	public String html(BufferedWriter out, OutputStream outRaw, @Value(key = "pathData") String path) {
		// /static
		String realPath = path.substring(7);
		makeFileResponse(out, outRaw, realPath, "text/html; charset=UTF-8");
		return null;
	}

	@Controller(path = "/js", matchAll = false, note = "js文件")
	public String js(BufferedWriter out, OutputStream outRaw, @Value(key = "pathData") String path) {
		String realPath = path.substring(7);
		makeFileResponse(out, outRaw, realPath, "application/javascript; charset=utf-8");
		return null;
	}

	@Controller(path = "/css", matchAll = false, note = "css文件")
	public String css(BufferedWriter out, OutputStream outRaw, @Value(key = "pathData") String path) {
		String realPath = path.substring(7);
		makeFileResponse(out, outRaw, realPath, "text/css; charset=utf-8");
		return null;
	}

	public void makeFileResponse(BufferedWriter out, OutputStream outRaw, String path, String contentType) {
		try {
			// System.out.println("请求的path: " + path);
			URL url = this.getClass().getResource("/resources/geetest-validator" + path);
			if (url != null) {
				InputStream in = this.getClass().getResourceAsStream("/resources/geetest-validator" + path);
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
