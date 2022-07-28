package nicelee.server.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.bilibili.PackageScanLoader;
import nicelee.bilibili.annotations.Controller;
import nicelee.bilibili.annotations.Value;
import nicelee.server.util.ResponseUtil;

public class PathDealer {

	protected Socket socketClient;

	public PathDealer() {
	}

	public PathDealer(Socket socketClient) {
		this.socketClient = socketClient;
	}

	/**
	 * 处理请求
	 * 
	 * @param out
	 * @param outRaw
	 * @param path  请求路径
	 * @param param 请求参数?号后面那一坨
	 * @param data  POST的内容，GET为null
	 * @param headersMap  头部
	 * @throws IOException
	 */
	public void dealRequest(BufferedWriter out, OutputStream outRaw, String path, String param, String data,
			HashMap<String, String> headersMap) throws IOException {
		dealRequest(out, outRaw, path, param, data, headersMap, false);
	}

	public void dealRequest(BufferedWriter out, OutputStream outRaw, String path, String param, String data,
			HashMap<String, String> headersMap, boolean isCmd) throws IOException {
		// 遍历Controller类，得到和Path匹配的处理方法, 目前仅一个Class
		Method currentMethod = null;
		currentMethod = findMethod(path, currentMethod);
		// 找到Method方法后，根据param给Method变量赋值
		if (currentMethod != null) {
			dealWithPathKnown(out, outRaw, path, param, data, currentMethod, headersMap);
		} else {
			dealWithPathUnknown(out, path, isCmd);
		}
	}

	/**
	 * 寻找匹配Method的方法
	 * 
	 * @param path
	 * @param currentMethod
	 * @return
	 */
	private Method findMethod(String path, Method currentMethod) {
		for (Class<?> klass : PackageScanLoader.controllerClazzes) {
			Controller preAnno = klass.getAnnotation(Controller.class);
			String pathPrefix = preAnno.path();
			for (Method method : klass.getMethods()) {
				Controller controller = method.getAnnotation(Controller.class);
				if (controller != null && controller.specificPath().equals(path)) {
					currentMethod = method;
					break;
				}
				if (controller != null && (controller.specificPath().isEmpty() || !controller.path().isEmpty())) {
					String realPath = pathPrefix + controller.path();
					if (controller.matchAll()) {
						if (realPath.equals(path)) {
							currentMethod = method;
							break;
						}
					} else {
						if (path != null && path.startsWith(realPath)) {
							currentMethod = method;
							break;
						}
					}
				}
			}
		}
		return currentMethod;
	}

	/**
	 * 
	 * @param out
	 * @param param
	 * @param data
	 * @param currentMethod
	 * @param headersMap
	 */
	private void dealWithPathKnown(BufferedWriter out, OutputStream outRaw, String path,String param, String data,
			Method currentMethod, HashMap<String, String> headersMap) {
		Class<?> klass = currentMethod.getDeclaringClass();
		Annotation[][] paramAnnos = currentMethod.getParameterAnnotations();
		Class<?>[] paramTypes = currentMethod.getParameterTypes();
		Object[] values = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			// 如果是BufferedWriter，那么直接赋值，否则从params中找
			if (paramTypes[i] == BufferedWriter.class) {
				values[i] = out;
			} else if (paramTypes[i] == OutputStream.class) {
				values[i] = outRaw;
			} else {
				if (paramAnnos[i].length > 0) {
					Value value = (Value) paramAnnos[i][0];
					switch (value.key()) {
					case "pathData":
						values[i] = path;
						break;
					case "postData":
						values[i] = data;
						break;
					case "ipData":
						values[i] = getRealIpAddr(headersMap);
						break;
					case "paramData":
						values[i] = param;
						break;
					default:
						values[i] = getValue(param, value.key());
					}
				} else {
					values[i] = null;
				}
			}
		}
		// 实例化Controller，并执行该方法
		try {
			String result = (String) currentMethod.invoke(klass.newInstance(), values);
			if (result != null) {
				out.write(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(klass.getName());
			System.out.println(currentMethod.getName());
			for (Object obj : values) {
				System.out.println(obj);
			}
		}
	}

	/**
	 * 处理不知道的路径
	 * 
	 * @param out
	 * @param isCmd 是否直接返回command not found；还是返回引导页面
	 * @throws IOException
	 */
	private void dealWithPathUnknown(BufferedWriter out, String path, boolean isCmd) throws IOException {
		ResponseUtil.response404NotFound(out);
		ResponseUtil.responseHeader(out, "Content-Type", "text/html; charset=UTF-8");
		ResponseUtil.endResponseHeader(out);
		if (isCmd) {
			out.write("command not found");
			return;
		}
		out.write("<html><head><title>Index</title></head><body><ul>");
		for (Class<?> klass : PackageScanLoader.controllerClazzes) {
			Controller preAnno = klass.getAnnotation(Controller.class);
			String pathPrefix = preAnno.path();
			out.write("<li><a href=\"");
			out.write(preAnno.path());
			out.write("\">");
			out.write(preAnno.note());
			out.write("</a><br/>\r\n<ul>");
			for (Method method : klass.getMethods()) {
				Controller controller = method.getAnnotation(Controller.class);
				if (controller != null && (controller.specificPath().isEmpty() || !controller.path().isEmpty())) {
					String methodPath = pathPrefix + controller.path();
					if (path == null || methodPath.startsWith(path)) {
						out.write("<li>");
						out.write(controller.note());
						out.write("<br/>\r\n<a href=\"");
						out.write(methodPath);
						out.write("\">");
						out.write(methodPath);
						out.write("</a><br/>\r\n<br/>\r\n</li>");
					}

				}
			}
			out.write("</ul></li><hr/>");
		}
		out.write("</ul></body></html>");
		ResponseUtil.endResponse(out);
	}

	/**
	 * 从参数字符串中取出值 "key1=value1&key2=value2 ..."
	 * 
	 * @param param
	 * @param key
	 * @return
	 */
	public static String getValue(String param, String key) {
		Pattern pattern = Pattern.compile(key + "=([^&]*)");
		Matcher matcher = pattern.matcher(param);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	/** 
	 * 获取用户真实IP地址
	 *  
	 * @return ip
	 */
	private String getRealIpAddr(HashMap<String, String> headersMap) {
		String ip = headersMap.get("x-forwarded-for");
		// System.out.println("x-forwarded-for ip: " + ip);
		if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			if (ip.indexOf(",") != -1) {
				ip = ip.split(",")[0];
			}
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = headersMap.get("proxy-client-ip");
			// System.out.println("Proxy-Client-IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = headersMap.get("wl-proxy-client-ip");
			// System.out.println("WL-Proxy-Client-IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = headersMap.get("http_client_ip");
			// System.out.println("HTTP_CLIENT_IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = headersMap.get("http_x_forwarded_for");
			// System.out.println("HTTP_X_FORWARDED_FOR ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = headersMap.get("x-real-ip");
			// System.out.println("X-Real-IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = socketClient.getInetAddress().toString().replace("/", "");
			// System.out.println("getRemoteAddr ip: " + ip);
		}
		// System.out.println("获取客户端ip: " + ip);
		return ip;
	}
}
