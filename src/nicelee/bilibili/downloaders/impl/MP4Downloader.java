package nicelee.bilibili.downloaders.impl;

import nicelee.bilibili.annotations.Bilibili;


@Bilibili(name = "mp4-downloader", 
	type = "downloader",
	note = "MP4下载")
public class MP4Downloader extends FLVDownloader{

	@Override
	public boolean matches(String url) {
		if(url.contains(".mp4")) {
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
		return download(url, avId, qn, page, ".mp4");
	}

}
