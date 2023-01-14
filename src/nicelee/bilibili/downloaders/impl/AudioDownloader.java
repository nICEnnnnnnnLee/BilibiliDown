package nicelee.bilibili.downloaders.impl;

import java.io.File;
import java.util.HashMap;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.ui.Global;

@Bilibili(name = "AudioDownloader", type = "downloader", note = "音频下载")
public class AudioDownloader extends FLVDownloader {

	private String format;
	@Override
	public boolean matches(String url) {
		if (url.contains(".m4a")) {
			format = "m4a";
			return true;
		}else if (url.contains(".flac")) {
			format = "flac";
			return true;
		}
		return false;
	}

	/**
	 * 下载matches
	 * 
	 * @param url
	 * @param avId 相簿id
	 * @param qn 默认0
	 * @param page 第几张图片
	 * @return
	 */
	@Override
	public boolean download(String url, String avId, int qn, int page) {
		convertingStatus = StatusEnum.NONE;
		currentTask = 1;
		String fName = String.format("%s-%d-p%d.%s", avId, qn, page, format);
		if(file == null) {
			file = new File(Global.savePath, fName);
		}
		HashMap<String, String> headers = new HttpHeaders().getCommonHeaders();
		headers.put("Accept", "audio/webm,audio/ogg,audio/wav,audio/*;q=0.9,application/ogg;q=0.7,video/*;q=0.6,*/*;q=0.5");
		headers.put("Referer", "https://www.bilibili.com/");
		boolean succ = util.download(url, fName, headers);
		if (succ) {
			sumSuccessDownloaded += util.getTotalFileSize();
			util.reset();
		}
		return succ;
	}

}
