package nicelee.bilibili.downloaders;

import java.io.File;

import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.HttpRequestUtil;

public interface IDownloader {

	/**
	 * 根据下载链接匹配下载器
	 * 
	 * @param url
	 * @return
	 */
	public boolean matches(String url);

	/**
	 * 初始化
	 * 
	 * @param util
	 */
	public void init(HttpRequestUtil util);

	/**
	 * 下载视频
	 * 
	 * @param url
	 * @param avId
	 * @param qn
	 * @param page
	 * @return
	 */
	public boolean download(String url, String avId, int qn, int page);

	/**
	 * 开始下载
	 * 
	 * @return
	 */
	public void startTask();

	/**
	 * 停止下载
	 * 
	 * @return
	 */
	public void stopTask();

	/**
	 * 返回完成后目标文件
	 * 
	 * @return
	 */
	public File file();

	/**
	 * 返回当前状态
	 * 
	 * @return
	 */
	public StatusEnum currentStatus();

	/**
	 * 返回总任务数
	 * 
	 * @return
	 */
	public int totalTaskCount();

	/**
	 * 返回当前第几个任务
	 * 
	 * @return
	 */
	public int currentTask();

	/**
	 * 返回该任务所有下载的总文件大小(没有下载完成时，该统计没有意义)
	 * 
	 * @return
	 */
	public long sumTotalFileSize();

	/**
	 * 返回该任务已经下载的总文件大小
	 * 
	 * @return
	 */
	public long sumDownloadedFileSize();

	/**
	 * 返回当前正在下载的文件进度
	 * 
	 * @return
	 */
	public long currentFileDownloadedSize();

	/**
	 * 返回当前正在下载的文件总大小
	 * 
	 * @return
	 */
	public long currentFileTotalSize();

	final static long KB = 1024L;
	final static long MB = KB * 1024L;
	final static long GB = MB * 1024L;

	/**
	 * 文件大小转换为字符串
	 * 
	 * @param size
	 * @return
	 */
	public static String transToSizeStr(long size) {
		if (size == 0) {
			return "未知";
		}
		double dSize;
		if (size >= GB) {
			dSize = size * 1.0 / GB;
			return String.format("%.2f GB", dSize);
		} else if (size >= MB) {
			dSize = size * 1.0 / MB;
			return String.format("%.2f MB", dSize);
		} else if (size >= KB) {
			dSize = size * 1.0 / KB;
			return String.format("%.2f KB", dSize);
		}
		return size + " Byte";
	}

}
