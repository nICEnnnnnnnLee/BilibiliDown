package nicelee.bilibili.downloaders.impl;

import nicelee.bilibili.enums.StatusEnum;

//@Bilibili(name = "test-downloader", type = "downloader", note = "用于测试")
public class TestDownloader extends FLVDownloader{


	
	@Override
	public boolean matches(String url) {
		if(url.contains(".test")) {
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
		util.init();
		totalTaskCnt = 2;
		currentTask = 1;
		convertingStatus = StatusEnum.PROCESSING;
		return true;
	}

	@Override
	public StatusEnum currentStatus() {
		return StatusEnum.PROCESSING;
	}
}
