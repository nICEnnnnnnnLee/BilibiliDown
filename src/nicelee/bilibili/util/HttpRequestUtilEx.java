package nicelee.bilibili.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

import nicelee.bilibili.enums.StatusEnum;
import nicelee.ui.Global;

public class HttpRequestUtilEx extends HttpRequestUtil {

	
	protected volatile boolean hasError = false;
	@Override
	public boolean download(String url, String fileName, HashMap<String, String> headers) {
		// 判断是否要分段下载
		if (Global.multiThreadCnt <= 1) {
			return super.download(url, fileName, headers);
		}
		Matcher m = Global.singleThreadPattern.matcher(url);
		if(m.find()){
			return super.download(url, fileName, headers);
		}
		// 获取文件大小
		totalFileSize = getTotalSize(url, headers);
		if (totalFileSize == -1) {
			status = StatusEnum.FAIL;
			return false;
		}else if(totalFileSize == 0) {
			Logger.println("不支持断点续传");
			return super.download(url, fileName, headers);
		} else if(Global.multiThreadMinFileSize != 0 && totalFileSize <= Global.multiThreadMinFileSize) {
			Logger.println("小于一定阈值，不值得开启多线程");
			return super.download(url, fileName, headers);
		} 
		Boolean result = check(fileName);
		if(result != null)
			return result;
		// 计算分段大小
		long perSize = totalFileSize / Global.multiThreadCnt;

		Object lock = new Object();
		final AtomicInteger count = new AtomicInteger(Global.multiThreadCnt);
		List<File> srcFiles = new ArrayList<File>();
		for (int i = 0; i < Global.multiThreadCnt; i++) {
			long min = perSize * i;
			long max = i == Global.multiThreadCnt - 1 ? totalFileSize - 1 : min + perSize - 1;
			final int index = i;
			HashMap<String, String> specificHeader = new HashMap<>(headers);
			// 文件下载中先添加.parti后缀
			File fileDownloadPart = new File(fileDownload.getParent(), fileDownload.getName() + ".part" + index);
			srcFiles.add(fileDownloadPart);
			Thread thDownload = new Thread(new Runnable() {
				@Override
				public void run() {
					InputStream inn = null;
					RandomAccessFile raf = null;
					try {
						raf = new RandomAccessFile(fileDownloadPart, "rw");
						// 调整headers
						String range = "bytes=%d-%d";
						long offset = 0;
						if (fileDownloadPart.exists() && fileDownloadPart.length() > 0) {
							offset = fileDownloadPart.length();
							if(offset == max - min + 1) {
								synchronized (downloadedFileSize) {
									downloadedFileSize += offset;
									return;
								}
							}
							// System.out.println("part" + index + "当前已下载: [" + offset + "]字节");
							raf.seek(offset);
						}
						specificHeader.put("range", String.format(range, min + offset, max));
						// Logger.println(String.format(range, min + offset, max));
						// 开始下载
						HttpURLConnection conn = connect(specificHeader, url, null);
						conn.connect();
//						if (conn.getResponseCode() == 403) {
//							Logger.println("403被拒，尝试更换Headers");
//							conn.disconnect();
//							specificHeader.clear();
//							specificHeader.putAll(HttpHeaders.getBiliAppDownHeaders());
//							specificHeader.put("range", String.format(range, min + offset, max));
//							conn = connect(specificHeader, url, null);
//							conn.connect();
//						}
						// 获取所有响应头字段
						// Map<String, List<String>> map = conn.getHeaderFields();
						// Logger.println("Content-Range" + map.get("Content-Range"));

						try {
							inn = conn.getInputStream();
						} catch (Exception e) {
							e.printStackTrace();
							Logger.println(specificHeader.get("range"));
							BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
							String temp;
							while ((temp = reader.readLine()) != null) {
								System.out.println(temp);
							}
							reader.close();
							throw e;
						}
						byte[] buffer = new byte[1024 * 1024];
						int lenRead = inn.read(buffer);
						synchronized (downloadedFileSize) {
							downloadedFileSize += offset + lenRead;
						}
						while (lenRead > -1) {
							if (!bDown) {
								status = StatusEnum.STOP;
								return;
							}
							if(hasError)
								return;
							raf.write(buffer, 0, lenRead);
							// System.out.println("当前完成度: " + cnt*100/total + "%");
							lenRead = inn.read(buffer);
							synchronized (downloadedFileSize) {
								downloadedFileSize += lenRead;
							}
						}
						raf.close();
					} catch (Exception e) {
						System.out.println("发送GET请求出现异常！" + e);
						e.printStackTrace();
						hasError = true;
						return;
					}
					// 使用finally块来关闭输入流
					finally {
						// System.out.println("下载Finally...");
						ResourcesUtil.closeQuietly(inn);
						ResourcesUtil.closeQuietly(raf);
						synchronized (lock) {
							if (count.decrementAndGet() == 0)
								lock.notifyAll();
						}
						System.out.println("下载完毕... Thread-" + index);
					}
				}
			});
			thDownload.start();
		}

		// 等待执行完毕
		try {
			synchronized (lock) {
				lock.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(hasError) {
			status = StatusEnum.FAIL;
			return false;
		}else if (status == StatusEnum.STOP) {
			return false;
		}
		// 合并
		try {
			merge(srcFiles, fileDownload);
			status = StatusEnum.SUCCESS;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			status = StatusEnum.FAIL;
			return false;
		}
	}

	public long getTotalSize(String url, HashMap<String, String> headers) {
		HttpURLConnection conn = null;
		try {
			conn = connect(headers, url, null);
			conn.connect();
			if (conn.getResponseCode() == 403) {
				Logger.println("403被拒，尝试更换Headers");
				conn.disconnect();
				headers = HttpHeaders.getBiliAppDownHeaders();
				conn = connect(headers, url, null);
				conn.connect();
			}
			// 获取所有响应头字段
			Map<String, List<String>> map = conn.getHeaderFields();
			long size = Long.parseUnsignedLong(map.get("Content-Length").get(0));
//			Logger.println(url);
//			for(Entry<String, List<String>> entry: map.entrySet()){
//				Logger.println(entry.getKey()+ ": " + entry.getValue());
//			}
//			List<String> acRanges = map.get("Accept-Ranges");
//			List<String> acExposeHeaders = map.get("Access-Control-Expose-Headers");
//			if(acRanges == null && acExposeHeaders.contains("Content-Range")) {
//				return 0;
//			}
			return size;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}finally {
			try {
				conn.disconnect();
			}catch (Exception e) {
			}
		}
	}

	private void merge(List<File> srcFiles, File dst) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(dst, "rw");

		byte[] buffer = new byte[1024 * 1024 * 4];
		for (int i = 0; i < srcFiles.size(); i++) {
			File f = srcFiles.get(i);
			RandomAccessFile rafSrc = new RandomAccessFile(f, "r");
			int len = rafSrc.read(buffer);
			while (len > -1) {
				raf.write(buffer, 0, len);
				len = rafSrc.read(buffer);
			}
			rafSrc.close();
			f.delete();
		}
		raf.close();
	}
}
