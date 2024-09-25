package nicelee.bilibili.downloaders.impl;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.downloaders.IDownloader;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.exceptions.BilibiliError;
import nicelee.bilibili.util.CmdUtil;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.ui.Global;


@Bilibili(name = "flv-downloader", 
	type = "downloader",
	note = "FLV下载")
public class FLVDownloader implements IDownloader {

	protected HttpRequestUtil util;
	protected File file = null;
	protected int currentTask = 1;
	protected int totalTaskCnt = 1;
	protected StatusEnum convertingStatus = StatusEnum.NONE;
	protected long sumSuccessDownloaded = 0;

	@Override
	public boolean matches(String url) {
		if(url.contains(".flv")) {
			return true;
		}
		return false;
	}
	
	@Override
	public void init(HttpRequestUtil util) {
		this.util = util;
	}

	/**
	 * 下载视频
	 * @param url
	 * @param avId
	 * @param qn
	 * @param page
	 * @return
	 */
	@Override
	public boolean download(String url, String avId, int qn, int page) {
		return download(url, avId, qn, page, ".flv");
	}
	
	static Pattern pcdnPattern;
	static Pattern hostPattern;
	static Pattern urlReplaceWhitelist;
	static String hostAlt;
	
	protected String tryBetterUrl(String url) {
		url = tryReplaceHost(url);
		url = tryForceHttp(url);
		return url;
	}
	
	private String tryReplaceHost(String url) {
		if(Global.forceReplaceUposHost) {
			if(hostPattern == null) {
				hostPattern = Pattern.compile("://[^/]+");
				// 不能替换 https://xy111x48x218x66xy.mcdn.bilivideo.cn:8082/v1/resource/1597612179-1-30280.m4s
				// 可以替换 https://xy111x48x218x66xy.mcdn.bilivideo.cn:8082/upgcxcode/35/83/1532588335/1532588335-1-30280.m4s
				// urlReplaceWhitelist = Pattern.compile("https?://[^/]+/upgcxcode");
				urlReplaceWhitelist = Pattern.compile(Global.forceReplaceUrlPattern);
				hostAlt = "://" + Global.altHost;
			}
			Matcher mWList = urlReplaceWhitelist.matcher(url);
			// Logger.println(url);
			if(mWList.find()) {
				Matcher m = hostPattern.matcher(url);
				return m.replaceFirst(hostAlt);
			}
		}
		return url;
	}
	
	private String tryForceHttp(String url) {
		if(Global.forceHttp) {
			if(pcdnPattern == null) {
				pcdnPattern = Pattern.compile("://[^/:]+:\\d+/");
			}
			Matcher m = pcdnPattern.matcher(url);
			if(!m.find()) {
				// Logger.println("https替换为http");
				return url.replace("https:/", "http:/");
			}
			// Logger.println("检测为PCDN，forceHttp未生效");
		}
		return url;
	}
	
	protected boolean download(String url, String avId, int qn, int page, String suffix) {
		convertingStatus = StatusEnum.NONE;
		currentTask = 1;
		String fName = avId + "-" + qn + "-p" + page;
		HttpHeaders header = new HttpHeaders();
		if(file == null) {
			file = new File(Global.savePath, fName + suffix);
		}
		if (url.contains("#")) {
			String links[] = url.split("#");
			totalTaskCnt = links.length;
			Pattern numUrl = Pattern.compile("^([0-9]+)(http.*)$");
			if(util.getStatus() == StatusEnum.STOP)
				return false;
			// 从 currentTask 继续开始任务
			util.init();
			for (int i = currentTask - 1; i < links.length; i++) {
				links[i] = tryBetterUrl(links[i]);
				currentTask = (i + 1);
				Matcher matcher = numUrl.matcher(links[i]);
				matcher.find();
				String order = matcher.group(1);
				String tUrl = matcher.group(2);
				String fileName = fName + "-part" + order + suffix;
				if (!util.download(tUrl, fileName, header.getBiliWwwFLVHeaders(avId))) {
					return false;
				}
				sumSuccessDownloaded += util.getTotalFileSize();
				util.reset();
			}
			// 下载完毕后,进行合并
			convertingStatus = StatusEnum.PROCESSING;
			boolean result = CmdUtil.convert(fName + suffix, links.length);
			return throwErrorIfNotConvertOk(result, fName);
		} else {
			url = tryBetterUrl(url);
			String fileName = fName + suffix;
			boolean succ = util.download(url, fileName, header.getBiliWwwFLVHeaders(avId));
			if (succ) {
				sumSuccessDownloaded += util.getTotalFileSize();
				util.reset();
			}
			return succ;
		}
	}

	protected boolean throwErrorIfNotConvertOk(boolean ok, String msg) {
		if (ok) {
			convertingStatus = StatusEnum.SUCCESS;
			return true;
		} else {
			convertingStatus = StatusEnum.FAIL;
			if(Global.alertIfFFmpegFail)
				throw new BilibiliError("如需关闭该警告，请在配置页搜索并修改配置 bilibili.alert.ffmpegFail\n\t转码失败，请检查ffmpeg配置: " + msg);
			else
				return false;
		}
	}
	/**
	 * 返回当前状态
	 * 
	 * @return
	 */
	@Override
	public StatusEnum currentStatus() {
		// totalTask
		// currentTask
		// util status; // 0 正在下载; 1 下载完毕; -1 出现错误; -2 人工停止;-3 队列中
		if(file != null && file.exists() && (convertingStatus == StatusEnum.SUCCESS || convertingStatus == StatusEnum.NONE)) {
			return StatusEnum.SUCCESS;
		}
		//System.out.println("转码状态： " + convertingStatus.getDescription());
		//System.out.println("下载工具状态： " + util.getStatus().getDescription());
		// 如果当前是最后一个任务
		if (currentTask == totalTaskCnt) {
			// 当前任务转码状态判断
			if (convertingStatus == StatusEnum.SUCCESS) {
				return StatusEnum.SUCCESS;
			} else if (convertingStatus == StatusEnum.FAIL) {
				return StatusEnum.FAIL;
			} else if(convertingStatus == StatusEnum.PROCESSING){
				return StatusEnum.PROCESSING;
			} else { //与转码无关
				return util.getStatus();
			}
		}

		switch (util.getStatus()) {
		case DOWNLOADING:
			return StatusEnum.DOWNLOADING;
		case STOP:
			return StatusEnum.STOP;
		case FAIL:
			return StatusEnum.FAIL;
		case SUCCESS: {
			return StatusEnum.DOWNLOADING;
		}
		default:
			return StatusEnum.NONE;// 还未处理，在队列中
		}
	}

	/**
	 * 返回总任务数
	 * 
	 * @return
	 */
	@Override
	public int totalTaskCount() {
		return totalTaskCnt;
	}

	/**
	 * 返回当前第几个任务
	 * 
	 * @return
	 */
	@Override
	public int currentTask() {
		return currentTask;
	}
	
	@Override
	public void startTask() {
		util.init();
	}
	@Override
	public void stopTask() {
		util.stopDownload();
	}

	@Override
	public long sumTotalFileSize() {
		return sumSuccessDownloaded + util.getTotalFileSize();
	}

	@Override
	public long sumDownloadedFileSize() {
		return sumSuccessDownloaded + util.getDownloadedFileSize();
	}

	@Override
	public long currentFileDownloadedSize() {
		return util.getDownloadedFileSize();
	}

	@Override
	public long currentFileTotalSize() {
		return util.getTotalFileSize();
	}
	@Override
	public File file() {
		return file;
	}

}
