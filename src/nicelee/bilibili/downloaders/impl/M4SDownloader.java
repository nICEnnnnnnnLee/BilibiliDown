package nicelee.bilibili.downloaders.impl;

import java.io.File;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.CmdUtil;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.ui.Global;


@Bilibili(name = "m4s-downloader", 
	type = "downloader",
	note = "音视频分流下载, 完成后合成MP4")
public class M4SDownloader extends FLVDownloader{


	
	@Override
	public boolean matches(String url) {
		if(url.contains(".m4s")) {
			return true;
		}
		return false;
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
		convertingStatus = StatusEnum.NONE;
		HttpHeaders header = new HttpHeaders();
		boolean audioOnly = url.startsWith("#");
		String links[] = url.split("#");
		for(int i=0; i<links.length; i++) {
			links[i] = tryBetterUrl(links[i]);
		}
		String fName = avId + "-" + qn + "-p" + page;
		String suffix = audioOnly? Global.suffix4AudioOnly : ".mp4";  // mp4 / aac / flac
		String videoName = fName + "_video.m4s";
		String audioName = fName + "_audio.m4s";
		String dstName = fName + suffix;
		if(file == null) {
			file = new File(Global.savePath, dstName);
		}
		if(audioOnly) {
			if (util.download(links[1], audioName, header.getBiliWwwM4sHeaders(avId))) {
				convertingStatus = StatusEnum.PROCESSING;
				boolean result = CmdUtil.convert(null, audioName, dstName);
				return throwErrorIfNotConvertOk(result, audioName);
			}
			return false;
		} 
		totalTaskCnt = 2;
		if (util.download(links[0], videoName, header.getBiliWwwM4sHeaders(avId))) {
			// 如下载成功，统计数据后重置
			sumSuccessDownloaded += util.getTotalFileSize();
			util.reset();
			currentTask = 2;
			if(util.getStatus() == StatusEnum.STOP)
				return false;
			util.init();
			if(links.length == 1 || links[1].isEmpty()) {
				totalTaskCnt = currentTask = 1;
				convertingStatus = StatusEnum.PROCESSING;
				boolean result = CmdUtil.convert(videoName, null, dstName);
				return throwErrorIfNotConvertOk(result, videoName);
			}else if (util.download(links[1], audioName, header.getBiliWwwM4sHeaders(avId))) {
				// 如下载成功，统计数据后重置
				sumSuccessDownloaded += util.getTotalFileSize();
				util.reset();
				// 下载完毕后,进行合并
				convertingStatus = StatusEnum.PROCESSING;
				boolean result = CmdUtil.convert(videoName, audioName, dstName);
				return throwErrorIfNotConvertOk(result, dstName);
			}
			return false;
		}
		return false;
	}

}
