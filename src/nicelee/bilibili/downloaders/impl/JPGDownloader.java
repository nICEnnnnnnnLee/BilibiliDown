package nicelee.bilibili.downloaders.impl;

import java.io.File;
import java.util.HashMap;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.ui.Global;

@Bilibili(name = "jpg-downloader", type = "downloader", note = "图片下载")
public class JPGDownloader extends FLVDownloader {

	private String format;
	@Override
	public boolean matches(String url) {
		if (url.endsWith(".jpg")) {
			format = "jpg";
			return true;
		}else if (url.endsWith(".png")) {
			format = "png";
			return true;
		}else if (url.endsWith(".webp")) {
			format = "webp";
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
		boolean succ = util.download(url, fName, new HashMap<>());
		if (succ) {
			sumSuccessDownloaded += util.getTotalFileSize();
			util.reset();
		}
		return succ;
	}

}
