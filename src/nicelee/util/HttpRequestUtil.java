package nicelee.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;


public class HttpRequestUtil {

	static CookieManager defaultManager = new CookieManager();
	// 下载缓存区
	byte[] buffer = new byte[1024 * 1024];
	// 下载文件大小状态
	long cnt;
	long total;
	// 下载文件
	File fileDownload;
	// 下载状态
	int status; // 0 正在下载; 1 下载完毕; -1 出现错误; -2 人工停止
	// 下载标志,置False可以停止下载
	boolean bDown = true;
	// Cookie管理
	CookieManager manager = new CookieManager();
	
	public HttpRequestUtil() {
		this.manager = defaultManager;
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
	}
	
	public HttpRequestUtil(CookieManager manager) {
		this.manager = manager;
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
	}
	
	public static CookieManager DefaultCookieManager() {
		return defaultManager;
	}
	/**
	 * 停止下载
	 */
	public void stopDownload() {
		bDown = false;
	}

	/**
	 * 下载文件
	 * @param url	文件url
	 * @param fileName  文件保存名称
	 * @param headers   http报文头部
	 * @return
	 */
	public boolean download(String url, String fileName, HashMap<String, String> headers) {
		status = 0;
		System.out.println(url);
		InputStream inn = null;
		RandomAccessFile raf = null;
		try {
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			conn.setConnectTimeout(2000);
			conn.setReadTimeout(2000);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
			conn.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = conn.getHeaderFields();
			// 遍历所有的响应头字段
//			for (String key : map.keySet()) {
//				System.out.println(key + "--->" + map.get(key));
//			}
			System.out.printf("文件大小: %s 字节.\r\n", map.get("Content-Length"));

			total = Long.parseUnsignedLong(map.get("Content-Length").get(0));
			inn = conn.getInputStream();
			// 确保没有重复文件
			fileDownload = getFileEnsureNoExists(fileName);
			// 文件下载中先添加.part后缀
			File fileDownloadPart = new File(fileDownload.getParent(), fileDownload.getName() + ".part");
			// 删除前面未下载完成的文件
//			if(fileDownloadPart.exists())
//				fileDownloadPart.delete();
			raf = new RandomAccessFile(fileDownloadPart, "rw");
			int lenRead = inn.read(buffer);
			cnt = lenRead;
			while (lenRead > -1) {
				if (!bDown) {
					status = -2;
					return false;
				}
				raf.write(buffer, 0, lenRead);
				// System.out.println("当前完成度: " + cnt*100/total + "%");
				lenRead = inn.read(buffer);
				cnt += lenRead;
			}
			raf.close();
			fileDownloadPart.renameTo(fileDownload);
			System.out.println("下载完毕...");
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
			status = -1;
			return false;
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (inn != null) {
					inn.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			try {
				if (raf != null) {
					raf.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		status = 1;
		return true;
	}
	
	public String getContent(String url, HashMap<String, String> headers) {
		return getContent(url, headers, null);
	}
	
	/**
	 * do a Http Get
	 * Not Worked with http stream with Chunked
	 * 
	 * @param url
	 * @param headers
	 * @return content, mostly a html page
	 * @throws IOException
	 */
	public String getContent(String url, HashMap<String, String> headers, List<HttpCookie> listCookie) {
		StringBuffer result = new StringBuffer();
		BufferedReader in = null;
		try {
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			conn.setConnectTimeout(2000);
			conn.setReadTimeout(2000);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
			// 设置Cookie
			if (listCookie != null) {
				StringBuilder sb = new StringBuilder();
				for (HttpCookie cookie : listCookie) {
					sb.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
				}
				String cookie = sb.toString();
				if (cookie.endsWith("; ")) {
					cookie = cookie.substring(0, cookie.length() - 2);
				}
				//System.out.println(cookie);
				conn.setRequestProperty("Cookie", cookie);
			}
			conn.connect();

			String encoding = conn.getContentEncoding();
			InputStream ism = conn.getInputStream();
			if (encoding != null && encoding.contains("gzip")) {// 首先判断服务器返回的数据是否支持gzip压缩，
				// 如果支持则应该使用GZIPInputStream解压，否则会出现乱码无效数据
				ism = new GZIPInputStream(conn.getInputStream());
			}
			in = new BufferedReader(new InputStreamReader(ism));
			String line;
			while ((line = in.readLine()) != null) {
				line = new String(line.getBytes(), "UTF-8");
				result.append(line);
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		//printCookie(manager.getCookieStore());
		return result.toString();
	}
	
	/**
	 * do a Http Post
	 * Not Worked with http stream with Chunked
	 * @param url
	 * @param headers
	 * @param param
	 * @return
	 */
	public String postContent(String url, HashMap<String, String> headers, String param) {
		return postContent(url, headers, param, null);
	}
	
	/**
	 * do a Http Post
	 * Not Worked with http stream with Chunked
	 * @param url
	 * @param headers
	 * @param param
	 * @param iCookies 可以为null
	 * @return
	 */
	public String postContent(String url, HashMap<String, String> headers, String param, List<HttpCookie> listCookie) {
		StringBuffer result = new StringBuffer();
		BufferedReader in = null;
		try {
			// 建立连接
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			conn.setConnectTimeout(2000);
			conn.setReadTimeout(2000);
			// 设置参数
			conn.setDoOutput(true); // 需要输出
			conn.setDoInput(true); // 需要输入
			conn.setUseCaches(false); // 不允许缓存
			conn.setRequestMethod("POST"); // 设置POST方式连接
			// 设置Headers
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
			// 设置Cookie
			if (listCookie != null) {
				StringBuilder sb = new StringBuilder();
				for (HttpCookie cookie : listCookie) {
					sb.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
				}
				String cookie = sb.toString();
				if (cookie.endsWith("; ")) {
					cookie = cookie.substring(0, cookie.length() - 2);
				}
				System.out.println(cookie);
				conn.setRequestProperty("Cookie", cookie);
			}
			conn.connect();
			// 建立输入流，向指向的URL传入参数
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(param);
			dos.flush();
			dos.close();

			String encoding = conn.getContentEncoding();
			InputStream ism = conn.getInputStream();
			// 判断服务器返回的数据是否支持gzip压缩
			if (encoding != null && encoding.contains("gzip")) {
				ism = new GZIPInputStream(conn.getInputStream());
			}
			in = new BufferedReader(new InputStreamReader(ism));
			String line;
			while ((line = in.readLine()) != null) {
				line = new String(line.getBytes(), "UTF-8");
				result.append(line);
			}
			// printCookie(manager.getCookieStore());
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result.toString();
	}

	private File getFileEnsureNoExists(String dst) {
		//当前文件所在目录
		File curfolder = new File(dst).getParentFile();
		//新建download文件夹
		File folder = new File(curfolder, "download");
		if(!folder.exists()) {
			folder.mkdir();
		}
		//判断download文件下的文件状况
		File file = new File(folder, dst);
		if (file.exists()) {
			String name = file.getName().replaceAll(".[^.]+$", "");
			String suffix = file.getName().replace(name, "");
			for (int i = 1; i < 1000; i++) {
				file = new File(folder, name + "-" + i + suffix);
				System.out.println("目标文件已存在,尝试新建文件: " + name + "-" + i + suffix);
				if (!file.exists() && !new File(file.getParent(), file.getName() + ".part").exists())
					return file;
			}
		}
		return file;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public long getCnt() {
		return cnt;
	}

	public long getTotal() {
		return total;
	}

	public File getFileDownload() {
		return fileDownload;
	}

	public int getStatus() {
		return status;
	}

	// 打印cookie信息
	public static String printCookie(CookieStore cookieStore) {
		List<HttpCookie> listCookie = cookieStore.getCookies();
		StringBuilder sb = new StringBuilder();
		for (HttpCookie httpCookie : listCookie) {
//			System.out.println("--------------------------------------" + httpCookie.toString());
			sb.append(httpCookie.toString()).append("; ");
            System.out.println("class      : "+httpCookie.getClass());
            System.out.println("comment    : "+httpCookie.getComment());
            System.out.println("commentURL : "+httpCookie.getCommentURL());
            System.out.println("discard    : "+httpCookie.getDiscard());
            System.out.println("maxAge     : "+httpCookie.getMaxAge());
			System.out.println("name       : " + httpCookie.getName());
            System.out.println("portlist   : "+httpCookie.getPortlist());
            System.out.println("secure     : "+httpCookie.getSecure());
            System.out.println("version    : "+httpCookie.getVersion());
			System.out.println("domain     : " + httpCookie.getDomain());
			System.out.println("path       : " + httpCookie.getPath());
			System.out.println("value      : " + httpCookie.getValue());
			System.out.println("httpCookie : " + httpCookie);
		}
		return sb.toString();
	}

}
