package nicelee.server.util;

import java.io.BufferedWriter;
import java.io.IOException;

public class ResponseUtil {

	
	public static void response200OK(BufferedWriter out) throws IOException {
		out.write("HTTP/1.1 200 OK\r\n");
		out.flush();
	}
	
	public static void response404NotFound(BufferedWriter out) throws IOException {
		out.write("HTTP/1.1 404 Not Found\r\n");
		out.flush();
	}
	
	public static void responseHeader(BufferedWriter out, String name, String value) throws IOException {
		out.write(String.format("%s: %s\r\n", name, value));
	}
	
	public static void endResponseHeader(BufferedWriter out) throws IOException {
		out.write("\r\n");
		out.flush();
	}
	
	public static void endResponse(BufferedWriter out) throws IOException {
		out.write("\r\n");
		out.flush();
	}
	
	public static void htmlResponseBegin(BufferedWriter out) throws IOException {
		ResponseUtil.response200OK(out);
		ResponseUtil.responseHeader(out, "Content-Type", "text/html; charset=UTF-8");
		ResponseUtil.endResponseHeader(out);
	}
	
	public static void htmlResponseEnd(BufferedWriter out) throws IOException {
		ResponseUtil.endResponse(out);
	}
	
	public static void Response404(BufferedWriter out) throws IOException {
		ResponseUtil.endResponse(out);
	}
	
}
