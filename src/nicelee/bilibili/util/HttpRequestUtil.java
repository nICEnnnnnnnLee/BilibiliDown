package nicelee.bilibili.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.exceptions.Status412Exception;
import nicelee.ui.Global;

public class HttpRequestUtil {

	protected static CookieManager defaultManager = new CookieManager();
	// 下载缓存区
	protected byte[] buffer;
	// 下载文件大小状态
	protected Long downloadedFileSize;
	protected long totalFileSize;
	// 下载文件
	protected String savePath = "./download/";
	protected File fileDownload;
	// 下载状态
	protected volatile StatusEnum status = StatusEnum.NONE; // 0 正在下载; 1 下载完毕; -1 出现错误; -2 人工停止;-3 队列中
	// 下载标志,置False可以停止下载
	protected volatile boolean bDown = true;
	// Cookie管理
	CookieManager manager;
	
	static Charset UTF_8;
	static {
		UTF_8 = Charset.forName("UTF-8");
	}

	public HttpRequestUtil() {
		this(defaultManager);
	}

	public HttpRequestUtil(CookieManager manager) {
		downloadedFileSize = 0L;
		this.manager = manager;
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
		// 单纯API 可以自己设置Global里面的参数
		savePath = Global.savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public static CookieManager DefaultCookieManager() {
		return defaultManager;
	}

	/**
	 * 停止下载
	 */
	public void stopDownload() {
		bDown = false;
		status = StatusEnum.STOP;
	}
	
	public void stopDownloadAsFail() {
		bDown = false;
		status = StatusEnum.FAIL;
	}

	/**
	 * 重置统计参数
	 */
	public void init() {
		bDown = true;
		status = StatusEnum.NONE;
		reset();
	}

	/**
	 * 重置统计参数
	 */
	public void reset() {
		downloadedFileSize = 0L;
		totalFileSize = 0;
	}

	/**
	 *  是否要继续下载
	 * @param fileName
	 * @return 为null，继续，否则直接返回结果
	 */
	protected Boolean check(String fileName) {
		// 如果已经人工停止，那么直接返回
		if (status == StatusEnum.STOP) {
			return false;
		}
		status = StatusEnum.DOWNLOADING;

		// 确保没有重复文件
		fileDownload = getFile(fileName);
		File fileDst = new File(fileDownload.getParent(),
				fileDownload.getName().replaceAll("_(video|audio)", "").replaceAll("\\.m4s$", ".mp4"));
		System.out.println(fileDst.getName());
		// 如果av1234-64-p4.flv已下完， 那么av1234-64-p4-part1.flv这种也不是必须的
		Matcher ma = filePartPattern.matcher(fileDst.getName());
		if (ma.find()) {
			// 文件已存在,无需下载
			File fTemp = new File(fileDst.getParent(), ma.group(1) + "." + ma.group(2));
			if (fTemp.exists()) {
				status = StatusEnum.SUCCESS;
				return true;
			}
		}
		if (fileDownload.exists() || fileDst.exists()) {
			totalFileSize = fileDownload.length();
			downloadedFileSize = fileDownload.length();
			status = StatusEnum.SUCCESS;
			// 文件已存在,无需下载
			return true;
		}
		return null;
	}
	
	/**
	 * 下载文件 请确认 文件大小total为 0 或者正确值
	 * 
	 * @param url      文件url
	 * @param fileName 文件保存名称
	 * @param headers  http报文头部
	 * @return
	 */
	static Pattern filePartPattern = Pattern.compile("^(.*)-part[0-9]+\\.(flv|mp4)$");
	public boolean download(String url, String fileName, HashMap<String, String> headers) {
		Boolean result = check(fileName);
		if(result != null)
			return result;
		
		InputStream inn = null;
		RandomAccessFile raf = null;
		try {
			// 文件下载中先添加.part后缀
			File fileDownloadPart = new File(fileDownload.getParent(), fileDownload.getName() + ".part");
			raf = new RandomAccessFile(fileDownloadPart, "rw");
			// 获取下载进度
			long offset = 0;
			offset = modifyHeaderMapByDownloaded(headers, raf, fileDownloadPart, offset);
			// 开始下载
			String urlNameString = url;
			HttpURLConnection conn = connect(headers, urlNameString, null);
			conn.connect();

//			if (conn.getResponseCode() == 403) {
//				Logger.println("403被拒，尝试更换Headers");
//				conn.disconnect();
//				headers = HttpHeaders.getBiliAppDownHeaders();
//				offset = modifyHeaderMapByDownloaded(headers, raf, fileDownloadPart, offset);
//				conn = connect(headers, urlNameString, null);
//				conn.connect();
//			}
			// 获取所有响应头字段
			Map<String, List<String>> map = conn.getHeaderFields();
			// 遍历所有的响应头字段
//			for (String key : map.keySet()) {
//				System.out.println(key + "--->" + map.get(key));
//			}
			List<String> conLen = map.get("Content-Length");
			if(conLen == null)
				conLen = map.get("content-length");
			System.out.printf("文件大小: %s 字节.\r\n", conLen);

			if(conLen != null)
				totalFileSize = offset + Long.parseUnsignedLong(conLen.get(0));
			try {
				inn = conn.getInputStream();
			} catch (Exception e) {
				e.printStackTrace();
				Logger.println(headers.get("range"));
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
				String temp;
				while ((temp = reader.readLine()) != null) {
					System.out.println(temp);
				}
				reader.close();
				throw e;
			}
			if (buffer == null)
				buffer = new byte[1024 * 1024];
			int lenRead = inn.read(buffer);
			downloadedFileSize = offset + lenRead;
			while (lenRead > -1) {
				if (!bDown) {
					status = StatusEnum.STOP;
					return false;
				}
				raf.write(buffer, 0, lenRead);
				// System.out.println("当前完成度: " + cnt*100/total + "%");
				lenRead = inn.read(buffer);
				downloadedFileSize += lenRead;
			}
			raf.close();
			if (fileDownloadPart.length() < totalFileSize) {
				download(urlNameString, fileName, headers);
			} else {
				fileDownloadPart.renameTo(fileDownload);
				System.out.println("下载完毕...");
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
			status = StatusEnum.FAIL;
			return false;
		}
		// 使用finally块来关闭输入流
		finally {
			buffer = null;
			// System.out.println("下载Finally...");
			ResourcesUtil.closeQuietly(inn);
			ResourcesUtil.closeQuietly(raf);
		}
		status = StatusEnum.SUCCESS;
		return true;
	}

	/**
	 * @param headers
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	protected HttpURLConnection connect(HashMap<String, String> headers, String url, List<HttpCookie> listCookie)
			throws MalformedURLException, IOException {
		URL realUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(10000);
		if (headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
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
			conn.setRequestProperty("Cookie", cookie);
		}
		// conn.connect();
		return conn;
	}

	/**
	 * 获取已经下载字节数，并修饰Header
	 * 
	 * @param headers
	 * @param raf
	 * @param fileDownloadPart
	 * @param offset
	 * @return
	 * @throws IOException
	 */
	protected long modifyHeaderMapByDownloaded(HashMap<String, String> headers, RandomAccessFile raf,
			File fileDownloadPart, long offset) throws IOException {
		headers.remove("range");
		if (fileDownloadPart.exists() && fileDownloadPart.length() > 0) {
			offset = fileDownloadPart.length();
			// total = getfileSize(url, headers);
			headers.put("range", "bytes=" + offset + "-");// + (total - 1)
			System.out.println("当前已下载: [" + offset + "]字节");
			raf.seek(offset);
		}
		return offset;
	}

	public String getContent(String url, HashMap<String, String> headers) {
		return getContent(url, headers, null);
	}

	/**
	 * do a Http Get
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
			HttpURLConnection conn = connect(headers, url, listCookie);
			conn.connect();
			if(conn.getResponseCode() == 412)
				throw new Status412Exception("HTTP返回状态码为412");
			String encoding = conn.getContentEncoding();
			InputStream ism = conn.getInputStream();
			if (encoding != null && encoding.contains("gzip")) {// 首先判断服务器返回的数据是否支持gzip压缩，
				// System.out.println(encoding);
				// 如果支持则应该使用GZIPInputStream解压，否则会出现乱码无效数据
				ism = new GZIPInputStream(ism);
			} else if (encoding != null && encoding.contains("deflate")) {
				ism = new InflaterInputStream(ism, new Inflater(true));
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[256];
			int len = ism.read(buffer);
			while (len > 0) {
				out.write(buffer, 0, len);
				len = ism.read(buffer);
			}
			
			result.append(new String(out.toByteArray(), UTF_8));
			out.close();
		} catch (Status412Exception e) {
			throw e;
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		} finally {
			ResourcesUtil.closeQuietly(in);
		}
		return result.toString();
	}

	public String postContent(String url, HashMap<String, String> headers, String param) {
		return postContent(url, headers, param, null);
	}

	/**
	 * do a Http Post
	 * 
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
			HttpURLConnection conn = connect(headers, url, listCookie);
			// 设置参数
			conn.setDoOutput(true); // 需要输出
			conn.setDoInput(true); // 需要输入
			conn.setUseCaches(false); // 不允许缓存
			conn.setRequestMethod("POST"); // 设置POST方式连接
			conn.connect();

			// 建立输入流，向指向的URL传入参数
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(param);
			dos.flush();
			dos.close();
			
			if(conn.getResponseCode() == 412)
				throw new Status412Exception("HTTP返回状态码为412");

			String encoding = conn.getContentEncoding();
			InputStream ism = conn.getInputStream();
			// 判断服务器返回的数据是否支持gzip压缩
			if (encoding != null && encoding.contains("gzip")) {
				ism = new GZIPInputStream(conn.getInputStream());
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[256];
			int len = ism.read(buffer);
			while (len > 0) {
				out.write(buffer, 0, len);
				len = ism.read(buffer);
			}
			result.append(new String(out.toByteArray(), UTF_8));
			out.close();
		} catch (Status412Exception e) {
			throw e;
		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
		} finally {
			ResourcesUtil.closeQuietly(in);
		}
		return result.toString();
	}

	/**
	 * 测试视频链接url的有效性
	 * 
	 * @param url
	 * @param headers
	 * @param listCookie
	 * @return
	 */
	public boolean checkValid(String url, HashMap<String, String> headers, List<HttpCookie> listCookie) {
		BufferedReader in = null;
		try {
			HttpURLConnection conn = connect(headers, url, listCookie);
			// 设置参数
			headers.put("range", "bytes=0-100");
			conn.connect();
			return conn.getResponseCode() != 404;
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			return false;
		} finally {
			ResourcesUtil.closeQuietly(in);
		}
	}

	protected File getFile(String dst) {
		File folder = new File(savePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(folder, dst);
		return file;
	}

	public File getFileDownload() {
		return fileDownload;
	}

	public StatusEnum getStatus() {
		return status;
	}

	public long getDownloadedFileSize() {
		return downloadedFileSize;
	}

	public long getTotalFileSize() {
		return totalFileSize;
	}

}
